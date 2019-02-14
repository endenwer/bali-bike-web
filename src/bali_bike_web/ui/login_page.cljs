(ns bali-bike-web.ui.login-page
  (:require [bali-bike-web.ant :as ant]
            [re-frame.core :as rf]))

(defn main []
  [ant/button {:on-click #(rf/dispatch [:signin-with-google])} "Login"])
