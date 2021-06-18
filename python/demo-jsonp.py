import base64
import time
import hmac
import hashlib
import json
import requests

KEY = '4r9bergjetiv1tsd'  # API key
UID = "U785B76FC9"  # 用户ID
API = 'https://api.seniverse.com/v4'

def fetchWeather():
    ts = int(time.time())
    # V4 API 的请求参数，可替换为其他
    params = "locations=36:118&fields=weather_hourly_1h&public_key={uid}&ts={ts}".format(ts=ts, uid=UID)
    str = '&'
    params = str.join(sorted(params.split('&'))) #请求参数需要按字母顺序排列
    
    key = bytes(KEY, 'UTF-8')
    raw = bytes(params, 'UTF-8')

    digester = hmac.new(key, raw, hashlib.sha1).digest()
    signature = base64.encodestring(digester).rstrip()
    sig = signature.decode('utf8')

    result = requests.get(API, params={
        'fields': 'weather_hourly_1h',
        'locations': '36:118',
        'public_key': UID,
        'ts': ts,
        'sig': sig },timeout=1)

    return result.json()

if __name__ == '__main__':
    results = fetchWeather() #返回json格式数据
