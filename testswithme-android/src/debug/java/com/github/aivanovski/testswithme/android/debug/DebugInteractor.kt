package com.github.aivanovski.testswithme.android.debug

import com.github.aivanovski.testswithme.android.debug.model.DebugCommand
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerManager
import com.github.aivanovski.testswithme.android.domain.flow.model.DriverServiceState
import com.github.aivanovski.testswithme.android.domain.flow.model.FlowRunnerState
import com.github.aivanovski.testswithme.domain.fomatters.CompactNodeFormatter
import com.github.aivanovski.testswithme.extensions.format
import timber.log.Timber

// TODO: move to debug DI module
class DebugInteractor(
    private val runnerManager: FlowRunnerManager
) {

    fun process(data: DebugCommand) {
        when {
            data.isPrintUiTree != null -> processPrintUiTree()
        }
    }

    private fun processPrintUiTree() {
        val driverState = runnerManager.getDriverState()
        if (driverState != DriverServiceState.RUNNING) {
            Timber.e("Driver is not running")
            return
        }

        if (runnerManager.getRunnerState() != FlowRunnerState.IDLE) {
            Timber.d("Runner is not in idle state")
            return
        }

        val uiTree = runnerManager.getUiTree()
        Timber.d("UI Tree:")
        Timber.d(uiTree?.format(CompactNodeFormatter()))
    }
}