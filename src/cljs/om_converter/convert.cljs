(ns om-converter.convert
  (:require [clojure.string :as string]))

(defn- id-from-tag [tag]
  (str "dom/" (-> (str tag) (subs 1))))

(defn- padding [n]
  (apply str (repeat (* n 2) " ")))

(defn- translate [_ _])

(defn- translate-children [children level]
  (if (empty? children)
    ""
    (apply str "\n" (padding level) (map #(translate % level) children))))

(defn translate-attribute [attr]
  (case attr
    :class :className
    :readonly :readOnly
    attr))

(defn translate-attributes [attrs]
  (apply merge {} (for [[k v] attrs] {(translate-attribute k) v})))

(defn- translate [item level]
  (if (string? item)
    (pr-str item)
    (let [[tag attributes & children] item
          dom-id (id-from-tag tag)]
      (apply str
             "("
             dom-id
             " #js "
             (pr-str (translate-attributes (or attributes {})))
             (translate-children children (+ 1 level))
             ")"))))

(defn hiccup->om [x] (translate x 0))

(defn- node-create [tag]
  (.createElement js/document tag))

(defn- node-name [node]
  (-> node .-tagName .toLowerCase))

(defn- node-children [node]
  (array-seq (.-childNodes node)))

(defn- node-attribute->hash [attr]
  (hash-map (keyword (.-nodeName attr)) (.-nodeValue attr)))

(defn- node-attributes [node]
  (reduce #(merge % (node-attribute->hash %2)) {} (array-seq (.-attributes node))))

(defn- node-text-content [node]
  (.-textContent node))

(defn- node-set-html [node s]
  (set! (.-innerHTML node) s))

(def ^:private node-type-element 1)
(def ^:private node-type-text 3)

(defn- blank-child? [child]
  (or
    (nil? child)
    (and (string? child) (string/blank? child))))

(defmulti read-html-node #(.-nodeType %))

(defmethod read-html-node node-type-text [node]
  (node-text-content node))

(defmethod read-html-node node-type-element [node]
  (let [tag (node-name node)
        children (node-children node)]
    (concat [(keyword tag) (node-attributes node)] (remove blank-child? (map read-html-node children)))))

(defmethod read-html-node :default [_] nil)

(defn- build-children [s]
  (let [div (node-create "div")]
    (node-set-html div s)
    (node-children div)))

(defn html->hiccup [s]
  (->> (build-children s)
       (map read-html-node)
       first))

(defn html->om [s]
  (->> s html->hiccup hiccup->om))