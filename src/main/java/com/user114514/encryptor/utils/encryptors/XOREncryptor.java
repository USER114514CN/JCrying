package com.user114514.encryptor.utils.encryptors;

import java.io.InputStream;
import java.io.OutputStream;

import com.user114514.encryptor.excep.DamagedDataException;
import com.user114514.encryptor.utils.GeneralEncryptor;

public class XOREncryptor extends GeneralEncryptor {

    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        if (data == null || data.length == 0) return data;
        if (key == null || key.length == 0) return data;

        int keyLen = key.length;
        byte[] out = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = (byte) (data[i] ^ key[i % keyLen]);
        }
        return out;
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) throws DamagedDataException {
        try {
            return encrypt(data, key);
        } catch (RuntimeException e) {
            throw new DamagedDataException("异或解密失败", e);
        }
    }

    @Override
    public boolean supportedStreaming() {
        return true;
    }

    @Override
    public void encryptStreaming(InputStream is, OutputStream os, byte[] key, int bufferSize) throws Exception {
        byte[] buf = new byte[bufferSize];
        int len;
        while ((len = is.read(buf)) != -1) {
            for (int i = 0; i < buf.length; i++) {
                buf[i] = (byte) (buf[i] ^ key[i % key.length]);
            }
            os.write(buf, 0, len);
        }
    }

    @Override
    public void decryptStreaming(InputStream is, OutputStream os, byte[] key, int bufferSize) throws Exception {
        encryptStreaming(is, os, key, bufferSize);
    }
}