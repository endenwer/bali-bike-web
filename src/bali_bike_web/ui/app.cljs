(ns bali-bike-web.ui.app
  (:require [bali-bike-web.ant :as ant]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn render-bikes-table []
  (r/with-let [bikes (rf/subscribe [:bikes])]
    (let [columns [{:title "Model"
                              :dataIndex "model"
                              :key "model"}
                             {:title "Manufacture Year"
                              :dataIndex "manufacture-year"
                              :key "manufacture-year"}
                             {:title "Mileage"
                              :dataIndex "mileage"
                              :key "mileage"}
                             {:title "Rating"
                              :dataIndex "rating"
                              :key "rating"}
                             {:title "Daily price"
                              :dataIndex "daily-price"
                              :key "daily-daily"}
                             {:title "Monthly price"
                              :dataIndex "monthly-price"
                              :key "monthly-price"}
                             {:title "Actions"
                              :dataIndex "actions"
                              :key "actions"}]]
                [ant/table {:dataSource @bikes
                            :className "bikes-table"
                            :rowKey "id"
                            :columns columns
                            :bordered true
                            :pagination false}])))

(defn main []
  (r/create-class
   {:component-did-mount #(rf/dispatch [:load-bikes])
    :render (fn []
              [:div.container
               [:a.sign-out-link {:on-click #(rf/dispatch [:sign-out])} "SIGN OUT"]
               [:h1.title "Bikes"]
               [ant/button {:type "primary" :className "new-bike-button"} "Add bike"]
               [render-bikes-table]])}))
