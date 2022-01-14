(ns Gandalf.programming-clojure
  (:require [clojure.repl :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.io InputStream File)
           (java.util Random Locale)
           (java.text MessageFormat))
  )

; (example-fn required-arg)
; (example-fn optional-arg?)
; (example-fn zero-or-more-arg*)
; (example-fn one-or-more-arg+)
; (example-fn & collection-of-variable-args)

(defn blank?
  [str]
  (every? #(Character/isWhitespace %) str))

(defrecord Person [first-name last-name])

(def foo (->Person "First Name" "Second Name"))


(comment
  (blank? "    ")

  (prn foo)

  (.. "hello" getClass getProtectionDomain)

  (.start (Thread. (fn [] (prn "Hello" (Thread/currentThread)))))
  
  (/ 1 0)
  (pst)
  
  (def visitors (atom #{}))
  
  (swap! visitors conj "John")
  
  (deref visitors)
  @visitors
  
  (source deref)

  
  ;
  )


;  a     a Java array
; agt    an agent
; coll   collection
; expr   an expression
; f      a function
; idx    Index
; r      a ref
; v      a vector
; val    a value

(comment
  
  (find-doc "reduce")
  
  (ancestors (class [1 2 3]))
  
  (quot 22 7) ; 3
  (rem 22 7); 1
  
  (println "another\nmultiline\nstring")
  
  foo
  
  (var foo)
  
  #'foo
  
  
  (let [[x y :as coords] [1 2 3 4 5 6]]
    (str "x: " x ", y: " y ", total dimensions " (count coords)))
  
  ;
  )


(defn ellipsize [words]
  (let [[w1 w2 w3] (str/split words #"\s+")]
    (str/join " " [w1 w2 w3 "..."])))

(comment

  (ellipsize "The quick brown fox jumps over the lazy dog.")

  (resolve 'foo)

  (.exists (File. "/tmp"))

  (def rnd (new java.util.Random))

  (def rnd (new Random))


  (. rnd nextInt)

  (. Math PI)

  (loop [result [] x 5]
    (if (zero? x)
      result
      (recur (conj result x) (dec x))))
  
  (into [] (take 5 (iterate dec 5)))
  
  (into [] (drop-last (reverse (range 6))))
  
  (vec (reverse (rest (range 6))))
  
  ;
  )

(defn countdown [result x]
  (if (zero? x)
    result
    (recur (conj result x) (dec x))))

(defn indexed [coll] (map-indexed vector coll))

(defn index-filter [pred coll]
  (when pred
    (for [[idx elt] (indexed coll) :when (pred elt)] idx)))
(defn index-of-any [pred coll]
  (first (index-filter pred coll)))

(comment

  (countdown [] 5)
  (indexed "abcde")

  (index-filter #{\a \b} "abcdbbb")
  (index-of-any #{\z \a} "zzabyycdxx")
  (index-of-any #{\b \y} "zzabyycdxx")

  (nth (index-filter #{:h} [:t :t :h :t :h :t :t :t :h :h])
       2)

  (meta #'str)

  (defn ^{:tag String} shout [^{:tag String} s] (.toUpperCase s))

  (meta #'shout)
  
  (defn ^String shout [^String s] (.toUpperCase s))
  
  
  ;
  )

(defn shout
  ([s] (.toUpperCase s))
  {:tag String})

(defn minutes-to-millis [mins] (* mins 1000 60))

(defn recently-modified? [file]
  (> (.lastModified file)
     (- (System/currentTimeMillis) (minutes-to-millis 30))))

(comment
  (meta #'shout)
  
  (cons 0 '(1 2 3))
  
  (class (rest [1 2 3]))
  
  (map #(.getName %) (.listFiles (File. ".")))
  
  (filter recently-modified? (file-seq (File. ".")))
  ;
  )

(defn non-blank? [line] (if (re-find #"\S" line) true false))

(defn non-svn? [file] (not (.contains (.toString file) ".svn")))

(defn clojure-source? [file] (.endsWith (.toString file) ".clj"))

(defn clojure-loc [base-file]
  (reduce
   +
   (for [file (file-seq base-file)
         :when (and (clojure-source? file) (non-svn? file))]
     (with-open [rdr (io/reader file)]
       (count (filter non-blank? (line-seq rdr)))))))

(comment
  
  (clojure-loc (java.io.File. "/home/user/code/clojure"))
  
  ;
  )

