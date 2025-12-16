(ns clojure-cafes.core-test
  (:require [midje.sweet :refer :all]
            [clojure-cafes.core :refer :all]))

(fact "Vraca kafice koji se nalaze na Dorcolu"
      (count (cafes-by-location "Dorćol")) )

(fact "Kafici koji imaju sojino mleko"
      (map :name (cafes-with-milk :soy)
           ))
