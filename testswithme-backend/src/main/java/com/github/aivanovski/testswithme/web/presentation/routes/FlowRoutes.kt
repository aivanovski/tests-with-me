package com.github.aivanovski.testswithme.web.presentation.routes

import com.github.aivanovski.testswithme.web.api.Endpoints.FLOW
import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.presentation.AUTH_PROVIDER
import com.github.aivanovski.testswithme.web.presentation.controller.FlowController
import com.github.aivanovski.testswithme.web.presentation.handleResponseWithUser
import com.github.aivanovski.testswithme.web.presentation.routes.Api.ID
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Routing.flowRoutes() {
    val authService: AuthService by lazy { get() }
    val flowController: FlowController by lazy { get() }

    authenticate(AUTH_PROVIDER) {
        get("/$FLOW") {
            handleResponseWithUser(authService, call) { user ->
                flowController.getFlows(user)
            }
        }

        get("/$FLOW/{$ID}") {
            handleResponseWithUser(authService, call) { user ->
                val uid = call.parameters[ID].orEmpty()
                flowController.getFlow(user, uid)
            }
        }

        post("/$FLOW") {
            handleResponseWithUser(authService, call) { user ->
                flowController.postFlow(user, call.receive())
            }
        }

        put("/$FLOW/{$ID}") {
            handleResponseWithUser(authService, call) { user ->
                val uid = call.parameters[ID].orEmpty()
                flowController.updateFlow(user, uid, call.receive())
            }
        }

        delete("/$FLOW/{$ID}") {
            handleResponseWithUser(authService, call) { user ->
                val uid = call.parameters[ID].orEmpty()
                flowController.deleteFLow(user, uid)
            }
        }
    }
}