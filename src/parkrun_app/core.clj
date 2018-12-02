(ns parkrun-app.core
  (:require [chime :refer [chime-at]]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [clj-time.periodic :as p]
            [net.cgrand.enlive-html :as html])
  (:import [org.joda.time DateTimeConstants DateTimeZone]))

(defn fetch-url
  []
  (html/html-resource (java.net.URL. "https://www.parkrun.com/cancellations/")))

(defn get-dates
  [scrape]
  (->> (html/select scrape [:div.wysiwyg])
       (first)
       (:content)
       (partition 2)))

(defn dates-of-interest?
  [scrape {:keys [saturday sunday]}]
  (let [date (->> scrape
                  (first)
                  (:content)
                  (first))]
    (if (or (= saturday date)
            (= sunday date))
      true)))

(defn extract-data
  [dates]
  (let [parkruns (html/select (second (first dates)) [:li])]
    (map #(zipmap [:reason :url :name] %)
         (map (juxt #(->> %
                          (:content)
                          (second))
                    #(->> %
                          (:content)
                          (first)
                          (:attrs)
                          (:href))
                    #(->> %
                          (:content)
                          (first)
                          (:content)
                          (first))) parkruns))))

(defn format-message
  [parkrun]
  (when (not-empty parkrun)
    (str "Hello! Your " (:name parkrun)
         " will not happen this weekend. The reason" (:reason parkrun)
         ". For more information, check out " (:url parkrun))))

(defn get-weekend
  []
  (take 3 (->> (p/periodic-seq (.. (t/now)
                                   (withZone (DateTimeZone/forID "Europe/London")))
                               (-> 1 t/days))
               (filter (comp #{DateTimeConstants/FRIDAY  ;; keep Friday to use in run-job later (or remove it)
                            DateTimeConstants/SATURDAY
                            DateTimeConstants/SUNDAY}
                          #(.getDayOfWeek %))))))

(defn format-saturday-and-sunday
  []
  (let [weekend (rest (map #(f/unparse (f/formatter "yyyy-MM-dd") %) (get-weekend)))]
    {:saturday (first weekend)
     :sunday   (second weekend)}))

(defn run-job
  [parkrun-of-interest]
  (chime-at [(-> 1 t/seconds t/from-now)]  ;; for the sake of running it now and seeing all the fun!
            (fn [time]
              (->> (fetch-url)
                   (get-dates)
                   (filter #(dates-of-interest? % (format-saturday-and-sunday)))
                   (extract-data)
                   (filter #(= ( :name %) parkrun-of-interest))
                   (first)
                   (format-message)
                   (println)))))

(run-job "Ally Pally parkrun")
