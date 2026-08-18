package com.user114514.encryptor.utils.hashalgorithms;

import java.security.MessageDigest;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.user114514.encryptor.utils.GeneralHashAlgorithm;

public class HMACSHA256HashAlgorithm extends GeneralHashAlgorithm {
    public HMACSHA256HashAlgorithm(Map<String, String> options) {
        super(options);
    }

    @Override
    public byte[] hash(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }

    @Override
    public byte[] hash(byte[] data, byte[] salt) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(salt, "HmacSHA256");
        mac.init(keySpec);
        return mac.doFinal(data);
    }
}
