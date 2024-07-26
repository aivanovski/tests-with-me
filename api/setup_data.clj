#!/usr/bin/env bb

(ns api.setup-data
  (:require [api.api :as api]))

(defn post-flow-file
  [file path]
  (let [content (api/encode-base64 (slurp file))]
    (println (str "Uploading flow: " file))
    (api/request 
      {:type :POST
       :endpoint "/flow"
       :headers (merge api/HEADER_CONTENT_TYPE api/HEADER_AUTH)
       :body (api/to-json
               {:path path
                :base64Content content})})))

;; User
(println "Creating user")
(api/sign-up-request
  "admin"
  "abc123"
  {:email "example@gmail.com"})

;; Projeects
(println "Creaing project: KeePassVault")
(api/post-project-request 
  {:name "KeePassVault"
   :packageName "com.ivanovsky.passnotes"
   :description "KeePass client app for Android"
   :imageUrl "https://raw.githubusercontent.com/aivanovski/keepassvault/master/fastlane/metadata/android/en-US/images/icon.png"
   :downloadUrl "https://github.com/aivanovski/keepassvault/releases"
   :siteUrl "https://github.com/aivanovski/keepassvault"})

(println "Creaing project: F-Droid")
(api/post-project-request 
  {:name "F-Droid"
   :packageName "org.fdroid.fdroid"
   :description "FOSS apps"
   :imageUrl "https://gitlab.com/uploads/-/system/project/avatar/36189/ic_launcher.png"
   :downloadUrl "https://f-droid.org"
   :siteUrl "https://f-droid.org"})

;; Groups
(println "Creaing group: KeePassVault/Screens")
(api/post-group-request
  "Screens"
  "KeePassVault")

(println "Creaing group: KeePassVault/Screens/About")
(api/post-group-request
  "About"
  "KeePassVault/Screens")

(println "Creaing group: KeePassVault/Screens/New Database")
(api/post-group-request
  "New Database"
  "KeePassVault/Screens")

(println "Creaing group: KeePassVault/Screens/Unlock")
(api/post-group-request
  "Unlock"
  "KeePassVault/Screens")

;; Flows
(post-flow-file
  "flows/keepassvault/about_navigate-back.yaml"
  "KeePassVault/Screens/About")

(post-flow-file
  "flows/keepassvault/about_open-screen.yaml"
  "KeePassVault/Screens/About")

(post-flow-file
  "flows/keepassvault/about_feedback-url-should-be-shown.yaml"
  "KeePassVault/Screens/About")

(post-flow-file
  "flows/keepassvault/failing.yaml"
  "KeePassVault/Screens/About")

(post-flow-file
  "flows/keepassvault/new_db_create-new.yaml"
  "KeePassVault/Screens/New Database")

(post-flow-file
  "flows/keepassvault/unlock_open-database.yaml"
  "KeePassVault/Screens/Unlock")

(post-flow-file
  "flows/keepassvault/unlock_remove-file.yaml"
  "KeePassVault/Screens/Unlock")

(post-flow-file
  "flows/keepassvault/all.yaml"
  "KeePassVault")

(comment
  )

