package com.user114514.encryptor.excep;

public class PackageNotInstalledException extends Exception {
    public PackageNotInstalledException() { super(); }
    public PackageNotInstalledException(String msg) { super(msg); }
    public PackageNotInstalledException(String msg, Throwable cause) { super(msg, cause); }
    public PackageNotInstalledException(Throwable cause) { super(cause); }
}
