(ns clojure-brainfuck.parser
  (:require [clojure.string :as str]))

(defn remove-initial-comment-loop [string]
  (if (str/starts-with? string "[") 
    (loop [i 1 matching-bracket-count 1]
      (if (= matching-bracket-count 0)
        (subs string i)
        (recur (inc i) 
          (if (= (.charAt string i) \[)
            (inc matching-bracket-count) 
            (if (= (.charAt string i) \])
              (dec matching-bracket-count)
              matching-bracket-count)))))
    string))
