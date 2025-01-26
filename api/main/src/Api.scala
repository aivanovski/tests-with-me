import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import okhttp3.Response
import utils.{HttpClient, JsonUtils}
import utils.JsonUtils.{parseAsMap, toJson}

import java.util.Base64

object Api {

  def login(): Response = HttpClient.post(
    endpoint = "login",
    body = toJson(Map(
      "username" -> "admin",
      "password" -> "abc123"
    ))
  )

  def signUp(username: String = "admin") = HttpClient.post(
    endpoint = "sign-up",
    body = toJson(Map(
      "username" -> username,
      "password" -> "abc123",
      "email" -> s"$username@test.com"
    ))
  )

  def getFlows() = HttpClient.get("flow", getAuthToken())

  def getFlow(uid: String) = HttpClient.get(s"flow/$uid", getAuthToken())

  def getGroups() = HttpClient.get("group", getAuthToken())

  def getProjects() = HttpClient.get("project", getAuthToken())

  def getUsers() = HttpClient.get("user", getAuthToken())

  def getFlowRuns() = HttpClient.get("flow-run", getAuthToken())

  def postProject(data: Map[String, String]) = HttpClient.post(
    endpoint = "project",
    body = toJson(data),
    authToken = getAuthToken()
  )

  def postGroup(path: String, name: String) = HttpClient.post(
    endpoint = "group",
    body = toJson(Map("path" -> path, "name" -> name)),
    authToken = getAuthToken()
  )

  def postFlow(path: String, content: String) = HttpClient.post(
    endpoint = "flow",
    body = toJson(
      Map(
        "path" -> path,
        "base64Content" -> Base64.getEncoder.encodeToString(content.getBytes())
      )
    ),
    authToken = getAuthToken()
  )

  def postFlowRun(flowUid: String) = HttpClient.post(
    endpoint = "flow-run",
    body = toJson(
      Map(
        "flowId" -> flowUid,
        "durationInMillis" -> "360",
        "isSuccess" -> "true",
        "result" -> "Either.Right(Unit)",
        "appVersionName" -> "1.7.0",
        "appVersionCode" -> "10700",
        "reportBase64Content" -> "",
      )
    ),
    authToken = getAuthToken()
  )

  def deleteGroup(uid: String) = HttpClient.delete(s"group/$uid", getAuthToken())

  def deleteFlow(uid: String) = HttpClient.delete(s"flow/$uid", getAuthToken())

  def getAuthToken(): Option[String] = {
    val response = login()

    response.code() match {
      case 200 => JsonUtils.parseLoginResponse(response.body().string())
        .map(result => result.token)
        .toOption
      case _ => None
    }
  }
}


