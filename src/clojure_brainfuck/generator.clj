(ns clojure-brainfuck.generator
  (:require [clojure.string :as str]))

(defn generate-segment [name & statements]
  (str "segment ." name \newline (str/join \newline statements) \newline))

(defn generate-label [name & statements]
  (str name \: \newline (str/join \newline (flatten statements)) \newline))

(defn generate-loop [name statements]
  (generate-label name statements "cmp byte [eax], 0" (str "jne " name) "ret"))

(def print-character 
  (generate-label "print_character" ["push eax" "push ecx" "push ebx" "push edx" "mov ecx, eax" 
    "mov eax, 0x04" "mov ebx, 0x01" "mov edx, 0x01" "int 0x80" "pop edx" "pop ebx" "pop ecx" "pop eax" "ret"]))

(defn generate-assembly [ast]
  (dotimes [i (count ast)]
    (cond 
      (str/includes? (nth (keys ast) i) "loop") (println (generate-loop (nth (keys ast) i) (map brainfuck-to-assembly (nth (vals ast) i)))) 
      :else (println (generate-label (nth (keys ast) i) (map brainfuck-to-assembly (nth (vals ast) i)))))))