package com.user114514.encryptor.utils.encoders;

import java.util.Arrays;
import com.user114514.encryptor.excep.IllegalDataException;
import com.user114514.encryptor.utils.GeneralEncoder;

public class AnyRadixEncoder extends GeneralEncoder {
    private int radix;
    private int singleByteMaxLength;
    private String assignment = "";
    private boolean isUpperCase;

    public AnyRadixEncoder(int radix) {
        this.radix = radix;
        this.singleByteMaxLength = Integer.toUnsignedString(255, radix).length();
        this.isUpperCase = false;
    }

    @Override
    public byte[] encode(byte[] data) {
        if (data == null || data.length == 0)
            return new byte[0];
        StringBuilder sb = new StringBuilder(64);
        for (byte element : data) {
            String byteStr = padLeft(Integer.toUnsignedString(element & 0xFF, radix), singleByteMaxLength, '0');
            if (isUpperCase)
                byteStr = byteStr.toUpperCase();
            sb.append(byteStr).append(assignment);
        }
        sb.delete(sb.length() - assignment.length(), sb.length());
        return sb.toString().getBytes();
    }

    public void setAssignment(String assignment) {
        if (assignment != null)
            this.assignment = assignment;
    }

    public void setUpperCase(boolean v) {
        this.isUpperCase = v;
    }

    @Override
    public byte[] decode(byte[] data) throws IllegalDataException {
        try {
            if (data == null || data.length == 0)
                return new byte[0];
            String text = new String(data);
            StringBuilder processor = new StringBuilder(text);
            while (processor.toString().contains(assignment)) {
                int sIndex = processor.toString().indexOf(assignment);
                processor.delete(sIndex, sIndex + assignment.length());
            }
            text = processor.toString();
            if (text.length() % singleByteMaxLength != 0) {
                text = padLeft(text, (int) Math.ceil(data.length / singleByteMaxLength) * singleByteMaxLength, '0');
            }

            byte[] arr = new byte[text.length() / singleByteMaxLength];
            int destIndex = 0;
            for (int i = 0; i <= text.length() - singleByteMaxLength; i += singleByteMaxLength) {
                String sub = text.substring(i, i + singleByteMaxLength);
                arr[destIndex] = (byte) Integer.parseUnsignedInt(sub, radix);
                destIndex++;
            }
            return arr;
        } catch (NumberFormatException ex) {
            throw new IllegalDataException("非法或损坏的数据：" + Arrays.toString(data), ex);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 右侧填充（尾部补字符）
     * 
     * @param str       原始字符串
     * @param targetLen 目标总长度N
     * @param padChar   填充字符c
     * @return 填充后字符串
     */
    public static String padRight(String str, int targetLen, char padChar) {
        // 空字符串兼容
        if (str == null)
            str = "";
        int len = str.length();
        // 长度超过目标，直接返回
        if (len >= targetLen) {
            return str;
        }
        // 需要填充的字符数量
        int needPad = targetLen - len;
        StringBuilder sb = new StringBuilder(targetLen);
        sb.append(str);
        // 循环补字符
        for (int i = 0; i < needPad; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    /**
     * 左侧填充（头部前面补字符）
     * 
     * @param str       原始字符串
     * @param targetLen 目标总长度N
     * @param padChar   填充字符c
     * @return 填充后字符串
     */
    public static String padLeft(String str, int targetLen, char padChar) {
        if (str == null)
            str = "";
        int len = str.length();
        if (len >= targetLen) {
            return str;
        }
        int needPad = targetLen - len;
        StringBuilder sb = new StringBuilder(targetLen);
        // 先填充字符
        for (int i = 0; i < needPad; i++) {
            sb.append(padChar);
        }
        // 再拼接原字符串
        sb.append(str);
        return sb.toString();
    }
}
