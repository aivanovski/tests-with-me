package com.github.aivanovski.testswithme.android.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.defaultComponentContext
import com.github.aivanovski.testswithme.android.di.GlobalInjector.inject
import com.github.aivanovski.testswithme.android.extensions.getParcelableCompat
import com.github.aivanovski.testswithme.android.presentation.core.ThemeProviderImpl
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ThemeProvider
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootScreen
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootScreenComponent

class MainActivity : ComponentActivity() {

    private val themeProvider: ThemeProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeProvider.onThemedContextCreated(this)

        val component = RootScreenComponent(
            componentContext = defaultComponentContext(),
            onExitNavigation = {
                finish()
            },
            args = getArguments()
        )

        setContent {
            val themeProvider = ThemeProviderImpl(LocalContext.current)

            AppTheme(theme = themeProvider.getCurrentTheme()) {
                RootScreen(
                    rootComponent = component
                )
            }
        }
    }

    private fun getArguments(): StartArgs {
        return intent.getParcelableCompat(ARGUMENTS, StartArgs::class.java)
            ?: StartArgs.EMPTY
    }

    companion object {

        private const val ARGUMENTS = "arguments"

        fun createStartIntent(
            context: Context,
            args: StartArgs
        ): Intent {
            return Intent(context, MainActivity::class.java)
                .apply {
                    putExtra(ARGUMENTS, args)
                }
        }
    }
}