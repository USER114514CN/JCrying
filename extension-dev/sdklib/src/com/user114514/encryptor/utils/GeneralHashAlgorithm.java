package com.user114514.encryptor.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public abstract class GeneralHashAlgorithm {
    public Map<String, String> options;

    public GeneralHashAlgorithm() {
    }

    public GeneralHashAlgorithm(Map<String, String> options) {
        this.options = null;
    }

    public GeneralHashAlgorithm setOptions(Map<String, String> options) {
        return null;
    }

    public byte[] hash(byte[] data) throws Exception {
        return null;
    }

    public byte[] hash(byte[] data, byte[] salt) throws Exception {
        return null;
    }

    public boolean supportedStreaming() {
        return false;
    }

    public void hashStreaming(InputStream is, OutputStream os, int bufferSize) throws Exception {
    }

    public void hashStreaming(InputStream is, OutputStream os, int bufferSize, byte[] salt) throws Exception {
    }
}
