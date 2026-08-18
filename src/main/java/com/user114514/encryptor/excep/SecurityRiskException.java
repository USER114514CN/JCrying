package com.user114514.encryptor.excep;

public class SecurityRiskException extends RuntimeException {
    public SecurityRiskException() { super(); }
    public SecurityRiskException(String msg) { super(msg); }
    public SecurityRiskException(String msg, Throwable cause) { super(msg, cause); }
    public SecurityRiskException(Throwable cause) { super(cause); }
}
