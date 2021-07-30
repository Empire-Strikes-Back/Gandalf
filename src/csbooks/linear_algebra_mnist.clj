(ns csbooks.linear-algebra-mnist
  (:require [csbooks.linear-algebra-math :refer :all]
            [clojure.repl :refer :all]
            [clojure.java.io :refer [file output-stream input-stream]]
            [clojure.pprint  :as pp]
            [clojure.java.io :as io]
            [csbooks.linear-algebra-kmeans :refer [k-means-rand-x->z k-means-rand-z k-means-rand-gs]]))

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
   (prn-mx (take 784 (drop 784 T10-IMAGES)) 28)
   nil?)

  ;;;
  )

(defn read-int-from-file
  "returns the number represented by bytes in range [offset , + offset  limit]"
  [filename offset limit]
  (->   (read-binary-file  filename :offset offset :limit limit)
        bytes->int)
  )

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
  "prints nth image from file as matrix 28x28, using prn-mx "
  [filename n]
  (prn-mx (read-MNIST-image-nth filename n) image-size)
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
   (prn-mx (read-MNIST-image "data/t10k-images-idx3-ubyte" 0) 28)
   nil?)

  (->
   (prn-mx (read-MNIST-image-nth "data/t10k-images-idx3-ubyte" 0) 28)
   nil?)

  (prn-nth-image "data/t10k-images-idx3-ubyte" 0)

  (prn-nth-image "data/t10k-images-idx3-ubyte" 0)

  (prn-nth-image "data/t10k-images-idx3-ubyte" 1555)

  (read-MNIST-label "data/t10k-labels-idx1-ubyte" 0)


  (prn-nth-image "data/t10k-images-idx3-ubyte" 1555)
  (read-MNIST-label-nth "data/t10k-labels-idx1-ubyte" 1555)

  (prn-nth-image "data/t10k-images-idx3-ubyte" 1000)
  (read-MNIST-label-nth "data/t10k-labels-idx1-ubyte" 1000)

  (prn-nth-image-and-label-t10 1000 )
  
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
  [ n-start n-end dataset]
  (let [i-start (* (dec n-start) image-size-squared)
        i-end   (+ i-start (* (- (inc n-end) n-start) image-size-squared))]
    (subvec dataset i-start i-end)))

(defn nth-image
  "returns nth image from a dataset"
  [dataset i]
  (subvec dataset  (* i image-size-squared) (+ (* i image-size-squared) image-size-squared)))

(defn prn-image
  "prints image as matrix 28x28 using prn-mx"
  [image-vec]
  (prn-mx  image-size image-vec)
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
  (dataset i)
  )

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

(defn partition-imgvec
  "Returns vec of vecs w/ iamges"
  [images]
  (->>
   (partition image-size-squared images)
   (mapv vec)))

