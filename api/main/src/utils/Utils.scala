package utils

import okhttp3.Response
import utils.JsonUtils.{parseAsMap, reformatJson, toJson}
import utils.{HttpClient, JsonUtils}
import scala.annotation.retains

def printResponse(response: Response): Unit = {
  val statusCode = response.code()
  val body = response.body().string()
  println(s"Response[code=$statusCode]:")

  val lines = reformatJson(body)
    .getOrElse("")
    .split("\n")
    .filter(line => line.nonEmpty)
    .toList

  if (lines.isEmpty) {
    println(s"response=$response")
  }

  for (line <- lines) println(line)
}