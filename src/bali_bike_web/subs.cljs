(ns bali-bike-web.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :current-user
 (fn [app-db _]
   (:current-user app-db)))
