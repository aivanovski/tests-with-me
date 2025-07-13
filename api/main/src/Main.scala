
import utils.printResponse
import utils.readFile

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

    val user = Data.readDefaultUser()

    val response = command match {
      case "login" => api.login(user.username, user.password)
      case "sign-up" => api.signUp(user.username, user.password, user.email)
      case "flow" => api.getFlows()
      case s"flow $uid" => api.getFlow(uid = uid)
      case s"post-flow $path $file" => api.postFlow(path = path, content = readFile(file))
      case s"put-flow $uid $file" => api.putFlow(flowUid = uid, content = readFile(file))
      case s"delete-flow $uid" => api.deleteFlow(uid = uid)
      case "user" => api.getUsers()
      case "project" => api.getProjects()
      case s"request-project-sync $uid" => api.requestProjectSync(uid = uid)
      case "group" => api.getGroups()
      case s"delete-group $uid" => api.deleteGroup(uid = uid)
      case "flow-run" => api.getFlowRuns()
      case s"flow-run $flowRunUid" => api.getFlowRun(flowRunUid)
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
      |user                                                  Get all users
      |project                                               Get all projects
      |request-project-sync [UID]                            Request project to sync flows with linked GitHub repository
      |group                                                 Get all groups
      |delete-group [UID]                                    Deletes group by its UID
      |flow                                                  Get all flows
      |flow [UID]                                            Get flow by UID
      |post-flow [PATH] [FILE]                               Creates new flow at project and group specified in PATH
      |put-flow [UID] [FILE]                                 Updates flow by UID
      |delete-flow [UID]                                     Deletes flow by UID
      |flow-run                                              Get all flow runs
      |flow-run [UID]                                        Get flow run by UID
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
