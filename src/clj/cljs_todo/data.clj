(ns cljs-todo.data
  (:require [datomic.api :as d]))

;; TODO Figure out/where how to pass in the uri and initialize the
;; connection.  We don't need a 'new' connection for each operation
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


;; HACKISH I can't currently get the entity id back from the
;; transaction to use as an identifier, so I am generating my own for
;; now and handling the mapping between the id and the entity id
;; manually.  There may be a larger-scale change that makes this
;; unnecessary, but to preserve the current interface I need something
;; that passes back the newly added task with a unique id included.
(defonce ^:dynamic *task-id* (atom 10000))
(defn- next-id [] (swap! *task-id* inc))

(defn- id->eid [id]
  (ffirst
   (d/q '[:find ?t
          :in $ ?i
          :where
          [?t :task/id ?i]] (d/db (d/connect uri)) id)))

(defn- eid [t]
  (if-let [id (:id t)]
    (id->eid id)
    (d/tempid :db.part/user)))

(defn save-task [t]
  (let [id (or (:id t) (next-id))
        entity-id (eid t)]
    @(d/transact
      (d/connect uri)
      (map (fn [[k v]] {:db/id entity-id k v})
           {:task/description (:description t)
            :task/complete (boolean (:complete t))
            :task/id id}))
    (assoc t :id id)))

(defn list-tasks []
  (sort-by :id
           (map (fn [[id d c]] {:id id :description d :complete c})
                (d/q '[:find ?i ?d ?c
                       :where
                       [?t :task/id ?i]
                       [?t :task/description ?d]
                       [?t :task/complete ?c]]
                     (d/db (d/connect uri))))))

