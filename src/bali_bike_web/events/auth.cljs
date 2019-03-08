(ns bali-bike-web.events.auth)

(defn auth-state-changed-event
  [{:keys [db]} [_ current-user]]
  {:db (assoc db :current-user current-user :user-loaded? true)})

(defn sign-out-event
  [_ [_ _]]
  {:auth/sign-out nil})
