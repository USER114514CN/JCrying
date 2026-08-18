package com.user114514.encryptor_api;

import java.util.Map;

public abstract class ExtendEncryptor extends com.user114514.encryptor.utils.GeneralEncryptor {
    public ExtendEncryptor() { super(); }
    public ExtendEncryptor(Map<String, String> options) { super(options); }
}