(comment
  ; k-means

  (def xs-flat (take-images-range  1 20000 T60-IMAGES))
  (def xs-labels (subvec T60-LABELS 0 20000))


  (count xs-flat)
  (/ (count T60-IMAGES) image-size-squared)
  (count xs-labels)


  (prn-nth xs-flat xs-labels 19791)

  (count (partition image-size-squared xs-flat))
  (first (partition image-size-squared xs-flat))

  (count (first (partition-imgvec xs-flat)))

  (def xs (partition-imgvec xs-flat))
  (count xs)

  (def gs (k-means-rand-z xs :k 20 :i 10))
  (def gs (k-means-rand-x->z xs :k 20 :i 20)) ; split into 20 gs randomly and do 20 iterations
  (def gs (time (k-means-rand-gs xs :k 20 :i 8)))
  ; 3 iters 100 sec 9 iter 186 sec - because incorrect validation for = J J-prev  20i 420sec

  (def gs (time (k-means-rand-gs xs :k 20 :i 20)))
  
  (gs :iter)
  (count (gs :groups))

  (doseq [z (gs :zs)]
    (prn-image (map int z)))

  (prn-image (first xs))
  (prn-image (first (gs :zs)))
  (prn-image (nth (gs :zs) 6))
  (prn-image (nth (gs :zs) 7))

  (def t (time ((fn [] (prn 'hello) 3))))

  (defn long-running-job [n]
    (Thread/sleep 3000) ; wait for 3 seconds
    (+ n 10))

  (time (doall (map long-running-job (range 4))))
  (time (doall (pmap long-running-job (range 4))))
  
  ;;;
  )


(comment

  ; least squares calssification

  (prn-nth T60-IMAGES T60-LABELS 60000)

  (prn-nth T60-IMAGES T60-LABELS 999)



  (def test-images-subset1 (take-images-range  1 10 T10-IMAGES))
  (def test-labels-subset1 (subvec T10-LABELS 0 10))

  (def train-images-subset1 (take-images-range  1 1000 T60-IMAGES))
  (def train-labels-subset1 (subvec T60-LABELS 0 1000))

  (partition 1 [1 2 3 4])

  (def train-cols (vec (map vec (partition 784 train-images-subset1))))
  (prn-image (train-cols 0))
  (prn-nth train-images-subset1 train-labels-subset1 1)

  (count train-images-subset1)
  (count train-labels-subset1)

  (count train-cols)
  (count (first train-cols))

  (def have-non-zero-idx (filterv (fn [x]
                                    (not (== 0 (x 775))))  train-cols))
  (count have-non-zero-idx)
  (prn-image (have-non-zero-idx 0))
  ((have-non-zero-idx 0) 28)


  (def nnz-indices (vecs-nnz-indices train-cols))
  (count nnz-indices)
  (prn nnz-indices)
  (prn-nth train-images-subset1 train-labels-subset1 999)
  (prn (vec-nnz-indices (nth-image train-images-subset1 (dec 999))))

  (#{1 2} 3)
  (#{1 2} 784)
  (nnz-indices 784)
  
  (def train-cols-nnz  (mapv (fn [x]
                               (->
                                (keep-indexed #(if (nnz-indices %1) %2 nil ) x)
                                vec)
                               ;
                               ) train-cols ))
  
  (count (nth train-cols-nnz 999 ))

  (def X  (make-regression-model-feature-mx  train-cols-nnz))
  
  (count X)
  (/ (count X) 610)

  
  (time (def params (regression-model-parameters 1000 X  train-labels-subset1)))

  (def img8 (nth-image train-images-subset1 (dec 8)))

  (nnz img8)
  (count img8)

  (nnz (nth-image train-images-subset1 (dec 49)))

  (mx->cols 2 [1 2 3 4 5 6])


  ;;;
  )


(comment
   ; least squares calssification 2


  (def test-images-subset1 (take-images-range  1 10 T10-IMAGES))
  (def test-labels-subset1 (subvec T10-LABELS 0 10))

  (def train-images-subset1 (take-images-range  1 100 T60-IMAGES))
  (def train-labels-subset1 (subvec T60-LABELS 0 100))

  (def binary-labels (mapv #(if (== % 0) 1 -1)  train-labels-subset1))

  (def train-cols (vec (map vec (partition 784 train-images-subset1))))

  (def nnz-indices (vecs-nnz-indices train-cols))

  (def nnz-indices (sorted-set (keep-indexed (fn [i x]
                                               (if) ; REMOVE rwos and cols on edges
                                               )nnz-indices)))

  (def train-cols-nnz  (mapv (fn [x]
                               (->
                                (keep-indexed #(if (nnz-indices %1) %2 nil) x)
                                vec)
                               ;
                               )train-cols))

  (count nnz-indices)
  (count train-cols-nnz)


  (def X  (make-regression-model-feature-mx  train-cols-nnz))

  (count train-cols-nnz)
  (count (first train-cols-nnz))

  (prn-mx 23 (first train-cols-nnz))

  (time (def params (regression-model-parameters 100 X  binary-labels)))

  (def y- (regression-model-predictions 100 X params))
  (count y-)
  (def y-- (mapv sign  y-))

  (def residuals (elwise-subtract binary-labels y--))

  (float (error-rate binary-labels y--)) ; 0.07333333 correct

  ;;;
  )



(comment
  
  (pst)
  
  
  ;;;
  )