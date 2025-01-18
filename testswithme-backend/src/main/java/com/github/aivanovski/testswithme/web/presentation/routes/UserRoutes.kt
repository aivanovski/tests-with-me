package com.github.aivanovski.testswithme.web.presentation.routes

import com.github.aivanovski.testswithme.web.api.Endpoints.USER
import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.presentation.AUTH_PROVIDER
import com.github.aivanovski.testswithme.web.presentation.controller.UserController
import com.github.aivanovski.testswithme.web.presentation.handleResponseWithUser
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.userRoutes() {
    val authService: AuthService by lazy { get() }
    val userController: UserController by lazy { get() }

    authenticate(AUTH_PROVIDER) {
        get("/$USER") {
            handleResponseWithUser(authService, call) {
                userController.getUsers()
            }
        }
    }
}