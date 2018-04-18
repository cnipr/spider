package com.lq.spider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class TestSpeed  extends Thread{
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url = "jdbc:mysql://127.0.0.1/data?useUnicode=true&characterEncoding=utf-8";
			Connection con = DriverManager.getConnection(url, "root", "root");	
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select an from patent_after_2007 where db='FMZL' and pd = '2016.01.06' limit 20");
			List<String> anList = new ArrayList<String>();
			while (rs.next()) {
				String an = rs.getString(1);
				anList.add(an);
			}
			long start = System.currentTimeMillis();
			List<Future<PatentInfo>> futureList = callHttp(anList);
			//取出数据
			for (Iterator<Future<PatentInfo>> iterator = futureList.iterator(); iterator.hasNext();) {
				Future<PatentInfo> future = (Future<PatentInfo>) iterator.next();
				PatentInfo patentInfo = future.get();
				System.out.println(patentInfo.getAn()+"  ," + patentInfo.getShencha());
			}
			long consume = System.currentTimeMillis() - start;
			System.out.println("耗时:" + consume); 
			rs.close();
			st.close();
			con.close();
		} catch (Exception e) {
			System.out.print("exception:" + e.getMessage());
			e.printStackTrace();
		}
	}

	public static List<Future<PatentInfo>> callHttp(List<String> anList) {
		// 创建一个线程池
		ExecutorService pool = Executors.newFixedThreadPool(5);
		//创建结果集
		List<Future<PatentInfo>> futureList = new ArrayList<Future<PatentInfo>>();
		for (int i = 0; i < anList.size(); i++) {
			String an = anList.get(i);
			PatentInfo patentInfo = new PatentInfo();
			patentInfo.setAn(an);
			// 创建有返回值的任务
			Callable<PatentInfo> call = new ProxyTest(patentInfo);
			// 执行任务并获取Future对象
			Future<PatentInfo> future = pool.submit(call);
			futureList.add(future);
		}
		// 关闭线程池
		pool.shutdown();
		return futureList;
	}

}
