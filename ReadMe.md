# awd java agent logwaf

## 构建、使用命令
```angular2html
mvn clean package "-Dmaven.test.skip=true"

# java 8
java  -javaagent:agent-waf-jar-with-dependencies.jar=2.txt -jar .\java2.jar

# java 17 
java --add-opens=java.base/java.nio=ALL-UNNAMED -javaagent:agent-waf-jar-with-dependencies.jar=a.txt -jar .\java1.jar

# 参数是日志文件路径
```

## 原理

通过agent方式，使用javassist动态修改`org.apache.tomcat.util.net.NioEndpoint$NioSocketWrapper`中的read以及doWrite方法。
目前测试过如下版本
- tomcat-embed-core-9.0.68.jar
- tomcat-embed-core-8.5.34.jar

## 问题
- 同一个请求的响应会被记录多次
- 

## 效果
```angular2html

GET /upload?data=PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4NCjwhRE9DVFlQRSBmb28gWyA8IUVOVElUWSB4eGUgU1lTVEVNICJodHRwOi8vMTI3LjAuMC4xOjg4ODgvMTIzMTIzIj4gXT4NCjxzdG9ja0NoZWNrPjxwcm9kdWN0SWQ%2BJnh4ZTs8L3Byb2R1Y3RJZD48c3RvcmVJZD4xPC9zdG9yZUlkPjwvc3RvY2tDaGVjaz4%3D HTTP/1.1
Host: 172.19.176.1:8099
Pragma: no-cache
Cache-Control: no-cache
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
Connection: close

HTTP/1.1 200 
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/html;charset=UTF-8
Content-Length: 29
Date: Thu, 18 Apr 2024 04:27:58 GMT
Connection: close

Content-Type: application/xml

HTTP/1.1 200 
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/html;charset=UTF-8
Content-Length: 29
Date: Thu, 18 Apr 2024 04:27:58 GMT
Connection: close

Content-Type: application/xml

HTTP/1.1 200 
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/html;charset=UTF-8
Content-Length: 29
Date: Thu, 18 Apr 2024 04:27:58 GMT
Connection: close

Content-Type: application/xml

POST /upload HTTP/1.1
Host: 172.19.176.1:8099
Pragma: no-cache
Cache-Control: no-cache
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
Connection: close
Content-Type: application/x-www-form-urlencoded
Content-Length: 257

data=PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4NCjwhRE9DVFlQRSBmb28gWyA8IUVOVElUWSB4eGUgU1lTVEVNICJodHRwOi8vMTI3LjAuMC4xOjg4ODgvMTIzMTIzIj4gXT4NCjxzdG9ja0NoZWNrPjxwcm9kdWN0SWQ%2BJnh4ZTs8L3Byb2R1Y3RJZD48c3RvcmVJZD4xPC9zdG9yZUlkPjwvc3RvY2tDaGVjaz4%3D

HTTP/1.1 200 
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/html;charset=UTF-8
Content-Length: 29
Date: Thu, 18 Apr 2024 04:28:02 GMT
Connection: close

Content-Type: application/xml

HTTP/1.1 200 
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/html;charset=UTF-8
Content-Length: 29
Date: Thu, 18 Apr 2024 04:28:02 GMT
Connection: close

Content-Type: application/xml

HTTP/1.1 200 
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/html;charset=UTF-8
Content-Length: 29
Date: Thu, 18 Apr 2024 04:28:02 GMT
Connection: close

Content-Type: application/xml


```
