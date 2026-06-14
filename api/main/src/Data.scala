import model.User
import utils.printResponse
import utils.readFileBytes

import java.io.{ByteArrayInputStream}
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
          "siteUrl" -> "https://github.com/aivanovski/keepassvault"
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

      // Setup groups
      () => api.postGroup("KeePassVault/Root", "Common"),
      () => api.postGroup("KeePassVault/Root", "Screens"),
      () => api.postGroup("KeePassVault/Root/Screens", "Unlock"),
      () => api.postGroup("KeePassVault/Root/Screens", "Group"),
      () => api.postGroup("KeePassVault/Root/Screens", "New Database"),

      // Upload flows
      () => api.postFlow("KeePassVault/Root/Common", Flows.ResetData),
      () => api.postFlow("KeePassVault/Root/Common", Flows.SetupBasicKdbxDatabase),
      () => api.postFlow("KeePassVault/Root/Screens/Unlock", Flows.UnlockDatabaseWithPassword),
    )

    for (response <- requests) printResponse(response.apply())
  }

  def readDefaultUser(): User = {
    val debugPropertiesPath = "$HOME/dev/tests-with-me/dev-data/debug.properties"

    val properties = new Properties()
    properties.load(ByteArrayInputStream(readFileBytes(debugPropertiesPath)))

    val username = properties.getProperty("username")
    val password = properties.getProperty("password")
    val email = properties.getProperty("email")

    if (username.isEmpty || password.isEmpty || email.isEmpty) {
      throw IllegalStateException("Failed to read debug credentials")
    }

    User(username, password, email)
  }
}
