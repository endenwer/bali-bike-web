(ns bali-bike-web.events.bike
  (:require [bali-bike-web.edb :as edb]))

(defn on-bikes-loaded-event
  [db [_ {:keys [data]}]]
  (edb/append-collection db :bikes :list (:own-bikes data) {:loading? false}))

(defn load-bikes-event
  [{:keys [db]} [_ _]]
  (let [bikes (edb/get-collection db :bikes :list)]
    (when (empty? bikes)
      {:db (edb/insert-meta db :bikes :list {:loading? true})
       :api/send-graphql {:query
                          [:ownBikes
                           [:id :modelId :photos
                            :dailyPrice :monthlyPrice
                            :rating :reviewsCount
                            :mileage :manufactureYear]]
                          :callback-event :on-bikes-loaded}})))

(defn show-new-bike-form-event
  [db [_ _]]
  (-> db
      (assoc :show-form? true)
      (assoc :form-data (:new-bike db))))

(defn close-form-modal-event
  [db [_ _]]
  (assoc db :show-form? false))

(defn change-formd-data-event
  [db [_ id value]]
  (assoc-in db [:form-data id] value))

(defn upload-photo-event
  [{:keys [db uuid]} [_ file]]
  {:db (edb/append-collection db :photos :list [{:id uuid :progress 0 :status "progress"}])})
