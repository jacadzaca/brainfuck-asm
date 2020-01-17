(ns clojure-brainfuck.generator-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.string :as str]
            [clojure-brainfuck.generator :as generator]))

(let [generated-asm (generator/generate-assembly {})]
  (deftest generates-bss-segement-test
    (is (re-find #"segment .bss\s*" generated-asm)))

  (deftest is-array-definied-test
    (is (re-find #"array: resb \d*\s" generated-asm)))

  (deftest is-array-definied-in-bss-segment-test
    (is (re-find #"segment .bss\n\s*\w*\s*array: resb \d*\s\n" generated-asm)))

  (deftest generates-text-segement-test
    (is (re-find #"segment .text\s*" generated-asm)))

  (deftest is-global-start-defined-test
    (is (str/includes? generated-asm "global _start")))

  (deftest is-global-start-defined-in-text-segment-test
    (is (re-find #"segment .text\n\s*\w*\s*global _start\n" generated-asm))))

(deftest inc-statement-properly-translated
  (is (re-matches #"( |\t)*inc byte \[eax\]" (@#'generator/statement->asm {:type :inc}))))

(deftest dec-statement-properly-translated
  (is (re-matches #"( |\t)*dec byte \[eax\]" (@#'generator/statement->asm {:type :dec}))))

(deftest inc-pointer-statement-properly-translated
  (is (re-matches #"( |\t)*inc eax" (@#'generator/statement->asm {:type :inc-pointer}))))

(deftest dec-pointer-statement-properly-translated
  (is (re-matches #"( |\t)*dec eax" (@#'generator/statement->asm {:type :dec-pointer}))))

(deftest add-statement-properly-translated
  (is (re-matches #"( |\t)*add byte \[eax\], 2" (@#'generator/statement->asm {:type :add :argument 2}))))

(deftest sub-statement-properly-translated
  (is (re-matches #"( |\t)*sub byte \[eax\], 2" (@#'generator/statement->asm {:type :sub :argument 2}))))

(deftest add-pointer-statement-properly-translated
  (is (re-matches #"( |\t)*add eax, 2" (@#'generator/statement->asm {:type :add-pointer :argument 2}))))

(deftest sub-pointer-statement-properly-translated
  (is (re-matches #"( |\t)*sub eax, 2" (@#'generator/statement->asm {:type :sub-pointer :argument 2}))))

(deftest call-print-statement-properly-translated
  (is (re-matches #"( |\t)*call print" (@#'generator/statement->asm {:type :call-print}))))

(deftest call-read-statement-properly-translated
  (is (re-matches #"( |\t)*call read" (@#'generator/statement->asm {:type :call-read}))))

(deftest call-loop-statement-properly-translated
  (is (re-matches #"( |\t)*call loop1" (@#'generator/statement->asm {:type :call-loop :argument 1}))))

(deftest call-exit-statement-properly-translated
  (is (re-matches #"( |\t)*mov eax, 1\n( |\t)*xor ebx, ebx\n( |\t)*int 0x80" (@#'generator/statement->asm {:type :call-exit}))))

(deftest loop-end-statement-properly-translated
  (is (re-matches #"( |\t)*cmp byte \[eax\], 0\n( |\t)*jne loop1\n( |\t)*ret" (@#'generator/statement->asm {:type :loop-end :argument "loop1"}))))

(deftest statement->asm-throws-illegal-argument-when-cannot-translate-statement
  (is (thrown-with-msg? IllegalArgumentException #":invalid is not a proper statement type" (@#'generator/statement->asm {:type :invalid}))))