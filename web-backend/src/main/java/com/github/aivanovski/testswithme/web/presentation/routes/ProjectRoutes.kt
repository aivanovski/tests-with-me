package com.github.aivanovski.testswithme.web.presentation.routes

import com.github.aivanovski.testswithme.web.api.Endpoints.PROJECT
import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.presentation.AUTH_PROVIDER
import com.github.aivanovski.testswithme.web.presentation.controller.ProjectController
import com.github.aivanovski.testswithme.web.presentation.handleResponseWithUser
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Routing.projectRoutes() {
    val authService: AuthService by lazy { get() }
    val projectController: ProjectController by lazy { get() }

    authenticate(AUTH_PROVIDER) {
        get("/$PROJECT") {
            handleResponseWithUser(authService, call) { user ->
                projectController
                    .getProjects(user)
            }
        }

        post("/$PROJECT") {
            handleResponseWithUser(authService, call) { user ->
                projectController.postProject(user, call.receive())
            }
        }
    }
}