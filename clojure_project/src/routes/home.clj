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
    [:h2 [:a {:href "/show" :class "button"} "Show available fruits" ]]
    [:h2 [:a {:href "/requested" :class "button"} "Show requested fruits" ]]]))


(defn show-fruits []
  [:table {:class "table"}
   [:thead
    [:tr
     [:th {:class "column"} "Id"]
     [:th {:class "column"}"Name"]
     [:th {:class "column"} "Price"]
     [:th {:class "column"} "Quantity"]
     [:th {:class "column"} "Unit"]
     [:th {:class "column"} "Descent"]
     [:th {:class "column"} "Currency"]]]
   (into [:tbody]
         (for [fruit (crud/read-fruit)]
           [:tr
            [:td {:class "columnentry"} (:id fruit)]
            [:td {:class "columnentry"} (:name fruit)]
            [:td {:class "columnentry"} (:price fruit)]
            [:td {:class "columnentry"} (:quantity fruit)]
            [:td {:class "columnentry"} (:unit fruit)]
            [:td {:class "columnentry"} (:descent fruit)]
            [:td {:class "columnentry"} (:currency fruit)]
            [:td [:a {:href (str "/buy/" (h (:id fruit))) :class "button"} "Buy"]]]))])

(defn show-requested-fruits []
  (if (> (count (crud/read-request-fruit)) 0)
    (layout/common 
  [:table {:class "table"}
   [:thead
    [:tr
     [:th {:class "column"} "Name" ]
     [:th {:class "column"} "Quantity"]
     [:th {:class "column"} "Descent"]
     [:th ""]
     [:th ""]]]
   (into [:tbody]
         (for [fruit (crud/read-request-fruit)]
           [:tr
            [:td {:class "columnentry"} (:name fruit)]
            [:td {:class "columnentry"} (:quantity fruit)]
            [:td {:class "columnentry"} (:descent fruit)]
            [:td [:a {:href (str "/delete/" (h (:id fruit))) :class "button"} "Delete"]]
            [:td [:a {:href (str "/update/" (h (:id fruit))) :class "button"} "Update"]]]))])))

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
           [:a {:href "/" :class "home"} "Go Back"])))

(defn insert-or-update [id nulloption notnulloption]
  (if (nil? id)
        (str nulloption)
        (str notnulloption)))

(defn insert-update [& [name quantity unit descent id error]]
  (layout/common
  [:h2 (insert-or-update id "Request fruit" "Update requsted fruit")]
  (form-to {:id "frm_insert"}
    [:post "/save"]
           [:p {:style "color:red;"} error]
           [:hr]
           (if (not (nil? id)) [:p "Id:"])
           (if (not (nil? id)) (text-field {:readonly true} "id" id))
           [:p "Name:"]
           (text-field "name" name)
           [:p "Requsted quantity:"]
           (text-field "quantity" quantity)
           [:p "unit:"]
           (text-field "unit" unit)
           [:p "Descent:"]
           (text-field "descent" descent)
           [:br] [:br]
           (submit-button (insert-or-update id "Request" "Update Request"))
           [:hr]
           [:br]
           [:a {:href "/" :class "home"} "Go Back"])))

(defn parse-number [s]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))

(defn save-fruit [name quantity unit descent & [id]]
  (cond
    (empty? name)
    (insert-update  name quantity unit descent id "Fruit name must be entered")
    (empty? quantity)
    (insert-update  name quantity unit descent id "Requsted quantity must be entered")
    (<= (parse-number quantity) 0)
    (insert-update name quantity unit descent id "Requsted quantity must be above zero!")
    (empty? unit)
    (insert-update name quantity unit descent id "Please fill unit")
    (empty? descent)
    (insert-update name quantity unit descent id "Enter descent")
    :else
  (do (if (nil? id) (crud/save-fruit name quantity unit descent 0)
        (crud/update-fruit id name quantity unit descent 0))
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
  (insert-update (:name fruit) (:quantity fruit) (:unit fruit) (:descent fruit) (:id fruit) nil))

(defn update-fruit-qty [fruit]
  (update-qty (:name fruit) (:price fruit) (:currency fruit) (:quantity fruit) (:reqqty fruit) (:unit fruit) (:descent fruit) (:id fruit) nil))

(defn show []
  (layout/common
    [:h1 "Available Fruit"]
    (show-fruits)
    [:a {:href "/" :class "home" } "Go Back"]))

(defn requested []
  (layout/common
    [:h1 "Requested Fruit" ]
    [:a {:href "/add" :class "button"} "Request new type of fruit" ]
    [:br]
    (show-requested-fruits)
    [:a {:href "/" :class "home"} "Go Back"]))

(defroutes home-routes
  (GET "/" [] (indexpage))
  (GET "/show" [] (show))
  (GET "/requested" [] (requested))
  (GET "/add" [] (insert-update))
  (GET "/add" [name quantity unit descent id error] (insert-update name quantity unit descent id error))
  (POST "/save" [name quantity unit descent id] (save-fruit name quantity unit descent id))
  (POST "/buy" [name price currency quantity reqqty unit descent id] (buy-fruit name price currency quantity reqqty unit descent id))
  (GET "/buy/:id" [id] (update-fruit-qty (crud/find-fruit id)))
  (GET "/delete/:id" [id] (delete-fruit id))
  (GET "/update/:id"[id] (show-fruit (crud/find-fruit id))))


