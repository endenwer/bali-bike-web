(ns bali-bike-web.ui.form-modal
  (:require [bali-bike-web.ant :as ant]
            [re-frame.core :as rf]))

(defn main []
  [:div.modal
   [:div.close-btn
    [ant/icon {:type :close :on-click #(rf/dispatch [:close-form-modal])}]]
   [:div.form-container "FORM"]])
