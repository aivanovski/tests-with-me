#!/usr/bin/env bb

(ns api.transpiler.cli
  (:require [babashka.fs :as fs]
            [api.transpiler.transpiler :as tr]
            [clojure.string :as str])
  (:import [java.io File]))

(def INPUT_PATH "web-api")
(def OUTPUT_PATH "web-api-scala/shared")
(def INPUT_PATTERN #"([\w-_]+)/([\w\/-_]+)/(java)/([\w\/-_]+).(kt)")

(defn parse-input-path
  [path]

  (when
    (nil? (re-matches INPUT_PATTERN path))
    (throw (Exception. (str "Invalid path: " path))))

  (zipmap
    [:project :source-set :language :path :extension]
    (rest
      (re-find INPUT_PATTERN path))))

(defn format-output-path
  [segments output-root-path]
  (let [scala-segments {:project output-root-path
                        :language "scala"
                        :extension "scala"}
        merged (merge segments scala-segments)]

    (format
      "%s/%s/%s/%s.%s"
      (:project merged)
      (:source-set merged)
      (:language merged)
      (:path merged)
      (:extension merged))))

(defn read-file
  [path]
  (if (fs/exists? path)
    (String. (fs/read-all-bytes path))
    nil))

(defn transpile-file
  [src-path dst-root-path]
  (let [src-segments (parse-input-path src-path)
        dst-path (format-output-path src-segments dst-root-path)
        dst-name (fs/file-name dst-path)
        dst-parent (.toString (.getParent (File. dst-path)))]

    (when (not (fs/exists? dst-parent)) (fs/create-dirs dst-parent))

    (let [src-content (read-file src-path)
          curr-dst-content (read-file dst-path)
          dst-content (tr/transpile src-content)]

      (if (not= dst-content curr-dst-content)
        (do
         (println
           (format
             "Write to: %s %s bytes"
             dst-name
             (count dst-content)))

         (fs/write-bytes dst-path (.getBytes dst-content)))
        (println (format "Skip %s" dst-name))))

    dst-path))

(defn list-files
  [root file-pattern]
  (->> (fs/glob root file-pattern)
       (map #(str %))))

(defn transpile-sources
  [src-root dst-root]
  (let
    [src-files (list-files src-root "**{.kt}")
     existing-dst-files (list-files dst-root "**{.scala}")
     dst-files (->> src-files
                    (map #(transpile-file % dst-root))
                    (set))]

    (doseq [file existing-dst-files]
      (when (not (contains? dst-files file))
        (println (format "Remove %s" file))
        (fs/delete file)))
    ))

(transpile-sources INPUT_PATH OUTPUT_PATH)

(comment

  (transpile-sources INPUT_PATH OUTPUT_PATH)
  )