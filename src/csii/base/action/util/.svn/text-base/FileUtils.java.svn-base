/**
 * 操作各种类型文件的工具类
 */
package csii.base.action.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import csii.base.constant.Constants;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-7-7
 */
public class FileUtils {
	private static Log log = LogFactory.getLog(FileUtils.class);
	private static Properties pros;
	static {
		pros = new Properties();
		try{
			pros.load(FileUtils.class.getClassLoader().getResourceAsStream("config/msg/fieldName.properties"));
//			log.debug("===============================>加载fileName.properties");
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	
	/**
	 * 根据文件路径和名称读取XML文件
	 * 
	 * @param fileName
	 */
	public static Element getRootElement(String fileName) {
		SAXReader saxReader = new SAXReader();
		Element root = null;
		try {
			Document document = saxReader.read(Thread.class.getClass().getResourceAsStream(fileName));
			root = document.getRootElement();
//			System.out.println("读文件成功：" + fileName);
		} catch (DocumentException e) {
			log.error(e.getMessage());
		}
		return root;
	}
	
	/**
	 * 将XML写入文件
	 * 
	 * @param document
	 * @param fileName
	 */
	public static void fileWriter(Document document, String fileName){
		try {
			Writer fileWriter = new FileWriter(fileName);
			XMLWriter xmlWriter = new XMLWriter(fileWriter);
			xmlWriter.write(document);
			xmlWriter.flush();
			xmlWriter.close();
			log.info("写文件成功：" + fileName);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	/**
	 * 读取属性文件，获得key值的属性
	 * @param key
	 * @return
	 */
	public static String getParamValue(String key){
		
		return pros.getProperty(key);
		
	}
	/**
	 * Map排序
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map sort(Map map){
		Map<Object, Object> mapVK = new TreeMap<Object, Object>(
				new Comparator<Object>() {
					public int compare(Object obj1, Object obj2) {
						String v1 = (String) obj1;
						String v2 = (String) obj2;
						int s = v1.compareTo(v2);
						return s;
					}
				});
		Set col = map.keySet();
		Iterator it = col.iterator();
		while(it.hasNext()){
			String key = (String)it.next();
			String value = (String)map.get(key);
			mapVK.put(key, value);
		}
		return mapVK;
	}
	/**
	 * 显示各个域名属性
	 * @param map
	 */
	@SuppressWarnings("unchecked")
	public static void showFieldProperties(Map map) {
		String bit = "bit";
		if (map.get("BITMAP") != null) {
			String bitMap = map.get("BITMAP").toString();
			int startIndex = bitMap.indexOf("{") + 1;
			int endIndex = bitMap.indexOf("}");
			String str = bitMap.substring(startIndex, endIndex);
			StringTokenizer stringtokenizer = new StringTokenizer(str, ", ");
			Date now = new Date();
			DateFormat current = DateFormat.getDateTimeInstance();
			while (stringtokenizer.hasMoreTokens()) {
				String nextToken = stringtokenizer.nextToken();
				int fieleNum = Integer.parseInt(nextToken);
				String key = bit.concat(String.valueOf(fieleNum + 1));
				String fieldName = FileUtils.getParamValue(key);
				try {
					fieldName = new String(fieldName.getBytes(Constants.CHARSET_ISO_8859_1),Constants.CHARSET_GBK);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (map.get(key) != null)
					log.info(current.format(now) + ": "+ key + "=" + map.get(key) + " [" + fieldName + "]");

			}
		}
		else {
			Map paramMap = new HashMap();
			Iterator it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();
				if (key.startsWith(bit) && map.get(key) != null) {
					paramMap.put(key, map.get(key));
				}
			}
			Map sortMap = FileUtils.sort(paramMap);
			Iterator iterator = sortMap.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry entry = (Map.Entry)iterator.next();
				String key = entry.getKey().toString();
				String fieldName = FileUtils.getParamValue(key);
				try {
					 fieldName = new String(fieldName.getBytes(Constants.CHARSET_ISO_8859_1),Constants.CHARSET_GBK);
					} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
				if (sortMap.get(key) != null)
				   log.info(key + "=" + map.get(key) + " [" + fieldName+ "]");
				}
			}
	}
	/**
	 * 读取文本文件返回字符串
	 * 
	 * @author 陈少启
	 * @param txtFileName
	 * @return
	 * @throws IOException
	 */
	public static String readTxt(String txtFileName) throws IOException {
		StringBuffer strb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new FileReader(txtFileName));
			String str = br.readLine();
			while (str != null) {
				strb.append(str);
				str = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return strb.toString();
	}

}
