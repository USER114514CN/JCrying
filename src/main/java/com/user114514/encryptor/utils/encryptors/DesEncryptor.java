package com.user114514.encryptor.utils.encryptors;

import com.user114514.encryptor.excep.IllegalDataException;
import com.user114514.encryptor.utils.GeneralEncryptor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class DesEncryptor extends GeneralEncryptor {
    private static final String TRANSFORMATION = "DES/CBC/PKCS5Padding";
    private static final int IV_LEN = 8;
    private static final int KEY_LEN = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        if (data == null || key == null) {
            throw new IllegalArgumentException("data/key 不能为 null。");
        }
        if (key.length != KEY_LEN) {
            throw new IllegalArgumentException("DES 密钥必须为8字节。");
        }
        try {
            // 生成IV
            byte[] iv = new byte[IV_LEN];
            RANDOM.nextBytes(iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, "DES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] cipherText = cipher.doFinal(data);

            // IV + 密文
            byte[] result = new byte[IV_LEN + cipherText.length];
            System.arraycopy(iv, 0, result, 0, IV_LEN);
            System.arraycopy(cipherText, 0, result, IV_LEN, cipherText.length);
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("DES 加密失败。", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) throws IllegalDataException {
        if (data == null || key == null) {
            throw new IllegalDataException("data/key 不能为 null。");
        }
        if (data.length < IV_LEN) {
            throw new IllegalDataException("cipher 文本过短，丢失 IV。");
        }
        if (key.length != KEY_LEN) {
            throw new IllegalDataException("DES 密钥必须为8字节。");
        }
        try {
            // 拆分IV与密文
            byte[] iv = new byte[IV_LEN];
            System.arraycopy(data, 0, iv, 0, IV_LEN);
            byte[] cipherText = new byte[data.length - IV_LEN];
            System.arraycopy(data, IV_LEN, cipherText, 0, cipherText.length);

            SecretKeySpec keySpec = new SecretKeySpec(key, "DES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new IllegalDataException("DES 解密失败, 无效密钥或数据。", e);
        }
    }
}
