(defproject om-converter "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}

  :min-lein-version "2.3.4"

  ;; We need to add src/cljs too, because cljsbuild does not add its
  ;; source-paths to the project source-paths
  :source-paths ["src/cljs"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2156"]
                 [om "0.6.2"]]

  :plugins [[com.cemerick/austin "0.1.4"]
            [lein-cljsbuild "1.0.3"]]

  :repl-options {:init-ns om-converter.core}

  :cljsbuild {:builds { :dev { :source-paths ["src/cljs"]
                               :compiler { :output-to "public/js/om-converter.js"
                                           :optimizations :whitespace
                                           :pretty-print true}}}})