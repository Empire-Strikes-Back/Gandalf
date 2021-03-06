(ns Gandalf.mxnet-bert-qa-infer
  (:require [clojure.string :as string]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [org.apache.clojure-mxnet.dtype :as dtype]
            [org.apache.clojure-mxnet.context :as context]
            [org.apache.clojure-mxnet.layout :as layout]
            [org.apache.clojure-mxnet.ndarray :as ndarray]
            [org.apache.clojure-mxnet.infer :as infer]
            [clojure.pprint :as pprint]))


(comment
  infer
  
  )

(def model-path-prefix "data/static_bert_qa")

;; the maximum length of the sequence
(def seq-length 384)
; (def seq-length 8000)


;;; data helpers

(defn break-out-punctuation [s str-match]
  (->> (string/split (str s "<punc>") (re-pattern (str "\\" str-match)))
       (map #(string/replace % "<punc>" str-match))))

(defn break-out-punctuations [s]
  (if-let [target-char (first (re-seq #"[.,?!]" s))]
    (break-out-punctuation s target-char)
    [s]))

(defn tokenize [s]
  (->> (string/split s #"\s+")
       (mapcat break-out-punctuations)
       (into [])))

(defn pad [tokens pad-item num]
  (if (>= (count tokens) num)
    tokens
    (into tokens (repeat (- num (count tokens)) pad-item))))

(defn get-vocab []
  (let [vocab (json/parse-stream (io/reader "data/vocab.json"))]
    {:idx->token (get vocab "idx_to_token")
     :token->idx (get vocab "token_to_idx")}))

(defn tokens->idxs [token->idx tokens]
  (let [unk-idx (get token->idx "[UNK]")]
    (mapv #(get token->idx % unk-idx) tokens)))

(defn post-processing [result tokens]
  (let [output1 (ndarray/slice-axis result 2 0 1)
        output2 (ndarray/slice-axis result 2 1 2)
        ;;; get the formatted logits result
        start-logits (ndarray/reshape output1 [0 -3])
        end-logits (ndarray/reshape output2 [0 -3])
        start-prob (ndarray/softmax start-logits)
        end-prob (ndarray/softmax end-logits)
        start-idx (-> (ndarray/argmax start-prob 1)
                      (ndarray/->vec)
                      (first))
        end-idx (-> (ndarray/argmax end-prob 1)
                    (ndarray/->vec)
                    (first))]
    (if (> end-idx start-idx)
      (subvec tokens start-idx (inc end-idx))
      (subvec tokens end-idx (inc end-idx)))))

(defn make-predictor [ctx]
  (let [input-descs [{:name "data0"
                      :shape [1 seq-length]
                      :dtype dtype/FLOAT32
                      :layout layout/NT}
                     {:name "data1"
                      :shape [1 seq-length]
                      :dtype dtype/FLOAT32
                      :layout layout/NT}
                     {:name "data2"
                      :shape [1]
                      :dtype dtype/FLOAT32
                      :layout layout/N}]
        factory (infer/model-factory model-path-prefix input-descs)]
    (infer/create-predictor
     factory
     {:contexts [ctx]
      :epoch 2})))

(defn pre-processing [ctx idx->token token->idx qa-map]
  (let [{:keys [input-question input-answer ground-truth-answers]} qa-map
       ;;; pre-processing tokenize sentence
        token-q (tokenize (string/lower-case input-question))
        token-a (tokenize (string/lower-case input-answer))
        valid-length (+ (count token-q) (count token-a))
        ;;; generate token types [0000...1111...0000]
        qa-embedded (into (pad [] 0 (count token-q))
                          (pad [] 1 (count token-a)))
        token-types (pad qa-embedded 0 seq-length)
        ;;; make BERT pre-processing standard
        token-a (conj token-a "[SEP]")
        token-q (into [] (concat ["[CLS]"] token-q ["[SEP]"] token-a))
        tokens (pad token-q "[PAD]" seq-length)
        ;;; pre-processing - token to index translation

        indexes (tokens->idxs token->idx tokens)]
    {:input-batch [(ndarray/array indexes [1 seq-length] {:context ctx})
                   (ndarray/array token-types [1 seq-length] {:context ctx})
                   (ndarray/array [valid-length] [1] {:context ctx})]
     :tokens tokens
     :qa-map qa-map}))

(defn inf
  ([] (inf (context/default-context)))
  ([ctx]
   (let [predictor (make-predictor ctx)
         {:keys [idx->token token->idx]} (get-vocab)
        ;;; samples taken from https://rajpurkar.github.io/SQuAD-explorer/explore/v2.0/dev/
         question-answers (clojure.edn/read-string (slurp "resources/squad-samples.edn"))]
     (doseq [qa-map question-answers]
       (let [{:keys [input-batch tokens qa-map]} (pre-processing ctx idx->token token->idx qa-map)
             result (first (infer/predict-with-ndarray predictor input-batch))
             answer (post-processing result tokens)]
         (println "===============================")
         (println "      Question Answer Data")
         (pprint/pprint qa-map)
         (println)
         (println "  Predicted Answer: " answer)
         (println "==============================="))))
   ))

(defn inf-many
  [question-data]
    (let [ctx (context/cpu)
          predictor                       (make-predictor ctx)
          {:keys [idx->token token->idx]} (get-vocab)
        ;;; samples taken from https://rajpurkar.github.io/SQuAD-explorer/explore/v2.0/dev/
          question-answers                question-data]
      (doseq [qa-map question-answers]
        (let [{:keys [input-batch tokens qa-map]} (pre-processing ctx idx->token token->idx qa-map)
              result                              (first (infer/predict-with-ndarray predictor input-batch))
              answer                              (post-processing result tokens)]
          (println "===============================")
          (println "      Question Answer Data")
          (pprint/pprint qa-map)
          (println)
          (println "  Predicted Answer: " answer)
          (println "==============================="))))
  )

(defn infer-one
  [question-datum]
  (->
   question-datum
   vector
   inf-many
   )
  )

(defn -main [& args]
  (let [[dev] args]
    (if (= dev ":gpu")
      (inf (context/gpu))
      (inf (context/cpu)))))

(comment

  (inf)

  (inf (context/cpu))
  (inf (context/gpu)); [05:07:16] src/storage/storage.cc:118: Compile with USE_CUDA=1 to enable GPU usage 

  
  (clojure.repl/source doseq)
  
  ;
  )
