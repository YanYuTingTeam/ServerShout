# 📣 ServerShout 2

[English](./README.md) | 简体中文

ServerShout 是一款适用于 BungeeCord/Velocity/Bukkit 的跨服喊话与邀请插件。具备高性能和高度自定义的特点，兼容 PlaceholderAPI 和 LuckPerms 插件。支持多种消息格式，允许自定义频道和指令，以及消息屏蔽等功能。

了解更多详细信息，请访问我们的[帖子](https://www.minebbs.com/resources/9367/)。

## 构建

要构建 ServerShout，需要以下环境：

- JDK 8 或更高
- Git

请按照以下步骤进行构建：

```bash
git clone https://github.com/TheRamU/ServerShout.git
cd ServerShout
./gradlew clean build
```

构建完成后 jar 文件将位于 `build/libs` 目录下。

## AerVexHorn 消息认证

可选启用认证：在代理端的 ServerShout 数据目录创建 `shout-display-auth.properties`，内容为 `hmac-key=` 后紧跟 64 个十六进制字符（随机生成的 32 字节密钥）。在每个目标子服的 AerVexHorn 配置中设置同一个 `servershout.hmac-key`，并重启或重新加载各端配置。密钥不得公开；确保代理和子服系统时间同步。

未创建该文件，或文件中的密钥为空时使用旧协议，旧协议不提供消息认证。文件存在时必须包含且只能包含 `hmac-key`；非空密钥格式不正确会导致配置加载失败，而不会静默退回旧协议。运行时从已启用改为空值或移除文件将拒绝重新加载；如确需停用，修改配置后重启代理，并同步修改各子服。启用认证需要配套支持 v4 的 AerVexHorn。逐人转发遵守玩家的屏蔽设置，并在本服广播关闭时仅向发言人回显；这会增加在线人数较多时的消息包数量。
