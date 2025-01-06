#!/usr/bin/env bb

(ns api.data
  (:require [api.api :as api]))

(defn post-flow-file
  [file path]
  (let [content (api/encode-base64 (slurp file))]
    (println (str "Uploading flow: " file))
    (api/request
      {:type :POST
       :endpoint "/flow"
       :headers (merge api/HEADER_CONTENT_TYPE (api/auth-header))
       :body (api/to-json
               {:path path
                :base64Content content})})))

(defn setup-user
  []
  (println "Create user")
  (api/sign-up-request
    "admin"
    "abc123"
    {:email "example@gmail.com"})

  (api/save-token
    (api/parse-token
      (api/login-request "admin" "abc123"))))

(defn setup-projects
  []
  (println "Create project: KeePassVault")
  (api/post-project-request
    {:name "KeePassVault"
     :packageName "com.ivanovsky.passnotes.automation"
     :description "KeePass client app for Android"
     :imageUrl "https://raw.githubusercontent.com/aivanovski/keepassvault/master/fastlane/metadata/android/en-US/images/icon.png"
     :downloadUrl "https://github.com/aivanovski/keepassvault/releases"
     :siteUrl "https://github.com/aivanovski/keepassvault"})

  (println "Create project: F-Droid")
  (api/post-project-request
    {:name "F-Droid"
     :packageName "org.fdroid.fdroid"
     :description "FOSS apps"
     :imageUrl "https://gitlab.com/uploads/-/system/project/avatar/36189/ic_launcher.png"
     :downloadUrl "https://f-droid.org"
     :siteUrl "https://f-droid.org"}))

(defn setup-groups
  []
  (println "Create group: KeePassVault/Root/Screens")
  (api/post-group-request
    "Screens"
    "KeePassVault/Root")

  (println "Create group: KeePassVault/Root/Common")
  (api/post-group-request
    "Common"
    "KeePassVault/Root")

  (println "Create group: KeePassVault/Root/Screens/About")
  (api/post-group-request
    "About"
    "KeePassVault/Root/Screens")

  (println "Create group: KeePassVault/Root/Screens/New Database")
  (api/post-group-request
    "New Database"
    "KeePassVault/Root/Screens")

  (println "Create group: KeePassVault/Root/Screens/Unlock")
  (api/post-group-request
    "Unlock"
    "KeePassVault/Root/Screens"))

(defn setup-flows
  []
  (post-flow-file
    "flows/keepassvault/reset-and-setup-passwords-database.yaml"
    "KeePassVault/Root/Common")

  (post-flow-file
    "flows/keepassvault/unlock-with-key-file-and-password.yaml"
    "KeePassVault/Root/Screens/Unlock")

  (post-flow-file
    "flows/keepassvault/unlock-with-password.yaml"
    "KeePassVault/Root/Screens/Unlock")

  (post-flow-file
    "flows/keepassvault/unlock-with-key-file.yaml"
    "KeePassVault/Root/Screens/Unlock"))

(defn setup-data
  []
  (setup-user)
  (setup-projects)
  (setup-groups)
  (setup-flows))

(comment
  )

