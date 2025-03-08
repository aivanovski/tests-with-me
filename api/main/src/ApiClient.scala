import okhttp3.Response
import utils.{HttpClient, JsonUtils}
import utils.JsonUtils.toJson

import java.util.Base64

enum ServerUrl(val value: String):
  case Prod extends ServerUrl("https://testswithme.org")
  case Local extends ServerUrl("https://127.0.0.1:8443")

class ApiClient(private val baseServerUrl: String) {

  def login(
    username: String,
    password: String
  ): Response = HttpClient.post(
    url = s"$baseServerUrl/login",
    body = toJson(Map(
      "username" -> username,
      "password" -> password
    ))
  )

  def signUp(
    username: String,
    password: String,
    email: String
  ) = HttpClient.post(
    url = s"$baseServerUrl/sign-up",
    body = toJson(Map(
      "username" -> username,
      "password" -> password,
      "email" -> email
    ))
  )

  def getFlows() = HttpClient.get(s"$baseServerUrl/flow", getAuthToken())

  def getFlow(uid: String) = HttpClient.get(s"$baseServerUrl/flow/$uid", getAuthToken())

  def getGroups() = HttpClient.get(s"$baseServerUrl/group", getAuthToken())

  def getProjects() = HttpClient.get(s"$baseServerUrl/project", getAuthToken())

  def requestProjectSync(uid: String) = HttpClient.post(
    url = s"$baseServerUrl/request-project-sync/$uid",
    body = "",
    authToken = getAuthToken()
  )

  def getUsers() = HttpClient.get(s"$baseServerUrl/user", getAuthToken())

  def getFlowRuns() = HttpClient.get(s"$baseServerUrl/flow-run", getAuthToken())

  def getFlowRun(uid: String) = HttpClient.get(s"$baseServerUrl/flow-run/$uid", getAuthToken())

  def postProject(data: Map[String, String]) = HttpClient.post(
    url = s"$baseServerUrl/project",
    body = toJson(data),
    authToken = getAuthToken()
  )

  def postGroup(path: String, name: String) = HttpClient.post(
    url = s"$baseServerUrl/group",
    body = toJson(Map("path" -> path, "name" -> name)),
    authToken = getAuthToken()
  )

  def postFlow(path: String, content: String) = HttpClient.post(
    url = s"$baseServerUrl/flow",
    body = toJson(
      Map(
        "path" -> path,
        "base64Content" -> Base64.getEncoder.encodeToString(content.getBytes())
      )
    ),
    authToken = getAuthToken()
  )

  def postFlowRun(flowUid: String) = HttpClient.post(
    url = s"$baseServerUrl/flow-run",
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

  def deleteGroup(uid: String) = HttpClient.delete(s"$baseServerUrl/group/$uid", getAuthToken())

  def deleteFlow(uid: String) = HttpClient.delete(s"$baseServerUrl/flow/$uid", getAuthToken())

  def getAuthToken(): Option[String] = {
    val user = Data.readDefaultUser()
    val response = login(username = user.username, password = user.password)

    response.code() match {
      case 200 => JsonUtils.parseLoginResponse(response.body().string())
        .map(result => result.token)
        .toOption
      case _ => None
    }
  }
}

object ApiClient {

  def build(url: ServerUrl): ApiClient =
    ApiClient(baseServerUrl = url.value)
}