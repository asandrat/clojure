(ns clojure-project.handler
  (:require [routes.home :refer [home-routes]]
            [db.ddl :as ddl]
            [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(defn init []
  (if-not (.exists (java.io.File. "./dump.sql"))
    (ddl/create-table-fruit)))

(defn destroy []
  (println "fruitshop is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Error 404: Page Not Found"))

(def app
  (-> (routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)))
