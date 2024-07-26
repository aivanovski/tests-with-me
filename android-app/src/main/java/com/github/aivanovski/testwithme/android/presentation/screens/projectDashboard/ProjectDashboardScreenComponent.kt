package com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.decompose.ComponentContext
import com.github.aivanovski.testwithme.android.presentation.core.ViewModelFactory
import com.github.aivanovski.testwithme.android.presentation.core.decompose.ViewModelStoreOwnerImpl
import com.github.aivanovski.testwithme.android.presentation.core.decompose.attach
import com.github.aivanovski.testwithme.android.presentation.core.navigation.Router
import com.github.aivanovski.testwithme.android.presentation.core.navigation.ScreenComponent
import com.github.aivanovski.testwithme.android.presentation.screens.projectDashboard.model.ProjectDashboardScreenArgs
import com.github.aivanovski.testwithme.android.presentation.screens.root.RootViewModel

class ProjectDashboardScreenComponent(
    context: ComponentContext,
    rootViewModel: RootViewModel,
    router: Router,
    args: ProjectDashboardScreenArgs
) : ScreenComponent,
    ComponentContext by context,
    ViewModelStoreOwner by ViewModelStoreOwnerImpl() {

    private val viewModel: ProjectDashboardViewModel by lazy {
        ViewModelProvider(
            owner = this,
            factory = ViewModelFactory(rootViewModel, router, args)
        )[ProjectDashboardViewModel::class]
    }

    init {
        // TODO: refactor, lazy viewmodel is not necessary in this case
        lifecycle.attach(viewModel)
    }

    @Composable
    override fun render() {
        ProjectDashboardScreen(viewModel)
    }
}