package com.github.aivanovski.testswithme.ui

import com.github.aivanovski.testswithme.Msg
import com.github.aivanovski.testswithme.model.LoginPageModel
import tyrian.{Attr, Html, HtmlAttributes}
import tyrian.Html.*
import tyrian.Empty

object LoginView {

  def render(model: LoginPageModel): Html[Msg] = {
    val error = if (model.errorMessage.nonEmpty) {
      errorMessage(model)
    } else {
      Empty
    }

    div(cls := "login-main")(
      div(cls := "login-box")(
        error,
        loginInput(model),
        passwordInput(model),
        loginButton(model)
      )
    )
  }

  private def errorMessage(model: LoginPageModel): Html[Msg] =
    div(cls := "error-box")(
      span(cls := "error-text") {
        model.errorMessage
      }
    )

  private def loginInput(model: LoginPageModel): Html[Msg] =
    input(
      cls         := "login-input",
      placeholder := "Username or email address",
      value       := model.login,
      disabled(model.isLoading),
      onInput {
        Msg.LoginTextChanged(_)
      }
    )

  private def passwordInput(model: LoginPageModel): Html[Msg] =
    input(
      cls         := "login-input",
      placeholder := "Password",
      value       := model.password,
      _type       := "password",
      disabled(model.isLoading),
      onInput {
        Msg.PasswordTextChanged(_)
      }
    )

  private def loginButton(model: LoginPageModel): Html[Msg] =
    val buttonText = if (model.isLoading) "Signing in..." else "Sign in"

    val attrs = List(
    )

    button(
      cls := "login-button",
      onClick(Msg.LoginButtonClick),
      disabled(model.isLoading)
    )(
      buttonText
    )
}
