(ns brainfuck-asm.generate-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.string :as str]
            [brainfuck-asm.generate :as generate]))

(let [generated-asm (generate/generate-assembly {})]
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

(defmacro test-statement-properly-translated
  [statement-name translation to-translate]
  `((deftest ~(symbol (str statement-name "-statement-properly-translated"))
      (is (= ~translation (`@#'generate/statement->asm  ~to-translate))))))

(test-statement-properly-translated `inc         "inc byte [eax]"    {:type :inc})
(test-statement-properly-translated `dec         "dec byte [eax]"    {:type :dec})
(test-statement-properly-translated `inc-pointer "inc eax"           {:type :inc-pointer})
(test-statement-properly-translated `dec-pointer "dec eax"           {:type :dec-pointer})
(test-statement-properly-translated `add         "add byte [eax], 2" {:type :add :argument 2})
(test-statement-properly-translated `sub         "sub byte [eax], 2" {:type :sub :argument 2})
(test-statement-properly-translated `add-pointer "add eax, 2"        {:type :add-pointer :argument 2})
(test-statement-properly-translated `sub-pointer "sub eax, 2"        {:type :sub-pointer :argument 2})
(test-statement-properly-translated `call-print  "call print_cell"   {:type :call-print})
(test-statement-properly-translated `call-read   "call read"         {:type :call-read})
(test-statement-properly-translated `call-loop   "call loop1"        {:type :call-loop :argument 1})
(test-statement-properly-translated `load-array  "mov eax, array"    {:type :load-array})

(let [call-exit-translation ["mov eax, 1" "xor ebx, ebx" "int 0x80"]
      loop-end-translation  ["cmp byte [eax], 0" "jne loop1" "ret"]]
  (test-statement-properly-translated call-exit call-exit-translation {:type :call-exit})
  (test-statement-properly-translated loop-end loop-end-translation   {:type :loop-end :argument "loop1"}))

(deftest statement->asm-throws-illegal-argument-when-cannot-translate-statement
  (is (thrown-with-msg? IllegalArgumentException #":invalid is not a proper statement type" (@#'generate/statement->asm {:type :invalid}))))
