package com.github.aivanovski.testswithme.android.domain.driverServer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.aivanovski.testswithme.android.di.GlobalInjector.inject
import com.github.aivanovski.testswithme.android.domain.driverServer.parser.GatewayCommandParser
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class GatewayCommandReceiver : BroadcastReceiver() {

    private val interactor: GatewayReceiverInteractor by inject()
    private val parser = GatewayCommandParser()
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        val parseResult = parser.parse(intent?.extras)
        if (parseResult.isLeft()) {
            Timber.e("Failed to process command: %s", parseResult.unwrapError())
            return
        }

        val command = parseResult.unwrap()
        Timber.d("Processing command: %s", command)

        scope.launch {
            interactor.processCommand(command)
        }
    }
}