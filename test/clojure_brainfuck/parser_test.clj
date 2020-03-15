(ns clojure-brainfuck.parser-test
  (:require [clojure.test :refer [deftest is]]
            [clojure-brainfuck.parser :as parser]))

(deftest throws-assertion-error-when-bracket-is-not-closed-test
  (is (thrown? AssertionError (parser/generate-ast "[+++][")))
  (is (thrown? AssertionError (parser/generate-ast "[+++[")))
  (is (thrown? AssertionError (parser/generate-ast "[][+++[]"))))

(deftest throws-illegal-argument-exception-when-encounters-bad-symbol-test
  (is (thrown? IllegalArgumentException (parser/generate-ast "asdf"))))

(def ^:private asts
  {"+-><.,"   (list {:type :entrypoint, :statements [{:type :load-array}
                                                     {:type :inc}
                                                     {:type :dec}
                                                     {:type :inc-pointer}
                                                     {:type :dec-pointer}
                                                     {:type :call-print}
                                                     {:type :call-read}
                                                     {:type :call-exit}]})
   "+[+[+]+]+" (list {:type :entrypoint, :statements [{:type :load-array}
                                                      {:type :inc}
                                                      {:type :call-loop, :argument 0}
                                                      {:type :inc}
                                                      {:type :call-exit}]}
                     {:name "loop0", :type :loop, :statements [{:type :inc}
                                                               {:type :call-loop, :argument 1}
                                                               {:type :inc}
                                                               {:type :loop-end :argument "loop0"}]}
                     {:name "loop1", :type :loop, :statements [{:type :inc} {:type :loop-end :argument "loop1"}]})})

(deftest generates-valid-ast
  (doseq [[code ast] asts]
    (is (= ast (parser/generate-ast code)))))