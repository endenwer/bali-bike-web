(ns bali-bike-web.events.auth)

(defn sign-in-with-google-event
  [{:keys [db]} [_ _]]
  {:db (assoc db :signing-in? true)
   :auth/sign-in-with-google nil})

(defn auth-state-changed-event
  [{:keys [db]} [_ current-user]]
  {:db (assoc db :current-user current-user :signing-in? false)})

(defn sign-out-event
  [_ [_ _]]
  {:auth/sign-out nil})
