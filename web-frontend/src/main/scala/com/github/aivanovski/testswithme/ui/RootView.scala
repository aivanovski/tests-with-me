package com.github.aivanovski.testswithme.ui

import com.github.aivanovski.testswithme.Msg
import com.github.aivanovski.testswithme.model.{LoginPageModel, PageModel, ProjectsPageModel}
import tyrian.Html

object RootView {

  def render(model: PageModel): Html[Msg] = model match {
    case loginModel: LoginPageModel       => LoginView.render(loginModel)
    case projectsModel: ProjectsPageModel => ProjectsView.render(projectsModel)
  }
}
