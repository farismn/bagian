(ns bagian.components.aleph
  (:require
   [aleph.http :refer [start-server]]
   [bagian.edge.logger :refer [info]]
   [com.stuartsierra.component :as c]))

(defrecord HttpServer [config handler logger server]
  c/Lifecycle
  (start [this]
    (if (some? server)
      this
      (let [handler' (:handler handler handler)
            _        (info logger ::http-server
                       {:bagian/lifecycle :bagian.lifecycle/starting
                        :config           config})
            server'  (start-server handler' config)]
        (assoc this :server server'))))
  (stop [this]
    (when (some? server)
      (info logger ::http-server
        {:bagian/lifecycle :bagian.lifecycle/stopping})
      (.close server))
    (assoc this :server nil)))

(defn new-http-server
  [config]
  (map->HttpServer {:config config}))
