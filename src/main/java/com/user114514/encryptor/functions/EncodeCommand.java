package com.user114514.encryptor.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.user114514.encryptor.excep.UnknownProcessorNameException;
import com.user114514.encryptor.utils.GeneralEncoder;
import com.user114514.encryptor.utils.encoders.AnyRadixEncoder;
import com.user114514.encryptor.utils.encoders.Base64Encoder;
import com.user114514.encryptor.utils.encoders.DoNotingEncoder;
import com.user114514.encryptor.utils.encoders.HexEncoder;
import com.user114514.encryptor.utils.encoders.MorseCodeEncoder;

@Parameters(commandNames = "encode", commandDescription = "对一串数据进行编码。")
public class EncodeCommand {
    @Parameter(names = { "--encoder", "-X" }, description = "选择指定的编码器进行编码。", arity = 1)
    public String encoder = "base64";

    @DynamicParameter(names = { "--text",
            "-T" }, description = "向编码器输入的文本数据属性。(text指定内容，encoding指定编码)", assignment = "=")
    public Map<String, String> textData = new HashMap<>();

    @Parameter(names = { "--hex", "-r" }, description = "向编码器输入一串十六进制字符串。", arity = 1)
    public String hexString;

    @Parameter(names = { "--file", "-f" }, description = "将文件的原始二进制数据向编码器输入。", arity = 1)
    public String fromFile;

    @Parameter(names = { "--url", "-u" }, description = "从 URL 读取数据至编码器。", arity = 1)
    public URL url;

    @Parameter(names = { "--write-file", "-w" }, description = "将编码器输出的二进制数据写入至文件。(如文件已存在，则覆盖写入)", arity = 1)
    public String writeFile;

    @Parameter(names = { "--hex-output", "-A" }, description = "将编码器的输出以二进制输出在控制台。", arity = 0)
    public boolean hexOutput;

    @Parameter(names = {"--info", "-I"}, description = "展示详细信息。", arity = 0)
    public boolean info;

    public EncodeCommand() {

    }

    public byte[] getData() throws Exception {
        if (textData != null && !textData.isEmpty()) {
            if (!textData.containsKey("text"))
                throw new IllegalStateException("参数--text或-T缺少属性'text'。");
            String text = textData.get("text");
            return text.getBytes(Charset.forName(textData.getOrDefault("encoding", "UTF-8")));
        } else if (hexString != null) {
            HexEncoder hexEncoder = new HexEncoder();
            return hexEncoder.decodeToBytesUTF8(hexString);
        } else if (fromFile != null) {
            File target = new File(fromFile);
            if (!target.exists())
                throw new FileNotFoundException("找不到文件：" + target.getCanonicalPath());
            return Files.readAllBytes(target.toPath());
        } else if (url != null) {
            return url.openStream().readAllBytes();
        }
        return new byte[0];
    }

    public void writeData(byte[] data) throws Exception {
        if (writeFile != null) {
            Files.write(Path.of(writeFile), data, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            return;
        } else if (hexOutput) {
            HexEncoder hexEncoder = new HexEncoder();
            System.out.println(hexEncoder.encodeToStringUTF8(data));
            return;
        }
        System.out.println(new String(data));
    }

    public GeneralEncoder getEncoder() throws UnknownProcessorNameException {
        if (encoder == null || encoder.isBlank())
            throw new UnknownProcessorNameException("空的编码器名称。");
        switch (encoder.toLowerCase()) {
            case "b64":
            case "std-base64":
            case "base64":
                return Base64Encoder.standard();
            case "url-base64":
                return Base64Encoder.url();
            case "mime-base64":
                return Base64Encoder.mime();
            case "hex":
            case "hexadecimal":
                return new HexEncoder();
            case "morse":
            case "mose":
            case "mos":
            case "morse-code":
                return new MorseCodeEncoder();
            case "emp":
            case "empty":
            case "do-noting":
            case "no":
                return new DoNotingEncoder();
        }
        if (encoder.matches("^(r|R)\\d+((a|A)\\[.*\\])?$")) {
            try {
                String radixStr = encoder.substring(1,
                        (encoder.toLowerCase().contains("a") ? encoder.toLowerCase().indexOf("a") : encoder.length()));
                int radixNum;
                try {
                    radixNum = Integer.parseInt(radixStr);
                } catch (NumberFormatException nfe) {
                    throw new UnknownProcessorNameException("无效的进制整数: " + radixStr, nfe);
                }
                if (radixNum > 36 || radixNum < 2)
                    throw new UnknownProcessorNameException("无效的进制整数, 必须在区间 [2, 36] 之间: " + radixNum);
                AnyRadixEncoder anyRadixEncoder = new AnyRadixEncoder(radixNum);
                if (encoder.matches("^(r|R)\\d+((a|A)\\[.*\\])$")) {
                    String assignment = encoder.substring(1 + radixStr.length() + 2, encoder.length() - 1);
                    anyRadixEncoder.setAssignment(assignment);
                }
                if (Character.isUpperCase(encoder.charAt(0))) anyRadixEncoder.setUpperCase(true);
                return anyRadixEncoder;
            } catch (Exception e) {
                throw e;
            }
        }
        throw new UnknownProcessorNameException("未知或不支持的编码器：" + encoder + ", 输入 --available-encoder 查看可用的编码器。");
    }
}
