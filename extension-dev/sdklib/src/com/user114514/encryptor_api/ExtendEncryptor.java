package com.user114514.encryptor_api;

import java.util.Map;

import com.user114514.encryptor.utils.GeneralEncryptor;

public abstract class ExtendEncryptor extends GeneralEncryptor {
    public ExtendEncryptor() {
        super();
    }

    public ExtendEncryptor(Map<String, String> options) {
        super(options);
    }
}
