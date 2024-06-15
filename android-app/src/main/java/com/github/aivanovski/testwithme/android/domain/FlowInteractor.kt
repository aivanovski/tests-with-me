package com.github.aivanovski.testwithme.android.domain

import arrow.core.raise.either
import com.github.aivanovski.testwithme.android.data.repository.FlowRepository
import com.github.aivanovski.testwithme.android.data.Settings
import com.github.aivanovski.testwithme.android.data.repository.ExecutionDataRepository
import com.github.aivanovski.testwithme.android.entity.db.StepEntry
import com.github.aivanovski.testwithme.android.entity.OnStepFinishedAction
import com.github.aivanovski.testwithme.android.data.repository.JobRepository
import com.github.aivanovski.testwithme.android.domain.usecases.GetCurrentJobUseCase
import com.github.aivanovski.testwithme.android.domain.usecases.ParseFlowFileUseCase
import com.github.aivanovski.testwithme.android.entity.FlowSourceType
import com.github.aivanovski.testwithme.android.entity.FlowWithSteps
import com.github.aivanovski.testwithme.android.entity.JobData
import com.github.aivanovski.testwithme.android.entity.OnFinishAction
import com.github.aivanovski.testwithme.android.entity.JobStatus
import com.github.aivanovski.testwithme.android.entity.StepVerificationType
import com.github.aivanovski.testwithme.android.entity.db.ExecutionData
import com.github.aivanovski.testwithme.android.entity.db.JobEntry
import com.github.aivanovski.testwithme.android.entity.exception.AppException
import com.github.aivanovski.testwithme.android.entity.exception.FlowException
import com.github.aivanovski.testwithme.android.extensions.unwrapError
import com.github.aivanovski.testwithme.entity.FlowStep
import com.github.aivanovski.testwithme.entity.exception.AssertionException
import com.github.aivanovski.testwithme.entity.exception.FailedToGetUiNodesException
import com.github.aivanovski.testwithme.entity.exception.NodeException
import java.util.UUID
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import arrow.core.Either

