package com.lq.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

public class ProxyNull implements Callable<PatentInfo> {
	private PatentInfo patentInfo;
	private HttpClient client;

	public ProxyNull(PatentInfo patentInfo) {
		super();
		this.patentInfo = patentInfo;
	}

	public PatentInfo getPatentInfo() {
		return patentInfo;
	}

	public void setPatentInfo(PatentInfo patentInfo) {
		this.patentInfo = patentInfo;
	}

	public ProxyNull() {
		super();
	}
    public ProxyNull(PatentInfo patentInfo, HttpClient client) {
		super();
		this.patentInfo = patentInfo;
		this.client = client;
	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}
	
	public String extract(String an) throws HttpException, IOException {
		String httpurl = "http://www.sipop.cn/patent-interface-search/patentDetail/queryAuditInfo?p_application_docNum="
				+ an;
		httpurl = "http://localhost:9000/shencha.json";
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		HttpMethod method = new GetMethod(httpurl);
		method.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727");
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");  
		int status = client.executeMethod(method);
		String responseBody = method.getResponseBodyAsString();
		return responseBody;
	}

	public static void main(String[] args) throws Exception, Exception {
    	String url = "http://www.sipop.cn/patent-interface-search/patentDetail/queryAuditInfo?p_application_docNum=CN201410225664.8";
        //doPostRequest();
    	new ProxyNull().extract("CN201410225664.8"); 
    }

	@Override
	public PatentInfo call() throws Exception {
	        try {
	            String shencha = extract(patentInfo.getAn());
	            patentInfo.setShencha(shencha);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		return patentInfo;
	}
}
