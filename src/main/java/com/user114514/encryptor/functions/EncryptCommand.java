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
import com.user114514.encryptor.utils.GeneralEncryptor;
import com.user114514.encryptor.utils.encoders.HexEncoder;
import com.user114514.encryptor.utils.encryptors.BlowfishEncryptor;
import com.user114514.encryptor.utils.encryptors.DesEdeEncryptor;
import com.user114514.encryptor.utils.encryptors.DesEncryptor;
import com.user114514.encryptor.utils.encryptors.Rc2Encryptor;
import com.user114514.encryptor.utils.encryptors.StandardAesGcmEncryptor;
import com.user114514.encryptor.utils.encryptors.XOREncryptor;
import com.user114514.encryptor_api.ExtendEncryptor;

@Parameters(commandNames = "encrypt", commandDescription = "对一串数据进行加密。")
public class EncryptCommand {
    @Parameter(names = { "--encryptor", "-X" }, description = "选择指定的加密器进行加密。", arity = 1)
    public String encryptor = "xor";

    @DynamicParameter(names = { "--text",
            "-T" }, description = "向加密器输入的文本数据属性。(text指定内容，encoding指定加密)", assignment = "=")
    public Map<String, String> textData = new HashMap<>();

    @Parameter(names = { "--hex", "-r" }, description = "向加密器输入一串十六进制字符串。", arity = 1)
    public String hexString;

    @Parameter(names = { "--file", "-f" }, description = "将文件的原始二进制数据向加密器输入。", arity = 1)
    public String fromFile;

    @Parameter(names = { "--url", "-u" }, description = "从 URL 读取数据至加密器。", arity = 1)
    public URL url;

    @Parameter(names = { "--write-file", "-w" }, description = "将加密器输出的二进制数据写入至文件。(如文件已存在，则覆盖写入)", arity = 1)
    public String writeFile;

    @Parameter(names = { "--hex-output", "-A" }, description = "将加密器的输出以二进制输出在控制台。", arity = 0)
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

    @Parameter(names = {"--streaming", "-S"}, description = "启用流式加密模式。", arity = 0)
    public boolean streaming;

    @Parameter(names = {"--streaming-buffer-size", "--buffer-size", "-B"}, description = "流式加密模式下的缓冲区大小。", arity = 1)
    public String streamingBufferSize = "4MB";

    @DynamicParameter(names = {"-options", "-O"}, descriptionKey = "加密器的选项。")
    public Map<String, String> options = new HashMap<>();

    public EncryptCommand() {

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

    public GeneralEncryptor getEncryptor() throws Exception {
        if (encryptor == null || encryptor.isBlank())
            throw new UnknownProcessorNameException("空的加密器名称。");
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
        File userPackDir = new File(AppPathManager.pmgr.getUserConfig(),
                ApplicationConfigs.PACKAGE_INSTALLED_PERFIX + "algorithm-pack/encrypt/" + encryptor);
        File globalPackDir = new File(AppPathManager.pmgr.getGlobalDir(), 
                ApplicationConfigs.PACKAGE_INSTALLED_PERFIX + "algorithm-pack/encrypt/" + encryptor);
        if (userPackDir.exists()) {
            return loadEncryptor(userPackDir);
        } else if (globalPackDir.exists()) {
            return loadEncryptor(globalPackDir);
        }
        throw new UnknownProcessorNameException("未知或不支持的加密器：" + encryptor + ", 输入 --available-encryptor 查看可用的加密器。");
    }

    public GeneralEncryptor loadEncryptor(File packDir) throws Exception {
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
            boolean implementedTargetInterface = ExtendEncryptor.class.isAssignableFrom(entranceClass);
            if (!implementedTargetInterface)
                throw new DamagedExtractPackageException("包的入口类未实现 ExtendEncryptor 接口。");
            ExtendEncryptor encryptor = null;
    
            Constructor<?>[] constructors = entranceClass.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    encryptor = (ExtendEncryptor) constructor.newInstance();
                    encryptor.setOptions(options);
                    break;
                }
                if (constructor.getParameterCount() == 1
                        && constructor.getParameterTypes()[0].getName().equals(Map.class.getName())) {
                    encryptor = (ExtendEncryptor) constructor.newInstance(options);
                }
            }
    
            if (encryptor == null)
                throw new DamagedExtractPackageException("此扩展包的入口类没有合法的构造器。");
            return encryptor;
        }
    }
}
