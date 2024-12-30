#!/usr/bin/env bb

(ns api.cli
  (:require [api.api :as api]))

(defn send-n-times
  [n action]
  (dotimes [index n] action))

(defn second-arg
  [args default]
  (nth args 1 default))

(def args *command-line-args*)

(case (first args)
  "login" (let [response (api/login-request "admin" "abc123")
                has-body (not (empty? (:body response)))]
            (if has-body (api/save-token (api/parse-token response)) ))

  "sign-up" (api/sign-up-request
              (second-arg args "")
              "abc123"
              {:email (str (second-arg args "") "@mail.com")})

  "user" (api/get-users-request)

  "flow" (if (nil? (second args))
           (api/get-flows-request)
           (api/get-flow-by-uid-request (second-arg args "")))

  "delete-flow" (api/delete-flow-by-uid-request (second-arg args ""))

  "flow-run" (if (nil? (second args))
               (api/get-flow-runs-request)
               (api/get-flow-run-by-uid-request (second-arg args "")))

  "post-flow-run" (send-n-times
                    (Integer/parseInt (second-arg args "1"))
                    (api/post-flow-run-request "40693df8-4681-4c58-aae0-64cb4e5ff0bd:2bd4e35a-f153-4d06-898a-c965fb1a575e"))

  "project" (api/get-projects-request)

  "group" (api/get-groups-request)

  "delete-group" (api/delete-group-by-uid-request (second-arg args ""))

  nil (println "No arguments were specified"))


(comment

  (api/get-flow-run-by-uid-request "b8396d57-f09c-4b61-92a7-967be6edcdd8:4fd72d3f-999c-4603-b133-2933a6d78ff0:866f7283-cdcd-4669-a7f6-2c6d1b581489")

  )
