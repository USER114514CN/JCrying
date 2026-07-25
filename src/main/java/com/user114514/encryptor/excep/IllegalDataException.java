package com.user114514.encryptor.excep;

public class IllegalDataException extends Exception {
    
    public IllegalDataException() {
        super();
    }

    public IllegalDataException(String message) {
        super(message);
    }

    public IllegalDataException(Throwable th) {
        super(th);
    }

    public IllegalDataException(String message, Throwable th) {
        super(message, th);
    }

}
