(ns csbooks.deeplearning-math-mx
  (:require [clojure.repl :refer :all]
            [io.print :refer [cprn]]))



(defn make-mx
  "Returns a mx given width, height and element"
  [wid hei el]
  (vec (repeat (* wid hei) el)))

(defn make-mx-with
  "Returns a matrix given width, height and make-el"
  [wid hei make-el]
  (->>
   (range (* wid hei))
   (map-indexed #(make-el %1  %2))
   vec))

(defn rows->mx
  "Returns a vector. flattens the A"
  [A]
  (->
   (flatten A)
   vec))

(defn mx->rows
  "Returns two dim vector of rows of A"
  [wid A]
  (->>
   (partition wid A)
   (mapv vec)))

(defn prn-rows
  "Prtints rows, used by prn-mx"
  [wid A & {:keys [print-fn] :or {print-fn prn}}]
  (as-> nil R
    (mx->rows  wid A)
    (doseq [row R]
      ; (print)
      (print-fn row))))

(defn prn-mx
  "Prtints A"
  ([wid A]
   (prn-rows wid A)
   (println))
  ([wid A return?]
   (prn-rows wid A)
   (println)
   A))

(defn cprn-mx
  "Color-prtints A"
  ([wid A]
   (prn-rows wid A :print-fn cprn)
   (println))
  ([wid A return?]
   (prn-rows wid A :print-fn cprn)
   (println)
   A))

(comment

  (rows->mx [[1 2 3]
            [4 5 6]])

  (cprn-mx 3 (rows->mx [[1 2 3]
                      [4 5 6]]))

  ;;;
  )

(defn index->row
  "Returns the row index given el index, row length"
  [i wid]
  (int (/ i wid)))


(defn index->col
  "returns the col index given el index, row length"
  [i wid]
  (mod i wid))

(defn index->pos
  "Returns the position [i,j] el in the mx of given width"
  [i wid]
  (vector (index->row) (index->col)))

(defn pos->index
  "Returns el index given row, col and row length"
  [i j wid]
  (->
   (* (inc i) wid)
   (- (- wid j))))


(defn mx->el
  "Returns the i,j el of A"
  [i j wid A]
  (A (pos->index i j wid)))

(defn mx->row
  "Returns i-th row of the mx"
  [i wid A]
  (->
   (keep-indexed  #(if (= (index->row %1 wid) i) %2 nil) A)
   vec))

(defn mx->col
  "Returns i-th row of the mx"
  [i wid A]
  (->
   (keep-indexed  #(if (= (index->col %1 wid) i) %2 nil) A)
   vec))

(comment
  
  (mx->row 1 3 [1 2 3 4 5 6] )
  (mx->col 1 3 [1 2 3 4 5 6])
  
  ;;;
  )

(defn make-v
  "Returns a vector of specified size and el"
  [size el]
  (vec (repeat size el)))



(defn iden-mx
  "Returns an identity mx of size"
  [size]
  (let [A  (make-mx size size nil)]
    (->>
     A
     (map-indexed (fn mpr [i x]
                    (let [row-i (index->row i size)
                          col-i (index->col i size)]
                      (cond
                        (= row-i col-i) 1
                        :else 0))))
     vec)))

(defn iden-mx-3
  "Returns 3x3 identity mx"
  []
  (iden-mx 3))


(defn iden-mx-4
  "Returns 4x4 identity mx"
  []
  (iden-mx 4))


(defn diag
  "Returns a square mx with the vector as the diagonal "
  ([v]
   (->
    (let [size (count v)
          A    (make-mx size size 0)]
      (map-indexed (fn [i x]
                     (let [row-i (index->row i size)
                           col-i (index->col i size)]
                       (cond
                         (= row-i col-i) (nth v row-i)
                         :else x))) A))
    vec))
  ([wid hei v]
   (->
    (let [size (count v)
          A    (make-mx wid hei 0)]
      (map-indexed (fn [i x]
                     (let [row-i (index->row i wid)
                           col-i (index->col i wid)]
                       (cond
                         (and (<= row-i size) (= row-i col-i) (and (< row-i size))) (nth v row-i)
                         :else x))) A))
    vec)))

(comment

  (diag [1 2 3])
  (diag  4 4 [1 2 3])
  (get [1 2 3]  4)

  
  (iden-mx-4 )
  ;;;
  )


(defn elwise-prod
  "Returns a vector (mx), multiplies a,b element-wise "
  [a b]
  (mapv * a b))

(defn elwise-sum
  "Returns a vector (mx), adds a,b element-wise "
  [a b]
  (mapv + a b))

(defn elwise-subtract
  "Returns a vector (mx), subtracts a,b element-wise "
  [a b]
  (mapv - a b))

(defn elwise-divide
  "Returns a vector (mx), divides a,b element-wise "
  [a b]
  (mapv / a b))

(defn dot-prod
  "Retruns the sum of products of corresponding els"
  [a b]
  (reduce + 0 (map * a b)))

(defn scalar-prod
  "Returns vector of scalar el products"
  [x v]
  (mapv #(* x %)  v))

(defn scalar-divide
  "Returns vector, divides v by scalar element-wise"
  [x v]
  (mapv #(/ % x)  v))


(defn mx-prod
  "Returns the product of multiplying AB.
   Left mx must have cols as right mx rows.
   Result mx has A-rows, B-cols
  "
  [wid-A wid-B A B]
  (let [hei-A (/ (count A) wid-A)
        M     (make-mx hei-A wid-B nil)]
    (map-indexed (fn [i x]
                   (let [row-i (index->row i wid-B)
                         col-i (index->col i wid-B)
                         row   (mx->row row-i wid-A A)
                         col   (mx->col col-i wid-B B)]
                     (dot-prod row col)
                      ;
                     ))
                 M)))


(comment

  (def A (rows->mx [[1 2 3]
                    [2 5 6]]))

  (def B (rows->mx [[0 1]
                    [2 3]
                    [4 5]]))

  (prn-mx 2 (mx-prod 3 2 A B))
  
  ;;;
  )

(defn vec-broadcast
  "Returns mx with vector added to each row element-wise"
  [wid A a]
  (as-> nil R
    (mx->rows wid A)
    (mapv #(elwise-sum a %)  R)))


(comment

  (def A [1 2 3 4 5 6])

  (def a [1 2 3])

  (vec-broadcast  3 A a)

  ;;;
  )

(defn mx-square->size
  "Returns the size of a square mx"
  [A]
  (int (Math/sqrt (count A))))

(defn vec-length
  "Returns the length (Euclidean norm) of a vector.
   L2 norm"
  [a]
  (Math/sqrt (dot-prod a a)))

(defn vec-L1-norm
  [a]
  "Returns the L1 norm, that grows at the same rate in all locations.
   Every time an element of x moves
   away from 0 by e , the L 1 norm increases by e"
  (->>
   a
   (mapv #(Math/abs %))
   (reduce +)))

(defn vec-max-norm
  "Returns the absolue value of the element with the largest magnitutde"
  [v]
  (->>
   v
   (mapv #(Math/abs %))
   sort
   last))

(defn mx-frobenius-norm
  "Returns the norm (size) of a matrix.
   Frobenius size of A = sqrt of the sum of a[ij]^2  
  "
  [A]
  (->>
   A
   (mapv  #(* % %))
   (reduce +)
   Math/sqrt))

(defn vec-unit
  "Returns the unit vector (normalizes) of a"
  [a]
  (scalar-divide (vec-length a) a))


(defn count-non-zero
  "Returns the count of non-zero elemts of a vector.
   Mistakenly called L0 norm.
   It's not a norm, scaling vector does not change the count of non-zero elems
  "
  [a]
  (->>
   a
   (filterv (fn [x] (not (zero? x))))
   count))

(comment
  (vec-length [0 0 2])
  (vec-unit [0 0 2])
  (count-non-zero [0 0 2])
  ;;;
  )

(defn mx-transpose
  "Returns transposed A - all indices are mirrored, e.g. 1,2 -> 2,1"
  [wid A]
  (let [len (count A)
        hei (/ len wid)]
    (reduce-kv (fn [acc i x]
                 (let [row-i (index->row i wid)
                       col-i (index->col i wid)
                       new-i (pos->index col-i row-i  hei)]
                   (assoc acc new-i x))) (make-mx hei wid nil) A)))




(defn mx-symmetric?
  "Returns true if matrix is equal to its own trasnpose"
  [A]
  (->
   (mx-transpose  (mx-square->size A) A)
   (= A)))

(defn vec-orthogonal?
  "returns true if both vectors are at 90 degree agle"
  [a b]
  (= (dot-prod a b) 0))

(comment
  (as-> nil R
    (make-mx-with 3 3  (fn [i x] i))
    (prn-mx 3 R true)
    (mx-transpose 3 R)
    (prn-mx 3 R )
    )
  
  
  (vec-orthogonal? [0 0 2] [1 0 0])
  ;;;
  )

(defn mx-reciprocal
  "returns the diag matrix with non-zero elems becoming 1/elem"
  [A]
  (mapv #(if (zero? %) %
            (/ 1 %)) A))

(defn mx-trace
  "Returns the sum of all diagonal entries of a matrix"
  [A]
  (let [size (mx-square->size A)]
    (->>
     A
     (keep-indexed #(if
                     (= (index->row %1 size) (index->col %1 size)) %2
                     nil))
     (reduce +))))







