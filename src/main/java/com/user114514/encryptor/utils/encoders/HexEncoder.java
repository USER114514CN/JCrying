package com.user114514.encryptor.utils.encoders;

import java.nio.charset.StandardCharsets;

import com.user114514.encryptor.excep.DamagedDataException;
import com.user114514.encryptor.utils.GeneralEncoder;

public class HexEncoder extends GeneralEncoder {

    public HexEncoder() {
        super();
    }

    public HexEncoder(java.util.Map<String, String> options) {
        super(options);
    }

    @Override
    public byte[] encode(byte[] data) {
        String hexStr = bytesToHex(data);
        return hexStr.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decode(byte[] data) throws DamagedDataException {
        try {
            String hexStr = new String(data, StandardCharsets.UTF_8);
            return hexToBytes(hexStr);
        } catch (NumberFormatException e) {
            throw new DamagedDataException("输入进编码器的数据并不是有效的十六进制字符串。", e);
        }
    }

    public static byte[] hexToBytes(String hexStr) {
        if (hexStr == null || hexStr.isEmpty()) {
            return new byte[0];
        }
        String fixHex = hexStr;
        if (fixHex.length() % 2 != 0) {
            fixHex = "0" + fixHex;
        }
        int length = fixHex.length() / 2;
        byte[] res = new byte[length];
        for (int i = 0; i < length; i++) {
            String chunk = fixHex.substring(i * 2, i * 2 + 2);
            res[i] = (byte) Integer.parseInt(chunk, 16);
        }
        return res;
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String s = Integer.toHexString(Byte.toUnsignedInt(b));
            if (s.length() == 1) {
                sb.append('0');
            }
            sb.append(s);
        }
        return sb.toString();
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
            byte[] encoded = encode(java.util.Arrays.copyOf(buffer, bytesRead));
            os.write(encoded);
        }
    }

    @Override
    public void decodeStreaming(java.io.InputStream is, java.io.OutputStream os, int bufferSize) throws Exception {
        byte[] buffer = new byte[bufferSize];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            byte[] decoded = decode(java.util.Arrays.copyOf(buffer, bytesRead));
            os.write(decoded);
        }
    }
}