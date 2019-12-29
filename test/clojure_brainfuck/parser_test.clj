(ns clojure-brainfuck.parser-test
  (:require [clojure.test :refer :all]
            [clojure-brainfuck.parser :refer :all]))

(deftest correctly-maps-brainfuck->statement-test
  (is (= (create-statement :inc) (brainfuck->ast-node \+)))
  (is (= (create-statement :dec) (brainfuck->ast-node \-)))
  (is (= (create-statement :inc-pointer) (brainfuck->ast-node \>)))
  (is (= (create-statement :dec-pointer) (brainfuck->ast-node \<)))
  (is (= (create-statement :call-print) (brainfuck->ast-node \.)))
  (is (= (create-statement :call-read) (brainfuck->ast-node \,))))
