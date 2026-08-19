package com.user114514.encryptor_api;

import java.util.Map;

import com.user114514.encryptor.utils.GeneralHashAlgorithm;

public abstract class ExtendHashAlgorithm extends GeneralHashAlgorithm {
    public ExtendHashAlgorithm() {
        super();
    }

    public ExtendHashAlgorithm(Map<String, String> options) {
        super(options);
    }
}
