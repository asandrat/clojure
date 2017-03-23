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
    [:div {:id "content"}
    [:h1 {:class "text-success"} "Welcome to my fruitstore"]
    [:br]
    [:a {:href "/add"} "Request new type of fruit" ]
    [:br]
    [:a {:href "/show"} "Show available fruits" ]
    [:br]
    [:a {:href "/requested"} "Show requested fruits" ]]))


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
     [:th "Currency"]]]
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
            [:td [:a {:href (str "/update/" (h (:id fruit)))} "update"]]
            [:td [:a {:href (str "/buy/" (h (:id fruit)))} "buy"]]]))])

(defn show-requested-fruits []
  [:table {:border 1}
   [:thead
    [:tr
     [:th "Name"]
     [:th "Quantity"]
     [:th "Descent"]
     [:th "Update"]]]
   (into [:tbody]
         (for [fruit (crud/read-request-fruit)]
           [:tr
            [:td (:name fruit)]
            [:td (:quantity fruit)]
            [:td (:descent fruit)]
            [:td [:a {:href (str "/delete/" (h (:id fruit)))} "delete"]]
            [:td [:a {:href (str "/update/" (h (:id fruit)))} "update"]]]))])

(defn update [& [name price currency quantity reqqty unit descent id error]]
  (layout/common
  [:h2 "Buy fruit"]
   [:a {:href "/" :class "back"} "Go Back"]
  (form-to {:id "frmBuy"}
    [:post "/buy"]
           [:p "Id:"]
           (text-field {:readonly true} "id" id)
           [:p "Name:"]
           (text-field {:readonly true} "name" name)
           [:p "Price:" ]
           (text-field {:id "price"} "price" price)
           [:p "Currency:"]
           (text-field {:readonly true} "currency" currency)
           [:p "unit:"]
           (text-field {:readonly true} "unit" unit)
           [:p "Available quantity:"]
           (text-field {:readonly true} "quantity" quantity)
           [:p "Requsted quantity:"]
           (text-field "reqqty" reqqty)
           [:br] [:br]
           (submit-button {:onclick " return javascript:validateInsertForm()"} "Buy")
           [:hr]
           [:p {:style "color:red;"} error])))

(defn insert_update [& [name price currency quantity unit descent error id]]
  (layout/common
  [:h2 (if (nil? id) "Request fruit" "Buy fruit")]
   [:a {:href "/" :class "back"} "Go Back"]
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
           [:p "Currency:"]
           (text-field "currency" currency)
           [:p "Quantity:"]
           (text-field {:rows 5 :cols 30} "quantity" quantity)
           [:p "unit:"]
           (text-field "unit" unit)
           [:p "Descent:"]
           (text-field "descent" descent)
           [:br] [:br]
           (submit-button {:onclick " return javascript:validateInsertForm()"} (if (nil? id)"Insert" "Update"))
           [:hr]
           [:p {:style "color:red;"} error])))

(defn parse-number [s]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))

(defn save-fruit [name price currency quantity unit descent & [id]]
  (cond
    (empty? name)
    (insert_update  name price currency quantity unit descent "Fruit name must be entered" id)
    (nil? (parse-number price))
    (insert_update  name price currency quantity unit descent "Price must be a number" id)
    (empty? currency)
    (insert_update  name price currency quantity unit descent "Currency must be entered!" id)
    (<= (parse-number quantity) 0)
    (insert_update  name price currency quantity unit descent "Enter quantity!" id)
    (empty? unit)
    (insert_update  name price currency quantity unit descent "Please fill unit" id)
    (empty? descent)
    (insert_update  name price currency quantity unit descent "Enter descent" id)
    :else
  (do
    (if (nil? id)
      (crud/save-fruit name price currency quantity unit descent)
      (crud/update-fruit id name price currency quantity unit descent))
  (ring/redirect "/requested"))))

(defn buy-fruit [name price currency quantity reqqty unit descent error &[id]]
  (cond
    (<= (parse-number quantity) 0)
    (update  name price currency quantity reqqty unit descent "Fruit is out of stock" id)
    (<= (parse-number reqqty) 0)
    (update  name price currency quantity reqqty unit descent "You have to request qunatity above 0" id)
    (<= (parse-number quantity) (parse-number reqqty))
    (update  name price currency quantity reqqty unit descent "Requested qunatity is not available" id)
    :else
  (do
    (crud/decreaseFruitQty id (- (Integer/parseInt quantity) (Integer/parseInt reqqty)))
  (ring/redirect "/show"))))

(defn delete-fruit [id]
  (when-not (str/blank? id)
    (crud/delete-fruit id))
  (ring/redirect "/show"))

(defn show-fruit [fruit]
  (insert_update (:name fruit) (:price fruit) (:currency fruit) (:quantity fruit) (:unit fruit) (:descent fruit) nil (:id fruit)))

(defn update-fruit [fruit]
  (update (:name fruit) (:price fruit) (:currency fruit) (:quantity fruit) (:reqqty fruit) (:unit fruit) (:descent fruit) (:id fruit) nil))

(defn show []
  (layout/common
    [:h1 "fruits"]
    (show-fruits)
    [:a {:href "/" :class "back"} "Home"]))

(defn requested []
  (layout/common
    [:h1 "fruits"]
    (show-requested-fruits)
    [:a {:href "/" :class "back"} "Home"]))

(defroutes home-routes
  (GET "/" [] (indexpage))
  (GET "/add" [] (insert_update))
  (GET "/add" [name price currency quantity unit descent error id] (insert_update name price currency quantity unit descent error id))
  (GET "/show" [] (show))
  (GET "/requested" [] (requested))
  (POST "/save" [name price quantity unit descent currency id] (save-fruit name price currency quantity unit descent id))
  (POST "/buy" [name price currency quantity reqqty unit descent id] (buy-fruit name price currency quantity reqqty unit descent id))
  (GET "/buy/:id" [id] (update-fruit (crud/find-fruit id)))
  (GET "/delete/:id" [id] (delete-fruit id))
  (GET "/update/:id"[id] (show-fruit (crud/find-fruit id))))


