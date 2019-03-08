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
 :user-loaded?
 (fn [app-db _]
   (:user-loaded? app-db)))

(rf/reg-sub
 :bike-submitting?
 (fn [app-db _]
   (:bike-submitting? app-db)))

(rf/reg-sub
 :form-data
 (fn [app-db _]
   (:form-data app-db)))

(rf/reg-sub
 :bikes
 (fn [app-db _]
   (edb/get-collection app-db :bikes :list)))

(rf/reg-sub
 :bikes-meta
 (fn [app-db _]
   (edb/get-collection-meta app-db :bikes :list)))

(rf/reg-sub
 :photos
 (fn [app-db _]
   (edb/get-collection app-db :photos :list)))
