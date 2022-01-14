(ns Gandalf.repl
  (:require [clojure.repl :refer :all]
            [clojure.java.javadoc :refer [javadoc]]
            ;
            ))


(defn javadoc-print-url
  "Opens a browser window displaying the javadoc for the argument.
  Tries *local-javadocs* first, then *remote-javadocs*."
  {:added "1.2"}
  [class-or-object]
  (let [^Class c (if (instance? Class class-or-object)
                   class-or-object
                   (class class-or-object))]
    (if-let [url (#'clojure.java.javadoc/javadoc-url (.getName c))]
    ;   (browse-url url)
      url
      (println "Could not find Javadoc for" c))))


(comment

  (source javadoc)
  (source clojure.java.javadoc/javadoc-url)


  (apropos "javadoc-url")

  (javadoc-print-url Runtime)
  (javadoc-print-url String)

;;;
  )

(comment

  ; (Examples/hello)
  {:hello "world"}

  (System/getProperty "java.vm.version")
  (System/getProperty "java.version")
  (System/getProperty "java.specification.version")
  (clojure-version)


  ;
  )