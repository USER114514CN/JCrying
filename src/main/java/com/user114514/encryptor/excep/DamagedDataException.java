package com.user114514.encryptor.excep;

public class DamagedDataException extends Exception {
    
    public DamagedDataException() {
        super();
    }

    public DamagedDataException(String message) {
        super(message);
    }

    public DamagedDataException(Throwable th) {
        super(th);
    }

    public DamagedDataException(String message, Throwable th) {
        super(message, th);
    }

}
