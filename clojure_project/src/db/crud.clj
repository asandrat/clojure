(ns db.crud
  (:require [clojure.java.jdbc :as jdbc])
  (:import java.sql.DriverManager))

(def db
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//127.0.0.1:3306/sandrafruit"
   :user "root"
   :password ""})

(defn read-fruit []
  (jdbc/with-connection
    db
    (jdbc/with-query-results res
      ["SELECT * FROM fruit ORDER BY id ASC"]
      (doall res))))


(defn save-fruit [name price currency quantity unit descent]
  (jdbc/with-connection
    db
    (jdbc/insert-values
      :fruit
      [:name :price :currency :quantity :unit :descent]
      [name price currency quantity unit descent])))

(defn delete-fruit [id]
  (jdbc/with-connection
    db
    (jdbc/delete-rows
      :fruit
      ["id=?" id])))

(defn find-fruit [id]
  (first
    (jdbc/with-connection
      db
      (jdbc/with-query-results res
        ["SELECT * FROM fruit WHERE id= ?" id]
        (doall res)))))

(defn update-fruit [id name price currency quantity unit descent]
  (jdbc/with-connection
    db
    (jdbc/update-values
      :fruit
      ["id=?" id]
      {:name name :price price :currency currency :quantity quantity :unit unit :descent descent})))


