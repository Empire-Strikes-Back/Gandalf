(ns Gandalf.sicp-ex1
  (:require [clojure.repl :refer :all]
            [Gandalf.sicp-ch1 :refer [square average improve sqrt good-enough?  abs]]))
  
  ;; Exercise 1.3
  
  
(def sum-squares-of-two-max-numbers 
  (fn [x y z]
    (cond
      (and (> x y) (> y z) ) (+ (square x) (square y)) 
      ( and (< x y) (< y z) ) (+ (square y) (square z))
      :else (+ (square x) (square z)))))
        
      
      
    
  
(sum-squares-of-two-max-numbers 1 2 3)
  
(sum-squares-of-two-max-numbers 1 1 3)
  
(sum-squares-of-two-max-numbers -2 1 3)
  
(sum-squares-of-two-max-numbers 4 1 2)
  
  
  ;; Exercise 1.4
  
(def a 4)
(def b -3)
  
(- b 1)
  
(- a b)
  
  ;; Exercise 1.5 Ben Bitdiddle
  
(def p (fn [] (p)))
(def test-xy (fn [x y]
               (if (= x 0) 0 y)))
                 
  
  ; (test-xy 0 (p) ) will be infinite loop
  
  ; applicative - inifine loop
  ; normal - 0
  
  ;; Exercise 1.6 
  
(defn new-if 
  [predicate then-clause else-clause]
  (cond
    predicate then-clause
    :else else-clause))
      
    
  
(new-if (= 2 3) 0 5)
(new-if (= 1 1) 0 5)
  
(defn sqrt-iter
  [guess x]
  (new-if (good-enough? guess x) 
          guess
          (sqrt-iter (improve guess x) x))) 
            
    

(defn sqroot
  [x]
  (sqrt-iter 1.0 x)) 
    

; (sqroot 2) ; will fail, as new-if will evaluate second argument, will be StackOverflow


(sqrt 2)
(Math/sqrt 2)

(sqrt 0.1)
(Math/sqrt 0.1)


(sqrt 0.001)
(Math/sqrt 0.001)


; (sqroot2 10000000000000) stackoverflow
(Math/sqrt 10000000000000)


;; Exerseise 1.7  - yes, usign guess quotient is better

(defn ok-enough?
  [guess  old-guess]
  ; (prn guess " , " old-guess)
  (< (abs (- (/ guess old-guess) 1)) 0.001))


(defn  sqrt-iter2
  [guess old-guess  x]
  (if (ok-enough? guess old-guess)
    guess
    (sqrt-iter2 (improve guess x) guess x)))
    ;  (recur (improve guess x) guess x)
    


(defn sqroot2
  [x]
  (sqrt-iter2 1.0 0.1 x))

(ok-enough? 1.0 0.9)

(Math/sqrt 2) ; 1.4142135623730951
(sqrt 2)      ; 1.4142156862745097
(sqroot2 2)   ; 1.4142135623746899

(Math/sqrt 0.1) ; 0.31622776601683794
(sqrt 0.1)      ; 0.316245562280389
(sqroot2 0.1)   ; 0.31622776651756745

(Math/sqrt 0.001) ; 0.03162277660168379
(sqrt 0.001)      ; 0.04124542607499115
(sqroot2 0.001)   ; 0.03162278245070105

; (sqrt   10000000000000) ; stackoverflow
(sqroot2   10000000000000) 
(Math/sqrt 10000000000000)

  
  
;; Exercise 1.8

(defn improve-cbrt
  [x guess]
  (/ 
    (+ (/ x (square guess)) (* guess 2)) 
    3)) 
    
  

(improve-cbrt 2 1.5)

(defn cbrt-iter
  [ guess old-guess x]
  (if (ok-enough? guess old-guess) 
    guess
    (cbrt-iter (improve-cbrt x guess) guess x)))

(defn cbrt [x]
  (cbrt-iter 1.0 0.1 x))
  


(Math/cbrt 2)
(cbrt 2)

;; Exercise 1.9

;; both are iterative procedures
;; the first is linear iterative process  uses tail-recursion (if implemented) 
;; the second is linear recursive process with growing stack
(defn +' 
  [a b]
  (if (= b 0)
    a
    (+' (inc a) (dec b)))) ;; 

(+' 1 2)

(defn +'
  [a b]
  (if (= b 0)
    a
    (inc (+' a (dec b)) )))

(+' 1 2)

;; Exercise 1.10

(defn A 
  [x y]
  (cond
    (= y 0) 0
    (= x 0) (* 2 y)
    (= y 1) 2
    :else (A (- x 1) (A x (- y 1)))))

(A 1 2)
(A 3 2)
(A 3 3)

(A 1 10)
(A 2 4)
(A 3 3)

(Math/pow 2 16)
(Math/pow 2 32)
(Math/pow 2 10)

; (A 2 5)


(defn f [n] (A 0 n))  ;  (f n) 2n
(defn g [n] (A 1 n))  ;  (g n) 2^n
(defn h [n] (A 2 n))  ;  (h n) 2^(h (- n 1))
(defn k [n] (* 5 n n)) ; (k n) 5n^2

; (h 3)
; (h 4)

  
  (declare ack-3)

(defn ack-3
  "The Ackermann function using a stack and only tail recursion.
  Thanks to Allan Malloy's post at:
  http://grokbase.com/p/gg/clojure/127rbk4518/reduction-to-tail-recursion"
  [m n]
  (loop [m     m
         n     n
         stack ()]
    (cond
      (zero? m)
      (if (empty? stack)
        (inc n)
        (recur (peek stack) (inc n) (pop stack)))
      (zero? n) (recur (dec m) 1 stack)
      :else (recur m (dec n) (conj stack (dec m))))))

; (ack-3 2 4)
  
  
