package com.user114514.encryptor.utils.encoders;

import java.nio.charset.StandardCharsets;

import com.user114514.encryptor.excep.IllegalDataException;
import com.user114514.encryptor.utils.GeneralEncoder;

public class HexEncoder extends GeneralEncoder {

    @Override
    public byte[] encode(byte[] data) {
        String hexStr = bytesToHex(data);
        return hexStr.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decode(byte[] data) throws IllegalDataException {
        try {
            String hexStr = new String(data, StandardCharsets.UTF_8);
            return hexToBytes(hexStr);
        } catch (NumberFormatException e) {
            throw new IllegalDataException("输入进编码器的数据并不是有效的十六进制字符串。", e);
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
}