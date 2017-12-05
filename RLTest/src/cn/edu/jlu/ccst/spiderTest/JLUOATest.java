package cn.edu.jlu.ccst.spiderTest;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class JLUOATest implements PageProcessor {


    // 部分�?：抓取网站的相关配置，包括编码�?�抓取间隔�?�重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    // process是定制爬虫�?�辑的核心接口，在这里编写抽取�?�辑
    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来
        page.putField("date", page.getHtml().xpath("//div[@class='content-title fl']/span/a/text()").toString());
        page.putField("title", page.getHtml().xpath("//div[@class='content-title fl']/h2/text()").toString());
        page.putField("content", page.getHtml().xpath("//div[@id='vsb_content_2']/p/text()").all());
//        page.putField("content1", page.getHtml().xpath("//div[@class='content_font fontsize immmge']/text()").all());

        // 部分三：从页面发现后续的url地址来抓https://oa.jlu.edu.cn/defaultroot/PortalInformation!jldxList.action?channelId=179577
        // 部分三：从页面发现后续的url地址来抓https://oa.jlu.edu.cn/defaultroot/PortalInformation!jldxList.action?1=1&channelId=179577&startPage=3
        // 部分三：从页面发现后续的url地址来抓https://oa.jlu.edu.cn/defaultroot/PortalInformation!getInformation.action?title=%E5%85%B3%E4%BA%8E%E6%94%B6%E7%9C%8B%E4%B8%AD%E5%A4%AE%E7%94%B5%E8%A7%86%E5%8F%B0%E4%B9%9D%E5%A5%97%E9%A6%96%E6%92%AD%E7%BA%AA%E5%BD%95%E7%89%87%E3%80%8A%E9%BB%84%E5%A4%A7%E5%B9%B4%E3%80%8B%E7%9A%84%E9%80%9A%E7%9F%A5&id=4533896&categoryName=%E6%A0%A1%E5%86%85%E9%80%9A%E7%9F%A5&channelName=%E6%A0%A1%E5%86%85%E9%80%9A%E7%9F%A5&fromflag=login&channelId=179577&orgname=%E5%85%9A%E5%A7%94%E5%AE%A3%E4%BC%A0%E9%83%A8
       // page.addTargetRequests(page.getHtml().links().regex("(https://oa\\.jlu\\.edu\\.cn/defaultroot/PortalInformation!jldxList\\.action\\?1=1&channelId=179577&startPage=.+)").all());
        //page.addTargetRequests(page.getHtml().links().regex("(https://oa\\.jlu\\.edu\\.cn/defaultroot/PortalInformation!getInformation\\.action\\?title=.+)").all());
        
        page.addTargetRequests(page.getHtml().links().regex("(http://news\\.jlu\\.edu\\.cn/info/.+)").all());
        System.out.println(JLUOAFilePipeline.count);    
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        Spider.create(new JLUOATest())
                //�?"https://github.com/code4craft"�?始抓http://www.jlu.edu.cn/info/1095/42978.htm
                .addUrl("http://news.jlu.edu.cn/jdxw/xykx.htm")
                //.addUrl("https://oa.jlu.edu.cn/defaultroot/PortalInformation!jldxList.action?channelId=179577")
                .addPipeline(new JLUOAFilePipeline("/Users/user/Desktop/oa/"))
                //�?�?5个线程抓�?
                .thread(5)
                //启动爬虫
                .run();
        System.out.println(JLUOAFilePipeline.count);
    }
}
