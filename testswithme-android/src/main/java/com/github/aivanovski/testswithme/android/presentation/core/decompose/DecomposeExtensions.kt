package com.github.aivanovski.testswithme.android.presentation.core.decompose

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.subscribe
import com.github.aivanovski.testswithme.android.presentation.core.ScreenViewModel

fun Lifecycle.attach(viewModel: ScreenViewModel) {
    this.subscribe(
        onStart = {
            viewModel.start()
        },
        onDestroy = {
            viewModel.destroy()
        }
    )
}