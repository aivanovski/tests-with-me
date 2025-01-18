package com.github.aivanovski.testswithme.android.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings

object IntentUtils {

    fun newAccessibilityServicesIntent(): Intent {
        return Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            .apply {
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }

    fun newOpenUrlIntent(url: String): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
    }
}