
;;

;;

(ns csbooks.mxnet-viz
  (:require [org.apache.clojure-mxnet.symbol :as sym]
            [org.apache.clojure-mxnet.visualization :as viz]))

(defn get-symbol []
  (as-> (sym/variable "data") data

    #_(sym/convolution "conv1" {:data       data
                                :kernel     [3 3]
                                :num-filter 32
                                :stride     [2 2]})
    #_(sym/batch-norm "bn1" {:data data})
    #_(sym/activation "relu1" {:data     data
                               :act-type "relu"})
    #_(sym/pooling "mp1" {:data      data
                          :kernel    [2 2]
                          :pool-type "max"
                          :stride    [2 2]}) #_(sym/convolution "conv2" {:data       data
                                                                         :kernel     [3 3]
                                                                         :num-filter 32
                                                                         :stride     [2 2]})
    #_(sym/batch-norm "bn2" {:data data})
    #_(sym/activation "relu2" {:data     data
                               :act-type "relu"})
    #_(sym/pooling "mp2" {:data      data
                          :kernel    [2 2]
                          :pool-type "max"
                          :stride    [2 2]})

    (sym/flatten "fl" {:data data})
    #_(sym/fully-connected "fc2" {:data       data
                                  :num-hidden 10})
    (sym/softmax-output "softmax" {:data data})))

(defn test-viz []
  (let [dot (viz/plot-network (get-symbol)
                              {"data" [1 1 28 28]}
                              {:title      "foo"
                               :node-attrs {:shape     "oval"
                                            :fixedsize "false"}})]
    (viz/render dot "testviz" "./")))

(defn -main [& args]
  (do (test-viz)
      (println "Check for the testviz.pdf file in the project directory")))


(comment
  (-main)
  )