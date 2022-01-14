(ns Gandalf.deeplearning-mx-num
  (:require [Gandalf.deeplearning-mx]))

; numerical computation

;https://en.wikipedia.org/wiki/Softmax_function
(defn softmax
  "returns a vector 
  we apply the standard exponential function to each element  and normalize these values by dividing by the sum of all these exponentials; 
  this normalization ensures that the sum   of the components of the output vector is 1 "
  [v]
  (let [exps  (mapv (fn [x] (Math/exp x)) v)
        total (reduce + exps)]
    (mapv #(/ % total) exps)))

(comment
  (softmax [1 2 3 4 5])

  (reduce + (softmax [1 2 3 4 5]))

  (softmax [2 2 2 2 2])
  (reduce + (softmax [2 2  2 2 2]))


  ;;;
  )