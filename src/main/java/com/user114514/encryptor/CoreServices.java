package com.user114514.encryptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xml.sax.SAXException;

import com.beust.jcommander.JCommander;
import com.user114514.encryptor.excep.DamagedDataException;
import com.user114514.encryptor.excep.DamagedExtractPackageException;
import com.user114514.encryptor.excep.IllegalManifestException;
import com.user114514.encryptor.excep.PackageNotInstalledException;
import com.user114514.encryptor.excep.SecurityRiskException;
import com.user114514.encryptor.excep.UnknownProcessorNameException;
import com.user114514.encryptor.extend_pack.ExtendPackageManager;
import com.user114514.encryptor.functions.DecodeCommand;
import com.user114514.encryptor.functions.DecryptCommand;
import com.user114514.encryptor.functions.EncodeCommand;
import com.user114514.encryptor.functions.EncryptCommand;
import com.user114514.encryptor.functions.HashCommand;
import com.user114514.encryptor.functions.PackdevCommand;
import com.user114514.encryptor.functions.PackmgrCommand;
import com.user114514.encryptor.utils.encoders.HexEncoder;
import com.user114514.encryptor.utils.utils.StorageSizeParser;

public class CoreServices {
    public static int executeEncodeCommand(JCommander parsedCommander, EncodeCommand commandInstance) {
        try {
            if (commandInstance.streaming) {
                if (!commandInstance.getEncoder().supportedStreaming()) {
                    throw new IllegalStateException("编码器不支持流式编码模式。");
                }
                InputStream is = null;
                OutputStream os = null;
                if (commandInstance.url != null) {
                    is = commandInstance.url.openStream();
                } else if (commandInstance.fromFile != null) {
                    is = new java.io.FileInputStream(commandInstance.fromFile);
                } else if (commandInstance.textData != null && !commandInstance.textData.isEmpty()) {
                    if (!commandInstance.textData.containsKey("text"))
                        throw new IllegalStateException("参数--text或-T缺少属性'text'。");
                    String text = commandInstance.textData.get("text");
                    String encoding = commandInstance.textData.getOrDefault("encoding", "utf-8");
                    is = new java.io.ByteArrayInputStream(text.getBytes(encoding));
                } else if (commandInstance.hexString != null) {
                    HexEncoder hexEncoder = new HexEncoder();
                    is = new java.io.ByteArrayInputStream(hexEncoder.decodeToBytesUTF8(commandInstance.hexString));
                } else {
                    throw new IllegalStateException("未指定输入数据源。");
                }
                if (commandInstance.writeFile != null) {
                    os = new java.io.FileOutputStream(commandInstance.writeFile);
                } else {
                    os = System.out;
                }
                commandInstance.getEncoder().encodeStreaming(is, os,
                        StorageSizeParser.parseToIntBytes(commandInstance.streamingBufferSize));
            } else {
                long dataReadingStartTime = System.currentTimeMillis();
                byte[] inputData = commandInstance.getData();
                long dataReadingEndTime = System.currentTimeMillis();
                int inputDataLen = inputData.length;
                long dataProcessingStartTime = System.currentTimeMillis();
                byte[] outputData = commandInstance.getEncoder().encode(inputData);
                long dataProcessingEndTime = System.currentTimeMillis();
                int outputDataLen = outputData.length;
                long dataWritingStartTime = System.currentTimeMillis();
                commandInstance.writeData(outputData);
                long dataWritingEndTime = System.currentTimeMillis();
                long processingTime = dataProcessingEndTime - dataProcessingStartTime;
                long readingTime = dataReadingEndTime - dataReadingStartTime;
                long writingTime = dataWritingEndTime - dataWritingStartTime;

                if (commandInstance.info) {
                    System.out.printf("详细信息\n" +
                            "输入数据长度: %d bit(%d byte(s)) 输出数据长度: %d bit(%d byte(s))\n" +
                            "数据读取耗时: %d ms 数据输出/写入耗时: %d ms\n" +
                            "数据 I/O 速度: 输入 %.2f bit/s (%.2f Bytes/s) 输出 %.2f bit/s (%.2f Bytes/s)\n" +
                            "数据处理耗时: %d ms 数据处理速度: %.2f bit/s\n",
                            inputDataLen * 8, inputDataLen, outputDataLen * 8, outputDataLen,
                            readingTime, writingTime,
                            inputDataLen * 8.0 / readingTime, inputDataLen * 1.0 / readingTime,
                            outputDataLen * 8.0 / writingTime, outputDataLen * 1.0 / writingTime,
                            processingTime, inputDataLen * 8.0 / processingTime);
                }
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e);
            return 0x02;
        } catch (UnknownProcessorNameException e2) {
            System.out.println(e2);
            return 0x03;
        } catch (IOException e3) {
            System.out.println(e3);
            return 0x04;
        } catch (Exception exp) {
            System.out.println(exp);
            TerminalExceptionProcessor.blockingViewException(exp);
            return 0x01;
        }
        return 0x0;
    }

