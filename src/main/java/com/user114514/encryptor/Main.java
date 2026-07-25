package com.user114514.encryptor;

import java.util.Date;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.user114514.encryptor.functions.DecodeCommand;
import com.user114514.encryptor.functions.DecryptCommand;
import com.user114514.encryptor.functions.EncodeCommand;
import com.user114514.encryptor.functions.EncryptCommand;
import com.user114514.encryptor.functions.MainParameters;

public class Main {
    public static void main(String[] args) {
        try {
            //args = new String[] { "decode", "--decoder", "b64", "-Ttext=zxX", "-Tencoding=UTF-8"};
            MainParameters mainParameters = new MainParameters();
            EncodeCommand encodeCommand = new EncodeCommand();
            DecodeCommand decodeCommand = new DecodeCommand();
            EncryptCommand encryptCommand = new EncryptCommand();
            DecryptCommand decryptCommand = new DecryptCommand();

            JCommander cmd = new JCommander.Builder()
                    .programName(VersionConfig.APPLICATION_NAME)
                    .addObject(mainParameters)
                    .addCommand(encodeCommand)
                    .addCommand(decodeCommand)
                    .addCommand(encryptCommand)
                    .addCommand(decryptCommand)
                    .build();

            if (args.length == 0) {
                cmd.usage();
                return;
            }

            cmd.parse(args);

            if (mainParameters.version) {
                System.out.printf("%s %tY %s\n", VersionConfig.APPLICATION_NAME, new Date(), VersionConfig.VER_NAME);
            } else if (mainParameters.help) {
                cmd.usage();
            } else if (mainParameters.avaibleEncoder) {
                System.out.printf("当前版本 %s 可用的编码器：\n%s\n", VersionConfig.VER_NAME,
                        String.join("\n", VersionConfig.SUPPORTED_ENCODER));
            } else if (mainParameters.avaibleEncryptor) {
                System.out.printf("当前版本 %s 可用的加密器：\n%s\n", VersionConfig.VER_NAME,
                        String.join("\n", VersionConfig.SUPPORTED_ENCRYPTOR));
            } else {
                String commandName = cmd.getParsedCommand();

                int code = 0;

                if (commandName.equals("encode")) {
                    code = CoreServices.executeEncodeCommand(cmd, encodeCommand);
                } else if (commandName.equals("decode")) {
                    code = CoreServices.executeDecodeCommand(cmd, decodeCommand);
                } else if (commandName.equals("encrypt")) {
                    code = CoreServices.executeEncryptCommand(cmd, encryptCommand);
                } else if (commandName.equals("decrypt")) {
                    code = CoreServices.executeDecryptCommand(cmd, decryptCommand);
                }

                System.out.printf("%s, 状态码: %#x\n", (code == 0x00 ? "成功" : "错误"), code);
            }

        } catch (ParameterException syntaxException) {
            System.out.println("语法错误: " + syntaxException.getLocalizedMessage());
        } catch (Exception e) {
            TerminalExceptionProcessor.blockingViewException(e);
        }
    }
}