package com.user114514.encryptor.excep;

public class UnknownProcessorNameException extends Exception {

    public UnknownProcessorNameException(String string) {
        super(string);
    }

    public UnknownProcessorNameException(String str, Throwable cause) {
        super(str, cause);
    }
    
}
