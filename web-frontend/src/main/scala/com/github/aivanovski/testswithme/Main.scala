package com.github.aivanovski.testswithme

import cats.effect.IO
import com.github.aivanovski.testswithme.model.{LoginPageModel, PageModel, ProjectsPageModel}
import com.github.aivanovski.testswithme.network.ApiClient
import com.github.aivanovski.testswithme.ui.RootView
import com.github.aivanovski.testswithme.web.api.response.ProjectsItemDto
import tyrian.Html.*
import tyrian.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("TyrianApp")
object Main extends TyrianIOApp[Msg, PageModel]:

  def router: Location => Msg = Routing.none(Msg.NoOp)

  def init(flags: Map[String, String]): (PageModel, Cmd[IO, Msg]) =
    (
      LoginPageModel(
        isLoading = false,
        login = "",
        password = "",
        errorMessage = ""
      ),
      Cmd.None
    )

  def update(model: PageModel): Msg => (PageModel, Cmd[IO, Msg]) =
    case Msg.NoOp => (model, Cmd.None)

    case Msg.Error(error) =>
      println(s"onError: $error")
      val newModel = model match {
        case lm: LoginPageModel =>
          lm.copy(
            errorMessage = error,
            isLoading = false
          )
        case pm: ProjectsPageModel => pm.copy() // TODO: implement error
      }

      (newModel, Cmd.None)

    case Msg.LoginTextChanged(login) =>
      val loginModel = model.asInstanceOf[LoginPageModel]
      (loginModel.copy(login = login), Cmd.None)

    case Msg.PasswordTextChanged(password) =>
      val loginModel = model.asInstanceOf[LoginPageModel]
      (loginModel.copy(password = password), Cmd.None)

    case Msg.LoginButtonClick =>
      val loginModel = model.asInstanceOf[LoginPageModel]
      val username   = loginModel.login
      val password   = loginModel.password
      (loginModel.copy(isLoading = true), ApiClient.login(username, password))

    case Msg.LoginError(error) =>
      val loginModel = model.asInstanceOf[LoginPageModel]
      (loginModel.copy(isLoading = false, errorMessage = error), Cmd.None)

    case Msg.LoginSuccess(token) =>
      (ProjectsPageModel(projects = List.empty), ApiClient.getProjects())

    case Msg.ProjectsLoaded(projects) =>
      println(s"projects=$projects")
      (model, Cmd.None)

  def view(model: PageModel): Html[Msg] = RootView.render(model)

  def subscriptions(model: PageModel): Sub[IO, Msg] =
    Sub.None

enum Msg:
  case NoOp
  case Error(error: String)
  case LoginTextChanged(username: String)
  case PasswordTextChanged(password: String)
  case LoginButtonClick
  case LoginError(error: String)
  case LoginSuccess(token: String)
  case ProjectsLoaded(projects: List[ProjectsItemDto])
