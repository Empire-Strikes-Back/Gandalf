(ns csbooks.linear-algebra-iris
  (:require [clojure.repl :refer :all]
            [csbooks.linear-algebra-print :refer [cprn]]
            [csbooks.linear-algebra-math :refer :all]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  ;
  )

(defn read-column [filename column-index]
  (with-open [reader (io/reader filename)]
    (let [data (csv/read-csv reader)]
      ; (map #(nth % column-index) data) ; lazy
      (mapv #(nth % column-index) data))))

(defn read-csv-file
  [filename]
  (with-open [reader (io/reader filename)]
    (let [data (doall (csv/read-csv reader))]
      data)))


(def iris-filename "data/iris/iris.data")

(defn try-str->num
  "returns number or nil"
  [strin]
  (try (Float/parseFloat strin) (catch Exception e strin)))

(defn parse-numbers
  [data]
  (mapv #(mapv try-str->num %) data))

(defn read-csv-data
  "reads and parses csv data"
  [filename]
  (-> (read-csv-file filename)
      parse-numbers))

#_(def IRIS_DATA_RAW (read-csv-file iris-filename))
#_(def IRIS_DATA (->  IRIS_DATA_RAW parse-numbers drop-last vec))

#_(count IRIS_DATA)



(comment
  (sign 0)
  ; least squares calssification
  
  ;162 Regression model
  ;299 iris

  (def samples (mapv #(vec (drop-last  %)) IRIS_DATA))
  (def labels (mapv #(last %)  IRIS_DATA))
  (def binary-lables-virginica (mapv #(if (= % "Iris-virginica") 1 -1) labels))
  (def binary-lables-setosa (mapv #(if (= % "Iris-setosa") 1 -1) labels))
  (def binary-lables-versicolor (mapv #(if (= % "Iris-versicolor") 1 -1) labels))






  (count samples)
  (def X (make-regression-model-feature-mx samples))
  (count X)
  (def heiXT (/ (count X) 5))
  (take 5 X)

  (def params (regression-model-parameters 150 X binary-lables-virginica))
  ; values are not precisly correct v = −2.39, β 1 = −0.0918, β 2 = 0.406,β 3 = 0.00798,β 4 = 1.10.
  ; [-2.370893168639163
  ;  -0.08819672913977447
  ;  0.3964650446833074
  ;  0.008480010289205383
  ;  1.0930853325898924]

  (def v (first params))
  (def bs (vec (rest params)))

  (def y- (regression-model-predictions 150 X params))

  (def y-- (mapv sign  y-))
  (count (filterv pos? y--))
  (- 1 (float (/ 50 53)))

  (def residuals (elwise-subtract binary-lables-virginica y--))

  (root-mean-square residuals)
  (def relative-pred-error (/ (root-mean-square residuals) (root-mean-square binary-lables-virginica)))

  (root-mean-square [-1 -1 1])

  (float (error-rate binary-lables-virginica y--)) ; 0.07333333 correct

  (with-precision 10 (* (bigdec (Math/pow 2M 1/2)) (bigdec (Math/pow 2M 1/2))) )
  
  
  ;;;
  )