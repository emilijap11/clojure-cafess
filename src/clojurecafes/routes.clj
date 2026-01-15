(ns clojurecafes.routes
  (:require [compojure.core :refer :all]
            [ring.util.response :as resp]
            [cheshire.core :as json]
            [clojurecafes.core :as core]))

(defroutes app-routes
           (POST "/recommend" req
             (let [prefs (json/parse-string (slurp (:body req)) true)
                   cafes (core/recommend prefs)]
               (resp/response (json/generate-string cafes)))))
