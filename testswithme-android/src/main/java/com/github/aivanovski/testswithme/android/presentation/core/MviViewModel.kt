package com.github.aivanovski.testswithme.android.presentation.core

import androidx.annotation.CallSuper
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class MviViewModel<State, Intent>(
    initialState: State,
    private val initialIntent: Intent
) : BaseViewModel() {

    val state = MutableStateFlow(initialState)
    private val intents = Channel<Intent>(capacity = 16)

    @OptIn(ExperimentalCoroutinesApi::class)
    @CallSuper
    override fun start() {
        super.start()

        doOnceWhenStarted {
            viewModelScope.launch {
                intents.receiveAsFlow()
                    .onStart { emit(initialIntent) }
                    .flatMapLatest { intent -> handleIntent(intent) }
                    .flowOn(Dispatchers.IO)
                    .collect { newState ->
                        state.value = newState
                    }
            }
        }
    }

    fun sendIntent(intent: Intent) {
        viewModelScope.launch {
            intents.send(intent)
        }
    }

    abstract fun handleIntent(intent: Intent): Flow<State>
}