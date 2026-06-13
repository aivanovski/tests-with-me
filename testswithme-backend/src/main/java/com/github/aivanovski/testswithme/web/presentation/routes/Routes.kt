package com.github.aivanovski.testswithme.web.presentation.routes

import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRoutes() {
    routing {
        loginRoutes()
        userRoutes()
        projectRoutes()
        groupRoutes()
        flowRoutes()
        flowRunRoutes()
    }
}
