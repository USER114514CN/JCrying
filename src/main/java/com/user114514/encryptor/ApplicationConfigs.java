package com.user114514.encryptor;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ApplicationConfigs {
    public static final int VER_CODE = 1;
    public static final String VER_NAME = "beta-v0.4";
    public static final String APPLICATION_NAME = "jcrying";

    public static final List<String> ALGORITHM_TYPES = List.of(
        "encode",
        "decode",
        "encrypt",
        "decrypt",
        "hash"
    );

    public static final String[] SUPPORTED_ENCODER = {
        "标准 Base64: base64 std-base64 b64",
        "URL Base64: url-base64",
        "Mime Base64: mime-base64",
        "十六进制: hex hexadecimal",
        "任意进制: r{radixNum 2~36}s[separator(不支持对 ] 结束符转义)]",
        "摩斯密码: morse mose mos morse-code"
    };
    
    public static final String[] SUPPORTED_ENCRYPTOR = {
        "异或加密(cipher[i]=data[i] ^ key[i % key.length]): xor",
        "AES-GCM: aes aes-gcm std-aes std-aes-gcm",
        "DES: des",
        "DesEde: desede",
        "Blowfish: blowfish",
        "RC2: rc2"
    };

    public static final String PACKAGE_INSTALLED_PERFIX = "packages/installed/";
    public static final String PACKAGE_FILE_IDENTIFIER = "JCRYING-EXTENDPACKAGE";

    public static final String INSTALLED_PACKAGES_LIST_FILE = "packages/installedpacks.txt";

    public static ResourceBundle r;

    public static final Scanner INPUT = new Scanner(System.in);
}
