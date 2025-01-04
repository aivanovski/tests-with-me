package com.github.aivanovski.testswithme.android.data.settings

import com.github.aivanovski.testswithme.android.entity.Account

interface Settings {
    var startJobUid: String?
    var authToken: String?
    var isSslVerificationDisabled: Boolean
    var account: Account?
    var serverUrl: String

    fun subscribe(listener: OnSettingsChangeListener)
    fun unsubscribe(listener: OnSettingsChangeListener)
}