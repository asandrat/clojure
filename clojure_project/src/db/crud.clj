(ns db.crud
  (:require [clojure.java.jdbc :as jdbc])
  (:import java.sql.DriverManager))

(def db
  {:classname "org.sqlite.JDBC",
         :subprotocol "sqlite",
         :subname "sandrafruit.sqlite"})

(defn read-fruit []
  (jdbc/with-connection
    db
    (jdbc/with-query-results res
      ["SELECT * FROM fruit WHERE stock=1 ORDER BY id ASC"]
      (doall res))))

(defn read-request-fruit []
  (jdbc/with-connection
    db
    (jdbc/with-query-results res
      ["SELECT * FROM fruit WHERE stock=0 ORDER BY name ASC"]
      (doall res))))

(defn save-fruit [name quantity unit descent stock]
  (jdbc/with-connection
    db
    (jdbc/insert-values
      :fruit
      [:name :quantity :unit :descent :stock]
      [name quantity unit descent stock])))

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

(defn update-fruit [id name quantity unit descent stock]
  (jdbc/with-connection
    db
    (jdbc/update-values
      :fruit
      ["id=?" id]
      {:name name :quantity quantity :unit unit :descent descent})))

(defn decreaseFruitQty [id quantity]
  (jdbc/with-connection
    db
    (jdbc/update-values
      :fruit
      ["id=?" id]
      {:quantity quantity})))

