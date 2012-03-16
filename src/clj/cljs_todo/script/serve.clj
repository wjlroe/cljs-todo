(ns cljs-todo.script.serve
  "Provides a -main function which will start the production server."
  (:require [cljs-todo.prod-server :as prod]))

(defn -main
  "Start the production server which serves the content from
  out/public as well as the sample application's API."
  []
  (prod/run-server))