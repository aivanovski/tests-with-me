package com.github.aivanovski.testswithme.cli.domain

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.driverServerApi.dto.DriverStatusDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.ExecutionResultDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.JobDto
import com.github.aivanovski.testswithme.android.driverServerApi.dto.JobStatusDto
import com.github.aivanovski.testswithme.android.driverServerApi.request.StartTestRequest
import com.github.aivanovski.testswithme.cli.data.device.DeviceConnection
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.cli.domain.printer.OutputLevel
import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinter
import com.github.aivanovski.testswithme.cli.entity.ConnectionState
import com.github.aivanovski.testswithme.cli.entity.exception.AppException
import com.github.aivanovski.testswithme.cli.extensions.isReadyToStartTest
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.utils.Base64Utils
import com.github.aivanovski.testswithme.utils.StringUtils
import java.nio.file.Path
import java.util.Collections.synchronizedList
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EventLoop(
    private val fsProvider: FileSystemProvider,
    private val printer: OutputPrinter,
    private val connection: DeviceConnection
) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val messageQueue = synchronizedList(LinkedList<Message>())
    private val pendingMessages: Queue<Message> = ConcurrentLinkedQueue()
    private val watcher = FileWatcherImpl(printer)

    @Volatile
    private var lastSentFile: SentFile? = null

    @Volatile
    private var isActive = true

    @Volatile
    private var delayJob: Job? = null

    @Volatile
    private var wasReady: Boolean = false

    fun loop(filePath: String) {
        val file = Path(filePath)

        printer.printLine("Watch file: ${file.fileName}")

        pendingMessages.add(Message.SendStartTestRequest(file))

        watcher.watch(
            file = file,
            onContentChanged = { path -> handleFileChanged(path) }
        )

        val stateJob = scope.launch {
            connection.state.collectLatest { state ->
                handleConnectionStateChanged(state)
            }
        }

        val heartbeatJob = scope.launch {
            while (isActive) {
                if (!messageQueue.contains(Message.SendHeartbeatRequest)) {
                    messageQueue.add(Message.SendHeartbeatRequest)
                }

                delay(1000L)
            }
        }

        val loopJob = scope.launch {
            var heartBeatRetry = 0

            while (isActive) {
                while (messageQueue.isNotEmpty() && isActive) {
                    val message = messageQueue.removeFirst()
                    printer.debugLine("Process message: $message")

                    val result = processMessage(message)

                    if (result.isLeft()) {
                        if (message == Message.SendHeartbeatRequest) {
                            connection.state.value = ConnectionState(
                                isConnected = false,
                                isDriverReady = false
                            )
                            messageQueue.add(0, Message.SendHeartbeatRequest)

                            if (heartBeatRetry < MAX_RETRY_COUNT) {
                                heartBeatRetry++
                            } else {
                                exitLoop()
                            }
                        } else {
                            printer.debugLine("Failed to process message: $message")

                            if (OutputLevel.isDebug()) {
                                result.unwrapError().printStackTrace()
                            }

                            exitLoop()
                        }
                    } else {
                        heartBeatRetry = 0
                    }
                }

                if (isActive) {
                    delay(200L)
                }
            }

            watcher.cancel()
            delayJob?.cancel()
        }

        runBlocking {
            joinAll(loopJob, heartbeatJob, stateJob)
        }

        printer.debugLine("Event loop is finished")
    }

    private fun handleConnectionStateChanged(state: ConnectionState) {
        when {
            state.isConnected && state.isDriverReady -> {
                printer.printLine("Connected")
            }

            state.isConnected && !state.isDriverReady -> {
                printer.printLine("Driver is not running")
            }

            else -> {
                printer.printLine("Disconnected")
            }
        }

        val isReady = state.isReadyToStartTest()
        if (wasReady != isReady && isReady) {
            messageQueue.addAll(pendingMessages)
            pendingMessages.clear()
        }

        wasReady = isReady
    }

    private fun handleFileChanged(file: Path) {
        val isAlreadyPending =
            pendingMessages.any { message -> message is Message.SendStartTestRequest }
        if (isAlreadyPending) {
            return
        }

        val isAbleToStartTest = connection.state.value.isReadyToStartTest()

        if (!isAbleToStartTest) {
            pendingMessages.add(Message.SendStartTestRequest(file))
            return
        }

        delayJob?.cancel()
        delayJob = scope.launch {
            delay(2000L)

            messageQueue.add(Message.SendStartTestRequest(file))
            delayJob = null
        }
    }

    private suspend fun processMessage(message: Message): Either<AppException, Unit> =
        either {
            when (message) {
                is Message.SendHeartbeatRequest -> sendHeartbeatRequest().bind()
                is Message.SendStartTestRequest -> sendStartTestRequest(message.file).bind()
                is Message.SendGetJobRequest -> sendGetJobRequest(message.jobId).bind()
            }
        }

    private suspend fun sendHeartbeatRequest(): Either<AppException, Unit> =
        either {
            val status = connection.api.getStatus().bind()
            printer.debugLine("Status: $status")

            val sentFile = lastSentFile
            if (sentFile != null) {
                val job = status.jobs.firstOrNull { job -> job.id == sentFile.jobId }

                if (job != null &&
                    (job.isSuccessfullyFinished() || job.isFailed())
                ) {
                    messageQueue.add(Message.SendGetJobRequest(jobId = sentFile.jobId))
                    lastSentFile = null
                }
            }

            connection.state.value = ConnectionState(
                isConnected = true,
                isDriverReady = (status.driverStatus == DriverStatusDto.RUNNING)
            )
        }

    private suspend fun sendGetJobRequest(jobId: String): Either<AppException, Unit> =
        either {
            val response = connection.api.getJob(jobId).bind()

            val job = response.job

            if (job.isSuccessfullyFinished()) {
                printer.printLine("Test Passed")
            } else {
                printer.printLine("Test Failed")

                val failedStep = response.flow.steps.firstOrNull { stepRun ->
                    val result = stepRun.result
                    result != null && result.startsWith("Either.Left")
                }

                if (failedStep != null) {
                    printer.printLine("    Step at index ${failedStep.index + 1} is failed")
                    if (failedStep.result != null) {
                        printer.printLine("    ${failedStep.result}")
                    }
                }
            }
        }

    private suspend fun sendStartTestRequest(file: Path): Either<AppException, Unit> =
        either {
            lastSentFile = null

            val filePath = file.pathString
            val fileName = file.name

            val content = fsProvider.read(filePath)
                .mapLeft { exception -> AppException(cause = exception) }
                .bind()

            if (content.isBlank()) {
                return@either
            }

            val base64Content = Base64Utils.encode(content)

            printer.printLine("Sending test file: $fileName")

            val response = connection.api.startTest(
                request = StartTestRequest(
                    name = fileName,
                    base64Content = base64Content
                )
            ).bind()

            val error = response.error

            when {
                response.isStarted -> {
                    lastSentFile = SentFile(
                        jobId = response.jobId ?: StringUtils.EMPTY,
                        file = file
                    )
                }

                error != null -> {
                    printer.printLine("ERROR: Failed to process $fileName")

                    val responseMessage = Base64Utils.decode(error.base64Message)
                        .mapLeft { exception -> AppException(cause = exception) }
                        .bind()
                        ?: StringUtils.EMPTY

                    val lines = responseMessage.split("\n")

                    for (line in lines) {
                        printer.printLine("    $line")
                    }
                }
            }

            printer.debugLine("Response: $response")
        }

    private fun exitLoop() {
        isActive = false
        scope.cancel()
    }

    private fun JobDto.isSuccessfullyFinished(): Boolean {
        return status == JobStatusDto.FINISHED && executionResult == ExecutionResultDto.SUCCESS
    }

    private fun JobDto.isFailed(): Boolean {
        return (status == JobStatusDto.FINISHED && executionResult == ExecutionResultDto.FAILED) ||
            status == JobStatusDto.CANCELLED
    }

    data class SentFile(
        val jobId: String,
        val file: Path
    )

    sealed interface Message {
        data object SendHeartbeatRequest : Message
        data class SendStartTestRequest(val file: Path) : Message
        data class SendGetJobRequest(val jobId: String) : Message
    }

    companion object {
        private const val MAX_RETRY_COUNT = 3
    }
}