class FlowInteractor(
    private val settings: Settings,
    private val flowRepository: FlowRepository,
    private val runnerRepository: JobRepository,
    private val executionRepository: ExecutionDataRepository,
    private val getCurrentJobUseCase: GetCurrentJobUseCase,
    private val parseFlowUseCase: ParseFlowFileUseCase
) {

    suspend fun getCurrentJobData(): Either<AppException, JobData> = withContext(IO) {
        either {
            val getJobResult = getCurrentJobUseCase.getCurrentJob()
            if (getJobResult.isLeft()) {
                raise(getJobResult.unwrapError())
            }

            val job = getJobResult.getOrNull()
                ?: raise(AppException("Unable to find current job"))

            val flow = getFlowByUid(job.flowUid).bind()

            val step = flow.steps.firstOrNull { step -> step.uid == job.currentStepUid }
                ?: raise(AppException("Unable to find step: ${job.currentStepUid}"))

            val executionData = executionRepository.getOrCreate(
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

    suspend fun getCurrentStepEntry(): Either<AppException, StepEntry?> = withContext(IO) {
        either {
            val runningEntry = getCurrentJobUseCase.getCurrentJob().bind()

            if (runningEntry != null) {
                flowRepository.getStepByUid(runningEntry.currentStepUid).bind()
            } else {
                null
            }
        }
    }

    suspend fun updateJob(
        entry: JobEntry
    ): Either<AppException, Unit> = withContext(IO) {
        runnerRepository.update(entry)
    }

    suspend fun removeJob(
        jobUid: String
    ): Either<AppException, Unit> = withContext(IO) {
        either {
            runnerRepository.removeByUid(jobUid)
        }
    }

    suspend fun getJobs(): Either<AppException, List<JobEntry>> = withContext(IO) {
        either {
            runnerRepository.getAll()
        }
    }

    suspend fun getFlowByUid(
        flowUid: String
    ): Either<AppException, FlowWithSteps> = withContext(IO) {
        flowRepository.getFlowByUid(flowUid)
    }

    suspend fun getStepByUid(
        stepUid: String
    ): Either<AppException, StepEntry> = withContext(IO) {
        flowRepository.getStepByUid(stepUid)
    }

    suspend fun getExecutionData(
        jobUid: String,
        flowUid: String,
        stepUid: String
    ): Either<AppException, ExecutionData> = withContext(IO) {
        executionRepository.getOrCreate(jobUid, flowUid, stepUid)
    }

    suspend fun getJobByUid(
        jobUid: String
    ): Either<AppException, JobEntry> = withContext(IO) {
        runnerRepository.getJobByUid(jobUid)
    }

    suspend fun removeAllJobs(
        exclude: Set<String>
    ): Either<AppException, Unit> = withContext(IO) {
        either {
            val removeUids = runnerRepository.getAll()
                .filter { entry -> entry.uid !in exclude }
                .map { entry -> entry.uid }

            for (id in removeUids) {
                runnerRepository.removeByUid(id)
            }
        }
    }

    suspend fun parseAndAddToJobQueue(
        base64Content: String
    ): Either<AppException, String> = withContext(IO) {
        either {
            val flow = parseFlowUseCase.parseBase64File(base64Content)
                .map { flow ->
                    flow.copy(
                        entry = flow.entry.copy(
                            sourceType = FlowSourceType.LOCAL
                        )
                    )
                }
                .bind()

            val flowUid = flow.entry.uid

            flowRepository.removeFlowData(flowUid).bind()

            flowRepository.save(flow).bind()

            val firstStepUid = flow.steps.firstOrNull()?.uid
                ?: raise(AppException("No steps found"))

            addRunnerEntry(
                flowUid = flowUid,
                stepUid = firstStepUid
            )
                .bind()
        }
    }

    suspend fun onStepFinished(
        jobUid: String,
        entry: StepEntry,
        result: Either<Exception, Any>
    ): Either<AppException, OnStepFinishedAction> = withContext(IO) {
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
        result: Either<Exception, Any>
    ): Either<AppException, OnStepFinishedAction> = either {
        val nextStepEntry = flowRepository.getNextStep(stepEntry.uid).bind()

        val flow = flowRepository.getFlowByUid(flowUid = stepEntry.flowUid).bind()

        val executionData = executionRepository.getOrCreate(
            jobUid = jobUid,
            flowUid = stepEntry.flowUid,
            stepUid = stepEntry.uid
        ).bind()

        val isFinishedSuccessfully = result.isRight()
        val isLast = (nextStepEntry == null)
        val updatedExecutionData = executionData.copy(
            result = result.toString(),
            attemptCount = executionData.attemptCount + 1
        )
        val isRetry = canRetry(stepEntry, updatedExecutionData, result)

        executionRepository.update(updatedExecutionData).bind()

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

        action
    }

    private fun verifyRemotely(
        entry: StepEntry,
        result: Either<Exception, Any>
    ): Either<AppException, OnStepFinishedAction> {
        TODO()
    }

    private suspend fun addRunnerEntry(
        flowUid: String,
        stepUid: String
    ): Either<AppException, String> = withContext(IO) {
        either {
            val uid = UUID.randomUUID().toString()

            runnerRepository.add(
                JobEntry(
                    id = null,
                    flowUid = flowUid,
                    currentStepUid = stepUid,
                    uid = uid,
                    addedTimestamp = System.currentTimeMillis(),
                    status = JobStatus.PENDING,
                    onFinishAction = OnFinishAction.STOP
                )
            )

            settings.startJobUid = uid

            uid
        }
    }

    private fun canRetry(
        entry: StepEntry,
        executionData: ExecutionData,
        result: Either<Exception, Any>
    ): Boolean {
        if (result.isRight()) {
            return false
        }

        val exception = result.unwrapError()
        val isFlaky = (entry.command.isStepFlaky() || exception.isFlakyException())

        return isFlaky && executionData.attemptCount < 3
    }

    private fun FlowStep.isStepFlaky(): Boolean {
        return this is FlowStep.AssertVisible ||
            this is FlowStep.AssertNotVisible ||
            this is FlowStep.TapOn
    }

    private fun Exception.isFlakyException(): Boolean {
        return this is FlowException &&
            (this.cause is NodeException ||
                this.cause is FailedToGetUiNodesException ||
                this.cause is AssertionException)
    }

    companion object {
    }
}