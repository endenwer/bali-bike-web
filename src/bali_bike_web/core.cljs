(ns bali-bike-web.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [bali-bike-web.auth :as auth]
            [bali-bike-web.ui.login-page :as login-page]
            [bali-bike-web.ui.app :as app]
            [bali-bike-web.ant :as ant]
            [bali-bike-web.subs]
            [bali-bike-web.events]))

(defn app-root []
  (r/with-let [current-user (rf/subscribe [:current-user])]
    (if @current-user [app/main] [login-page/main])))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (r/render-component [app-root] (.getElementById js/document "app")))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))

(defn ^:export main []
  (.log js/console "start")
  (auth/init)
  (start))