    public static int executeDecodeCommand(JCommander parsedCommander, DecodeCommand commandInstance) {
        try {
            if (commandInstance.streaming) {
                if (!commandInstance.getDecoder().supportedStreaming()) {
                    throw new IllegalStateException("解码器不支持流式解码模式。");
                }
                InputStream is = null;
                OutputStream os = null;
                if (commandInstance.url != null) {
                    is = commandInstance.url.openStream();
                } else if (commandInstance.fromFile != null) {
                    is = new java.io.FileInputStream(commandInstance.fromFile);
                } else if (commandInstance.textData != null && !commandInstance.textData.isEmpty()) {
                    if (!commandInstance.textData.containsKey("text"))
                        throw new IllegalStateException("参数--text或-T缺少属性'text'。");
                    String text = commandInstance.textData.get("text");
                    String encoding = commandInstance.textData.getOrDefault("encoding", "utf-8");
                    is = new java.io.ByteArrayInputStream(text.getBytes(encoding));
                } else {
                    throw new IllegalStateException("未指定输入数据源。");
                }
                if (commandInstance.writeFile != null) {
                    os = new java.io.FileOutputStream(commandInstance.writeFile);
                } else {
                    os = System.out;
                }
                commandInstance.getDecoder().decodeStreaming(is, os,
                        StorageSizeParser.parseToIntBytes(commandInstance.streamingBufferSize));
            } else {
                long dataReadingStartTime = System.currentTimeMillis();
                byte[] inputData = commandInstance.getData();
                long dataReadingEndTime = System.currentTimeMillis();
                int inputDataLen = inputData.length;
                long dataProcessingStartTime = System.currentTimeMillis();
                byte[] outputData = commandInstance.getDecoder().decode(inputData);
                long dataProcessingEndTime = System.currentTimeMillis();
                int outputDataLen = outputData.length;
                long dataWritingStartTime = System.currentTimeMillis();
                commandInstance.writeData(outputData);
                long dataWritingEndTime = System.currentTimeMillis();
                long processingTime = dataProcessingEndTime - dataProcessingStartTime;
                long readingTime = dataReadingEndTime - dataReadingStartTime;
                long writingTime = dataWritingEndTime - dataWritingStartTime;

                if (commandInstance.info) {
                    System.out.printf("详细信息\n" +
                            "输入数据长度: %d bit(%d byte(s)) 输出数据长度: %d bit(%d byte(s))\n" +
                            "数据读取耗时: %d ms 数据输出/写入耗时: %d ms\n" +
                            "数据 I/O 速度: 输入 %.2f bit/s (%.2f Bytes/s) 输出 %.2f bit/s (%.2f Bytes/s)\n" +
                            "数据处理耗时: %d ms 数据处理速度: %.2f bit/s\n",
                            inputDataLen * 8, inputDataLen, outputDataLen * 8, outputDataLen,
                            readingTime, writingTime,
                            inputDataLen * 8.0 / readingTime, inputDataLen * 1.0 / readingTime,
                            outputDataLen * 8.0 / writingTime, outputDataLen * 1.0 / writingTime,
                            processingTime, inputDataLen * 8.0 / processingTime);
                }
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e);
            return 0x02;
        } catch (UnknownProcessorNameException e2) {
            System.out.println(e2);
            return 0x03;
        } catch (DamagedDataException e3) {
            System.out.println(e3);
            return 0x04;
        } catch (IOException e3) {
            System.out.println(e3);
            return 0x05;
        } catch (Exception exp) {
            System.out.println(exp);
            TerminalExceptionProcessor.blockingViewException(exp);
            return 0x01;
        }
        return 0x0;
    }

