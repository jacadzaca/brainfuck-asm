(ns brainfuck-asm.parser
  (:require [clojure.string :as str]))

(defn remove-initial-comment-loop [string]
  (if (str/starts-with? string "[")
    (loop [i 1 matching-bracket-count 1]
      (if (zero? matching-bracket-count)
        (subs string i)
        (recur (inc i)
               (if (= (nth string i) \[)
                 (inc matching-bracket-count)
                 (if (= (nth string i) \])
                   (dec matching-bracket-count)
                   matching-bracket-count)))))
    string))

(defn- balanced?
  "Returns whether brackets contained in expr are balanced"
  ([expr] (balanced? expr 0))
  ([[x & xs] count]
   (cond (neg? count) false
         (nil? x) (zero? count)
         (= x \[) (recur xs (inc count))
         (= x \]) (recur xs (dec count))
         :else (recur xs count))))

(defn create-statement
  ([type]
   {:type type})
  ([type argument]
   {:type      type
    :argument argument}))

(defn- craete-loop [name]
  {:name name
   :type :loop
   :statements []})

(defn- brainfuck->ast-node [character]
  (case character
    \+ (create-statement :inc)
    \- (create-statement :dec)
    \> (create-statement :inc-pointer)
    \< (create-statement :dec-pointer)
    \. (create-statement :call-print)
    \, (create-statement :call-read)
    (throw (IllegalArgumentException. (format "%s is not a valid brainfuck symbol" character)))))

(defn generate-ast
  ([brainfuck-symbols]
   {:pre [(balanced? brainfuck-symbols)]}
   (generate-ast '() {:type :entrypoint :statements [{:type :load-array}]} '() brainfuck-symbols 0))
  ([ast current-label stack [character & characters] loop-count]
   (case character
     nil (apply conj ast (update current-label :statements conj (create-statement :call-exit)) stack)
     \[  (recur ast
                (craete-loop (str "loop" loop-count))
                (conj stack (update current-label :statements conj (create-statement :call-loop loop-count)))
                characters
                (inc loop-count))
     \]  (recur (conj ast (update current-label :statements conj (create-statement :loop-end (:name current-label))))
                (peek stack)
                (pop stack)
                characters
                loop-count)
     (recur ast
            (update current-label :statements conj (brainfuck->ast-node character))
            stack
            characters
            loop-count))))
