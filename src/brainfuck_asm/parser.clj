(ns brainfuck-asm.parser
  (:require [clojure.string :as str]))

(defn ^:const balanced?
  "Returns whether brackets contained in expr are balanced"
  ([expr] (balanced? expr 0))
  ([[x & xs] count]
   (cond (neg? count) false
         (nil? x) (zero? count)
         (= x \[) (recur xs (inc count))
         (= x \]) (recur xs (dec count))
         :else (recur xs count))))

(defn ^:const create-statement
  ([type]
   {:type type})
  ([type argument]
   {:type      type
    :argument argument}))

(defn- ^:const craete-loop [name]
  {:name name
   :type :loop
   :statements []})

(defn- ^:const brainfuck->ast-node [character]
  (case character
    \+ (create-statement :inc)
    \- (create-statement :dec)
    \> (create-statement :inc-pointer)
    \< (create-statement :dec-pointer)
    \. (create-statement :call-print)
    \, (create-statement :call-read)
    (throw (IllegalArgumentException. (format "%s is not a valid brainfuck symbol" character)))))

(defn ^:const generate-ast
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
