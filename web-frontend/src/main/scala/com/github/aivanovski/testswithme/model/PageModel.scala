package com.github.aivanovski.testswithme.model

import com.github.aivanovski.testswithme.web.api.response.ProjectsItemDto

sealed trait PageModel

case class LoginPageModel(
    isLoading: Boolean,
    login: String,
    password: String,
    errorMessage: String
) extends PageModel

case class ProjectsPageModel(
    projects: List[ProjectsItemDto]
) extends PageModel
