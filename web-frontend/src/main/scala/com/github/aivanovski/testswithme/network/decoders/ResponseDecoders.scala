package com.github.aivanovski.testswithme.network.decoders

import com.github.aivanovski.testswithme.Msg
import com.github.aivanovski.testswithme.web.api.response.{
  ErrorMessage,
  LoginResponse,
  ProjectsResponse
}
import io.circe.generic.auto.*
import io.circe.parser.decode
import tyrian.http.Response

object ResponseDecoders {

  def decodeErrorResponse(response: Response): Msg = {
    decode[ErrorMessage](input = response.body) match {
      case Left(error) =>
        if (response.status.message.nonEmpty) {
          Msg.Error(s"code=${response.status.code}, message=${response.status.message}")
        } else {
          Msg.Error(s"code=${response.status.code}")
        }
      case Right(message) => Msg.Error(message.message)
    }
  }

  def decodeLoginResponse(response: Response): Msg = {
    decode[LoginResponse](input = response.body) match {
      case Left(error)           => Msg.Error(error.toString)
      case Right(parsedResponse) => Msg.LoginSuccess(token = parsedResponse.token)
    }
  }

  def decodeProjectsResponse(response: Response): Msg = {
    decode[ProjectsResponse](input = response.body) match {
      case Left(error) => Msg.Error(error.toString)
      case Right(resp) => Msg.ProjectsLoaded(projects = resp.projects)
    }
  }
}
