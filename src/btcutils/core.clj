(ns btcutils.core
  "Generate Bitcoin key/address pairs."
  (:require [base58.core :as b])
  (:import [java.security MessageDigest SecureRandom]
           org.spongycastle.crypto.generators.ECKeyPairGenerator
           org.spongycastle.crypto.digests.RIPEMD160Digest
           [org.spongycastle.crypto.params ECKeyGenerationParameters ECDomainParameters]
           org.spongycastle.asn1.sec.SECNamedCurves))

(def sr (SecureRandom.))
(def params (SECNamedCurves/getByName "secp256k1"))
(def curve (ECDomainParameters. (.getCurve params)
                                (.getG params)
                                (.getN params)
                                (.getH params)))

(defn sha256hash160 
  "RIPEM160 hash of the SHA256 hash of a byte array."
  [b]
  (let [md (MessageDigest/getInstance "SHA-256")
        sha256 (.digest md b)
        rd (RIPEMD160Digest.)
        out (byte-array 20)]
    (.update rd sha256 0 (count sha256))
    (.doFinal rd out 0)
    out))

(defn public-to-address
  "Derive a Bitcoin address from a public key"
  [k]
  (b/encode-check (sha256hash160 k)
                  (byte 0)))

(defn random-key
  "Generate a random key/address pair."
  []
  (let [gen (ECKeyPairGenerator.)
        key-params (ECKeyGenerationParameters. curve sr)
        _ (.init gen key-params)
        key-pair (.generateKeyPair gen)
        priv (.. key-pair getPrivate getD)
        pub (.. key-pair getPublic getQ getEncoded)
        hash (sha256hash160 pub)
        k (b/encode-check (take-last 33 (-> priv
                                            .toByteArray
                                            (concat [(byte 1)])))
                          (byte -128))
        a (public-to-address pub)]
    {k a}))

(defn public-from-private
  "Derive a Bitcoin public key from a private key expressed as a big integer"
  [n]
  (-> curve
      .getG
      (.multiply n)
      .getEncoded))

(defn -main [& args]
  (let [ka (random-key)]
    (println ka)))