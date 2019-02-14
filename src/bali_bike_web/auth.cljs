(ns bali-bike-web.auth
  (:require ["firebase/app" :as firebase]
            ["firebase/auth"]
            [re-frame.core :as rf]
            [promesa.async-cljs :refer-macros [async]]
            [promesa.core :as p :refer-macros [alet]]))

(def user-instance (atom nil))

(defn get-token []
  (when-let [user @user-instance]
    (->
     (alet [token (p/await (.getIdToken user))] token)
     (p/catch (fn [error] nil)))))

(defn sign-in-with-google []
  (let [auth (.auth firebase)
        provider (firebase/auth.GoogleAuthProvider.)]
    (->
     (async (.signInWithPopup auth provider))
     (p/catch (fn [error] (.log js/console error))))))

(defn sign-out []
  (let [auth (.auth firebase)]
    (.signOut auth)))

(defn listen-user-auth []
  (let [auth (.auth firebase)]
    (.onAuthStateChanged auth (fn [user]
                                (reset! user-instance user)
                                (let [user-data (if user (js->clj (.toJSON user)) nil)]
                                  (rf/dispatch [:auth-state-changed user-data]))))))

(defn init []
  (firebase/initializeApp
   #js {:apiKey "AIzaSyB9KaM4ipBVgBp6or0HpmlW3pAly2SwWQc",
        :authDomain "bali-bike-app.firebaseapp.com",
        :databaseURL "https://bali-bike-app.firebaseio.com",
        :projectId "bali-bike-app",
        :storageBucket "bali-bike-app.appspot.com",
        :messagingSenderId "982817603223"})
  (listen-user-auth))

