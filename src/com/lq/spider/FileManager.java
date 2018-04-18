package com.lq.spider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FileManager { 
    public static List<String> readNums(String fileName, String encoding) { 
    	List<String> rs = new ArrayList<String>();
//        StringBuffer fileContent = new StringBuffer();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try { 
            fis = new FileInputStream(fileName); 
            
            br = new BufferedReader(new UnicodeReader(fis, encoding));  
//            isr = new InputStreamReader(fis, encoding); 
//            
//            
//            br = new BufferedReader(isr); 
            String line = null; 
            while ((line = br.readLine()) != null) { 
//                fileContent.append(line); 
            	if (line.trim().startsWith("<申请号>=")) {
            		rs.add(line.replace("<申请号>=", ""));
            	} else {
            		continue;
            	}
//                fileContent.append(System.getProperty("line.separator")); 
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
        return rs; 
    } 
    
    public static String read(String fileName, String encoding) { 
        StringBuffer fileContent = new StringBuffer();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try { 
            fis = new FileInputStream(fileName); 
            isr = new InputStreamReader(fis, encoding); 
            br = new BufferedReader(isr); 
            String line = null; 
            while ((line = br.readLine()) != null) { 
                fileContent.append(line); 
                fileContent.append(System.getProperty("line.separator")); 
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
        return fileContent.toString(); 
    } 
    
    public static void read(String fileName, String encoding, Statement stmt) { 
        StringBuffer fileContent = new StringBuffer();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try { 
            fis = new FileInputStream(fileName); 
            isr = new InputStreamReader(fis, encoding); 
            br = new BufferedReader(isr); 
            String line = null; 
            while ((line = br.readLine()) != null) { 
            	if (!line.trim().equals("")) {
            		stmt.addBatch(line);
            	}
            } 
        } catch (Exception e) { 
        	e.printStackTrace(); 
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
    
    

    public static void write(String fileContent, String dir, String fileName, String encoding) { 
    	FileOutputStream fos = null;
    	OutputStreamWriter osw = null;
        try {
        	File sqlFileDir = new File(dir);
        	if (!sqlFileDir.exists()) {
        		sqlFileDir.mkdirs(); 
        	}
        	
//        	File sqlFile = new File(fileName);
//        	if (sqlFile.exists()) {
//        		fos = new FileOutputStream(fileName, true); 
//        	} else {
//        		fos = new FileOutputStream(fileName); 
//        	}
        	fos = new FileOutputStream(fileName); 
            osw = new OutputStreamWriter(fos, encoding); 
            osw.write(fileContent); 
        } catch (Exception e) { 
        	e.printStackTrace(); 
        } finally {
        	if (osw != null) {
        		try {
					osw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
        	if (fos != null) {
        		try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
    } 
    
    public static void write(String fileContent, String dir, String fileName, String encoding, Boolean flag) { 
    	FileOutputStream fos = null;
    	OutputStreamWriter osw = null;
        try {
        	File sqlFileDir = new File(dir);
        	if (!sqlFileDir.exists()) {
        		sqlFileDir.mkdirs(); 
        	}
        	
        	File sqlFile = new File(fileName);
        	if (sqlFile.exists()) {
        		fos = new FileOutputStream(fileName, flag); 
        	} else {
        		fos = new FileOutputStream(fileName); 
        	}
            osw = new OutputStreamWriter(fos, encoding); 
            osw.write(fileContent); 
        } catch (Exception e) { 
        	e.printStackTrace(); 
        } finally {
        	if (osw != null) {
        		try {
					osw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
        	if (fos != null) {
        		try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
    } 
    
 // 复制文件夹
    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
         // 新建目标目录
        (new File(targetDir)).mkdirs();
         // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
         for (int i = 0; i < file.length; i++) {
             if (file[i].isFile()) {
                 // 源文件
                File sourceFile = file[i];
                 // 目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                 copyFile(sourceFile, targetFile);
             }
             if (file[i].isDirectory()) {
                 // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                 // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                 copyDirectiory(dir1, dir2);
             }
         }
     }

 // 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
         BufferedInputStream inBuff = null;
         BufferedOutputStream outBuff = null;
         try {
             // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

             // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

             // 缓冲数组
            byte[] b = new byte[1024 * 5];
             int len;
             while ((len = inBuff.read(b)) != -1) {
                 outBuff.write(b, 0, len);
             }
             // 刷新此缓冲的输出流
            outBuff.flush();
         } finally {
             // 关闭流
            if (inBuff != null)
                 inBuff.close();
             if (outBuff != null)
                 outBuff.close();
         }
     }
    
    /**
	 * 新建目录
	 * 
	 * @param folderPath
	 *            String 如 c:/fqf
	 * @return boolean
	 */
	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 新建文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String 文件内容
	 * @return boolean
	 */
	public static void newFile(String filePathAndName, String fileContent) {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			resultFile.close();

		} catch (Exception e) {
			System.out.println("新建文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}

	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 移动文件到指定目录
	 * 
	 * @param oldPath
	 *            String 如：c:/fqf.txt
	 * @param newPath
	 *            String 如：d:/fqf.txt
	 */
	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);

	}

	/**
	 * 移动文件到指定目录
	 * 
	 * @param oldPath
	 *            String 如：c:/fqf.txt
	 * @param newPath
	 *            String 如：d:/fqf.txt
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);

	}


	// 拷贝文件
	private static void copyFile2(String source, String dest) {
		try {
			File in = new File(source);
			File out = new File(dest);
			FileInputStream inFile = new FileInputStream(in);
			FileOutputStream outFile = new FileOutputStream(out);
			byte[] buffer = new byte[1024];
			int i = 0;
			while ((i = inFile.read(buffer)) != -1) {
				outFile.write(buffer, 0, i);
			}// end while
			inFile.close();
			outFile.close();
		}// end try
		catch (Exception e) {

		}// end catch
	}// end copyFile
	
	public static Boolean isExist(String fileName, String encoding, String word) { 
		boolean flag = false;
		
		FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try { 
            fis = new FileInputStream(fileName); 
            isr = new InputStreamReader(fis, encoding); 
            br = new BufferedReader(isr); 
            String line = null; 
            while ((line = br.readLine()) != null) { 
            	if (line.equals(word)) {
            		flag = true;
            		break;
            	}
            } 
        } catch (Exception e) { 
        	e.printStackTrace(); 
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
        return flag; 
    } 

} 
