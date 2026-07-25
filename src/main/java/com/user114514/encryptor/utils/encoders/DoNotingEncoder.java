package com.user114514.encryptor.utils.encoders;

import com.user114514.encryptor.excep.IllegalDataException;
import com.user114514.encryptor.utils.GeneralEncoder;

public class DoNotingEncoder extends GeneralEncoder {

    @Override
    public byte[] encode(byte[] data) {
        return data;
    }

    @Override
    public byte[] decode(byte[] data) throws IllegalDataException {
        return data;
    }
    
}
