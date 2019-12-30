(ns clojure-brainfuck.parser-test
  (:require [clojure.test :refer [deftest is]]
            [clojure-brainfuck.parser :as parser]))

(deftest throws-assertion-error-if-the-argument-is-not-a-string-test
  (is (thrown? AssertionError (parser/generate-ast 123))))

(deftest throws-assertion-error-when-bracket-is-not-closed-test
  (is (thrown? AssertionError (parser/generate-ast "[+++][")))
  (is (thrown? AssertionError (parser/generate-ast "[+++[")))
  (is (thrown? AssertionError (parser/generate-ast "[][+++[]"))))

(deftest throws-illegal-argument-exception-when-encounters-bad-symbol-test
  (is (thrown? IllegalArgumentException (parser/generate-ast "asdf"))))

(def ^:private asts
  {"+-><.,"   (list {:type :entrypoint, :statements [{:type :inc}
                                                     {:type :dec}
                                                     {:type :inc-pointer}
                                                     {:type :dec-pointer}
                                                     {:type :call-print}
                                                     {:type :call-read}]})
   "+[+[+]+]+" (list {:type :entrypoint, :statements [{:type :inc}
                                                      {:type :call-loop, :arguemnt 0}
                                                      {:type :inc}]}
                     {:name "loop0", :type :loop, :statements [{:type :inc}
                                                               {:type :call-loop, :arguemnt 1}
                                                               {:type :inc}]}
                     {:name "loop1", :type :loop, :statements [{:type :inc}]})})

(deftest generates-valid-ast
  (doseq [[code ast] asts]
    (is (= (parser/generate-ast code) ast))))