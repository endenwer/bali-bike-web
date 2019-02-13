(ns bali-bike-web.core
  (:require [reagent.core :as r]))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (r/render-component [:div "HELLO"] (.getElementById js/document "app")))

(defn ^:export main []
  (.log js/console "start")
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
