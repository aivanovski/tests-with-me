package com.github.aivanovski.testswithme.cli.presentation.main

import arrow.core.Either
import arrow.core.raise.either
import com.fasterxml.jackson.core.JacksonException
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.DriverStatusDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.ExecutionResultDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.JobDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.JobStatusDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.dto.ScreenStateDto
import com.github.aivanovski.testswithme.android.gatewayServerApi.response.StartTestResponse
import com.github.aivanovski.testswithme.cli.data.argument.Arguments
import com.github.aivanovski.testswithme.cli.data.device.DeviceConnection
import com.github.aivanovski.testswithme.cli.data.file.FileWatcherImpl
import com.github.aivanovski.testswithme.cli.entity.ScreenSize
import com.github.aivanovski.testswithme.cli.entity.exception.AppException
import com.github.aivanovski.testswithme.cli.entity.exception.ConnectionLostException
import com.github.aivanovski.testswithme.cli.entity.exception.FailedToFindDeviceException
import com.github.aivanovski.testswithme.cli.entity.exception.ParsingException
import com.github.aivanovski.testswithme.cli.extensions.isReadyToStartTest
import com.github.aivanovski.testswithme.cli.extensions.toReadableString
import com.github.aivanovski.testswithme.cli.presentation.core.CliStrings
import com.github.aivanovski.testswithme.cli.presentation.main.command.MessageQueue
import com.github.aivanovski.testswithme.cli.presentation.main.model.DeviceState
import com.github.aivanovski.testswithme.cli.presentation.main.model.FileState
import com.github.aivanovski.testswithme.cli.presentation.main.model.MainViewState
import com.github.aivanovski.testswithme.cli.presentation.main.model.TestData
import com.github.aivanovski.testswithme.cli.presentation.main.model.TestState
import com.github.aivanovski.testswithme.cli.presentation.main.model.TextColor
import com.github.aivanovski.testswithme.domain.fomatters.AsciScreenNodeFormatter
import com.github.aivanovski.testswithme.extensions.describe
import com.github.aivanovski.testswithme.extensions.format
import com.github.aivanovski.testswithme.extensions.getRootCause
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.utils.Base64Utils
import com.github.aivanovski.testswithme.utils.StringUtils
import com.github.aivanovski.testswithme.utils.mutableStateFlow
import java.lang.StringBuilder
import java.nio.file.Path
import java.time.Instant
import kotlin.io.path.Path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

