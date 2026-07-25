package com.user114514.encryptor.utils.encryptors;

import com.user114514.encryptor.excep.IllegalDataException;
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
    public byte[] decrypt(byte[] data, byte[] key) throws IllegalDataException {
        try {
            return encrypt(data, key);
        } catch (RuntimeException e) {
            throw new IllegalDataException("异或解密失败", e);
        }
    }
}