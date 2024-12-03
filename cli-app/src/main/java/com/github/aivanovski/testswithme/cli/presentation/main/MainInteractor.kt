package com.github.aivanovski.testswithme.cli.presentation.main

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.driverServerApi.request.StartTestRequest
import com.github.aivanovski.testswithme.android.driverServerApi.response.GetJobResponse
import com.github.aivanovski.testswithme.android.driverServerApi.response.StartTestResponse
import com.github.aivanovski.testswithme.cli.data.device.DeviceConnection
import com.github.aivanovski.testswithme.cli.data.file.FileSystemProvider
import com.github.aivanovski.testswithme.cli.domain.usecases.ConnectToDeviceUseCase
import com.github.aivanovski.testswithme.cli.domain.usecases.FormatHelpTextUseCase
import com.github.aivanovski.testswithme.cli.entity.ConnectionAndState
import com.github.aivanovski.testswithme.cli.entity.exception.AppException
import com.github.aivanovski.testswithme.cli.entity.exception.DeviceConnectionException
import com.github.aivanovski.testswithme.cli.entity.exception.ParsingException
import com.github.aivanovski.testswithme.entity.YamlFlow
import com.github.aivanovski.testswithme.flow.yaml.YamlParser
import com.github.aivanovski.testswithme.utils.Base64Utils
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainInteractor(
    private val formatHelpUseCase: FormatHelpTextUseCase,
    private val connectUseCase: ConnectToDeviceUseCase,
    private val fsProvider: FileSystemProvider
) {

    fun getHelpText(): String = formatHelpUseCase.formatHelpText()

    suspend fun connectToDevice(): Either<DeviceConnectionException, ConnectionAndState> =
        connectUseCase.connectToDevice()

    suspend fun readAndParseFile(file: Path): Either<AppException, Pair<String, YamlFlow>> =
        withContext(Dispatchers.IO) {
            either {
                val content = fsProvider.read(file.pathString)
                    .mapLeft { exception -> AppException(cause = exception) }
                    .bind()

                val flow = YamlParser().parse(content)
                    .mapLeft { exception -> ParsingException(cause = exception) }
                    .bind()

                (content to flow)
            }
        }

    suspend fun sendGetJobRequest(
        connection: DeviceConnection,
        jobId: String
    ): Either<AppException, GetJobResponse> =
        withContext(Dispatchers.IO) {
            either {
                connection.api.getJob(jobId = jobId)
                    .bind()
            }
        }

    suspend fun sendStartTestRequest(
        connection: DeviceConnection,
        fileName: String,
        content: String
    ): Either<AppException, StartTestResponse> =
        withContext(Dispatchers.IO) {
            either {
                val base64Content = Base64Utils.encode(content)

                connection.api.startTest(
                    request = StartTestRequest(
                        name = fileName,
                        base64Content = base64Content
                    )
                )
                    .mapLeft { exception -> AppException(cause = exception) }
                    .bind()
            }
        }
}