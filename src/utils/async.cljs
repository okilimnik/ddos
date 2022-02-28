(ns utils.async
  (:require-macros [utils.async]))

(defn sleep [ms]
  (js/Promise. (fn [resolve]
                 (js/setTimeout resolve ms))))
