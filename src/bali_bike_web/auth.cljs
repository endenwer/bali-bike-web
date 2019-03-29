(ns bali-bike-web.auth
  (:require ["firebase/app" :as firebase]
            ["firebase/auth"]
            [re-frame.core :as rf]
            [promesa.async-cljs :refer-macros [async]]
            [promesa.core :as p :refer-macros [alet]]
            [bali-bike-web.api :as api]))

(defn sign-out []
  (let [auth (.auth firebase)]
    (.signOut auth)))

(defn- update-role
  [user]
  (if user
    (->
     (p/promise (.currentUser.getIdTokenResult (.auth firebase)))
     (p/then (fn [token ]
               (let [role (.-claims.role token)]
                 (when-not (contains? #{"bike-owner" "moderator"} role)
                   (api/send-graphql {:mutation [:changeRole {:role "bike-owner"}]}))
                 role))))
    (p/promise nil)))

(defn listen-user-auth []
  (let [auth (.auth firebase)]
    (.onAuthStateChanged
     auth
     (fn [user]
       (let [user-data (if user (js->clj (.toJSON user)) nil)]
         (->
          (update-role user)
          (p/then #(rf/dispatch [:auth-state-changed user-data %]))))))))

(defn init []
  (firebase/initializeApp
   #js {:apiKey "AIzaSyB9KaM4ipBVgBp6or0HpmlW3pAly2SwWQc",
        :authDomain "bali-bike-app.firebaseapp.com",
        :databaseURL "https://bali-bike-app.firebaseio.com",
        :projectId "bali-bike-app",
        :storageBucket "bali-bike-app.appspot.com",
        :messagingSenderId "982817603223"})
  (listen-user-auth))

