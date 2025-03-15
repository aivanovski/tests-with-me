package com.github.aivanovski.testswithme.android.domain.flow

import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityNodeInfo
import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.domain.flow.logger.TimberFlowLogger
import com.github.aivanovski.testswithme.android.domain.flow.model.FlowRunnerState
import com.github.aivanovski.testswithme.android.entity.JobStatus
import com.github.aivanovski.testswithme.android.entity.OnFinishAction
import com.github.aivanovski.testswithme.android.entity.OnStepFinishedAction
import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.presentation.MainActivity
import com.github.aivanovski.testswithme.android.presentation.StartArgs
import com.github.aivanovski.testswithme.domain.fomatters.CompactNodeFormatter
import com.github.aivanovski.testswithme.entity.UiNode
import com.github.aivanovski.testswithme.entity.exception.CancelledExecutionException
import com.github.aivanovski.testswithme.entity.exception.DriverDisconnectedException
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testswithme.extensions.format
import com.github.aivanovski.testswithme.extensions.getRootCause
import com.github.aivanovski.testswithme.extensions.toSerializableTree
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.flow.driver.Driver
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext
import com.github.aivanovski.testswithme.flow.runner.listener.ListenerComposite
import com.github.aivanovski.testswithme.flow.runner.report.ReportCollector
import com.github.aivanovski.testswithme.flow.runner.report.ReportWriter
import com.github.aivanovski.testswithme.flow.runner.report.ReportWriter.ShortNameTransformer
import com.github.aivanovski.testswithme.flow.runner.report.TimeCollector
import com.github.aivanovski.testswithme.utils.mutableStateFlow
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class FlowRunner(
    private val context: Context,
    private val settings: Settings,
    private val interactor: FlowRunnerInteractor
) {

    var state by mutableStateFlow(FlowRunnerState.IDLE)
        private set

    private val logger = TimberFlowLogger(tag = TimberFlowLogger::class.java.simpleName)
    private val environment = Environment(settings)
    private val flowLifecycleListeners = ListenerComposite()
    private val scopeJobs = CopyOnWriteArrayList<Job>()
    private var scope = CoroutineScope(Dispatchers.Main)
    private var stepIndex by mutableStateFlow(0)
    private var jobUidRef by mutableStateFlow<String?>(null)
    private var isCollectUiTree by mutableStateFlow(false)
    private var driver by mutableStateFlow<Driver<AccessibilityNodeInfo>?>(null)
    private var commandExecutor by mutableStateFlow<CommandExecutor?>(null)

    private val commandFactory = StepCommandFactory(interactor)
    private val timeCollector = TimeCollector()
    private val reportCollector = ReportCollector()
    private val uiNodeFormatter = CompactNodeFormatter()

    init {
        flowLifecycleListeners.add(
            ReportWriter(
                writer = logger,
                flowTransformer = ShortNameTransformer()
            )
        )
        flowLifecycleListeners.add(timeCollector)
        flowLifecycleListeners.add(reportCollector)
    }

    fun isRunning(): Boolean = (state == FlowRunnerState.RUNNING)

    fun isIdle(): Boolean = (state == FlowRunnerState.IDLE)

    fun setCollectUiTreeFlag() {
        isCollectUiTree = true

        if (isIdle()) {
            updateUiTreeIfNeed()
        }
    }

    fun onDriverConnected(driver: Driver<AccessibilityNodeInfo>) {
        this.driver = driver

        commandExecutor = CommandExecutor(
            interactor = interactor,
            context = ExecutionContext(driver, logger, environment),
            lifecycleListener = flowLifecycleListeners
        )
    }

    fun onDriverDisconnected() {
        state = FlowRunnerState.IDLE
        driver = null
        commandExecutor = null
        stop()
    }

    fun isDriverConnected(): Boolean = (driver != null)

    fun runNextFlow() {
        val jobUid = settings.startJobUid

        val job = scope.launch {
            val findNextJobResult = findNextJobToRun(jobUid)
            if (findNextJobResult.isLeft()) {
                onErrorOccurred(exception = findNextJobResult.unwrapError())
                return@launch
            }

            val nextJobUid = findNextJobResult.getOrNull()
            Timber.d("runNextFlow: jobUid=%s", nextJobUid)

            if (nextJobUid != null) {
                val runResult = runFlow(nextJobUid)
                if (runResult.isLeft()) {
                    onErrorOccurred(exception = runResult.unwrapError())
                    return@launch
                }
            }
        }

        scopeJobs.add(job)
    }

    private suspend fun findNextJobToRun(targetJobUid: String?): Either<AppException, String?> =
        either {
            val jobs = interactor.getJobs()
                .bind()
                .sortedByDescending { job -> job.addedTimestamp }

            val running = jobs.filterByStatus(JobStatus.RUNNING)
            val pending = jobs.filterByStatus(JobStatus.PENDING)

            val cancelledUids = jobs
                .filterByStatus(JobStatus.CANCELLED)
                .map { job -> job.uid }

            val finishedUids = jobs
                .filterByStatus(JobStatus.FINISHED)
                .map { job -> job.uid }

            // TODO: try to upload FINISHED jobs and then move them to the history

            if (targetJobUid in cancelledUids || targetJobUid in finishedUids) {
                settings.startJobUid = null
            }

            Timber.d(
                "jobs: size=%s, running=%s, pending=%s, targetJob=%s",
                jobs.size,
                running.size,
                pending.size,
                targetJobUid
            )
            for (job in jobs) {
                Timber.d("    %s", job)
            }

            if (running.isNotEmpty() && isIdle()) {
                for (job in running) {
                    val cancelledJob = job.copy(
                        status = JobStatus.CANCELLED
                    )

                    interactor.updateJob(cancelledJob).bind()
                }
            }

            val jobUid = if (pending.isNotEmpty() && isIdle()) {
                val jobToStart = targetJobUid?.let {
                    pending.firstOrNull { entry ->
                        entry.uid == targetJobUid
                    }
                }

                jobToStart?.uid ?: pending.first().uid
            } else {
                null
            }

            jobUid
        }

    private suspend fun runFlow(jobUid: String): Either<AppException, Unit> =
        either {
            val jobs = interactor.getJobs().bind()

            val job = jobs.firstOrNull { job -> job.uid == jobUid }
                ?: raise(AppException("Failed to find job by uid: $jobUid"))

            jobUidRef = jobUid
            stepIndex = 0
            state = FlowRunnerState.RUNNING

            if (settings.startJobUid == jobUid) {
                settings.startJobUid = null
            }

            interactor.updateJob(
                job.copy(
                    status = JobStatus.RUNNING
                )
            ).bind()

            timeCollector.clear()
            reportCollector.clear()

            runCurrentStep(
                isFirstStep = true
            ).bind()
        }

    fun stop() {
        state = FlowRunnerState.IDLE

        for (job in scopeJobs) {
            job.cancel()
        }
        scopeJobs.clear()
    }

    private suspend fun onErrorOccurred(
        jobUid: String? = null,
        exception: AppException
    ) {
        Timber.e("onErrorOccurred: jobUid=%s, error=%s", jobUid, exception)
        Timber.e(exception)

        state = FlowRunnerState.IDLE

        printUiTreeIfCan(exception)

        if (jobUid != null) {
            val job = interactor.getJobByUid(jobUid).getOrNull()
            if (job != null) {
                interactor.updateJob(
                    job.copy(
                        status = JobStatus.CANCELLED,
                        executionTime = timeCollector.getDuration()?.inWholeMilliseconds
                    )
                )
            }
        }
    }

    private fun printUiTreeIfCan(exception: AppException) {
        val cause = exception.getRootCause()

        val uiTree = if (cause is FlowExecutionException) {
            when (val flowError = cause.error) {
                is FlowError.AssertionError -> flowError.uiRoot
                is FlowError.FailedToFindUiNodeError -> flowError.uiRoot
                else -> getUiTreeOrNull()
            }
        } else {
            getUiTreeOrNull()
        }

        if (uiTree != null) {
            val formattedTree = uiTree.format(uiNodeFormatter)
            Timber.e("UI Tree:")
            Timber.e(formattedTree)
        }
    }

    private suspend fun runCurrentStep(
        isFirstStep: Boolean,
        initialDelay: Long = interactor.getDelayBetweenSteps()
    ): Either<AppException, Unit> =
        either {
            updateUiTreeIfNeed()

            delay(initialDelay)

            val commandExecutor = commandExecutor
                ?: raise(AppException(cause = DriverDisconnectedException()))

            if (!isRunning()) {
                raise(AppException(cause = CancelledExecutionException()))
            }

            val jobData = interactor.getCurrentJobData().bind()

            val (job, flow, stepEntry, executionData) = jobData

            val command = commandFactory.createCommand(
                flow = flow.entry,
                step = stepEntry.command
            ).bind()

            val nextActionResult = commandExecutor.execute(
                isFirstStep = isFirstStep,
                job = job,
                flow = flow.entry,
                stepEntry = stepEntry,
                command = command,
                stepIndex = stepIndex,
                attemptIndex = executionData.attemptCount
            )
            if (nextActionResult.isLeft()) {
                printUiTreeIfCan(nextActionResult.unwrapError())

                finishFlowExecution(
                    jobUid = job.uid,
                    isRunNextAllowed = (job.onFinishAction == OnFinishAction.RUN_NEXT)
                ).bind()

                return@either
            }

            val nextAction = nextActionResult.unwrap()
            when (nextAction) {
                is OnStepFinishedAction.Next -> {
                    stepIndex++
                    runCurrentStep(
                        isFirstStep = false
                    ).bind()
                }

                OnStepFinishedAction.Complete -> {
                    finishFlowExecution(
                        jobUid = job.uid,
                        isRunNextAllowed = true
                    ).bind()
                }

                OnStepFinishedAction.Retry -> {
                    runCurrentStep(
                        isFirstStep = false
                    ).bind()
                }

                OnStepFinishedAction.Stop -> {
                    finishFlowExecution(
                        jobUid = job.uid,
                        isRunNextAllowed = true
                    ).bind()

                    raise(AppException("Flow was stopped"))
                }
            }
        }

    private suspend fun finishFlowExecution(
        jobUid: String,
        isRunNextAllowed: Boolean
    ): Either<AppException, Unit> =
        either {
            val job = interactor.getJobByUid(jobUid).bind()
            val flow = interactor.getCachedFlowByUid(job.flowUid).bind()
            val flowSourceType = flow.entry.sourceType
            val duration = timeCollector.getDuration()
            val reportContent = reportCollector.getLines().joinToString(separator = "\n")

            interactor.saveReport(
                jobUid = job.uid,
                reportContent = reportContent
            ).bind()

            interactor.updateJob(
                job.copy(
                    status = JobStatus.FINISHED,
                    executionTime = duration?.inWholeMilliseconds
                )
            ).bind()

            val isUploaded = when (flowSourceType) {
                SourceType.REMOTE -> {
                    val uploadResult = interactor.uploadJobResult(job.uid)
                    uploadResult.isRight()
                }

                SourceType.LOCAL -> {
                    interactor.moveJobToHistory(jobUid).bind()
                    false
                }
            }

            state = FlowRunnerState.IDLE
            val isRunNext = (job.onFinishAction == OnFinishAction.RUN_NEXT)

            Timber.d(
                "finishFlowExecution: isUploaded=%s, isRunNext=%s, " +
                    "isRunNextAllowed=%s, sourceType=%s",
                isUploaded,
                isRunNext,
                isRunNextAllowed,
                flowSourceType
            )

            if (isRunNext &&
                isRunNextAllowed &&
                (isUploaded || flowSourceType == SourceType.REMOTE)
            ) {
                runNextFlow()
            } else if (job.onFinishAction == OnFinishAction.SHOW_DETAILS) {
                val intent = MainActivity.createStartIntent(
                    context = context,
                    args = StartArgs(
                        flowUid = job.flowUid,
                        isShowTestRuns = true
                    )
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }

    private fun updateUiTreeIfNeed() {
        val driver = driver ?: return

        if (isCollectUiTree) {
            isCollectUiTree = false

            val job = scope.launch {
                val getUiTreeResult = driver.getUiTree()
                    .mapLeft { error -> FlowExecutionException.fromFlowError(error) }

                if (getUiTreeResult.isLeft()) {
                    Timber.d(getUiTreeResult.unwrapError())
                }
            }

            scopeJobs.add(job)
        }
    }

    fun getUiTreeOrNull(): UiNode<Unit>? {
        val driver = driver ?: return null

        val getUiTreeResult = driver.getUiTree()
            .mapLeft { error -> FlowExecutionException.fromFlowError(error) }

        return if (getUiTreeResult.isRight()) {
            getUiTreeResult.unwrap().toSerializableTree()
        } else {
            Timber.d(getUiTreeResult.unwrapError())
            null
        }
    }

    private fun List<JobEntry>.filterByStatus(status: JobStatus): List<JobEntry> {
        return filter { job -> job.status == status }
    }
}