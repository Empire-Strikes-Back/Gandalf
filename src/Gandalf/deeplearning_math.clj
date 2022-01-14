(ns Gandalf.deeplearning-math
  (:require [clojure.repl :refer :all]))

(defn argmin
  "Returns the argmin of  ..."
  []
  0)

(defn sum
  "Returns the sum of numbers.
   Sum is a linear combintaion of scalars"
  [a]
  (reduce + 0 a))

(defn fact
  "Returns factorial of x"
  [x]
  (reduce * 1 (range 1 (inc x))))

(comment
  
  (fact 20)
  
  
  ;;;
  )