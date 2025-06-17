package com.github.aivanovski.testswithme.android.presentation.screens.sandbox

import com.github.aivanovski.testswithme.android.presentation.core.MviViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.sandbox.model.SandboxIntent
import com.github.aivanovski.testswithme.android.presentation.screens.sandbox.model.SandboxState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SandboxViewModel : MviViewModel<SandboxState, SandboxIntent>(
    initialState = SandboxState.Loading,
    initialIntent = SandboxIntent.Initialize
) {

    override fun handleIntent(intent: SandboxIntent): Flow<SandboxState> {
        return when (intent) {
            SandboxIntent.Initialize -> loadData()
        }
    }

    private fun loadData(): Flow<SandboxState> {
        return flow {
            emit(SandboxState.Loading)

            delay(1000L)

            emit(SandboxState.Data(emptyList()))
        }
    }
}