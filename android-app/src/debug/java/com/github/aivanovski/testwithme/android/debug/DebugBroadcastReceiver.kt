package com.github.aivanovski.testwithme.android.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber
import com.github.aivanovski.testwithme.android.debug.parser.DebugCommandParser
import com.github.aivanovski.testwithme.android.di.GlobalInjector.get
import com.github.aivanovski.testwithme.extensions.unwrap
import com.github.aivanovski.testwithme.extensions.unwrapError

class DebugBroadcastReceiver : BroadcastReceiver() {

    private val parser = DebugCommandParser()
    private val interactor = DebugInteractor(
        flowRunnerInteractor = get()
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        val parseResult = parser.parse(intent?.extras)
        if (parseResult.isLeft()) {
            Timber.e("Failed to process command: ", parseResult.unwrapError())
            return
        }

        val command = parseResult.unwrap()
        interactor.process(command)
    }
}