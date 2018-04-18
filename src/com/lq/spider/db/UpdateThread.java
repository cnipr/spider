package com.lq.spider.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class UpdateThread  extends Thread{
	private HttpClient client = new HttpClient();
	private String threadName;
	private Long from;
	private Long to;
	private int batchSize;
	private String tableName;
	
	private CountDownLatch cdl;  
	
	public static void main(String[] args) {
		String[] tables = { 
				"patent_after_2007_36_30w",
				"patent_after_2007_37_30w",
				"patent_after_2007_38_30w",
				"patent_after_2007_39_30w",
				"patent_after_2007_40_30w",
				"patent_after_2007_41_30w",
				"patent_after_2007_42_30w",
				"patent_after_2007_43_30w",
				"patent_after_2007_44_30w",
				"patent_after_2007_45_30w",
				"patent_after_2007_46_30w",
				"patent_after_2007_47_30w",
				"patent_after_2007_48_30w",
				"patent_after_2007_49_30w",
				"patent_after_2007_50_30w",
				"patent_after_2007_51_30w",
				"patent_after_2007_52_30w",
				"patent_after_2007_53_30w",
				"patent_after_2007_54_30w",
				"patent_after_2007_55_30w",
				"patent_after_2007_56_30w",
				"patent_after_2007_57_30w",
				"patent_after_2007_14_30w",
				"patent_after_2007_15_30w",
				"patent_after_2007_16_30w",
				"patent_after_2007_17_30w",
				"patent_after_2007_18_30w",
				"patent_after_2007_19_30w"};
		for (int i = 0; i < tables.length; i++) {
			long start = System.currentTimeMillis();
			int threadNums = 30;
			CountDownLatch cdl = new CountDownLatch(threadNums);
			int first = 0;
			int last = 30 * 10000;
			for (int j = 0; j < threadNums; j++) {
				String threadName = "Thread" + (j + 1);
				long from = first + (last - first) / threadNums * j;
				long to = first + (last - first) / threadNums * (j + 1);
				System.out.println(new Date() + threadName + "," + from + "," + to);
				UpdateThread thread = new UpdateThread(threadName, from, to, 100, tables[i], cdl);
				thread.start();
			}
			try {
				cdl.await();				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
			long consume = (System.currentTimeMillis() - start) / 1000;
			System.out.println("############################################" 
			+ new Date() + "," + tables[i] + "_complete!############################################耗时：" + consume);  
		}
	}

	
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
				
				updateJson(startIndex, endIndex,tableName);	
				
				long consume = System.currentTimeMillis() - start;
//				System.out.println(new Date() + "["+ threadName + "]" + startIndex + "-" + endIndex + "耗时:" + consume); 
			}		
			System.out.println(new Date() + "["+ threadName + "]" + tableName + "_complete!");  
			cdl.countDown();  
//		System.out.println(new Date() + "["+ threadName +"]END" + to);
	}


	public String extract(String an) throws HttpException, IOException, InterruptedException {
		String httpurl = "http://www.sipop.cn/patent-interface-search/patentDetail/queryAuditInfo?p_application_docNum="
				+ an;
//		httpurl = "http://localhost:9000/shencha.json";
		client.getHttpConnectionManager().getParams().setConnectionTimeout(100000);
		HttpMethod method = new GetMethod(httpurl);
		method.addRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 2.0.50727");
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 100000);
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
			System.out.println(new Date() + "[error]" + threadName + "," + startIndex + "," + endIndex + "," + tableName + e.getMessage()); 
//			System.out.print("exception:" + e.getMessage());
//			e.printStackTrace();
		}
	}
	
	/*public void updateJson(long startIndex, long endIndex, String tableName) {
		System.out.println(new Date() + threadName +  "," + startIndex + "," + endIndex + "," + tableName + "...Start"); 
		try {
			Thread.sleep(2000);
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


	public UpdateThread() {
		super();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public UpdateThread(String threadName, Long from, Long to, int batchSize, String tableName, CountDownLatch cdl) {
		super();
		this.threadName = threadName;
		this.from = from;
		this.to = to;
		this.batchSize = batchSize;
		this.tableName = tableName;
		this.cdl = cdl;
	}

}
