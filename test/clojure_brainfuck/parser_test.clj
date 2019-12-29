(ns clojure-brainfuck.parser-test
  (:require [clojure.test :refer :all]
            [clojure-brainfuck.parser :refer :all]))

(deftest correctly-maps-brainfuck-to-statement
  (is (= (create-statement :inc) (brainfuck-to-ast-node \+)))
  (is (= (create-statement :dec) (brainfuck-to-ast-node \-)))
  (is (= (create-statement :inc-pointer) (brainfuck-to-ast-node \>)))
  (is (= (create-statement :dec-pointer) (brainfuck-to-ast-node \<)))
  (is (= (create-statement :call-print) (brainfuck-to-ast-node \.)))
  (is (= (create-statement :call-read) (brainfuck-to-ast-node \,))))
