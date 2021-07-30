;;

;;

(ns csbooks.mxnet-rnn-test-char
  (:require [clojure.string :as string]
            [clojure.java.shell :refer [sh]]
            [csbooks.mxnet-rnn-impl]
            [csbooks.mxnet-rnn-lstm :as lstm]
            [org.apache.clojure-mxnet.context :as context]
            [org.apache.clojure-mxnet.executor :as executor]
            [org.apache.clojure-mxnet.module :as m]
            [org.apache.clojure-mxnet.ndarray :as ndarray]))

(when-not (.exists (clojure.java.io/file "data"))
  (do (println "Retrieving data...") (sh "./get_data.sh")))

(def data-path "data/obama.txt")
(def model-prefix)
(def start-sentence "The joke ")
(def num-hidden 512) ;; hidden unit in LSTM cell
(def num-embed 256) ;; the embedding dim (a char is mapped to 256 dim)
(def num-lstm-layer 3) ;; number of lstm layers

(def vocab (csbooks.mxnet-rnn-impl/build-vocab data-path))

(defn rnn-test [model-prefix epoch-num seq-length random?]
  (let [trained-mod (m/load-checkpoint {:prefix model-prefix :epoch epoch-num})
        trained-arg-params (m/arg-params trained-mod)
        model (lstm/lstm-inference-model {:num-lstm-layer 3
                                          :input-size (inc (count vocab))
                                          :num-label (inc (count vocab))
                                          :num-hidden num-hidden
                                          :num-embed num-embed
                                          :arg-params trained-arg-params})
        input-ndarray (ndarray/zeros [1])
        revert-vocab (csbooks.mxnet-rnn-impl/make-revert-vocab vocab)
        fix-dict (into [""]
                       (mapv #(str (get revert-vocab %))
                             (sort (vals vocab))))
        random-sample random? ;; use this to do random sample or max prob
        ignore-length (count start-sentence)]
    (println "Starter sentence: " start-sentence)
    (println "===")
    (loop [i 0
           new-sentence true
           output start-sentence]
      (if (= seq-length i)
        output
        (do
          (if (<= i (dec ignore-length))
            (csbooks.mxnet-rnn-impl/make-input (get start-sentence i) vocab input-ndarray)
            (csbooks.mxnet-rnn-impl/make-input (last output) vocab input-ndarray))
          (let [prob (ndarray/->vec (lstm/forward model input-ndarray new-sentence))
                next-char (csbooks.mxnet-rnn-impl/make-output prob fix-dict random-sample)]
            (recur (inc i)
                   (if (= "" next-char) true false)
                   (if (< i (dec ignore-length))
                     output
                     (str output next-char)))))))))

(comment

  (rnn-test "data/obama" 75 200 false)
  ;=>"The joke that we can start by the challenges of the American people. The American people have been talking about how to compete with the streets of San Antonio who the courage to come together as one "

  (rnn-test "data/obama" 75 200 true)
  ;=>"The joke before them prepared for five years ago, we only hear a chance to lose our efforts and they made striggling procedural deficit at the city between a politics in the efforts on the Edmund Pett"
)
