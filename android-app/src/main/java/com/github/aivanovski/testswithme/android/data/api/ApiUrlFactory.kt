package com.github.aivanovski.testswithme.android.data.api

import com.github.aivanovski.testswithme.android.BuildConfig
import com.github.aivanovski.testswithme.android.data.settings.Settings
import com.github.aivanovski.testswithme.web.api.Endpoints.FLOW
import com.github.aivanovski.testswithme.web.api.Endpoints.FLOW_RUN
import com.github.aivanovski.testswithme.web.api.Endpoints.GROUP
import com.github.aivanovski.testswithme.web.api.Endpoints.LOGIN
import com.github.aivanovski.testswithme.web.api.Endpoints.PROJECT
import com.github.aivanovski.testswithme.web.api.Endpoints.RESET_FLOW_RUN
import com.github.aivanovski.testswithme.web.api.Endpoints.SIGN_UP
import com.github.aivanovski.testswithme.web.api.Endpoints.USER

class ApiUrlFactory(
    private val settings: Settings
) {

    fun flow(flowUid: String): String = getServerUrl() + "/$FLOW/$flowUid"

    fun flows(): String = getServerUrl() + "/$FLOW"

    fun projects(): String = getServerUrl() + "/$PROJECT"

    fun flowRuns(): String = getServerUrl() + "/$FLOW_RUN"

    fun resetFlowRun(): String = getServerUrl() + "/$RESET_FLOW_RUN"

    fun users(): String = getServerUrl() + "/$USER"

    fun login(): String = getServerUrl() + "/$LOGIN"

    fun groups(): String = getServerUrl() + "/$GROUP"

    fun group(groupUid: String): String = getServerUrl() + "/$GROUP/$groupUid"

    fun signUp(): String = getServerUrl() + "/$SIGN_UP"

    private fun getServerUrl(): String {
        return if (BuildConfig.DEBUG) {
            settings.serverUrl
        } else {
            PROD_URL
        }
    }

    companion object {
        const val PROD_URL = "https://testswithme.org"
        const val DEBUG_URL = "https://10.0.2.2:8443"
    }
}