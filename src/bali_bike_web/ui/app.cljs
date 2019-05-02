(ns bali-bike-web.ui.app
  (:require [bali-bike-web.ant :as ant]
            [bali-bike-web.constants :as constants]
            [bali-bike-web.ui.form-modal :as form-modal]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(def status-colors
  {"MODERATION" "orange"
   "ACTIVE" "green"})

(defn render-rating
  [bike]
  [:div
   [ant/rate {:disabled true :defaultValue (:rating bike)}]
   [:span (str "(" (:reviews-count bike) ")")]])

(defn render-price
  [price]
  [:span (when price (str "Rp" (.toLocaleString price)))])

(defn render-model
  [bike]
  [:div.model-row
   [:div.bike-model-name (get constants/models (:model-id bike))]
   [:div.bike-id (:id bike)]
   [ant/tag {:color (get status-colors (:status bike))} (:status bike)]])

(defn render-bike-photos
  [bike]
  [:div
   (for [photo (:photos bike)]
     ^{:key photo} [:img {:src photo :style {:margin-right "10px"
                                             :max-width "200px"}}])])

(defn render-bike-actions
  [bike]
  (r/with-let [user-role (rf/subscribe [:user-role])]
    [:div
     (if (and (= @user-role "moderator") (= (:status bike) "MODERATION"))
       [:<>
        [:a {:on-click #(rf/dispatch [:activate-bike (:id bike)])} "Activate"]
        [ant/divider {:type "vertical"}]])
     [:a {:on-click #(rf/dispatch [:edit-bike (:id bike)])} "Edit"]
     [ant/divider {:type "vertical"}]
     [ant/popconfirm {:title "Delete bike?"
                      :ok-text "Yes"
                      :cancel-text "No"
                      :placement "left"
                      :on-confirm #(rf/dispatch [:delete-bike (:id bike)])}
      [:a "Delete"]]]))

(defn render-bikes-table []
  (r/with-let [bikes (rf/subscribe [:bikes])
               bikes-meta (rf/subscribe [:bikes-meta])]
    (let [columns [{:title "Model"
                    :dataIndex "model"
                    :key "model"
                    :width 250
                    :render (fn [_ bike]
                              (r/as-element [render-model (js->clj bike :keywordize-keys true)]))}
                   {:title "Areas"
                    :dataIndex "areas"
                    :key "areas"
                    :width 220
                    :render (fn [_ bike]
                              (r/as-element [:div.area-tags
                                             (for [area-id (get (js->clj bike) "area-ids")]
                                               ^{:key area-id}
                                               [ant/tag (get constants/areas area-id)])]))}
                   {:title "Whatsapp"
                    :dataIndex "whatsapp"
                    :key "whatsapp"
                    :render (fn [_ bike]
                              (let [bike-data (js->clj bike)]
                                (r/as-element [:div (get bike-data "whatsapp")])))}
                   {:title "Daily price"
                    :dataIndex "daily-price"
                    :key "daily-daily"
                    :render #(r/as-element [render-price %])}
                   {:title "Weekly price"
                    :dataIndex "weekly-price"
                    :key "weekly-daily"
                    :render #(r/as-element [render-price %])}
                   {:title "Monthly price"
                    :dataIndex "monthly-price"
                    :key "monthly-price"
                    :render #(r/as-element [render-price %])}
                   {:title "Mileage"
                    :dataIndex "mileage"
                    :key "mileage"
                    :render #(r/as-element [:span (when % (.toLocaleString %))])}
                   {:title "Manufacture Year"
                    :dataIndex "manufacture-year"
                    :key "manufacture-year"}
                   {:title "Actions"
                    :dataIndex "actions"
                    :key "actions"
                    :width 120
                    :render (fn [_ bike]
                              (r/as-element
                               [render-bike-actions (js->clj bike :keywordize-keys true)]))}]]
      [ant/table {:dataSource @bikes
                  :loading (or (:loading? @bikes-meta) false)
                  :expandedRowRender (fn [bike]
                                       (r/as-element [render-bike-photos
                                                      (js->clj bike :keywordize-keys true)]))
                  :className "bikes-table"
                  :rowKey "id"
                  :columns columns
                  :pagination false}])))

(defn main []
  (r/create-class
   {:component-did-mount #(rf/dispatch [:load-bikes])
    :render (fn []
              (r/with-let [show-form? (rf/subscribe [:show-form?])]
                (if @show-form?
                  [form-modal/main]
                  [:div.container
                   [:a.sign-out-link {:on-click #(rf/dispatch [:sign-out])} "SIGN OUT"]
                   [:h1.title "Bikes"]
                   [ant/button {:type "primary"
                                :className "new-bike-button"
                                :on-click #(rf/dispatch [:show-new-bike-form])}
                    "Add bike"]
                   [render-bikes-table]])))}))
