(printout t "---" crlf)
(defrule null-rule
"A rule that does nothing"
=> 
)
(ppdefrule null-rule)

(watch facts)
(watch activations)
(watch rules)

(reset) ; pattern amtching happens here - comparing the LHS of rules to a given fact
(run) ; firing rules



; (defrule change-baby-if-wet
; "If baby is wet, change its diaper."
; ?wet <- (baby-is-wet)
; =>
; (change-baby)
; (retract ?wet))
(printout t "---" crlf)
(clear)
(watch all)
(reset)

(deffunction change-baby ()
(printout t "Baby is now dry" crlf))

(defrule change-baby-if-wet
"If baby is wet, change its diaper."
?wet <- (baby-is-wet)
=>
(change-baby)
(retract ?wet))

(assert (baby-is-wet))
(run)