package com.github.aivanovski.testswithme.android.presentation.screens.uploadTest

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.decompose.ComponentContext
import com.github.aivanovski.testswithme.android.presentation.core.ViewModelFactory
import com.github.aivanovski.testswithme.android.presentation.core.decompose.ViewModelStoreOwnerImpl
import com.github.aivanovski.testswithme.android.presentation.core.decompose.attach
import com.github.aivanovski.testswithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testswithme.android.presentation.core.navigation.ScreenComponent
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootViewModel
import com.github.aivanovski.testswithme.android.presentation.screens.uploadTest.model.UploadTestScreenArgs

class UploadTestScreenComponent(
    context: ComponentContext,
    rootViewModel: RootViewModel,
    router: Router,
    args: UploadTestScreenArgs
) : ScreenComponent,
    ComponentContext by context,
    ViewModelStoreOwner by ViewModelStoreOwnerImpl() {

    private val viewModel: UploadTestViewModel by lazy {
        ViewModelProvider(
            owner = this,
            factory = ViewModelFactory(rootViewModel, router, args)
        )[UploadTestViewModel::class]
    }

    init {
        lifecycle.attach(viewModel)
    }

    @Composable
    override fun render() {
        UploadTestScreen(viewModel)
    }
}