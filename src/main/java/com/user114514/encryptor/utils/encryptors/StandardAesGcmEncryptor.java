package com.user114514.encryptor.utils.encryptors;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.user114514.encryptor.excep.IllegalDataException;
import com.user114514.encryptor.utils.GeneralEncryptor;

import java.security.SecureRandom;

public class StandardAesGcmEncryptor extends GeneralEncryptor {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        try {
            // 1. 生成随机 IV (Nonce)
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            
            // 2. 初始化 Cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec params = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, params);
            
            // 3. 执行加密 (自动添加 Auth Tag)
            byte[] ciphertext = cipher.doFinal(data);
            
            // 4. 拼接 IV + Ciphertext (因为解密需要 IV)
            byte[] result = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(ciphertext, 0, result, iv.length, ciphertext.length);
            
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("加密时出现错误。", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) throws IllegalDataException {
        if (data == null || key == null || data.length < GCM_IV_LENGTH) 
            throw new IllegalDataException("Invalid input");

        try {
            // 1. 提取 IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(data, 0, iv, 0, GCM_IV_LENGTH);
            
            // 2. 提取密文
            byte[] ciphertext = new byte[data.length - GCM_IV_LENGTH];
            System.arraycopy(data, GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);
            
            // 3. 初始化解密
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec params = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, params);
            
            // 4. 解密并验证 Tag (如果 Tag 不匹配，doFinal 会抛出 AEADBadTagException)
            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            throw new IllegalDataException("Decryption failed or integrity check failed", e);
        }
    }
}
