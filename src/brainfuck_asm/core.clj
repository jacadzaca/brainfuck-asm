(ns brainfuck-asm.core
  (:require [brainfuck-asm.parser :as parser]
            [brainfuck-asm.optimizer :as optimizer]
            [brainfuck-asm.generator :as generator]
            [clojure.java.io :as io])
  (:gen-class))

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
        (-> (first args) 
            slurp 
            parser/remove-initial-comment-loop
            sanitize-input 
            parser/generate-ast 
            optimize-ast 
            generator/generate-assembly 
            println)
      :else (println (str "Cannot find: " input-file-name "\nExiting...")))))
