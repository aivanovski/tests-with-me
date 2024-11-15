(ns api.api
  (:require [babashka.http-client :as http]
            [cheshire.core :as json])
  (:import java.util.Base64))

(def URL "https://127.0.0.1:8443")
(def TOKEN "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4NDQzIiwiaXNzIjoiaHR0cHM6Ly8wLjAuMC4wOjg0NDMiLCJ1c2VybmFtZSI6ImFkbWluIiwiZXhwIjoxNzM0Mjc5ODAzfQ.4od5izKAoPV-nyRtbr9mPQqWmr52YeOO3q8BVcgtfts")
(def HEADER_CONTENT_TYPE {:content-type "application/json"})
(def HEADER_AUTH {:authorization (str "Bearer " TOKEN)})
(def PRINT_HEADERS true)
(defn to-json [data] (json/encode data))
(defn decode-base64 [str]
  (String. (.decode (Base64/getDecoder) (.getBytes str))))
(defn encode-base64 [str]
  (.encodeToString (Base64/getEncoder) (.getBytes str)))

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

    (print-response response)))

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
     :headers HEADER_AUTH}))

(defn get-flow-by-uid-request
  [uid]
  (request
        {:type :GET
         :endpoint (str "/flow/" uid)
         :headers HEADER_AUTH}))

(defn get-flows-request
  []
  (request
    {:type :GET
     :endpoint "/flow"
     :headers HEADER_AUTH}))

(defn get-projects-request
  []
  (request
    {:type :GET
     :endpoint "/project"
     :headers HEADER_AUTH}))

(defn get-flow-runs-request
  []
  (request
    {:type :GET
     :endpoint "/flow-run"
     :headers HEADER_AUTH}))

(defn get-flow-run-by-uid-request
  [uid]
  (request
    {:type :GET
     :endpoint (str "/flow-run/" uid)
     :headers HEADER_AUTH}))

(defn get-groups-request
  []
  (request
    {:type :GET
     :endpoint "/group"
     :headers HEADER_AUTH}))

(defn update-group-request
  [uid params]
  (request
    {:type :PUT
     :endpoint (str "/group/" uid)
     :headers (merge HEADER_CONTENT_TYPE HEADER_AUTH)
     :body (to-json params)}))

(defn post-flow-run-request
  [flow-uid]
  (request
    {:type :POST
     :endpoint "/flow-run"
     :headers (merge HEADER_CONTENT_TYPE HEADER_AUTH)
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
     :headers (merge HEADER_CONTENT_TYPE HEADER_AUTH)
     :body (to-json
             params)}))

(defn post-group-request
  [name path]
  (request
    {:type :POST
     :endpoint "/group"
     :headers (merge HEADER_CONTENT_TYPE HEADER_AUTH)
     :body (to-json
             {:name name
              :path path})}))

(comment
  (request
    {:type :GET
     :endpoint "/user"
     :headers HEADER_AUTH})

  (post-group-request "Garbage" "KeePassVault")

  (update-group-request
    "0be53378-2abc-4fa2-960f-2f58eb5de00d"
    {:name "Test"
     :parent {:path "KeePassVault/Other"}})
  )
