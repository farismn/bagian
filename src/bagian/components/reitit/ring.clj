(ns bagian.components.reitit.ring
  (:require
   [bagian.components.reitit.core :as bgn.c.reit]
   [com.stuartsierra.component :as c]
   [reitit.ring :as reit.rg]))

(defn new-ring-routes
  [routes-fn]
  (bgn.c.reit/new-ring-routes routes-fn))

(defn new-ring-options
  [options-fn]
  (bgn.c.reit/new-ring-options options-fn))

(defrecord RingRouter [options]
  c/Lifecycle
  (start [this]
    (let [routes   (into [] (comp (map val) (keep :routes)) this)
          options' (get options :options options)
          ctor     (partial reit.rg/router routes)
          router   (if (empty? options') (ctor) (ctor options'))]
      (assoc this :router router)))
  (stop [this]
    (assoc this :router nil)))

(defn new-ring-router
  []
  (map->RingRouter {}))

(def ^:private default-default-handler
  (reit.rg/routes
    (reit.rg/create-default-handler)
    (reit.rg/redirect-trailing-slash-handler)))

(defrecord RingHandler [router def-handler options]
  c/Lifecycle
  (start [this]
    (let [router'      (:router router)
          def-handler' (or (:handler def-handler def-handler)
                           default-default-handler)
          options'     (:options options options)
          ctor         (partial reit.rg/ring-handler router' def-handler')
          handler      (if (empty? options') (ctor) (ctor options'))]
      (assoc this :handler handler)))
  (stop [this]
    (assoc this :handler nil)))

(defn new-ring-handler
  []
  (map->RingHandler {}))
