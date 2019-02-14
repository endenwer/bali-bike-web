(ns bali-bike-web.ant
  (:require ["antd/lib/menu" :default Menu]
            ["antd/lib/button" :default Button]
            [reagent.core :as r]))

(def menu (r/adapt-react-class Menu))
(def menu-item (r/adapt-react-class (.-Item Menu)))
(def button (r/adapt-react-class Button))

