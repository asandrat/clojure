(ns db.ddl
  (:require [clojure.java.jdbc :as jdbc])
  (:import java.sql.DriverManager))

(def dbspec 
  {:classname "org.sqlite.JDBC",
         :subprotocol "sqlite",
         :subname "sandrafruit.sqlite"})

(defn create-table-fruit []
  (jdbc/with-connection
    dbspec
    (jdbc/create-table
      :fruit
      [:id "INTEGER PRIMARY KEY AUTOINCREMENT"]
      [:name "TEXT"]
      [:price "REAL"]
      [:quantity "INTEGER"]
      [:unit "TEXT"]
      [:descent "TEXT"]
      [:currency "TEXT"]
      [:stock "BOOL"])))

