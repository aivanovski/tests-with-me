package com.github.aivanovski.testswithme.android.domain.driverServer

import arrow.core.Either
import com.github.aivanovski.testswithme.android.di.GlobalInjector.get
import com.github.aivanovski.testswithme.android.domain.driverServer.controllers.StartTestController
import com.github.aivanovski.testswithme.android.domain.driverServer.controllers.StatusController
import com.github.aivanovski.testswithme.android.driverServerApi.DriverServerEndpoints.START_TEST
import com.github.aivanovski.testswithme.android.driverServerApi.DriverServerEndpoints.STATUS
import com.github.aivanovski.testswithme.android.driverServerApi.response.ErrorMessage
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.extensions.getRootCause
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.utils.Base64Utils
import com.github.aivanovski.testswithme.utils.StringUtils
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Application.configureRoutes() {
    val statusController: StatusController by lazy { get() }
    val testController: StartTestController by lazy { get() }

    routing {
        get("/$STATUS") {
            handleResponse(call) {
                statusController.getStatus()
            }
        }

        post("/$START_TEST") {
            handleResponse(call) {
                testController.startTest(call.receive())
            }
        }
    }
}

suspend inline fun <reified T : Any> handleResponse(
    call: ApplicationCall,
    noinline block: suspend () -> Either<AppException, T>
) {
    val response = withContext(Dispatchers.IO) {
        block.invoke()
    }

    if (response.isRight()) {
        call.respond(
            status = HttpStatusCode.OK,
            message = response.unwrap()
        )
    } else {
        val exception = response.unwrapError().getRootCause()

        val rootMessage = exception.message
        val message = when {
            !rootMessage.isNullOrEmpty() -> rootMessage
            else -> exception::class.simpleName ?: StringUtils.EMPTY
        }

        val error = ErrorMessage(
            base64Message = Base64Utils.encode(message)
        )

        call.respond(
            status = HttpStatusCode.BadRequest,
            message = error
        )
    }
}