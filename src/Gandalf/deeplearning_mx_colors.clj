(ns Gandalf.deeplearning-mx-colors
  (:require [clojure.repl :refer :all]
   [Gandalf.deeplearning-mx :refer :all :as plants]))


(comment

  (def A (plants/mkmx [[1 0 2]
                       [-1 3 4]
                       [7 1 0]]))

  (plants/system-of-linear-equations  [1 2 3] A)

  (def B (plants/mk-square-mx 3 1))

  (plants/det-leibniz B)

  (->
   (plants/mk-square-mx 3 1)
   (plants/multiply-vec [1 2 3])

   (plants/prnmx 3))

  (->
   (plants/mk-square-mx 3 0)
   (plants/multiply-vec [1 2 3])

   (plants/prnmx 3))

  (plants/vec-unit [1 2 3])

  (def I (plants/iden 3))
  (def q [17,246,239])
  (def q-unit (plants/vec-unit q))

  (def Aq (plants/multiply-vec I q))

  (def qM (mkmx [[17 0 0
                  0 246 0
                  0 0 239]]))


  (def Aq (multiply qM I 3 3))

  (def U (mk-square-mx 3 1))
  (def Aq (multiply qM U 3 3))

  (def qU (multiply q-unit U 1 3))

  (def qI (multiply q-unit I 1 3))

  (def yellow [[255 250 30]
               [240 243 20]
               [238 255 10]])

  (def teal [[10 251 248]
             [15 240 242]
             [30 243 254]])

  (def magenta [[238 12 250]
                [239 21 253]
                [248 30 245]])

  (def red [[255 2 3]
            [250 10 9]
            [247 8 11]])

  (nth red 2)

  (def Y (->>
          (reduce (fn [acc x]
                    (multiply (vec-unit x) acc 1 3)) U  yellow)
          vec))

  (prnmx Y 3)


  (det-leibniz Y)
  (det-leibniz U)

  (inverse Y)

  (system-of-linear-equations q-unit Y)

  (def R (mkmx [[1 1 1]
                [2 2 2]
                [3 3 3]]))

  (det-leibniz R)

  (def M (->>
          (reduce (fn [acc x]
                    (multiply (vec-unit x) acc 1 3)) I  magenta)
          vec))

  (def U2 (mk-square-mx 3 0.5))

  (det-leibniz U2)

  (def U3 (mkmx [[1 0.5 0.5]
                 [0.5 1 0.5]
                 [0.5 0.5 1]]))


  (= (det-leibniz U3) (det-leibniz (transpose U3 3)))


  (multiply [1 2 3] U3 1 3); nonsense



  (defn multiply-A-by-vector-mxs
    "returns matrix after making an iden-like mxs from vectors and multiplying them one after another : V*A "
    [vs A]
    (->
     (reduce (fn [acc x]
               (let [u    (vec-unit x)
                     size (size-square-matrix A)
                     m    (diag-v u)]
                ;  (prnmx acc 3)
                ;  (prnmx m 3)
                ;  (prn (det-leibniz acc))
                 (multiply m acc size size))) A  vs)

     vec))

  (defn vec-against-linear-systems
    "prints out vecs after solving system-of-linear-equations against matrices"
    [v ms key]
    (println key "---")
    (doseq [M ms]
      (println (system-of-linear-equations v M)))
    (println "---"))

  (def M2 (->
           (reduce (fn [acc x]
                     (let [u (vec-unit x)
                           m (mkmx [[(nth u 0) 0 0]
                                    [0 (nth u 1) 0]
                                    [0 0 (nth u 2)]])]
                       (prnmx acc 3)
                       (prnmx m 3)
                       (prn (det-leibniz acc))
                       (multiply m acc 3 3))) U3  magenta)

           vec))
  (prnmx M2 3)


  (def B (mkmx [[1 0.5 0.5]
                [0.5 1 0.5]
                [0.5 0.5 1]]))

  (def Mg (multiply-A-by-vector-mxs magenta B))

  (def Y (multiply-A-by-vector-mxs yellow B))

  (def T (multiply-A-by-vector-mxs teal B))

  (def R (multiply-A-by-vector-mxs red B))


  (system-of-linear-equations (vec-unit [241 11 251]) Mg) ; magenta 
  (system-of-linear-equations (vec-unit [235 35 255]) Mg) ; magenta 
  (system-of-linear-equations (vec-unit [17 246 239]) Mg) ; teal
  (system-of-linear-equations (vec-unit [241 249 19]) Mg) ; yellow



  (vec-against-linear-systems (vec-unit [235 35 255]) [Mg Y T R] :magenta) ; magenta 
  (vec-against-linear-systems (vec-unit [17 246 239]) [Mg Y T R] :teal) ; teal
  (vec-against-linear-systems (vec-unit [241 249 19]) [Mg Y T R] :yellow) ; yellow



  (->>
   (multiply  M2 q-unit 3 1)
   det-leibniz)

  (inverse M2)
  (det-leibniz M2)


  ; design matrix

  (def q [17,246,239])
  (def q-unit (plants/vec-unit q))

  (def yellow-dm (mkmx [[255 250 30]
                        [240 243 20]
                        [238 255 10]]))

  (def teal-dm (mkmx  [[10 251 248]
                       [15 240 242]
                       [30 243 254]]))

  (def magenta-dm (mkmx [[238 12 250]
                         [239 21 253]
                         [248 30 245]]))

  (def red-dm (mkmx [[255 2 3]
                     [250 10 9]
                     [247 8 11]]))
  
  (system-of-linear-equations   q  yellow-dm)
  
  (system-of-linear-equations   q  teal-dm)
  
  (system-of-linear-equations   q  magenta-dm)
  
  (system-of-linear-equations   q  red-dm)
  
  
  
  
  



  ;;;
  )
