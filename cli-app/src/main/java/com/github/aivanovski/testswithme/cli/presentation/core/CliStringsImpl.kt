package com.github.aivanovski.testswithme.cli.presentation.core

class CliStringsImpl : CliStrings {
    override val connecting: String = "Connecting"
    override val awaiting: String = "Awaiting"
    override val connected: String = "Connected"
    override val disconnected: String = "Disconnected"
    override val running: String = "Running"
    override val stopped: String = "Stopped"
    override val failed: String = "Failed"
    override val passed: String = "Passed"
    override val testStatus: String = "Test Status:"
    override val strWithDots: String = "%s..."
    override val sending: String = "Sending"
    override val awaitingTestDriverMessage: String = "Awaiting Test Driver becoming active"
    override val awaitingDriverGatewayMessage: String = "Awaiting connection to Driver Gateway"
    override val driverGatewayWithStr: String = "Driver Gateway: %s"
    override val testDriverWithStr: String = "Test Driver: %s"
    override val fileWithStr: String = "File: %s"
    override val queued: String = "queued"
    override val sentAtWithStr: String = "sent at %s"
    override val stepFailedWithStr: String = "Step at index %s is failed:"

    override val gatewayIsNotConnectedMessage: String = """
        ERROR: Unable to connect to Driver Gateway server.
        Please ensure that:
        1. The device is connected via cable
        2. Driver Gateway is running in the application.
           It can be done with following steps:
             - Open TestsWithMe application
             - Go to Settings
             - Enable 'Driver Gateway' toggle
    """.trimIndent()

    override val driverIsNotRunningMessage: String = """
        ERROR: Test Driver is not running.
        It should be enabled in the Accessibility settings of device.
        It can be done with following steps:
          - Open TestsWithMe application
          - Go to Settings
          - In the section 'Test Driver', click on Settings
          - Find the 'Test Driver' in the screen and enable it
    """.trimIndent()

    override val deviceNotFoundMessage: String = """
        Unable to find connected device.
        Please ensure Android Device is connected via cable
    """.trimIndent()
}