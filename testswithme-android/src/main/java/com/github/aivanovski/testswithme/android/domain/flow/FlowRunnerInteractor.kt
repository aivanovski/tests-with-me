package com.github.aivanovski.testswithme.android.domain.flow

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.data.file.FileCache
import com.github.aivanovski.testswithme.android.data.repository.FlowRepository
import com.github.aivanovski.testswithme.android.data.repository.FlowRunRepository
import com.github.aivanovski.testswithme.android.data.repository.JobRepository
import com.github.aivanovski.testswithme.android.data.repository.ProjectRepository
import com.github.aivanovski.testswithme.android.data.repository.StepRunRepository
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.android.domain.dataconverters.convertToFlowEntry
import com.github.aivanovski.testswithme.android.domain.dataconverters.convertToStepEntries
import com.github.aivanovski.testswithme.android.domain.usecases.GetCurrentJobUseCase
import com.github.aivanovski.testswithme.android.domain.usecases.GetExternalApplicationDataUseCase
import com.github.aivanovski.testswithme.android.domain.usecases.ParseFlowFileUseCase
import com.github.aivanovski.testswithme.android.entity.ExecutionResult
import com.github.aivanovski.testswithme.android.entity.FlowRunUploadResult
import com.github.aivanovski.testswithme.android.entity.FlowWithSteps
import com.github.aivanovski.testswithme.android.entity.JobData
import com.github.aivanovski.testswithme.android.entity.JobStatus
import com.github.aivanovski.testswithme.android.entity.OnFinishAction
import com.github.aivanovski.testswithme.android.entity.OnStepFinishedAction
import com.github.aivanovski.testswithme.android.entity.SourceType
import com.github.aivanovski.testswithme.android.entity.StepVerificationType
import com.github.aivanovski.testswithme.android.entity.SyncStatus
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.LocalStepRun
import com.github.aivanovski.testswithme.android.entity.db.ProjectEntry
import com.github.aivanovski.testswithme.android.entity.db.StepEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.data.json.JsonSerializer
import com.github.aivanovski.testswithme.entity.FlowStep
import com.github.aivanovski.testswithme.entity.Hash
import com.github.aivanovski.testswithme.entity.StepResult
import com.github.aivanovski.testswithme.entity.YamlFlow
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testswithme.extensions.isFlakyException
import com.github.aivanovski.testswithme.extensions.isStepFlaky
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.utils.Base64Utils
import com.github.aivanovski.testswithme.utils.StringUtils
import com.github.aivanovski.testswithme.web.api.request.PostFlowRunRequest
import java.util.UUID
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import timber.log.Timber

