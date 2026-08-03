package com.user114514.encryptor;

import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.user114514.encryptor.excep.IllegalDataException;
import com.user114514.encryptor.excep.UnknownProcessorNameException;
import com.user114514.encryptor.functions.DecodeCommand;
import com.user114514.encryptor.functions.DecryptCommand;
import com.user114514.encryptor.functions.EncodeCommand;
import com.user114514.encryptor.functions.EncryptCommand;

public class CoreServices {
    public static int executeEncodeCommand(JCommander parsedCommander, EncodeCommand commandInstance) {
        try {
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
                                    processingTime, inputDataLen * 8.0 / processingTime
                                    );
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
                                    processingTime, inputDataLen * 8.0 / processingTime
                                    );
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e);
            return 0x02;
        } catch (UnknownProcessorNameException e2) {
            System.out.println(e2);
            return 0x03;
        } catch (IllegalDataException e3) {
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
                                    commandInstance.getKeyData().length * 8
                                    );
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
                                    commandInstance.getKeyData().length * 8
                                    );
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e);
            return 0x02;
        } catch (UnknownProcessorNameException e2) {
            System.out.println(e2);
            return 0x03;
        } catch (IllegalDataException e3) {
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
}
