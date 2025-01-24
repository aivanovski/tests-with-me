package com.github.aivanovski.testswithme.web.presentation.routes

import com.github.aivanovski.testswithme.web.api.Endpoints.DEVICE
import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.presentation.AUTH_PROVIDER
import com.github.aivanovski.testswithme.web.presentation.controller.DeviceController
import com.github.aivanovski.testswithme.web.presentation.handleResponseWithUser
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get

fun Routing.deviceRoutes() {
    val authService: AuthService by lazy { get() }
    val deviceController: DeviceController by lazy { get() }

    authenticate(AUTH_PROVIDER) {
        get("/$DEVICE") {
            handleResponseWithUser(authService, call) { user ->
                deviceController.getDevices(user)
            }
        }
    }
}