(ns Gandalf.linear-algebra
  (:require [clojure.repl :refer :all]
            [Gandalf.linear-algebra-math :as math]
            [Gandalf.linear-algebra-print :refer [cprn]]
            [Gandalf.linear-algebra-kmeans]
            [Gandalf.linear-algebra-least-squares]
            [Gandalf.linear-algebra-iris]
            [Gandalf.linear-algebra-qr]

   ;
            ))

; applied linear algebra
; https://www.seas.ucla.edu/~vandenbe/ee133a.html
; Introduction to Applied Linear Algebra  Stephen Boyd  Lieven Vandenberghe

; page 449 pdf - notaion 

; f(x) - real-valued, scalar-valued (returns a scalar)

(comment
  ; Exercises 1

  ; 1.4
  ; w = [ d{1} d{n} ] n in t%24
  ; d = w{t:t+24}

  ; 1.6

  ; d = x{1,n} - [0,x{2}..., x{n-1}]

  

  ;;;
  )