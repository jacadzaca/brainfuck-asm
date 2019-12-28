(ns clojure-brainfuck.parser
  (:require [clojure.string :as str]))

(defn remove-initial-comment-loop [string]
  (if (str/starts-with? string "[") 
    (loop [i 1 matching-bracket-count 1]
      (if (= matching-bracket-count 0)
        (subs string i)
        (recur (inc i) 
          (if (= (.charAt string i) \[)
            (inc matching-bracket-count) 
            (if (= (.charAt string i) \])
              (dec matching-bracket-count)
              matching-bracket-count)))))
    string))

(defn find-coresponding-bracket
  "Returns index of the corespoding closing bracket or nil if no bracket can be found.
  Function expects the passed string to have an opening bracket on index n"
  [string n]
  (cond 
    (or (empty? string) (not= (nth string n) \[)) nil
    :else (loop [i (+ 1 n) matching-bracket-count 1] 
        (cond 
          (zero? matching-bracket-count) (- i 1)
          (= (.length string) i) nil
          :else (recur (inc i)
            (case (.charAt string i)
              \[ (inc matching-bracket-count)
              \] (dec matching-bracket-count)
              matching-bracket-count))))))

(defn update-first-map-entry [map x]
  (update map (key (first map)) x))

(defn generate-ast [string]
  (loop [ast () current-label {:main ""} stack () i 0 loop-count 0]
    (if (= (count string) i)
      (reduce conj(apply conj ast current-label stack))
      (case (.charAt string i)
        \[ (recur ast {loop-count ""} (conj stack (update-first-map-entry current-label #(str % loop-count))) (inc i) (inc loop-count))
        \] (recur (conj ast (update-first-map-entry current-label #(str % \R))) (first stack) (pop stack) (inc i) loop-count)
        (recur ast (update-first-map-entry current-label #(str % (nth string i))) stack (inc i) loop-count)))))

(defn brainfuck-to-assembly [character]
  (case character
    \+ "inc byte [array]"
    \- "dec byte [array]"
    \< "dec eax"
    \> "inc eax"
    \. "call print_character"
    \, "call read_character"
    \R "ret"
    ;;assume it's a loop
    (str "call loop" character)))

(defn generate-segment [name & statements]
  (str "segment ." name \newline (str/join \newline statements) \newline))

(defn generate-label [name & statements]
  (str name \: \newline (str/join \newline statements) \newline))


(def print-character 
  (generate-label "print_character" ["push eax" "push ecx" "push ebx" "push edx" "mov ecx, eax" 
    "mov eax, 0x04" "mov ebx, 0x01" "mov edx, 0x01" "int 0x80" "pop edx" "pop ebx" "pop ecx" "pop eax" "ret"]))

(defn generate-assembly [ast]
  (dotimes [i (count ast)]
    (cond 
      (str/includes (nth (keys ast) i) "loop") (println (generate-loop (keys ast) i) (map brainfuck-to-assembly (nth (vals ast) i))) 
      :else (println (generate-label (nth (keys ast) i) (map brainfuck-to-assembly (nth (vals ast) i)))))))

