package com.github.aivanovski.testswithme.android.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.defaultComponentContext
import com.github.aivanovski.testswithme.android.di.GlobalInjector.inject
import com.github.aivanovski.testswithme.android.extensions.getParcelableCompat
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ThemeProvider
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootScreen
import com.github.aivanovski.testswithme.android.presentation.screens.root.RootScreenComponent

class MainActivity : AppCompatActivity() {

    private val themeProvider: ThemeProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeProvider.onThemedContextCreated(this)

        val component = RootScreenComponent(
            componentContext = defaultComponentContext(),
            fragmentManager = supportFragmentManager,
            onExitNavigation = {
                finish()
            },
            args = getArguments()
        )

        setContent {
            AppTheme(theme = themeProvider.theme) {
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