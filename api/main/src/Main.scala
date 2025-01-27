
import utils.printResponse

object Main {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      printHelp()
      return
    }

    val url = if (args.contains("-p") || args.contains("--prod")) ServerUrl.Prod else ServerUrl.Local
    val api = ApiClient.build(url)

    val command = args.toList
      .filter(line => !line.startsWith("-"))
      .mkString(" ")

    println(s"Command: $command")
    println(s"Using url: ${url.value}")

    val response = command match {
      case "login" => api.login()
      case "sign-up" => api.signUp()
      case s"sign-up $username" => api.signUp(username = username)
      case "flow" => api.getFlows()
      case s"flow $uid" => api.getFlow(uid = uid)
      case s"delete-flow $uid" => api.deleteFlow(uid = uid)
      case "user" => api.getUsers()
      case "project" => api.getProjects()
      case "group" => api.getGroups()
      case s"delete-group $uid" => api.deleteGroup(uid = uid)
      case "flow-run" => api.getFlowRuns()
      case s"post-flow-run $flowUid $timesStr" => {
        val times = timesStr.toIntOption.get
        for (i <- Range.inclusive(1, times)) {
          println(s"Request number: $i")
          printResponse(api.postFlowRun(flowUid))
        }
        return
      }
      case s"post-flow-run $flowUid" => api.postFlowRun(flowUid)
      case "help" => {
        printHelp()
        return
      }
      case "setup-data" => {
        Data.setupData(api)
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
      |--prod, -p                                            Use prod url to run the requests
      |
      |Commands:
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
      |post-flow-run [FLOW_UID] [TIMES]                      Posts fake run specified number of times
      |setup-data                                            Creates default test data on server
      |help                                                  Print help
      |""".stripMargin

  /*
      |post-flow-run *FLOW_UID* *VER_NAME* *VER_CODE*        Sends flow execution report
   */
  println(helpText)
}
