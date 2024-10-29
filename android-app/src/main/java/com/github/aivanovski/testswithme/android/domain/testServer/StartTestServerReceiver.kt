package com.github.aivanovski.testswithme.android.domain.testServer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.aivanovski.testswithme.android.domain.testServer.parser.TestServerCommandParser
import com.github.aivanovski.testswithme.extensions.unwrap
import com.github.aivanovski.testswithme.extensions.unwrapError
import timber.log.Timber

class StartTestServerReceiver : BroadcastReceiver() {

    private val parser = TestServerCommandParser()

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
    }
}