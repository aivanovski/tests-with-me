(ns api.transpiler.transpiler-test
  (:require [api.transpiler.transpiler :as tr]
            [clojure.string :as str]))

(defn trim-indent [s]
  (let [lines (str/split-lines s)
        non-empty-lines (filter #(not (str/blank? %)) lines)
        min-indent (apply min (map #(count (re-find #"^\s*" %)) non-empty-lines))]
    (->> lines
         (drop-while #(str/blank? %))
         (map #(subs % (min min-indent (count %))))
         (str/join "\n"))))

(defn assert-transpile
  [input expected]
  (assert
    (=
      (tr/transpile (trim-indent input))
      (trim-indent expected))))

(assert-transpile
  ; Input
  "
  package com.github.aivanovski.testswithme.web.api.request
  import kotlinx.serialization.Serializable
  @Serializable
  data class LoginRequest(
     val username: String?,
     val password: List<String>
  )"

  ; Result
  "
  package com.github.aivanovski.testswithme.web.api.request
  case class LoginRequest(
     username: Option[String],
     password: List[String]
  )")

(assert-transpile
  ; Input
  "
  package com.github.aivanovski.testswithme.web.api.request
  object Api {
     const val username: String,
     const val password: String
  }"

  ; Result
  "
  package com.github.aivanovski.testswithme.web.api.request
  object Api {
     val username: String,
     val password: String
  }")

(comment
  )
