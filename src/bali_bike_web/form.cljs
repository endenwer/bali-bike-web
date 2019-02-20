(ns bali-bike-web.form
  (:require [reagent.core :as r]
            [forms.core :as f]
            [forms.validator :as v]))

(def not-blank [:not-blank (fn [v _ _] (not (empty? v)))])
(def is-integer [:is-integer (fn [v _ _] (integer? v))])

(def validator
  (v/validator {:model-id [is-integer]
                :area-ids [not-blank]
                :daily-price [is-integer]
                :monthly-price [is-integer]
                :mileage [is-integer]
                :manufacture-year [is-integer]}))

(def create
  (f/constructor validator))
