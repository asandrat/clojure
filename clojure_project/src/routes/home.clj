(ns routes.home
  (:require [compojure.core :refer :all]
            [views.view :as layout]
            [clojure.string :as str]
            [hiccup.form :refer :all]
            [hiccup.core :refer [h]]
            [ring.util.response :as ring]
            [db.crud :as crud]))

(defn indexpage []
  (layout/common
    [:h2 "Welcome to my fruitstore"]
    [:br]
    [:a {:href "/add"} "Add new" ]
    [:br]
    [:a {:href "/show"} "Show fruits" ]))


(defn show-fruits []
  [:table {:border 1}
   [:thead
    [:tr
     [:th "Id"]
     [:th "Name"]
     [:th "Price"]
     [:th {:width 250} "Quantity"]
     [:th "Unit"]
     [:th "Descent"]
     [:th "Currency"]
     [:th "Update"]]]
   (into [:tbody]
         (for [fruit (crud/read-fruit)]
           [:tr
            [:td (:id fruit)]
            [:td (:name fruit)]
            [:td (:price fruit)]
            [:td (:quantity fruit)]
            [:td (:unit fruit)]
            [:td (:descent fruit)]
            [:td (:currency fruit)]
            [:td [:a {:href (str "/delete/" (h (:id fruit)))} "delete"]]
            [:td [:a {:href (str "/update/" (h (:id fruit)))} "update"]]]))])

(defn insert_update [& [name price quantity unit descent currency error id]]
  (layout/common
  [:h2 (if (nil? id) "Add new fruit" "Updating fruit")]
  (form-to {:id "frm_insert"}
    [:post "/save"]
           (if (not (nil? id))
             [:p "Id:"])
           (if (not (nil? id))
               (text-field {:readonly true} "id" id))
           [:p "Name:"]
           (text-field "name" name)
           [:p "Price:" ]
           (text-field {:id "price"} "price" price)
           [:p "Quantity:"]
           (text-area {:rows 5 :cols 30} "quantity" quantity)
           [:p "unit:"]
           (text-field "unit" unit)
           [:p "Descent:"]
           (text-field "descent" descent)
           [:p "Currency:"]
           (text-field "currency" currency)
           [:br] [:br]
           (submit-button {:onclick " return javascript:validateInsertForm()"} (if (nil? id)"Insert" "Update"))
           [:hr]
           [:p {:style "color:red;"} error])
    [:a {:href "/" :class "back"} "Home"]))

(defn parse-number [s]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))

(defn save-fruit [name price quantity unit descent currency & [id]]
  (cond
    (empty? name)
    (insert_update  name price quantity unit descent currency "Enter name" id)
    (nil? (parse-number price))
    (insert_update  name price quantity unit descent currency "Price must be a number!" id)
    (<= (parse-number price) 0)
    (insert_update  name price quantity unit descent currency "Price must be a positive number!" id)
    (empty? quantity)
    (insert_update  name price quantity unit descent currency "Enter quantity!" id)
    (empty? unit)
    (insert_update  name price quantity unit descent currency "Enter unit" id)
    (empty? descent)
    (insert_update  name price quantity unit descent currency "Enter descent" id)
    (empty? currency)
    (insert_update  name price quantity unit descent currency "Enter currency" id)
    :else
  (do
    (if (nil? id)
      (crud/save-fruit name price quantity unit descent currency)
      (crud/update-fruit id name price quantity unit descent currency))
  (ring/redirect "/show"))))



(defn format-time [timestamp]
  (-> "dd/MM/yyyy"
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn delete-fruit [id]
  (when-not (str/blank? id)
    (crud/delete-fruit id))
  (ring/redirect "/show"))

(defn show-fruit [fruit]
  (insert_update (:name fruit) (:price fruit) (:quantity fruit) (:unit fruit) (:descent fruit) nil (:id fruit)))

(defn show []
  (layout/common
    [:h1 "fruits"]
    (show-fruits)
    [:a {:href "/" :class "back"} "Home"]))

(defroutes home-routes
  (GET "/" [] (indexpage))
  (GET "/add" [] (insert_update))
  (GET "/add" [name price quantity unit descent currency error id] (insert_update name price quantity unit descent currency error id))
  (GET "/show" [] (show))
  (POST "/save" [name price quantity unit descent currency id] (save-fruit name price quantity unit descent currency id))
  (GET "/delete/:id" [id] (delete-fruit id))
  (GET "/update/:id"[id] (show-fruit (crud/find-fruit id))))


