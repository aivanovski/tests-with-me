package com.github.aivanovski.testswithme.android.presentation.screens.sandbox

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aivanovski.testswithme.android.presentation.core.compose.CenteredBox
import com.github.aivanovski.testswithme.android.presentation.core.compose.ProgressIndicator
import com.github.aivanovski.testswithme.android.presentation.screens.sandbox.model.SandboxIntent
import com.github.aivanovski.testswithme.android.presentation.screens.sandbox.model.SandboxState

@Composable
fun SandboxScreen(viewModel: SandboxViewModel) {
    val state by viewModel.state.collectAsState()

    SandboxScreen(
        state = state,
        onIntent = viewModel::sendIntent
    )
}

@Composable
fun SandboxScreen(
    state: SandboxState,
    onIntent: (intent: SandboxIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (state) {
            SandboxState.Loading -> {
                CenteredBox {
                    ProgressIndicator()
                }
            }

            is SandboxState.Data -> {

                CenteredBox {
                    Text("data")
                }
            }
        }
    }
}

@Composable
@Preview
fun SandboxScreenPreview() {

}