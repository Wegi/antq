(ns antq.dep.shadow-test
  (:require
   [antq.dep.shadow :as sut]
   [antq.record :as r]
   [clojure.java.io :as io]
   [clojure.test :as t]))

(defn- dependency
  [m]
  (r/map->Dependency (merge {:type :java :file "shadow-cljs.edn"} m)))

(t/deftest extract-deps-test
  (let [deps (sut/extract-deps
              (slurp (io/resource "dep/shadow-cljs.edn")))]
    (t/is (sequential? deps))
    (t/is (every? #(instance? antq.record.Dependency %) deps))
    (t/is (= #{(dependency {:name "foo/core" :version "1.0.0"})
               (dependency {:name "bar" :version "2.0.0"})
               (dependency {:name "baz" :version "3.0.0"})}
             (set deps)))))
