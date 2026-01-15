(ns clojurecafes.db
  (:require [next.jdbc :as jdbc]
            [clojure.set :as set]))

(def db-spec
  {:dbtype "h2"
   :DB_CLOSE_DELAY "-1"})

(def ds (jdbc/get-datasource db-spec))

(defn create-tables! []
  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS cafes (
        id IDENTITY PRIMARY KEY,
        name VARCHAR(255),
        location VARCHAR(255),
        coffee INT,
        ambience INT,
        esthetics INT,
        noise INT,
        coffee_until INT,
        lat DOUBLE,
        lng DOUBLE
     )"])

  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS cafe_milk (
        cafe_id BIGINT,
        milk VARCHAR,
        FOREIGN KEY (cafe_id) REFERENCES cafes(id)
     )"])

  (jdbc/execute! ds
                 ["CREATE TABLE IF NOT EXISTS users (
        id IDENTITY PRIMARY KEY,
        name VARCHAR NOT NULL,
        location_pref VARCHAR,
        milk_pref VARCHAR,
        min_coffee INT,
        likes_quiet BOOLEAN,
        open_after INT,
        lat DOUBLE,
        lng DOUBLE
     )"]))

(defn insert-cafe! [cafe]
  (jdbc/execute! ds
                 ["INSERT INTO cafes
      (name, location, coffee, ambience, esthetics, noise, coffee_until, lat, lng)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                  (:name cafe)
                  (:location cafe)
                  (:coffee cafe)
                  (:ambience cafe)
                  (:esthetics cafe)
                  (:noise cafe)
                  (:coffee-until cafe)
                  (:lat cafe)
                  (:lng cafe)]))

(defn last-cafe-id []
  (:id (first (jdbc/execute! ds
                             ["SELECT id FROM cafes ORDER BY id DESC LIMIT 1"]))))

(defn insert-cafe-milk! [cafe-id milk]
  (jdbc/execute! ds
                 ["INSERT INTO cafe_milk (cafe_id, milk) VALUES (?, ?)"
                  cafe-id
                  (name milk)]))

(defn load-cafes []
  (map #(set/rename-keys % {:coffee_until :coffee-until})
       (jdbc/execute! ds ["SELECT * FROM cafes"])))

(defn insert-user! [user]
  (jdbc/execute! ds
                 ["INSERT INTO users
      (name, location_pref, milk_pref, min_coffee, likes_quiet, open_after, lat, lng)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                  (:name user)
                  (:location (:preferences user))
                  (name (:milk (:preferences user)))
                  (:min-coffee (:preferences user))
                  (:likes-quiet (:preferences user))
                  (:open-after (:preferences user))
                  (:lat user)
                  (:lng user)]))
