(ns Gandalf.main
  (:require
   [Gandalf.mxnet-ndarray]
   [Gandalf.mxnet-module]
   [Gandalf.mxnet-bert-qa-infer]
   [Gandalf.mxnet-bert-qa-ask]
   [Gandalf.mxnet-cnn-text-classifier]
   [Gandalf.mxnet-rnn-train-char]
   [Gandalf.mxnet-viz]

   [Gandalf deeplearning-mx-colors deeplearning-mx-pca deeplearning-mx-prob deeplearning-mx-num]
   [Gandalf deeplearning-linreg deeplearning-xor]
   [Gandalf deeplearning-math deeplearning-math-mx deeplearning-mx-pca]
   [Gandalf linear-algebra linear-algebra-mnist]
   [Gandalf deeplearning-mnist deeplearning-sdss]
   
   [Gandalf.programming-clojure]
   [Gandalf.sicp]
   

   [Gandalf.repl]))

(defn -main [& args]
  (println :main))

(comment

  (require '[Gandalf.seasoned-schemer])
  (require '[Gandalf.reasoned-schemer])
  (require '[Gandalf.little-schemer])
  
  ;
  )