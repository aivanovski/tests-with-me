package com.github.aivanovski.testwithme.android.domain.resources

import android.content.Context
import androidx.annotation.StringRes

class ResourceProviderImpl(
    private val content: Context
) : ResourceProvider {

    override fun getString(@StringRes resId: Int): String {
        return content.getString(resId)
    }

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return content.getString(resId, *formatArgs)
    }
}