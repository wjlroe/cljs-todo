(ns cljs-todo.data
  (:require [datomic.api :as d]))

;; TODO Figure out where to pass this in.
;; Should it be a param to create-db and the fn will initialize a
;; namespace private var?
;;
;; It also maybe that I should just initialize a connection at
;; start up and save that.
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

(defn save-task [t]
  @(d/transact
    (d/connect uri)
    (let [id (or (:id t)
                 (d/tempid :db.part/user))]
      (map (fn [[k v]] {:db/id id k v})
           {:task/description (:description t)
            :task/complete (boolean (:complete t))}))))

(defn list-tasks []
  (map (fn [[id d c]] {:id id :description d :complete c})
       (d/q '[:find ?t ?d ?c
              :where
              [?t :task/description ?d]
              [?t :task/complete ?c]]
            (d/db (d/connect uri)))))

