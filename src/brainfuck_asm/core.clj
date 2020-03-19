(ns brainfuck-asm.core
  (:require [brainfuck-asm.parser :as parser]
            [brainfuck-asm.optimizer :as optimizer]
            [brainfuck-asm.generator :as generator]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:gen-class))

(defn- remove-initial-comment-loop 
  ([sequence] (case (first sequence)
                \[ (remove-initial-comment-loop (drop 1 sequence) 1)
                sequence))
  ([[character & chars] matching-bracket-count]
    (cond
      (nil? character) (throw (IllegalArgumentException. "Unbalanced braces"))
      (zero? matching-bracket-count)  (str character (str/join chars))
      (not= 0 matching-bracket-count) (recur chars (case character
                                        \[ (inc matching-bracket-count)
                                        \] (dec matching-bracket-count)
                                        matching-bracket-count)))))

(defn- optimize-ast [ast]
  (map #(update % :statements optimizer/optimize-sentence) ast))

(defn ^:private ^:const sanitize-input [input]
  (let [legal-characters #{\+ \- \< \> \. \, \[ \]}]
    (filter #(contains? legal-characters %) input)))

(defn -main [& args]
  (let [input-file-name (first args)]
    (cond 
      (= input-file-name nil) (println "Please specify a brainfuck source file to compile")
      (.exists (io/file input-file-name))
        (let [brainfuck-code (slurp input-file-name)]
        (if (parser/balanced? brainfuck-code) 
          (-> brainfuck-code
                      remove-initial-comment-loop
                      sanitize-input 
                      parser/generate-ast 
                      optimize-ast 
                      generator/generate-assembly 
                      println)
          (println "Your code's braces are unbalanced (missing [ or ]).")))
      :else (println (str "Cannot find: " input-file-name "\nExiting...")))))
