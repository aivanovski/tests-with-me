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
          "repositoryUrl" -> "https://github.com/aivanovski/keepassvault-tests.git"
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
