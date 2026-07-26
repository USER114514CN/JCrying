package com.user114514.encryptor.utils.encryptors;

import com.user114514.encryptor.excep.IllegalDataException;
import com.user114514.encryptor.utils.GeneralEncryptor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class BlowfishEncryptor extends GeneralEncryptor {
    private static final String TRANSFORM = "Blowfish/CBC/PKCS5Padding";
    private static final int IV_BYTE_LEN = 8;
    private static final int MIN_KEY_LEN = 8;
    private static final int MAX_KEY_LEN = 56;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        if (data == null || key == null) {
            throw new IllegalArgumentException("明文数据、密钥不能为null");
        }
        if (key.length < MIN_KEY_LEN || key.length > MAX_KEY_LEN) {
            throw new IllegalArgumentException("Blowfish密钥长度范围：8 ~ 56 字节");
        }

        try {
            byte[] iv = new byte[IV_BYTE_LEN];
            SECURE_RANDOM.nextBytes(iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] cipherText = cipher.doFinal(data);

            byte[] result = new byte[IV_BYTE_LEN + cipherText.length];
            System.arraycopy(iv, 0, result, 0, IV_BYTE_LEN);
            System.arraycopy(cipherText, 0, result, IV_BYTE_LEN, cipherText.length);
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Blowfish加密发生异常", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) throws IllegalDataException {
        if (data == null || key == null) {
            throw new IllegalDataException("密文、密钥不能为null");
        }
        if (data.length < IV_BYTE_LEN) {
            throw new IllegalDataException("密文长度不足，缺失IV向量");
        }
        if (key.length < MIN_KEY_LEN || key.length > MAX_KEY_LEN) {
            throw new IllegalDataException("Blowfish密钥长度范围：8 ~ 56 字节");
        }

        try {
            byte[] iv = new byte[IV_BYTE_LEN];
            System.arraycopy(data, 0, iv, 0, IV_BYTE_LEN);
            byte[] cipherText = new byte[data.length - IV_BYTE_LEN];
            System.arraycopy(data, IV_BYTE_LEN, cipherText, 0, cipherText.length);

            SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new IllegalDataException("Blowfish解密失败：密钥错误或数据损坏", e);
        }
    }
}
