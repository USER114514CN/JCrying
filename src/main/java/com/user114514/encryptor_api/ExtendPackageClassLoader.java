package com.user114514.encryptor_api;

import java.util.ArrayList;
import java.util.List;

public class ExtendPackageClassLoader extends ClassLoader {

    public List<String> whitelistPerfixs = new ArrayList<>(List.of(
        "java.lang.", "java.util.", "com.user114514.encryptor_api"
    ));

    public List<String> blacklistPerfixs = new ArrayList<>(List.of(
        "com.user114514.encryptor.", "java.lang.ClassLoader", "java.security.SecureClassLoader", "java.net.URLClassLoader", "sun.misc.Launcher$ExtClassLoader", "sun.misc.Launcher$AppClassLoader", "jdk.internal.loader.PlatformClassLoader", "jdk.internal.loader.AppClassLoader", "org.springframework.boot.loader.LaunchedURLClassLoader"
    ));

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Permissions permissions = hasPermissions(name);
        if (permissions == Permissions.REJECTED) {
           return null;
        } else if (permissions == Permissions.INQUIRE) {
            System.out.printf("在加载类时发现包含可能不安全的 API 类或包内部类 (%s), 您希望程序允许访问此类吗?\n[Y] 是   [N] 否", name);
            String result = App.INPUT_SCANNER.nextLine();
            if (!result.isBlank() || Character.toLowerCase(result.charAt(0)) == 'y') return Class.forName(name);
            else return null;
        } else {
            return Class.forName(name);
        }
    }

    public static enum Permissions {
            ALLOWED, REJECTED, INQUIRE
    }

    private Permissions hasPermissions(String className) {
        for (String perfix : blacklistPerfixs) {
            if (className.startsWith(perfix)) return Permissions.REJECTED;
        }
        for (String perfix : whitelistPerfixs) {
            if (className.startsWith(perfix)) return Permissions.ALLOWED;
        }
        return Permissions.INQUIRE;
    }
    
}
