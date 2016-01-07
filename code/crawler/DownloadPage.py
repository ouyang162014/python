#coding=utf-8
import urllib2
import cookielib

#方法一
#直接请求
print "方法一"
response=urllib2.urlopen("http://www.baidu.com")
#获取状态码，如果是200则表示获取成功
print(response.getcode())
#读取内容
content=response.read()
print content

#方法二
#创建request对象
print "方法二"
request=urllib2.Request("http://www.baidu.com")
#添加数据
#添加http的header，伪装成网页请求
request.add_header("User-Agent","Mazilla/5.0")
#发送请求获取结果
response=urllib2.urlopen(request)
content=response.read()
print content

#方法三
#创建cookie容器
print "方法三"
cj=cookielib.CookieJar()
#创建一个opener
opener=urllib2.build_opener(urllib2.HTTPCookieProcessor(cj))
#给urllib2安装opener
urllib2.install_opener(opener)
#使用cookie的urllib访问网页
response=urllib2.urlopen("https://www.baidu.com")
content=response.read()
print content