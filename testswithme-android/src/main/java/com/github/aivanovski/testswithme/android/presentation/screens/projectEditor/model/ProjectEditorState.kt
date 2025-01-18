package com.github.aivanovski.testswithme.android.presentation.screens.projectEditor.model

import com.github.aivanovski.testswithme.android.entity.ErrorMessage
import com.github.aivanovski.testswithme.utils.StringUtils

data class ProjectEditorState(
    val isLoading: Boolean = false,
    val errorMessage: ErrorMessage? = null,
    val packageName: String = StringUtils.EMPTY,
    val packageNameError: String? = null,
    val name: String = StringUtils.EMPTY,
    val nameError: String? = null,
    val description: String = StringUtils.EMPTY,
    val siteUrl: String = StringUtils.EMPTY,
    val downloadUrl: String = StringUtils.EMPTY
)