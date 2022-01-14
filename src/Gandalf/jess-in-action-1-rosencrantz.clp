    ;; Jess in action: java rule based systems


;;
;; Mrs. Rosencrantz' math problem from chapter 1 of "Jess in Action"
;;

(deftemplate pants-color (slot of) (slot is))
(deftemplate position (slot of) (slot is))

(defrule generate-combos
  =>
  (foreach ?name (create$ Fred Joe Bob Tom)
           (foreach ?color (create$ red blue plaid orange)
                    (assert (pants-color (of ?name)
                                   (is ?color))))

           
           (foreach ?position (create$ 1 2 3 4)
                    (assert (position (of ?name)
                                      (is ?position))))))

(defrule find-solution
  ;; The golfer to Fred's right is wearing blue pants.
  (position (of Fred) (is ?p1))
  (pants-color (of Fred) (is ?c1))

  (position (of ?n&~Fred) (is ?p&:(eq ?p (+ ?p1 1))))
  (pants-color (of ?n&~Fred) (is blue&~?c1))

  ;; Joe is in position #2
  (position (of Joe) (is ?p2&2&~?p1))
  (pants-color (of Joe) (is ?c2&~?c1))

  ;; Bob is wearing the plaid pants
  (position (of Bob) (is ?p3&~?p1&~?p&~?p2))
  (pants-color (of Bob&~?n) (is plaid&?c3&~?c1&~?c2))

  ;; Tom isn't in position 1 or 4 and isn't wearing orange
  (position (of Tom&~?n) (is ~1&~4&?p4&~?p1&~?p2&~?p3))
  (pants-color (of Tom) (is ?c4&~orange&~blue&~?c1&~?c2&~?c3))
  =>
  (printout t Fred  " " ?p1 " " ?c1 crlf)
  (printout t Joe  "  " ?p2 " " ?c2 crlf)
  (printout t Bob  "  " ?p3 " " ?c3 crlf)
  (printout t Tom  "  " ?p4 " " ?c4 crlf)
  )


(reset)
(run)
