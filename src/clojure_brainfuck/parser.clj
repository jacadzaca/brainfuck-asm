(ns clojure-brainfuck.parser
  (:require [clojure.string :as str]))

(defn remove-initial-comment-loop [string]
  (if (str/starts-with? string "[") 
    (loop [i 1 matching-bracket-count 1]
      (if (= matching-bracket-count 0)
        (subs string i)
        (recur (inc i) 
          (if (= (nth string i) \[)
            (inc matching-bracket-count) 
            (if (= (nth string i) \])
              (dec matching-bracket-count)
              matching-bracket-count)))))
    string))

(defn create-statement 
  ([type]
    {:type type})
  ([type arguemnt]
    {:type      type
     :arguemnt arguemnt}))

(defn craete-loop [name]
  {:name name
   :type :loop
   :statements []})

(defn brainfuck->ast-node [character]
  (case character
    \+ (create-statement :inc)
    \- (create-statement :dec)
    \> (create-statement :inc-pointer)
    \< (create-statement :dec-pointer)
    \. (create-statement :call-print)
    \, (create-statement :call-read)))

(defn generate-ast [string]
  (loop [ast () current-label {:type :entrypoint :statements []} stack () i 0 loop-count 0]
    (if (= (count string) i)
      (apply conj ast current-label stack)
      (case (nth string i)
        \[ (recur ast
                  (craete-loop (str "loop" loop-count))
                  (conj stack (update current-label :statements conj (create-statement :call-loop loop-count)))
                  (inc i)
                  (inc loop-count))
        \] (recur (conj ast current-label)
                  (first stack)
                  (pop stack)
                  (inc i)
                  loop-count)
           (recur ast
                  (update current-label :statements conj (brainfuck->ast-node (nth string i)))
                  stack
                  (inc i)
                  loop-count)))))
