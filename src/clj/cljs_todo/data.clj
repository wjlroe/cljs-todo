(ns cljs-todo.data
  (:require [datomic.api :as d]))

;; TODO Figure out where to pass this in.
;; Should it be a param to create-db and the fn will initialize a
;; namespace private var?
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

(defn- att-add [id [k v]]
  {:db/id id k v})

(defn create-task [t]
  @(d/transact
    (d/connect uri)
    (let [id (d/tempid :db.part/user)]
      (map #(att-add id %)
           {:task/description (:description t)
            :task/complete (boolean (:complete t))}))))

(defn list-tasks []
  (map (fn [[d c]] {:description d :complete c})
       (d/q '[:find ?d ?c
              :where
              [?t :task/description ?d]
              [?t :task/complete ?c]]
            (d/db (d/connect uri)))))

