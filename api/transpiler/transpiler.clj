(ns api.transpiler.transpiler
  (:require [clojure.string :as str])
  (:import [java.util.regex Pattern]))

(def RULES
  (partition
    2
    ["@Serializable" nil
     "import kotlinx.serialization.Serializable" nil
     "const val" "#CONST"
     "val " ""
     "var " ""
     "data class" "case class"
     "#CONST" "val"
     "<" "["
     ">" "]"

     ; Rules for nullable types
     #"([\s]+)([\w]+):\s+(\w+)\?" "%s%s: Option[%s]"
     #"([\s]+)([\w]+):\s+(\w+)\?\," "%s%s: Option[%s],"
     ]))

(defn apply-pattern-rule
  [line pattern replacement]

  (when
    (nil? (re-matches pattern line)))

  (cond
    (nil? (re-matches pattern line)) line
    :else (let
            [values (rest (re-find pattern line))
             result (apply format replacement values)]

             result)))

(defn apply-rule
  [line rule replacement]

  (cond
    (instance? Pattern rule) (apply-pattern-rule line rule replacement)
    :else (let
            [replace-to (if (nil? replacement) "" replacement)
             result (str/replace-first line rule replace-to)]

             (if (not (str/blank? result)) result nil))))

(defn apply-rules
  [line rules]
  (let []
    (reduce
      (fn
        [acc rule]
        (cond
          (nil? acc) nil
          (str/blank? acc) ""
          :else (apply-rule acc (first rule) (second rule))))
      line
      rules)))

(defn transpile
  [content]
  (let
    [lines (str/split content #"\n")
     result
     (->> lines
          ; (#(do (println (str "before: " %)) %))
          (map #(apply-rules % RULES))
          ; (#(do (println (str "after: " (str/join "," %))) %))
          (filter #(not (nil? %)))) ]

    (str/join "\n" result)))

(comment
  )
