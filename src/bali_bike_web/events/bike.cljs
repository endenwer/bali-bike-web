(ns bali-bike-web.events.bike
  (:require [bali-bike-web.edb :as edb]))

;; helpers

(defn move-element
  [v old-index new-index]
  (assoc (assoc v new-index (v old-index)) old-index (v new-index)))

(def bike-query
  [:id :modelId :photos
   :dailyPrice :monthlyPrice
   :weeklyPrice
   :rating :reviewsCount
   :mileage :manufactureYear
   :areaIds :status :whatsapp
   :onlyContacts
   :address :addressLat :addressLng])

;; events

(defn on-bikes-loaded-event
  [db [_ {:keys [data]}]]
  (let [user-role (:user-role db)
        query-name (if (= user-role "moderator") :bikes-on-moderation :own-bikes)]
    (edb/append-collection db :bikes :list (query-name data) {:loading? false})))

(defn load-bikes-event
  [{:keys [db]} [_ _]]
  (let [bikes (edb/get-collection db :bikes :list)
        user-role (:user-role db)
        query-name (if (= user-role "moderator") :bikesOnModeration :ownBikes)]
    (when (empty? bikes)
      {:db (edb/insert-meta db :bikes :list {:loading? true})
       :api/send-graphql {:query
                          [query-name bike-query]
                          :callback-event :on-bikes-loaded}})))

(defn show-new-bike-form-event
  [db [_ _]]
  (-> db
      (edb/remove-collection :photos :list)
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
  {:db (edb/append-collection db :photos :list [{:id uuid :progress 0 :status "progress"}])
   :storage/save-photo {:id uuid :file file}})

(defn update-upload-progress-event
  [db [_ data]]
  (edb/update-item-by-id db :photos (:id data) data))

(defn remove-photo-event
  [db [_ id]]
  (edb/remove-item db :photos id))

(defn move-photo-event
  [db [_ old-index new-index]]
  (let [photos (edb/get-collection db :photos :list)]
    (edb/insert-collection db :photos :list (move-element photos old-index new-index))))

(defn on-bike-saved-event
  [db [_ {:keys [data]}]]
  (let [bike-data (or (:create-bike data) (:update-bike data))
        bike (edb/get-item-by-id db :bikes (:id bike-data))
        db-with-bikes (if bike
                        (edb/update-item-by-id db :bikes (:id bike) bike-data)
                        (edb/prepend-collection db :bikes :list [bike-data]))]
    (-> db-with-bikes
        (edb/remove-collection :photos :list)
        (assoc :bike-submitting? false)
        (assoc :show-form? false))))

(defn save-bike-event
  [{:keys [db]}
   [_ {:keys [id
              model-id
              manufacture-year
              mileage
              daily-price
              monthly-price
              weekly-price
              area-ids
              address
              whatsapp]}]]
  (let [photos (edb/get-collection db :photos :list)
        photo-urls (into [] (filter some? (map :url photos)))
        shared-params {:photos photo-urls
                       :mileage mileage
                       :dailyPrice daily-price
                       :weeklyPrice weekly-price
                       :monthlyPrice monthly-price
                       :areaIds area-ids
                       :whatsapp whatsapp
                       :address (:address address)
                       :addressLat (str (:lat address))
                       :addressLng (str (:lng address))
                       :onlyContacts true}
        create-params (into {} (remove (comp nil? second)
                                       (merge shared-params {:modelId model-id
                                                             :manufactureYear manufacture-year})))
        create-mutation [:createBike create-params bike-query]
        update-params (into {} (remove (comp nil? second) (merge shared-params {:id id})))
        update-mutation [:updateBike update-params bike-query]]
    {:db (assoc db :bike-submitting? true)
     :api/send-graphql {:mutation (if id update-mutation create-mutation)
                        :callback-event :on-bike-saved}}))

(defn edit-bike-event
  [db [_ id]]
  (let [bike (edb/get-item-by-id db :bikes id)
        photos (map-indexed #(identity {:id %1 :url %2 :status "success"}) (:photos bike))]
    (-> db
        (assoc :form-data bike)
        (assoc-in [:form-data :address] {:address (:address bike)
                                         :lat (js/parseFloat (:address-lat bike))
                                         :lng (js/parseFloat (:address-lng bike))})
        (assoc :show-form? true)
        (edb/insert-collection :photos :list photos))))

(defn delete-bike-event
  [{:keys [db]} [_ id]]
  (let [bikes (edb/get-collection db :bikes :list)
        filtered-bikes (filter #(not= (:id %)id) bikes)]
    {:db (edb/insert-collection db :bikes :list filtered-bikes)
     :api/send-graphql {:mutation [:deleteBike {:id id} [:id]]}}))

(defn activate-bike-event
  [{:keys [db]} [_ id]]
  {:db (edb/update-item-by-id db :bikes id {:status "ACTIVE"})
   :api/send-graphql {:mutation [:activateBike {:id id} [:id]]}})
