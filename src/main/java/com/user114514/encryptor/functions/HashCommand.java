package com.user114514.encryptor.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.lang.reflect.Constructor;
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
import com.user114514.encryptor_api.ExtendHashAlgorithm;
import com.user114514.encryptor.utils.GeneralHashAlgorithm;
import com.user114514.encryptor.utils.encoders.HexEncoder;
import com.user114514.encryptor.utils.hashalgorithms.HMACSHA256HashAlgorithm;
import com.user114514.encryptor.utils.hashalgorithms.SHA256HashAlgorithm;

@Parameters(commandNames = {"hash"}, commandDescription = "对一串数据进行哈希。")
public class HashCommand {
    @Parameter(
        names = {"--hash-algorithm", "-X"},
        description = "哈希算法名称。",
        required = true
    )
    public String hashAlgorithm;

    @DynamicParameter(names = { "--text",
            "-T" }, description = "向哈希处理器输入的文本数据属性。(text指定内容，encoding指定编码)", assignment = "=")
    public Map<String, String> textData = new HashMap<>();

    @Parameter(names = { "--hex", "-r" }, description = "向哈希处理器输入一串十六进制字符串。", arity = 1)
    public String hexString;

    @Parameter(names = { "--file", "-f" }, description = "将文件的原始二进制数据向哈希处理器输入。", arity = 1)
    public String fromFile;

    @Parameter(names = { "--url", "-u" }, description = "从 URL 读取数据至哈希处理器。", arity = 1)
    public URL url;

    @Parameter(names = { "--write-file", "-w" }, description = "将哈希处理器输出的二进制数据写入至文件。(如文件已存在，则覆盖写入)", arity = 1)
    public String writeFile;

    @Parameter(names = { "--hex-output", "-A" }, description = "将哈希处理器的输出以二进制输出在控制台。", arity = 0)
    public boolean hexOutput;

    @Parameter(names = {"--info", "-I"}, description = "展示详细信息。", arity = 0)
    public boolean info;

    @DynamicParameter(names = {"--options", "-O"},
        description = "哈希处理器的选项参数。",
        assignment = "="
    )
    public Map<String, String> options = new HashMap<>();

    @Parameter(names = {"--streaming", "-S"}, description = "启用流式哈希处理模式。", arity = 0)
    public boolean streaming;

    @Parameter(names = {"--streaming-buffer-size", "--buffer-size", "-B"}, description = "流式哈希处理模式下的缓冲区大小。", arity = 1)
    public String streamingBufferSize = "4MB";

    @DynamicParameter(
        names = {"--salt-text", "-sT"},
        description = "哈希处理器的盐值的文本属性。",
        assignment = "="
    )
    public Map<String, String> saltTextData = new HashMap<>();

    @Parameter(
        names = {"--salt-hex", "-sH"},
        description = "哈希处理器的盐值的十六进制字符串。",
        arity = 1
    )
    public String saltHexString;

    @Parameter(
        names = {"--salt-file", "-sF"},
        description = "哈希处理器的盐值的文件路径。",
        arity = 1
    )
    public String saltFromFile;

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

    public byte[] getSaltData() throws Exception {
        if (saltTextData != null && !saltTextData.isEmpty()) {
            if (!saltTextData.containsKey("text"))
                throw new IllegalStateException("参数--salt-text或-sT缺少属性'text'。");
            String text = saltTextData.get("text");
            return text.getBytes(Charset.forName(saltTextData.getOrDefault("encoding", "UTF-8")));
        } else if (saltHexString != null) {
            HexEncoder hexEncoder = new HexEncoder();
            return hexEncoder.decodeToBytesUTF8(saltHexString);
        } else if (saltFromFile != null) {
            File target = new File(saltFromFile);
            if (!target.exists())
                throw new FileNotFoundException("找不到文件：" + target.getCanonicalPath());
            return Files.readAllBytes(target.toPath());
        }
        return new byte[0];
    }

    public GeneralHashAlgorithm getHashAlgorithm() throws Exception {
        switch (hashAlgorithm.toLowerCase()) {
            case "hmac-sha-256":
            case "sha-256":
                return new HMACSHA256HashAlgorithm(options);
            case "sha-256-simplesalt":
                return new SHA256HashAlgorithm(options);
        }

        File userPackDir = new File(AppPathManager.pmgr.getUserConfig(),
                ApplicationConfigs.PACKAGE_INSTALLED_PERFIX + "algorithm-pack/hash/" + hashAlgorithm);
        File globalPackDir = new File(AppPathManager.pmgr.getGlobalDir(),
                ApplicationConfigs.PACKAGE_INSTALLED_PERFIX + "algorithm-pack/hash/" + hashAlgorithm);
        if (userPackDir.exists()) {
            return loadHashAlgorithm(userPackDir);
        } else if (globalPackDir.exists()) {
            return loadHashAlgorithm(globalPackDir);
        }
        throw new UnknownProcessorNameException("未知或不支持的哈希函数：" + hashAlgorithm);
    }

    public GeneralHashAlgorithm loadHashAlgorithm(File packDir) throws Exception {
        File manifestFile = new File(packDir, "manifest.bin");
        if (!manifestFile.exists()) {
            throw new DamagedExtractPackageException("此扩展包已经正确安装, 但安装完成后安装目录结构可能已经损坏。");
        }

        PackageManifest manifest = ExtendPackageManager.getBinaryManifest(new FileInputStream(manifestFile));
        if (!"hash".equalsIgnoreCase(manifest.subAttirbutes.get("algorithmType"))) {
            throw new IllegalManifestException("此扩展包不是哈希算法包。");
        }
        if (!manifest.subAttirbutes.containsKey("entrance")) {
            throw new IllegalManifestException("此扩展包没有定义入口类。");
        }

        String entrance = manifest.subAttirbutes.get("entrance");
        File coreJarFile = new File(packDir, "core.jar");
        if (!coreJarFile.exists()) {
            throw new DamagedExtractPackageException("无法找到核心Jar文件, 安装目录结构可能已经损坏。");
        }

        try (ExtendPackageClassLoader loader = new ExtendPackageClassLoader(coreJarFile)) {
            loader.whitelistPerfixs.add(entrance);
            Class<?> entranceClass = loader.loadClass(entrance);
            if (!ExtendHashAlgorithm.class.isAssignableFrom(entranceClass)) {
                throw new DamagedExtractPackageException("包的入口类未实现 ExtendHashAlgorithm 接口。");
            }

            ExtendHashAlgorithm hashAlgorithmInstance = null;
            Constructor<?>[] constructors = entranceClass.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    hashAlgorithmInstance = (ExtendHashAlgorithm) constructor.newInstance();
                    hashAlgorithmInstance.setOptions(options);
                    break;
                }
                if (constructor.getParameterCount() == 1
                        && constructor.getParameterTypes()[0].getName().equals(Map.class.getName())) {
                    hashAlgorithmInstance = (ExtendHashAlgorithm) constructor.newInstance(options);
                }
            }

            if (hashAlgorithmInstance == null) {
                throw new DamagedExtractPackageException("此扩展包的入口类没有合法的构造器。");
            }
            return hashAlgorithmInstance;
        }
    }

}
