package com.github.aivanovski.testswithme.android.data.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.github.aivanovski.testswithme.android.data.api.ApiUrlFactory
import com.github.aivanovski.testswithme.android.data.settings.SettingKey.ACCOUNT
import com.github.aivanovski.testswithme.android.data.settings.SettingKey.AUTH_TOKEN
import com.github.aivanovski.testswithme.android.data.settings.SettingKey.IS_SSL_VERIFICATION_DISABLED
import com.github.aivanovski.testswithme.android.data.settings.SettingKey.SERVER_URL
import com.github.aivanovski.testswithme.android.data.settings.SettingKey.START_JOB_UID
import com.github.aivanovski.testswithme.android.data.settings.encryption.DataCipher
import com.github.aivanovski.testswithme.android.data.settings.encryption.DataCipherProvider
import com.github.aivanovski.testswithme.android.entity.Account
import com.github.aivanovski.testswithme.extensions.splitToPair
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsImpl(
    context: Context,
    private val dataCipherProvider: DataCipherProvider
) : Settings {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val listeners = CopyOnWriteArrayList<OnSettingsChangeListener>()
    private val scope = CoroutineScope(Dispatchers.Main)
    private val cipher: DataCipher by lazy {
        dataCipherProvider.getCipher()
    }

    override var startJobUid: String?
        get() = getString(START_JOB_UID)
        set(value) = putString(START_JOB_UID, value)

    override var authToken: String?
        get() = getString(AUTH_TOKEN)?.let { cipher.decode(it) }
        set(value) = putString(AUTH_TOKEN, value?.let { cipher.encode(value) })

    override var isSslVerificationDisabled: Boolean
        get() = getBoolean(IS_SSL_VERIFICATION_DISABLED)
        set(value) = putBoolean(IS_SSL_VERIFICATION_DISABLED, value)

    override var account: Account?
        get() {
            val encodedData = getString(ACCOUNT) ?: return null
            return cipher.decode(encodedData)?.readAccount()
        }
        set(value) {
            val data = value?.formatToString()
            putString(ACCOUNT, data?.let { cipher.encode(it) })
        }

    override var serverUrl: String
        get() = getString(SERVER_URL) ?: ApiUrlFactory.PROD_URL
        set(value) {
            putString(SERVER_URL, value)
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

    private fun putBoolean(
        key: SettingKey,
        value: Boolean
    ) {
        val isChanged = (getBoolean(key) != value)

        putValue {
            putBoolean(key.key, value)
        }

        if (isChanged) {
            notifyOnSettingsChanged(key)
        }
    }

    private fun putString(
        key: SettingKey,
        value: String?
    ) {
        val isChanged = (getString(key) != value)

        putValue {
            putString(key.key, value)
        }

        if (isChanged) {
            notifyOnSettingsChanged(key)
        }
    }

    private fun putInt(
        key: SettingKey,
        value: Int
    ) {
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

    private fun Account.formatToString(): String {
        return "$name:$password"
    }

    private fun String.readAccount(): Account? {
        val (name, password) = this.splitToPair(separator = ":")
            ?: return null

        return Account(
            name = name,
            password = password
        )
    }
}