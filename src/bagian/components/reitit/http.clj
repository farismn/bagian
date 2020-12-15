(ns bagian.components.reitit.http
  (:require
   [bagian.components.reitit.core :as bgn.c.reit]
   [reitit.http :as reit.http]))

(defn new-ring-routes
  [routes-fn]
  (bgn.c.reit/new-ring-routes routes-fn))

(defn new-ring-options
  [options-fn]
  (bgn.c.reit/new-ring-options options-fn))

(defn new-ring-router
  []
  (bgn.c.reit/new-ring-router reit.http/router))

(defn new-ring-handler
  []
  (bgn.c.reit/new-ring-handler reit.http/ring-handler))
