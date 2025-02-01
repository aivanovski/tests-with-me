package com.github.aivanovski.testswithme.android.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.aivanovski.testswithme.android.debug.parser.DebugCommandParser
import com.github.aivanovski.testswithme.android.di.GlobalInjector.get
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import timber.log.Timber

class DebugBroadcastReceiver : BroadcastReceiver() {

    private val interactor: DebugInteractor by lazy {
        DebugInteractor(
            runnerManager = get()
        )
    }
    private val parser = DebugCommandParser()

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        val parseResult = parser.parse(intent?.extras)
        if (parseResult.isLeft()) {
            Timber.e("Failed to process command: ", parseResult.unwrapError())
            return
        }

        val command = parseResult.unwrap()
        interactor.process(command)
    }
}