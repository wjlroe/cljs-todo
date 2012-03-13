(ns cljs-todo.config
  "Contains configuration for the application."
  (:require [net.cgrand.enlive-html :as html]))

(defn- production-transform [h]
  (html/transform h
                  [:div.topbar]
                  (html/substitute (html/html-snippet ""))))

(def ^{:doc "Configuration for the sample application."}
  config {:src-root "src"
          :app-root "src/cljs"
          :top-level-package "cljs_todo"
          :js "public/javascripts"
          :dev-js-file-name "main.js"
          :prod-js-file-name "mainp.js"
          :dev-js ["goog.require('cljs_todo.core');"
                   "goog.require('cljs_todo.model');"
                   "goog.require('cljs_todo.view');"
                   "goog.require('cljs_todo.controller');"
                   "goog.require('cljs_todo.history');"
                   "goog.require('cljs_todo.logging');"
                   "cljs_todo.core.start();cljs_todo.core.repl();"]
          :prod-js ["cljs_todo.core.start();"]
          :reload-clj ["/cljs_todo/api"
                       "/cljs_todo/config"
                       "/cljs_todo/dev_server"]
          :prod-transform production-transform})
