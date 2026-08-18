package com.user114514.encryptor.functions;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import com.user114514.encryptor.ApplicationConfigs;
import com.user114514.encryptor.extend_pack.ExtendPackageManager;
import com.user114514.encryptor.extend_pack.PackageManifest;

@Parameters(commandNames = "buildpack", commandDescription = "打包构建一个 JCrying 扩展包。")

public class PackdevCommand {

    @Parameter(names = {"--manifest", "-m"}, description = "清单文件的位置。", arity = 1, converter = FileConverter.class)
    public File manifestFile;

    @Parameter(names = {"--core", "-c"}, description = "核心 Jar 文件的位置。", arity = 1, converter = FileConverter.class)
    public File coreJarFile;

    @Parameter(names = {"--output", "-o"}, description = "最终的扩展包文件输出位置。(虽然并不强制, 但推荐以 .jcp 结尾)", arity = 1, converter = FileConverter.class)
    public File outputFile;

    public void building() throws Exception {
        if (manifestFile == null || !manifestFile.isFile()) {
            throw new IllegalStateException("清单文件不存在或未指定: " + manifestFile);
        }
        if (coreJarFile == null || !coreJarFile.isFile()) {
            throw new IllegalStateException("核心 Jar 文件不存在或未指定: " + coreJarFile);
        }
        if (outputFile == null) {
            throw new IllegalStateException("输出文件未指定。");
        }

        String xmlManifest = Files.readString(manifestFile.toPath(), StandardCharsets.UTF_8);
        PackageManifest manifest = PackageManifest.loadBy(xmlManifest);
        if (manifest.packId == null || manifest.packId.isBlank()) {
            throw new IllegalStateException("清单文件中的包 ID 为空。");
        }
        if (manifest.packId.matches(ExtendPackageManager.illegalFileNameRegex)) {
            throw new IllegalStateException("非法的包 ID: " + manifest.packId);
        }

        Path outputPath = outputFile.toPath();
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputFile));
             FileInputStream coreIn = new FileInputStream(coreJarFile)) {
            dos.writeUTF(ApplicationConfigs.PACKAGE_FILE_IDENTIFIER);
            dos.writeUTF(xmlManifest);

            byte[] buffer = new byte[8192];
            int read;
            while ((read = coreIn.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
            }
            dos.flush();
        }

        System.out.println("扩展包构建成功: " + outputFile.getAbsolutePath());
    }
}
