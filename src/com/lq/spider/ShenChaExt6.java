package com.lq.spider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ShenChaExt6 {
	private HttpClient client = null;

	public String extract(String an) throws HttpException, IOException, InterruptedException {
		String citedpats = null;

		String httpurl = "http://www.sipop.cn/patent-interface-search/patentDetail/queryAuditInfo?p_application_docNum="
				+ an;
//		httpurl = "http://localhost:9000/shencha.json";
		// String CC = pn.substring(0, 2);
		// int startidx = pn.indexOf("(");
		// String KC = pn.substring(startidx + 1, pn.length() - 1);
		// String NR = pn.substring(2, startidx) + KC;
		// String date = pd.replace(".", "");
		// httpurl = httpurl + "&CC=" + CC + "&NR=" + NR + "&KC="
		// + KC + "&date=" + date;
		// System.out.println(httpurl);
		client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);

		HttpMethod method = new GetMethod(httpurl);

		method.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727");
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");  
		// Mozilla/5.0 (Windows NT 6.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1
		int status = client.executeMethod(method);
		String responseBody = null;
		if (200 == status) {
			responseBody = method.getResponseBodyAsString();
		}else {
			System.out.println("status=" + status + "sleeping..."); 
			Thread.sleep(3 * 1000);
			extract(an);
		}

		return responseBody;
	}

	public static void main(String[] args) throws InterruptedException {
		try {
			String s = new ShenChaExt6().extract("CN201410225664.8"); 
			System.out.println(s);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
