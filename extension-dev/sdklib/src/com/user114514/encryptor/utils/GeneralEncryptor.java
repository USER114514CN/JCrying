package com.user114514.encryptor.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

import com.user114514.encryptor.excep.DamagedDataException;

public abstract class GeneralEncryptor {
    protected Map<String, String> options;

    public GeneralEncryptor() {
    }

    public GeneralEncryptor(Map<String, String> options) {
        this.options = null;
    }

    public GeneralEncryptor setOptions(Map<String, String> options) {
        return null;
    }

    public byte[] encrypt(byte[] data, byte[] key) {
        return null;
    }

    public byte[] decrypt(byte[] data, byte[] key) throws DamagedDataException {
        return null;
    }

    public boolean supportedStreaming() {
        return false;
    }

    public void encryptStreaming(InputStream is, OutputStream os, byte[] key, int bufferSize) throws Exception {
    }

    public void decryptStreaming(InputStream is, OutputStream os, byte[] key, int bufferSize) throws Exception {
    }

    public String strEncrypt(String data, String key, Charset charset) {
        return null;
    }

    public String strDecrypt(String data, String key, Charset charset) throws DamagedDataException {
        return null;
    }
}
