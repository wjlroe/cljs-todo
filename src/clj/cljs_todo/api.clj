(ns cljs-todo.api
  "The server side of the application. Provides a simple API for
  updating an in-memory database."
  (:use [compojure.core :only (defroutes ANY)]))

(defonce ^:dynamic *task-list* (atom []))

(defonce ^:dynamic *task-id* (atom 10000))

(defn- next-id []
  (swap! *task-id* inc))

(defmulti remote
  "Multimethod to handle incoming API calls. Implementations are
  selected based on the :fn key in the data sent by the client.
  Implementation are called with whatever data struture the client
  sends (which will already have been read into a Clojure value) and
  can return any Clojure value. The value the implementation returns
  will be serialized to a string before being sent back to the client."
  :fn)

(defmethod remote :default [data]
  {:status :error :message "Unknown endpoint."})

(defmethod remote :add-task [data]
  (let [t (assoc (-> data :args :task) :id (next-id))]
    (swap! *task-list* conj t)
    t))

(defmethod remote :update-task [data]
  (let [new-t (-> data :args :task)]
    (swap! *task-list*
           (fn [old-list]
             (let [old-t (first (filter #(= (:id %) (:id new-t)) old-list))]
               (replace {old-t (merge old-t new-t)} old-list))))
    new-t))

(defmethod remote :list-tasks [data]
  {:task-list @*task-list*})

(defroutes remote-routes
  (ANY "/remote" {{data "data"} :params}
       (pr-str
        (remote
         (binding [*read-eval* false]
           (read-string data))))))
