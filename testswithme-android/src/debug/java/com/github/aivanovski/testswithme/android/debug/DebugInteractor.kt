package com.github.aivanovski.testswithme.android.debug

import com.github.aivanovski.testswithme.android.debug.model.DebugCommand
import com.github.aivanovski.testswithme.android.domain.flow.FlowRunnerManager
import com.github.aivanovski.testswithme.android.entity.DriverServiceState
import com.github.aivanovski.testswithme.flow.commands.PrintUiTree
import timber.log.Timber

// TODO: move to debug DI module
class DebugInteractor {

    fun process(data: DebugCommand) {
        when {
            data.isPrintUiTree != null -> processPrintUiTree()
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
}