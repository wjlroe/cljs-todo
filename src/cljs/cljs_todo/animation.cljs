(ns ^{:doc "Defines animations which are used in the sample
  application."}
  cljs-todo.animation
  (:use [one.core :only (start)]
        [one.browser.animation :only (play play-animation parallel bind)]
        [domina :only (by-id set-html! append! set-styles! destroy-children! single-node
                             add-class! remove-class!)]
        [domina.xpath :only (xpath)])
  (:require [goog.dom.forms :as gforms]
            [goog.style :as style]))

(def ^:private
  form-in {:effect :fade :start 0 :end 1 :time 800})

(def task-form "//div[@id='task-form']")
(def task-label "//label[@id='task-input-label']/span")
(def login-form "//div[@id='login-form']")

(def login-view "//div[@id='login-view']")
(def tasks-view "//div[@id='tasks-view']")

(defn initialize-views
  [[login-html task-html]]
  (let [content (xpath "//div[@id='content']")]
    (destroy-children! content)
    (set-html! content login-html)
    (append! content task-html)
    (set-styles! (xpath tasks-view) {:opacity "0" :display "none" :margin-top "-200px"})
    (play "//div[@id='content']" form-in {:after #(.focus (by-id "username") ())})))

(def username-label "//label[@id='username-label']/span")
(def password-label "//label[@id='password-label']/span")

(defn show-task-list []
  (let [e {:effect :fade :end 0 :time 500}]
    (play-animation #(parallel (bind login-view e)
                               (bind tasks-view
                                     {:effect :color :time 500} ; Dummy animation for delay purposes
                                     {:effect :fade-in-and-show :time 600}))
                    {;; We need this next one because IE8 won't hide the button
                     :after #(set-styles! (by-id "login-button") {:display "none"})})))

(def fade-in {:effect :fade :end 1 :time 400})
(def fade-out {:effect :fade :end 0 :time 400})

(def white [255 255 255])
(def smoky [235 235 235])
(defn show-new-task [id]
  (start (bind (by-id id)
               [(assoc fade-in :time 400)
                {:effect :bg-color :start white :end smoky :time 400}]
               {:effect :bg-color :start smoky :end white :time 600})))

(defn fade-task-out [id]
  (let [li (by-id id)]
    (play li (assoc fade-out :end 0.4 :time 200))
    (remove-class! li "not-completed")
    (add-class! li "completed")))

(defn fade-task-in [id]
  (let [li (by-id id)]
    (play li fade-in)
    (remove-class! li "completed")
    (add-class! li "not-completed")))

(defn disable-button
  "Accepts an element id for a button and disables it. Fades the
  button to 0.2 opacity."
  [id]
  (let [button (by-id id)]
    (gforms/setDisabled button true)
    (play button (assoc fade-in :end 0.2))))

(defn enable-button
  "Accepts an element id for a button and enables it. Fades the button
  to an opactiy of 1."
  [id]
  (let [button (by-id id)]
    (gforms/setDisabled button false)
    (play button fade-in)))

