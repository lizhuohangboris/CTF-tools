Web

测试{{7*7}}发现又ssti漏洞

![image-20240705174424693](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240705174424693.png)



访问所有 `object` 类的子类

```
{{ ''.__class__.__mro__[1].__subclasses__() }}
```

![image-20240705180418565](C:\Users\92579\AppData\Roaming\Typora\typora-user-images\image-20240705180418565.png)

Fenjing一把梭