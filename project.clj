(defproject brainfuck-asm "0.1.0-SNAPSHOT"
  :description "A brainfuck compiler in Clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :main ^:skip-aot brainfuck-asm.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[lein-kibit "0.1.8"]
            [lein-cljfmt "0.6.6"]])
