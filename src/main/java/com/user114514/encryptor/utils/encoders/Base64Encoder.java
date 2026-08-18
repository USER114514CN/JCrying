package com.user114514.encryptor.utils.encoders;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Map;

import com.user114514.encryptor.excep.DamagedDataException;
import com.user114514.encryptor.utils.GeneralEncoder;

public class Base64Encoder extends GeneralEncoder {
    private Encoder encoder;
    private Decoder decoder;

    private Base64Encoder(Encoder enc, Decoder dec) {
        this.encoder = enc;
        this.decoder = dec;
    }

    public static Base64Encoder standard() {
        return new Base64Encoder(Base64.getEncoder(), Base64.getDecoder());
    }

    public static Base64Encoder url() {
        return new Base64Encoder(Base64.getUrlEncoder(), Base64.getUrlDecoder());
    }

    public static Base64Encoder mime() {
        return new Base64Encoder(Base64.getMimeEncoder(), Base64.getMimeDecoder());
    }

    public Base64Encoder setOptions(Map<String, String> options) {
        this.options = options;
        return this;
    }

    @Override
    public byte[] encode(byte[] data) {
        return encoder.encode(data);
    }

    @Override
    public byte[] decode(byte[] data) throws DamagedDataException {
        try {
            return decoder.decode(data);
        } catch (Exception e) {
            throw new DamagedDataException("输入编码器的数据并不是有效的 Base64 编码字符串。", e);
        }
    }

    @Override
    public boolean supportedStreaming() {
        return true;
    }

    @Override
    public void encodeStreaming(java.io.InputStream is, java.io.OutputStream os, int bufferSize) throws Exception {
        try (java.io.OutputStream base64OutputStream = encoder.wrap(os)) {
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                base64OutputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    @Override
    public void decodeStreaming(java.io.InputStream is, java.io.OutputStream os, int bufferSize) throws Exception {
        try (java.io.InputStream base64InputStream = decoder.wrap(is)) {
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = base64InputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
}
