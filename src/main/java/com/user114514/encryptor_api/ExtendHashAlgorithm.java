package com.user114514.encryptor_api;

import java.util.Map;

public abstract class ExtendHashAlgorithm extends com.user114514.encryptor.utils.GeneralHashAlgorithm {
    public ExtendHashAlgorithm() { super(); }
    public ExtendHashAlgorithm(Map<String, String> options) { super(options); }
}