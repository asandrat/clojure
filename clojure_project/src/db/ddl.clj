(ns db.ddl
  (:require [clojure.java.jdbc :as jdbc])
  (:import java.sql.DriverManager))

(def dbspec 
  {:classname "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname "//localhost:3306/sandrafruit"
   :user "root"
   :password ""})

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
      [:currency "TEXT"])))

