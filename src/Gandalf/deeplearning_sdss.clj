(ns Gandalf.deeplearning-sdss
  (:require [Gandalf.deeplearning-mx :refer [transpose multiply multiply-scalar prnmx mkmx adjugate det-leibniz]]
            [clojure.repl :refer :all]
            [clojure.pprint :as pp]
            [puget.printer :as pug]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [Gandalf.deeplearning-linreg :refer [y-value]])
  (:import
   (org.apache.commons.io.input BOMInputStream)))


; https://www.kaggle.com/lucidlenn/sloan-digital-sky-survey

(defn read-column [filename column-index]
  (with-open [reader (io/reader filename)]
    (let [data (csv/read-csv reader)]
      ; (map #(nth % column-index) data) ; lazy
      (mapv #(nth % column-index) data)
      )))

(defn read-csv-file
  [filename]
  (with-open [reader (io/reader filename)]
    (let [data (doall (csv/read-csv reader))]
      data)))


(def sdss-filename "data/Skyserver_SQL2_27_2018 6_51_39 PM.csv")

(defn try-str->num
  "returns number or nil"
  [strin]
  (try (Float/parseFloat strin) (catch Exception e strin)))

(defn parse-numbers
  [data]
  (mapv #(mapv try-str->num %) data ))


(defn read-csv-data
  "reads and parses csv data"
  [filename]
  (-> (read-csv-file sdss-filename)
      parse-numbers))


(comment

  (def SDSS-DATA-CSV-RAW (read-csv-file sdss-filename))
  (def COLUMNS (first SDSS-DATA-CSV-RAW))
  (def SDSS-DATA-CSV-STRINGS (drop 1 SDSS-DATA-CSV-RAW))
  (def SDSS-DATA-CSV (parse-numbers SDSS-DATA-CSV-STRINGS))

  ;
  )

(comment

  (read-column sdss-filename 2)

  (with-open [reader (-> sdss-filename
                         io/input-stream
                        ;  BOMInputStream.
                         io/reader)]
    (doall (csv/read-csv reader)))


  (count (read-csv-file sdss-filename))

  (as-> nil x
    (nth  (read-csv-file sdss-filename) 9999)
    (update x 1 #(Float/parseFloat %)))

  (try-str->num "123a")

  (try-str->num "0.11")

  (time (-> (read-csv-file sdss-filename)
            parse-numbers
            count))

  (as-> nil x
    (read-csv-data sdss-filename)
    (nth x 5))

  (nth SDSS-DATA-CSV 9888)
  
  (count (drop 1 SDSS-DATA-CSV-STRINGS))

  ;;;
  )




(comment
  (type (nth (nth SDSS-DATA-CSV 1) 13))
  (nth SDSS-DATA-CSV-STRINGS 1)

  (nth SDSS-DATA-CSV 1)
  
  (count (nth SDSS-DATA-CSV 1))

  (try-str->num "STAR")
  (read-string "STAR")

  ;;;
  )

(defn keep-columns
  "returns vector of examples with slected column indices"
  [data idxs]
  (mapv  (fn [row]
           (->
            (keep-indexed #(when ((set idxs) %1) %2) row)
            vec))   data))


(comment

  (def SDSS-SET (keep-columns SDSS-DATA-CSV [0 1 2 3 4 5 6 7 8 9 10 11 12 14 15 16 17]))
  (def SDSS-LABELS (-> (keep-columns SDSS-DATA-CSV [13]) flatten vec))


  (def SDSS-TRAIN-SET (subvec  SDSS-SET 0 8000))
  (def SDSS-TEST-SET  (subvec  SDSS-SET 8000 10000))

  (def CLASS {"STAR"   1
              "GALAXY" 2
              "QSO"    3})

  (def SDSS-TRAIN-LABEL  (mapv #(CLASS %) (subvec SDSS-LABELS 0 8000)))
  (def SDSS-TEST-LABEL (mapv #(CLASS %) (subvec SDSS-LABELS 8000 10000)))

  ;
  )


(defn prn-nth
  [items labels i]
  (pug/cprint (nth items i))
  (pug/cprint (nth labels i) ))

(comment

  (keys CLASS)

  (keep-columns [[1 2 3] [1 2 3]] [0 1])
  (first SDSS-DATA-CSV)
  (first SDSS-SET)
  (count SDSS-TRAIN-SET)
  (count SDSS-TEST-SET)
  (count SDSS-LABELS)

  (first SDSS-LABELS)
  
  (first SDSS-TRAIN-LABEL)
  

  ((set [0 1 2 3 4 5 6 7 8 9 10 11 13 14 15 16 17]) 12)

  (subvec SDSS-TRAIN-SET  0 3)

  (count (nth SDSS-TRAIN-SET 123))
  
  (prn-nth SDSS-TRAIN-SET SDSS-TRAIN-LABEL 4323)

  ;;;
  )


(defn normal-equations
  "returns weights vector"
  [X-train  y-train X-width y-width]
  (let [len    (count X-train)
        height (/ len  X-width)]
    (prn  height X-width )
    (as-> X-train V
      (transpose V X-width)
      (multiply V X-train height X-width)
      (Gandalf.deeplearning-mx/inverse V)
      ; (multiply V (transpose X-train X-width) X-width height)
      ; (multiply V y-train height y-width)
      ;
      )))

(defn inverse
  "returns the inverse of a matrix A^-1
  AB = BA = In
  "
  [mx]
  (multiply-scalar (adjugate mx) (/ 1 (det-leibniz mx))))


(comment

  (def from 0)
  (def to 4)


  (def train-set-design-mx (mkmx (subvec SDSS-TRAIN-SET from to)))
  (def train-regression-targets (mkmx (subvec SDSS-TRAIN-LABEL from to)))

  (count train-set-design-mx)
  (count train-regression-targets)

  (prnmx train-set-design-mx 17)

  (->
   (transpose train-set-design-mx 17)
   (prnmx 4)
   nil?)
  
  
  (as-> nil V
    (multiply (transpose train-set-design-mx 17) train-set-design-mx 4 17)
    ; (inverse V)
    ; (count V)
    (det-leibniz V) ; overflows heap computing minors
   )


  (def w (normal-equations train-set-design-mx train-regression-targets 17 1))

  ; (def w (normal-equations [1 2 0 3 4 3 2 5 -1 1 7 1 3 2 1 1] [1 2 3 4 ] 4 1))


  (nth  SDSS-TEST-SET 1)

  (prn-nth SDSS-TEST-SET SDSS-TEST-LABEL 1)


  (-> (y-value w (nth  SDSS-TEST-SET 1)) first float)

  ;;;
  )

