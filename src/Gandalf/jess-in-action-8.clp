
(printout t "---How Jess works" crlf)
(clear)

(defrule library-rule-1
(book (name ?x) (status late) (borrower ?y))
(borrower (name ?y) (address ?z))
=>
(send-late-notice ?x ?y ?z))


(reset)
(run)