package com.user114514.encryptor;

public class VersionConfig {
    public static final int VER_CODE = 1;
    public static final String VER_NAME = "pre-alpha-0.0.1";
    public static final String APPLICATION_NAME = "jcrying";

    public static final String[] SUPPORTED_ENCODER = {
        "标准 Base64: base64 std-base64 b64",
        "URL Base64: url-base64",
        "Mime Base64: mime-base64",
        "十六进制: hex hexadecimal",
        "任意进制: r{radixNum 2~36}a[assignment(不支持对 ] 结束符转义)]",
        "摩斯密码: morse mose mos morse-code"
    };
    
    public static final String[] SUPPORTED_ENCRYPTOR = {
        "异或加密(cipher[i]=data[i] ^ key[i % key.length]): xor"
    };
}
