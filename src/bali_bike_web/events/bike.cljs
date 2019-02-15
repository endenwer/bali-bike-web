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
