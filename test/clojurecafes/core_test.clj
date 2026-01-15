(ns clojurecafes.core-test
  (:require [midje.sweet :refer :all]
            [clojurecafes.core :refer :all]))

(fact "Vraca kafice koji se nalaze na Dorcolu"
      (count (cafes-by-location "Dorćol")) => 1)

(fact "Kafici koji imaju sojino mleko"
      (map :name (cafes-with-milk :soy)) => '("Kafeterija" "Blaznavac Café"))
