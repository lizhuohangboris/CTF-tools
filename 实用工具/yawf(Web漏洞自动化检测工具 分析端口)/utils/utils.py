import os
import random
import requests
import json
import re
import esprima
from configparser import ConfigParser
from difflib import SequenceMatcher
from xml.etree import ElementTree as ET
from requests.auth import HTTPDigestAuth
from requests_ntlm2 import HttpNtlmAuth

# 忽略 SSL 告警信息
try:
    from requests.packages import urllib3
    urllib3.disable_warnings()
except Exception:
    pass

def check_file(filename):
    """
    检查文件是否存在和可读
    """

    valid = False

    if os.path.isfile(filename) and os.access(filename, os.R_OK):
        valid = True

    return valid

def get_default_headers():
    """
    获取默认请求头
    返回区分大小写的常规 dict 类型，且请求头名称为小写
    """

    case_sensitive_dict = requests.utils.default_headers()
    normal_plain_dict = dict((k.lower(), v) for k, v in case_sensitive_dict.items())

    return normal_plain_dict

def send_request(request, require_response_header=False):
    """
    发送 HTTP 请求
    """

    response = headers = status = None
    
    json_data = None
    data_data = None
    auth = None
    if request['method'] == 'POST':
        if 'json' in request['headers']['content-type']:
            json_data = json.loads(request['data']) if type(request['data']) is str else request['data']
        else:
            data_data = request['data']
    if request['auth']:
        cred = request['auth']['auth_cred'].split(':', 1)
        if request['auth']['auth_type'] == 'Basic':
            auth = (cred[0], cred[1])
        elif request['auth']['auth_type'] == 'Digest':
            auth = HTTPDigestAuth(cred[0], cred[1])
        elif request['auth']['auth_type'] == 'NTLM':
            auth = HttpNtlmAuth(cred[0], cred[1])
    try:    
        rsp = requests.request(request['method'], request['url'], 
            params=request['params'],
            headers=request['headers'], 
            cookies=request['cookies'], 
            proxies=request['proxies'], 
            data=data_data,
            json=json_data, 
            auth=auth,
            timeout=request['timeout'], 
            verify=False)

        response = rsp.text
        headers = rsp.headers if require_response_header else None
        status = rsp.status_code

    except requests.exceptions.RequestException as e:
        print(f'[*] WARN : request error : {str(e)}')

    return {
        'request': request,
        'response': response,
        'headers': headers,
        'status': status
    }

def parse_conf(file):
    """
    解析配置文件，将配置数据存储在内存中
    通过 conf_dict[section_option] 获取配置项的值
    """

    conf_dict = {}

    try:
        conf = ConfigParser()
        conf.read(file, encoding='utf-8')
        for section in conf.sections():
            for option in conf.options(section):
                conf_dict[f'{section}_{option}'] = conf.get(section, option)
    except Exception:
        pass

    return conf_dict

def read_file(file):
    """
    逐行读取文件内容到 list，忽略 # 开头行和空白行
    """

    lines = []

    with open(file, 'r', encoding='utf-8') as f:
        lines = [line.strip() for line in f if not line.startswith('#') and line != '\n']
    
    return lines

def similar(str1, str2):
    """
    比较字符串 str1 和 str2 的相似度
    """

    return SequenceMatcher(None, str1, str2).quick_ratio()

def get_random_str(length):
    """
    生成指定长度的随机字符串
    """

    return ''.join(random.choice('0123456789abcdefghijklmnopqrstuvwxyz') for _ in range(length))

def get_content_type(content):
    """
    获取字符串内容类型，支持 x-www-form-urlencoded、json 和 xml 三种类型
    """

    type = None
    
    if not content.replace('.','',1).isdigit() and content not in ['true', 'false']:
        try:
            json.loads(content)
            type = 'json'
        except ValueError:
            try:
                ET.fromstring(content)
                type = 'xml'
            except ET.ParseError:
                if re.search(r"^[A-Za-z0-9_]+=[^=]+", content):
                    type = 'form'

    return type

def is_base64(string):
    """
    【废弃】校验字符串是否为 Base64 编码
    不可能真正校验字符串是否为 Base64 编码，只能判断字符串是否符合 Base64 数据格式
    """

    is_b64 = False

    if re.search("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$", string):
        is_b64 = True

    return is_b64

def get_jsonp_keys(jsonp):
    """
    递归获取 jsonp 参数中所有的键名，用于敏感数据检测。
    如：
    callback({"username":"admin"}); // username
    callback({"data": {username:"admin"}}); // data, username
    """

    def get_keys(node):
        result = []
        if isinstance(node, esprima.nodes.ObjectExpression):
            for property in node.properties:
                if isinstance(property.key, esprima.nodes.Identifier):
                    result.append(property.key.name)
                elif isinstance(property.key, esprima.nodes.Literal):
                    result.append(property.key.value)
                result += get_keys(property.value)
        elif isinstance(node, esprima.nodes.Node):
            for _, value in node.items():
                result += get_keys(value)
        elif isinstance(node, list):
            for item in node:
                result += get_keys(item)
        return result

    ast_obj = esprima.parse(jsonp)

    return list(get_keys(ast_obj))
