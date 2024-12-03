package com.github.aivanovski.testswithme.cli.presentation.main.model

import java.nio.file.Path

sealed interface MessageImpl {
    data object SendHeartbeatRequest : MessageImpl
    data class SendStartTestRequest(val file: Path) : MessageImpl
    data class SendGetJobRequest(val jobId: String) : MessageImpl
}