    public static int executeEncryptCommand(JCommander parsedCommander, EncryptCommand commandInstance) {
        try {
            if (commandInstance.streaming) {
                if (!commandInstance.getEncryptor().supportedStreaming()) {
                    throw new IllegalStateException("加密器不支持流式编码模式。");
                }
                InputStream is = null;
                OutputStream os = null;
                if (commandInstance.url != null) {
                    is = commandInstance.url.openStream();
                } else if (commandInstance.fromFile != null) {
                    is = new java.io.FileInputStream(commandInstance.fromFile);
                } else if (commandInstance.textData != null && !commandInstance.textData.isEmpty()) {
                    if (!commandInstance.textData.containsKey("text"))
                        throw new IllegalStateException("参数--text或-T缺少属性'text'。");
                    String text = commandInstance.textData.get("text");
                    String encoding = commandInstance.textData.getOrDefault("encoding", "utf-8");
                    is = new java.io.ByteArrayInputStream(text.getBytes(encoding));
                } else if (commandInstance.hexString != null) {
                    HexEncoder hexEncoder = new HexEncoder();
                    is = new java.io.ByteArrayInputStream(hexEncoder.decodeToBytesUTF8(commandInstance.hexString));
                } else {
                    throw new IllegalStateException("未指定输入数据源。");
                }
                if (commandInstance.writeFile != null) {
                    os = new java.io.FileOutputStream(commandInstance.writeFile);
                } else {
                    os = System.out;
                }
                commandInstance.getEncryptor().encryptStreaming(is, os, commandInstance.getKeyData(),
                        StorageSizeParser.parseToIntBytes(commandInstance.streamingBufferSize));
            } else {
                long dataReadingStartTime = System.currentTimeMillis();
                byte[] inputData = commandInstance.getData();
                long dataReadingEndTime = System.currentTimeMillis();
                int inputDataLen = inputData.length;
                long dataProcessingStartTime = System.currentTimeMillis();
                byte[] outputData = commandInstance.getEncryptor().encrypt(inputData, commandInstance.getKeyData());
                long dataProcessingEndTime = System.currentTimeMillis();
                int outputDataLen = outputData.length;
                long dataWritingStartTime = System.currentTimeMillis();
                commandInstance.writeData(outputData);
                long dataWritingEndTime = System.currentTimeMillis();
                long processingTime = dataProcessingEndTime - dataProcessingStartTime;
                long readingTime = dataReadingEndTime - dataReadingStartTime;
                long writingTime = dataWritingEndTime - dataWritingStartTime;

                if (commandInstance.info) {
                    System.out.printf("详细信息\n" +
                            "输入数据长度: %d bit(%d byte(s)) 输出数据长度: %d bit(%d byte(s))\n" +
                            "数据读取耗时: %d ms 数据输出/写入耗时: %d ms\n" +
                            "数据 I/O 速度: 输入 %.2f bit/s (%.2f Bytes/s) 输出 %.2f bit/s (%.2f Bytes/s)\n" +
                            "数据处理耗时: %d ms 数据处理速度: %.2f bit/s\n" +
                            "密钥长度: %d bit\n",
                            inputDataLen * 8, inputDataLen, outputDataLen * 8, outputDataLen,
                            readingTime, writingTime,
                            inputDataLen * 8.0 / (readingTime / 1000.0), inputDataLen / (readingTime / 1000.0),
                            outputDataLen * 8.0 / (writingTime / 1000.0), outputDataLen * 1.0 / (writingTime / 1000.0),
                            processingTime, inputDataLen * 8.0 / (processingTime / 1000.0),
                            commandInstance.getKeyData().length * 8);
                }
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e);
            return 0x02;
        } catch (UnknownProcessorNameException e2) {
            System.out.println(e2);
            return 0x03;
        } catch (IOException e3) {
            System.out.println(e3);
            return 0x04;
        } catch (Exception exp) {
            System.out.println(exp);
            TerminalExceptionProcessor.blockingViewException(exp);
            return 0x01;
        }
        return 0x0;
    }

