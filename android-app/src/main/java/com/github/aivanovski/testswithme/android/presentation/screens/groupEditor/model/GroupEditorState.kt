package com.github.aivanovski.testswithme.android.presentation.screens.groupEditor.model

import com.github.aivanovski.testswithme.android.entity.ErrorMessage
import com.github.aivanovski.testswithme.utils.StringUtils

data class GroupEditorState(
    val isLoading: Boolean = false,
    val errorMessage: ErrorMessage? = null,
    val name: String = StringUtils.EMPTY,
    val nameError: String? = null
)