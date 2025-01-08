package com.github.aivanovski.testswithme.android.data.settings

enum class SettingKey(val key: String) {
    START_JOB_UID(key = "startJobUid"),
    AUTH_TOKEN(key = "authToken"),
    IS_SSL_VERIFICATION_DISABLED(key = "isSslVerificationDisabled"),
    ACCOUNT(key = "account"),
    SERVER_URL(key = "server_url"),
    DELAY_SCALE_FACTOR(key = "delay_scale_factor"),
    NUMBER_OF_RETRIES(key = "number_of_retries")
}