(ns antq.dep.github-action
  (:require
   [antq.record :as r]
   [antq.util.ver :as u.ver]
   [clj-yaml.core :as yaml]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.walk :as walk])
  (:import
   java.io.File))

(defn extract-deps
  [workflow-content-str filename]
  (let [deps (atom [])]
    (walk/prewalk (fn [form]
                    (when (and (vector? form)
                               (= :uses (first form)))
                      (swap! deps conj (second form)))
                    form)
                  (yaml/parse-string workflow-content-str))
    (for [d @deps
          :let [[name version] (str/split d #"@" 2)]
          :when (seq version)]
      (r/map->Dependency {:type :github-action
                          :file filename
                          :name name
                          :version (u.ver/normalize-version version)}))))

(defn relative-path
  [^File file]
  (subs (.getAbsolutePath file)
        (dec (count (.getAbsolutePath (io/file "."))))))

(defn load-deps
  ([] (load-deps "."))
  ([dir]
   (let [dir (io/file dir ".github" "workflows")]
     (when (.isDirectory dir)
       (->> (file-seq dir)
            (filter #(.isFile ^File %))
            (mapcat #(extract-deps (slurp %) (relative-path %))))))))
