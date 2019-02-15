(ns bali-bike-web.ui.app
  (:require [bali-bike-web.ant :as ant]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(defn main []
  (r/create-class
   {:component-did-mount #(rf/dispatch [:load-bikes])
    :render (fn []
              (let [columns [{:title "Model"
                              :dataIndex "model"
                              :key "model"}
                             {:title "Manufacture Year"
                              :dataIndex "year"
                              :key "year"}
                             {:title "Mileage"
                              :dataIndex "mileage"
                              :key "mileage"}
                             {:title "Rating"
                              :dataIndex "rating"
                              :key "rating"}
                             {:title "Daily price"
                              :dataIndex "daily"
                              :key "daily"}
                             {:title "Monthly price"
                              :dataIndex "monthly"
                              :key "monthly"}
                             {:title "Actions"
                              :dataIndex "actions"
                              :key "actions"}]
                    data [{:key "1"
                           :model "asdf"}]]
                [ant/table {:dataSource data
                            :columns columns
                            :pagination false}]))}))
