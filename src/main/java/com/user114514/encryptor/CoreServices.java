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
            byte[] inputData = commandInstance.getData();
            byte[] outputData = commandInstance.getEncoder().encode(inputData);
            commandInstance.writeData(outputData);
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
            byte[] inputData = commandInstance.getData();
            byte[] outputData = commandInstance.getDecoder().decode(inputData);
            commandInstance.writeData(outputData);
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
            byte[] inputData = commandInstance.getData();
            byte[] outputData = commandInstance.getEncryptor().encrypt(inputData, commandInstance.getKeyData());
            commandInstance.writeData(outputData);
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
            byte[] inputData = commandInstance.getData();
            byte[] outputData = commandInstance.getDecryptor().decrypt(inputData, commandInstance.getKeyData());
            commandInstance.writeData(outputData);
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
