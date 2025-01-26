package com.github.aivanovski.testswithme.android.presentation.screens.textViewer.model

sealed interface TextViewerIntent {

    data object Initialize : TextViewerIntent
}