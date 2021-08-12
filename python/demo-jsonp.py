from urllib.parse import urlencode
from urllib.request import urlopen
import hashlib
import hmac
from base64 import b64encode
import json
import time
from urllib.error import HTTPError
from pprint import pprint
import logging
import os
import sys
logger = logging.getLogger(__name__)

def fetch_weather(public_key, secret_key, **params):
    params['public_key'] = public_key
    params.setdefault('ts', str(int(time.time())))
    query = "&".join(f"{key}={value}" for key, value in sorted(params.items())).encode()
    params['sig'] = b64encode(hmac.new(secret_key.encode(), query, hashlib.sha1).digest()).decode()
    try:
        response = urlopen("https://api.seniverse.com/v4?" + urlencode(params))
    except HTTPError as e:
        error = e.fp.read().decode()
        logger.exception(error)
        raise
    return json.load(response)

def main(argv):
    argv = argv or ["locations=36:118", "fields=weather_hourly_1h"]
    params = dict(arg.split("=", 1) for arg in argv)
    result = fetch_weather(PUBLIC_KEY, PRIVATE_KEY, **params)
    pprint(result)
    
PUBLIC_KEY = 'P8itVvN3qWEhoSor'
PRIVATE_KEY = 'SgSn6OPU_0MDadrDi'
if __name__ == '__main__':
    main(sys.argv[1:])
