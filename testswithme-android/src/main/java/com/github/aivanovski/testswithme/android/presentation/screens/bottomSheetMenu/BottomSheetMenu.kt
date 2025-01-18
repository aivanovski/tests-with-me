package com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu

import android.os.Parcelable
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model.BottomSheetItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class BottomSheetMenu(
    val items: List<BottomSheetItem>
) : Parcelable