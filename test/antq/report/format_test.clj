(ns antq.report.format-test
  (:require
   [antq.report :as report]
   [antq.report.format]
   [antq.test-helper :as h]
   [clojure.string :as str]
   [clojure.test :as t]))

(defn- reporter
  [deps error-format]
  (report/reporter deps {:reporter "format"
                         :error-format error-format}))

(t/deftest reporter-test
  (let [dummy-deps [(h/test-dep {:file "a" :name "foo" :version "1" :latest-version "2"})
                    (h/test-dep {:file "b" :name "bar" :version "1" :latest-version nil})]]

    (t/is (seq (with-out-str (reporter
                              dummy-deps
                              "::error file={{file}}::{{message}}"))))

    (t/is (= ["::error file=a::foo,1,2" "::error file=b::bar,1,"]
             (str/split-lines
              (with-out-str
                (reporter
                 dummy-deps
                 "::error file={{file}}::{{name}},{{version}},{{latest-version}}")))))))
