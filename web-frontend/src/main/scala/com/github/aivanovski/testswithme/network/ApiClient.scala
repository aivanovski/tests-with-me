package com.github.aivanovski.testswithme.network

import cats.effect.IO
import com.github.aivanovski.testswithme.Msg
import com.github.aivanovski.testswithme.network.decoders.ResponseDecoders
import com.github.aivanovski.testswithme.network.decoders.ResponseDecoders.{
  decodeErrorResponse,
  decodeLoginResponse,
  decodeProjectsResponse
}
import com.github.aivanovski.testswithme.web.api.{ApiHeaders, Endpoints}
import com.github.aivanovski.testswithme.web.api.ApiHeaders.X_REQUEST_SET_COOKIE
import com.github.aivanovski.testswithme.web.api.Endpoints.{LOGIN, PROJECT}
import com.github.aivanovski.testswithme.web.api.request.LoginRequest
import io.circe.generic.semiauto
import io.circe.syntax.EncoderOps
import io.circe.generic.auto.*
import tyrian.Cmd
import tyrian.http.Body.PlainText
import tyrian.http.{Decoder, Header, Http, Request}

object ApiClient {

  private val SERVER_URL        = "https://api.testswithme.org"
  private val CONTENT_TYPE_JSON = "application/json"

  private val HTTP_STATUS_OK = 200

  private var token: Option[String] = None

  def login(username: String, password: String): Cmd[IO, Msg] = {
    val codec = semiauto.deriveCodec[LoginRequest]
    val body  = LoginRequest(username = username, password = password).asJson(codec).toString

    val request = Request
      .post(
        url = s"$SERVER_URL/$LOGIN",
        PlainText(contentType = CONTENT_TYPE_JSON, body = body)
      )
      .withHeaders(Header(X_REQUEST_SET_COOKIE, "true"))

    val decoder = Decoder[Msg](
      response => {
        println(s"response: status=${response.status}")
        println(s"    body=${response.body}")

        if (response.status.code == HTTP_STATUS_OK) {
          decodeLoginResponse(response)
        } else {
          decodeErrorResponse(response)
        }
      },
      err => Msg.Error(err.toString)
    )

    Http.send(request, decoder)
  }

  def getProjects(): Cmd[IO, Msg] = {
    val request = Request.get(
      url = s"$SERVER_URL/$PROJECT"
    )

    val decoder = Decoder[Msg](
      response => {
        if (response.status.code == HTTP_STATUS_OK) {
          decodeProjectsResponse(response)
        } else {
          decodeErrorResponse(response)
        }
      },
      err => Msg.Error(err.toString)
    )

    Http.send(request, decoder)
  }
}
