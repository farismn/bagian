(ns bagian.components.reitit.ring
  (:require
   [bagian.components.reitit.core :as bgn.c.reit]
   [reitit.ring :as reit.ring]))

(defn new-ring-routes
  [routes-fn]
  (bgn.c.reit/new-ring-routes routes-fn))

(defn new-ring-options
  [options-fn]
  (bgn.c.reit/new-ring-options options-fn))

(defn new-ring-router
  []
  (bgn.c.reit/new-ring-router reit.ring/router))

(defn new-ring-handler
  []
  (bgn.c.reit/new-ring-handler reit.ring/ring-handler))
