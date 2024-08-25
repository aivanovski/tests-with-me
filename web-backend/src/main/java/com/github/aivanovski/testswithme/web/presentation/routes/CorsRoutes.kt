package com.github.aivanovski.testswithme.web.presentation.routes

import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.presentation.controller.CORSController
import io.ktor.server.application.call
import io.ktor.server.routing.Routing
import io.ktor.server.routing.options

fun Routing.corsRoutes() {
    val corsController: CORSController by lazy { get() }

    options("/*") {
        corsController.handleCorsOptionsCall(call)
    }
}