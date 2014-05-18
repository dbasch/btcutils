(defproject btcutils "0.1.0"
  :description "Generate Bitcoin keys and addresses."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main btcutils.core
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.madgag.spongycastle/prov "1.50.0.0"]
                 [base58 "0.1.0"]])
