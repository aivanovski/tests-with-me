#!/usr/bin/env bb

(ns api.cli
  (:require [api.api :as api]
            [api.data :as data]))

(defn second-arg
  [args default]
  (nth args 1 default))

(defn third-arg
  [args default]
  (nth args 2 default))

(defn fourth-arg
  [args default]
  (nth args 3 default))

(defn print-help
  []
  (let [lines ["Options:"
               ""
               "login                                                 Sends login request with default credentials"
               "sing-up                                               Creates user with default credentials"
               "sing-up *USER_NAME*                                   Creates user with *USER_NAME*"
               "user                                                  Get all users"
               "project                                               Get all projects"
               "group                                                 Get all groups"
               "delete-group *GROUP_UID*                              Deletes group by uid"
               "flow                                                  Get all flows"
               "flow *FLOW_UID*                                       Get flow by uid"
               "delete-flow *FLOW_UID*                                Deletes flow by uid"
               "post-flow-run *FLOW_UID* *VER_NAME* *VER_CODE*        Sends flow execution report"
               "setup-data                                            Creates default test data on server"
               ""
               ]]
    (doseq [line lines]
      (println line))))

(def args *command-line-args*)

(case (first args)
  "login" (let [response (api/login-request "admin" "abc123")
                has-body (not (empty? (:body response)))]
            (if has-body (api/save-token (api/parse-token response)) ))

  "sign-up" (let [username (second-arg args "admin")
                  email (str username "@mail.com")]
              (api/sign-up-request
                username
                "abc123"
                {:email email}))

  "user" (api/get-users-request)

  "flow" (if (nil? (second args))
           (api/get-flows-request)
           (api/get-flow-by-uid-request (second-arg args "")))

  "delete-flow" (api/delete-flow-by-uid-request (second-arg args ""))

  "flow-run" (if (nil? (second args))
               (api/get-flow-runs-request)
               (api/get-flow-run-by-uid-request (second-arg args "")))

  "post-flow-run" (let [flow-uid (second-arg args "")
                        version-name (third-arg args "1.7.0")
                        version-code (fourth-arg args "10700")]
                    (api/post-flow-run-request
                      flow-uid
                      {:version-name version-name
                       :version-code version-code}))

  "project" (api/get-projects-request)

  "group" (api/get-groups-request)

  "delete-group" (api/delete-group-by-uid-request (second-arg args ""))

  "setup-data" (data/setup-data)

  nil (print-help))


(comment

  (api/get-flow-run-by-uid-request "b8396d57-f09c-4b61-92a7-967be6edcdd8:4fd72d3f-999c-4603-b133-2933a6d78ff0:866f7283-cdcd-4669-a7f6-2c6d1b581489")

  )
