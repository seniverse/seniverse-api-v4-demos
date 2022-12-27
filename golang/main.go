package main

import (
	"crypto/hmac"
	"crypto/sha1"
	"encoding/base64"
	"fmt"
	"io/ioutil"
	"net/http"
	"net/url"
	"sort"
	"strings"
	"time"
)

const publicKey = "XXX" // 公钥
const secret = "XXX"    // 私钥

func Get(url string) ([]byte, error) {
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		fmt.Println("net.NewRequest err", url)
		return nil, err
	}
	client := &http.Client{
		Timeout: 15 * time.Second,
		Transport: &http.Transport{
			TLSHandshakeTimeout:   15 * time.Second,
			ResponseHeaderTimeout: 15 * time.Second,
			ExpectContinueTimeout: 1 * time.Second,
		},
	}

	rsp, err := client.Do(req)
	if err != nil {
		return nil, err
	}

	defer rsp.Body.Close()

	bodyBytes, err := ioutil.ReadAll(rsp.Body)
	if err != nil {
		return nil, err
	}

	return bodyBytes, nil
}

// UrlValuesToString 将 url.Value 转换成字符串
func UrlValuesToString(v url.Values) string {
	if v == nil {
		return ""
	}
	var buf strings.Builder
	keys := make([]string, 0, len(v))
	for k := range v {
		keys = append(keys, k)
	}
	sort.Strings(keys)
	for _, k := range keys {
		vs := v[k]

		for _, v := range vs {
			if buf.Len() > 0 {
				buf.WriteByte('&')
			}
			buf.WriteString(k)
			buf.WriteByte('=')
			buf.WriteString(v)
		}
	}
	return buf.String()
}

func EncodeUrlValues(v url.Values) string {
	if v == nil {
		return ""
	}
	var buf strings.Builder
	keys := make([]string, 0, len(v))
	for k := range v {
		keys = append(keys, k)
	}
	sort.Strings(keys)
	for _, k := range keys {
		vs := v[k]

		for _, v := range vs {
			if buf.Len() > 0 {
				buf.WriteByte('&')
			}
			buf.WriteString(k)
			buf.WriteByte('=')
			buf.WriteString(url.QueryEscape(v))
		}
	}
	return buf.String()
}

// 签名
func signature(urlStr string, params map[string]string) (string, error) {
	urlObj, err := url.ParseRequestURI(urlStr)
	if err != nil {
		return "", err
	}

	values, err := url.ParseQuery(urlObj.RawQuery)
	if err != nil {
		return "", err
	}
	for key, value := range params {
		values[key] = []string{value}
	}
	//values["public_key"] = []string{publicKey}

	queryStr := UrlValuesToString(values)
	encodeQueryStr := EncodeUrlValues(values)
	secretBytes := []byte(secret)

	hash := hmac.New(sha1.New, secretBytes)
	hash.Write([]byte(queryStr))

	encoded := base64.StdEncoding.EncodeToString(hash.Sum(nil))
	encoded = url.QueryEscape(encoded)

	urlObj.RawQuery = fmt.Sprintf("%s&sig=%s", queryStr, encoded)
	encodeUrl := fmt.Sprintf("%s://%s%s?%s&sig=%s", urlObj.Scheme, urlObj.Host, urlObj.Path, encodeQueryStr, encoded)
	fmt.Println("浏览器访问链接:", encodeUrl)

	return urlObj.String(), nil
}

func main() {
	urlStr := "https://api.seniverse.com/v4?fields=precip_minutely"
	ts := fmt.Sprintf("%d", time.Now().Unix())
	ttl := "600"
	latitude := 29.5617
	longitude := 120.0962
	urlStr, err := signature(urlStr, map[string]string{
		"ts":         ts,
		"ttl":        ttl,
		"public_key": publicKey,
		"locations":  fmt.Sprintf("%f:%f", latitude, longitude),
	})
	if err != nil {
		return
	}

	bodyBytes, err := Get(urlStr)
	if err != nil {
		return
	}

	fmt.Println(string(bodyBytes))
}
