(ns clojure-cafes.core)


;; PODACI
(def cafes
  [{:name "Kafeterija" :location "Dorćol" :coffee 5 :ambience 4}
   {:name "Pržionica D59B" :location "Vračar" :coffee 4 :ambience 5}
   {:name "Blaznavac Café" :location "Centar" :coffee 3 :ambience 5}
   {:name "Novobeogradska Pržionica" :location "Novi Beograd" :coffee 5 :ambience 3}])

;; MAP – izvlačenje ocena kafe
(def coffee-scores
  (map :coffee cafes))

;; FILTER – kafeterije u Dorćolu
(def dorcol-cafes
  (filter #(= "Dorćol" (:location %)) cafes))

;; LET + REDUCE – prosečan ambijent
(defn average-ambience []
  (let [scores (map :ambience cafes)
        total  (reduce + scores)
        n      (count cafes)]
    (/ total n)))

;; REDUCE – zbirni podaci
(def aggregated
  (reduce
    (fn [[cnt coffee-sum ambience-sum] e]
      [(inc cnt)
       (+ coffee-sum (:coffee e))
       (+ ambience-sum (:ambience e))])
    [0 0 0]
    cafes))
