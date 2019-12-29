(defproject clojure-brainfuck "0.1.0-SNAPSHOT"
  :description "A brainfuck compiler in Clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :main ^:skip-aot clojure-brainfuck.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[jonase/eastwood "0.3.5"]
            [lein-kibit "0.1.8"]
            [lein-cljfmt "0.6.6"]])
