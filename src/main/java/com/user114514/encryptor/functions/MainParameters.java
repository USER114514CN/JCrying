package com.user114514.encryptor.functions;

import com.beust.jcommander.Parameter;

public class MainParameters {
    @Parameter(names = {"--version", "-v"},
        description = "输出当前的版本信息。",
        arity = 0
    )
    public boolean version;

    @Parameter(names = {"--help", "--usage", "-h", "-?"},
        description = "输出帮助信息文档。",
        help = true
    )
    public boolean help;

    @Parameter(
        names = {"--available-encoder", "--supported-encoder", "-aE"},
        description = "输出当前版本可用的编码器。",
        arity = 0
    )
    public boolean avaibleEncoder;

    @Parameter(
        names = {"--available-encryptor", "--supported-encryptor", "-aX"},
        description = "输出当前版本可用的加密器。",
        arity = 0
    )
    public boolean avaibleEncryptor;
}
