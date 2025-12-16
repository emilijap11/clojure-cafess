(ns clojure-cafes.core)


(def cafes
  [{:id 1
    :name "Kafeterija"
    :location "Dorćol"
    :coffee 5
    :ambience 4
    :esthetics 4
    :noise 3
    :milk #{:soy :cow}
    :coffee-until 22}

   {:id 2
    :name "Pržionica D59B"
    :location "Vračar"
    :coffee 4
    :ambience 5
    :esthetics 5
    :noise 4
    :milk #{:cow :coconut}
    :coffee-until 20}

   {:id 3
    :name "Blaznavac Café"
    :location "Centar"
    :coffee 3
    :ambience 5
    :esthetics 5
    :noise 2
    :milk #{:soy :cow :coconut}
    :coffee-until 23}

   {:id 4
    :name "Novobeogradska Pržionica"
    :location "Novi Beograd"
    :coffee 5
    :ambience 3
    :esthetics 3
    :noise 3
    :milk #{:cow}
    :coffee-until 19}])


(def user
  {:id 100
   :name "Emilija"
   :preferences
   {:location "Dorćol"
    :milk :soy
    :min-coffee 4
    :likes-quiet true
    :open-after 21}})


(defn average [values]
  (/ (reduce + values) (count values)))


(defn average-coffee []
  (average (map :coffee cafes)))

(defn average-ambience []
  (average (map :ambience cafes)))

(defn cafes-by-location [loc]
  (filter #(= loc (:location %)) cafes))

(defn cafes-with-milk [milk-type]
  (filter #(contains? (:milk %) milk-type) cafes))

(defn cafes-open-after [hour]
  (filter #(>= (:coffee-until %) hour) cafes))

(defn total-scores []
  (reduce
    (fn [sum cafe]
      (+ sum (:coffee cafe) (:ambience cafe)))
    0
    cafes))


(defn filter-cafes [cafes prefs]
  (->> cafes
       (filter #(= (:location %) (:location prefs)))
       (filter #(>= (:coffee %) (:min-coffee prefs)))
       (filter #(contains? (:milk %) (:milk prefs)))
       (filter #(>= (:coffee-until %) (:open-after prefs)))))


(defn cafe-score [cafe prefs]
  (let [quiet-bonus (if (:likes-quiet prefs)
                      (- 5 (:noise cafe))
                      0)]
    (+ (* 3 (:coffee cafe))
       (* 2 (:ambience cafe))
       (:esthetics cafe)
       quiet-bonus)))


(defn recommend-cafes [cafes prefs]
  (->> (filter-cafes cafes prefs)
       (map #(assoc % :score (cafe-score % prefs)))
       (sort-by :score >)))


(def user-prefs
  {:location "Dorćol"
   :milk :soy
   :min-coffee 4
   :likes-quiet true
   :open-after 21})


(defn recommend-for-user [cafes user]
  (recommend-cafes cafes (:preferences user)))



(comment
  (average-coffee)
  (average-ambience)
  (cafes-by-location "Dorćol")
  (cafes-with-milk :soy)
  (recommend-cafes cafes user-prefs)


(  comment
    (defn time-ms [f]
      (let [start (System/nanoTime)
            result (doall (f))
            end (System/nanoTime)]
        {:result result
         :time-ms (/ (- end start) 1e6)}))


(def avg-coffee-time
  (time-ms #(average-coffee)))

(def by-location-time
  (time-ms #(cafes-by-location "Dorćol")))

(def with-milk-time
  (time-ms #(cafes-with-milk :soy)))

    (def recommend-time
      (time-ms #(recommend-cafes cafes user-prefs))))