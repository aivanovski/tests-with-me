package com.github.aivanovski.testswithme.cli.presentation.main

import com.github.aivanovski.testswithme.cli.presentation.core.CliStrings
import com.github.aivanovski.testswithme.cli.presentation.main.model.MainViewState
import com.github.aivanovski.testswithme.cli.presentation.main.model.TextColor
import com.github.aivanovski.testswithme.extensions.splitIntoLines
import com.github.aivanovski.testswithme.utils.StringUtils.EMPTY
import com.github.aivanovski.testswithme.utils.StringUtils.SPACE
import com.github.aivanovski.testswithme.utils.mutableStateFlow
import com.github.ajalt.mordant.rendering.TextColors.brightGreen
import com.github.ajalt.mordant.rendering.TextColors.brightRed
import com.github.ajalt.mordant.terminal.Terminal
import java.lang.StringBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class MainView(
    private val strings: CliStrings
) {

    private val terminal = Terminal()
    private val scope = CoroutineScope(Dispatchers.Default)
    private var collectJob: Job? = null

    private var lastRenderedState: MainViewState? by mutableStateFlow(null)
    private var viewModel: MainViewModel? by mutableStateFlow(null)

    fun bind(viewModel: MainViewModel) {
        this.viewModel = viewModel

        collectJob = scope.launch {
            viewModel.viewState.collectLatest { state ->
                renderState(state)
            }
        }
    }

    fun unbind() {
        ensureLatestStateWasDrawn()

        collectJob?.cancel()
        collectJob = null
        viewModel = null
    }

    private fun ensureLatestStateWasDrawn() {
        val viewModel = viewModel ?: return

        runBlocking {
            val state = viewModel.viewState.value

            while (lastRenderedState == null || state != lastRenderedState) {
                delay(10)
            }
        }
    }

    private fun renderState(state: MainViewState) {
        terminal.cursor.move {
            clearScreen()
            setPosition(0, 0)
        }

        val lines = state.formatLines()
        for (line in lines) {
            terminal.println(line)
        }

        lastRenderedState = state
    }

    private fun MainViewState.formatLines(): List<String> {
        val lines = mutableListOf<String>()

        if (gatewayStatus.isNotEmpty()) {
            lines.add(
                strings.driverGatewayWithStr.format(createColoredText(gatewayStatus, gatewayColor))
            )
        }

        if (driverStatus.isNotEmpty()) {
            lines.add(
                strings.testDriverWithStr.format(createColoredText(driverStatus, driverColor))
            )
        }

        if (fileName.isNotEmpty()) {
            val line = StringBuilder(strings.fileWithStr.format(fileName))

            if (fileStatus.isNotEmpty()) {
                line.append(", $fileStatus")
            }

            lines.add(line.toString())
        }

        if (testStatusLabel.isNotEmpty() || testStatus.isNotEmpty()) {
            val line = StringBuilder()

            if (testStatusLabel.isNotEmpty()) {
                line.append(testStatusLabel)
            }

            if (testStatus.isNotEmpty()) {
                if (line.isNotEmpty()) {
                    line.append(SPACE)
                }

                line.append(createColoredText(testStatus, testStatusColor))
            }

            lines.add(EMPTY)
            lines.add(line.toString())
        }

        if (errorMessage.isNotEmpty()) {
            lines.add(EMPTY)

            for (line in errorMessage.splitIntoLines()) {
                lines.add(createColoredText(line, TextColor.RED))
            }
        }

        if (screen.isNotEmpty() && errorMessage.isEmpty()) {
            lines.add(EMPTY)
            lines.addAll(screen.splitIntoLines())
        }

        if (helpText.isNotEmpty()) {
            for (line in helpText.splitIntoLines()) {
                lines.add(line)
            }
        }

        return lines
    }

    private fun createColoredText(
        text: String,
        color: TextColor
    ): String {
        return when (color) {
            TextColor.DEFAULT -> text
            TextColor.RED -> brightRed.invoke(text)
            TextColor.GREEN -> brightGreen.invoke(text)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MainView::class.java)
    }
}