    public static int executeDecryptCommand(JCommander parsedCommander, DecryptCommand commandInstance) {
        try {
            if (commandInstance.streaming) {
                if (!commandInstance.getDecryptor().supportedStreaming()) {
                    throw new IllegalStateException("解密器不支持流式编码模式。");
                }
                InputStream is = null;
                OutputStream os = null;
                if (commandInstance.url != null) {
                    is = commandInstance.url.openStream();
                } else if (commandInstance.fromFile != null) {
                    is = new java.io.FileInputStream(commandInstance.fromFile);
                } else if (commandInstance.textData != null && !commandInstance.textData.isEmpty()) {
                    if (!commandInstance.textData.containsKey("text"))
                        throw new IllegalStateException("参数--text或-T缺少属性'text'。");
                    String text = commandInstance.textData.get("text");
                    String encoding = commandInstance.textData.getOrDefault("encoding", "utf-8");
                    is = new java.io.ByteArrayInputStream(text.getBytes(encoding));
                } else if (commandInstance.hexString != null) {
                    HexEncoder hexEncoder = new HexEncoder();
                    is = new java.io.ByteArrayInputStream(hexEncoder.decodeToBytesUTF8(commandInstance.hexString));
                } else {
                    throw new IllegalStateException("未指定输入数据源。");
                }
                if (commandInstance.writeFile != null) {
                    os = new java.io.FileOutputStream(commandInstance.writeFile);
                } else {
                    os = System.out;
                }
                commandInstance.getDecryptor().encryptStreaming(is, os, commandInstance.getKeyData(),
                        StorageSizeParser.parseToIntBytes(commandInstance.streamingBufferSize));
            } else {
                long dataReadingStartTime = System.currentTimeMillis();
                byte[] inputData = commandInstance.getData();
                long dataReadingEndTime = System.currentTimeMillis();
                int inputDataLen = inputData.length;
                long dataProcessingStartTime = System.currentTimeMillis();
                byte[] outputData = commandInstance.getDecryptor().decrypt(inputData, commandInstance.getKeyData());
                long dataProcessingEndTime = System.currentTimeMillis();
                int outputDataLen = outputData.length;
                long dataWritingStartTime = System.currentTimeMillis();
                commandInstance.writeData(outputData);
                long dataWritingEndTime = System.currentTimeMillis();
                long processingTime = dataProcessingEndTime - dataProcessingStartTime;
                long readingTime = dataReadingEndTime - dataReadingStartTime;
                long writingTime = dataWritingEndTime - dataWritingStartTime;

                if (commandInstance.info) {
                    System.out.printf("详细信息\n" +
                            "输入数据长度: %d bit(%d byte(s)) 输出数据长度: %d bit(%d byte(s))\n" +
                            "数据读取耗时: %d ms 数据输出/写入耗时: %d ms\n" +
                            "数据 I/O 速度: 输入 %.2f bit/s (%.2f Bytes/s) 输出 %.2f bit/s (%.2f Bytes/s)\n" +
                            "数据处理耗时: %d ms 数据处理速度: %.2f bit/s\n" +
                            "密钥长度: %d bit\n",
                            inputDataLen * 8, inputDataLen, outputDataLen * 8, outputDataLen,
                            readingTime, writingTime,
                            inputDataLen * 8.0 / readingTime, inputDataLen * 1.0 / readingTime,
                            outputDataLen * 8.0 / writingTime, outputDataLen * 1.0 / writingTime,
                            processingTime, inputDataLen * 8.0 / processingTime,
                            commandInstance.getKeyData().length * 8);
                }
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e);
            return 0x02;
        } catch (UnknownProcessorNameException e2) {
            System.out.println(e2);
            return 0x03;
        } catch (DamagedDataException e3) {
            System.out.println(e3);
            return 0x04;
        } catch (IOException e3) {
            System.out.println(e3);
            return 0x05;
        } catch (Exception exp) {
            System.out.println(exp);
            TerminalExceptionProcessor.blockingViewException(exp);
            return 0x01;
        }
        return 0x0;
    }

