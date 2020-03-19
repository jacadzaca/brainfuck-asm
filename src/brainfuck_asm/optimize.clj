(ns brainfuck-asm.optimize
  (:require [brainfuck-asm.parse :as parse]
            [clojure.zip :as zip]))

(defn- ^:const optimize-two-statements
  "Defines the rules for optimzing statements.
  Returns nil if statements cannot be optimized, else the combination of statements"
  [statement statement1]
  (cond
    (= (statement :type) (statement1 :type) :inc)                                (parse/create-statement :add 2)
    (= (statement :type) (statement1 :type) :dec)                                (parse/create-statement :sub 2)
    (= (statement :type) (statement1 :type) :inc-pointer)                        (parse/create-statement :add-pointer 2)
    (= (statement :type) (statement1 :type) :dec-pointer)                        (parse/create-statement :sub-pointer 2)
    (and (= (statement :type) :add) (= (statement1 :type) :inc))                 (update statement :argument inc)
    (and (= (statement :type) :sub) (= (statement1 :type) :dec))                 (update statement :argument inc)
    (and (= (statement :type) :add-pointer) (= (statement1 :type) :inc-pointer)) (update statement :argument inc)
    (and (= (statement :type) :sub-pointer) (= (statement1 :type) :dec-pointer)) (update statement :argument inc)
    :else                                                                        nil))

(defn- ^:const optimize-sentence-zipper
  "Takes a zipped sentence and optimizes it"
  ([sentence-zipper]
   (if (-> sentence-zipper zip/next zip/end?)
     (zip/root sentence-zipper)
     (let [replacement (optimize-two-statements (zip/node sentence-zipper) (-> sentence-zipper zip/next zip/node))]
       (case replacement
         nil (recur (zip/next sentence-zipper))
         (recur (-> sentence-zipper
                    (zip/replace replacement)
                    zip/next
                    zip/remove)))))))

(defn ^:const optimize-sentence
  "Turns a vector of statements (sentence) into an optimized sentence.
  Returns a vector"
  [sentence]
  (optimize-sentence-zipper (-> sentence zip/vector-zip zip/next)))
