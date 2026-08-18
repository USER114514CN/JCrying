package com.user114514.encryptor.utils.encoders;

import com.user114514.encryptor.excep.DamagedDataException;
import com.user114514.encryptor.utils.GeneralEncoder;

public class DoNotingEncoder extends GeneralEncoder {

    @Override
    public byte[] encode(byte[] data) {
        return data;
    }

    @Override
    public byte[] decode(byte[] data) throws DamagedDataException {
        return data;
    }

    @Override
    public boolean supportedStreaming() {
        return true;
    }

    @Override
    public void encodeStreaming(java.io.InputStream is, java.io.OutputStream os, int bufferSize) throws Exception {
        byte[] buffer = new byte[bufferSize];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
    }

    @Override
    public void decodeStreaming(java.io.InputStream is, java.io.OutputStream os, int bufferSize) throws Exception {
        byte[] buffer = new byte[bufferSize];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
    }
    
}
