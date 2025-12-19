(ns clojure-cafes.db
  (:require [next.jdbc :as jdbc]))

(def db-spec
  {:dbtype "h2"
   :dbname "mem:cafes-db"
   :DB_CLOSE_DELAY "-1"})

(def ds (jdbc/get-datasource db-spec))

(defn create-tables! []
  ;; cafes
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS cafes (
        id IDENTITY PRIMARY KEY,
        name VARCHAR NOT NULL,
        location VARCHAR,
        coffee INT,
        ambience INT,
        esthetics INT,
        noise INT,
        coffee_until INT
     )"])

  ;; milk (normalizovano)
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS cafe_milk (
        cafe_id BIGINT,
        milk VARCHAR,
        FOREIGN KEY (cafe_id) REFERENCES cafes(id)
     )"]))

;; --------------------
;; INSERTS
;; --------------------

(defn insert-cafe! [cafe]
  (jdbc/execute! ds
                 ["INSERT INTO cafes
      (name, location, coffee, ambience, esthetics, noise, coffee_until)
      VALUES (?, ?, ?, ?, ?, ?, ?)"
                  (:name cafe)
                  (:location cafe)
                  (:coffee cafe)
                  (:ambience cafe)
                  (:esthetics cafe)
                  (:noise cafe)
                  (:coffee-until cafe)]))

(defn last-cafe-id []
  (:cafes/id
    (first
      (jdbc/execute! ds
                     ["SELECT id FROM cafes ORDER BY id DESC LIMIT 1"]))))

(defn insert-cafe-milk! [cafe-id milk]
  (jdbc/execute! ds
                 ["INSERT INTO cafe_milk (cafe_id, milk)
      VALUES (?, ?)"
                  cafe-id
                  (name milk)]))

(defn create-users-table! []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS users (
       id IDENTITY PRIMARY KEY,
       name VARCHAR NOT NULL,
       location_pref VARCHAR,
       milk_pref VARCHAR,
       min_coffee INT,
       likes_quiet BOOLEAN,
       open_after INT
     )"]))

(defn insert-user! [user]
  (jdbc/execute! ds
                 ["INSERT INTO users
      (name, location_pref, milk_pref, min_coffee, likes_quiet, open_after)
      VALUES (?, ?, ?, ?, ?, ?)"
                  (:name user)
                  (:location (:preferences user))
                  (name (:milk (:preferences user)))
                  (:min-coffee (:preferences user))
                  (:likes-quiet (:preferences user))
                  (:open-after (:preferences user))]))
(defn register-user! [name location milk min-coffee likes-quiet open-after]
  (jdbc/execute! db/ds
                 ["INSERT INTO users
      (name, location_pref, milk_pref, min_coffee, likes_quiet, open_after)
      VALUES (?, ?, ?, ?, ?, ?)"
                  name
                  location
                  (name milk)
                  min-coffee
                  likes-quiet
                  open-after]))


(defn find-user [name]
  (first (jdbc/execute! db/ds
                        ["SELECT * FROM users WHERE name = ?" name])))
(defn create-users-table! []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS users (
        id IDENTITY PRIMARY KEY,
        name VARCHAR NOT NULL,
        location_pref VARCHAR,
        milk_pref VARCHAR,
        min_coffee INT,
        likes_quiet BOOLEAN,
        open_after INT
    )"]))
(defn register-user! [name location milk min-coffee likes-quiet open-after]
  (jdbc/execute! ds
                 ["INSERT INTO users
      (name, location_pref, milk_pref, min_coffee, likes_quiet, open_after)
      VALUES (?, ?, ?, ?, ?, ?)"
                  name
                  location
                  (name milk)        ;; keyword u string
                  min-coffee
                  likes-quiet
                  open-after]))
(defn find-user [name]
  (first (jdbc/execute! ds
                        ["SELECT * FROM users WHERE name = ?" name])))
