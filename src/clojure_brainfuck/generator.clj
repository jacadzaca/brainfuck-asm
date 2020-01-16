(ns clojure-brainfuck.generator
  (:require [clojure.string :as str]))

(defn- generate-segment [name & statements]
  (str "segment ." name \newline (str/join \newline statements) \newline))

(defn- generate-label [name & statements]
  (str name \: \newline (str/join \newline (flatten statements)) \newline))

(defn- generate-loop [name statements]
  (generate-label name statements "cmp byte [eax], 0" (str "jne " name) "ret"))

(def :private print-character
  (generate-label "print_character" "push eax" "push ecx" "push ebx" "push edx" "mov ecx, eax"
                                     "mov eax, 0x04" "mov ebx, 0x01" "mov edx, 0x01" "int 0x80" "pop edx" "pop ebx" "pop ecx" "pop eax" "ret"))

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
    :call-print  "call print"
    :call-read   "call read"
    :call-loop   (str "call loop" (:argument statement))))

(defn generate-assembly [ast]
  (dotimes [i (count ast)]
    (cond
      (str/includes? (nth (keys ast) i) "loop") (println (generate-loop (nth (keys ast) i) (nth (vals ast) i)))
      :else (println (generate-label (nth (keys ast) i) (nth (vals ast) i))))))
