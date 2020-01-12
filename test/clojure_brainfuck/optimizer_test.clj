(ns clojure-brainfuck.optimizer-test
  (:require [clojure.test :refer [deftest is]]
            [clojure-brainfuck.optimizer :as optimizer]))

(deftest combines-inc-into-addition-test
  (is (= [{:type :add :argument 2}] (optimizer/optimize-sentence [{:type :inc}
                                                                  {:type :inc}]))))

(deftest combines-inc-into-addition-test
  (is (= [{:type :sub :argument 2}] (optimizer/optimize-sentence [{:type :dec}
                                                                  {:type :dec}]))))

(deftest combines-inc-pointer-into-pointer-addition-test
  (is (= [{:type :add-pointer :argument 2}] (optimizer/optimize-sentence [{:type :inc-pointer}
                                                                           {:type :inc-pointer}]))))

(deftest combines-dec-pointer-into-pointer-substracion-test
  (is (= [{:type :sub-pointer :argument 2}] (optimizer/optimize-sentence [{:type :dec-pointer}
                                                                          {:type :dec-pointer}]))))

(deftest combines-addition-with-inc-test
  (is (= [{:type :add :argument 3}] (optimizer/optimize-sentence [{:type :add :argument 2}
                                                                  {:type :inc}]))))

(deftest combines-substraction-with-dec-test
  (is (= [{:type :sub :argument 3}] (optimizer/optimize-sentence [{:type :sub :argument 2}
                                                                  {:type :dec}]))))

(deftest combines-pointer-addition-with-pointer-inc-test
  (is (= [{:type :add-pointer :argument 3}] (optimizer/optimize-sentence [{:type :add-pointer :argument 2}
                                                                          {:type :inc-pointer}]))))

(deftest combines-substraction-with-dec-test
  (is (= [{:type :sub-pointer :argument 3}] (optimizer/optimize-sentence [{:type :sub-pointer :argument 2}
                                                                          {:type :dec-pointer}]))))

(deftest combines-multiple-dec-into-substracion-test
  (is (= [{:type :sub :argument 5}] (optimizer/optimize-sentence [{:type :dec}
                                                                   {:type :dec}
                                                                   {:type :dec}
                                                                   {:type :dec}
                                                                   {:type :dec}]))))

(deftest combines-multiple-inc-into-addition-test
  (is (= [{:type :add :argument 4}] (optimizer/optimize-sentence [{:type :inc}
                                                                  {:type :inc}
                                                                  {:type :inc}
                                                                  {:type :inc}]))))

(deftest combines-multiple-pointer-inc-into-pointer-addition-test
  (is (= [{:type :add-pointer :argument 4}] (optimizer/optimize-sentence [{:type :inc-pointer}
                                                                          {:type :inc-pointer}
                                                                          {:type :inc-pointer}
                                                                          {:type :inc-pointer}]))))

(deftest combines-multiple-pointer-dec-into-pointer-substraction-test
  (is (= [{:type :sub-pointer :argument 4}] (optimizer/optimize-sentence [{:type :dec-pointer}
                                                                          {:type :dec-pointer}
                                                                          {:type :dec-pointer}
                                                                          {:type :dec-pointer}]))))

(deftest dose-not-combine-if-cannot-test
  (let [unoptimizable-code [{:type :inc}
                            {:type :dec}
                            {:type :inc}
                            {:type :dec}]]
    (is (= unoptimizable-code (optimizer/optimize-sentence unoptimizable-code)))))
