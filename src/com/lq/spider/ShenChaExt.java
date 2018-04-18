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

public class ShenChaExt {
	private HttpClient client = null;

	public String extract(String an) throws HttpException, IOException {
		String citedpats = null;
		
		/*  审查系统
		请求地址：
		http://cpquery.sipo.gov.cn/txnQueryPatentFileData.jdo
		请求头：
		Host: cpquery.sipo.gov.cn
		User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0
		Accept: application/json, text/javascript, *; q=0.01
		Accept-Language: zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3
		Accept-Encoding: gzip, deflate
		Content-Type: application/x-www-form-urlencoded
		X-Requested-With: XMLHttpRequest
		Referer: http://cpquery.sipo.gov.cn/txnQueryPatentFileData.do?select-key:shenqingh=2011103698755&select-key:zhuanlilx=1&select-key:backPage=http%3A%2F%2Fcpquery.sipo.gov.cn%2FtxnQueryOrdinaryPatents.do%3Fselect-key%3Ashenqingh%3D2011103698755%26select-key%3Azhuanlimc%3D%26select-key%3Ashenqingrxm%3D%26select-key%3Azhuanlilx%3D%26select-key%3Ashenqingr_from%3D%26select-key%3Ashenqingr_to%3D%26verycode%3D5%26inner-flag%3Aopen-type%3Dwindow%26inner-flag%3Aflowno%3D1501568979418&inner-flag:open-type=window&inner-flag:flowno=1501634268203
		Content-Length: 36
		Cookie: _gscu_1718069323=77977364zcg09517; _gscu_2029180466=77977365cmm1dg72; BSFIT_EXPIRATION=1490285295707; BSFIT_DEVICEID=SdjL8oWQ_6a9_7aDFirfjmKz_gihjG1Tc53sIy5ZIJpiCrjJ9FAMZFeD3tmrV0vGppxxzN6baoN93LZnfK9LlAThxep5WxJDnTYN5nj2IQYsI7knpQdvpnjMzU4Jl1D1Q9dqoy4RMJXgwvkw15XpWJJUIsm0-i4f; BSFIT_OkLJUJ=FCDpPEmf4wErTMuZIRD1IKZ1TLsmQ7r9; _va_id=d2c72ef5c9a2d808.1498631727.3.1501568642.1501568642.; _va_ref=%5B%22%22%2C%22%22%2C1501568642%2C%22https%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3DanhH5BKXIiGjX53gGiTaQLwnHhAI_hrDd0h9oRt9eSolzLHmzh_98MyrKpvxEfwv%26wd%3D%26eqid%3Da268d8a100004be30000000259801e73%22%5D; JSESSIONID=673e759100c554d51c4ffd9d6fde
		Connection: keep-alive
		Cache-Control: max-age=0
		请求主体：
		select-key%3Ashenqingh=2011103698755
		返回：
		{"error-code":"000000","result":[{"fid":"DA000011039440","rid":"1022529504","primary-key":"1022529504","filename":"2011-11-21 发明专利请求书","pid":"sqwj","showcont":"1","filecode":"110101","wenjianlx":"01","filedate":"20111121","shenqingh":"2011103698755"},{"fid":"DA000011039441","rid":"1022529505","primary-key":"1022529505","filename":"2011-11-21 权利要求书","pid":"sqwj","showcont":"2","filecode":"100001","wenjianlx":"01","filedate":"20111121","shenqingh":"2011103698755"},{"fid":"DA000011039442","rid":"1022529506","primary-key":"1022529506","filename":"2011-11-21 说明书","pid":"sqwj","showcont":"2","filecode":"100002","wenjianlx":"01","filedate":"20111121","shenqingh":"2011103698755"},{"fid":"DA000011039443","rid":"1022529507","primary-key":"1022529507","filename":"2011-11-21 说明书摘要","pid":"sqwj","showcont":"2","filecode":"100004","wenjianlx":"01","filedate":"20111121","shenqingh":"2011103698755"},{"rid":"sqwj","filename":"申请文件","pid":"0"},{"fid":"DA000011039444","rid":"1016552316","primary-key":"1016552316","filename":"2011-11-21 费用减缓请求书","pid":"zjwj","showcont":"1","filecode":"100008","wenjianlx":"02","filedate":"20111121","shenqingh":"2011103698755"},{"fid":"DA000011039445","rid":"1016552317","primary-key":"1016552317","filename":"2011-11-21 费用减缓证明","pid":"zjwj","showcont":"1","filecode":"100110","wenjianlx":"02","filedate":"20111121","shenqingh":"2011103698755"},{"fid":"DA000026126935","rid":"102102804158","primary-key":"102102804158","filename":"2012-12-19 实质审查请求书","pid":"zjwj","showcont":"1","filecode":"110401","wenjianlx":"02","filedate":"20121219","shenqingh":"2011103698755"},{"fid":"DA000035178016","rid":"102209172607","primary-key":"102209172607","filename":"2013-07-16 修改对照页","pid":"zjwj","showcont":"1","filecode":"100042","wenjianlx":"02","filedate":"20130716","shenqingh":"2011103698755"},{"fid":"DA000035178015","rid":"102209172606","primary-key":"102209172606","filename":"2013-07-16 意见陈述书","pid":"zjwj","showcont":"1","filecode":"100012","wenjianlx":"02","filedate":"20130716","shenqingh":"2011103698755"},{"fid":"DA000035178014","rid":"102209172605","primary-key":"102209172605","filename":"2013-07-16 权利要求书","pid":"zjwj","showcont":"2","filecode":"100001","wenjianlx":"02","filedate":"20130716","shenqingh":"2011103698755"},{"fid":"DA000037479491","rid":"102252008747","primary-key":"102252008747","filename":"2013-08-30 意见陈述书","pid":"zjwj","showcont":"1","filecode":"100012","wenjianlx":"02","filedate":"20130830","shenqingh":"2011103698755"},{"fid":"DA000037479490","rid":"102252008746","primary-key":"102252008746","filename":"2013-08-30 权利要求书","pid":"zjwj","showcont":"2","filecode":"100001","wenjianlx":"02","filedate":"20130830","shenqingh":"2011103698755"},{"fid":"DA000037479492","rid":"102252008748","primary-key":"102252008748","filename":"2013-08-30 修改对照页","pid":"zjwj","showcont":"1","filecode":"100042","wenjianlx":"02","filedate":"20130830","shenqingh":"2011103698755"},{"fid":"DA000037752609","rid":"102255762790","primary-key":"102255762790","filename":"2013-09-05 修改对照页","pid":"zjwj","showcont":"1","filecode":"100042","wenjianlx":"02","filedate":"20130905","shenqingh":"2011103698755"},{"fid":"DA000037752607","rid":"102255762788","primary-key":"102255762788","filename":"2013-09-05 权利要求书","pid":"zjwj","showcont":"2","filecode":"100001","wenjianlx":"02","filedate":"20130905","shenqingh":"2011103698755"},{"fid":"DA000037752608","rid":"102255762789","primary-key":"102255762789","filename":"2013-09-05 意见陈述书","pid":"zjwj","showcont":"1","filecode":"100012","wenjianlx":"02","filedate":"20130905","shenqingh":"2011103698755"},{"fid":"DA000042964580","rid":"102335216378","primary-key":"102335216378","filename":"2013-12-04 意见陈述书","pid":"zjwj","showcont":"1","filecode":"100012","wenjianlx":"02","filedate":"20131204","shenqingh":"2011103698755"},{"fid":"DA000042964581","rid":"102335216379","primary-key":"102335216379","filename":"2013-12-04 修改对照页","pid":"zjwj","showcont":"1","filecode":"100042","wenjianlx":"02","filedate":"20131204","shenqingh":"2011103698755"},{"fid":"DA000042964579","rid":"102335216377","primary-key":"102335216377","filename":"2013-12-04 权利要求书","pid":"zjwj","showcont":"2","filecode":"100001","wenjianlx":"02","filedate":"20131204","shenqingh":"2011103698755"},{"fid":"DA000043989414","rid":"102339593525","primary-key":"102339593525","filename":"2013-12-17 权利要求书","pid":"zjwj","showcont":"2","filecode":"100001","wenjianlx":"02","filedate":"20131217","shenqingh":"2011103698755"},{"fid":"DA000043989415","rid":"102339593526","primary-key":"102339593526","filename":"2013-12-17 意见陈述书","pid":"zjwj","showcont":"1","filecode":"100012","wenjianlx":"02","filedate":"20131217","shenqingh":"2011103698755"},{"fid":"DA000043989416","rid":"102339593527","primary-key":"102339593527","filename":"2013-12-17 修改对照页","pid":"zjwj","showcont":"1","filecode":"100042","wenjianlx":"02","filedate":"20131217","shenqingh":"2011103698755"},{"rid":"zjwj","filename":"中间文件","pid":"0"},{"fid":"GA000039897560","rid":"10110310326115","primary-key":"10110310326115","filename":"2013-01-09 发明专利申请进入实质审查阶段通知书","pid":"tzs","showcont":"2","filecode":"210307","wenjianlx":"03","filedate":"20130109","shenqingh":"2011103698755"},{"fid":"GA000044948879","rid":"10110401347038","primary-key":"10110401347038","filename":"2013-05-27 第一次审查意见通知书","pid":"tzs","showcont":"2","filecode":"210401","wenjianlx":"03","filedate":"20130527","shenqingh":"2011103698755"},{"fid":"GA000050434650","rid":"10110448955617","primary-key":"10110448955617","filename":"2013-09-29 第N次审查意见通知书","pid":"tzs","showcont":"2","filecode":"210403","wenjianlx":"03","filedate":"20130929","shenqingh":"2011103698755"},{"fid":"GA000054861692","rid":"10110463617691","primary-key":"10110463617691","filename":"2013-12-26 办理登记手续通知书","pid":"tzs","showcont":"2","filecode":"200602","wenjianlx":"03","filedate":"20131226","shenqingh":"2011103698755"},{"fid":"GA000128324283","rid":"10110540645782","primary-key":"10110540645782","filename":"2016-12-27 缴费通知书","pid":"tzs","showcont":"1","filecode":"200701","wenjianlx":"03","filedate":"20161227","shenqingh":"2011103698755"},{"fid":"GA000143458482","rid":"10110554841981","primary-key":"10110554841981","filename":"2017-07-27 专利权终止通知书","pid":"tzs","showcont":"2","filecode":"20070202","wenjianlx":"03","filedate":"20170727","shenqingh":"2011103698755"},{"rid":"tzs","filename":"通知书","pid":"0"},{"fid":"GA000054644922","rid":"101675154938","primary-key":"101675154938","filename":"授予发明专利权通知书","pid":"10110463617691","showcont":"2","filecode":"210413","wenjianlx":"0301","filedate":"","shenqingh":"2011103698755"},{"fid":"","rid":"101660603639","primary-key":"101660603639","filename":"2013-05-13 首次检索","pid":"10110401347038","filecode":"990101","wenjianlx":"06","filedate":"20130513","shenqingh":"2011103698755"},{"fid":"","rid":"101870805031","primary-key":"101870805031","filename":"2013-12-17 补充检索","pid":"10110463617691","filecode":"990102","wenjianlx":"06","filedate":"20131217","shenqingh":"2011103698755"}]}
		*/
		
		// 国家运营平台数据源
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
			// citedpats = rowid + "###" + linkText.replace("Publication info:
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
			File dir = new File("E:\\中间文件\\申请号单");
			for (File f : dir.listFiles()) {

				if (f.isFile() && f.getName().endsWith(".trs")) {
					List<String> numlist = FileManager.readNums("E:\\中间文件\\申请号单\\1985-1990.trs", "utf-8");
					for (String an : numlist) {
						try {
							String s = new ShenChaExt().extract(an);
							FileManager.write(s, "E:\\中间文件\\json\\" + f.getName().split("\\.")[0],
									"E:\\中间文件\\json\\" + f.getName().split("\\.")[0] + "\\" + an.trim(), "utf-8");
						} catch (Exception e) {
							FileManager.write(an, "E:\\中间文件\\error\\", "E:\\中间文件\\error\\"+f.getName(), "utf-8", true);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
