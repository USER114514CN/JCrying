package com.user114514.encryptor.utils.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageSizeParser {
    // 1024 二进制进制映射
    private static final Map<String, Long> UNIT_MULTIPLIER;
    static {
        UNIT_MULTIPLIER = new HashMap<>();
        // B / byte
        UNIT_MULTIPLIER.put("b", 1L);
        UNIT_MULTIPLIER.put("byte", 1L);
        // K / KB / kbyte
        UNIT_MULTIPLIER.put("k", 1024L);
        UNIT_MULTIPLIER.put("kb", 1024L);
        UNIT_MULTIPLIER.put("kbyte", 1024L);
        // M / MB / mbyte
        UNIT_MULTIPLIER.put("m", 1024L * 1024);
        UNIT_MULTIPLIER.put("mb", 1024L * 1024);
        UNIT_MULTIPLIER.put("mbyte", 1024L * 1024);
        // G / GB / gbyte
        UNIT_MULTIPLIER.put("g", 1024L * 1024 * 1024);
        UNIT_MULTIPLIER.put("gb", 1024L * 1024 * 1024);
        UNIT_MULTIPLIER.put("gbyte", 1024L * 1024 * 1024);
        // T / TB / tbyte
        UNIT_MULTIPLIER.put("t", 1024L * 1024 * 1024 * 1024);
        UNIT_MULTIPLIER.put("tb", 1024L * 1024 * 1024 * 1024);
        UNIT_MULTIPLIER.put("tbyte", 1024L * 1024 * 1024 * 1024);
    }

    // 正则：匹配数字(整数/小数) + 可选空格 + 单位字母
    private static final Pattern SIZE_REGEX = Pattern.compile(
            "^\\s*([0-9]+(\\.[0-9]+)?)\\s*([a-zA-Z]*)\\s*$"
    );

    /**
     * 解析存储字符串为总字节数（1024进制）
     * 支持复数后缀s：bytes kbytes mbytes gbytes tbytes
     * @param sizeStr 输入例如 "8kbytes", "2.5MByte", "1024"
     * @return 总字节 long
     */
    public static long parseToBytes(String sizeStr) {
        if (sizeStr == null || sizeStr.isBlank()) {
            throw new IllegalArgumentException("输入不能为空");
        }

        Matcher matcher = SIZE_REGEX.matcher(sizeStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("格式非法：" + sizeStr + "，示例：1B、5bytes、2.8MB、128");
        }

        // 提取数值
        String numText = matcher.group(1);
        BigDecimal num = new BigDecimal(numText);

        // 提取单位，小写，并统一去除末尾s（兼容所有复数）
        String rawUnit = matcher.group(3).toLowerCase();
        String realUnit;
        if (rawUnit.endsWith("s")) {
            realUnit = rawUnit.substring(0, rawUnit.length() - 1);
        } else {
            realUnit = rawUnit;
        }

        // 无单位默认 byte
        long multi = 1L;
        if (!realUnit.isBlank()) {
            if (!UNIT_MULTIPLIER.containsKey(realUnit)) {
                throw new IllegalArgumentException("不支持单位：" + matcher.group(3));
            }
            multi = UNIT_MULTIPLIER.get(realUnit);
        }

        // 计算总字节，四舍五入取整
        BigDecimal totalBytes = num.multiply(new BigDecimal(multi));
        if (totalBytes.compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("数值过大，超出long范围：" + sizeStr);
        }
        return totalBytes.setScale(0, RoundingMode.HALF_UP).longValue();
    }

    /**
     * 转为int字节（不推荐，极易溢出）
     */
    public static int parseToIntBytes(String sizeStr) {
        long bytes = parseToBytes(sizeStr);
        if (bytes > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("字节超出int上限，使用long：" + bytes);
        }
        return (int) bytes;
    }
}
