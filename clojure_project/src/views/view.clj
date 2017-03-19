(ns views.view
  (:require [hiccup.page :refer :all]))

(defn common [& body]
  (html5
    [:head
     [:title "Welcome to online fruit shop"]]
    [:body body]))

