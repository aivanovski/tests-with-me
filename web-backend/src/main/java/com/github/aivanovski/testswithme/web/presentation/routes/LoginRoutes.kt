package com.github.aivanovski.testswithme.web.presentation.routes

import com.github.aivanovski.testswithme.web.api.Endpoints.LOGIN
import com.github.aivanovski.testswithme.web.api.Endpoints.SIGN_UP
import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.presentation.controller.LoginController
import com.github.aivanovski.testswithme.web.presentation.controller.SignUpController
import com.github.aivanovski.testswithme.web.presentation.handleResponse
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post

fun Routing.loginRoutes() {
    val signUpController: SignUpController by lazy { get() }
    val loginController: LoginController by lazy { get() }

    post(LOGIN) {
        handleResponse(call) {
            loginController.login(call.request.headers, call.receive())
        }
    }

    post(SIGN_UP) {
        handleResponse(call) {
            signUpController.createNewUser(call.receive())
        }
    }
}