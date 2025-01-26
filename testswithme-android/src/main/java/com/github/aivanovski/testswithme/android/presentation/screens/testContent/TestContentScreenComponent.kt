package com.github.aivanovski.testswithme.android.presentation.screens.testContent

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
import com.github.aivanovski.testswithme.android.presentation.screens.testContent.model.TestContentArgs

class TestContentScreenComponent(
    context: ComponentContext,
    rootViewModel: RootViewModel,
    router: Router,
    args: TestContentArgs
) : ScreenComponent,
    ComponentContext by context,
    ViewModelStoreOwner by ViewModelStoreOwnerImpl() {

    private val viewModel: TestContentViewModel by lazy {
        ViewModelProvider(
            owner = this,
            factory = ViewModelFactory(rootViewModel, router, args)
        )[TestContentViewModel::class.java]
    }

    init {
        lifecycle.attach(viewModel)
    }

    @Composable
    override fun render() {
        TestContentScreen(viewModel)
    }
}