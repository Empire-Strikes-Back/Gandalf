(ns csbooks.deeplearning-mx-prob
  (:require [csbooks.deeplearning-mx]))


(defn logistic-sigmoid
  "returns the value (numeric) of sigmoid(x)"
  [x]
  (/ 1 (+ 1 (Math/exp (* x -1)))))

(defn softplus
  "returns a number"
  [x]
  (Math/log (+ 1 (Math/exp x))))

(defn logit
  "return a number"
  [x]
  (Math/log (/ x (- 1 x))))


(comment

  (def x 3)

  (logistic-sigmoid 0)
  (logistic-sigmoid 1)
  (logistic-sigmoid 200)
  (logistic-sigmoid 1.1)
  (logistic-sigmoid 9)


  (softplus 3)



  ;;;
  )