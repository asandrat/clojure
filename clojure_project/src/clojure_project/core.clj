(ns clojure-project.core
  (:use [clojure-project.handler]
        [ring.server.standalone]
        [ring.middleware file-info file]))

(defonce server (atom nil))

(defn get-handler []
  (-> #'app
      (wrap-file "resources")
      (wrap-file-info)))

(defn start-server
  "used for starting the server in development mode from REPL"
  [& [port]]
  (let [port (if port (Integer/parseInt port) 8090)]
    (reset! server
            (serve (get-handler)
                   {:port port
                    :init init
                    :auto-reload? true
                    :destroy destroy
                    :join true}))
    (println (str "Go to http://localhost:" port "- in order to access the app"))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))
