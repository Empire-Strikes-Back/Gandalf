(ns Gandalf.sicp
  (:require [clojure.repl :refer :all]
            [Gandalf.sicp-ch1]
            [Gandalf.sicp-ex1]))

; Structure and Interpretation of Computer Programs second edition 1996

(comment
  
  (prn 3)
  ;
  )

; counting change

(defn first-denomination
  [kinds-of-coins]
  (cond
    (= kinds-of-coins 1) 1
    (= kinds-of-coins 2) 5
    (= kinds-of-coins 3) 10
    (= kinds-of-coins 4) 25
    (= kinds-of-coins 5) 50
    :else nil))

(defn cc
  [amount kinds-of-coins]
  (cond
    (= amount 0) 1
    (or (< amount 0) (= kinds-of-coins 0)) 0
    :else (+
           (cc amount (- kinds-of-coins 1))
           (cc (- amount (first-denomination kinds-of-coins)) kinds-of-coins))))

(defn count-change
  [amount]
  (cc amount 5))

(comment
  
  (count-change 100 ) ; 292
  
  ;
  )