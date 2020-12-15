(ns bagian.components.reitit.core
  (:require
   [com.stuartsierra.component :as c]
   [reitit.ring :as reit.ring]))

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

(defrecord RingRouter [ctor-fn options]
  c/Lifecycle
  (start [this]
    (let [routes   (into [] (comp (map val) (keep :routes)) this)
          options' (get options :options options)
          ctor     (partial ctor-fn routes)
          router   (if (empty? options') (ctor) (ctor options'))]
      (assoc this :router router)))
  (stop [this]
    (assoc this :router nil)))

(defn new-ring-router
  [ctor-fn]
  (map->RingRouter {:ctor-fn ctor-fn}))

(def ^:private default-default-handler
  (reit.ring/routes
    (reit.ring/create-default-handler)
    (reit.ring/redirect-trailing-slash-handler)))

(defrecord RingHandler [ctor-fn router def-handler options]
  c/Lifecycle
  (start [this]
    (let [router'      (:router router)
          def-handler' (or (:handler def-handler def-handler)
                           default-default-handler)
          options'     (:options options options)
          ctor         (partial ctor-fn router' def-handler')
          handler      (if (empty? options') (ctor) (ctor options'))]
      (assoc this :handler handler)))
  (stop [this]
    (assoc this :handler nil)))

(defn new-ring-handler
  [ctor-fn]
  (map->RingHandler {:ctor-fn ctor-fn}))
