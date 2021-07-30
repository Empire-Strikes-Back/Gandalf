(ns csbooks.linear-algebra-kmeans
  (:require [clojure.repl :refer :all]
            [csbooks.linear-algebra-print :refer [cprn]]
            [csbooks.linear-algebra-math :refer :all])
  ;
  )


(defn j-clust
  "Returns a scalar J (clustering objective estimate).
   The lower the better clustering is.
   Mean square distance from vecs to their representatives.
   "
  [xs zs cs]
  (->
   (reduce-kv (fn [acc i x]
                (+ acc (sq (vec-dist x (zs (cs i)))))) 0 xs)
   (/ (count xs))))



(defn j-clust-nearest
  "Returns a scalar J.
   Is a version of j-lcust, picks min||x - z|| (distance from vec to representative)
   to minimize J.
  "
  [xs zs]
  (->
   (reduce-kv (fn [acc i x]
                (+ acc (sq (vec-dist x (second (nearest-neighbor x zs)))))) 0 xs)
   (/ (count xs))))

(defn j-clust-nearest-p
  "Returns a scalar J.
   Is a version of j-lcust, picks min||x - z|| (distance from vec to representative)
   to minimize J.
  "
  [xs zs]
  (as-> nil R
    (pmap (fn [x]
            [x (sq (vec-dist x (second (nearest-neighbor x zs))))]) xs)
    (vec R)
    (reduce-kv (fn [acc i [x sq-dist]]
                 (+ acc sq-dist)) 0 R)
    (/ R (count xs))
    ;
    ))

(defn group-centroid
  "Returns the average of vecs"
  [xs]
  (apply vecs-mean xs))


