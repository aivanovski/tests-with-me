package com.github.aivanovski.testwithme.android.di

import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier

object GlobalInjector {

    inline fun <reified T : Any> inject(
        qualifier: Qualifier? = null,
        params: ParametersHolder? = null
    ): Lazy<T> =
        GlobalContext.get().inject(
            qualifier = qualifier,
            parameters = if (params != null) {
                { params }
            } else {
                null
            }
        )

    inline fun <reified T : Any> get(
        qualifier: Qualifier? = null,
        params: ParametersHolder? = null
    ): T =
        GlobalContext.get().get(
            qualifier = qualifier,
            parameters = if (params != null) {
                { params }
            } else {
                null
            }
        )
}