
import okhttp3.Response
import utils.JsonUtils.{parseAsMap, reformatJson, toJson}
import utils.{HttpClient, JsonUtils}
import scala.annotation.retains

object Main {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      printHelp()
      return
    }

    val command = args.toList.mkString(" ")
    println(s"command: $command")

    val response = command match {
      case "login" => Api.login()
      case "sign-up" => Api.signUp()
      case s"sign-up $username" => Api.signUp(username = username)
      case "flow" => Api.getFlows()
      case s"flow $uid" => Api.getFlow(uid = uid)
      case s"delete-flow $uid" => Api.deleteFlow(uid = uid)
      case "user" => Api.getUsers()
      case "project" => Api.getProjects()
      case "group" => Api.getGroups()
      case s"delete-group $uid" => Api.deleteGroup(uid = uid)
      case "flow-run" => Api.getFlowRuns()
      case s"post-flow-run $flowUid" => Api.postFlowRun(flowUid)
      case "help" => {
        printHelp()
        return
      }
      case "setup-data" => {
        Data.setupData()
        return
      }
      case _ => throw IllegalArgumentException(s"Invalid option: $command")
    }

    printResponse(response)
  }
}

def printHelp(): Unit = {
  val helpText =
    """
      |Options:
      |
      |login                                                 Sends login request with default credentials
      |sing-up                                               Creates user with default credentials
      |sing-up [USER_NAME]                                   Creates user with specified USER_NAME
      |user                                                  Get all users
      |project                                               Get all projects
      |group                                                 Get all groups
      |delete-group [UID]                                    Deletes group by its UID
      |flow                                                  Get all flows
      |flow [UID]                                            Get flow by UID
      |delete-flow [UID]                                     Deletes flow by UID
      |flow-run                                              Get all flow runs
      |post-flow-run [FLOW_UID]                              Posts fake run for flow with FLOW_UID
      |setup-data                                            Creates default test data on server
      |help                                                  Print help
      |""".stripMargin

  /*
      |post-flow-run *FLOW_UID* *VER_NAME* *VER_CODE*        Sends flow execution report
   */
  println(helpText)
}

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
