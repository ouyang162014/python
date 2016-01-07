#coding:utf8
class UrlManager(object):
    #初始化
    def __init__(self):
        #新的url
        self.newUrls=set()
        #已经读取的url
        self.oldUrls=set()

    #添加单个新的url
    def addNewUrl(self, newUrl):
        if newUrl is None:
            return
        if newUrl not in self.newUrls and newUrl not in self.oldUrls:
            self.newUrls.add(newUrl)

    #批量添加urls
    def addNewUrls(self, newUrls):
        #如果url为空
        if newUrls is None or len(newUrls)==0:
            return
        for newUrl in newUrls:
            #单个添加
            self.newUrls.addNewUrl(newUrl)

    #获取url
    def getNewUrl(self):
        #获取新的url
        newUrl=self.newUrls.pop()
        #将url添加到已经读取的url中
        self.oldUrls.add(newUrl)
        #返回获取的url
        return newUrl

    #判断是否存在url
    def hasNewUrl(self):
        return len(self.newUrls)!=0

