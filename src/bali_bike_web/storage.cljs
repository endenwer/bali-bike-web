(ns bali-bike-web.storage
  (:require ["firebase/app" :as firebase]
            ["firebase/storage"]
            [clojure.string :as string]
            [re-frame.core :as rf]))

(defn save-photo
  [{:keys [id file]}]
  (let [file-extension (last (string/split (.-name file) "."))
        file-name (str id "." file-extension)
        storage-ref (.ref (.storage firebase))
        upload-task (.put (.child storage-ref (str "bike_photos/" file-name)) file)]
    (.on upload-task
         "state_changed"
         (fn [snapshot]
           (let [progress (int (* (/ (.-bytesTransferred snapshot) (.-totalBytes snapshot)) 100))]
             (rf/dispatch [:update-upload-progress
                           {:id id :status "progress" :progress progress}])))
         (fn [error]
           (rf/dispatch [:update-upload-progress {:id id :status "error"}]))
         (fn []
           (.then
            (.snapshot.ref.getDownloadURL upload-task)
            #(rf/dispatch [:update-upload-progress {:id id :status "success" :url %}]))))))
