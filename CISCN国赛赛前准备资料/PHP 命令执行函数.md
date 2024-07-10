在PHP中，有几种函数可以用于执行系统命令。这些函数在CTF比赛中经常被用来发现和利用命令执行漏洞。以下是常见的PHP命令执行函数及其用法：

### 1. `exec()`

`exec()` 函数执行给定的命令，并返回最后一行的输出结果。可以选择将命令的所有输出作为数组返回。

```php
// 执行命令并返回最后一行输出
$last_line = exec('ls', $output, $return_var);
echo "最后一行输出: $last_line\n";

// 输出所有行
print_r($output);

// 返回值
echo "返回值: $return_var\n";
```

### 2. `shell_exec()`

`shell_exec()` 函数执行命令，并返回完整的输出结果作为字符串。

```php
// 执行命令并返回完整输出
$output = shell_exec('ls -l');
echo "<pre>$output</pre>";
```

### 3. `system()`

`system()` 函数执行命令，并将输出直接发送到浏览器，同时返回命令执行的最后一行结果。

```php
// 执行命令并将输出发送到浏览器
$last_line = system('ls -l', $return_var);

// 返回值
echo "返回值: $return_var\n";
```

### 4. `passthru()`

`passthru()` 函数执行命令，并将原始输出（包括二进制数据）直接发送到浏览器，通常用于需要保留输出格式的情况，如图像或其他二进制数据。

```php
// 执行命令并将原始输出发送到浏览器
passthru('ls -l', $return_var);

// 返回值
echo "返回值: $return_var\n";
```

### 5. `popen()`

`popen()` 函数打开一个进程管道，用于执行命令，并返回一个文件指针，用于读取或写入命令的输入/输出。

```php
// 打开进程管道
$handle = popen('ls -l', 'r');

// 读取输出
while (!feof($handle)) {
    echo fgets($handle);
}

// 关闭管道
pclose($handle);
```

### 6. `proc_open()`

`proc_open()` 函数提供了更细粒度的进程控制，可以设置管道、进程属性和环境变量。

```php
$descriptorspec = [
    0 => ["pipe", "r"],  // stdin
    1 => ["pipe", "w"],  // stdout
    2 => ["pipe", "w"]   // stderr
];

$process = proc_open('ls -l', $descriptorspec, $pipes);

if (is_resource($process)) {
    // 关闭标准输入管道
    fclose($pipes[0]);

    // 读取标准输出
    echo stream_get_contents($pipes[1]);
    fclose($pipes[1]);

    // 读取标准错误
    echo stream_get_contents($pipes[2]);
    fclose($pipes[2]);

    // 关闭进程
    proc_close($process);
}
```

### 7. `backticks` (反引号)

反引号是PHP中的另一种执行命令的方法，类似于shell中的反引号。

```php
// 使用反引号执行命令
$output = `ls -l`;
echo "<pre>$output</pre>";
```

### 安全注意事项

在实际应用中，直接使用这些命令执行函数会带来严重的安全风险，尤其是在处理用户输入时。因此，以下是一些常见的安全措施：

- **输入验证和过滤**：严格验证和过滤用户输入，防止恶意命令注入。
- **最小权限原则**：以最小权限运行PHP脚本，减少潜在的安全风险。
- **禁用不必要的函数**：在PHP配置中禁用不必要的命令执行函数（如`disable_functions`配置）。

通过这些措施，可以有效减少命令执行漏洞带来的安全风险。在CTF比赛中，熟悉这些函数的用法和绕过技巧有助于发现和利用漏洞。