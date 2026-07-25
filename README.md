*Document Language: zh-CN*
# JCrying 工具介绍与示例

### 介绍
JCrying 是一个 CLI 式的数据编解码，加解密的工具，提供了许多可以使用的编解码与加解密算法。

### 示例
1. 显示帮助文档
```bash
jcrying --help
```

2. 显示版本信息
```bash
jcrying --version
```

3. 列出当前版本可以使用的编码器
```bash
jcrying --avaible-encoder
```

4. 使用 Base64 编码 “HelloWorld”
```bash
jcrying encode --encoder base64 -Ttext=HelloWorld -Tencoding=UTF-8
```
P.S. 如果-Ttext后面的文本全部由ASCII字符组成，-Tencoding可以不写，因为所有的字符集的ASCII字符的二进制值都一样，也就可以互相转换