class FlowRunnerInteractor(
    private val settings: Settings,
    private val flowRepository: FlowRepository,
    private val jobRepository: JobRepository,
    private val stepRunRepository: StepRunRepository,
    private val flowRunRepository: FlowRunRepository,
    private val projectRepository: ProjectRepository,
    private val getCurrentJobUseCase: GetCurrentJobUseCase,
    private val parseFlowUseCase: ParseFlowFileUseCase,
    private val getAppDataUseCase: GetExternalApplicationDataUseCase,
    private val fileCache: FileCache,
    private val jsonSerializer: JsonSerializer,
    private val referenceResolver: ReferenceResolver
) {

    fun saveReport(
        jobUid: String,
        reportContent: String
    ): Either<AppException, Unit> =
        either {
            fileCache.put(jobUid, reportContent)
        }

    suspend fun getCurrentJobData(): Either<AppException, JobData> =
        withContext(IO) {
            either {
                val getJobResult = getCurrentJobUseCase.getCurrentJob()
                if (getJobResult.isLeft()) {
                    raise(getJobResult.unwrapError())
                }

                val job = getJobResult.unwrap()
                    ?: raise(AppException("Unable to find current job"))

                val flow = getCachedFlowByUid(job.flowUid).bind()

                val step = flow.steps.firstOrNull { step -> step.uid == job.currentStepUid }
                    ?: raise(AppException("Unable to find step: ${job.currentStepUid}"))

                val executionData = stepRunRepository.getOrCreate(
                    jobUid = job.uid,
                    flowUid = flow.entry.uid,
                    stepUid = step.uid
                ).bind()

                JobData(
                    job = job,
                    flow = flow,
                    currentStep = step,
                    executionData = executionData
                )
            }
        }

    suspend fun updateJob(entry: JobEntry): Either<AppException, Unit> =
        withContext(IO) {
            jobRepository.update(entry)
        }

    suspend fun removeJob(jobUid: String): Either<AppException, Unit> =
        withContext(IO) {
            either {
                jobRepository.removeByUid(jobUid)
            }
        }

    suspend fun moveJobToHistory(jobUid: String): Either<AppException, Unit> =
        withContext(IO) {
            either {
                jobRepository.moveToHistory(jobUid)
            }
        }

    suspend fun getJobs(): Either<AppException, List<JobEntry>> =
        withContext(IO) {
            either {
                jobRepository.getAll()
            }
        }

    suspend fun getCachedProjectByUid(projectUid: String): Either<AppException, ProjectEntry> =
        withContext(IO) {
            projectRepository.getCachedProjectByUid(projectUid)
        }

    suspend fun getCachedFlowByUid(flowUid: String): Either<AppException, FlowWithSteps> =
        withContext(IO) {
            flowRepository.getCachedFlowByUid(flowUid)
        }

    suspend fun resolveFlowByPathOrName(
        projectUid: String,
        pathOrName: String
    ): Either<AppException, FlowWithSteps?> =
        withContext(IO) {
            either {
                val flow = referenceResolver.resolveFlowByPathOrName(
                    projectUid = projectUid,
                    pathOrName = pathOrName
                ).bind()

                flowRepository.getFlowByUid(flow.uid).bind()
            }
        }

    suspend fun getFlowByUid(flowUid: String): Either<AppException, FlowWithSteps> =
        withContext(IO) {
            flowRepository.getFlowByUid(flowUid)
        }

    suspend fun getExecutionData(
        jobUid: String,
        flowUid: String,
        stepUid: String
    ): Either<AppException, LocalStepRun> =
        withContext(IO) {
            stepRunRepository.getOrCreate(jobUid, flowUid, stepUid)
        }

    suspend fun getJobByUid(jobUid: String): Either<AppException, JobEntry> =
        withContext(IO) {
            jobRepository.getJobByUid(jobUid)
        }

    suspend fun removeAllJobs(exclude: Set<String> = emptySet()): Either<AppException, Unit> =
        withContext(IO) {
            either {
                val removeUids = jobRepository.getAll()
                    .filter { entry -> entry.uid !in exclude }
                    .map { entry -> entry.uid }

                for (id in removeUids) {
                    jobRepository.removeByUid(id)
                }
            }
        }

    suspend fun saveFlowContent(
        flowUid: String,
        content: String
    ): Either<AppException, Unit> =
        withContext(IO) {
            flowRepository.saveFlowContent(flowUid, content)
        }

    suspend fun parseFlow(
        base64Content: String,
        contentHash: Hash,
        name: String? = null
    ): Either<AppException, FlowWithSteps> =
        withContext(IO) {
            either {
                val yamlFlow = parseFlowUseCase.parseBase64File(base64Content).bind()

                val flowName = yamlFlow.name.ifEmpty {
                    name ?: StringUtils.EMPTY
                }

                validateReferences(yamlFlow).bind()

                // TODO: will not work without server requests
                val (project, group) = if (yamlFlow.project != null || yamlFlow.group != null) {
                    referenceResolver.resolveProjectAndGroupByPath(
                        projectName = yamlFlow.project,
                        groupName = yamlFlow.group
                    ).bind()
                } else {
                    null to null
                }

                val projectUid = project?.uid ?: LOCAL_PROJECT_UID
                val groupUid = group?.uid

                val newUid = UUID.randomUUID().toString()
                val flowUid = "$projectUid:$flowName:$newUid"
                val flow = yamlFlow.convertToFlowEntry(
                    flowUid = flowUid,
                    projectUid = projectUid,
                    groupUid = groupUid,
                    sourceType = SourceType.LOCAL,
                    name = flowName,
                    contentHash = contentHash
                )
                val steps = yamlFlow.steps.convertToStepEntries(
                    flowUid = flowUid
                )

                FlowWithSteps(
                    entry = flow,
                    steps = steps
                )
            }
        }

    private suspend fun validateReferences(flow: YamlFlow): Either<AppException, Unit> =
        either {
            // TODO: improvement, check all nested flows recursively

            val names = flow.steps
                .mapNotNull { step ->
                    if (step is FlowStep.RunFlow) {
                        step.path
                    } else {
                        null
                    }
                }

            val projectUid = if (!flow.project.isNullOrEmpty()) {
                val project = referenceResolver.resolveProjectByName(
                    projectName = flow.project ?: StringUtils.EMPTY
                ).bind()

                project.uid
            } else {
                LOCAL_PROJECT_UID
            }

            val isLocalOnlyFlow = (projectUid == LOCAL_PROJECT_UID)

            for (name in names) {
                val resolveResult = referenceResolver.resolveFlowByPathOrName(
                    projectUid = projectUid,
                    pathOrName = name
                )

                if (!isLocalOnlyFlow && resolveResult.isLeft()) {
                    referenceResolver.resolveFlowByPathOrName(
                        projectUid = LOCAL_PROJECT_UID,
                        pathOrName = name
                    ).bind()
                } else {
                    resolveResult.bind()
                }
            }
        }

    suspend fun addFlowToJobQueue(
        flow: FlowWithSteps,
        onFinishAction: OnFinishAction
    ): Either<AppException, String> =
        withContext(IO) {
            either {
                val flowUid = flow.entry.uid

                flowRepository.removeFlowData(flowUid).bind()
                flowRepository.save(flow).bind()

                val firstStepUid = flow.steps.firstOrNull()?.uid
                    ?: raise(AppException("No steps found"))

                addJob(
                    flowUid = flowUid,
                    stepUid = firstStepUid,
                    jobUid = null,
                    onFinishAction = onFinishAction
                ).bind()
            }
        }

    suspend fun addFlowToJobQueue(
        flowUid: String,
        jobUid: String?,
        onFinishAction: OnFinishAction
    ): Either<AppException, String> =
        withContext(IO) {
            either {
                val flow = flowRepository.getFlowByUid(flowUid).bind()

                val firstStepUid = flow.steps.firstOrNull()?.uid
                    ?: raise(AppException("No steps found"))

                addJob(
                    flowUid = flowUid,
                    stepUid = firstStepUid,
                    jobUid = jobUid,
                    onFinishAction = onFinishAction
                ).bind()
            }
        }

    suspend fun uploadJobResult(jobUid: String): Either<AppException, FlowRunUploadResult> =
        withContext(IO) {
            either {
                val job = jobRepository.getJobByUid(jobUid).bind()
                val steps = stepRunRepository.getByJobUid(jobUid).bind()
                val flow = flowRepository.getCachedFlowByUid(job.flowUid).bind()
                val project = getCachedProjectByUid(flow.entry.projectUid).bind()
                val appData = getAppDataUseCase.getApplicationData(project.packageName).bind()
                val reportContent = fileCache.get(job.uid).bind()

                val stepToUpload = steps
                    .sortedByDescending { step -> step.id }
                    .firstOrNull { step ->
                        step.syncStatus == SyncStatus.WAITING_FOR_SYNC
                    }
                    ?: raise(AppException("Failed to find step to upload"))

                Timber.d("stepToUpload: stepUid=${stepToUpload.stepUid}")

                val stepResult = if (!stepToUpload.result.isNullOrEmpty()) {
                    jsonSerializer.deserialize<StepResult>(stepToUpload.result)
                        .mapLeft { exception -> AppException(cause = exception) }
                        .bind()
                } else {
                    null
                }
                val isSuccess = (stepResult != null && stepResult.isSuccess)

                val encodedReport = Base64Utils.encode(reportContent)

                val flowRun = PostFlowRunRequest(
                    flowId = job.flowUid,
                    durationInMillis = job.executionTime ?: 0L,
                    isSuccess = isSuccess,
                    result = stepResult?.result ?: StringUtils.EMPTY,
                    appVersionName = appData.appVersion.name,
                    appVersionCode = appData.appVersion.code.toString(),
                    reportBase64Content = encodedReport
                )

                val uploadResult = uploadFlowRunWithRetry(flowRun).bind()

                Timber.d("uploadResult: result=$uploadResult, jobUid=$jobUid")

                when (uploadResult?.isAccepted) {
                    true -> {
                        stepRunRepository.update(
                            stepToUpload.copy(
                                syncStatus = SyncStatus.SYNCED
                            )
                        )
                        jobRepository.moveToHistory(jobUid).bind()

                        uploadResult
                    }

                    false -> {
                        stepRunRepository.update(
                            stepToUpload.copy(
                                syncStatus = SyncStatus.FAILURE
                            )
                        )

                        uploadResult
                    }

                    else -> {
                        raise(AppException("Unable to upload flow"))
                    }
                }
            }
        }

    private suspend fun uploadFlowRunWithRetry(
        flowRun: PostFlowRunRequest
    ): Either<AppException, FlowRunUploadResult?> =
        either {
            var result: FlowRunUploadResult? = null
            var uploadCount = 0

            do {
                val uploadResult = flowRunRepository.uploadRun(flowRun)
                if (uploadResult.isRight()) {
                    result = uploadResult.unwrap()
                }
                uploadCount++
            } while (uploadCount < 3 && result == null)

            result
        }

    suspend fun onStepFinished(
        jobUid: String,
        entry: StepEntry,
        result: Either<FlowExecutionException, Any>
    ): Either<AppException, OnStepFinishedAction> =
        withContext(IO) {
            either {
                when (entry.stepVerificationType) {
                    StepVerificationType.LOCAL -> {
                        verifyLocally(jobUid, entry, result)
                            .bind()
                    }

                    StepVerificationType.REMOTE -> {
                        verifyRemotely(entry, result)
                            .bind()
                    }
                }
            }
        }

    private suspend fun verifyLocally(
        jobUid: String,
        stepEntry: StepEntry,
        result: Either<FlowExecutionException, Any>
    ): Either<AppException, OnStepFinishedAction> =
        either {
            val nextStepEntry = flowRepository.getNextStep(stepEntry.uid).bind()

            val stepRun = stepRunRepository.getOrCreate(
                jobUid = jobUid,
                flowUid = stepEntry.flowUid,
                stepUid = stepEntry.uid
            ).bind()

            val attemptCount = stepRun.attemptCount + 1
            val isFinishedSuccessfully = result.isRight()
            val isLast = (nextStepEntry == null)
            val isRetry = canRetry(stepEntry, attemptCount, result)

            val action = if (isFinishedSuccessfully) {
                if (isLast) {
                    OnStepFinishedAction.Complete
                } else {
                    if (nextStepEntry != null) {
                        OnStepFinishedAction.Next(nextStepUid = nextStepEntry.uid)
                    } else {
                        OnStepFinishedAction.Stop
                    }
                }
            } else {
                if (isRetry) {
                    OnStepFinishedAction.Retry
                } else {
                    OnStepFinishedAction.Stop
                }
            }

            val syncStatus = if (action == OnStepFinishedAction.Complete ||
                action == OnStepFinishedAction.Stop
            ) {
                SyncStatus.WAITING_FOR_SYNC
            } else {
                SyncStatus.NONE
            }

            val updatedStepRun = stepRun.copy(
                result = jsonSerializer.serialize(createStepResult(result)),
                attemptCount = attemptCount,
                syncStatus = syncStatus
            )
            stepRunRepository.update(updatedStepRun).bind()

            action
        }

    private fun verifyRemotely(
        entry: StepEntry,
        result: Either<Exception, Any>
    ): Either<AppException, OnStepFinishedAction> {
        TODO()
    }

    private suspend fun addJob(
        flowUid: String,
        stepUid: String,
        jobUid: String?,
        onFinishAction: OnFinishAction
    ): Either<AppException, String> =
        withContext(IO) {
            either {
                val uid = jobUid ?: UUID.randomUUID().toString()

                jobRepository.add(
                    JobEntry(
                        id = null,
                        flowUid = flowUid,
                        currentStepUid = stepUid,
                        uid = uid,
                        addedTimestamp = System.currentTimeMillis(),
                        executionTime = null,
                        finishedTimestamp = null,
                        executionResult = ExecutionResult.NONE,
                        status = JobStatus.PENDING,
                        onFinishAction = onFinishAction
                    )
                )

                settings.startJobUid = uid

                uid
            }
        }

    private fun canRetry(
        entry: StepEntry,
        attemptCount: Int,
        result: Either<FlowExecutionException, Any>
    ): Boolean {
        if (result.isRight()) {
            return false
        }

        val error = result.unwrapError().error
        Timber.d("error=$error")
        val isFlaky = (
            entry.command.isStepFlaky() ||
                (error != null && error.isFlakyException())
            )

        return isFlaky && attemptCount < getFlakyStepMaxRetryCount()
    }

    private fun createStepResult(result: Either<FlowExecutionException, Any>): StepResult {
        return when (result) {
            is Either.Right -> {
                StepResult(
                    isSuccess = true,
                    result = result.value.toString(),
                    error = null
                )
            }

            is Either.Left -> {
                val error = result.value.error

                StepResult(
                    isSuccess = false,
                    result = if (error == null) {
                        result.value.toString()
                    } else {
                        null
                    },
                    error = error
                )
            }
        }
    }

    private fun getFlakyStepMaxRetryCount(): Int {
        return settings.numberOfRetries
    }

    fun getDelayBetweenSteps(): Long {
        return DELAY_BETWEEN_STEPS * settings.delayScaleFactor
    }

    companion object {
        private val DELAY_BETWEEN_STEPS = 2.seconds.inWholeMilliseconds
        const val LOCAL_PROJECT_UID = "Local"
    }
}