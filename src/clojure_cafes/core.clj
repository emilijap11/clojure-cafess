(ns clojure-cafes.core)


;; PODACI
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


(defn average [values]
  (/ (reduce + values)
     (count values)))
(defn average-coffee []
  (average (map :coffee cafes)))

(defn average-ambience []
  (average (map :ambience cafes)))

(defn total-scores []
  (reduce
    (fn [sum cafe]
      (+ sum (:coffee cafe) (:ambience cafe)))
    0
    cafes))
(defn cafes-by-location [loc]
  (filter #(= loc (:location %)) cafes))

(defn cafes-with-milk [milk-type]
  (filter #(contains? (:milk %) milk-type) cafes))

(defn cafes-open-after [hour]
  (filter #(>= (:coffee-until %) hour) cafes))
(def aggregated
  (reduce
    (fn [[cnt coffee-sum ambience-sum] cafe]
      [(inc cnt)
       (+ coffee-sum (:coffee cafe))
       (+ ambience-sum (:ambience cafe))])
    [0 0 0]
    cafes))
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
(recommend-for-user cafes ana)
(time
  (doall
    (recommend-for-user cafes ana)))

(defn time-ms [f]
  (let [start (System/nanoTime)
        result (f)
        end (System/nanoTime)]
    {:time-ms (/ (- end start) 1e6)
     :result result}))

(time-ms #(doall (recommend-for-user cafes ana)))
