package com.github.aivanovski.testwithme.android.data.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.github.aivanovski.testwithme.android.data.settings.SettingKey.AUTH_TOKEN
import com.github.aivanovski.testwithme.android.data.settings.SettingKey.START_JOB_UID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

class SettingsImpl(
    context: Context
) : Settings {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val listeners = CopyOnWriteArrayList<OnSettingsChangeListener>()
    private val scope = CoroutineScope(Dispatchers.Main)

    override var startJobUid: String?
        get() = getString(START_JOB_UID)
        set(value) {
            putString(START_JOB_UID, value)
        }

    override var authToken: String?
        get() = getString(AUTH_TOKEN)
        set(value) {
            putString(AUTH_TOKEN, value)
        }

    override fun subscribe(listener: OnSettingsChangeListener) {
        if (listener !in listeners) {
            listeners.add(listener)
        }
    }

    override fun unsubscribe(listener: OnSettingsChangeListener) {
        listeners.remove(listener)
    }

    private fun getBoolean(key: SettingKey): Boolean {
        return preferences.getBoolean(key.key, false)
    }

    private fun getString(key: SettingKey): String? {
        return preferences.getString(key.key, null)
    }

    private fun getInt(key: SettingKey): Int {
        return preferences.getInt(key.key, 0)
    }

    private fun putBoolean(key: SettingKey, value: Boolean) {
        val isChanged = (getBoolean(key) != value)

        putValue {
            putBoolean(key.key, value)
        }
    }

    private fun putString(key: SettingKey, value: String?) {
        val isChanged = (getString(key) != value)

        putValue {
            putString(key.key, value)
        }

        if (isChanged) {
            notifyOnSettingsChanged(key)
        }
    }

    private fun putInt(key: SettingKey, value: Int) {
        val isChanged = (getInt(key) != value)

        putValue {
            putInt(key.key, value)
        }

        if (isChanged) {
            notifyOnSettingsChanged(key)
        }
    }

    private fun notifyOnSettingsChanged(key: SettingKey) {
        scope.launch {
            listeners.forEach { listener ->
                listener.onSettingChanged(key)
            }
        }
    }

    private inline fun putValue(action: SharedPreferences.Editor.() -> Unit) {
        val editor = preferences.edit()
        action.invoke(editor)
        editor.apply()
    }
}