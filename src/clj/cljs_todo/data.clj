(ns cljs-todo.data
  )

(def ^:dynamic *items* (ref []))

(defn save-task
  "Create or update a task entity"
  [t]
  (dosync
   (alter *items* conj t)))

(defn list-tasks []
  @*items*)

(defn -main
  []
  (println "tasks:" (list-tasks))
  (println "add something:" (save-task {:description "woot"}))
  (println "tasks:" (list-tasks)))