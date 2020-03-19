(ns brainfuck-asm.core-test
  (:require [clojure.test :refer [deftest is]]
            [brainfuck-asm.core :as core]))

(deftest remove-initial-comment-loop-throws-illegal-argument-test
  (is (thrown? IllegalArgumentException (@#'core/remove-initial-comment-loop "[aa[]"))))

(deftest remove-initial-comment-loop-returns-input-if-bracket-not-first-test
  (let [input "asdf"] (is (= input (@#'core/remove-initial-comment-loop input)))))

(deftest remove-initial-comment-loop-remove-comment-test
  (is (= "asdf" (@#'core/remove-initial-comment-loop "[asdf[]++]asdf")))
  (is (= "asdf[]" (@#'core/remove-initial-comment-loop "[asdf[[]]++]asdf[]"))))

(deftest remove-illegal-characters-test
  (is (= "+++[]-" (@#'core/remove-illegal-characters "asfa++asfag[]")))
  (is (= "-<>,."  (@#'core/remove-illegal-characters "xac-asfasfsa<>,safa'''."))))
