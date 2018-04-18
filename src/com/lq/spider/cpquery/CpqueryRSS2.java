package com.lq.spider.cpquery;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.lq.spider.FileManager;
import com.lq.spider.ProxyTest;
import com.lq.spider.Util;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;  
  
public class CpqueryRSS2 {  
	private static Logger logger = Logger.getLogger("log4j.properties");
  
    public static void main(String[] args) {  
    	String workspace = "E:\\中间文件\\cpquery";
    	String numdir = workspace + "\\nums2";
    	String rsdir = workspace + "\\json";
    	String errordir = workspace + "\\error";
        File dir = new File(numdir);
		for (File f : dir.listFiles()) {

			if (f.isFile() && f.getName().endsWith(".trs")) {
				List<String> numlist = FileManager.readNums(f.getAbsolutePath(), "utf-8");
				List<Map> rslist = new ArrayList<Map>();
				int i = 0;
				long begin = System.currentTimeMillis();
				for (String an : numlist) {
					i++;
					try {
						List s = new CpqueryRSS2().parseRss(an, f);
						if (s != null) {
							Map m = new HashMap();
							m.put("an", an);
							m.put("list", s);
							m.put("cnt", s.size());
							rslist.add(m);
						}
					} catch (Exception e) {
						FileManager.write(an, errordir, errordir + "\\" +f.getName(), "utf-8", true);
					}
					if (i % 100 == 0) {
						System.out.println(i);
						if (rslist.size() > 0) {
							FileManager.write(Util.objToString(rslist), rsdir,
									rsdir + "\\" + f.getName() + "_" + i, "utf-8");
							rslist.clear();
						}
					}
				}
				if (rslist.size() > 0) {
					FileManager.write(Util.objToString(rslist), rsdir,
							rsdir + "\\" + f.getName() + "_" + (i+1), "utf-8");
				}
				System.out.println(f.getName() + " done :" + (System.currentTimeMillis() - begin)/1000 + "s");
			}
		}
    }  
    
//    public String extract(String httpurl) throws HttpException, IOException {
//		HttpClient client = new HttpClient();
//		client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
//
//		HttpMethod method = new GetMethod(httpurl);
//
//		method.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727");
//		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
//		// Mozilla/5.0 (Windows NT 6.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1
//		int status = client.executeMethod(method);
//		// HttpMethod s = method.getParams("data");
//		String responseBody = method.getResponseBodyAsString();
//		// citedpats = parseHtml(responseBody);
//
//		return responseBody;
//	}
    
