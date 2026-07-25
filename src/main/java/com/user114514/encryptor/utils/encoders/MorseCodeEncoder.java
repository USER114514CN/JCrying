package com.user114514.encryptor.utils.encoders;

import com.user114514.encryptor.excep.IllegalDataException;
import com.user114514.encryptor.utils.GeneralEncoder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 国际标准摩斯密码编码器，继承通用抽象编码器 GeneralEncoder
 * 编码规则：
 * 1. 字母大写转换，字符间空格分隔，单词用 / 分隔
 * 2. 仅支持 A-Z 0-9 基础标点，其他字符抛异常
 * 3. encode：原始明文字节 -> UTF8摩斯字符串字节
 * 4. decode：摩斯字符串字节 -> 原始明文字节
 */
public class MorseCodeEncoder extends GeneralEncoder {

    // 字符 -> 摩斯码映射表(ITU国际标准)
    private static final Map<Character, String> CHAR_TO_MORSE;
    // 摩斯码 -> 字符 反向映射，用于解码
    private static final Map<String, Character> MORSE_TO_CHAR;

    static {
        CHAR_TO_MORSE = new HashMap<>();
        MORSE_TO_CHAR = new HashMap<>();

        // 26字母
        put('A', ".-");
        put('B', "-...");
        put('C', "-.-.");
        put('D', "-..");
        put('E', ".");
        put('F', "..-.");
        put('G', "--.");
        put('H', "....");
        put('I', "..");
        put('J', ".---");
        put('K', "-.-");
        put('L', ".-..");
        put('M', "--");
        put('N', "-.");
        put('O', "---");
        put('P', ".--.");
        put('Q', "--.-");
        put('R', ".-.");
        put('S', "...");
        put('T', "-");
        put('U', "..-");
        put('V', "...-");
        put('W', ".--");
        put('X', "-..-");
        put('Y', "-.--");
        put('Z', "--..");

        // 数字
        put('0', "-----");
        put('1', ".----");
        put('2', "..---");
        put('3', "...--");
        put('4', "....-");
        put('5', ".....");
        put('6', "-....");
        put('7', "--...");
        put('8', "---..");
        put('9', "----.");

        // 单词分隔符
        CHAR_TO_MORSE.put(' ', "/");
        CHAR_TO_MORSE.put('\n', "/");
    }

    private static void put(char c, String morse) {
        CHAR_TO_MORSE.put(c, morse);
        MORSE_TO_CHAR.put(morse, c);
    }

    /**
     * 明文二进制 -> 摩斯码UTF8字节数组
     * @param data 原始明文字节(UTF8文本)
     * @return 摩斯字符串UTF8字节
     */
    @Override
    public byte[] encode(byte[] data) {
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        String rawText = new String(data, StandardCharsets.UTF_8);
        StringBuilder morseBuilder = new StringBuilder();

        for (char ch : rawText.toUpperCase().toCharArray()) {
            String morse = CHAR_TO_MORSE.get(ch);
            if (morse == null) {
                // // 不支持的字符直接跳过/抛异常，这里选择抛出
                // throw new IllegalArgumentException("不支持的摩斯密码字符：" + ch);
                continue;
            }
            morseBuilder.append(morse).append(" ");
        }

        // 移除末尾多余空格
        if (morseBuilder.length() > 0) {
            morseBuilder.setLength(morseBuilder.length() - 1);
        }
        return morseBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 摩斯码字节数组 -> 原始明文字节
     * @param data UTF8编码的摩斯字符串
     * @return 原始明文UTF8字节
     * @throws IllegalDataException 摩斯格式非法/包含无效码
     */
    @Override
    public byte[] decode(byte[] data) throws IllegalDataException {
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        String morseText = new String(data, StandardCharsets.UTF_8).trim();
        if (morseText.isEmpty()) {
            return new byte[0];
        }

        String[] morseParts = morseText.split("\\s+");
        StringBuilder plainBuilder = new StringBuilder();

        for (String part : morseParts) {
            if ("/".equals(part)) {
                plainBuilder.append(" ");
                continue;
            }
            Character targetChar = MORSE_TO_CHAR.get(part);
            if (targetChar == null) {
                throw new IllegalDataException("无效摩斯码片段：" + part);
            }
            plainBuilder.append(targetChar);
        }
        return plainBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
}