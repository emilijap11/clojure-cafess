(ns clojure-cafes.core)

;; PODACI O KAFIĆIMA (immutable map-e)


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


;; USER + USER PREFERENCES


(def user
  {:id 100
   :name "Emilija"
   :location "Dorćol"
   :preferences {:milk :soy
                 :min-coffee 4
                 :likes-quiet true
                 :open-after 21}})

;; GENERIČKE POMOĆNE FUNKCIJE


(defn average [values]
  (/ (reduce + values) (count values)))

;; ANALIZA PODATAKA (map / filter / reduce)

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



;; Score = kafa,ambijent,estetika,buka ,(prilagođeno user-u)

(defn cafe-score [cafe user]
  (let [{:keys [preferences]} user
        milk-match (if (contains? (:milk cafe) (:milk preferences)) 1 0)
        quiet-bonus (if (and (:likes-quiet preferences)
                             (<= (:noise cafe) 3))
                      1
                      0)]
    (+ (:coffee cafe)
       (:ambience cafe)
       (:esthetics cafe)
       milk-match
       quiet-bonus)))


;; PERSONALIZOVANA PREPORUKA


(defn recommend-cafes [user]
  (let [{:keys [preferences location]} user]
    (->> cafes
         ;; filtriranje po uslovima
         (filter #(>= (:coffee %) (:min-coffee preferences)))
         (filter #(contains? (:milk %) (:milk preferences)))
         (filter #(>= (:coffee-until %) (:open-after preferences)))
         ;; dodavanje score-a
         (map #(assoc % :score (cafe-score % user)))
         ;; sortiranje po score-u
         (sort-by :score >))))


;; PRIMER KORIŠĆENJA


(comment
  (average-coffee)
  (average-ambience)
  (cafes-by-location "Dorćol")
  (cafes-with-milk :soy)
  (recommend-cafes user))
