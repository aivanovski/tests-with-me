package com.github.aivanovski.testswithme.android.domain.flow

import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.flow.runner.ExecutionEnvironment

class Environment(
    private val settings: Settings
) : ExecutionEnvironment {
    override fun getDelayScaleFactor(): Int = settings.delayScaleFactor
}