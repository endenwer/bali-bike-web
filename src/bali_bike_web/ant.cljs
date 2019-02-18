(ns bali-bike-web.ant
  (:require ["antd/lib/menu" :default Menu]
            ["antd/lib/button" :default Button]
            ["antd/lib/table" :default Table]
            ["antd/lib/rate" :default Rate]
            ["antd/lib/divider" :default Divider]
            ["antd/lib/icon" :default Icon]
            ["antd/lib/form" :default Form]
            ["antd/lib/input" :default Input]
            ["antd/lib/select" :default Select]
            ["antd/lib/input-number" :default InputNumber]
            [reagent.core :as r]))

(def menu (r/adapt-react-class Menu))
(def menu-item (r/adapt-react-class (.-Item Menu)))
(def button (r/adapt-react-class Button))
(def table (r/adapt-react-class Table))
(def rate (r/adapt-react-class Rate))
(def divider (r/adapt-react-class Divider))
(def icon (r/adapt-react-class Icon))
(def form (r/adapt-react-class Form))
(def form-item (r/adapt-react-class (.-Item Form)))
(def input (r/adapt-react-class Input))
(def select (r/adapt-react-class Select))
(def select-option (r/adapt-react-class (.-Option Select)))
(def input-number (r/adapt-react-class InputNumber))
