package com.user114514.encryptor.extend_pack;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.user114514.encryptor.ApplicationConfigs;
import com.user114514.encryptor.excep.SecurityRiskException;

public class ExtendPackageClassLoader extends URLClassLoader {

    public static final List<String> whitelistPerfixs = List.of(
        "java.lang.", "java.util.", "com.user114514.encryptor_api"
    );

    public static final List<String> blacklistPerfixs = List.of(
        "java.lang.Process", "java.lang.ProcessBuilder", "java.lang.ProcessHandler", "com.user114514.encryptor."
    );

    public ExtendPackageClassLoader(File jar) throws MalformedURLException {
        super(
            new URL[]{jar.toURI().toURL()},
            null
        );
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Permissions permissions = hasPermissions(name);
        if (permissions == Permissions.REJECTED) {
            throw new SecurityRiskException("在加载类时发现包含了危险的 API, 拒绝执行。");
        } else if (permissions == Permissions.INQUIRE) {
            System.out.printf("在加载类时发现包含不安全的 API 类(%s), 您希望程序允许访问此类吗?\n[Y] 是   [N] 否");
            String result = ApplicationConfigs.INPUT.nextLine();
            if (!result.isBlank() || Character.toLowerCase(result.charAt(0)) == 'y') return super.loadClass(name, false);
            else throw new SecurityRiskException("在加载类时发现包含了不安全的 API, 用户未授权访问。");
        } else {
            return super.loadClass(name, false);
        }
    }

    public static enum Permissions {
            ALLOWED, REJECTED, INQUIRE
    }

    private static Permissions hasPermissions(String className) {
        for (String perfix : blacklistPerfixs) {
            if (className.startsWith(perfix)) return Permissions.REJECTED;
        }
        for (String perfix : whitelistPerfixs) {
            if (className.startsWith(perfix)) return Permissions.ALLOWED;
        }
        return Permissions.INQUIRE;
    }
    
}
