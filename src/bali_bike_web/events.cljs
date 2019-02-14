(ns bali-bike-web.events
  (:require [bali-bike-web.events.auth :as auth-events]
            [bali-bike-web.interceptors :as interceptors]
            [bali-bike-web.auth :as auth]
            [re-frame.core :as rf]))

(rf/reg-fx :auth/sign-in-with-google auth/sign-in-with-google)
(rf/reg-fx :auth/sign-out auth/sign-out)
(rf/reg-event-fx :signin-with-google auth-events/sign-in-with-google-event)
(rf/reg-event-fx :sign-out auth-events/sign-out-event)
(rf/reg-event-fx :auth-state-changed
                 [interceptors/transform-event-to-kebab]
                 auth-events/auth-state-changed-event)
