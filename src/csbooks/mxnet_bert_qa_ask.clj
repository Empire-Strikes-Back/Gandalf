(ns csbooks.mxnet-bert-qa-ask
  (:require [clojure.repl :refer :all]
            [csbooks.mxnet-bert-qa-infer :refer [inf inf-many infer-one]]
            [clj-http.client :as client]
            [cheshire.core :refer :all]
   ;
            )
  )

; https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&&titles=Protein_(nutrient)
(defn fetch-wiki
  [title]
  (->
    (client/get
     (str "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&&titles=" title)
     {:accept :json})
   :body
   (parse-string)
  ;  (get-in ["query" "pages" "revisions"])
   )
  )

(defn page-text
  [page]
  (->
   page
   (get "query")
   (get "pages")
   first
   second
   (get "revisions")
   first
   (get "*"))
  )

(defn get-wiki-page-text
  [title]
  (->
   (fetch-wiki title)
   page-text
   )
  )


(defn infer-page
  [{:keys [title ground-truth-answers input-question ] :as opts }]
  (let [
        input-answer (get-wiki-page-text title)
        ]
    (infer-one (assoc opts :input-answer input-answer ))
    )
)


(comment
  (source get-in)
  (source ->)

  
  (def article (fetch-wiki "Protein_ (nutrient)"))


  (get-in article ["query"])

  (->
   article
   (get "query")
   (get "pages")
   first
   second
   (get "revisions")
   first
   (get "*"))

  (vector 1 2 3)

  (get-wiki-page-text "Protein_ (nutrient)")
  
  (infer-page {:title                "Protein_ (nutrient)"
               :ground-truth-answers ["plants"]
               :input-question       "What is the source of protein on a plant-based diet?"})
  
  
  (infer-one {:title                ""
              :ground-truth-answers ["?"]
              :input-question       "When was the Matrix released ?"
              :input-answer         "The Matrix is a 1999 science fiction action film written and directed by The Wachowskis [a] that stars Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss, Hugo Weaving, and Joe Pantoliano. It depicts a dystopian future in which humanity is unknowingly trapped inside a simulated reality called 'the Matrix' created by thought-capable machines (artificial beings) [b] to distract humans while using their bodies as an energy source. [4] When computer programmer Thomas Anderson, under the hacker alias 'Neo', uncovers this truth, he 'is drawn into a rebellion against the machines' [4] along with other people who have been freed from the Matrix.

The Matrix is an example of the cyberpunk subgenre of science fiction. [5] The Wachowskis' approach to action scenes was influenced by Japanese animation [6] and martial arts films, and the film's use of fight choreographers and wire fu techniques from Hong Kong action cinema influenced subsequent Hollywood action film productions. The film is known for popularizing a visual effect known as 'bullet time', where the heightened perception of certain characters is represented by allowing the action within a shot to progress in slow-motion while the camera appears to move through the scene at normal speed, allowing the sped-up movements of certain characters to be perceived normally. The film contains numerous allusions to philosophical and religious ideas, including existentialism, Marxism, Buddhism, feminism, nihilism, and postmodernism. [7] [8] While some critics have praised the film for its handling of difficult subjects, others have said the deeper themes are largely overshadowed by its action scenes."})

  
  ;
  )