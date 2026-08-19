package com.user114514.encryptor_api;

import java.util.List;

public class ExtendPackageClassLoader extends ClassLoader {

    public List<String> whitelistPerfixs = null;
    public List<String> blacklistPerfixs = null;

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return null;
    }

    public static enum Permissions {
            ALLOWED, REJECTED, INQUIRE
    }

    private Permissions hasPermissions(String className) {
        return null;
    }
}