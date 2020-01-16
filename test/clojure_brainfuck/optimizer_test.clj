(ns clojure-brainfuck.optimizer-test
  (:require [clojure.test :refer [deftest is]]
            [clojure-brainfuck.optimizer :as optimizer]))

(deftest combines-two-inc-instructions-into-addition-test
  (is (= [{:type :add :argument 2}] (optimizer/optimize-sentence [{:type :inc}
                                                                  {:type :inc}]))))

(deftest combines-two-dec-instructions-into-substraction-test
  (is (= [{:type :sub :argument 2}] (optimizer/optimize-sentence [{:type :dec}
                                                                  {:type :dec}]))))

(deftest combines-two-inc-pointer-instructions-into-pointer-addition-test
  (is (= [{:type :add-pointer :argument 2}] (optimizer/optimize-sentence [{:type :inc-pointer}
                                                                           {:type :inc-pointer}]))))

(deftest combines-two-dec-pointer-instructions-into-pointer-substracion-test
  (is (= [{:type :sub-pointer :argument 2}] (optimizer/optimize-sentence [{:type :dec-pointer}
                                                                          {:type :dec-pointer}]))))

(deftest combines-multiple-dec-instructions-into-substracion-test
  (is (= [{:type :sub :argument 5}] (optimizer/optimize-sentence [{:type :dec}
                                                                   {:type :dec}
                                                                   {:type :dec}
                                                                   {:type :dec}
                                                                   {:type :dec}]))))

(deftest combines-multiple-inc-instructions-into-addition-test
  (is (= [{:type :add :argument 4}] (optimizer/optimize-sentence [{:type :inc}
                                                                  {:type :inc}
                                                                  {:type :inc}
                                                                  {:type :inc}]))))

(deftest combines-multiple-pointer-inc-instructions-into-pointer-addition-test
  (is (= [{:type :add-pointer :argument 4}] (optimizer/optimize-sentence [{:type :inc-pointer}
                                                                          {:type :inc-pointer}
                                                                          {:type :inc-pointer}
                                                                          {:type :inc-pointer}]))))

(deftest combines-multiple-pointer-dec-instructions-into-pointer-substraction-test
  (is (= [{:type :sub-pointer :argument 4}] (optimizer/optimize-sentence [{:type :dec-pointer}
                                                                          {:type :dec-pointer}
                                                                          {:type :dec-pointer}
                                                                          {:type :dec-pointer}]))))

(deftest changes-nothing-if-cannot-optimize-test
  (let [unoptimizable-code [{:type :inc}
                            {:type :dec}
                            {:type :inc}
                            {:type :dec}]]
    (is (= unoptimizable-code (optimizer/optimize-sentence unoptimizable-code)))))
