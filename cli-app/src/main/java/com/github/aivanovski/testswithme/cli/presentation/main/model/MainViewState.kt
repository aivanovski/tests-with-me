package com.github.aivanovski.testswithme.cli.presentation.main.model

import com.github.aivanovski.testswithme.utils.StringUtils.EMPTY

data class MainViewState(
    val gatewayStatus: String = EMPTY,
    val gatewayColor: TextColor = TextColor.DEFAULT,
    val driverStatus: String = EMPTY,
    val driverColor: TextColor = TextColor.DEFAULT,
    val fileName: String = EMPTY,
    val fileStatus: String = EMPTY,
    val helpText: String = EMPTY,
    val testStatusLabel: String = EMPTY,
    val testStatus: String = EMPTY,
    val testStatusColor: TextColor = TextColor.DEFAULT,
    val errorMessage: String = EMPTY
)