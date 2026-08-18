package com.user114514.encryptor.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.user114514.encryptor.excep.DamagedDataException;

public abstract class GeneralEncoder {
    protected Map<String, String> options;

    public GeneralEncoder() {
        this(new HashMap<>());
    }

    public GeneralEncoder(Map<String, String> options) {
        this.options = options;
    }

    public GeneralEncoder setOptions(Map<String, String> options) {
        this.options = options;
        return this;
    }

    public abstract byte[] encode(byte[] data);
    public abstract byte[] decode(byte[] data) throws DamagedDataException;
    public boolean supportedStreaming() { return false; }
    public void encodeStreaming(InputStream is, OutputStream os, int bufferSize) throws Exception { throw new UnsupportedOperationException("不支持流式处理。"); }
    public void decodeStreaming(InputStream is, OutputStream os, int bufferSize) throws Exception { throw new UnsupportedOperationException("不支持流式处理。"); }

    public String encodeToString(byte[] data, Charset charset) {
        return new String(encode(data), charset);
    }

    public String encodeToStringUTF8(byte[] data) {
        return encodeToString(data, StandardCharsets.UTF_8);
    }

    public String stringEncodeToString(String strData, Charset charset) {
        byte[] raw = strData.getBytes(charset);
        return encodeToString(raw, charset);
    }

    public String stringEncodeToStringUTF8(String strData) {
        return stringEncodeToString(strData, StandardCharsets.UTF_8);
    }

    public byte[] decodeToBytes(String encodeStr, Charset charset) throws DamagedDataException {
        byte[] bytes = encodeStr.getBytes(charset);
        return decode(bytes);
    }

    public byte[] decodeToBytesUTF8(String encodeStr) throws DamagedDataException {
        return decodeToBytes(encodeStr, StandardCharsets.UTF_8);
    }

    public String decodeToStringUTF8(String encodeStr) throws DamagedDataException {
        byte[] raw = decodeToBytesUTF8(encodeStr);
        return new String(raw, StandardCharsets.UTF_8);
    }
}