    public static InputStream String2InputStream(String str) {
        ByteArrayInputStream stream = null;
        try {
            stream = new ByteArrayInputStream(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return stream;
    }
  
    public List<Map<String, Object>> parseRss(String an, File f) {  
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//      String rss = "http://news.baidu.com/n?cmd=1&class=civilnews&tn=rss&sub=0]http://news.baidu.com/n?cmd=1&class=civilnews&tn=rss&sub=0";  
    	String searchAn = an.toUpperCase().replace("CN", "").replace(".", "");
    	String rssUrl = "http://cpquery.sipo.gov.cn/txnrss_list.do?q:a="+searchAn+"&q:b=CN";  
          
        try {  
//            URL url = new URL(rss); 
        	String s = ProxyTest.doGetRequest(rssUrl);
        	if (s == null || s.contains("!doctype html")) {
        		return list;
        	}
            XmlReader reader = new XmlReader(String2InputStream(s));  
            SyndFeedInput input = new SyndFeedInput();  
            SyndFeed feed = input.build(reader);  
            // 得到Rss新闻中子项列表     
            List entries = feed.getEntries();  
            // 循环得到每个子项信息     
            
            for (int i = 0; i < entries.size(); i++) {  
            	Map<String, Object> info = new HashMap<String, Object>();
                SyndEntry entry = (SyndEntry) entries.get(i);  
                // 标题、连接地址、标题简介、时间是一个Rss源项最基本的组成部分    
//                info.put("an", an);
                String title = entry.getTitle();
                String link = entry.getLink();
                info.put("title", title);
                info.put("link", link);
                info.put("date", Util.getFormatTime(entry.getPublishedDate(), "yyyyMMdd"));
                if (title.contains("检索报告")) {
                	String searchreport = ProxyTest.doGetRequest(link);
                	Map<String, Object> obj = parseHtml(searchreport, an, f);
                	info.put("sreport", obj);
                }
                list.add(info);
            }  
        } catch (Exception e) {  
//            e.printStackTrace();  
        }  
        
        return list;
    }  
    
    public Map<String, Object> parseHtml(String html, String an, File f) {
    	Map<String, Object> obj = new HashMap<String, Object>();

		Document doc = Jsoup.parse(html);
		try {
			Elements tmps = doc.select("[name=record:jiansuobglx]");
			obj.put("jiansuobglx", tmps.first().text());
			tmps = doc.select("[name=record:shenqingh]");
			obj.put("shenqingh", tmps.first().text());
			tmps = doc.select("[name=record:shenqingr]");
			obj.put("shenqingr", tmps.first().text());
			tmps = doc.select("[name=record:shenqingren]");
			obj.put("shenqingren", tmps.first().text());
			tmps = doc.select("[name=record:zuizaoyxqr]");
			obj.put("zuizaoyxqr", tmps.first().text());
			tmps = doc.select("[name=record:quanliyqxs]");
			obj.put("quanliyqxs", tmps.first().text());
			tmps = doc.select("[name=record:shuomingsys]");
			obj.put("shuomingsys", tmps.first().text());
			tmps = doc.select("[name=record:jiansuoflh]");
			obj.put("jiansuoflh", tmps.first().text());
			tmps = doc.select("[name=record:jiansuobds]");
			obj.put("jiansuobds", tmps.first().text());
			
			try {
				Elements yinyonglx = doc.select("[name=record_zlwx:yinyonglx]");
				Elements guobie = doc.select("[name=record_zlwx:guobie]");
				Elements wenxianh = doc.select("[name=record_zlwx:wenxianh]");
				Elements wenxianlb = doc.select("[name=record_zlwx:wenxianlb]");
				Elements gongkairq = doc.select("[name=record_zlwx:gongkairq]");
				Elements ipcflh = doc.select("[name=record_zlwx:ipcflh]");
				Elements shejiqlyqc = doc.select("[name=record_zlwx:shejiqlyqc]");
				Elements yinyongymc = doc.select("[name=record_zlwx:yinyongymc]");
				
				if (yinyonglx != null && yinyonglx.size() > 0) {
					List<Map<String, Object>> zldbwx = new ArrayList<Map<String, Object>>();
					int size = yinyonglx.size();
					if ((yinyonglx.size() & 1) == 0 &&  wenxianh.get(0).text().equals(wenxianh.get(yinyonglx.size()/2).text())) {
						size = yinyonglx.size()/2;
					}
					for (int i = 0; i < size; i++) {
						Map<String, Object> dbwx = new HashMap<String, Object>();
						dbwx.put("yinyonglx", yinyonglx.get(i).text());
						dbwx.put("guobie", guobie.get(i).text());
						dbwx.put("wenxianh", wenxianh.get(i).text());
						dbwx.put("wenxianlb", wenxianlb.get(i).text());
						dbwx.put("gongkairq", gongkairq.get(i).text());
						dbwx.put("ipcflh", ipcflh.get(i).text());
						dbwx.put("shejiqlyqc", shejiqlyqc.get(i).text());
						dbwx.put("yinyongymc", yinyongymc.get(i).text());
						zldbwx.add(dbwx);
					}
					obj.put("zldbwx", zldbwx);
				}
			} catch (Exception e) {
				logger.error("zldbwx error:" + an);
			}
			
			
			try {
				Elements qkyinyonglx = doc.select("[name=record_qkwx:yinyonglx]");
				Elements wenjianmc = doc.select("[name=record_qkwx:wenjianmc]");
				Elements juanhao = doc.select("[name=record_qkwx:juanhao]");
				Elements qihao = doc.select("[name=record_qkwx:qihao]");
				Elements chubanrq = doc.select("[name=record_qkwx:chubanrq]");
				Elements zuozhe = doc.select("[name=record_qkwx:zuozhe]");
				Elements wenzhangbt = doc.select("[name=record_qkwx:wenzhangbt]");
				Elements qkshejiqlyqc = doc.select("[name=record_qkwx:shejiqlyqc]");
				Elements qkyinyongymc = doc.select("[name=record_qkwx:yinyongymc]");
				
				if (qkyinyonglx != null && qkyinyonglx.size() > 0) {
					List<Map<String, Object>> qkdbwx = new ArrayList<Map<String, Object>>();
					int size = qkyinyonglx.size();
					if ((qkyinyonglx.size() & 1) == 0 &&  wenzhangbt.get(0).text().equals(wenzhangbt.get(qkyinyonglx.size()/2).text())) {
						size = qkyinyonglx.size()/2;
					}
					for (int i = 0; i < size; i++) {
						Map<String, Object> dbwx = new HashMap<String, Object>();
						dbwx.put("yinyonglx", qkyinyonglx.get(i).text());
						dbwx.put("wenjianmc", wenjianmc.get(i).text());
						dbwx.put("juanhao", juanhao.get(i).text());
						dbwx.put("qihao", qihao.get(i).text());
						dbwx.put("chubanrq", chubanrq.get(i).text());
						dbwx.put("zuozhe", zuozhe.get(i).text());
						dbwx.put("wenzhangbt", wenzhangbt.get(i).text());
						dbwx.put("shejiqlyqc", qkshejiqlyqc.get(i).text());
						dbwx.put("yinyongymc", qkyinyongymc.get(i).text());
						qkdbwx.add(dbwx);
					}
					obj.put("qkdbwx", qkdbwx);
				}
			} catch (Exception e) {
				logger.error("qkdbwx error:" + an);
			}
			
			
//			<span name="record_sjwx:yinyonglx" title="A">A</span>
//			<span name="record_sjwx:wenjianmc" title="《中国重要经济树种》">《中国重要经济树种》</span>
//			<span name="record_sjwx:juanhao" title=""></span>
//			<span name="record_sjwx:qihao" title="第1版">第1版</span>
//			<span name="record_sjwx:chubanrq" title="1986-10-31">1986-10-31</span>
//			<span name="record_sjwx:zuozhe" title="柳鎏等">柳鎏等</span>
//			<span name="record_sjwx:wenzhangbt" title="种及品种">种及品种</span>
//			<span name="record_sjwx:shejiqlyqc" title="1-10">1-10</span>
//			<span name="record_sjwx:yinyongymc" title="第319页第3段">第319页第3段</span>

			try {
				Elements yinyonglx = doc.select("[name=record_sjwx:yinyonglx]");
				Elements wenjianmc = doc.select("[name=record_sjwx:wenjianmc]");
				Elements juanhao = doc.select("[name=record_sjwx:juanhao]");
				Elements qihao = doc.select("[name=record_sjwx:qihao]");
				Elements chubanrq = doc.select("[name=record_sjwx:chubanrq]");
				Elements zuozhe = doc.select("[name=record_sjwx:zuozhe]");
				Elements wenzhangbt = doc.select("[name=record_sjwx:wenzhangbt]");
				Elements shejiqlyqc = doc.select("[name=record_sjwx:shejiqlyqc]");
				Elements yinyongymc = doc.select("[name=record_sjwx:yinyongymc]");
				
				if (yinyonglx != null && yinyonglx.size() > 0) {
					List<Map<String, Object>> qkdbwx = new ArrayList<Map<String, Object>>();
					int size = yinyonglx.size();
					if ((yinyonglx.size() & 1) == 0 &&  wenzhangbt.get(0).text().equals(wenzhangbt.get(yinyonglx.size()/2).text())) {
						size = yinyonglx.size()/2;
					}
					for (int i = 0; i < size; i++) {
						Map<String, Object> dbwx = new HashMap<String, Object>();
						dbwx.put("yinyonglx", yinyonglx.get(i).text());
						dbwx.put("wenjianmc", wenjianmc.get(i).text());
						dbwx.put("juanhao", juanhao.get(i).text());
						dbwx.put("qihao", qihao.get(i).text());
						dbwx.put("chubanrq", chubanrq.get(i).text());
						dbwx.put("zuozhe", zuozhe.get(i).text());
						dbwx.put("wenzhangbt", wenzhangbt.get(i).text());
						dbwx.put("shejiqlyqc", shejiqlyqc.get(i).text());
						dbwx.put("yinyongymc", yinyongymc.get(i).text());
						qkdbwx.add(dbwx);
					}
					obj.put("sjdbwx", qkdbwx);
				}
			} catch (Exception e) {
				logger.error("sjdbwx error:" + an);
			}
			
			
			
//			try {
//				Elements sjelements = doc.select("#sjwxid").first().select("tr");
//				if (sjelements.size() > 1) {
//					FileManager.write(an, f.getParent() + "\\sjwx", f.getParent() + "\\sjwx\\"+f.getName(), "utf-8", true);
//				}
//			} catch (Exception e) {
//				
//			}
			
//			yinyonglx = doc.select("[name=record_sjwx:yinyonglx]");
//			guobie = doc.select("[name=record_sjwx:guobie]");
//			wenxianh = doc.select("[name=record_sjwx:wenxianh]");
//			wenxianlb = doc.select("[name=record_sjwx:wenxianlb]");
//			gongkairq = doc.select("[name=record_sjwx:gongkairq]");
//			ipcflh = doc.select("[name=record_sjwx:ipcflh]");
//			shejiqlyqc = doc.select("[name=record_sjwx:shejiqlyqc]");
//			yinyongymc = doc.select("[name=record_sjwx:yinyongymc]");
//			
//			if (yinyonglx != null && yinyonglx.size() > 0) {
//				List<Map<String, Object>> sjdbwx = new ArrayList<Map<String, Object>>();
//				for (int i = 0; i < yinyonglx.size(); i++) {
//					Map<String, Object> dbwx = new HashMap<String, Object>();
//					dbwx.put("yinyonglx", yinyonglx.get(i).text());
//					dbwx.put("guobie", guobie.get(i).text());
//					dbwx.put("wenxianh", wenxianh.get(i).text());
//					dbwx.put("wenxianlb", wenxianlb.get(i).text());
//					dbwx.put("gongkairq", gongkairq.get(i).text());
//					dbwx.put("ipcflh", ipcflh.get(i).text());
//					dbwx.put("shejiqlyqc", shejiqlyqc.get(i).text());
//					dbwx.put("yinyongymc", yinyongymc.get(i).text());
//					sjdbwx.add(dbwx);
//				}
//				obj.put("sjdbwx", sjdbwx);
//			}
			
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println(an);
			obj = null;
		}
		
		return obj;
	}
}  