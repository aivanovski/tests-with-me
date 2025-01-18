package com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BottomSheetItem(
    val id: String,
    val icon: BottomSheetIcon,
    val title: String
) : Parcelable