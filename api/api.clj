(ns api.api
  (:require [babashka.http-client :as http]
            [cheshire.core :as json]
            [clojure.java.io :as io])
  (:import java.util.Base64))

(def URL "https://127.0.0.1:8443")
(def HEADER_CONTENT_TYPE {:content-type "application/json"})
(def PRINT_HEADERS true)
(defn to-json [data] (json/encode data))
(defn decode-base64 [str]
  (String. (.decode (Base64/getDecoder) (.getBytes str))))
(defn encode-base64 [str]
  (.encodeToString (Base64/getEncoder) (.getBytes str)))

(def token-file (str (System/getProperty "user.home") "/.cache/tests-with-me/token"))

(defn save-token [content]
  (let [cache-dir (.getParent (io/file token-file))]
    (when-not (.exists (io/file cache-dir))
      (.mkdirs (io/file cache-dir))))
  (with-open [writer (io/writer token-file)]
    (.write writer content)))

(defn load-token []
  (if (.exists (io/file token-file))
    (slurp token-file)
    ""))

(defn parse-token
  [response]
  (:token
    (json/parse-string (:body response) true)
    ""))

(defn auth-header [] {:authorization (str "Bearer " (load-token))})


(defn http-client
  []
  (http/client
    {:ssl-context (http/->SSLContext {:insecure true})}))

(defn print-response
  [response]
  (let [has-body (not (empty? (:body response)))
        headers (:headers response {})
        data (json/parse-string (:body response) true)]

    (when PRINT_HEADERS
      (println (format "HEADERS: %s" (count headers)))
      (doseq [key (keys headers)]
        (println (format "%s: %s" key (get headers key)))))

    (if has-body
      (println (json/generate-string data {:pretty true}))
      (println response))
    ))

(defn request
  [params]
  (let [other-params (dissoc params :type)
        url (str URL (:endpoint params))
        response
        (case (:type params)

          :GET (http/get
                 url
                 (merge
                   other-params
                   {:throw false
                    :method :get
                    :client (http-client)}))

          :POST (http/post
                  url
                  (merge
                    other-params
                    {:throw false
                     :method :post
                     :client (http-client)}))

          :PUT (http/put
                 url
                 (merge
                   other-params
                   {:throw false
                    :method :put
                    :client (http-client)})))]

    (print-response response)
    response))

(defn sign-up-request
  [username password params]
  (request
    {:type :POST
     :endpoint "/sign-up"
     :headers HEADER_CONTENT_TYPE
     :body (to-json
             {:username username
              :password password
              :email (:email params)})}))

(defn login-request
  [username password]
  (request
    {:type :POST
     :endpoint "/login"
     :headers HEADER_CONTENT_TYPE
     :body (to-json {:username username, :password password})}))

(defn get-users-request
  []
  (request
    {:type :GET
     :endpoint "/user"
     :headers (auth-header)}))

(defn get-flow-by-uid-request
  [uid]
  (request
        {:type :GET
         :endpoint (str "/flow/" uid)
         :headers (auth-header)}))

(defn get-flows-request
  []
  (request
    {:type :GET
     :endpoint "/flow"
     :headers (auth-header)}))

(defn get-projects-request
  []
  (request
    {:type :GET
     :endpoint "/project"
     :headers (auth-header)}))

(defn get-flow-runs-request
  []
  (request
    {:type :GET
     :endpoint "/flow-run"
     :headers (auth-header)}))

(defn get-flow-run-by-uid-request
  [uid]
  (request
    {:type :GET
     :endpoint (str "/flow-run/" uid)
     :headers (auth-header)}))

(defn get-groups-request
  []
  (request
    {:type :GET
     :endpoint "/group"
     :headers (auth-header)}))

(defn update-group-request
  [uid params]
  (request
    {:type :PUT
     :endpoint (str "/group/" uid)
     :headers (merge HEADER_CONTENT_TYPE (auth-header))
     :body (to-json params)}))

(defn post-flow-run-request
  [flow-uid]
  (request
    {:type :POST
     :endpoint "/flow-run"
     :headers (merge HEADER_CONTENT_TYPE (auth-header))
     :body (to-json
             {:flowId flow-uid
              :durationInMillis 360
              :isSuccess true
              :result "Either.Right(Unit)"
              :appVersionName "1.7.0"
              :appVersionCode "10700"
              :reportBase64Content (encode-base64 "[Step 1] Finished")})}))

(defn post-project-request
  [params]
  (request
    {:type :POST
     :endpoint "/project"
     :headers (merge HEADER_CONTENT_TYPE (auth-header))
     :body (to-json
             params)}))

(defn post-group-request
  [name path]
  (request
    {:type :POST
     :endpoint "/group"
     :headers (merge HEADER_CONTENT_TYPE (auth-header))
     :body (to-json
             {:name name
              :path path})}))

(comment
  (request
    {:type :GET
     :endpoint "/user"
     :headers (auth-header)})

  (post-group-request "Garbage" "KeePassVault")

  (update-group-request
    "0be53378-2abc-4fa2-960f-2f58eb5de00d"
    {:name "Test"
     :parent {:path "KeePassVault/Other"}})
  )
