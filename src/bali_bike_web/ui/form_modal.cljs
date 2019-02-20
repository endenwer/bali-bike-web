(ns bali-bike-web.ui.form-modal
  (:require [bali-bike-web.ant :as ant]
            [bali-bike-web.constants :as constants]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :as string]
            [bali-bike-web.form :as form]
            [forms.core :as f]
            ["react-sortable-hoc" :refer [SortableContainer SortableElement arrayMove]]))

(defn render-bike-model
  [{:keys [model-id on-change is-valid?]}]
  [ant/form-item {:label "Bike model"
                  :validate-status (if is-valid? "success" "error")
                  :help (when-not is-valid? "Please select bike model")
                  :className "model-input"}
   [ant/select {:placeholder "Select bike model"
                :value model-id
                :onChange on-change
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
  [{:keys [area-ids is-valid? on-change]}]
  [ant/form-item {:label "Areas"
                  :validate-status (if is-valid? "success" "error")
                  :help (when-not is-valid? "Please select at least one area")
                  :className "areas-input"}
   [ant/select {:placeholder "Select areas"
                :on-change on-change
                :value (or area-ids [])
                :mode "multiple"}
    (for [[id title] constants/areas]
      ^{:key id} [ant/select-option {:value id} title])]])

(defn render-bike-price
  [{:keys [title price is-valid? on-change id]}]
  [ant/form-item {:label title
                  :validate-status (if is-valid? "success" "error")
                  :help (when-not is-valid? "Please input price")
                  :className (str (name id) "-input")}
   [ant/input-number {:value price
                      :on-change on-change
                      :parser #(string/replace (str %) #"Rp\s?|(,*)" "")
                      :formatter #(str "Rp "
                                       (string/replace (str %) #"\B(?=(\d{3})+(?!\d))" ","))}]])

(defn render-bike-manufacture-year
  [{:keys [manufacture-year is-valid? on-change]}]
  [ant/form-item {:label "Manufacture year"
                  :validate-status (if is-valid? "success" "error")
                  :help (when-not is-valid? "Please input manufacture year")
                  :className "manufacture-year-input"}
   [ant/input-number {:value manufacture-year
                      :on-change on-change}]])

(defn render-bike-mileage
  [{:keys [mileage is-valid? on-change]}]
  [ant/form-item {:label "Mileage"
                  :validate-status (if is-valid? "success" "error")
                  :help (when-not is-valid? "Please input mileage")
                  :className "mileage-input"}
   [ant/input-number {:value mileage
                      :parser #(string/replace (str %) #"(,*)" "")
                      :formatter #(string/replace (str %) #"\B(?=(\d{3})+(?!\d))" ",")
                      :on-change on-change}]])

(defn render-bike-photo-item
  [{:keys [url status progress]}]
  [:div.photo-upload-preview
   (case status
     "progress" [ant/progress {:type "circle" :percent progress}]
     "error" [ant/progress {:type "circle" :percent progress :status "exception"}]
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

(defn render-form
  [form photos]
  (r/with-let [form-data (f/data form)]
    (let [on-change (fn [path value]
                      (swap! form-data assoc path value)
                      (when-not @(f/is-valid-path? form path) (f/validate! form true)))]
      [ant/form {:layout "vertical"
                 :on-submit (fn [e]
                              (.preventDefault e)
                              (f/validate! form))
                 :className "form-container"}
       [render-bike-model {:model-id (:model-id @form-data)
                           :is-valid? @(f/is-valid-path? form :model-id)
                           :on-change #(on-change :model-id %)}]
       [render-bike-areas {:area-ids (:area-ids @form-data)
                           :is-valid? @(f/is-valid-path? form :area-ids)
                           :on-change #(on-change :area-ids %)}]
       [render-bike-price {:title "Daily price"
                           :id :daily-price
                           :is-valid? @(f/is-valid-path? form :daily-price)
                           :on-change #(on-change :daily-price %)
                           :price (:daily-price @form-data)}]
       [render-bike-price {:title "Monthly price"
                           :id :monthly-price
                           :is-valid? @(f/is-valid-path? form :monthly-price)
                           :on-change #(on-change :monthly-price %)
                           :price (:monthly-price @form-data)}]
       [render-bike-mileage {:mileage (:mileage @form-data)
                             :is-valid? @(f/is-valid-path? form :mileage)
                             :on-change #(on-change :mileage %)}]
       [render-bike-manufacture-year {:manufacture-year (:manufacture-year @form-data)
                                      :is-valid? @(f/is-valid-path? form :manufacture-year)
                                      :on-change #(on-change :manufacture-year %)}]
       [render-bike-photos {:axis "xy" :photos photos}]
       [render-buttons]])))

(defn main []
  (r/with-let [form-data-sub (rf/subscribe [:form-data])
               form (form/create @form-data-sub)
               photos (rf/subscribe [:photos])]
    [:div.modal
     [:div.close-btn
      [ant/icon {:type :close :on-click #(rf/dispatch [:close-form-modal])}]]
     [render-form form @photos]]))