    public static int executeHashCommand(JCommander jcmd, HashCommand commandInstance) {
        try {
            if (commandInstance.streaming) {
                if (!commandInstance.getHashAlgorithm().supportedStreaming()) {
                    throw new IllegalStateException("哈希算法不支持流式编码模式。");
                }
                InputStream is = null;
                OutputStream os = null;
                if (commandInstance.url != null) {
                    is = commandInstance.url.openStream();
                } else if (commandInstance.fromFile != null) {
                    is = new java.io.FileInputStream(commandInstance.fromFile);
                } else if (commandInstance.textData != null && !commandInstance.textData.isEmpty()) {
                    if (!commandInstance.textData.containsKey("text"))
                        throw new IllegalStateException("参数--text或-T缺少属性'text'。");
                    String text = commandInstance.textData.get("text");
                    String encoding = commandInstance.textData.getOrDefault("encoding", "utf-8");
                    is = new java.io.ByteArrayInputStream(text.getBytes(encoding));
                } else if (commandInstance.hexString != null) {
                    HexEncoder hexEncoder = new HexEncoder();
                    is = new java.io.ByteArrayInputStream(hexEncoder.decodeToBytesUTF8(commandInstance.hexString));
                } else {
                    throw new IllegalStateException("未指定输入数据源。");
                }
                if (commandInstance.writeFile != null) {
                    os = new java.io.FileOutputStream(commandInstance.writeFile);
                } else {
                    os = System.out;
                }
                byte[] salt = commandInstance.getSaltData();
                if (salt.length > 0) {
                    commandInstance.getHashAlgorithm().hashStreaming(is, os,
                            StorageSizeParser.parseToIntBytes(commandInstance.streamingBufferSize), salt);
                } else {
                    commandInstance.getHashAlgorithm().hashStreaming(is, os,
                            StorageSizeParser.parseToIntBytes(commandInstance.streamingBufferSize));
                }
            } else {
                long dataReadingStartTime = System.currentTimeMillis();
                byte[] inputData = commandInstance.getData();
                long dataReadingEndTime = System.currentTimeMillis();
                int inputDataLen = inputData.length;
                long dataProcessingStartTime = System.currentTimeMillis();
                byte[] salt = commandInstance.getSaltData();
                byte[] outputData = (salt.length > 0 ? commandInstance.getHashAlgorithm().hash(commandInstance.getData(), salt) : commandInstance.getHashAlgorithm().hash(commandInstance.getData()));
                long dataProcessingEndTime = System.currentTimeMillis();
                int outputDataLen = outputData.length;
                long dataWritingStartTime = System.currentTimeMillis();
                commandInstance.writeData(outputData);
                long dataWritingEndTime = System.currentTimeMillis();
                long processingTime = dataProcessingEndTime - dataProcessingStartTime;
                long readingTime = dataReadingEndTime - dataReadingStartTime;
                long writingTime = dataWritingEndTime - dataWritingStartTime;

                if (commandInstance.info) {
                    System.out.printf("详细信息\n" +
                            "输入数据长度: %d bit(%d byte(s)) 输出数据长度: %d bit(%d byte(s))\n" +
                            "数据读取耗时: %d ms 数据输出/写入耗时: %d ms\n" +
                            "数据 I/O 速度: 输入 %.2f bit/s (%.2f Bytes/s) 输出 %.2f bit/s (%.2f Bytes/s)\n" +
                            "数据处理耗时: %d ms 数据处理速度: %.2f bit/s\n" +
                            "哈希盐长度: %d bit\n",
                            inputDataLen * 8, inputDataLen, outputDataLen * 8, outputDataLen,
                            readingTime, writingTime,
                            inputDataLen * 8.0 / readingTime, inputDataLen * 1.0 / readingTime,
                            outputDataLen * 8.0 / writingTime, outputDataLen * 1.0 / writingTime,
                            processingTime, inputDataLen * 8.0 / processingTime,
                            commandInstance.getSaltData().length * 8);
                }
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e);
            return 0x02;
        } catch (UnknownProcessorNameException e2) {
            System.out.println(e2);
            return 0x03;
        } catch (DamagedDataException e3) {
            System.out.println(e3);
            return 0x04;
        } catch (IOException e3) {
            System.out.println(e3);
            return 0x05;
        } catch (Exception exp) {
            System.out.println(exp);
            TerminalExceptionProcessor.blockingViewException(exp);
            return 0x01;
        }
        return 0x0;
    }

