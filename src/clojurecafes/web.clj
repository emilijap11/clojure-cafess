(ns clojurecafes.web
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.page :refer [html5]]
            [clojurecafes.core :as core]
            [clojure.cafes.db :as db]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]))

;; home page sa preporukama za Anu
(defn home-page []
  (html5
    [:head [:title "Cafe Recommendations"]]
    [:body
     [:h1 "Recommendations for Ana"]
     [:ul
      (for [c (core/recommend-for-user (db/load-cafes) core/ana)]
        [:li (str (:name c) " - score: " (:score c))])]]))

;; rute
(defroutes app
           (GET "/" [] (home-page))

           ;; forma za registraciju novog korisnika
           (GET "/register" []
                (html5
                  [:form {:method "POST" :action "/register"}
                   [:input {:name "name" :placeholder "Name"}]
                   [:input {:name "location" :placeholder "Location"}]
                   [:input {:name "milk" :placeholder "Milk type"}]
                   [:input {:name "min-coffee" :placeholder "Min coffee"}]
                   [:input {:type "checkbox" :name "likes-quiet"}]
                   [:input {:name "open-after" :placeholder "Open after"}]
                   [:button "Register"]]))

           ;; POST ruta za registraciju
           (POST "/register" {params :params}
                 (core/register-user! (:name params)
                                      (:location params)
                                      (keyword (:milk params))
                                      (Integer/parseInt (:min-coffee params))
                                      (= "on" (:likes-quiet params))
                                      (Integer/parseInt (:open-after params)))
                 (html5 [:p "User created!"]))

           (route/not-found "Not Found"))

;; middleware za parsiranje parametara
(def app-with-params (wrap-params app))

;; start servera
(defn start-server []
  (db/create-tables!)
  (core/seed-cafes!)
  (run-jetty app-with-params {:port 3000 :join? false}))

