package com.github.aivanovski.testswithme.web.presentation.routes

import com.github.aivanovski.testswithme.web.api.Endpoints.GROUP
import com.github.aivanovski.testswithme.web.di.GlobalInjector.get
import com.github.aivanovski.testswithme.web.domain.service.AuthService
import com.github.aivanovski.testswithme.web.presentation.AUTH_PROVIDER
import com.github.aivanovski.testswithme.web.presentation.controller.GroupController
import com.github.aivanovski.testswithme.web.presentation.handleResponseWithUser
import com.github.aivanovski.testswithme.web.presentation.routes.Api.ID
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Routing.groupRoutes() {
    val authService: AuthService by lazy { get() }
    val groupController: GroupController by lazy { get() }

    authenticate(AUTH_PROVIDER) {
        get("/$GROUP") {
            handleResponseWithUser(authService, call) { user ->
                groupController.getGroups(user)
            }
        }

        post("/$GROUP") {
            handleResponseWithUser(authService, call) { user ->
                groupController.addGroup(user, call.receive())
            }
        }

        put("/$GROUP/{$ID}") {
            handleResponseWithUser(authService, call) { user ->
                val uid = call.parameters[ID].orEmpty()
                groupController.updateGroup(user, uid, call.receive())
            }
        }
    }
}