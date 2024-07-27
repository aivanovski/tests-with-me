package com.github.aivanovski.testswithme.android.debug

import arrow.core.Either
import arrow.core.raise.either
import com.github.aivanovski.testswithme.android.debug.model.DebugCommand
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerInteractor
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerManager
import com.github.aivanovski.testswithme.android.entity.DriverServiceState
import com.github.aivanovski.testswithme.android.entity.exception.AppException
import com.github.aivanovski.testswithme.android.entity.exception.ParsingException
import com.github.aivanovski.testswithme.android.utils.Base64Utils
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import com.github.aivanovski.testswithme.flow.commands.PrintUiTree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

// TODO: move to debug DI module
class DebugInteractor(
    private val flowRunnerInteractor: FlowRunnerInteractor
) {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun process(data: DebugCommand) {
        when {
            data.testFlowContent != null -> processFlowContent(data.testFlowContent)
            data.isPrintUiTree != null -> processPrintUiTree()
        }
    }

    private fun processFlowContent(content: String) {
        ioScope.launch {
            val parseAndRunResult = parseAndRunFlow(content)

            if (parseAndRunResult.isRight()) {
                val jobUid = parseAndRunResult.unwrap()
                Timber.d("Processed successfully: jobUid=%s", jobUid)
            } else {
                Timber.e("Failed to run flow: ")
                Timber.e(parseAndRunResult.unwrapError())
            }
        }
    }

    private fun processPrintUiTree() {
        val driverState = FlowRunnerManager.getDriverState()
        if (driverState != DriverServiceState.RUNNING) {
            Timber.e("Driver is not running")
            return
        }

        FlowRunnerManager.sendDebugCommand(PrintUiTree())
    }

    private suspend fun parseAndRunFlow(flowBase64Content: String): Either<AppException, String> =
        either {
            val decodedContent = Base64Utils.decode(flowBase64Content)
                ?: raise(ParsingException("Failed to decode flow"))

            val flow = flowRunnerInteractor.parseFlow(flowBase64Content).bind()

            flowRunnerInteractor.saveFlowContent(
                flowUid = flow.entry.uid,
                content = decodedContent
            ).bind()

            flowRunnerInteractor.removeAllJobs().bind()

            val jobUid = flowRunnerInteractor.addFlowToJobQueue(flow).bind()

            jobUid
        }
}