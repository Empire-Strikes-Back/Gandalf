(ns csbooks.impl
  (:require [puget.printer :as pug]
            [clojure.repl :refer :all]))


(defn cprn
  "color-prints a value "
  ([x]
   (pug/cprint x nil))
  ([x opts]
   (pug/cprint x opts)))



(comment
  
  (cprn [1 2 3 4 5])

  ;;;
  )