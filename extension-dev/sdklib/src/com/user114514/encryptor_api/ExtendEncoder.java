package com.user114514.encryptor_api;

import java.util.Map;

import com.user114514.encryptor.utils.GeneralEncoder;

public abstract class ExtendEncoder extends GeneralEncoder {
    public ExtendEncoder() {
        super();
    }

    public ExtendEncoder(Map<String, String> options) {
        super(options);
    }
}
