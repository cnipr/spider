package com.lq.spider.db;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class Start {

	public static void main(String[] args) {
		String[] tables = { "patent_after_2007_13_30w", "patent_after_2007_14_30w", "patent_after_2007_15_30w" };
		for (int i = 0; i < tables.length; i++) {
			int threadNums = 30;
			CountDownLatch cdl = new CountDownLatch(threadNums);
			int first = 0;
			int last = 30 * 10000;
			for (int j = 0; j < threadNums; j++) {
				String threadName = "Thread" + (j + 1);
				long from = first + (last - first) / threadNums * j;
				long to = first + (last - first) / threadNums * (j + 1);
				System.out.println(new Date() + threadName + "," + from + "," + to);
				UpdateThread thread = new UpdateThread(threadName, from, to, 5000, tables[i], cdl);
				thread.start();
			}
			try {
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
		}
	}

}
