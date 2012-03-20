(ns cljs-todo.data
  (:use [datomic.api :only (q db transact) :as d]))

(def ^:private
  uri "datomic:mem://todo")
(def
  ^:private
  ^{:doc "The definition of the transaction that will create our schema entities
          Could be stored in a file, but left it here since it was so short."}
  schema-tx [
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

(defonce ^:private
  ^{:doc "Create the db and retrieve a connection to it"}
  conn (do (d/create-database uri)
           (let [conn (d/connect uri)]
             @(transact conn schema-tx)
             conn)))

;; HACK-ISH I can't currently get the entity id back from the
;; transaction to use as an identifier, so I am generating my own for
;; now and handling the mapping between the id and the entity id
;; manually.  There may be a larger-scale change that makes this
;; unnecessary, but to preserve the current interface I need something
;; that passes back the newly added task with a unique id included.
;;
;; The Datomic team has a ticket for providing access to the generated
;; entity ids:
;;   https://groups.google.com/forum/#!topic/datomic/YB3LaRlRp7I
(defonce ^:dynamic *task-id* (atom 10000))
(defn- next-id [] (swap! *task-id* inc))

(defn- id->eid
  "Find the datomic entity add for the given app generated id"
  [id]
  (ffirst
   (q '[:find ?t
        :in $ ?i
        :where
        [?t :task/id ?i]] (db conn) id)))

(defn- eid
  "Determine the appropriate datomic entity id for the task.
   New tasks for need a temporary datomic id and existing tasks
   will need their app generated id mapped to their existing
   datomic entity id"
  [t]
  (if-let [id (:id t)]
    (id->eid id)
    (d/tempid :db.part/user)))

(defn- app-id
  "Returns the unique id create by this application for a task.
   If it does not yet exist (a new task) generate a new id and
   return that."
  [t]
  (or (:id t) (next-id)))

(defn save-task
  "Create or update a task entity"
  [t]
  (let [id (app-id t)
        entity-id (eid t)]
    @(transact
      conn
      (map (fn [[k v]] {:db/id entity-id k v})
           {:task/description (:description t)
            :task/complete (boolean (:complete t))
            :task/id id}))
    (assoc t :id id)))

(defn list-tasks []
  (sort-by :id
           (map (fn [[id d c]] {:id id :description d :complete c})
                (q '[:find ?i ?d ?c
                     :where
                     [?t :task/id ?i]
                     [?t :task/description ?d]
                     [?t :task/complete ?c]]
                   (db conn)))))

