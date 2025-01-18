package com.github.aivanovski.testswithme.android.presentation.core.navigation

import com.github.aivanovski.testswithme.android.presentation.screens.Screen
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.BottomSheetMenu
import kotlin.reflect.KClass

typealias ResultListener = (result: Any) -> Unit

interface Router {

    fun setRoot(screen: Screen)
    fun navigateTo(screen: Screen)
    fun replaceCurrent(screen: Screen)
    fun exit()
    fun showBottomSheet(
        menu: BottomSheetMenu,
        onClick: (index: Int) -> Unit
    )

    fun setResultListener(
        screenType: KClass<out Screen>,
        onResult: (result: Any) -> Unit
    )

    fun setResult(
        screenType: KClass<out Screen>,
        result: Any
    )
}