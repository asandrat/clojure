(ns views.view
  (:require [hiccup.page :refer :all]))

(defn common [& body]
  (html5
    [:head
     [:title "Online fruit shop"]
     (include-css "/stylesheet.css")]
    [:body body]))

