package utils

import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*

object JsonUtils {

  def toJson(data: Map[String, String]): String =
    data.asJson().toString()

  def reformatJson(body: String): Option[String] = {
    val json = parse(body)

    return json match {
      case Right(data) => Some(data.spaces4)
      case Left(e) => None
    }
  }


  def parseAsMap(body: String): Map[String, String] =
    parse(body)
      .getOrElse(null)
      .as[Map[String, String]]
      .getOrElse(Map())
}