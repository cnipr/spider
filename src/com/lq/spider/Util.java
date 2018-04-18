package com.lq.spider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class Util {
	private static Properties _properties = null;
	
	public static String getProperty(String key) {
		if (_properties == null) {
			try {
				InputStream ins = Util.class
						.getResourceAsStream("/app.properties");
				_properties = new Properties();
				_properties.load(ins);
			} catch (Exception ex) {
				_properties = null;
			}
		}

		return _properties.getProperty(key);
	}
	
	public static String getFormatTime(Date date, String format) {
		SimpleDateFormat sy1 = new SimpleDateFormat(format);
		return sy1.format(date);
	}
	
	public static Map<?, ?> StringToObj(String s) {
		Map<?, ?> map = null;

		ObjectMapper mapper = new ObjectMapper();

		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT,
				Boolean.TRUE);
		try {
			map = mapper.readValue(s, Map.class);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}
	
	public static String objToString(Object o) {
		String json = "";

		ObjectMapper mapper = new ObjectMapper();

		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT,
				Boolean.TRUE);
		try {
			json = mapper.writeValueAsString(o).replace("\r\n", "");
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return json;
	}
	
	public static String JPPNFormat(String pn, String type) {
		if (pn.length() == 7) {// A��K2��K4��K5
			if (type.equals("A") || type.equals("K2") || type.equals("K4")
					|| type.equals("K5")) {
				pn = "H0" + pn;
			} else if (type.equals("B")) {

			}
		}

		return pn;
	}

	public static int writeFile(String path, String filename, String content,
			boolean appandFlag) {
		int rs = 1;

		File filepath = new File(path);
		if (!filepath.exists()) {
			if (!filepath.mkdirs()) {
				rs = 0;
			}
		}

		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(path + filename,
					appandFlag), "UTF-8");
			out.write(content);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			rs = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			rs = 0;
		} catch (IOException e) {
			e.printStackTrace();
			rs = 0;
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				rs = 0;
			}
		}

		return rs;
	}
	
	public static String numberFormat(String number) {
		String num = "";
		
		try {
			if (!number.contains("(")) {
				String s = number;
				int idx = 0;
				for (int i = s.length(); i > 0; i--) {
					if (Character.isLetter(s.charAt(i - 1))) {
						idx = i;
						break;
					}
				}
				
				num = s.substring(0,idx-1) + "(" + s.substring(idx-1, number.length()) + ")";
			} else {
				num = number;
			}
		} catch (Exception e) {
			num = "";
		}
		
		return num;
	}

	public static void main(String[] args) {

	}
}
