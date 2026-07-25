package com.user114514.encryptor.utils.encoders;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import com.user114514.encryptor.excep.IllegalDataException;
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

    @Override
    public byte[] encode(byte[] data) {
        return encoder.encode(data);
    }

    @Override
    public byte[] decode(byte[] data) throws IllegalDataException {
        try {
            return decoder.decode(data);
        } catch (Exception e) {
            throw new IllegalDataException("输入编码器的数据并不是有效的 Base64 编码字符串。", e);
        }
    }
}
