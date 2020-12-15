(ns bagian.components.http-client
  (:require
   [bagian.edge.logger :refer [debug]]
   [clj-http.client :as http-client]))

(defrecord HttpClient [config logger])

(defn new-http-client
  [config]
  (map->HttpClient {:config config}))

(defn request
  [{:keys [config logger]} http-method uri req]
  (let [caller  (case http-method
                  :get     http-client/get
                  :head    http-client/head
                  :post    http-client/post
                  :put     http-client/put
                  :delete  http-client/delete
                  :patch   http-client/patch
                  :options http-client/options
                  :copy    http-client/copy
                  :move    http-client/move)
        new-uri (str (:host-uri config) uri)]
    (try
      (let [_    (debug logger ::http-client {:request req})
            resp (caller new-uri req)]
        (debug logger ::http-client {:response resp})
        resp)
      (catch Throwable err
        (debug logger ::http-client err)
        (throw err)))))
