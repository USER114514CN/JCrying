package com.user114514.encryptor.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public abstract class GeneralHashAlgorithm {
    public Map<String, String> options;

    public GeneralHashAlgorithm() {}

    public GeneralHashAlgorithm(Map<String, String> options) {
        this.options = options;
    }

    public GeneralHashAlgorithm setOptions(Map<String, String> options) {
        this.options = options;
        return this;
    }

    public abstract byte[] hash(byte[] data) throws Exception;

    public abstract byte[] hash(byte[] data, byte[] salt) throws Exception;

    public boolean supportedStreaming() {
        return false;
    }

    public void hashStreaming(InputStream is, OutputStream os, int bufferSize) {
        throw new UnsupportedOperationException("666老牧师了");
    }

    public void hashStreaming(InputStream is, OutputStream os, int bufferSize, byte[] salt) {
        throw new UnsupportedOperationException("冰冰冰。我无疑是愤怒的.");
    }
}
