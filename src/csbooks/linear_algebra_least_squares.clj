(ns csbooks.linear-algebra-least-squares
  (:require [clojure.repl :refer :all]
            [csbooks.linear-algebra-print :refer [cprn]]
            [csbooks.linear-algebra-math :refer :all])
  ;
  )


(defn least-squares-x
  "Returns the approximate solution to 
  Ax=b
  "
  [widA A b]
  ; (QR-linear-equation widA A b)
  (linear-equation-pseudo-inverse widA A b))


(defn least-squares-X
  "Returns the solution of
  AX = B
  "
  [widA widB A B]
  (as-> nil E
    (mx-pseudo-inverse widA A)
    (mx-prod (/ (count A) widA) widB E B)))

(comment

  ; residual -  remaining after the greater part or quantity has gone.

  (def A (rows->mx [[2 0]
                    [-1 1]
                    [0 2]]))

  (QR-linear-equation 2 A [1 0 -1])

  (least-squares-x 2 A [1 0 -1])

  (linear-equation-pseudo-inverse 2 A [1 0 -1])

  (least-squares-x 2 A [2 1 0])


  (def B (cols->mx [[1 0 -1]
                    [2 1 0]]))

  (prn-mx 2 (least-squares-X 2 2 A B))

  (def B (cols->mx [[1 0 -1]
                    [2 1 0]
                    [-1 3 1]]))
  (least-squares-x 2 A [-1 3 1])
  

  (prn-mx 3 (least-squares-X 2 3 A B))

  ;;;
  )


(comment

  (def R (rows->mx [[0.97 1.86 0.41]
                    [1.23 2.18 0.53]
                    [0.80 1.24 0.62]
                    [1.29 0.98 0.51]
                    [1.10 1.23 0.69]
                    [0.67 0.34 0.54]
                    [0.87 0.26 0.62]
                    [1.10 0.16 0.48]
                    [1.92 0.22 0.71]
                    [1.29 0.12 0.62]]))

  (/ 2.18 0.12)

  (def v-des (make-vec 10 1000))

  (least-squares-x 3 R v-des)
  ; [62.07662454385152 99.98500402826195 1442.837462541221]

  (Math/round 62.07662454385152)

  ; (/ 2M 3M)
  ; (with-precision 2 (/ 2M 3M))

  (root-mean-square (least-squares-x 3 R v-des))
  
  (hash "asd")
  
  (.hashCode "asd")
  
  ;;;
  )




(comment

;   the n-vector x (i) is the feature vector and the scalar y (i) is the associated
; value of the outcome for data sample i. We sometimes refer to the pair x (i) , y (i)
; as the ith data pair. These data are also called observations, examples, samples
; or measurements, depending on the context.

  

  ;;;
  )
