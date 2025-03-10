package com.github.aivanovski.testswithme.android.domain.flow

import android.view.accessibility.AccessibilityNodeInfo
import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.entity.ExecutionResult
import com.github.aivanovski.testswithme.android.entity.OnStepFinishedAction
import com.github.aivanovski.testswithme.android.entity.db.FlowEntry
import com.github.aivanovski.testswithme.android.entity.db.JobEntry
import com.github.aivanovski.testswithme.android.entity.db.StepEntry
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.entity.PreconditionedResult
import com.github.aivanovski.testswithme.entity.exception.ExternalException
import com.github.aivanovski.testswithme.entity.exception.FlowExecutionException
import com.github.aivanovski.testswithme.entity.exception.StepVerificationException
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.flow.commands.CompositeStepCommand
import com.github.aivanovski.testswithme.flow.commands.ExecutableStepCommand
import com.github.aivanovski.testswithme.flow.commands.Precondition
import com.github.aivanovski.testswithme.flow.commands.RunFlow
import com.github.aivanovski.testswithme.flow.commands.StepCommand
import com.github.aivanovski.testswithme.flow.error.FlowError
import com.github.aivanovski.testswithme.flow.runner.ExecutionContext
import com.github.aivanovski.testswithme.flow.runner.listener.FlowLifecycleListener
import kotlinx.coroutines.delay

class CommandExecutor(
    private val interactor: FlowRunnerInteractor,
    private val context: ExecutionContext<AccessibilityNodeInfo>,
    private val lifecycleListener: FlowLifecycleListener
) {

    suspend fun execute(
        isFirstStep: Boolean,
        job: JobEntry,
        flow: FlowEntry,
        stepEntry: StepEntry,
        command: StepCommand,
        stepIndex: Int,
        attemptIndex: Int
    ): Either<AppException, OnStepFinishedAction> =
        either {
            if (isFirstStep) {
                lifecycleListener.onFlowStarted(flow)
            }

            lifecycleListener.onStepStarted(flow, stepEntry.command, stepIndex, attemptIndex)

            val result = executeCommand(job, command)

            val nextAction = interactor.onStepFinished(job.uid, stepEntry, result).bind()

            lifecycleListener.onStepFinished(flow, stepEntry.command, stepIndex, result)

            when (nextAction) {
                is OnStepFinishedAction.Next -> {
                    val updatedJob = job.copy(currentStepUid = nextAction.nextStepUid)
                    interactor.updateJob(updatedJob).bind()

                    nextAction
                }

                is OnStepFinishedAction.Complete -> {
                    lifecycleListener.onFlowFinished(flow, result)

                    val updatedJob = job.copy(
                        finishedTimestamp = System.currentTimeMillis(),
                        executionResult = ExecutionResult.SUCCESS
                    )
                    interactor.updateJob(updatedJob).bind()

                    nextAction
                }

                is OnStepFinishedAction.Stop -> {
                    lifecycleListener.onFlowFinished(flow, result)

                    if (result.isLeft()) {
                        val updatedJob = job.copy(
                            finishedTimestamp = System.currentTimeMillis(),
                            executionResult = ExecutionResult.FAILED
                        )
                        interactor.updateJob(updatedJob).bind()

                        raise(AppException(cause = result.unwrapError()))
                    } else {
                        nextAction
                    }
                }

                else -> {
                    nextAction
                }
            }
        }

    private suspend fun executeCompositeCommand(
        job: JobEntry,
        compositeCommand: CompositeStepCommand
    ): Either<FlowExecutionException, Any> =
        either {
            var lastResult: Either<FlowExecutionException, Any>? = null

            if (compositeCommand !is RunFlow) {
                throw IllegalStateException() // TODO: check
            }

            var flow = interactor.getCachedFlowByUid(compositeCommand.flowUid)
                .mapLeft { exception -> ExternalException(cause = exception) }
                .bind()

            lifecycleListener.onFlowStarted(flow.entry)

            val commands = compositeCommand.getCommands()
            var commandIndex = 0
            while (commandIndex < commands.size) {
                delay(interactor.getDelayBetweenSteps())

                flow = interactor.getCachedFlowByUid(compositeCommand.flowUid)
                    .mapLeft { exception -> ExternalException(cause = exception) }
                    .bind()
                val command = commands[commandIndex]

                val stepEntry = flow.steps[commandIndex]
                val stepUid = stepEntry.uid

                val executionData = interactor.getExecutionData(
                    jobUid = job.uid,
                    flowUid = flow.entry.uid,
                    stepUid = stepUid
                )
                    .mapLeft { exception -> ExternalException(cause = exception) }
                    .bind()

                lifecycleListener.onStepStarted(
                    flow.entry,
                    stepEntry.command,
                    commandIndex,
                    executionData.attemptCount
                )

                val commandResult = executeCommand(job, command)

                val nextAction = interactor.onStepFinished(job.uid, stepEntry, commandResult)
                    .mapLeft { exception -> StepVerificationException(exception) }
                    .bind()

                lifecycleListener.onStepFinished(
                    flow.entry,
                    stepEntry.command,
                    commandIndex,
                    commandResult
                )

                lastResult = commandResult

                when (nextAction) {
                    is OnStepFinishedAction.Next -> {
                        commandIndex++
                    }

                    OnStepFinishedAction.Stop -> {
                        lifecycleListener.onFlowFinished(flow.entry, commandResult)
                        if (commandResult.isLeft()) {
                            raise(commandResult.unwrapError())
                        } else {
                            raise(FlowExecutionException(message = "Child flow was sopped"))
                        }
                    }

                    OnStepFinishedAction.Complete -> {
                        lifecycleListener.onFlowFinished(flow.entry, commandResult)
                        break
                    }

                    OnStepFinishedAction.Retry -> {
                    }
                }
            }

            return lastResult ?: raise(FlowExecutionException(message = "No steps were executed"))
        }

    private suspend fun executeCommand(
        job: JobEntry,
        command: StepCommand
    ): Either<FlowExecutionException, Any> =
        either {
            when (command) {
                is ExecutableStepCommand<*> -> {
                    command.execute(context)
                        .transformError()
                        .bind()
                }

                is CompositeStepCommand -> {
                    executeCompositeCommand(
                        job = job,
                        compositeCommand = command
                    ).bind()
                }

                is Precondition -> {
                    val underlyingCommand = command.command
                    if (underlyingCommand is CompositeStepCommand) {
                        val preconditionResult = command.execute(context).transformError()
                        val isSatisfied = preconditionResult.isRight() &&
                            preconditionResult.unwrap().isSatisfied

                        if (isSatisfied) {
                            executeCompositeCommand(
                                job = job,
                                compositeCommand = underlyingCommand
                            )
                                .map { compositeResult ->
                                    PreconditionedResult(
                                        isSatisfied = true,
                                        result = compositeResult
                                    )
                                }.bind()
                        } else {
                            preconditionResult.bind()
                        }
                    } else {
                        command.execute(context)
                            .transformError()
                            .bind()
                    }
                }

                else -> throw IllegalStateException()
            }
        }

    private fun <T> Either<FlowError, T>.transformError(): Either<FlowExecutionException, T> {
        return this.mapLeft { error -> FlowExecutionException.fromFlowError(error) }
    }
}