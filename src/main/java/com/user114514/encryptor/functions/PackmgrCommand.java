package com.user114514.encryptor.functions;

import java.net.URL;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(
    commandNames = {"packmgr"},
    commandDescription = "管理、安装、卸载扩展包。"
)

public class PackmgrCommand {
    @Parameter(
        names = {"--install", "-i"},
        description = "根据 URL 安装新扩展包。",
        arity = 1
    )
    public URL installPack;

    @Parameter(
        names = {"--uninstall", "-u"},
        description = "使用包ID卸载扩展包。",
        arity = 1
    )
    public String uninstallPackId;

    @Parameter(
        names = {"--source", "-s"},
        description = "根据包ID查看一个已安装包在您设备的安装位置。",
        arity = 1
    )
    public String sourcePackId;

    @Parameter(
        names = {"--info", "--info-notinstalled", "-I"},
        description = "根据 URL 查看一个未安装包的清单信息。",
        arity = 1
    )
    public URL infoNotInstalledUrl;

    @Parameter(
        names = {"--infoi", "--info-installed", "-M"},
        description = "根据已安装的包ID查看包信息。",
        arity = 1
    )
    public String infoInstalledId;

    @Parameter(
        names = {"--list", "-l"},
        description = "列出所有已安装的包。",
        arity = 0
    )
    public boolean list;

    @Parameter(
        names = {"--global", "--everyone", "-g"},
        description = "为所有用户可访问的公共包执行操作(不填此选项表示为当前用户)。",
        arity = 0
    )
    public boolean global;
}
