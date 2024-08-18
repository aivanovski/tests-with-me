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
  "login" (api/login-request "admin" "abc123")
  "sign-up" (api/sign-up-request
              "admin1"
              "abc123"
              {:email "example@gmail.com"})
  "user" (api/get-users-request)

  "flow" (if (nil? (second args))
           (api/get-flows-request)
           (api/get-flow-by-uid-request (second-arg args "")))

  "flow-run" (if (nil? (second args))
               (api/get-flow-runs-request)
               (api/get-flow-run-by-uid-request (second-arg args "")))

  "post-flow-run" (send-n-times
                    (Integer/parseInt (second-arg args "1"))
                    (api/post-flow-run-request "b8396d57-f09c-4b61-92a7-967be6edcdd8:4fd72d3f-999c-4603-b133-2933a6d78ff0"))

  "project" (api/get-projects-request)

  "group" (api/get-groups-request)

  nil (println "No arguments were specified"))


(comment

  (api/get-flow-run-by-uid-request "b8396d57-f09c-4b61-92a7-967be6edcdd8:4fd72d3f-999c-4603-b133-2933a6d78ff0:866f7283-cdcd-4669-a7f6-2c6d1b581489")

  )
