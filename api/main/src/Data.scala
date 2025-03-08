import model.User
import utils.printResponse

import java.io.FileInputStream
import java.util.Properties

object Data {

  def setupData(api: ApiClient): Unit = {
    val user = readDefaultUser()

    val requests = List(
      () => api.signUp(
        username = user.username,
        password = user.password,
        email = user.email
      ),

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

  def readDefaultUser(): User = {
    val debugPropertiesPath = "$HOME/dev/tests-with-me/data/debug.properties"
      .replace("$HOME", System.getProperty("user.home"))

    val properties = new Properties()
    val inputStream = new FileInputStream(debugPropertiesPath)
    properties.load(inputStream)
    inputStream.close()

    val username = properties.getProperty("username")
    val password = properties.getProperty("password")
    val email = properties.getProperty("email")

    if (username.isEmpty || password.isEmpty || email.isEmpty) {
      throw IllegalStateException("Failed to read debug credentials")
    }

    User(username, password, email)
  }
}
