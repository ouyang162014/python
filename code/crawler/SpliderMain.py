#coding:utf8

from crawler import UrlManager
from crawler import HtmlDownloader
from crawler import HtmlParser
from crawler import HtmlOutputer

class SpliderMain(object):
    def __init__(self):
        #创建url管理器
        self.urls=UrlManager.UrlManager()
        #创建url下载器
        self.downloader=HtmlDownloader.HtmlDownloader()
        #创建解析器
        self.parser=HtmlParser.HtmlParser()
        #创建上传器
        self.outputer=HtmlOutputer.HtmlOutputer()

    def craw(self,rootUrl):
        #记录爬取的数目
        count=1
        #添加入口url
        self.urls.addNewUrl(rootUrl)
        #循环rootUrl
        while self.urls.hasNewUrl():
            try:
                #获取new url
                newUrl=self.urls.getNewUrl()
                #打印信息
                print 'craw %d ：%s'%(count,newUrl)
                #下载url对应的网页信息
                htmlCont=self.downloader.downloadData(newUrl)
                #获取新的url
                newUrls,newData=self.parser.parse(newUrl,htmlCont)
                #获取的url添加到urls中
                self.urls.addNewUrls(newUrls)
                #收集数据
                self.outputer.collectData(newData)
                #获取100条
                if count==100:
                    break
                count+=1
            except:
                print 'craw failed'


#创建main函数
if __name__=="__main__":
    #创建入口url
    rootUrl='http://baike.baidu.com/'
    objSpider=SpliderMain()
    objSpider.craw(rootUrl)
