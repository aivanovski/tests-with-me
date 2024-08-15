#!/usr/bin/env bb

(ns api.transpiler.cli
  (:require [babashka.fs :as fs]
            [api.transpiler.transpiler :as tr]
            [clojure.string :as str])
  (:import [java.io File]))

(def INPUT_PATH "./web-api/src/main")
(def OUTPUT_PATH "./web-api-scala/src/main")
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
  [segments]
  (let [scala-segments {:project "web-api-scala"
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

(defn transpile-file
  [src-path]
  (let [src-segments (parse-input-path src-path)
        dst-path (format-output-path src-segments)
        dst-parent (.toString (.getParent (File. dst-path)))]

    (println (str "parent: " dst-parent))
    (println (format "output into: %s" dst-path))

    (when (not (fs/exists? dst-parent)) (fs/create-dirs dst-parent))

    (let [src-content (String. (fs/read-all-bytes src-path))
          dst-content (tr/transpile src-content)]
      (fs/write-bytes dst-path (.getBytes dst-content)))
    ))

(defn transpile-sources
  [src-root dst-root]
  (let [src-files (fs/glob src-root "**{.kt}")]

    (doseq [src-file src-files]

      (println (format "transpiling: %s" src-file))
      (transpile-file (str src-file)))))

(comment

  (transpile-sources INPUT_PATH OUTPUT_PATH)

  )
