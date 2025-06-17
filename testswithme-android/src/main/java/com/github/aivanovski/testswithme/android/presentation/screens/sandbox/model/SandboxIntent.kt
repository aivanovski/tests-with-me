package com.github.aivanovski.testswithme.android.presentation.screens.sandbox.model

sealed interface SandboxIntent {

    data object Initialize : SandboxIntent
}