package com.user114514.encryptor.extend_pack;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UTFDataFormatException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import com.user114514.encryptor.ApplicationConfigs;
import com.user114514.encryptor.excep.DamagedExtractPackageException;
import com.user114514.encryptor.excep.PackageNotInstalledException;
import com.user114514.encryptor.excep.SecurityRiskException;

public class ExtendPackageManager {
    public static final String illegalFileNameRegex = "(?i)^(con|nul|prn|aux|com[1-9]|lpt[1-9])(\\.[^.]+)?$|[\\/:*?\"<>|]";

    // 安装但不关闭流
    public static void installFromStream(InputStream is, boolean forEveryone) throws Exception {
        DataInputStream dis = new DataInputStream(is);
        try {
            String typeId = dis.readUTF();
            if (typeId.equals(ApplicationConfigs.PACKAGE_FILE_IDENTIFIER)) throw new DamagedExtractPackageException("此文件并不是扩展包类型。");
            String xmlManifest = dis.readUTF();
            PackageManifest manifest = PackageManifest.loadBy(xmlManifest);
    
            if (manifest.packId.matches(illegalFileNameRegex)) {
                throw new DamagedExtractPackageException("非法的包标识符: " + manifest.packId);
            }
    
            if (manifest.minRuntimeVersion > ApplicationConfigs.VER_CODE) throw new DamagedExtractPackageException("当前版本最低的环境要求是 " + manifest.minRuntimeVersion + ", 而当前版本为 " + ApplicationConfigs.VER_CODE + "(" + ApplicationConfigs.VER_NAME + ")");
    
            String algorithmType = manifest.subAttirbutes.getOrDefault("algorithmType", "encode");
            if (!ApplicationConfigs.ALGORITHM_TYPES.contains(algorithmType.toLowerCase())) throw new DamagedExtractPackageException("未知的算法类型: " + algorithmType);
            File installedPackDir = new File((forEveryone ? AppPathManager.pmgr.getGlobalDir() : AppPathManager.pmgr.getUserConfig()), ApplicationConfigs.PACKAGE_INSTALLED_PERFIX + typeFullyName(manifest.type) + "/" + algorithmType + "/");
            installedPackDir.mkdirs();
            File targetDir = new File(installedPackDir, manifest.packId);
            if (!targetDir.toPath().toRealPath().startsWith(installedPackDir.toPath().toRealPath())) throw new SecurityRiskException("此扩展包并不安全, 它绕过了文件名合法校验并正在进行路径穿越以修改安装目录之外的文件, 请务必重视此警告。");
            
            File coreJarFile = new File(targetDir, "core.jar");
            Files.copy(dis, coreJarFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            dis.close();
    
            File serializedManifestFile = new File(targetDir, "manifest.bin");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializedManifestFile));) {
                oos.writeObject(manifest);
            }
            appendToInstalledList(manifest.packId, forEveryone);
        } catch (StreamCorruptedException | EOFException | UTFDataFormatException e) {
            throw new DamagedExtractPackageException("此包并不是合法的扩展包, 可能已经损坏。", e);
        }
    }

    public static PackageManifest getManifest(InputStream is) throws Exception {
        DataInputStream dis = new DataInputStream(is);
        String typeId = dis.readUTF();
        if (typeId.equals(ApplicationConfigs.PACKAGE_FILE_IDENTIFIER)) throw new DamagedExtractPackageException("此文件并不是扩展包类型。");
        String xmlManifest = dis.readUTF();
        PackageManifest manifest = PackageManifest.loadBy(xmlManifest);

        return manifest;
    }

    public static PackageManifest getInstalledManifest(String packId, boolean forEveryone) throws Exception {
        if (!isInstalled(packId, forEveryone)) throw new PackageNotInstalledException("包 ID 为 " + packId + "未安装。");
        File packFile = findPack(packId, forEveryone);
        PackageManifest manifest;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(packFile, "manifest.bin")));) {
            manifest = (PackageManifest) ois.readObject();
        }
        return manifest;
    }

    // 返回是否存在
    public static void uninstallPack(String packId, boolean forEveryone) throws Exception {
        if (!isInstalled(packId, forEveryone)) throw new PackageNotInstalledException("包 ID 为 " + packId + "未安装。");
        deleteDir(findPack(packId, forEveryone));
    }

    public static File findPack(String packId, boolean forEveryone) {
        File installedDir = new File((forEveryone ? AppPathManager.pmgr.getGlobalDir() : AppPathManager.pmgr.getUserConfig()), ApplicationConfigs.PACKAGE_INSTALLED_PERFIX);
        for (File typeDir : installedDir.listFiles()) {
            if (typeDir.isFile()) continue;
            for (File algorithmType : typeDir.listFiles()) {
                if (algorithmType.isFile()) continue;
                for (File packDir : algorithmType.listFiles()) {
                    if (packDir.getName().equals(packId)) {
                        return packDir;
                    }
                }
            }
        }
        return null;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isFile()) {
            return dir.delete();
        }
        File[] childs = dir.listFiles();
        if (childs.length == 0) return dir.delete();
        
        for (File f : childs) {
            if (!deleteDir(f)) return false;
        }
        return dir.delete();
    }

    public static String typeFullyName(String str) {
        return switch (str.toLowerCase()) {
            case "alp" -> "algorithm-pack";
            case "cmp" -> "command-pack";
            case "fcp" -> "functional-pack";
            default -> null;
        };
    }

    public static void appendToInstalledList(String packId, boolean forEveryone) throws Exception {
        File installedPackListFile = new File((forEveryone ? AppPathManager.pmgr.getGlobalDir() : AppPathManager.pmgr.getUserConfig()), ApplicationConfigs.INSTALLED_PACKAGES_LIST_FILE);
        Files.writeString(installedPackListFile.toPath(), packId + "\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static boolean isInstalled(String packId, boolean forEveryone) throws Exception {
        File installedPackListFile = new File((forEveryone ? AppPathManager.pmgr.getGlobalDir() : AppPathManager.pmgr.getUserConfig()), ApplicationConfigs.INSTALLED_PACKAGES_LIST_FILE);
        return readOrEmpty(installedPackListFile.toPath()).contains(packId + "\n");
    }

    public static void removeFromInstalledList(String packId, boolean forEveryone) throws Exception {
        File installedPackListFile = new File((forEveryone ? AppPathManager.pmgr.getGlobalDir() : AppPathManager.pmgr.getUserConfig()), ApplicationConfigs.INSTALLED_PACKAGES_LIST_FILE);
        String content = readOrEmpty(installedPackListFile.toPath()).replace(packId + "\n", "");
        Files.writeString(installedPackListFile.toPath(), content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    public static String getAllInstalledList(boolean forEveryone) throws Exception {
        File installedPackListFile = new File((forEveryone ? AppPathManager.pmgr.getGlobalDir() : AppPathManager.pmgr.getUserConfig()), ApplicationConfigs.INSTALLED_PACKAGES_LIST_FILE);
        return readOrEmpty(installedPackListFile.toPath());
    }

    public static String readOrEmpty(Path p) throws Exception {
        try {
            return Files.readString(p, StandardCharsets.UTF_8);
        } catch (NoSuchFileException noSuchFileException) {
            return "";
        }
    }

}
