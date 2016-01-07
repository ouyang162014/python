#coding:utf-8
#下载类
import urllib2

class HtmlDownloader(object):
    #下载所需要信息
    def downloadData(self, newUrl):
        #判断url是否为none
        if newUrl is None:
            return None
        #获取相应的信息
        response=urllib2.urlopen(newUrl)
        #判断是否获取到信息
        if response.getcode()!=200:
            #数据获取失败
            return None
        #返回读取到的信息
        return response.read()
