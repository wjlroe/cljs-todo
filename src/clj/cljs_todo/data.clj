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

          {:db/id (d/tempid :db.part/db)
           :db/ident :task/id
           :db/valueType :db.type/long
           :db/cardinality :db.cardinality/one
           :db.install/_attribute :db.part/db}
          ])

(defn create-db []
  (d/create-database uri)
  (let [conn (d/connect uri)]
    @(d/transact conn schema)))

(defonce ^:dynamic *task-id* (atom 10000))
(defn- next-id [] (swap! *task-id* inc))

(defn- id->eid [id]
  (ffirst
   (d/q '[:find ?t
          :in $ ?i
          :where
          [?t :task/id ?i]] (d/db (d/connect uri)) id)))

(defn save-task [t]
  (let [id (or (:id t)
                 (next-id))
          entity-id (if (:id t)
                      (id->eid (:id t))
                      (d/tempid :db.part/user))]
      @(d/transact
        (d/connect uri)
        (map (fn [[k v]] {:db/id entity-id k v})
             {:task/description (:description t)
              :task/complete (boolean (:complete t))
              :task/id id}))
      (assoc t :id id)))

(defn list-tasks []
  (sort (fn [a b] (compare (:id a) (:id b)))
        (map (fn [[id d c]] {:id id :description d :complete c})
             (d/q '[:find ?i ?d ?c
                    :where
                    [?t :task/id ?i]
                    [?t :task/description ?d]
                    [?t :task/complete ?c]]
                  (d/db (d/connect uri))))))

