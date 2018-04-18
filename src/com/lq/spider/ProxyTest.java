package com.lq.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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

public class ProxyTest implements Callable<PatentInfo> {
	private PatentInfo patentInfo;

	public ProxyTest(PatentInfo patentInfo) {
		super();
		this.patentInfo = patentInfo;
	}

	public PatentInfo getPatentInfo() {
		return patentInfo;
	}

	public void setPatentInfo(PatentInfo patentInfo) {
		this.patentInfo = patentInfo;
	}

	public ProxyTest() {
		super();
	}

	

	// 代理服务器
    final static String proxyHost = "http-dyn.abuyun.com";
    final static Integer proxyPort = 9020;

    // 代理隧道验证信息
    final static String proxyUser = "HM60V1147Y5OD6YD";
    final static String proxyPass = "F3A874CC5948D597";

    // IP切换协议头
    final static String switchIpHeaderKey = "Proxy-Switch-Ip";
    final static String switchIpHeaderVal = "yes";

    private static PoolingHttpClientConnectionManager cm = null;
    private static HttpRequestRetryHandler httpRequestRetryHandler = null;
    private static HttpHost proxy = null;

    private static CredentialsProvider credsProvider = null;
    private static RequestConfig reqConfig = null;

    static {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();

        Registry registry = RegistryBuilder.create()
            .register("http", plainsf)
            .register("https", sslsf)
            .build();

        cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(20);
        cm.setDefaultMaxPerRoute(5);

        proxy = new HttpHost(proxyHost, proxyPort, "http");

        credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUser, proxyPass));

        reqConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(30000)
            .setConnectTimeout(30000)
            .setSocketTimeout(30000)
            .setExpectContinueEnabled(false)
            .setProxy(new HttpHost(proxyHost, proxyPort))
            .build();
    }

    public static StringBuffer doRequest(HttpRequestBase httpReq) {
    	StringBuffer response = new StringBuffer();
        CloseableHttpResponse httpResp = null;

        try {
            setHeaders(httpReq);

            httpReq.setConfig(reqConfig);

            CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultCredentialsProvider(credsProvider)
                .build();

            AuthCache authCache = new BasicAuthCache();
            authCache.put(proxy, new BasicScheme());

            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            //httpResp = httpClient.execute(httpReq, localContext);
            httpResp = httpClient.execute(proxy, httpReq, localContext);

            int statusCode = httpResp.getStatusLine().getStatusCode();

            if (statusCode != 200) {
            	return doRequest(httpReq);
            } 
//            System.out.println(statusCode);

            BufferedReader rd = new BufferedReader(new InputStreamReader(httpResp.getEntity().getContent(),"utf-8"));

            String line = "";
            while((line = rd.readLine()) != null) {
            	response.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = doRequest(httpReq);
        } finally {
            try {
                if (httpResp != null) {
                    httpResp.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * 设置请求头
     *
     * @param httpReq
     */
    private static void setHeaders(HttpRequestBase httpReq) {
        httpReq.setHeader("Accept-Encoding", null);
        httpReq.setHeader(switchIpHeaderKey, switchIpHeaderVal);
    }

    public static StringBuffer doPostRequest(String begin, String num, String lan, String searchType, String searchInfo) {
    	StringBuffer sb = new StringBuffer();
    	try {
            // 要访问的目标页面
            HttpPost httpPost = new HttpPost("http://xlore.org/json/moreresult");

            // 设置表单参数
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("begin", begin));
            params.add(new BasicNameValuePair("num", num));
            params.add(new BasicNameValuePair("lan", lan));
            params.add(new BasicNameValuePair("searchType", searchType));
            params.add(new BasicNameValuePair("searchInfo", searchInfo));

            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

            sb = doRequest(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	return sb;
    }

    public static String doGetRequest(String url) {
        // 要访问的目标页面
//        String targetUrl = "https://test.abuyun.com/proxy.php";
        //String targetUrl = "http://proxy.abuyun.com/switch-ip";
        //String targetUrl = "http://proxy.abuyun.com/current-ip";
        StringBuffer s = new StringBuffer();
        try {
            HttpGet httpGet = new HttpGet(url);
            s = doRequest(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s.toString();
    }

    public static void main(String[] args) {
    	String url = "http://www.sipop.cn/patent-interface-search/patentDetail/queryAuditInfo?p_application_docNum=CN201410225664.8";
        String json = doGetRequest(url);
        System.out.println(json);
        //doPostRequest();
    }

	@Override
	public PatentInfo call() throws Exception {
	        try {
	        	String httpurl = "http://www.sipop.cn/patent-interface-search/patentDetail/queryAuditInfo?p_application_docNum=" + patentInfo.getAn();
	            HttpGet httpGet = new HttpGet(httpurl);
	            String shencha = doRequest(httpGet).toString();
	            patentInfo.setShencha(shencha);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		return patentInfo;
	}
}
