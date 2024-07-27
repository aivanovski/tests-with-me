package com.github.aivanovski.testswithme.android.presentation.screens.projects.model

sealed interface ProjectsIntent {

    object Initialize : ProjectsIntent

    object ReloadData : ProjectsIntent

    object OnAddButtonClick : ProjectsIntent
}