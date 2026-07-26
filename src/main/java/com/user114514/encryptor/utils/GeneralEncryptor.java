package com.user114514.encryptor.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.user114514.encryptor.excep.IllegalDataException;

public abstract class GeneralEncryptor {
    protected Map<String, String> options;

    public GeneralEncryptor() {
        this(new HashMap<>());
    }

    public GeneralEncryptor(Map<String, String> options) {
        this.options = options;
    }

    public abstract byte[] encrypt(byte[] data, byte[] key);
    public abstract byte[] decrypt(byte[] data, byte[] key) throws IllegalDataException;

    public String strEncrypt(String data, String key, Charset charset) {
        return new String(encrypt(data.getBytes(charset), key.getBytes(charset)));
    }

    public String strDecrypt(String data, String key, Charset charset) throws IllegalDataException {
        return new String(decrypt(data.getBytes(charset), key.getBytes(charset)), charset);
    }

    public String strEncryptUTF8(String data, String key) {
        return strEncrypt(data, key, StandardCharsets.UTF_8);
    }

    public String strDecryptUTF8(String data, String key) throws IllegalDataException {
        return strDecrypt(data, key, StandardCharsets.UTF_8);
    }
}
