(ns clojurecafes.maps
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(def google-api-key "TVOJ_API_KLJUC")

(defn geocode [address]
  (let [resp (http/get
               "https://maps.googleapis.com/maps/api/geocode/json"
               {:query-params {:address address
                               :key google-api-key}
                :as :json})
        location (get-in resp [:body :results 0 :geometry :location])]
    {:lat (:lat location)
     :lng (:lng location)}))
(defn distance-km [lat1 lon1 lat2 lon2]
  (let [R 6371
        dLat (Math/toRadians (- lat2 lat1))
        dLon (Math/toRadians (- lon2 lon1))
        a (+ (Math/pow (Math/sin (/ dLat 2)) 2)
             (* (Math/cos (Math/toRadians lat1))
                (Math/cos (Math/toRadians lat2))
                (Math/pow (Math/sin (/ dLon 2)) 2)))]
    (* R 2 (Math/atan2 (Math/sqrt a) (Math/sqrt (- 1 a))))))
