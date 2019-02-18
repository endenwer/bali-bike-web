(ns bali-bike-web.ui.form-modal
  (:require [bali-bike-web.ant :as ant]
            [bali-bike-web.constants :as constants]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :as string]))

(defn render-bike-model
  [form-data]
  [ant/form-item {:label "Bike model"
                  :className "model-input"}
   [ant/select {:placeholder "Select bike model"
                :showSearch true
                :optionFilterProp "children"
                :filterOption (fn [input option]
                                (>= (.indexOf
                                     (.props.children.toLowerCase option)
                                     (.toLowerCase input))
                                    0))}
    (for [[id title] constants/models]
      ^{:key id} [ant/select-option {:value id} title])]])

(defn render-bike-areas
  [form-data]
  [ant/form-item {:label "Areas"
                  :className "areas-input"}
   [ant/select {:placeholder "Select areas" :mode "multiple"}
    (for [[id title] constants/areas]
      ^{:key id} [ant/select-option {:value id} title])]])

(defn render-bike-price
  [{:keys [title value class-name]}]
  [ant/form-item {:label title
                  :className class-name}
   [ant/input-number {:size "200"
                      :parser #(string/replace % #"Rp\s?|(,*)" "")
                      :formatter #(str "Rp " (string/replace % #"\B(?=(\d{3})+(?!\d))" ","))}]])

(defn render-bike-manufacture-year
  [form-data]
  [ant/form-item {:label "Manufacture year"
                  :className "manufacture-year-input"}
   [ant/input-number]])

(defn render-bike-mileage
  [form-data]
  [ant/form-item {:label "Mileage"
                  :className "mileage-input"}
   [ant/input-number]])

(defn main []
  (r/with-let [form-data (rf/subscribe [:form-data])]
    [:div.modal
     [:div.close-btn
      [ant/icon {:type :close :on-click #(rf/dispatch [:close-form-modal])}]]
     [ant/form {:layout "vertical" :className "form-container"}
      [render-bike-model @form-data]
      [render-bike-areas @form-data]
      [render-bike-price {:title "Daily price"
                          :class-name "daily-price-input"
                          :price (:daily-price @form-data)}]
      [render-bike-price {:title "Monthly price"
                          :class-name "monthly-price-input"
                          :price (:monthly-price @form-data)}]
      [render-bike-mileage @form-data]
      [render-bike-manufacture-year @form-data]]]))
