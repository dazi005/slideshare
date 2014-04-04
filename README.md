# SlideShare 

#项目介绍
---
本项目实现对 www.slideshare.net 网站中的PPT的抓取。
由于本身网站的流量需要翻墙，在翻墙后网站又无法进行注册和登录，无法下载网站中的PPT，所以开发此程序。

#实现原理
---
使用HttpClient对网站页面的图片抓取到本地，然后使用Apache POI将图片重新组合成PPT文件。
由于国内无法直接浏览slideshare网站，所以，程序需要使用代理，代理配置在ParseHtml.java 中进行配置。


#代码结构
---
* libs：程序依赖包
    * httpclient 网络通信包，实现网页抓取
    * poi 操作ppt，excel，pdf文件的工具包
    * jsoup 解析html dom树工具
    * itext 操作pdf文件工具
* src: 源码包
    * SlideShare.java 主程序
	* ParseHtml.java 网页信息抓取，并解析出ppt文件名和网页中的图片的地址
	* ImageDownLoader.java 图片下载工具
        

##关于作者

* 网名 : 半日闲
* 邮件(wankunde#163.com, 把#换成@)

在使用中有任何问题，欢迎反馈给我
