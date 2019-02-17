(ns bali-bike-web.subs
  (:require [re-frame.core :as rf]
            [bali-bike-web.edb :as edb]))

(rf/reg-sub
 :current-user
 (fn [app-db _]
   (:current-user app-db)))

(rf/reg-sub
 :show-form?
 (fn [app-db _]
   (:show-form? app-db)))

(rf/reg-sub
 :bikes
 (fn [app-db _]
   (edb/get-collection app-db :bikes :list)))
