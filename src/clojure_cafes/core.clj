
(ns clojure-cafes.core
  (:require [clojure-cafes.db :as db]
            [next.jdbc :as jdbc]))

(db/create-tables!)

(def cafes
  [{:name "Kafeterija"
    :location "Dorćol"
    :coffee 5
    :ambience 4
    :esthetics 4
    :noise 3
    :milk #{:soy :cow}
    :coffee-until 22}

   {:name "Pržionica D59B"
    :location "Vračar"
    :coffee 4
    :ambience 5
    :esthetics 5
    :noise 4
    :milk #{:cow :coconut}
    :coffee-until 20}

   {:name "Blaznavac Café"
    :location "Centar"
    :coffee 3
    :ambience 5
    :esthetics 5
    :noise 2
    :milk #{:soy :cow :coconut}
    :coffee-until 23}

   {:name "Novobeogradska Pržionica"
    :location "Novi Beograd"
    :coffee 5
    :ambience 3
    :esthetics 3
    :noise 3
    :milk #{:cow}
    :coffee-until 19}])

(defn insert-cafe-with-milk! [cafe]
  (db/insert-cafe! cafe)
  (let [cafe-id (db/last-cafe-id)]
    (doseq [m (:milk cafe)]
      (db/insert-cafe-milk! cafe-id m))))

(doseq [cafe cafes]
  (insert-cafe-with-milk! cafe))

(defn get-cafes-with-milk []
  (jdbc/execute! db/ds
                 ["SELECT c.id,
             c.name,
             c.location,
             c.coffee,
             c.ambience,
             c.esthetics,
             c.noise,
             c.coffee_until,
             m.milk
      FROM cafes c
      LEFT JOIN cafe_milk m ON c.id = m.cafe_id"]))

(defn rows->cafes [rows]
  (->> rows
       (group-by :cafes/id)
       (map (fn [[_ rs]]
              (let [r (first rs)]
                {:name (:cafes/name r)
                 :location (:cafes/location r)
                 :coffee (:cafes/coffee r)
                 :ambience (:cafes/ambience r)
                 :esthetics (:cafes/esthetics r)
                 :noise (:cafes/noise r)
                 :coffee-until (:cafes/coffee_until r)
                 :milk (set
                         (keep #(some-> % :cafe_milk/milk keyword)
                               rs))})))))

(defn load-cafes []
  (rows->cafes (get-cafes-with-milk)))


(def ana
  {:name "Ana"
   :preferences
   {:location "Dorćol"
    :milk :soy
    :min-coffee 4
    :likes-quiet true
    :open-after 20}})

(defn cafe-score [cafe prefs]
  (let [quiet-score (if (:likes-quiet prefs)
                      (- 5 (:noise cafe))
                      0)]
    (+ (* 3 (:coffee cafe))
       (* 2 (:ambience cafe))
       (:esthetics cafe)
       quiet-score)))

(defn filter-cafes [cafes prefs]
  (->> cafes
       (filter #(= (:location %) (:location prefs)))
       (filter #(>= (:coffee %) (:min-coffee prefs)))
       (filter #(contains? (:milk %) (:milk prefs)))
       (filter #(>= (:coffee-until %) (:open-after prefs)))))

(defn recommend-for-user [cafes user]
  (let [prefs (:preferences user)]
    (->> (filter-cafes cafes prefs)
         (map #(assoc % :score (cafe-score % prefs)))
         (sort-by :score >))))


(defn main-test! []
  (db/create-tables!)
  (doseq [c cafes]
    (insert-cafe-with-milk! c))
  (let [loaded-cafes (load-cafes)
        recommendations (recommend-for-user loaded-cafes ana)]
    (println "Loaded cafes:" loaded-cafes)
    (println "Recommendations for Ana:" recommendations)
    recommendations))
(defn recommend-for-logged-in-user [user-name]
  (let [db-user (find-user user-name)
        prefs {:location (:users/location_pref db-user)
               :milk (keyword (:users/milk_pref db-user))
               :min-coffee (:users/min_coffee db-user)
               :likes-quiet (:users/likes_quiet db-user)
               :open-after (:users/open_after db-user)}]
    (recommend-for-user (load-cafes) {:name user-name :preferences prefs})))
