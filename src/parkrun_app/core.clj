(ns parkrun-app.core
  (:require [net.cgrand.enlive-html :as html]))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(def scrape
  (fetch-url "https://www.parkrun.com/cancellations/"))

(def saturday "2018-09-29")

(def sunday "2018-09-30")

(defn get-dates
  [scrape]
  (->> (html/select scrape [:div.wysiwyg])
       (first)
       (:content)
       (partition 2)))

(defn dates-of-interest?
  [scrape]
  (let [date (->> scrape
                  (first)
                  (:content)
                  (first))]
    (if (or (= saturday date)
            (= sunday date))
      true)))

(def dates (filter dates-of-interest? (get-dates scrape)))

(defn extract-data
  [dates]
  (let [parkrun (html/select (second (first dates)) [:li])]
    ((juxt (partial map #(->> %
                        (:content)
                        (second)))
           (partial map #(->> %
                        (:content)
                        (first)
                        (:attrs)
                        (:href)))
           (partial map #(->> %
                        (:content)
                        (first)
                        (:content)
                        (first)))) parkrun)))

(extract-data dates)

(partition 3 (apply interleave (extract-data dates)))

dates
