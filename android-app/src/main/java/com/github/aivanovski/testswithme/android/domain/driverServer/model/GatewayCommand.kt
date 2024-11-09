package com.github.aivanovski.testswithme.android.domain.driverServer.model

sealed interface GatewayCommand {

    data object Start : GatewayCommand
    data object Stop : GatewayCommand
}