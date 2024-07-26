package com.github.aivanovski.testwithme.android.data.api

import com.github.aivanovski.testwithme.web.api.Endpoints.FLOW_RUN
import com.github.aivanovski.testwithme.web.api.Endpoints.FLOW
import com.github.aivanovski.testwithme.web.api.Endpoints.GROUP
import com.github.aivanovski.testwithme.web.api.Endpoints.LOGIN
import com.github.aivanovski.testwithme.web.api.Endpoints.PROJECT
import com.github.aivanovski.testwithme.web.api.Endpoints.USER

class ApiUrlFactory {

    fun flow(flowUid: String): String = "$SERVER_URL/$FLOW/$flowUid"

    fun flows(): String = "$SERVER_URL/$FLOW"

    fun projects(): String = "$SERVER_URL/$PROJECT"

    fun flowRuns(): String = "$SERVER_URL/$FLOW_RUN"

    fun users(): String = "$SERVER_URL/$USER"

    fun login(): String = "$SERVER_URL/$LOGIN"

    fun groups(): String = "$SERVER_URL/$GROUP"

    companion object {
        private const val SERVER_URL = "http://10.0.2.2:8080"
    }
}