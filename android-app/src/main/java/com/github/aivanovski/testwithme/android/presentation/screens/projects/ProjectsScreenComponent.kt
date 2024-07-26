package com.github.aivanovski.testwithme.android.presentation.screens.projects

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.decompose.ComponentContext
import com.github.aivanovski.testwithme.android.presentation.core.decompose.ViewModelStoreOwnerImpl
import com.github.aivanovski.testwithme.android.presentation.core.ViewModelFactory
import com.github.aivanovski.testwithme.android.presentation.core.decompose.attach
import com.github.aivanovski.testwithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testwithme.android.presentation.core.navigation.ScreenComponent
import com.github.aivanovski.testwithme.android.presentation.screens.root.RootViewModel

class ProjectsScreenComponent(
    context: ComponentContext,
    rootViewModel: RootViewModel,
    router: Router
) : ScreenComponent,
    ComponentContext by context,
    ViewModelStoreOwner by ViewModelStoreOwnerImpl() {

    private val viewModel: ProjectsViewModel by lazy {
        ViewModelProvider(
            owner = this,
            factory = ViewModelFactory(rootViewModel, router)
        )[ProjectsViewModel::class]
    }

    init {
        lifecycle.attach(viewModel)
    }

    @Composable
    override fun render() {
        ProjectsScreen(viewModel)
    }
}