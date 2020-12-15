(ns bagian.components.hikari-cp
  (:require
   [bagian.edge.logger :refer [info]]
   [com.stuartsierra.component :as c]
   [hikari-cp.core :refer [make-datasource close-datasource]])
  (:import
   [javax.sql DataSource]
   [net.ttddyy.dsproxy QueryInfo]
   [net.ttddyy.dsproxy.support ProxyDataSourceBuilder]
   [net.ttddyy.dsproxy.listener QueryExecutionListener]))

(defn- query-parameter-lists
  [^QueryInfo query-info]
  (into []
        (map (fn [params]
               (->> params
                    (into [] (map (memfn getArgs)))
                    (sort-by #(aget % 0))
                    (into [] (map #(aget % 1))))))
        (.getParametersList query-info)))

(defn- logged-query
  [^QueryInfo query-info]
  (let [query  (.getQuery query-info)
        params (query-parameter-lists query-info)]
    (into [query] (if (= (count params) 1) (first params) params))))

(defn- logging-listener
  [logger]
  (reify QueryExecutionListener
    (beforeQuery [_ _ _])
    (afterQuery [_ exec-info query-infos]
      (let [elapsed (.getElapsedTime exec-info)
            queries (into [] (map logged-query) query-infos)]
        (if (= (count queries) 1)
          (info logger ::query
            {:query (first queries) :elapsed elapsed})
          (info logger ::batch-query
            {:queries queries :elapsed elapsed}))))))

(defn- wrap-with-logger
  [^DataSource datasource logger]
  (.. ProxyDataSourceBuilder
      (create datasource)
      (listener (logging-listener logger))
      (build)))

(defn- unwrap-logger
  [^DataSource datasource]
  (.unwrap datasource DataSource))

(defn- sanitize-pool-spec
  [pool-spec]
  (dissoc pool-spec :password))

(defrecord HikariCP [pool-spec logger datasource]
  c/Lifecycle
  (start [this]
    (if (some? datasource)
      this
      (let [_           (info logger ::hikari-cp
                          {:bagian/lifecycle :bagian.lifecycle/starting
                           :pool-sec         (sanitize-pool-spec pool-spec)})
            datasource' (cond-> (make-datasource pool-spec)
                          (some? logger)
                          (wrap-with-logger logger))]
        (assoc this :datasource datasource'))))
  (stop [this]
    (when (some? datasource)
      (info logger ::hikari-cp
        {:bagian/lifecycle :bagian.lifecycle/stopping})
      (close-datasource (unwrap-logger datasource)))
    (assoc this :datasource nil)))

(defn new-hikari-cp
  [pool-spec]
  (map->HikariCP {:pool-spec pool-spec}))
