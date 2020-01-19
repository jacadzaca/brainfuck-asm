(ns clojure-brainfuck.generator
  (:require [clojure.string :as str]))

(defn- prepare-statements [statements]
  (->> statements flatten (map #(str "    " %)) (str/join \newline)))

(defn- generate-segment [name & statements]
  (str "segment ." name \newline (prepare-statements statements) \newline))

(defn- generate-label [name & statements]
  (str \newline name \: \newline (prepare-statements statements) \newline))

(defn- generate-loop-condition [statement]
  ["cmp byte [eax], 0" (str "jne " (:argument statement)) "ret"])

(def ^:private ^:const print-cell
  (generate-label "print-cell" "push eax" "mov ecx, eax" "mov eax, 0x04"
                          "mov ebx, 0x01" "mov edx, 0x01" "int 0x80" "pop eax" "ret"))

(def ^:private ^:const exit ["mov eax, 1" "xor ebx, ebx" "int 0x80"])

(defn- statement->asm [statement]
  "Translates an statement to assembly code"
  (case (:type statement)
    :inc         "inc byte [eax]"
    :dec         "dec byte [eax]"
    :inc-pointer "inc eax"
    :dec-pointer "dec eax"
    :add         (str "add byte [eax], " (:argument statement))
    :sub         (str "sub byte [eax], " (:argument statement))
    :add-pointer (str "add eax, " (:argument statement))
    :sub-pointer (str "sub eax, " (:argument statement))
    :call-print  "all print_cell"
    :call-read   "all read"
    :call-loop   (str "all loop" (:argument statement))
    :call-exit   exit
    :loop-end    (generate-loop-condition statement)
    (throw (IllegalArgumentException. (str (:type statement) " is not a proper statement type")))))

(defn generate-assembly [ast]
  (str
    (generate-segment "bss" "array: resb 30000")
    \newline
    (generate-segment "text" "global _start")
    (apply str (map #(apply generate-label (:name % "_start") (map statement->asm (:statements %))) ast))
    print-cell))
