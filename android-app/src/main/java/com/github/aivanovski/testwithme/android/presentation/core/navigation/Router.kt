package com.github.aivanovski.testwithme.android.presentation.core.navigation

import com.github.aivanovski.testwithme.android.presentation.screens.Screen
import kotlin.reflect.KClass

typealias ResultListener = (result: Any) -> Unit

interface Router {

    fun setRoot(screen: Screen)
    fun navigateTo(screen: Screen)
    fun exit()

    fun setResultListener(
        screenType: KClass<out Screen>,
        onResult: (result: Any) -> Unit
    )

    fun setResult(
        screenType: KClass<out Screen>,
        result: Any
    )
}