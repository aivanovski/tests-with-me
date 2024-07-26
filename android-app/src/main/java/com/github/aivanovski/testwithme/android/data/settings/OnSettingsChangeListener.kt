package com.github.aivanovski.testwithme.android.data.settings

fun interface OnSettingsChangeListener {
    fun onSettingChanged(key: SettingKey)
}