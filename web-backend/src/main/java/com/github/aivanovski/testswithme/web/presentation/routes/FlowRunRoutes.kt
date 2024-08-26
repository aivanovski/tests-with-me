package com.github.aivanovski.testswithme.web.presentation.routes

import com.github.aivanovski.testswithme.web.api.Endpoints.FLOW_RUN
import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.presentation.AUTH_PROVIDER
import com.github.aivanovski.testswithme.web.presentation.controller.FlowRunController
import com.github.aivanovski.testswithme.web.presentation.handleResponseWithUser
import com.github.aivanovski.testswithme.web.presentation.routes.Api.ID
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Routing.flowRunRoutes() {
    val authService: AuthService by lazy { get() }
    val flowRunController: FlowRunController by lazy { get() }

    authenticate(AUTH_PROVIDER) {
        get("/$FLOW_RUN") {
            handleResponseWithUser(authService, call) { user ->
                flowRunController.getFlowRuns(user)
            }
        }

        get("/$FLOW_RUN/{$ID}") {
            handleResponseWithUser(authService, call) { user ->
                val uid = call.parameters[ID].orEmpty()
                flowRunController.getFlowRun(user, uid)
            }
        }

        post("/$FLOW_RUN") {
            handleResponseWithUser(authService, call) { user ->
                flowRunController.postFlowRun(user, call.receive())
            }
        }
    }
}