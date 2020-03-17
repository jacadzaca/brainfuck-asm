(ns brainfuck-asm.core
  (:require [brainfuck-asm.parser :as parser]
            [brainfuck-asm.optimizer :as optimizer]
            [brainfuck-asm.generator :as generator])
  (:gen-class))

(defn- optimize-ast [ast]
  (map #(update % :statements optimizer/optimize-sentence) ast))

(defn ^:private ^:const sanitize-input [input]
  (let [legal-characters #{\+ \- \< \> \. \, \[ \]}]
    (filter #(contains? legal-characters %) input)))

(defn -main [input-file-name & args]
  (-> input-file-name slurp parser/remove-initial-comment-loop sanitize-input parser/generate-ast optimize-ast generator/generate-assembly print))
