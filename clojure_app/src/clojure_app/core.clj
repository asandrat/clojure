(ns clojure-app.core)

(defn handler [request]
  { :status 200
    :headers {"Content-Type" "text/plain"}
    :body (str "Request123:\n\n"
    (with-out-str (clojure.pprint/pprint request)))})
