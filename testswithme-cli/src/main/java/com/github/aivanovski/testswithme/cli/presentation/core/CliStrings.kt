package com.github.aivanovski.testswithme.cli.presentation.core

interface CliStrings {
    val connecting: String
    val awaiting: String
    val connected: String
    val disconnected: String
    val running: String
    val stopped: String
    val failed: String
    val passed: String
    val testStatus: String
    val strWithDots: String
    val sending: String
    val awaitingTestDriverMessage: String
    val awaitingDriverGatewayMessage: String
    val driverGatewayWithStr: String
    val testDriverWithStr: String
    val fileWithStr: String
    val queued: String
    val sentAtWithStr: String
    val stepFailedWithStr: String

    // Errors
    val gatewayIsNotConnectedMessage: String
    val driverIsNotRunningMessage: String
    val deviceNotFoundMessage: String
}