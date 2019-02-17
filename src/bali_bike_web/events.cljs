(ns bali-bike-web.events
  (:require [bali-bike-web.events.auth :as auth-events]
            [bali-bike-web.events.bike :as bike-events]
            [bali-bike-web.api :as api]
            [bali-bike-web.interceptors :as interceptors]
            [bali-bike-web.auth :as auth]
            [re-frame.core :as rf]))

; auth

(rf/reg-fx :auth/sign-in-with-google auth/sign-in-with-google)
(rf/reg-fx :auth/sign-out auth/sign-out)
(rf/reg-event-fx :signin-with-google auth-events/sign-in-with-google-event)
(rf/reg-event-fx :sign-out auth-events/sign-out-event)
(rf/reg-event-fx :auth-state-changed
                 [interceptors/transform-event-to-kebab]
                 auth-events/auth-state-changed-event)

; bikes

(rf/reg-event-fx :load-bikes bike-events/load-bikes-event)
(rf/reg-event-db :show-new-bike-form bike-events/show-new-bike-form-event)
(rf/reg-event-db :close-form-modal bike-events/close-form-modal-event)
(rf/reg-event-db :on-bikes-loaded
                 [interceptors/transform-event-to-kebab]
                 bike-events/on-bikes-loaded-event)

; api
(rf/reg-fx :api/send-graphql api/send-graphql)