(defn -k-means-lazy-partition
  "Does not work, take-while is sequential, coll is not sorted"
  [xs zs]
  (lazy-seq
   (when-let [xs-s (seq xs)]
     (let [fst       (first xs-s)
           nrst-neig (second (nearest-neighbor fst zs))
           run       (cons fst (take-while #(= nrst-neig (second (nearest-neighbor % zs))) (next xs-s)))]
       (cprn run)
       (prn)
       (cons run (-k-means-lazy-partition (lazy-seq (drop (count run) xs-s))  zs))
        ;
       ))))

(defn k-means-iteration
  "Returns k-means groups after one iteration."
  [xs zs]
  (persistent!
   (reduce-kv
    (fn [acc i x]
      (let [nrst-neig (nearest-neighbor x zs)
            i-zs      (first nrst-neig)]
        (assoc! acc i-zs (conj (get acc i-zs []) x)))
      ;
      )
    (transient {}) xs)))

(defn k-means-iteration-p
  "Returns k-means groups after one iteration."
  [xs zs]
  (as-> nil R
    (pmap (fn [x]
            [x (nearest-neighbor x zs)])
          xs)
    (vec R)
    (persistent!
     (reduce-kv
      (fn [acc i [x nrst-neig]]
        (let [i-zs      (first nrst-neig)]
          (assoc! acc i-zs (conj (get acc i-zs []) x)))
      ;
        )
      (transient {}) R))
    ;
    ))

(defn k-means
  "Returns a group map {i-group [[x] [x] ..] .. }.
   Iterates until J (objective) is minimized"
  ([xs zs]
   (k-means xs zs nil 0 nil nil))
  ([xs zs J-prev cnt iters ]
  ;  (prn "zs:" (count zs))
   (let    [groups (k-means-iteration-p xs zs)
            J      (reduce + (mapv (fn [[i g]]
                                     (j-clust-nearest-p g zs)) groups))]
    ;  (cprn {:groups groups
    ;         :iter   cnt
    ;         :zs     zs
    ;         :J      J})
    ;  (prn "groups:" (keys groups))
     (cond
       (and (>= (inc cnt) (or iters 3)) (==* J J-prev)) {:J      J
                                                 :zs     zs
                                                 :iter   (inc cnt)
                                                 :groups groups}
       :else (k-means xs
                      ; (mapv (fn [[i g]] (apply vecs-mean g)) groups)
                      (vec (pmap (fn [[i g]] (apply vecs-mean g)) groups))
                      J (inc cnt) iters)
       ;
       ))))

(defn k-means-rand-x->z
  "Returns (k-means ..), representatives are chosen randomly from xs"
  [xs & {:keys [k i]
         :or   {k 3 i 3}}]
  (prn ":k" k ":i" i)
  (as-> nil zs
    (mapv (fn [_] (rand-nth xs)) (range k))
    (k-means xs zs nil 0 i)))

(defn k-means-rand-z
  "Returns (k-means ..), representatives are vecs of radndom ints"
  [xs & {:keys [k i]
         :or   {k 4 i 3}}]
  (prn ":k" k ":i" i)
  (as-> nil zs
    (mapv (fn [_] (mapv (fn [_] (rand-int 255)) (range (count (first xs))))) (range k))
    (k-means xs zs nil 0 i)))

(defn partitionv
  "Returns vec of vecs "
  [ n v]
  (->>
   (partition n v)
   (mapv vec)))

(defn k-means-rand-gs
  "Returns (k-means ..), representatives are means of initial random groups"
  [xs & {:keys [k i]
         :or   {k 3
                i 3}}]
  (prn ":k" k ":i" i)
  (as-> nil R
    (shuffle xs)
    (partitionv (/ (count R) k) R)
    ; (mapv (fn [g] (apply vecs-mean g)) R)
    (pmap (fn [g] (apply vecs-mean g)) R)
    (vec R)
    (k-means xs R nil 0 i)))

(comment

  (def points-A [[0.5 8.5]
                 [2.2 11.5]
                 [2.7 0]
                 [3.7 2.5]
                 [3.5 5.3]
                 [3.5 7.2]
                 [5 5.5]
                 [7.1 5.1]
                 [7.5 4.9]
                 [7.8 6.8]])

  (def points-B [[4.5 12.8]
                 [6.1 13.8]
                 [8 14.9]
                 [8.5 14.6]
                 [7.9 17.1]
                 [12.5 18]
                 [12.7 17.7]

                 [11 12.9]
                 [11.5 10.1]
                 [12.9 11]])
  (def points-C [[10 7.5]
                 [10.9 7.1]
                 [12.1 10]
                 [14.1 9.9]
                 [12 4.5]
                 [12.9 1]
                 [15.8 0.2]
                 [15.7 1.4]
                 [15.9 7]
                 [14.4 7.1]])

  (def points (shuffle (concat points-A points-B points-C)))
  (count points)

  (def points-A-centroid [4 5])
  (def points-B-centroid [9 15])
  (def points-C-centroid [14 5])

  (def zs [points-A-centroid points-B-centroid points-C-centroid])


  (def clusters (vec (-k-means-lazy-partition points zs)))
  (count clusters)

  (nearest-neighbor [2.7 0] zs)
  (nearest-neighbor [10 7.5] zs)

  (partition-by odd? [1 2 2 3 1 4 5 6 8 10 9 1 2 7 4])

  (def groups (k-means-iteration points zs))

  (reduce + (mapv (fn [[i g]]
                    (j-clust-nearest g zs)) groups))

  (def zs-2 [[3 3] [8 8] [15 15]])

  (def zs-3 [[0 10] [10 0] [13 15]])


  (def clusters-2 (k-means points zs))

  (def clusters-3 (k-means points zs-2)) ; successfully does 5 iterations, prn progress

  (def clusters-4 (k-means points zs-3))

  (def clusters-5 (k-means points (mapv (fn [_] (rand-nth points)) (range 3))))

  (def clusters-6 (k-means points (mapv (fn [_] (rand-nth points)) (range 4))))

  (def clusters-7 (k-means points (mapv (fn [_] (rand-nth points)) (range 5))))

  (doseq [c [clusters-2 clusters-3 clusters-4 clusters-5]]
    (cprn (c :J))
    (cprn (c :zs)))

  (doseq [c [clusters-5 clusters-6 clusters-7]]
    (cprn (c :J))
    (cprn (c :zs)))


  (k-means-rand-x->z points :k 20)

  (k-means-rand-gs points :k 3 :i 20)



  ;;;
  )

; heuristic