class MainViewModel(
    private val interactor: MainInteractor,
    private val strings: CliStrings,
    private val arguments: Arguments
) {

    val viewState = MutableStateFlow(MainViewState())

    private val fileWatcher = FileWatcherImpl(
        onContentChanged = { file ->
            onFileChanged(file)
        }
    )

    private val scope = CoroutineScope(Dispatchers.Default)
    private val queue = MessageQueue()
    private var loopJob: Job? by mutableStateFlow(null)
    private var heartbeatSupplierJob: Job? by mutableStateFlow(null)
    private var connection: DeviceConnection? by mutableStateFlow(null)
    private var errorMessage: String? by mutableStateFlow(null)
    private var currentDeviceState: DeviceState by mutableStateFlow(DeviceState.Connecting)
    private var currentTestState: TestState by mutableStateFlow(TestState.Awaiting)
    private var currentFileState: FileState by mutableStateFlow(FileState.NoState)
    private var fileDelayJob: Job? by mutableStateFlow(null)
    private var loopResult: Either<AppException, Unit>? by mutableStateFlow(null)
    private var isLoopActive by mutableStateFlow(true)
    private var lastSentData: TestData? by mutableStateFlow(null)
    private var heartBeatRetry: Int by mutableStateFlow(0)
    private var screenState by mutableStateFlow<ScreenStateDto?>(null)

    fun start() {
        val result = runBlocking {
            startApp()
        }

        fileWatcher.cancel()
        if (result.isLeft()) {
            errorMessage = result.unwrapError().formatRootMessageOrStacktrace()
            rebuildViewState()
        }

        logger.debug("Application finished it's work: result=%s".format(result))
    }

    private suspend fun startApp(): Either<AppException, Unit> =
        either {
            rebuildViewState()
            connection = prepareLoop().bind()
            startLoop().bind()
        }

    private suspend fun prepareLoop(): Either<AppException, DeviceConnection> =
        withContext(Dispatchers.IO) {
            either {
                val (connection, connectionState) = interactor.connectToDevice().bind()

                val file = getFile()

                val parseFileResult = interactor.readAndParseFile(file)
                if (parseFileResult.isRight()) {
                    currentFileState = FileState.QueuedForSending
                    queue.add(Message.SendStartTestRequest(file))
                } else {
                    currentFileState = FileState.InvalidFile(
                        message = parseFileResult.unwrapError().formatReadableErrorMessage()
                    )
                }

                onDeviceStateChanged(connectionState)

                fileWatcher.watch(file = file)

                rebuildViewState()

                connection
            }
        }

    private suspend fun startLoop(): Either<AppException, Unit> =
        either {
            heartbeatSupplierJob = scope.launch {
                while (isLoopActive) {
                    if (!queue.containsMessageByType(Message.SendHeartbeatRequest::class)) {
                        queue.add(Message.SendHeartbeatRequest)
                    }

                    delay(1000L)
                }
            }

            loopJob = scope.launch {
                while (isLoopActive) {
                    while (!queue.isEmpty() && isLoopActive) {
                        processNextMessageInQueue()
                    }

                    if (isLoopActive) {
                        delay(200L)
                    }
                }
            }

            listOfNotNull(loopJob, heartbeatSupplierJob)
                .joinAll()

            loopResult?.bind() ?: Unit
        }

    private fun exitLoop() {
        isLoopActive = false
        loopJob?.cancel()
        loopJob = null
    }

    private suspend fun processNextMessageInQueue() {
        val message = queue.poll() ?: return

        if (message !is Message.SendHeartbeatRequest) {
            logger.debug("Processing message: %s".format(message))
        }

        val result = processMessage(message)

        if (message !is Message.SendHeartbeatRequest) {
            logger.debug("Processing message: %s".format(message))
        }

        if (result.isLeft()) {
            val cause = result.unwrapError()
            logger.debug("   Root cause: %s".format(cause.getRootCause()))
            logger.debug(cause.stackTraceToString())

            when (message) {
                Message.SendHeartbeatRequest -> {
                    if (heartBeatRetry < MAX_RETRY_COUNT) {
                        heartBeatRetry++
                        queue.push(message)

                        onDeviceStateChanged(
                            DeviceState.Connected(
                                isConnected = false,
                                isDriverReady = false
                            )
                        )
                    } else {
                        loopResult = result
                        exitLoop()
                    }
                }

                is Message.SendStartTestRequest -> {
                    if (cause.isYamlParsingException()) {
                        val errMessage = result.unwrapError().formatReadableErrorMessage()

                        currentTestState = TestState.Failed
                        currentFileState = FileState.InvalidFile(errMessage)
                        errorMessage = errMessage
                    } else {
                        currentTestState = TestState.Failed
                        errorMessage = result.unwrapError().stackTraceToString()
                    }

                    rebuildViewState()
                }

                is Message.SendGetJobRequest -> {
                    errorMessage = result.unwrapError().stackTraceToString()

                    rebuildViewState()
                }
            }
        } else {
            heartBeatRetry = 0
        }
    }

    private suspend fun processMessage(message: Message): Either<AppException, Unit> =
        either {
            val connection = connection ?: raise(ConnectionLostException())

            when (message) {
                is Message.SendHeartbeatRequest -> sendHeartbeatRequest(connection).bind()

                is Message.SendStartTestRequest -> sendStartTestRequest(
                    connection = connection,
                    file = message.file
                ).bind()

                is Message.SendGetJobRequest -> sendGetJobRequest(connection, message.data).bind()
            }
        }

    private suspend fun sendHeartbeatRequest(
        connection: DeviceConnection
    ): Either<AppException, Unit> =
        either {
            val status = connection.api.getStatus().bind()

            val data = lastSentData
            if (data != null) {
                val job = status.jobs.firstOrNull { job -> job.id == data.jobId }

                if (job != null &&
                    (job.isSuccessfullyFinished() || job.isFailed())
                ) {
                    queue.add(Message.SendGetJobRequest(data = data))
                }
            }

            screenState = status.screen

            val newDeviceState = DeviceState.Connected(
                isConnected = true,
                isDriverReady = (status.driverStatus == DriverStatusDto.RUNNING)
            )

            if (currentDeviceState == newDeviceState && screenState != null) {
                rebuildViewState()
            }

            onDeviceStateChanged(newDeviceState)
        }

    private suspend fun sendStartTestRequest(
        connection: DeviceConnection,
        file: Path
    ): Either<AppException, StartTestResponse> =
        withContext(Dispatchers.IO) {
            either {
                lastSentData = null
                errorMessage = null

                val (content, flow) = interactor.readAndParseFile(file).bind()

                val response = interactor.sendStartTestRequest(
                    connection = connection,
                    file = file
                ).bind()

                if (response.isStarted) {
                    currentTestState = TestState.Running
                    lastSentData = TestData(
                        jobId = response.jobId ?: StringUtils.EMPTY,
                        file = file,
                        content = content,
                        flow = flow
                    )
                } else {
                    val encodedMessage = response.error?.base64Message ?: StringUtils.EMPTY

                    val message = if (encodedMessage.isNotEmpty()) {
                        Base64Utils.decode(encodedMessage)
                            .mapLeft { exception -> AppException(cause = exception) }
                            .bind()
                    } else {
                        StringUtils.EMPTY
                    }

                    currentTestState = TestState.Error(message)
                    errorMessage = message
                }

                if (currentFileState == FileState.QueuedForSending) {
                    currentFileState = FileState.Sent(Instant.now())
                }

                rebuildViewState()

                response
            }
        }

    private suspend fun sendGetJobRequest(
        connection: DeviceConnection,
        data: TestData
    ): Either<AppException, Unit> =
        either {
            val response = interactor.sendGetJobRequest(connection, data.jobId).bind()

            lastSentData = null

            val job = response.job

            val isSuccessfullyFinished = job.isSuccessfullyFinished()

            if (isSuccessfullyFinished) {
                currentTestState = TestState.Passed
                errorMessage = null
            } else {
                val failedStepDto = response.flow.steps.firstOrNull { stepRun ->
                    stepRun.result?.isSuccess == false
                }
                val failedStep = data.flow.steps.getOrNull(failedStepDto?.index ?: -1)

                if (failedStepDto != null) {
                    val error = failedStepDto.result?.errorMessage
                        ?.joinToString(separator = StringUtils.NEW_LINE)
                        ?: StringUtils.EMPTY
                    val stepIndex = failedStepDto.index + 1

                    val message = StringBuilder(
                        strings.stepFailedWithStr.format(stepIndex.toString())
                    )

                    if (failedStep != null) {
                        message.append(StringUtils.NEW_LINE).append(failedStep.describe())
                    }

                    if (error.isNotEmpty()) {
                        message.append(StringUtils.NEW_LINE).append(error)
                    }

                    errorMessage = message.toString()
                }

                currentTestState = TestState.Failed
            }

            onTestFinished(isSuccess = isSuccessfullyFinished)

            rebuildViewState()
        }

    private fun onFileChanged(file: Path) {
        if (!isReadyToStartTest() && currentFileState != FileState.QueuedForSending) {
            currentFileState = FileState.QueuedForSending
            return
        }

        if (currentFileState is FileState.Sent && isRunningTest()) {
            currentFileState = FileState.QueuedForSending
            return
        }

        fileDelayJob?.cancel()
        fileDelayJob = scope.launch {
            currentTestState = TestState.Sending
            currentFileState = FileState.QueuedForSending
            rebuildViewState()

            delay(1500L)

            queue.add(Message.SendStartTestRequest(file))
            fileDelayJob = null
        }
    }

    private fun onDeviceStateChanged(deviceState: DeviceState) {
        val wasReadyToStartTest = currentDeviceState.isReadyToStartTest()
        val isReadyToStartTest = deviceState.isReadyToStartTest()

        if (currentDeviceState != deviceState) {
            currentDeviceState = deviceState

            val fileState = currentFileState

            errorMessage = when {
                !isGatewayConnected() -> strings.gatewayIsNotConnectedMessage
                !isDriverReady() -> strings.driverIsNotRunningMessage
                fileState is FileState.InvalidFile -> fileState.message
                else -> errorMessage
            }

            if (wasReadyToStartTest != isReadyToStartTest) {
                queue.onStateChanged(isReadyToStartTest)
                onDeviceReadyToStartTest(isReadyToStartTest)
            }

            rebuildViewState()
        }
    }

    private fun onTestFinished(isSuccess: Boolean) {
        if (currentFileState == FileState.QueuedForSending) {
            if (isSuccess) {
                queue.add(Message.SendStartTestRequest(getFile()))
            } else {
                currentFileState = FileState.NoState
            }
        }
    }

    private fun onDeviceReadyToStartTest(isReadyToStartTest: Boolean) {
    }

    private fun Throwable.formatReadableErrorMessage(): String {
        val rootCause = this.getRootCause()

        val message = when {
            rootCause is FailedToFindDeviceException -> strings.deviceNotFoundMessage
            rootCause.isYamlParsingException() -> rootCause.message ?: StringUtils.EMPTY
            else -> this.stackTraceToString()
        }

        return message
    }

    private fun getFile(): Path {
        return Path(arguments.filePath)
    }

    private fun JobDto.isSuccessfullyFinished(): Boolean {
        return status == JobStatusDto.FINISHED && executionResult == ExecutionResultDto.SUCCESS
    }

    private fun JobDto.isFailed(): Boolean {
        return (status == JobStatusDto.FINISHED && executionResult == ExecutionResultDto.FAILED) ||
            status == JobStatusDto.CANCELLED
    }

    private fun isReadyToStartTest(): Boolean {
        return currentDeviceState.isReadyToStartTest()
    }

    private fun isRunningTest(): Boolean {
        return lastSentData != null
    }

    private fun Throwable.formatRootMessageOrStacktrace(): String {
        val rootMessage = this.getRootCause().message ?: StringUtils.EMPTY

        return if (rootMessage.isNotEmpty()) {
            rootMessage
        } else {
            this.stackTraceToString()
        }
    }

    private fun Throwable.isYamlParsingException(): Boolean {
        return this is ParsingException || this is JacksonException
    }

    private fun rebuildViewState() {
        viewState.value = buildViewState()
    }

    private fun isGatewayConnected(): Boolean {
        return (currentDeviceState as? DeviceState.Connected)?.isConnected ?: false
    }

    private fun isDriverReady(): Boolean {
        return (currentDeviceState as? DeviceState.Connected)?.isDriverReady ?: false
    }

    private fun isConnectedToDevice(): Boolean {
        return (currentDeviceState is DeviceState.Connected)
    }

    private fun buildViewState(): MainViewState {
        val isConnectedToDevice = isConnectedToDevice()
        val isGatewayConnected = isGatewayConnected()
        val isDriverReady = isDriverReady()

        val gatewayStatus = when {
            !isConnectedToDevice -> strings.connecting
            isGatewayConnected -> strings.connected.uppercase()
            else -> strings.disconnected.uppercase()
        }
        val gatewayColor = when {
            !isConnectedToDevice -> TextColor.DEFAULT
            isGatewayConnected -> TextColor.GREEN
            else -> TextColor.RED
        }

        val driverStatus = when {
            !isConnectedToDevice -> strings.connecting
            isDriverReady -> strings.running.uppercase()
            else -> strings.stopped.uppercase()
        }
        val driverColor = when {
            !isConnectedToDevice -> TextColor.DEFAULT
            isDriverReady -> TextColor.GREEN
            else -> TextColor.RED
        }

        val testStatusLabel = INDENT + strings.testStatus

        val testStatus = when (currentTestState) {
            TestState.Awaiting -> strings.awaiting
            TestState.Running -> strings.running
            TestState.Sending -> strings.sending
            TestState.Passed -> strings.passed
            TestState.Failed -> strings.failed
            is TestState.Error -> strings.failed
        }

        val testStatusColor = when (currentTestState) {
            TestState.Awaiting -> TextColor.DEFAULT
            TestState.Running -> TextColor.DEFAULT
            TestState.Sending -> TextColor.DEFAULT
            TestState.Passed -> TextColor.GREEN
            TestState.Failed -> TextColor.RED
            is TestState.Error -> TextColor.RED
        }

        val fileName = getFile().fileName.toString()
        val fileStatus = when (val state = currentFileState) {
            FileState.QueuedForSending -> strings.queued
            is FileState.Sent -> strings.sentAtWithStr.format(state.timestamp.toReadableString())
            else -> StringUtils.EMPTY
        }

        val screenState = this.screenState
        val screen = if (screenState != null) {
            val size = arguments.screenSize ?: ScreenSize.DEFAULT

            val formatter = AsciScreenNodeFormatter(
                screenPixelWidth = screenState.width,
                screenPixelHeight = screenState.height,
                screenCharWidth = size.width,
                screenCharHeight = size.height
            )

            screenState.uiTree
                .toUiNode { Unit }
                .format(formatter)
        } else {
            StringUtils.EMPTY
        }

        return MainViewState(
            gatewayStatus = gatewayStatus,
            gatewayColor = gatewayColor,
            driverStatus = driverStatus,
            driverColor = driverColor,
            fileName = fileName,
            fileStatus = fileStatus,
            testStatusLabel = testStatusLabel,
            testStatus = testStatus,
            testStatusColor = testStatusColor,
            errorMessage = errorMessage ?: StringUtils.EMPTY,
            screen = screen
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MainViewModel::class.java)
        private const val INDENT = "    "
        private const val MAX_RETRY_COUNT = 4
    }
}