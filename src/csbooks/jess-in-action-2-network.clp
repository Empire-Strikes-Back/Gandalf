; 
; The Rete algorithm demo rules from chapter 8 of "Jess in Action"
;

(deftemplate x (slot a))
(deftemplate y (slot b))
(deftemplate z (slot c))

(defrule example-1 
    (x (a ?v1)) 
    (y (b ?v1)) 
    => )

(defrule example-2 
    (x (a ?v2)) 
    (y (b ?v2)) 
    (z) 
    => )
