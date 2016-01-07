#coding:utf8
#解析html文档
import re
import urlparse

from bs4 import BeautifulSoup


class HtmlParser(object):
    def parser(self,pageUrl,htmlCont):
        #判断是否为空
        if pageUrl is None or htmlCont is None:
            return None
        #获取BeautifulSoup对象
        soup=BeautifulSoup(htmlCont,'html.parser',from_encoding='utf-8')
        #获取新的urls
        newUrls=self.getNewUrls(pageUrl,soup)
        #获取新的信息
        newData=self.getNewData(pageUrl,soup)
        return newUrls,newData

    #获取页面中其它url
    def getNewUrls(self, pageUrl, soup):
        newUrls=set()
        #通过正则表达式获取所有的url
        links=soup.find_all('a',href=re.compile(r"/view/\d.html"))
        for  link in links:
            #获取新的url
            newUrl=link['href']
            newFullUrl=urlparse.urljoin(pageUrl,newUrl)
            newUrls.add(newFullUrl)
        return newUrls

    def getNewData(self, pageUrl, soup):
        #存储信息
        resData={}
        #存储url信息
        resData['url']=pageUrl
        # <dd class="lemmaWgt-lemmaTitle-title"><h1>Python</h1>
        titleNode=soup.find("dd",class_="lemmaWgt-lemmaTitle-title").find("h1")
        #存储title信息
        resData['title']=titleNode.get_text()
        #<div class="para"
        paraNode=soup.find("div",class_="para")
        #存储para信息
        resData['para']=paraNode.get_text()
        return resData

