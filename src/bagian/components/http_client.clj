(ns bagian.components.http-client
  (:require
   [clj-http.client :as http-client]))

(defrecord HttpClient [config])

(defn new-http-client
  [config]
  (map->HttpClient {:config config}))

(defn request
  [{:keys [config]} http-method uri req]
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
    (caller new-uri req)))
