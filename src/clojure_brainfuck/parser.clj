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

(defn create-statement 
  ([type]
    {:type type})
  ([type arguemnt]
    {:type      type
     :arguemnt arguemnt}))

(defn craete-loop [name]
  {:name name
   :type :loop
   :statements []})

(defn brainfuck-to-ast-node [character]
  (case character
    \+ (create-statement :inc)
    \- (create-statement :dec)
    \> (create-statement :inc-pointer)
    \< (create-statement :dec-pointer)
    \. (create-statement :call-print)
    \, (create-statement :call-read)))

(defn generate-ast [string]
  (loop [ast () current-label {:type :entrypoint :statements []} stack () i 0 loop-count 0]
    (if (= (count string) i)
      (apply conj ast current-label stack)
      (case (.charAt string i)
        \[ (recur ast
                  (craete-loop (str "loop" loop-count))
                  (conj stack (update current-label :statements conj (create-statement :call-loop loop-count)))
                  (inc i)
                  (inc loop-count))
        \] (recur (conj ast current-label)
                  (first stack)
                  (pop stack)
                  (inc i)
                  loop-count)
           (recur ast
                  (update current-label :statements conj (brainfuck-to-ast-node (.charAt string i)))
                  stack
                  (inc i)
                  loop-count)))))

(defn brainfuck-to-assembly [character]
  (case character
    \+ "inc byte [eax]"
    \- "dec byte [eax]"
    \< "dec eax"
    \> "inc eax"
    \. "call print_character"
    \, "call read_character"
    ;;assume it's a loop
    (str "call loop" character)))

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

