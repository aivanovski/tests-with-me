package com.github.aivanovski.testswithme.cli.presentation.main

import com.github.aivanovski.testswithme.cli.presentation.main.command.LoopMessage
import com.github.aivanovski.testswithme.cli.presentation.main.model.TestData
import java.nio.file.Path

sealed class Message(
    val isRequireActiveState: Boolean
) : LoopMessage {

    data object SendHeartbeatRequest : Message(isRequireActiveState = false)

    data class SendStartTestRequest(
        val file: Path
    ) : Message(isRequireActiveState = true)

    data class SendGetJobRequest(
        val data: TestData
    ) : Message(isRequireActiveState = true)
}