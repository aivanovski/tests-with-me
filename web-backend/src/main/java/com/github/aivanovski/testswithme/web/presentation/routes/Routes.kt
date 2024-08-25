package com.github.aivanovski.testswithme.web.presentation.routes

import arrow.core.Either
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.web.api.Endpoints.FLOW
import com.github.aivanovski.testswithme.web.api.Endpoints.FLOW_RUN
import com.github.aivanovski.testswithme.web.api.Endpoints.GROUP
import com.github.aivanovski.testswithme.web.api.Endpoints.LOGIN
import com.github.aivanovski.testswithme.web.api.Endpoints.PROJECT
import com.github.aivanovski.testswithme.web.api.Endpoints.SIGN_UP
import com.github.aivanovski.testswithme.web.api.Endpoints.USER
import com.github.aivanovski.testswithme.web.api.response.ErrorMessage
import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.entity.ErrorResponse
import com.github.aivanovski.testswithme.web.entity.User
import com.github.aivanovski.testswithme.web.extensions.transformError
import com.github.aivanovski.testswithme.web.presentation.AUTH_PROVIDER
import com.github.aivanovski.testswithme.web.presentation.Errors.ERROR_HAS_BEEN_OCCURRED
import com.github.aivanovski.testswithme.web.presentation.Errors.INVALID_OR_EXPIRED_TOKEN
import com.github.aivanovski.testswithme.web.presentation.controller.CORSController
import com.github.aivanovski.testswithme.web.presentation.controller.FlowController
import com.github.aivanovski.testswithme.web.presentation.controller.FlowRunController
import com.github.aivanovski.testswithme.web.presentation.controller.GroupController
import com.github.aivanovski.testswithme.web.presentation.controller.LoginController
import com.github.aivanovski.testswithme.web.presentation.controller.ProjectController
import com.github.aivanovski.testswithme.web.presentation.controller.SignUpController
import com.github.aivanovski.testswithme.web.presentation.controller.UserController
import com.github.aivanovski.testswithme.web.presentation.routes.Api.ID
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.options
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("Routes")

fun Application.configureRouting() {
    val authService: AuthService by lazy { get() }
    val signUpController: SignUpController by lazy { get() }
    val loginController: LoginController by lazy { get() }
    val flowController: FlowController by lazy { get() }
    val projectController: ProjectController by lazy { get() }
    val flowRunController: FlowRunController by lazy { get() }
    val userController: UserController by lazy { get() }
    val groupController: GroupController by lazy { get() }
    val corsController: CORSController by lazy { get() }

    routing {
        options("/*") {
            corsController.handleCorsOptionsCall(call)
        }

        post(LOGIN) {
            handle(call) {
                loginController
                    .login(call, call.receive())
                    .transformError()
            }
        }

        post(SIGN_UP) {
            handle(call) {
                signUpController
                    .createNewUser(call.receive())
                    .transformError()
            }
        }

        authenticate(AUTH_PROVIDER) {
            get("/$FLOW") {
                handleAuthenticated(authService, call) { user ->
                    flowController
                        .getFlows(user)
                        .transformError()
                }
            }

            get("/$FLOW/{$ID}") {
                handleAuthenticated(authService, call) { user ->
                    val uid = call.parameters[ID].orEmpty()
                    flowController
                        .getFlow(user, uid)
                        .transformError()
                }
            }

            post("/$FLOW") {
                handleAuthenticated(authService, call) { user ->
                    flowController
                        .postFlow(user, call.receive())
                        .transformError()
                }
            }
        }

        authenticate(AUTH_PROVIDER) {
            get("/$PROJECT") {
                handleAuthenticated(authService, call) { user ->
                    projectController
                        .getProjects(user)
                        .transformError()
                }
            }

            post("/$PROJECT") {
                handleAuthenticated(authService, call) { user ->
                    projectController
                        .postProject(user, call.receive())
                        .transformError()
                }
            }
        }

        authenticate(AUTH_PROVIDER) {
            get("/$FLOW_RUN") {
                handleAuthenticated(authService, call) { user ->
                    flowRunController
                        .getFlowRuns(user)
                        .transformError()
                }
            }

            get("/$FLOW_RUN/{$ID}") {
                handleAuthenticated(authService, call) { user ->
                    val uid = call.parameters[ID].orEmpty()
                    flowRunController
                        .getFlowRun(user, uid)
                        .transformError()
                }
            }

            post("/$FLOW_RUN") {
                handleAuthenticated(authService, call) { user ->
                    flowRunController
                        .postFlowRun(user, call.receive())
                        .transformError()
                }
            }
        }

        authenticate(AUTH_PROVIDER) {
            get("/$USER") {
                handleAuthenticated(authService, call) {
                    userController.getUsers()
                }
            }
        }

        authenticate(AUTH_PROVIDER) {
            get("/$GROUP") {
                handleAuthenticated(authService, call) { user ->
                    groupController
                        .getGroups(user)
                        .transformError()
                }
            }

            post("/$GROUP") {
                handleAuthenticated(authService, call) { user ->
                    groupController
                        .addGroup(user, call.receive())
                        .transformError()
                }
            }

            put("/$GROUP/{$ID}") {
                handleAuthenticated(authService, call) { user ->
                    val uid = call.parameters[ID].orEmpty()
                    groupController
                        .updateGroup(user, uid, call.receive())
                        .transformError()
                }
            }
        }
    }
}

suspend inline fun <reified T : Any> handleAuthenticated(
    authService: AuthService,
    call: ApplicationCall,
    block: (user: User) -> Either<ErrorResponse, T>
) {
    val principal = call.principal<JWTPrincipal>()
    if (principal == null) {
        call.respond(
            status = HttpStatusCode.Unauthorized,
            message = ErrorMessage(message = INVALID_OR_EXPIRED_TOKEN)
        )
        return
    }

    val isValidTokenResult = authService.validateToken(principal)
    if (isValidTokenResult.isLeft()) {
        call.sendResponse(isValidTokenResult.unwrapError())
        return
    }

    val user = isValidTokenResult.unwrap()

    val response = block.invoke(user)
    call.sendResponse(response)
}

suspend inline fun <reified T : Any> handle(
    call: ApplicationCall,
    block: () -> Either<ErrorResponse, T>
) {
    val response = block.invoke()

    val origin = call.request.headers[HttpHeaders.Origin]
    if (!origin.isNullOrBlank()) {
        call.response.headers.apply {
            append(HttpHeaders.AccessControlAllowOrigin, origin)
            append(HttpHeaders.AccessControlAllowCredentials, "true")
            append(HttpHeaders.AccessControlExposeHeaders, HttpHeaders.AccessControlAllowOrigin)
        }
    }

    call.sendResponse(response)
}

suspend inline fun <reified T : Any> ApplicationCall.sendResponse(
    response: Either<ErrorResponse, T>
) {
    logger.debug("Request: {} isSuccess={}", request.uri, response.isRight())

    if (response.isRight()) {
        respond(
            status = HttpStatusCode.OK,
            message = response.unwrap()
        )
    } else {
        sendResponse(response.unwrapError())
    }
}

suspend fun ApplicationCall.sendResponse(error: ErrorResponse) {
    logger.error("Response error: status={}, message={}", error.status, error.message)

    error.exception.printStackTrace()

    respond(
        status = error.status,
        message = error.toErrorMessage()
    )
}

private fun ErrorResponse.toErrorMessage(): ErrorMessage {
    return ErrorMessage(
        message = message ?: ERROR_HAS_BEEN_OCCURRED
    )
}