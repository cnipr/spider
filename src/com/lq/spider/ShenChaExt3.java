package com.lq.spider;

import java.io.File;
import java.io.IOException;
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

public class ShenChaExt3 {
	private HttpClient client = null;

	public String extract(String an) throws HttpException, IOException {
		String citedpats = null;

		String httpurl = "http://www.sipop.cn/patent-interface-search/patentDetail/queryAuditInfo?p_application_docNum="
				+ an;
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
		// Mozilla/5.0 (Windows NT 6.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1
		int status = client.executeMethod(method);
		// HttpMethod s = method.getParams("data");
		String responseBody = method.getResponseBodyAsString();
		// citedpats = parseHtml(responseBody);

		return responseBody;
	}

	public String parseHtml(String html) {
		String citedpats = "";

		if (html.contains("An error has occurred")) {
			return "error";
		}

		Document doc = Jsoup.parse(html.replaceAll("&nbsp;", ""));
		Elements links = doc.select("td.publicationInfoColumn");
		StringBuffer sb = new StringBuffer();
		int i = 0;

		for (Element link : links) {
			i++;
			String linkText = link.text();
			sb.append(linkText.replace("Publication info: ", "").split(" ")[0] + ";");
			// citedpats = rowid + "###	" + linkText.replace("Publication info:
			// ", "").replace(" ", "###"));
			// System.out.println(linkText.replace("Publication info: ",
			// "").replace("&nbsp;", ""));
		}

		if (sb.length() > 0) {
			citedpats = sb.substring(0, sb.length() - 1) + "###" + i;
		}
		return citedpats;
	}

	public static void main(String[] args) {
		try {
			System.out.println(args[0] + args[1]);
			File dir = new File(args[0] + args[1]);
			for (File f : dir.listFiles()) {

				if (f.isFile() && f.getName().endsWith(".trs")) {
					List<String> numlist = FileManager.readNums(f.getAbsolutePath(), "utf-8");
					for (String an : numlist) {
						try {
							File tf = new File(args[0] + "result\\" + f.getName().split("\\.")[0] + "\\" + an.trim());
							if (tf != null && tf.exists()) {
								continue;
							}
							String s = new ShenChaExt3().extract(an);
							FileManager.write(s, args[0] + "result\\" + f.getName().split("\\.")[0],
									args[0] + "result\\" + f.getName().split("\\.")[0] + "\\" + an.trim(), "utf-8");
						} catch (Exception e) {
							FileManager.write(an, args[0] + "error\\", args[0] + "error\\"+f.getName(), "utf-8", true);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
