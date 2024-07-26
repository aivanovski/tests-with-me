package com.github.aivanovski.testwithme.android.presentation.core.decompose

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.subscribe
import com.github.aivanovski.testwithme.android.presentation.core.BaseViewModel

fun Lifecycle.attach(viewModel: BaseViewModel) {
    this.subscribe(
        onStart = {
            viewModel.start()
        },
        onDestroy = {
            viewModel.destroy()
        }
    )
}