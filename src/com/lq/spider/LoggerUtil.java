package com.lq.spider;

import org.apache.log4j.Logger;

/**
 * <p>
 * Title: FileServerLogger
 * </p>
 * <p>
 * Description: 日志
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: cnipr
 * </p>
 * 
 * @author lq
 * @version 1.0
 */
public class LoggerUtil {

	/**
	 * 
	 */
	public LoggerUtil() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static Logger logger = Logger.getLogger("log4j.properties");

	public static void LogInfo(String msg) {
		logger.info("[INFO] " + msg);
	}

	public static void LogDebug(String msg) {
		logger.debug("[DEBUG] " + msg);

	}

	public static void LogError(String msg) {
		logger.error("[ERROR] " + msg);
	}
	
	public static void LogMark() {
		logger.error("---------------------------------------------------------------");
	}
	
	public static void LogSMS(String msg, int errorCode) {
		logger.error("[SMS] [" + errorCode + "] " + msg);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void main(String[] args) {
		LoggerUtil.LogError("Hello,I am valley.");
		LoggerUtil.LogInfo("this is the test.");
	}
}
