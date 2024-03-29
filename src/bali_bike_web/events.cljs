(ns bali-bike-web.events
  (:require [bali-bike-web.events.auth :as auth-events]
            [bali-bike-web.events.bike :as bike-events]
            [bali-bike-web.api :as api]
            [bali-bike-web.interceptors :as interceptors]
            [bali-bike-web.auth :as auth]
            [bali-bike-web.storage :as storage]
            [re-frame.core :as rf]
            [goog.string.StringBuffer]))

(rf/reg-cofx
 :uuid
 (fn [cofx _]
   (assoc cofx
          :uuid
          (letfn [(f [] (.toString (rand-int 16) 16))
                  (g [] (.toString  (bit-or 0x8 (bit-and 0x3 (rand-int 15))) 16))]
            (.toString
             (goog.string.StringBuffer.
              (f) (f) (f) (f) (f) (f) (f) (f) "-" (f) (f) (f) (f)
              "-4" (f) (f) (f) "-" (g) (f) (f) (f) "-"
              (f) (f) (f) (f) (f) (f) (f) (f) (f) (f) (f) (f)))))))

; auth

(rf/reg-fx :auth/sign-out auth/sign-out)
(rf/reg-event-fx :sign-out auth-events/sign-out-event)
(rf/reg-event-fx :auth-state-changed
                 [interceptors/transform-event-to-kebab]
                 auth-events/auth-state-changed-event)

; bikes

(rf/reg-event-fx :load-bikes bike-events/load-bikes-event)
(rf/reg-event-fx :upload-photo [(rf/inject-cofx :uuid)] bike-events/upload-photo-event)
(rf/reg-event-fx :save-bike bike-events/save-bike-event)
(rf/reg-event-fx :delete-bike bike-events/delete-bike-event)
(rf/reg-event-fx :activate-bike bike-events/activate-bike-event)
(rf/reg-event-db :edit-bike bike-events/edit-bike-event)
(rf/reg-event-db :move-photo bike-events/move-photo-event)
(rf/reg-event-db :remove-photo bike-events/remove-photo-event)
(rf/reg-event-db :update-upload-progress bike-events/update-upload-progress-event)
(rf/reg-event-db :show-new-bike-form bike-events/show-new-bike-form-event)
(rf/reg-event-db :close-form-modal bike-events/close-form-modal-event)
(rf/reg-event-db :change-form-data bike-events/change-formd-data-event)
(rf/reg-event-db :on-bike-saved
                 [interceptors/transform-event-to-kebab]
                 bike-events/on-bike-saved-event)
(rf/reg-event-db :on-bikes-loaded
                 [interceptors/transform-event-to-kebab]
                 bike-events/on-bikes-loaded-event)

; api
(rf/reg-fx :api/send-graphql api/send-graphql)

;storate
(rf/reg-fx :storage/save-photo storage/save-photo)

