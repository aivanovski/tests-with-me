import utils.printResponse
import scala.io.Source

object Data {

  def setupData(api: ApiClient): Unit = {
    val requests = List(
      () => api.signUp(),

      // Projects
      () => api.postProject(
        Map(
          "name" -> "KeePassVault",
          "packageName" -> "com.ivanovsky.passnotes.automation",
          "description" -> "KeePass client app for Android",
          "imageUrl" -> "https://raw.githubusercontent.com/aivanovski/keepassvault/master/fastlane/metadata/android/en-US/images/icon.png",
          "downloadUrl" -> "https://github.com/aivanovski/keepassvault/releases",
          "siteUrl" -> "https://github.com/aivanovski/keepassvault",
        )
      ),
      () => api.postProject(
        Map(
          "name" -> "F-Droid",
          "packageName" -> "org.fdroid.fdroid",
          "description" -> "Market of FOSS applications",
          "imageUrl" -> "https://gitlab.com/uploads/-/system/project/avatar/36189/ic_launcher.png",
          "downloadUrl" -> "https://f-droid.org",
          "siteUrl" -> "https://f-droid.org",
        )
      ),

      // Groups
      () => api.postGroup(path = "KeePassVault/Root", name = "Screens"),
      () => api.postGroup(path = "KeePassVault/Root", name = "Common"),
      () => api.postGroup(path = "KeePassVault/Root/Screens", name = "About"),
      () => api.postGroup(path = "KeePassVault/Root/Screens", name = "Unlock"),
      () => api.postGroup(path = "KeePassVault/Root/Screens", name = "New Database"),

      // Flows
//      () => api.postFlow(
//        path = "KeePassVault/Root/Common",
//        content = readFile(s"$FLOW_DIRECTORY_PATH/reset-and-setup-basic.kdbx-database.yaml")
//      ),
//      () => api.postFlow(
//        path = "KeePassVault/Root/Screens/Unlock",
//        content = readFile(s"$FLOW_DIRECTORY_PATH/unlock-with-password.yaml")
//      ),
//      () => api.postFlow(
//        path = "KeePassVault/Root/Screens/Unlock",
//        content = readFile(s"$FLOW_DIRECTORY_PATH/unlock-with-key-file.yaml")
//      ),
//      () => api.postFlow(
//        path = "KeePassVault/Root/Screens/Unlock",
//        content = readFile(s"$FLOW_DIRECTORY_PATH/unlock-with-key-file-and-password.yaml")
//      ),
    )

    for (response <- requests) printResponse(response.apply())
  }

  private val FLOW_DIRECTORY_PATH = "$HOME/dev/tests-with-me/flows/keepassvault"

  private def readFile(path: String): String = {
    Source.fromFile(path.replace("$HOME", System.getProperty("user.home")))
      .getLines()
      .mkString("\n")
  }
}
