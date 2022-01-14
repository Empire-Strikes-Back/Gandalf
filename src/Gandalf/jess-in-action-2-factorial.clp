;
; The backward chaining demo from chapter 7 of "Jess in Action"
;

(do-backward-chaining factorial)

(defrule print-factorial-10 
    (factorial 10 ?r1) 
    => 
    (printout t "The factorial of 10 is " ?r1 crlf))

(defrule do-factorial 
    (need-factorial ?x ?) 
    => 
    ;; compute the factorial of ?x in ?r 
    (bind ?r 1) 
    (bind ?n ?x) 
    (while (> ?n 1) 
        (bind ?r (* ?r ?n)) 
        (bind ?n (- ?n 1))) 
    (assert (factorial ?x ?r)))

(reset)

(run)
