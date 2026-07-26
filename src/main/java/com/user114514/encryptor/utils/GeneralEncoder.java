package com.user114514.encryptor.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.user114514.encryptor.excep.IllegalDataException;

public abstract class GeneralEncoder {
    protected Map<String, String> options;

    public GeneralEncoder() {
        this(new HashMap<>());
    }

    public GeneralEncoder(Map<String, String> options) {
        this.options = options;
    }

    public abstract byte[] encode(byte[] data);
    public abstract byte[] decode(byte[] data) throws IllegalDataException;

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

    public byte[] decodeToBytes(String encodeStr, Charset charset) throws IllegalDataException {
        byte[] bytes = encodeStr.getBytes(charset);
        return decode(bytes);
    }

    public byte[] decodeToBytesUTF8(String encodeStr) throws IllegalDataException {
        return decodeToBytes(encodeStr, StandardCharsets.UTF_8);
    }

    public String decodeToStringUTF8(String encodeStr) throws IllegalDataException {
        byte[] raw = decodeToBytesUTF8(encodeStr);
        return new String(raw, StandardCharsets.UTF_8);
    }
}
