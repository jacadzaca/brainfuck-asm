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

(defn find-coresponding-bracket
  "Returns index of the corespoding closing bracket or nil if no bracket can be found.
  Function expects the passed string to have an opening bracket on index n"
  [string n]
  (cond 
    (or (empty? string) (not= (nth string n) \[)) nil
    :else (loop [i (+ 1 n) matching-bracket-count 1] 
        (cond 
          (zero? matching-bracket-count) (- i 1)
          (= (.length string) i) nil
          :else (recur (inc i)
            (case (.charAt string i)
              \[ (inc matching-bracket-count)
              \] (dec matching-bracket-count)
              matching-bracket-count))))))

(defn update-first-map-entry [map x]
  (update map (key (first map)) x))

(defn generate-ast [string]
  (loop [ast () current-label {:main ""} stack () i 0 loop-count 0]
    (if (= (count string) i)
      (reduce conj(apply conj ast current-label stack))
      (case (.charAt string i)
        \[ (recur ast {loop-count ""} (conj stack (update-first-map-entry current-label #(str % loop-count))) (inc i) (inc loop-count))
        \] (recur (conj ast (update-first-map-entry current-label #(str % \R))) (first stack) (pop stack) (inc i) loop-count)
        (recur ast (update-first-map-entry current-label #(str % (nth string i))) stack (inc i) loop-count)))))
