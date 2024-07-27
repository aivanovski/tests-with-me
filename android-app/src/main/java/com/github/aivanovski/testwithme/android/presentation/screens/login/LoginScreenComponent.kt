package com.github.aivanovski.testwithme.android.presentation.screens.login

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.decompose.ComponentContext
import com.github.aivanovski.testwithme.android.presentation.core.ViewModelFactory
import com.github.aivanovski.testwithme.android.presentation.core.decompose.ViewModelStoreOwnerImpl
import com.github.aivanovski.testwithme.android.presentation.core.decompose.attach
import com.github.aivanovski.testwithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testwithme.android.presentation.core.navigation.ScreenComponent
import com.github.aivanovski.testwithme.android.presentation.screens.root.RootViewModel

class LoginScreenComponent(
    context: ComponentContext,
    private val rootViewModel: RootViewModel,
    private val router: Router
) : ScreenComponent,
    ComponentContext by context,
    ViewModelStoreOwner by ViewModelStoreOwnerImpl() {

    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(
            owner = this,
            factory = ViewModelFactory(rootViewModel, router)
        )[LoginViewModel::class]
    }

    init {
        lifecycle.attach(viewModel)
    }

    @Composable
    override fun render() {
        LoginScreen(viewModel)
    }
}