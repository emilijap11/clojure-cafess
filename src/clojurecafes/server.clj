(ns clojurecafes.server
  (:require
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.resource :refer [wrap-resource]]
    [clojurecafes.routes :refer [app-routes]]))

(def app
  (-> app-routes
      (wrap-resource "public")
      (wrap-defaults site-defaults)))

(defn start-server []
  (run-jetty app {:port 3000 :join? false}))
