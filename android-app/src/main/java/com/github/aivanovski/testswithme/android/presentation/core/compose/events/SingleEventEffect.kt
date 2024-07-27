package com.github.aivanovski.testswithme.android.presentation.core.compose.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> SingleEventEffect(
    eventFlow: Flow<T>,
    lifeCycleState: Lifecycle.State = Lifecycle.State.STARTED,
    collector: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(eventFlow) {
        lifecycleOwner.repeatOnLifecycle(lifeCycleState) {
            eventFlow.collect(collector)
        }
    }
}