package com.user114514.encryptor.functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
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
import com.user114514.encryptor.ApplicationConfigs;
import com.user114514.encryptor.excep.DamagedExtractPackageException;
import com.user114514.encryptor.excep.IllegalManifestException;
import com.user114514.encryptor.excep.UnknownProcessorNameException;
import com.user114514.encryptor.extend_pack.AppPathManager;
import com.user114514.encryptor.extend_pack.ExtendPackageClassLoader;
import com.user114514.encryptor.extend_pack.ExtendPackageManager;
import com.user114514.encryptor.extend_pack.PackageManifest;
import com.user114514.encryptor.utils.GeneralEncoder;
import com.user114514.encryptor.utils.encoders.AnyRadixEncoder;
import com.user114514.encryptor.utils.encoders.Base64Encoder;
import com.user114514.encryptor.utils.encoders.DoNotingEncoder;
import com.user114514.encryptor.utils.encoders.HexEncoder;
import com.user114514.encryptor.utils.encoders.MorseCodeEncoder;
import com.user114514.encryptor_api.ExtendEncoder;

@Parameters(commandNames = "decode", commandDescription = "对一串数据进行解码。")
public class DecodeCommand {
    @Parameter(names = { "--decoder", "-X" }, description = "选择指定的解码器进行解码。", arity = 1)
    public String encoder = "base64";

    @DynamicParameter(names = { "--text",
            "-T" }, description = "向解码器输入的文本数据属性。(text指定内容，encoding指定解码)", assignment = "=")
    public Map<String, String> textData = new HashMap<>();

    @Parameter(names = { "--file", "-f" }, description = "将文件的原始二进制数据向解码器输入。", arity = 1)
    public String fromFile;

    @Parameter(names = { "--url", "-u" }, description = "从 URL 读取数据至解码器。", arity = 1)
    public URL url;

    @Parameter(names = { "--write-file", "-w" }, description = "将解码器输出的二进制数据写入至文件。(如文件已存在，则覆盖写入)", arity = 1)
    public String writeFile;

    @Parameter(names = { "--hex-output", "-A" }, description = "将解码器的输出以二进制输出在控制台。", arity = 0)
    public boolean hexOutput;

    @Parameter(names = {"--info", "-I"}, description = "展示详细信息。", arity = 0)
    public boolean info;

    @DynamicParameter(names = {"--options", "-O"},
        description = "解码器的选项参数。",
        assignment = "="
    )
    public Map<String, String> options = new HashMap<>();

    @Parameter(names = {"--streaming", "-S"}, description = "启用流式编码模式。", arity = 0)
    public boolean streaming;

    @Parameter(names = {"--streaming-buffer-size", "--buffer-size", "-B"}, description = "流式编码模式下的缓冲区大小。", arity = 1)
    public String streamingBufferSize = "4MB";

    public byte[] getData() throws Exception {
        if (textData != null && !textData.isEmpty()) {
            if (!textData.containsKey("text"))
                throw new IllegalStateException("参数--text或-T缺少属性'text'。");
            String text = textData.get("text");
            return text.getBytes(Charset.forName(textData.getOrDefault("encoding", "UTF-8")));
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

    public GeneralEncoder getDecoder() throws Exception {
        if (encoder == null || encoder.isBlank())
            throw new UnknownProcessorNameException("空的解码器名称。");
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
                return new MorseCodeEncoder(options);
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
                    anyRadixEncoder.setSeparator(assignment);
                }
                return anyRadixEncoder;
            } catch (Exception e) {
                throw e;
            }
        }
        File userPackDir = new File(AppPathManager.pmgr.getUserConfig(),
                ApplicationConfigs.PACKAGE_INSTALLED_PERFIX + "algorithm-pack/encode/" + encoder);
        File globalPackDir = new File(AppPathManager.pmgr.getGlobalDir(), 
                ApplicationConfigs.PACKAGE_INSTALLED_PERFIX + "algorithm-pack/encode/" + encoder);
        if (userPackDir.exists()) {
            return loadDecoder(userPackDir);
        } else if (globalPackDir.exists()) {
            return loadDecoder(globalPackDir);
        }
        throw new UnknownProcessorNameException("未知或不支持的解码器：" + encoder + ", 输入 --available-encoder 查看可用的解码器。");
    }

    public GeneralEncoder loadDecoder(File packDir) throws Exception {
        File manifestFile = new File(packDir, "manifest.bin");
        if (!manifestFile.exists())
            throw new DamagedExtractPackageException("此扩展包已经正确安装, 但安装完成后安装目录结构可能已经损坏。");
        PackageManifest manifest = ExtendPackageManager.getBinaryManifest(new FileInputStream(manifestFile));
        if (!manifest.subAttirbutes.containsKey("entrance"))
            throw new IllegalManifestException("此扩展包没有定义入口类。");
        String entrance = manifest.subAttirbutes.get("entrance");
        File coreJarFile = new File(packDir, "core.jar");
        if (!coreJarFile.exists())
            throw new DamagedExtractPackageException("无法找到核心Jar文件, 安装目录结构可能已经损坏。");
        
        try (ExtendPackageClassLoader loader = new ExtendPackageClassLoader(coreJarFile);) {
            loader.whitelistPerfixs.add(entrance);
            Class<?> entranceClass = loader.loadClass(entrance);
            boolean implementedTargetInterface = ExtendEncoder.class.isAssignableFrom(entranceClass);
            if (!implementedTargetInterface)
                throw new DamagedExtractPackageException("包的入口类未实现 ExtendEncoder 接口。");
            ExtendEncoder encoder = null;
    
            Constructor<?>[] constructors = entranceClass.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    encoder = (ExtendEncoder) constructor.newInstance();
                    encoder.setOptions(options);
                    break;
                }
                if (constructor.getParameterCount() == 1
                        && constructor.getParameterTypes()[0].getName().equals(Map.class.getName())) {
                    encoder = (ExtendEncoder) constructor.newInstance(options);
                }
            }
    
            if (encoder == null)
                throw new DamagedExtractPackageException("此扩展包的入口类没有合法的构造器。");
            return encoder;
        }
    }
}
