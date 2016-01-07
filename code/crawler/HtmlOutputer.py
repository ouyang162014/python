#coding:utf8
class HtmlOutputer(object):
    def __init__(self):
        self.datas=[]

    #收集数据
    def collectData(self, data):
        if data is None:
            return
        self.datas.append(data)

    #输出数据
    def outputHtml(self):
        #打开输出文件
        fout=open('output.html','w')
        #创建HTML标签
        fout.write("<html>")
        #创建body标签
        fout.write("<body>")
        #创建table标签
        fout.write("<table>")
        for data in self.datas:
            #创建tr标签
            fout.write("<tr>")
            #打印url信息
            fout.write("<td>%s</td>" %data['url'])
            #打印title信息
            fout.write("<td>%s</td>" %data['title'].encode('utf-8'))
            #打印para信息
            fout.write("<td>%s</td>" %data['para'].encode('utf-8'))
            fout.write("</tr>")
        fout.write("</table>")
        fout.write("</body>")
        fout.write("</html>")
