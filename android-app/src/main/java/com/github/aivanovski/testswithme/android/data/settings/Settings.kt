package com.github.aivanovski.testswithme.android.data.settings

interface Settings {
    var startJobUid: String?
    var authToken: String?
    var isSslVerificationDisabled: Boolean

    fun subscribe(listener: OnSettingsChangeListener)
    fun unsubscribe(listener: OnSettingsChangeListener)
}