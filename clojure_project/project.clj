(defproject clojure_project "0.1.0-SNAPSHOT"
  :description "online Fruit shop"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [hiccup "1.0.5"]
                 [hiccup-table "0.2.0"]
                 [ring-server "0.3.1"]
                 [compojure "1.1.6"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:init clojure-project.handler/createTable
         :handler clojure-project.handler/app
         :destroy clojure-project.handler/destroy
         }
  :profiles {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.3.1"]]}})
