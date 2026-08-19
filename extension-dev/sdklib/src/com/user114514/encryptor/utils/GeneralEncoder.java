package com.user114514.encryptor.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

import com.user114514.encryptor.excep.DamagedDataException;

public abstract class GeneralEncoder {
    protected Map<String, String> options;

    public GeneralEncoder() {
    }

    public GeneralEncoder(Map<String, String> options) {
        this.options = null;
    }

    public GeneralEncoder setOptions(Map<String, String> options) {
        return null;
    }

    public byte[] encode(byte[] data) {
        return null;
    }

    public byte[] decode(byte[] data) throws DamagedDataException {
        return null;
    }

    public boolean supportedStreaming() {
        return false;
    }

    public void encodeStreaming(InputStream is, OutputStream os, int bufferSize) throws Exception {
    }

    public void decodeStreaming(InputStream is, OutputStream os, int bufferSize) throws Exception {
    }

    public String encodeToString(byte[] data, Charset charset) {
        return null;
    }

    public byte[] decodeToBytes(String encodeStr, Charset charset) throws DamagedDataException {
        return null;
    }
}
