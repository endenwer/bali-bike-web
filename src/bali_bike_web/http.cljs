(ns bali-bike-web.http
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [promesa.core :as p]
            [cljs-http.client :as http]
            [cljs.core.async :as async]))

(defn promisify [method]
  (fn [url opts]
    (p/promise
     (fn [resolve reject]
       (go (let [response (<! (method url opts))
                 callback (if (:success response) resolve reject)]
             (callback response)))))))

(def GET (promisify http/get))
(def POST (promisify http/post))
