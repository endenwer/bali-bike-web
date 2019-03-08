(ns bali-bike-web.ui.login-page
  (:require [bali-bike-web.ant :as ant]
            [re-frame.core :as rf]
            ["firebase/app" :as firebase]
            ["react-firebaseui/StyledFirebaseAuth" :default firebase-ui]))

(def firebase-ui-config
  {:signInFlow "popup"
   :signInOptions [(.-auth.GoogleAuthProvider.PROVIDER_ID firebase)]})

(defn main []
  [:div.login-page
   [:h1 "BaliBike"]
   [:> firebase-ui {:uiConfig firebase-ui-config :firebaseAuth (.auth firebase)}]])
