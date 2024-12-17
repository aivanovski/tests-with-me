package com.github.aivanovski.testswithme.android.data.settings

enum class SettingKey(val key: String) {
    START_JOB_UID(key = "startJobUid"),
    AUTH_TOKEN(key = "authToken"),
    IS_SSL_VERIFICATION_DISABLED(key = "isSslVerificationDisabled"),
    ACCOUNT(key = "account")
}