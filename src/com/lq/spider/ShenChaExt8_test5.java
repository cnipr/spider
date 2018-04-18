package com.lq.spider;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class ShenChaExt8_test5  extends Thread{
	private HttpClient client = new HttpClient();
	private String threadName;
	private Long from;
	private Long to;
	private int batchSize;
	private static String[] tables = {
			"patent_after_2007_16_30w",
			"patent_after_2007_17_30w",
			"patent_after_2007_18_30w",
			"patent_after_2007_19_30w"};
	@Override
	public void run() {
//		System.out.println(new Date() + "["+ threadName +"]START:" + from);
		long batchNum = (to - from + batchSize - 1)/batchSize;
		for (int i = 0; i < batchNum; i++) {
			long startIndex = from + batchSize*i;
			long endIndex = startIndex + batchSize;
			if (endIndex > to) {
				endIndex = to;
			}
			long start = System.currentTimeMillis();
			
			for(int j=0;j<tables.length;j++) {
				updateJson(startIndex, endIndex,tables[j]);				
			}
			
			long consume = System.currentTimeMillis() - start;
			System.out.println(new Date() + "["+ threadName + "]" + startIndex + "-" + endIndex + "耗时:" + consume); 
		}
//		System.out.println(new Date() + "["+ threadName +"]END" + to);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int threadNums = 30;
		int first = 0;
//		int last = 319,5760;322,1667	;1698,3244;1701,2149;1667,0151;1640,4953;1598,8663;1552,7443;1528,4464
		/**
		 * 1. create table patent_after_2007_06_60w as SELECT * FROM patent_after_2007 limit 0,600000;
		 * 2. create table patent_after_2007_08_30w as SELECT * FROM patent_after_2007 limit 600000,300000;
		 * 3. create table patent_after_2007_09_40w as SELECT * FROM patent_after_2007 limit 900000,400000;
		 * 4. create table patent_after_2007_11_30w as SELECT * FROM patent_after_2007 limit 1300000,300000;
		 * 5. create table patent_after_2007_12_30w as SELECT * FROM patent_after_2007 limit 1600000,300000;
		 * 
		   6.  create table patent_after_2007_13_30w as SELECT * FROM patent_after_2007 limit 1900000,300000;
		 * 7.  create table patent_after_2007_14_30w as SELECT * FROM patent_after_2007 limit 2200000,300000;
		 * 8.  create table patent_after_2007_15_30w as SELECT * FROM patent_after_2007 limit 2500000,300000;
		 * 9.  create table patent_after_2007_16_30w as SELECT * FROM patent_after_2007 limit 2800000,300000;
		 * 10. create table patent_after_2007_17_30w as SELECT * FROM patent_after_2007 limit 3100000,300000;
		 * 
		 * 
		 * ALTER TABLE  patent_after_2007_15_30w ADD PRIMARY KEY(id);
		 */
		int last = 30 * 10000;	
//		last = 10;
		for (int i = 0; i < threadNums; i++) {
			String threadName = "Thread" + (i+1);
			long from = first + (last - first)/threadNums * i;
			long to = first + (last - first)/threadNums * (i+1);
			System.out.println(new Date() + threadName + "," + from + "," + to);
			ShenChaExt8_test5 thread = new ShenChaExt8_test5(threadName,from,to,100);
			thread.start();
		}
		
//		new PostgresTest().getAll();
	}

	public String extract(String an) throws HttpException, IOException, InterruptedException {
		String httpurl = "http://www.sipop.cn/patent-interface-search/patentDetail/queryAuditInfo?p_application_docNum="
				+ an;
//		httpurl = "http://localhost:9000/shencha.json";
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		HttpMethod method = new GetMethod(httpurl);
		method.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727");
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");  
		int status = client.executeMethod(method);
		String responseBody = null;
		if (200 == status) {
			responseBody = method.getResponseBodyAsString();
		}
		return responseBody;
	}
	
	public void updateJson(long startIndex, long endIndex, String tableName) {
//		System.out.println("开始更新:" + startIndex + "-" + endIndex);
		long start = System.currentTimeMillis();
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url = "jdbc:mysql://127.0.0.1/data?useUnicode=true&characterEncoding=utf-8";
			Connection con = DriverManager.getConnection(url, "root", "root");	
			Statement st = con.createStatement();
			long limit = endIndex - startIndex;
			ResultSet rs = st.executeQuery("select id,an from " + tableName + " limit " + startIndex + "," + limit);
			PreparedStatement ps = con.prepareStatement("update " + tableName + " set shencha = ? where id = ?");
			final int batchSize = 10;
			int count = 0;
			while (rs.next()) {
				int id = rs.getInt(1);
				String an = rs.getString(2);
				String shencha = extract(an);
				ps.setString(1 , shencha);
				ps.setInt(2 , id);
				ps.addBatch();
				if(++count % batchSize == 0) {
					ps.executeBatch();
//					System.out.println(new Date() + threadName + "[update]" + count); 
			    }
			}
			ps.executeBatch();  // insert remaining records
			
			long consume = (System.currentTimeMillis() - start) / 1000;
			System.out.println(new Date() + "[update]" + threadName + "," + startIndex + "," + endIndex + "," + tableName + "," + count + "耗时:" + consume); 
			rs.close();
			st.close();
			ps.close();
			con.close();
		} catch (Exception e) {
//			System.out.print("exception:" + e.getMessage());
//			e.printStackTrace();
		}
	}
	
	/*public void updateJson(long startIndex, long endIndex, String tableName) {
		System.out.println(new Date() + threadName +  "," + startIndex + "," + endIndex + "," + tableName + "...Start"); 
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(new Date() + threadName + "," + startIndex + "," + endIndex + "," + tableName + "...Done"); 
	}*/
	
	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public Long getFrom() {
		return from;
	}

	public void setFrom(Long from) {
		this.from = from;
	}

	public Long getTo() {
		return to;
	}

	public void setTo(Long to) {
		this.to = to;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public ShenChaExt8_test5(String threadName, Long from, Long to, int batchSize) {
		super();
		this.threadName = threadName;
		this.from = from;
		this.to = to;
		this.batchSize = batchSize;
	}

	public ShenChaExt8_test5() {
		super();
	}

}
