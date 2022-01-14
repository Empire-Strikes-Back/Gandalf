(ns Gandalf.deeplearning-mnist
  (:require [Gandalf.deeplearning-mx :refer :all]
            [clojure.repl :refer :all]
            [clojure.java.io :refer [file output-stream input-stream]]
            [clojure.pprint  :as pp]
            [clojure.java.io :as io]))

; http://yann.lecun.com/exdb/mnist/

(defn read-binary-file
  "returns bute-array , accepts :offset :limit"
  [filename & {:keys [offset limit]
               :or   {offset 0}}]
  (let [f   (file filename)
        lim (or limit (- (.length f) offset))
        buf (byte-array lim)]
    ; (prn lim offset)
    (with-open [in (input-stream  f)]
      (.skip in offset)
      (.read in buf 0 lim)
      buf
      ;
      )))

(defn read-file-to-vec
  "returns vector with file bytes (ints 8)"
  [filename offset]
  (->
   (if (.exists (io/as-file  filename))
     (read-binary-file filename
                       :offset  offset)
     nil)
   vec))

(defn bytes->int [bytes]
  "Converts a byte array into an integer."
  (->>
   bytes
   (map (partial format "%02x"))
   (apply (partial str "0x"))
   read-string))

(defn file-szie
  "returns file byte length"
  [filename]
  (.length (file filename)))


(def t10-iamges-filename "data/t10k-images-idx3-ubyte")
(def t10-labels-filename "data/t10k-labels-idx1-ubyte")
(def t60-iamges-filename "data/train-images-idx3-ubyte")
(def t60-labels-filename "data/train-labels-idx1-ubyte")


(def image-size 28)
(def image-size-squared (* 28 28))


(def image-count-t60 60000)
(def image-count-t10 10000)

(def t10-images-file-size 7840016)
(def t60-images-file-size 47040016)

(def t10-labels-file-size 10008)
(def t60-labels-file-size 60008)

(def image-file-meta-bits 16)
(def labels-file-meta-bits 8)

; reading files into memeory on init

(def  T10-IMAGES  (read-file-to-vec t10-iamges-filename  image-file-meta-bits))
(def  T60-IMAGES  (read-file-to-vec t60-iamges-filename  image-file-meta-bits))
(def  T10-LABELS  (read-file-to-vec t10-labels-filename  labels-file-meta-bits))
(def  T60-LABELS  (read-file-to-vec t60-labels-filename  labels-file-meta-bits))





(comment

  (->
   (prnmx (take 784 (drop 784 T10-IMAGES)) 28)
   nil?)

  ;;;
  )

(defn read-int-from-file
  "returns the number represented by bytes in range [offset , + offset  limit]"
  [filename offset limit]
  (->   (read-binary-file  filename :offset offset :limit limit)
        bytes->int))

(defn read-MNIST-image
  "returns vector of pixel values for an image at offset"
  [filename offset]
  (->>
   (read-binary-file filename
                     :offset (+ offset image-file-meta-bits)
                     :limit (Math/pow image-size 2))
   vec))

(defn read-MNIST-label
  "returns vector of pixel values for an image at offset"
  [filename offset]
  (->>
   (read-binary-file filename
                     :offset (+ offset labels-file-meta-bits)
                     :limit 1)
   vec))

(defn read-MNIST-image-nth
  "returns nth image from the file"
  [filename  n]
  (read-MNIST-image filename (* n (Math/pow image-size 2))))

(defn read-MNIST-label-nth
  "returns nth image from the file"
  [filename  n]
  (read-MNIST-label filename (* n 1)))

(defn prn-nth-image
  "prints nth image from file as matrix 28x28, using prnmx "
  [filename n]
  (prnmx (read-MNIST-image-nth filename n) image-size)
  nil)

(defn prn-nth-image-and-label-t10
  "prn iamge and label from "
  [n]
  (prn-nth-image t10-iamges-filename n)
  (read-MNIST-label-nth t10-labels-filename n))

(defn prn-nth-image-and-label-t60
  "prn iamge and label from "
  [n]
  (prn-nth-image t60-iamges-filename n)
  (read-MNIST-label-nth t60-labels-filename n))

