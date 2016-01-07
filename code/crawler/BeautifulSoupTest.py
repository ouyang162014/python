# coding=utf-8
from bs4 import BeautifulSoup
import re

# 待解析的html文本
html_doc = """
 <html><head><title>The Dormouse's story</title></head>
 <body>
 <p class="title"><b>The Dormouse's story</b></p>

 <p class="story">Once upon a time there were three little sisters; and their names were
 <a href="http://example.com/elsie" class="sister" id="link1">Elsie</a>,
 <a href="http://example.com/lacie" class="sister" id="link2">Lacie</a> and
 <a href="http://example.com/tillie" class="sister" id="link3">Tillie</a>;
 and they lived at the bottom of a well.</p>

 <p class="story">...</p>
 """

# 创建Soup对象
soup = BeautifulSoup(html_doc, 'html.parser', from_encoding='utf-8')
print '获取链接'
linkList=soup.find_all('a')
for link in linkList:
    print link.name,link['href'],link.get_text()

#只获取tillie这个链接
print '获取tillie链接'
linkNode=soup.find('a',href="http://example.com/tillie")
print linkNode.name,linkNode['href'],linkNode.get_text()

#正则表达式
print '正则表达式'
linkNode=soup.find('a',href=re.compile(r"lli"))
print linkNode.name,linkNode['href'],linkNode.get_text()

#更具class属性获取
print '获取P段落文字'
classNode=soup.find('p',class_='title')
print classNode.name,classNode.get_text()



