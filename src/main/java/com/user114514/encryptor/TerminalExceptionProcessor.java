package com.user114514.encryptor;

import java.util.Scanner;

public class TerminalExceptionProcessor {
    public static void blockingViewException(Throwable th) {
        System.out.println("错误: " + th.getMessage());
        System.out.println("[I] 查看详细信息 [Other] 忽略 (默认忽略)");
        try (Scanner scanner = new Scanner(System.in)) {
            String currentLine = scanner.nextLine();
            if (currentLine == null || currentLine.isBlank() || currentLine.toLowerCase().charAt(0) != 'i')
                return;
            else {
                System.out.printf("异常类名: %s | 异常全限定名: %s\n异常信息: %s\n",
                        th.getClass().getSimpleName(), th.getClass().getName(),
                        th.getMessage());
                System.out.println("[S] 显示堆栈 [C] 显示根本异常 [Other] 关闭");
                currentLine = scanner.nextLine();
                if (currentLine == null || currentLine.isBlank())
                    return;
                char key = currentLine.toLowerCase().charAt(0);
                if (key == 's') {
                    th.printStackTrace();
                    return;
                } else if (key == 'c') {
                    System.out.printf("异常类名: %s | 异常全限定名: %s\n异常信息: %s\n",
                            th.getCause().getClass().getSimpleName(), th.getCause().getClass().getName(),
                            th.getCause().getMessage());
                    return;
                } else {
                    return;
                }
            }
        }
    }
}
