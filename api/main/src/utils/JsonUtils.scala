package utils

import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.generic.auto.*

object JsonUtils {

  def toJson(data: Map[String, String]): String =
    data.asJson().toString()

  def reformatJson(body: String): Option[String] = {
    val json = parse(body)

    json match {
      case Right(data) => Some(data.spaces2)
      case Left(e) => None
    }
  }

  def parseAsMap(body: String): Map[String, String] =
    parse(body)
      .getOrElse(null)
      .as[Map[String, String]]
      .getOrElse(Map())

  def parseLoginResponse(body: String): Either[Error, LoginResponse] =
    decode[LoginResponse](body)

  def toJson(request: PostFlowRequest): String = request.asJson().toString()
  def toJson(request: PutFlowRequest): String = request.asJson().toString()
}

// Responses
case class LoginResponse(token: String)

// Requests
case class PostFlowRequest(parent: Map[String, String], base64Content: String)
case class PutFlowRequest(parent: Option[Map[String, String]], base64Content: String)