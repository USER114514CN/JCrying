package com.user114514.encryptor.utils.hashalgorithms;

import java.security.MessageDigest;
import java.util.Map;

import com.user114514.encryptor.utils.GeneralHashAlgorithm;

public class SHA256HashAlgorithm extends GeneralHashAlgorithm {

    public SHA256HashAlgorithm(Map<String, String> options) {
        super(options);
    }

    @Override
    public byte[] hash(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }

    @Override
    public byte[] hash(byte[] data, byte[] salt) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(data);
        digest.update(salt);
        return digest.digest();
    }
    
}
