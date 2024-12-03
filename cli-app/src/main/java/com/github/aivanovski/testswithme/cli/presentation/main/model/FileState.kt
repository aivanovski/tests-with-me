package com.github.aivanovski.testswithme.cli.presentation.main.model

import java.time.Instant

sealed interface FileState {
    data object NoState : FileState
    data object QueuedForSending : FileState
    data class Sent(val timestamp: Instant) : FileState
    data class InvalidFile(val message: String) : FileState
}