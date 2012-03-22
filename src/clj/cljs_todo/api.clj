(ns cljs-todo.api
  "The server side of the application. Provides a simple API for
  updating an in-memory database."
  (:use [compojure.core :only (defroutes ANY)])
  (:require [cljs-todo.data :as d]))

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

(defmethod remote :save-task [data]
  (d/save-task (-> data :args :task)))

(defmethod remote :list-tasks [data]
  {:task-list (d/list-tasks)})

(defroutes remote-routes
  (ANY "/remote" {{data "data"} :params}
       (pr-str
        (remote
         (binding [*read-eval* false]
           (read-string data))))))
