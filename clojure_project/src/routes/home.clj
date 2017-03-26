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
    [:h1 {:style "color: orange;"} "Hi! Welcome to my fruitstore"]
    [:br]
    [:br]
    [:h2 [:a {:href "/show" :style "text-decoration: none;"} "Show available fruits &nbsp;&nbsp;" ]]
    [:h2 [:a {:href "/requested" :style "text-decoration: none;"} "&nbsp;&nbsp;Show requested fruits" ]]]))


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
            [:td [:a {:href (str "/buy/" (h (:id fruit))) :style "text-decoration: none;"} "Buy"]]]))])

(defn show-requested-fruits []
  (if (> (count (crud/read-request-fruit)) 0)
  [:table {:style "color:grey;"}
   [:thead
    [:tr
     [:th {:style "color:orange;"} "Name&nbsp;" ]
     [:th {:style "color:orange;"} "Quantity&nbsp;"]
     [:th {:style "color:orange;"} "Descent&nbsp;"]
     [:th ""]
     [:th ""]]]
   (into [:tbody]
         (for [fruit (crud/read-request-fruit)]
           [:tr
            [:td (:name fruit)]
            [:td (:quantity fruit)]
            [:td (:descent fruit)]
            [:td [:a {:href (str "/delete/" (h (:id fruit))) :style "text-decoration: none;"} "Delete&nbsp;"]]
            [:td [:a {:href (str "/update/" (h (:id fruit))) :style "text-decoration: none;"} "Update"]]]))]))

(defn update-qty [& [name price currency quantity reqqty unit descent id error]]
  (layout/common
  [:h1 {:style "color:blue;"} "Buy fruit"]
  (form-to {:id "frmBuy"}
    [:post "/buy"]
           [:p {:style "color:red;"} error]
           [:hr]
            [:p "Id:"]
           (text-field {:readonly true} "id" id)
           [:p "Name:"]
           (text-field {:readonly true} "name" name)
           [:p "Price:" ]
           (text-field {:readonly true} "price" price)
           [:p "Currency:"]
           (text-field {:readonly true} "currency" currency)
           [:p "unit:"]
           (text-field {:readonly true} "unit" unit)
           [:p "Available quantity:"]
           (text-field {:readonly true} "quantity" quantity)
           [:p "Requsted quantity:"]
           (text-field "reqqty" reqqty)
           [:br] [:br]
           (submit-button "Buy")
           [:hr]
           [:a {:href "/" :class "back"} "Go Back"])))

(defn insert-update [& [name price currency quantity unit descent id error]]
  (layout/common
  [:h2 (if (nil? id) "Request fruit" "Update requsted fruit")]
  (form-to {:id "frm_insert"}
    [:post "/save"]
           [:p {:style "color:red;"} error]
           [:hr]
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
           (submit-button "Request")
           [:hr]
           [:br]
           [:a {:href "/" :class "back"} "Go Back"])))

(defn parse-number [s]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))

(defn save-fruit [name price currency quantity unit descent & [id]]
  (cond
    (empty? name)
    (insert  name price currency quantity unit descent "Fruit name must be entered" id)
    (nil? (parse-number price))
    (insert name price currency quantity unit descent "Price must be a number" id)
    (empty? currency)
    (insert name price currency quantity unit descent "Currency must be entered!" id)
    (<= (parse-number quantity) 0)
    (insert name price currency quantity unit descent "Enter quantity!" id)
    (empty? unit)
    (insert name price currency quantity unit descent "Please fill unit" id)
    (empty? descent)
    (insert name price currency quantity unit descent "Enter descent" id)
    :else
  (do (crud/save-fruit name price currency quantity unit descent 0)
  (ring/redirect "/requested"))))

(defn buy-fruit [name price currency quantity reqqty unit descent & [id]]
  (cond
    (<= (parse-number quantity) 0)
    (update-qty  name price currency quantity reqqty unit descent id "Fruit is out of stock")
    (empty? reqqty)
    (update-qty  name price currency quantity reqqty unit descent id "You have to enter requested qunatity")
    (<= (parse-number reqqty) 0)
    (update-qty  name price currency quantity reqqty unit descent id "You have to request qunatity above 0")
    (<= (parse-number quantity) (parse-number reqqty))
    (update-qty name price currency quantity reqqty unit descent id "Requested qunatity is not available")
    :else
  (do
    (crud/decreaseFruitQty id (- (Integer/parseInt quantity) (Integer/parseInt reqqty)))
  (ring/redirect "/show"))))

(defn delete-fruit [id]
  (when-not (str/blank? id)
    (crud/delete-fruit id))
  (ring/redirect "/requested"))

(defn show-fruit [fruit]
  (insert (:name fruit) (:price fruit) (:currency fruit) (:quantity fruit) (:unit fruit) (:descent fruit) (:id fruit) nil))

(defn update-fruit-qty [fruit]
  (update-qty (:name fruit) (:price fruit) (:currency fruit) (:quantity fruit) (:reqqty fruit) (:unit fruit) (:descent fruit) (:id fruit) nil))

(defn show []
  (layout/common
    [:h1 "fruits"]
    (show-fruits)
    [:a {:href "/" :class "back" } "Home"]))

(defn requested []
  (layout/common
    [:h1 "Requested Fruits" ]
    [:a {:href "/add" :style "text-decoration: none;"} "Request new type of fruit" ]
    [:br]
    (show-requested-fruits)
    [:a {:href "/" :class "back"} "Home"]))

(defroutes home-routes
  (GET "/" [] (indexpage))
  (GET "/show" [] (show))
  (GET "/requested" [] (requested))
  (GET "/add" [] (insert))
  (GET "/add" [name price currency quantity unit descent id error] (insert name price currency quantity unit descent id error))
  (POST "/save" [name price currency quantity unit descent id] (save-fruit name price currency quantity unit descent id))
  (POST "/buy" [name price currency quantity reqqty unit descent id] (buy-fruit name price currency quantity reqqty unit descent id))
  (GET "/buy/:id" [id] (update-fruit-qty (crud/find-fruit id)))
  (GET "/delete/:id" [id] (delete-fruit id))
  (GET "/update/:id"[id] (show-fruit (crud/find-fruit id))))


