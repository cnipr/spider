package com.lq.spider;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.NameValuePair;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.params.HttpMethodParams;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class RelatedwordsExt {
	
//	private HttpClient client = new HttpClient();
//	private int maxid = 0;
	
	public RelatedwordsExt() {
//		GetMethod getmethod = new GetMethod("http://xlore.org/locale.json?request_locale=ch&url=/index&query_string=");
//		try {
//			client.executeMethod(getmethod);
//		} catch (HttpException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		String strMaxid = FileManager.read(Util.getProperty("cfgdir") + "maxid.cfg", "utf-8");
//		if (!strMaxid.equals("")) {
//			maxid = Integer.valueOf(strMaxid);
//		} else {
//			FileManager.write("", Util.getProperty("cfgdir"), Util.getProperty("cfgdir") + "maxid.cfg", "utf-8");
//		}
	}
	
	public String extract(String begin, String num, String lan, String searchType, String searchInfo) {
		StringBuffer response = ProxyTest.doPostRequest(begin, num, lan, searchType, searchInfo);
		
		return response.toString();
	}
	
	public Map<?, ?> StringToObj(String s) {
		Map<?, ?> map = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
	
			mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT,
					Boolean.TRUE);
		
			map = mapper.readValue(s, Map.class);
		} catch (JsonGenerationException e) {
			System.out.println(s);
			e.printStackTrace();
		} catch (JsonMappingException e) {
			System.out.println(s);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(s);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	public static void main(String[] args) {
		RelatedwordsExt ext = new RelatedwordsExt();
		
		int perpage = 10000;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try { 
            fis = new FileInputStream("D:\\temp\\keyword(l1).txt"); 
            isr = new InputStreamReader(fis, "GBK"); 
            br = new BufferedReader(isr); 
            String line = null; 
            int i = 0;
            while ((line = br.readLine()) != null) { 
            	i++;
            	int page = (int)Math.ceil(i/perpage);
            	ext.doWork(line, page);
            } 
        } catch (Exception e) { 
//        	e.printStackTrace(); 
        } finally {
        		try {
        			if (br != null) {
        				br.close();
        			}
        			if (isr != null) {
        				isr.close();
        			}
        			if (fis != null) {
        				fis.close();
        			}
				} catch (IOException e) {
					e.printStackTrace();
				}
        }
		
	}
	
	private StringBuffer getSB(Map result, String uuid, Integer maxid, String simwordtyp, String word, Integer startid) {
		StringBuffer sb = new StringBuffer();
		String label = (String)result.get("label");
		String[] temparray = label.split("\\[");
		if (temparray.length > 0) {
			if (temparray[0].equals(word)) {
				sb.append("Wordid=").append((startid)).append("\r\n");
			} else {
				sb.append("Wordid=").append((maxid)).append("\r\n");
			}
			
			if (temparray.length > 1) {
				sb.append("Cnword=").append(temparray[0]).append("\r\n").append("Enword=").append(temparray[1].replace("[", "").replace("]", "")).append("\r\n");
			} else {
				sb.append("Cnword=").append(temparray[0]).append("\r\n");
			}
			
			if (uuid != null && !uuid.equals("")) {
				sb.append(simwordtyp).append("=").append(uuid.replace("-", "")).append("\r\n");
			}
			
			Map resultlist = (Map)result.get("resultlist");
			if (resultlist != null) {
				Map superclasses = (Map)resultlist.get("Super Classes");
				if (superclasses != null && superclasses.size() > 0) {
					sb.append("Super Classes=");
					for (Object key : superclasses.keySet()) {
						sb.append(superclasses.get(key)).append(";");
					}
					sb.append("\r\n");
				}
				Map subclasses = (Map)resultlist.get("Sub Classes");
				if (subclasses != null && subclasses.size() > 0) {
					sb.append("Sub Classes=");
					for (Object key : subclasses.keySet()) {
						sb.append(subclasses.get(key)).append(";");
					}
					sb.append("\r\n");
				}
				Map instances = (Map)resultlist.get("Instances");
				if (instances != null && instances.size() > 0) {
					sb.append("Instances=");
					for (Object key : instances.keySet()) {
						sb.append(instances.get(key)).append(";");
					}
					sb.append("\r\n");
				}
				Map domains = (Map)resultlist.get("Domains");
				if (domains != null && domains.size() > 0) {
					sb.append("Domains=");
					for (Object key : domains.keySet()) {
						sb.append(domains.get(key)).append(";");
					}
					sb.append("\r\n");
				}
				Map ranges = (Map)resultlist.get("Ranges");
				if (ranges != null && ranges.size() > 0) {
					sb.append("Ranges=");
					for (Object key : ranges.keySet()) {
						sb.append(ranges.get(key)).append(";");
					}
					sb.append("\r\n");
				}
				Map types = (Map)resultlist.get("Types");
				if (types != null && types.size() > 0) {
					sb.append("Types=");
					for (Object key : types.keySet()) {
						sb.append(types.get(key)).append(";");
					}
					sb.append("\r\n");
				}
				Map rInstances = (Map)resultlist.get("Related Instances");
				if (rInstances != null && rInstances.size() > 0) {
					sb.append("Related Instances=");
					for (Object key : rInstances.keySet()) {
						sb.append(rInstances.get(key)).append(";");
					}
					sb.append("\r\n");
				}
				Map rClasses = (Map)resultlist.get("Related Classes");
				if (rClasses != null && rClasses.size() > 0) {
					sb.append("Related Classes=");
					for (Object key : rClasses.keySet()) {
						sb.append(rClasses.get(key)).append(";");
					}
					sb.append("\r\n");
				}
			}
		}
		return sb;
	}
	public static int getRandom() {
        int max=7000;
        int min=5000;
        Random random = new Random();

        return random.nextInt(max)%(max-min+1) + min;
    }
	private void doWork(String word, int page) {
		FileManager.write(word + "\r\n", Util.getProperty("cfgdir"), Util.getProperty("cfgdir") + "done.cfg", "utf-8", true);
		try {
			Thread.sleep(getRandom());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String s = "";
		String end = "10";
		s = extract("0", end, "ch", "Classes", word).replaceAll("\\\\", "");
		Map map = StringToObj(s.replace("\\\\", "").substring(1, s.length() - 1));
		int startid = 0;
		if (map != null) {
			try {
				int total = (Integer)map.get("total");
				String temp = FileManager.read(Util.getProperty("cfgdir") + "maxid.cfg", "utf-8");
				if (!temp.equals("")) {
					startid = Integer.valueOf(temp.trim());
				}
				if (total > 0) {
					String uuid = UUID.randomUUID().toString();
					int maxid = 0;
					
					String strMaxid = FileManager.read(Util.getProperty("cfgdir") + "maxid.cfg", "utf-8");
					if (!strMaxid.equals("")) {
						maxid = Integer.valueOf(strMaxid.trim());
					}
					
					if (total > 1) {
						List<Map> results = (List<Map>)map.get("results");
						StringBuffer sb = new StringBuffer();
						for (Map result : results) {
							FileManager.write(getSB(result, uuid, ++maxid, "simword_classes", word, startid).toString() + "\r\n", Util.getProperty("cfgdir"), Util.getProperty("cfgdir")  + "result"+page+".txt", "utf-8", true);
						}
					} else {
						Map result = (Map)map.get("results");
						FileManager.write(getSB(result, uuid, ++maxid, "simword_classes", word, startid).toString() + "\r\n", Util.getProperty("cfgdir"), Util.getProperty("cfgdir")  + "result"+page+".txt", "utf-8", true);
					}
					
					FileManager.write((maxid)+"", Util.getProperty("cfgdir"), Util.getProperty("cfgdir") + "maxid.cfg", "utf-8");
				}
			} catch (Exception e) {
				
			}
		}
		
		try {
			Thread.sleep(getRandom());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		s = extract("0", end, "ch", "Properties", word).replaceAll("\\\\", "");
		Map pmap = StringToObj(s.replace("\\\\", "").substring(1, s.length() - 1));
		if (pmap != null) {
			try {
				int ptotal = (Integer)pmap.get("total");
				if (ptotal > 0) {
					String uuid = UUID.randomUUID().toString();
					Integer maxid = 0;
					String strMaxid = FileManager.read(Util.getProperty("cfgdir") + "maxid.cfg", "utf-8");
					if (!strMaxid.equals("")) {
						maxid = Integer.valueOf(strMaxid.trim());
					}
					
					if (ptotal > 1) {
						List<Map> results = (List<Map>)pmap.get("results");
						StringBuffer sb = new StringBuffer();
						for (Map result : results) {
							FileManager.write(getSB(result, uuid, ++maxid, "simword_properties", word, startid).toString() + "\r\n", Util.getProperty("cfgdir"), Util.getProperty("cfgdir")  + "result"+page+".txt", "utf-8", true);
						}
					} else {
						Map result = (Map)pmap.get("results");
						FileManager.write(getSB(result, "", ++maxid, "simword_properties", word, startid).toString() + "\r\n", Util.getProperty("cfgdir"), Util.getProperty("cfgdir")  + "result"+page+".txt", "utf-8", true);
					}
					
					FileManager.write((maxid)+"", Util.getProperty("cfgdir"), Util.getProperty("cfgdir") + "maxid.cfg", "utf-8");
				}
			} catch (Exception e) {
				
			}
		}
		
		try {
			Thread.sleep(getRandom());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		s = extract("0", end, "ch", "Instances", word).replaceAll("\\\\", "");
		Map imap = StringToObj(s.replace("\\\\", "").substring(1, s.length() - 1));
		if (imap != null) {
			try {
				int itotal = (Integer)imap.get("total");
				if (itotal > 0) {
					String uuid = UUID.randomUUID().toString();
					Integer maxid = 0;
					String strMaxid = FileManager.read(Util.getProperty("cfgdir") + "maxid.cfg", "utf-8");
					if (!strMaxid.equals("")) {
						maxid = Integer.valueOf(strMaxid.trim());
					}
					
					if (itotal > 1) {
						List<Map> results = (List<Map>)imap.get("results");
						StringBuffer sb = new StringBuffer();
						for (Map result : results) {
							FileManager.write(getSB(result, uuid, ++maxid, "simword_instances", word, startid).toString() + "\r\n", Util.getProperty("cfgdir"), Util.getProperty("cfgdir")  + "result"+page+".txt", "utf-8", true);
						}
					} else {
						Map result = (Map)imap.get("results");
						FileManager.write(getSB(result, uuid, ++maxid, "simword_instances", word, startid).toString() + "\r\n", Util.getProperty("cfgdir"), Util.getProperty("cfgdir")  + "result"+page+".txt", "utf-8", true);
					}
					
					FileManager.write((maxid)+"", Util.getProperty("cfgdir"), Util.getProperty("cfgdir") + "maxid.cfg", "utf-8");
				}
			} catch (Exception e) {
				
			}
			
		}
	}
}