    public static int executePackmgrCommand(JCommander jcmd, PackmgrCommand commandInstance) {
        try {
            if (commandInstance.installPack != null) {
                ExtendPackageManager.installFromStream(commandInstance.installPack.openStream(), commandInstance.global);
            } else if (commandInstance.uninstallPackId != null) {
                ExtendPackageManager.uninstallPack(commandInstance.uninstallPackId, commandInstance.global); 
            } else if (commandInstance.sourcePackId != null) {
                System.out.println(ExtendPackageManager.findPack(commandInstance.sourcePackId, commandInstance.global));
            } else if (commandInstance.infoInstalledId != null) {
                System.out.println(ExtendPackageManager.getInstalledManifest(commandInstance.infoInstalledId, commandInstance.global));
            } else if (commandInstance.infoNotInstalledUrl != null) {
                System.out.println(ExtendPackageManager.getManifest(commandInstance.infoNotInstalledUrl.openStream()));
            } else if (commandInstance.list) {
                String l = ExtendPackageManager.getAllInstalledList(commandInstance.global);
                System.out.println((l.isBlank() ? "无" : l));
            }
        } catch (SecurityRiskException securityRiskException) {
            System.out.println("警告: 安全问题, 请务必重视!");
            System.out.println(securityRiskException);
            return 0x02;
        } catch (SAXException saxException) {
            System.out.println(saxException);
            return 0x03;
        } catch (IllegalManifestException illegalManifestException) {
            System.out.println(illegalManifestException);
            return 0x04;
        } catch (DamagedExtractPackageException damagedExtractPackageException) {
            System.out.println(damagedExtractPackageException);
            return 0x05;
        } catch (PackageNotInstalledException packageNotInstalledException) {
            System.out.println(packageNotInstalledException);
            return 0x06;
        } catch (IOException ioException) {
            System.out.println(ioException);
            return 0x07;
        } catch (Throwable throwable) {
            TerminalExceptionProcessor.blockingViewException(throwable);
            return 0x01;
        }
        return 0x00;
    }

    public static int executePackdevCommand(JCommander jcmd, PackdevCommand commandInstance) {
        try {
            commandInstance.building();
        } catch (IllegalStateException illegalStateException) {
            System.out.println(illegalStateException);
            return 0x02;
        } catch (IllegalManifestException illegalManifestException) {
            System.out.println(illegalManifestException);
            return 0x03;
        } catch (IOException ioException) {
            System.out.println(ioException);
            return 0x04;
        } catch (Throwable throwable) {
            TerminalExceptionProcessor.blockingViewException(throwable);
            return 0x01;
        }
        return 0x00;
    }
}
