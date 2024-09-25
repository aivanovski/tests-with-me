package com.github.aivanovski.testswithme.ui

import com.github.aivanovski.testswithme.Msg
import com.github.aivanovski.testswithme.model.ProjectsPageModel
import tyrian.Html
import tyrian.Html.div

object ProjectsView {

  def render(model: ProjectsPageModel): Html[Msg] = {
    div()("PROJECTS PAGE")
  }
}
