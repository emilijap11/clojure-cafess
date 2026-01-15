(ns clojurecafes.core
  (:require [clojurecafes.maps :as maps]
            [clojurecafes.db :as db]))


(defn start-app []
  (println "Aplikacija pokrenuta")
  ;;(let [recommendation (rec/recommend-cafe-async)]
  ;;  (println "Mogu da radim druge stvari dok se računa preporuka...")
  ;;  (println "Preporučeni kafić je:" @recommendation))
  )


(def cafes
  [{:name "Kafeterija" :location "Dorćol" :lat 44.8186 :lng 20.4600 :coffee 5 :ambience 4 :esthetics 4 :noise 3 :milk #{:soy :cow} :coffee-until 22}
   {:name "Pržionica D59B" :location "Vračar" :lat 44.8100 :lng 20.4750 :coffee 4 :ambience 5 :esthetics 5 :noise 4 :milk #{:cow :coconut} :coffee-until 20}
   {:name "Blaznavac Café" :location "Centar" :lat 44.8170 :lng 20.4570 :coffee 3 :ambience 5 :esthetics 5 :noise 2 :milk #{:soy :cow :coconut} :coffee-until 23}])

(def ana
  {:name "Ana"
   :lat 44.8190
   :lng 20.4590
   :preferences {:location "Dorćol" :milk :soy :min-coffee 4 :likes-quiet true :open-after 20}})

(defn insert-cafe-with-milk! [cafe]
  (db/insert-cafe! cafe)
  (let [id (db/last-cafe-id)]
    (doseq [m (:milk cafe)]
      (db/insert-cafe-milk! id m))))

(defn seed-cafes! []
  (doseq [c cafes]
    (insert-cafe-with-milk! c)))

(defn cafes-by-location [loc]
  (filter #(= (:location %) loc) cafes))

(defn cafes-with-milk [milk-type]
  (filter #(contains? (:milk %) milk-type) cafes))

(defn filter-cafes-by-distance [cafes user-lat user-lon max-dist]
  (filter #(<= (maps/distance-km user-lat user-lon (:lat %) (:lng %)) max-dist) cafes))

(defn filter-cafes [cafes user max-distance]
  (let [{:keys [lat lng]} user
        prefs (:preferences user)]
    (->> cafes
         ;; filter po lokaciji (opcionalno)
         (filter #(= (:location %) (:location prefs)))
         ;; filter po min-coffee
         (filter #(>= (:coffee %) (:min-coffee prefs)))
         ;; filter po mleku
         (filter #(contains? (:milk %) (:milk prefs)))
         ;; filter po vremenu kada kafa služi
         (filter #(>= (:coffee-until %) (:open-after prefs)))
         ;; dodaj polje distance
         (map #(assoc % :distance (maps/distance-km lat lng (:lat %) (:lng %))))
         ;; filter po maksimalnoj udaljenosti
         (filter #(<= (:distance %) max-distance))
         ;; sort po rastućoj udaljenosti
         (sort-by :distance))))
(require '[cheshire.core :as json])

(json/generate-string (core/filter-cafes core/cafes core/ana 2))
