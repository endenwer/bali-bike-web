(ns bali-bike-web.ui.form-modal
  (:require [bali-bike-web.ant :as ant]
            [bali-bike-web.constants :as constants]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :as string]
            ["react-sortable-hoc" :refer [SortableContainer SortableElement arrayMove]]))

(defn render-bike-model
  [{:keys [model-id]}]
  [ant/form-item {:label "Bike model"
                  :className "model-input"}
   [ant/select {:placeholder "Select bike model"
                :value model-id
                :onChange #(rf/dispatch [:change-form-data :model-id %])
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
  [{:keys [area-ids]}]
  [ant/form-item {:label "Areas"
                  :className "areas-input"}
   [ant/select {:placeholder "Select areas"
                :on-change #(rf/dispatch [:change-form-data :area-ids (js->clj %)])
                :value (or area-ids [])
                :mode "multiple"}
    (for [[id title] constants/areas]
      ^{:key id} [ant/select-option {:value id} title])]])

(defn render-bike-price
  [{:keys [title price id]}]
  [ant/form-item {:label title
                  :className (str (name id) "-input")}
   [ant/input-number {:value price
                      :on-change #(rf/dispatch [:change-form-data id %])
                      :parser #(string/replace (str %) #"Rp\s?|(,*)" "")
                      :formatter #(str "Rp " (string/replace (str %) #"\B(?=(\d{3})+(?!\d))" ","))}]])

(defn render-bike-manufacture-year
  [{:keys [manufacture-year]}]
  [ant/form-item {:label "Manufacture year"
                  :className "manufacture-year-input"}
   [ant/input-number {:value manufacture-year
                      :on-change #(rf/dispatch [:change-form-data :manufacture-year %])}]])

(defn render-bike-mileage
  [{:keys [mileage]}]
  [ant/form-item {:label "Mileage"
                  :className "mileage-input"}
   [ant/input-number {:value mileage
                      :parser #(string/replace (str %) #"(,*)" "")
                      :formatter #(string/replace (str %) #"\B(?=(\d{3})+(?!\d))" ",")
                      :on-change #(rf/dispatch [:change-form-data :mileage %])}]])

(defn render-bike-photo-item
  [{:keys [url status progress]}]
  [:div.photo-upload-preview
   (if (= status "progress")
     [ant/progress {:type "circle" :percent progress}]
     [:img {:src url}])])

(def render-bike-photo
  (r/adapt-react-class
   (SortableElement (r/reactify-component render-bike-photo-item))))

(defn render-photos-container
  [{:keys [photos]}]
  [ant/form-item {:label "Photos"
                  :className "photos-upload-container"}
   (doall
    (map-indexed (fn [index photo]
                   ^{:key (.-id photo)} [render-bike-photo (assoc (js->clj photo) :index index)])
                 photos))
   [ant/dragger {:multiple true :customRequest #(rf/dispatch [:upload-photo (.-file %)])}
    [ant/icon {:type "plus"}]
    [:div {:class-name "ant-upload-text"} "Upload photos"]]])

(def render-bike-photos
  (r/adapt-react-class
   (SortableContainer (r/reactify-component render-photos-container))))

(defn render-buttons []
  [:div.form-buttons
   [ant/button "Cancel"]
   [ant/button {:type "primary" :htmlType "submit"} "Save"]])

(defn main []
  (r/with-let [form-data (rf/subscribe [:form-data])
               photos (rf/subscribe [:photos])]
    [:div.modal
     [:div.close-btn
      [ant/icon {:type :close :on-click #(rf/dispatch [:close-form-modal])}]]
     [ant/form {:layout "vertical" :className "form-container"}
      [render-bike-model @form-data]
      [render-bike-areas @form-data]
      [render-bike-price {:title "Daily price"
                          :id :daily-price
                          :price (:daily-price @form-data)}]
      [render-bike-price {:title "Monthly price"
                          :id :monthly-price
                          :price (:monthly-price @form-data)}]
      [render-bike-mileage @form-data]
      [render-bike-manufacture-year @form-data]
      [render-bike-photos {:axis "xy" :photos @photos}]
      [render-buttons]]]))
