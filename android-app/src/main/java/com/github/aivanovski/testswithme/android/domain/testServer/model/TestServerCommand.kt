package com.github.aivanovski.testswithme.android.domain.testServer.model

sealed interface TestServerCommand {

    data object Start : TestServerCommand
    data object Stop : TestServerCommand
}