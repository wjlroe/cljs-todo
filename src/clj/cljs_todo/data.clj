(ns cljs-todo.data
  (:require [datomic.api :as d]))

(def ^:private
  uri "datomic:mem://todo")
(def ^:private
  schema [
          {:db/id (d/tempid :db.part/db)
           :db/ident :task/description
           :db/valueType :db.type/string
           :db/cardinality :db.cardinality/one
           :db/fulltext true
           :db.install/_attribute :db.part/db}

          {:db/id (d/tempid :db.part/db)
           :db/ident :task/complete
           :db/valueType :db.type/boolean
           :db/cardinality :db.cardinality/one
           :db.install/_attribute :db.part/db}
          ])

(defn create-db []
  (d/create-database uri)
  (let [conn (d/connect uri)]
    @(d/transact conn schema)))

