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
import com.user114514.encryptor.utils.GeneralEncryptor;
import com.user114514.encryptor.utils.encoders.HexEncoder;
import com.user114514.encryptor.utils.encryptors.BlowfishEncryptor;
import com.user114514.encryptor.utils.encryptors.DesEdeEncryptor;
import com.user114514.encryptor.utils.encryptors.DesEncryptor;
import com.user114514.encryptor.utils.encryptors.Rc2Encryptor;
import com.user114514.encryptor.utils.encryptors.StandardAesGcmEncryptor;
import com.user114514.encryptor.utils.encryptors.XOREncryptor;

@Parameters(commandNames = "decrypt", commandDescription = "对一串数据进行解密。")
public class DecryptCommand {
    @Parameter(names = { "--decryptor", "-X" }, description = "选择指定的解密器进行解密。", arity = 1)
    public String encryptor = "xor";

    @DynamicParameter(names = { "--text",
            "-T" }, description = "向解密器输入的文本数据属性。(text指定内容，encoding指定解密)", assignment = "=")
    public Map<String, String> textData = new HashMap<>();

    @Parameter(names = { "--hex", "-r" }, description = "向解密器输入一串十六进制字符串。", arity = 1)
    public String hexString;

    @Parameter(names = { "--file", "-f" }, description = "将文件的原始二进制数据向解密器输入。", arity = 1)
    public String fromFile;

    @Parameter(names = { "--write-file", "-w" }, description = "将解密器输出的二进制数据写入至文件。(如文件已存在，则覆盖写入)", arity = 1)
    public String writeFile;

    @Parameter(names = { "--url", "-u" }, description = "从 URL 读取数据至解密器。", arity = 1)
    public URL url;

    @Parameter(names = { "--hex-output", "-A" }, description = "将解密器的输出以二进制输出在控制台。", arity = 0)
    public boolean hexOutput;

    @DynamicParameter(names = {"--key-text", "-kT"},
        description = "加密数据的密钥。(规则同--text)",
        assignment = "="
    )
    public Map<String, String> textKeyData = new HashMap<>();

     @Parameter(names = { "--key-hex", "-kH" }, description = "加密数据的密钥的十六进制数据。", arity = 1)
    public String hexKeyString;

    @Parameter(names = { "--key-file", "-kF" }, description = "存储的密钥的文件。", arity = 1)
    public String fromKeyFile;
    
    @Parameter(names = {"--info", "-I"}, description = "展示详细信息。", arity = 0)
    public boolean info;

    public DecryptCommand() {

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

    public byte[] getKeyData() throws Exception {
        if (textKeyData != null && !textKeyData.isEmpty()) {
            if (!textKeyData.containsKey("text"))
                throw new IllegalStateException("参数--text或-T缺少属性'text'。");
            String text = textKeyData.get("text");
            return text.getBytes(Charset.forName(textKeyData.getOrDefault("encoding", "UTF-8")));
        } else if (hexKeyString != null) {
            HexEncoder hexEncoder = new HexEncoder();
            return hexEncoder.decodeToBytesUTF8(hexKeyString);
        } else if (fromKeyFile != null) {
            File target = new File(fromKeyFile);
            if (!target.exists())
                throw new FileNotFoundException("找不到文件：" + target.getCanonicalPath());
            return Files.readAllBytes(target.toPath());
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

    public GeneralEncryptor getDecryptor() throws UnknownProcessorNameException {
        if (encryptor == null || encryptor.isBlank())
            throw new UnknownProcessorNameException("空的解密器名称。");
        switch (encryptor.toLowerCase()) {
            case "xor": return new XOREncryptor();
            case "aes":
            case "aes-gcm":
            case "std-aes":
            case "std-aes-gcm": return new StandardAesGcmEncryptor();
            case "des": return new DesEncryptor();
            case "desede": return new DesEdeEncryptor();
            case "blowfish": return new BlowfishEncryptor();
            case "rc2": return new Rc2Encryptor();
        }
        throw new UnknownProcessorNameException("未知或不支持的解密器：" + encryptor + ", 输入 --available-encryptor 查看可用的加密器。");
    }
}
