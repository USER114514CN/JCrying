package com.user114514.encryptor.excep;

public class DamagedExtractPackageException extends Exception {
    public DamagedExtractPackageException() { super(); }
    public DamagedExtractPackageException(String msg) { super(msg); }
    public DamagedExtractPackageException(String msg, Throwable cause) { super(msg, cause); }
    public DamagedExtractPackageException(Throwable cause) { super(cause); }
}
