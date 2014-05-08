(ns om-converter.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [om-converter.convert :refer [html->om]]))

(enable-console-print!)

(def initial-content "<form action=\"/order\" method=\"post\">
  <div class=\"row\">
    <div class=\"small-8\">
      <div class=\"row\">
        <div class=\"small-3 columns\">
          <label for=\"right-label\" class=\"right\">Label</label>
        </div>
        <div class=\"small-9 columns\">
          <input type=\"text\" id=\"right-label\" placeholder=\"Inline Text Input\">
        </div>
      </div>
    </div>
  </div>
</form>")

(def app-state
  (atom {:source initial-content
         :compiled (html->om initial-content)}))

(defn main-view [cursor _]
  (reify
    om/IRender
    (render [_]
      (let [compile-html (fn [e]
                             (let [value (-> e .-target .-value)]
                               (om/update! cursor [:source] value)
                               (om/update! cursor [:compiled] (html->om value))))]
        (dom/div #js {:className "flex flex-row"}
                 (dom/div #js {:className "flex flex-column block-container"}
                          (dom/label nil "HTML")
                          (dom/textarea #js {:value (:source cursor) :onChange compile-html}))
                 (dom/div #js {:className "flex flex-column block-container"}
                          (dom/div #js {:className "flex-row"}
                                   (dom/div #js {:className "flex"})
                                   (dom/label nil "Om"))
                          (dom/textarea #js {:value (:compiled cursor) :readOnly true})))))))

(om/root main-view app-state {:target (.getElementById js/document "container")})