(ns brainfuck-asm.parse-test
  (:require [clojure.test :refer [deftest is]]
            [brainfuck-asm.parse :as parse]))

(deftest throws-assertion-error-when-bracket-is-not-closed-test
  (is (thrown? AssertionError (parse/generate-ast "[+++][")))
  (is (thrown? AssertionError (parse/generate-ast "[+++[")))
  (is (thrown? AssertionError (parse/generate-ast "[][+++[]"))))

(deftest throws-illegal-argument-exception-when-encounters-bad-symbol-test
  (is (thrown? IllegalArgumentException (parse/generate-ast "asdf"))))

(deftest returns-true-if-brackets-balanced-test
  (is (parse/balanced? "[[+[+]+]]+")))

(deftest returns-false-if-brackets-unbalanced-test
  (is (not (parse/balanced? "+[[[[+]]+++]"))))

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
    (is (= ast (parse/generate-ast code)))))