(ns bagian.components.timbre
  (:require
   [bagian.edge.logger :as bgn.edge.logger]
   [com.stuartsierra.component :as c]
   [taoensso.timbre :as timbre :refer [log!]]))

(defn- collect-timbre-appenders
  [component]
  (into {}
        (keep (fn [[k v]]
                (when-let [appenders (:appenders v)]
                  [k appenders])))
        component))

(defrecord TimbreLogger [config settings previous-settings]
  c/Lifecycle
  (start [this]
    (if (some? settings)
      this
      (let [appenders (collect-timbre-appenders this)
            settings' (assoc config :appenders appenders)
            this'     (assoc this :settings settings')]
        (if (:set-root-config? config)
          (let [prev-settings timbre/*config*]
            (timbre/set-config! settings')
            (assoc this' :previous-settings prev-settings))
          this'))))
  (stop [this]
    (when (and (:set-root-config? config) (some? previous-settings))
      (timbre/set-config! previous-settings))
    (assoc this :settings nil :previous-settings nil))

  bgn.edge.logger/Logger
  (-log [_ level ns-str file line id tag data]
    (cond
      (instance? Throwable data)
      (log! level :p (tag) {:config     settings
                            :?err       data
                            :?ns-str    ns-str
                            :?file      file
                            :?line      line
                            :?base-data {:id_ id}})

      (nil? data)
      (log! level :p (tag) {:config     settings
                            :?ns-str    ns-str
                            :?file      file
                            :?line      line
                            :?base-data {:id_ id}})

      :else
      (log! level :p (tag data) {:config     settings
                                 :?ns-str    ns-str
                                 :?file      file
                                 :?line      line
                                 :?base-data {:id_ id}}))))

(defn new-timbre-logger
  [config]
  (map->TimbreLogger {:config config}))
