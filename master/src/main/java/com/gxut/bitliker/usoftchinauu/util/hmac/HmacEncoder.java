package com.gxut.bitliker.usoftchinauu.util.hmac;


import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class HmacEncoder {

    private final String algorithm;

    public HmacEncoder(String algorithm) {
        this.algorithm = algorithm;
    }


    private static Key toKey(byte[] key, String algorithm) {
        // 鐢熸垚瀵嗛挜
        return new SecretKeySpec(key, algorithm);
    }


    public byte[] getKey() {
        // 鍒濆鍖朘eyGenerator
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        // 浜х敓瀵嗛挜
        SecretKey secretKey = keyGenerator.generateKey();
        // 鑾峰緱瀵嗛挜
        return secretKey.getEncoded();
    }


    public byte[] encode(byte[] data, Key key) {
        Mac mac = null;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return new byte[0];
        }
        return mac.doFinal(data);
    }


    public byte[] encode(byte[] data, byte[] key) {
        return encode(data, toKey(key, algorithm));
    }

}
