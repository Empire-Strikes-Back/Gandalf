(ns Gandalf.deeplearning-linreg
  (:require [Gandalf.deeplearning-mx :refer :all] ))

; linear regression algorithm


(defn y-value
  "Let ŷ be the value that our model
   predicts y should take on
  w - feature vecot
  x - target vector 
  "
  [w x]
  (let [len (count w)]
    (multiply w x len 1)))


(defn rand-int-in-range
  "returns random int in range a b"
  [a b]
  (int (- b (* (rand) (- b a)))))


(defn r-red
  "returns random red color"
  []
  [(rand-int-in-range 200 255) (rand-int-in-range 1 30) (rand-int-in-range 1 30)])

(defn r-magenta
  "returns random red color"
  []
  [(rand-int-in-range 200 255) (rand-int-in-range 1 30) (rand-int-in-range 200 255)])

(defn r-teal
  "returns random red color"
  []
  [(rand-int-in-range 1 30) (rand-int-in-range 200 255) (rand-int-in-range 200 255)])


(defn r-yellow
  "returns random red color"
  []
  [(rand-int-in-range 200 255) (rand-int-in-range 200 255) (rand-int-in-range 1 30)])

(defn gen-dataset
  [size label gen-val]
  (mapv (fn [_]
          {:label label
           :value (gen-val)}) (range size)))

(defn set->deisgn-mx
  "returns design matrix for the set"
  [s]
  (->>
   s
   (mapv #(:value %))
   mkmx))

(def color-enum
  {:red     1
   :yellow  2
   :magenta 3
   :teal    4})

(defn set->regression-targets-vec
  "returns regression targets vector for the set"
  [s]
  (->>
   s
   (mapv #(->  % :label color-enum ))
   mkmx))

(defn normal-equations
  "returns weights vector"
  [X-train  y-train X-width y-width]
  (let [len    (count X-train)
        height (/ len  X-width)]
    (prn len height)
    (as-> X-train V
      (transpose V X-width)
      (multiply V X-train height X-width)
      ; (count V)
      (inverse V)
      ; (prnmx V 3)
      (multiply V (transpose X-train X-width) X-width height)
      (multiply V y-train height y-width)
      ; (mapv float V)
      ;
      )))

(comment

  (y-value [1 1 1] [250, 250, 10])



  (def red-train (gen-dataset 10 :red r-red))
  (def yellow-train (gen-dataset 10 :yellow r-yellow))
  (def magenta-train (gen-dataset 10 :magenta r-magenta))
  (def teal-train (gen-dataset 10 :teal r-teal))

  (def train-set (concat red-train yellow-train magenta-train teal-train))

  (def train-regression-targets-vec (set->regression-targets-vec train-set))

  (def train-set-design-mx (set->deisgn-mx train-set))

  (->
   train-set
   set->deisgn-mx
   (prnmx 3)
   nil?)

  (nil? (prnmx train-set-design-mx 3))



  (def red-test (gen-dataset 5 :red r-red))
  (def yellow-test (gen-dataset 5 :yellow r-yellow))
  (def magenta-test (gen-dataset 5 :magenta r-magenta))
  (def teal-test (gen-dataset 5 :teal r-teal))

  (def test-set (concat red-test yellow-test magenta-test teal-test))

  (def test-regression-targets-vec (set->regression-targets-vec test-set))

  (def test-set-design-mx (set->deisgn-mx test-set))

  (nil? (prnmx test-set-design-mx 3))

  (as-> (transpose train-set-design-mx 3) X
    (prnmx X (/ (count X) 3))
    (nil? X))

  (def w (normal-equations train-set-design-mx train-regression-targets-vec 3 1))

  (-> (y-value w [250, 1 ,1]) first float) ; 1 red or ~5
  (-> (y-value w [250, 1 ,245]) first float) ; 3 magenta or ~15 
  (-> (y-value w [250, 245 ,0]) first float) ; 2 yellow or ~10
  (-> (y-value w [11, 245 ,259]) first float) ; 4 teal or ~20

  (-> (dot-product w [11, 245 ,259]) first float)

  (-> (y-value w [11, 238 ,245]) first float) ; 4 teal


  (->>
   (conj test-set {:label :yellow ; error on purpose
                    :value [122,80, 20]})
   (map-indexed (fn [i x]
                   (let [label   (-> x :label)
                         v       (:value x)
                         label-i (-> label color-enum)
                         y       (-> (y-value w v) first float)]
                     (prn label y label-i)
                     (if (= label-i (Math/round y)) 1 nil)))))

  (-> (y-value w [70,70, 20]) first float) ; wrong


 ;;;
  )

; suppose X-train is a mx3 matrix  - m examples, each has 3 features
; if we compute w (weights vector), we get vec3
; so X-train*w = y
; the product of X-train (ddesign matrix,m by 3) and 2eigth vector (3) is a m-length vetor of 'labels' - regression targets