(ns bagian.components.reitit.core
  (:require
   [com.stuartsierra.component :as c]))

(defrecord RingRoutes [routes-fn routes]
  c/Lifecycle
  (start [this]
    (assoc this :routes (routes-fn this)))
  (stop [this]
    (assoc this :routes nil)))

(defn new-ring-routes
  [routes-fn]
  (map->RingRoutes {:routes-fn routes-fn}))

(defrecord RingOptions [options-fn options]
  c/Lifecycle
  (start [this]
    (assoc this :options (options-fn this)))
  (stop [this]
    (assoc this :options nil)))

(defn new-ring-options
  [options-fn]
  (map->RingOptions {:options-fn options-fn}))
