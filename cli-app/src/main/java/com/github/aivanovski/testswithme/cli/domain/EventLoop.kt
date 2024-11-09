package com.github.aivanovski.testswithme.cli.domain

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.driverServerApi.request.StartTestRequest
import com.github.aivanovski.testswithme.android.driverServerApi.response.DriverStatus
import com.github.aivanovski.testswithme.android.driverServerApi.response.JobDtoStatus
import com.github.aivanovski.testswithme.cli.data.device.DeviceConnection
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.cli.domain.printer.OutputPrinter
import com.github.aivanovski.testswithme.cli.entity.exception.AppException
import com.github.aivanovski.testswithme.utils.Base64Utils
import com.github.aivanovski.testswithme.utils.StringUtils
import java.nio.file.Path
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EventLoop(
    private val fsProvider: FileSystemProvider,
    private val printer: OutputPrinter
) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val messageQueue: Queue<Message> = ConcurrentLinkedQueue()
    private val watcher = FileWatcherImpl(printer)

    @Volatile
    private var lastSentFile: SentFile? = null

    @Volatile
    private var isDriverReady = false

    @Volatile
    private var isActive = true

    @Volatile
    private var delayJob: Job? = null

    fun loop(
        filePath: String,
        connection: DeviceConnection
    ) {
        val file = Path(filePath)

        printer.printLine("Watch file: ${file.fileName}")

        watcher.watch(
            file = file,
            onContentChanged = { path -> handleFileChanged(path) }
        )

        val heartbeatJob = scope.launch {
            while (isActive) {
                if (!messageQueue.contains(Message.SendHeartbeatRequest)) {
                    messageQueue.add(Message.SendHeartbeatRequest)
                }

                delay(1000L)
            }
        }

        val loopJob = scope.launch {
            while (isActive) {
                while (messageQueue.isNotEmpty() && isActive) {
                    val message = messageQueue.poll()
                    printer.debugLine("Process message: $message")

                    val result = handleMessage(connection, message)

                    if (result.isLeft()) {
                        if (message == Message.SendHeartbeatRequest) {
                            retryMessage(
                                connection = connection,
                                message = message,
                                onSuccess = {
                                    printer.printLine("Connected")
                                },
                                onFailure = {
                                    printer.debugLine("Max heartbeat retry limit reached")
                                    isActive = false
                                }
                            )
                        } else {
                            printer.debugLine("Failed to process message: $message")
                            isActive = false
                        }
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
            joinAll(loopJob, heartbeatJob)
        }

        printer.debugLine("Event loop is finished")
    }

    private fun handleFileChanged(file: Path) {
        if (!isDriverReady) {
            return
        }

        delayJob?.cancel()
        delayJob = scope.launch {
            delay(2000L)

            messageQueue.add(Message.SendFile(file))
            delayJob = null
        }
    }

    private suspend fun handleMessage(
        connection: DeviceConnection,
        message: Message
    ): Either<AppException, Unit> =
        either {
            when (message) {
                is Message.SendHeartbeatRequest -> sendHeartbeatRequest(connection).bind()
                is Message.SendFile -> sendFileRequest(connection, message.file).bind()
            }
        }

    private suspend fun sendHeartbeatRequest(
        connection: DeviceConnection
    ): Either<AppException, Unit> =
        either {
            val status = connection.api.getStatus().bind()
            printer.debugLine("Status: $status")

            val sentFile = lastSentFile
            if (sentFile != null) {
                val job = status.jobs.firstOrNull { job -> job.id == sentFile.jobId }

                if (job != null &&
                    (job.status == JobDtoStatus.FINISHED || job.status == JobDtoStatus.CANCELLED)
                ) {
                    val message = when (job.status) {
                        JobDtoStatus.FINISHED -> "Test Passed"
                        JobDtoStatus.CANCELLED -> "Test Failed"
                        else -> throw IllegalStateException()
                    }

                    printer.printLine(message)
                    lastSentFile = null
                }
            }

            val isDriverRunning = (status.driverStatus == DriverStatus.RUNNING)
            if (isDriverRunning != isDriverReady) {
                isDriverReady = isDriverRunning
                if (isDriverReady) {
                    printer.printLine("Test Driver is running")
                } else {
                    printer.printLine(
                        "Test Driver is not running, please enable it in device Accessibility Settings"
                    )
                }
            }
        }

    private suspend fun sendFileRequest(
        connection: DeviceConnection,
        file: Path
    ): Either<AppException, Unit> =
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

    private suspend fun retryMessage(
        connection: DeviceConnection,
        message: Message,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ): Either<AppException, Unit> =
        either {
            var retryCount = 1

            while (retryCount < MAX_RETRY_COUNT) {
                val retryResult = handleMessage(connection, message)

                if (retryResult.isRight()) {
                    onSuccess.invoke()
                    break
                }

                retryCount++
            }

            if (retryCount == MAX_RETRY_COUNT) {
                onFailure.invoke()
            }
        }

    data class SentFile(
        val jobId: String,
        val file: Path
    )

    sealed interface Message {
        data object SendHeartbeatRequest : Message
        data class SendFile(val file: Path) : Message
    }

    companion object {
        private const val MAX_RETRY_COUNT = 3
    }
}