(comment

  (count (read-binary-file "data/t10k-images-idx3-ubyte"))

  (as-> (read-binary-file "data/t10k-labels-idx1-ubyte") x
    (bytes x)
    (take 4 x))

  (int (bytes (take 4 (read-binary-file "data/t10k-images-idx3-ubyte"))))

  (subvec (read-binary-file "data/t10k-images-idx3-ubyte") 4 4)

  (def x (byte-array [(byte 0x43)
                      (byte 0x6c)
                      (byte 0x6f)
                      (byte 0x6a)
                      (byte 0x75)
                      (byte 0x72)
                      (byte 0x65)
                      (byte 0x21)]))

  (String. x)

  ; (clojure.main/repl :print pprint)

  (pp/pprint x)

  (apply str "0x" "00")

  (read-string (format "%02x" (byte 0x00)))

  (apply (partial str "0x") ["00" "02"])
  (apply (partial str "0x") ["asd"])
  (str "0x"  ["asd"])

  (file-szie "data/t10k-images-idx3-ubyte")
  (file-szie "data/train-images-idx3-ubyte")
  (file-szie "data/t10k-labels-idx1-ubyte")




  ;;;
  )


(comment

    ; number of iamges in t10
  (read-int-from-file "data/t10k-images-idx3-ubyte" 4 4)

    ; number of images rows
  (read-int-from-file "data/t10k-images-idx3-ubyte" 8 4)

    ; number of images rows
  (read-int-from-file "data/t10k-images-idx3-ubyte" 12 4)

    ; number of images in train
  (read-int-from-file "data/train-images-idx3-ubyte" 4 4)


  (count)

  (->
   (prnmx (read-MNIST-image "data/t10k-images-idx3-ubyte" 0) 28)
   nil?)

  (->
   (prnmx (read-MNIST-image-nth "data/t10k-images-idx3-ubyte" 0) 28)
   nil?)

  (prn-nth-image "data/t10k-images-idx3-ubyte" 0)

  (prn-nth-image "data/t10k-images-idx3-ubyte" 0)

  (prn-nth-image "data/t10k-images-idx3-ubyte" 1555)

  (read-MNIST-label "data/t10k-labels-idx1-ubyte" 0)


  (prn-nth-image "data/t10k-images-idx3-ubyte" 1555)
  (read-MNIST-label-nth "data/t10k-labels-idx1-ubyte" 1555)

  (prn-nth-image "data/t10k-images-idx3-ubyte" 1000)
  (read-MNIST-label-nth "data/t10k-labels-idx1-ubyte" 1000)

  (prn-nth-image-and-label-t10 1000)

  (prn-nth-image-and-label-t60 1000)



  ;;;
  )



(defn nth-image-0
  "returns nth image from a dataset"
  [dataset n]
  (->>
   dataset
   (drop (* n image-size-squared))
   (take image-size-squared)))

(defn take-images-range
  "reutrns vector with image data from image n-start to iamge n-end  "
  [dataset n-start n-end]
  (let [i-start (* (dec n-start) image-size-squared)
        i-end   (+ i-start (* (- n-end n-start) image-size-squared))]
    (subvec dataset i-start i-end)))

(defn nth-image
  "returns nth image from a dataset"
  [dataset i]
  (subvec dataset  (* i image-size-squared) (+ (* i image-size-squared) image-size-squared)))

(defn prn-image
  "prints image as matrix 28x28 using prnmx"
  [image-vec]
  (prnmx image-vec image-size)
  nil)

(defn prn-nth-image-dataset
  "prints nth image from a dataset"
  [dataset n]
  (->
   (nth-image dataset (dec n))
   prn-image)
  nil)

(defn nth-label
  "returns nth label from a dataset"
  [dataset i]
  (dataset i))

(defn prn-nth
  "prints nth image nad label from a dataset"
  [images labels  n]
  (prn-nth-image-dataset images n)
  (prn (nth-label labels (dec n))))

(comment
  (take 10  T10-IMAGES)

  (nth-image T10-IMAGES 1)

  (prn-nth-image-dataset T10-IMAGES 1)

  (nth-label T10-LABELS 1)

  (prn-nth T10-IMAGES T10-LABELS 1)

  (prn-nth T60-IMAGES T60-LABELS 59999)

  (prn-nth T60-IMAGES T60-LABELS 60000)

  (prn-nth T60-IMAGES T60-LABELS 8)


  (count (partition image-size-squared T10-IMAGES))

  (count (partition image-size-squared T60-IMAGES))

  (prn-nth T60-IMAGES T60-LABELS 12333)
  (prn-nth T60-IMAGES T60-LABELS 23233)

  (str (int (/  (count T60-IMAGES) 1000000)) " million")

  (time (prn-nth T60-IMAGES T60-LABELS 23233))

  (time (prn-nth T60-IMAGES T60-LABELS 60000))



  ;;;
  )


(comment



  ;;;
  )