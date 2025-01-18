package com.github.aivanovski.testswithme.android.data.settings

fun interface OnSettingsChangeListener {
    fun onSettingChanged(key: SettingKey)
}