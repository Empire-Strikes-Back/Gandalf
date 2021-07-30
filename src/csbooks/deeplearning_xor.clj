(ns csbooks.deeplearning-xor
  (:require [csbooks.deeplearning-mx :refer :all])
  )

; exclusive or

(defn xor
  "returns 1 if exacly one of (a,b) is equal to 1
  returns 0 otherwise"
  [a b]
  (cond
    (and (= 0 a) (= 1 b)) 1
    (and (= 1 a) (= 0 b)) 1
    :else 0))

(def X-points [[0 0] [0 1] [1 0] [1 1]])

(defn ReLU
  "returns a number
  rectified linear unit
  a non-liniear actiavation fucntion, applied element-wise
  "
  [z]
  (max 0 z))


(comment

  (xor 1 1)
  (xor 1 0)
  (xor 0 0)
  (xor 0 1)


  (max 0 -1)

  (ReLU -0.1)


  ;;;
  )

(defn neural-network-xor
  "returns the target (result) of processing x vector of features
  
  h - vector of hidden units
  h = f1(x; W,c)
  h = g(W^Tx +C ), where g is an activation fucntion
  h[i] = g(x^TW[:,i] + c[i]) - element of h
  
  f2 = g
  
  f1 - linear transformation
  
  f(x; W,c,w,b) = f2 (f1 (x))
  
  
  f(x; W,c,w,b) = w^Tmax{0, W^Tx +c } + b
  
  
  X - design matrix
  W - matrix, weights for the linear transformation
  x - feature vector
  c  - vector of biases
  b - scalar 
  max{0,z} - non-linear activation function
  w - weights vector
  
  "
  [W,W-width,c,w,b,X,X-width,x]
  (as-> x e
    ; (multiply (transpose W W-width) x (/ (count W) W-width) 1)
    (multiply  X W X-width W-width)
    (mx-add-vec e W-width c)
    (mapv ReLU e)
    (multiply e w W-width 1)
    (add-scalar e b)))
  
  (comment

    ; let
    (def W (mkmx [[1 1]
                  [1 1]]))

    (def c [0 -1])

    (def w [1 -2])

    (def b 0)


    ; let X be the design matrix containing all four XOR poits int he binary input space, with one example per row

    (def X (mkmx [[0 0]
                  [0 1]
                  [1 0]
                  [1 1]]))

    (neural-network-xor W 2 c w b X 2 []) ; [0 1 1 0]




    ;;;
    )
  

; 