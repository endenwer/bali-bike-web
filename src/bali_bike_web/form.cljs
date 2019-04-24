(ns bali-bike-web.form
  (:require [reagent.core :as r]
            [forms.core :as f]
            [forms.validator :as v]))

(def not-blank [:not-blank (fn [v _ _] (not (empty? v)))])
(def is-integer [:is-integer (fn [v _ _] (integer? v))])
(def is-integer-or-nil [:is-integer-or-nil (fn [v _ _] (or (integer? v) (nil? v)))])
(def greater-then-zero [:greater-then-zero (fn [v _ _] (> v 0))])

(def validator
  (v/validator {:model-id [is-integer]
                :daily-price [is-integer-or-nil]
                :weekly-price [is-integer-or-nil]
                :monthly-price [is-integer-or-nil]
                :mileage [is-integer-or-nil]
                :manufacture-year [is-integer-or-nil]
                :photos-count [greater-then-zero]}))

(def create
  (f/constructor validator))
