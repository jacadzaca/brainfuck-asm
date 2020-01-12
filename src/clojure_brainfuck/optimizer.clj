(ns clojure-brainfuck.optimizer
  (:require [clojure-brainfuck.parser :as parser]
            [clojure.zip :as zip]))

(defn- optimize-two-statements [statement statement1]
  (cond 
    (= (statement :type) (statement1 :type) :inc)                                (parser/create-statement :add 2)
    (= (statement :type) (statement1 :type) :dec)                                (parser/create-statement :sub 2)
    (= (statement :type) (statement1 :type) :inc-pointer)                        (parser/create-statement :add-pointer 2)
    (= (statement :type) (statement1 :type) :dec-pointer)                        (parser/create-statement :sub-pointer 2)
    (and (= (statement :type) :add) (= (statement1 :type) :inc))                 (update statement :argument inc)
    (and (= (statement :type) :sub) (= (statement1 :type) :dec))                 (update statement :argument inc)
    (and (= (statement :type) :add-pointer) (= (statement1 :type) :inc-pointer)) (update statement :argument inc)
    (and (= (statement :type) :sub-pointer) (= (statement1 :type) :dec-pointer)) (update statement :argument inc)
    :else                                                                        nil))

(defn optimize-ast-node
  ([ast-node]
    (let [zipped-ast (zip/vector-zip ast-node)]
    (optimize-ast-node (-> zipped-ast zip/next)
                       (-> zipped-ast zip/next zip/node)
                       (-> zipped-ast zip/next zip/next zip/node))))
  ([ast-zipper statement next-statement]
    (println (zip/node ast-zipper) statement next-statement)
    (if (-> ast-zipper zip/next zip/end?)
        (zip/root ast-zipper)
        (let [replacement (optimize-two-statements statement next-statement)]
          (case replacement
            nil (recur (zip/next ast-zipper)
                       (-> ast-zipper zip/next zip/node)
                       (-> ast-zipper zip/next zip/next zip/node))
            (recur (-> ast-zipper
                       (zip/replace replacement)
                       zip/next
                       zip/remove
                       zip/next)
                   (-> ast-zipper zip/next zip/next zip/node)
                   (-> ast-zipper zip/next zip/next zip/next zip/node)))))))
