package com.github.aivanovski.testswithme.android.data.api

import com.github.aivanovski.testswithme.web.api.Endpoints.FLOW
import com.github.aivanovski.testswithme.web.api.Endpoints.FLOW_RUN
import com.github.aivanovski.testswithme.web.api.Endpoints.GROUP
import com.github.aivanovski.testswithme.web.api.Endpoints.LOGIN
import com.github.aivanovski.testswithme.web.api.Endpoints.PROJECT
import com.github.aivanovski.testswithme.web.api.Endpoints.SIGN_UP
import com.github.aivanovski.testswithme.web.api.Endpoints.USER

class ApiUrlFactory {

    fun flow(flowUid: String): String = "$SERVER_URL/$FLOW/$flowUid"

    fun flows(): String = "$SERVER_URL/$FLOW"

    fun projects(): String = "$SERVER_URL/$PROJECT"

    fun flowRuns(): String = "$SERVER_URL/$FLOW_RUN"

    fun users(): String = "$SERVER_URL/$USER"

    fun login(): String = "$SERVER_URL/$LOGIN"

    fun groups(): String = "$SERVER_URL/$GROUP"

    fun group(groupUid: String): String = "$SERVER_URL/$GROUP/$groupUid"

    fun signUp(): String = "$SERVER_URL/$SIGN_UP"

    companion object {
        private const val SERVER_URL = "https://10.0.2.2:8443"
    }
}