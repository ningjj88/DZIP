/**
 * 
 */
package csii.dzip.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Element;

import csii.base.action.util.FileUtils;
import csii.base.bean.FieldBean;
import csii.base.constant.Constants;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-7-1
 */
public class FixedLength8583Processor {

	/**
	 * 常用的变量，常量声明
	 */

	public static int count = 4;
	private static final String TESTMESSAGE = "1446940027100200622135901010003440601000000000000500006100536495317660536490610    061060110210206         01006753   01000000   6221359010100034406=000020187599292=                                                                                                         650021622550        65002111000000511722001CHN6753自动柜员机                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       15657D1672518E4C0B2                                                        00000000010000                                                                                         04296510                                                           20060610531766087452000100009999999900000000                                                        00000000010000531766                                                                                087452                                                                                              2263DDDFD7440B12000100009400271087654321";
	public final Log logger = LogFactory.getLog(getClass());
	/**
	 * @param args
	 */
	 @SuppressWarnings("unchecked")
	 public static void main(String[] args) throws UnsupportedEncodingException {
		 FixedLength8583Processor parser = new FixedLength8583Processor();
		 Map map = parser.testMap();
		 System.out.println("用于测试的数据：" + map);
		 Element element = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
		 String formatedStr = parser.format(map, element);
		 String formatedStr2 = TESTMESSAGE;
		 System.out.println("formatedStr    length:" + formatedStr.length());
		 System.out.println("formatedStr：" + formatedStr);
		 System.out.println("TESTMESSAGE    length:" + new String(TESTMESSAGE.getBytes(Constants.CHARSET_GB18030),  Constants.CHARSET_ISO_8859_1).length());
		 System.out.println("TESTMESSAGE：" + TESTMESSAGE);
		 Map parsedMap = parser.parse(formatedStr, element);
		 Map parsedMap2 = parser.parse(new String(formatedStr2.getBytes(Constants.CHARSET_GB18030), Constants.CHARSET_ISO_8859_1), element);
		
		 System.out.println("解析测试的formatedStr ：" + parsedMap);
		 System.out.println("解析测试的TESTMESSAGE ：" + parsedMap2);
		
		 String formatedStr3 = parser.format(parsedMap, element);
		 System.out.println("formatedStr3   length:" + formatedStr.length());
		 System.out.println("formatedStr3: " + formatedStr3);
	 }
	 
	/**
	 * 根据XML文件格式组定长的8583报文
	 * 
	 * @param map
	 * @param elements
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String format(Map map, Element elements) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();   //定义Stringbuffer对象，动态生成字符串
//		System.err.println("FixedLength8583Processor========>"+map);
		String returnMessage= null;           	//返回值
		String value = null;
		for (Iterator<Element> iterator = elements.elementIterator(); iterator.hasNext();) {  	//解析定长8583配置文件
			value = null;
			Element element = (Element) iterator.next();                                    	//解析标签
			String elementName = element.getName();                   			   				//标签名，即域名
			Attribute attribute = element.attribute(Constants.LENGTH);						
			int length = Integer.valueOf(attribute.getValue());									//域的规定长度
			if(map.get(elementName)!=null)
				value = String.valueOf(map.get(elementName)).trim();							//从参数中取得域值
			if (value !=null) {																	//判断值是否为空
				int num = length - value.getBytes(Constants.CHARSET_GBK).length;				//计算需要填充的长度
				sb.append(value);																//组装
				if (num > 0) {
					fillSpace(sb, num);															//如果不够规定长度填充空格
				}
			} else {																			//如果值为空，全部用空格填充
				fillSpace(sb, length);
			}
		}
		sb.insert(0, sb.toString().getBytes(Constants.CHARSET_GBK).length);		//组装完毕后在报文头加上报文长度
		returnMessage = sb.toString();
//		System.err.println("FixedLength8583Processor========>"+returnMessage);	//返回报文
		return new String(returnMessage.getBytes(Constants.CHARSET_GBK),Constants.CHARSET_ISO_8859_1); 
	}

	/**
	 * 根据XML文件格式解定长的8583报文
	 * 
	 * @param str
	 *            待解析的字符串
	 * @param element
	 *            元素节点
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public Map parse(String str, Element element)
//			throws UnsupportedEncodingException {
//		@SuppressWarnings("unused")
//		int msgLength = Integer.valueOf(str.substring(0, 4));//解析报文前4位，获得报文长度；
//		Map map = new LinkedHashMap();
//		int index = 4;
//		for (Iterator<Element> i = element.elementIterator(); i.hasNext();) {//解析报文配置文件
//			Element e = (Element) i.next();									 //获得节点
//			String elementName = e.getName();								 //获得标签名
//			Attribute a = e.attribute(Constants.LENGTH);					 //获得属性名称
//			int length = Integer.valueOf(a.getValue());						 //获得属性值
//			int num = index + length;										 //报文总长度
//			String value = str.substring(index, num);						 //解析报文，获得域值
//			value = new  String(value.getBytes(Constants.CHARSET_ISO_8859_1),Constants.CHARSET_GBK);
//			index = num;
////			System.out.println(elementName + "=[" + value + "]");
//			map.put(elementName, value.trim());								   //返回报文的解析结果
//		}
//
//		return map;
//	}

	/**
	 * 用空格填充
	 * 
	 * @param sb
	 *            待填充空格的字符串
	 * @param num
	 *            需填充空格的数量
	 */
	public void fillSpace(StringBuffer sb, int num) {
		for (int i = 0; i < num; i++) {
			sb.append(" ");
		}
	}
	/**
	 * @param respMsg ：待解析报文
	 * @param fileName：报文格式定义文件路径
	 * @return Map    ：存放各域和值的集合
	 * add by chenshaoqi
	 * date：2010.07.19 
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> parse(byte[] byteMsg, Element root) throws UnsupportedEncodingException {
		int count = 4;
		Map<String, String> cxtMap = new HashMap<String, String>();  //结果集变量
		String name,property;   			//域名和属性变量定义
		String value = Constants.PE_NULL;	//临时变量
		@SuppressWarnings("unused")
		int fieldLength, msgLength;			//域的长度
		msgLength = Integer.valueOf( new String(byteMsg, 0, count));  //报文的长度
		Date now = new Date();
		DateFormat current = DateFormat.getDateTimeInstance();
		for (Iterator it = root.elementIterator(); it.hasNext();) {   //遍历报文配置文件
			Element element = (Element) it.next();  
			name = (String) element.getName();                         //域名
			fieldLength = Integer.parseInt(element.attributeValue(Constants.LENGTH));//域值的长度
			value = new String(byteMsg, count, fieldLength,Constants.CHARSET_GBK);   //在报文中取出
			value = value.trim();
			property = element.attributeValue(Constants.PROPERTY); //域值的属性
			count = count + fieldLength; 
			if(value != Constants.PE_NULL && value != null)
				logger.info(current.format(now) + ": " + name + "=[" + value + "] -----" + property);
			cxtMap.put(name, value);  //收集数据
		}
		return cxtMap;    //返回结果集
	}

	/**
	 * @param respMsg ：待解析报文
	 * @param fileName：报文格式定义文件路径
	 * @return Map    ：存放各域和值的集合
	 * add by chenshaoqi
	 * date：2010.07.19 
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> parse(String respMsg, Element root) throws UnsupportedEncodingException {
		int count = 4;
		Map<String, String> cxtMap = new HashMap<String, String>();  //结果集变量
		String name,property;   			//域名和属性变量定义
		String value = Constants.PE_NULL;	//临时变量
		@SuppressWarnings("unused")
		int fieldLength, msgLength;			//域的长度
		byte[] byteMsg = respMsg.getBytes(Constants.CHARSET_ISO_8859_1) ;
//		String HeadTxType = new String(byteMsg, 4, 4);   //在报文中取出消息类型
		msgLength = Integer.valueOf(respMsg.substring(0, count));  //报文的长度
		Date now = new Date();
		DateFormat current = DateFormat.getDateTimeInstance();
		for (Iterator it = root.elementIterator(); it.hasNext();) {   //遍历报文配置文件
			Element element = (Element) it.next();  
			name = (String) element.getName();                         //域名
			fieldLength = Integer.parseInt(element.attributeValue(Constants.LENGTH));//域值的长度
			value = new String(byteMsg, count, fieldLength,Constants.CHARSET_GBK).trim();   //在报文中取出消息类型
			property = element.attributeValue(Constants.PROPERTY); //域值的属性
			if(value != Constants.PE_NULL && value != null)
				logger.info(current.format(now) + ": " + name + "=[" + value + "] -----" + property);
			cxtMap.put(name, value);  //收集数据
			count = count + fieldLength; 
		}
		return cxtMap;    //返回结果集
	}

	/***
	 * @用途：用于ISO8583和定长8583之间的转换对应
	 * @param paramMap  传入的Map对象
	 * @param fileName
	 * @return 返回映射之后的Map对象
	 * @throws UnsupportedEncodingException 
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> convert8583(Map paramMap, String MapType) {
		Map<String, String> cxtMap = new HashMap<String, String>();
		String name, alias;
		String value = Constants.PE_NULL;
		int length;
		Element root = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
		for (Iterator it = root.elementIterator(); it.hasNext();) {								//解析报文配置文件
			Element element = (Element) it.next();
			name = (String) element.getName();													//标签名
			alias = element.attributeValue(Constants.ALIAS);                                    //别名，即定长8583的域和ISO8583的关联
			length = Integer.parseInt(element.attributeValue(Constants.LENGTH));                //长度属性
			if (MapType.equals(Constants.FIX8583)) {                                            // 当传入参数是ISO8583时
				if (!Constants.PE_NULL.equals(alias)
						&& !Constants.PE_NULL.equals(String.valueOf(paramMap.get(name)).trim())) { // 当传入参数是定长8583时
					value = String.valueOf(paramMap.get(name));                                     // 与ISO8583对应起来
					cxtMap.put(alias, value);
				}
			} else {                                                        // 当传入参数是ISO8583时
				if (paramMap.containsKey(alias)) {
					try {
						if(paramMap.get(alias)==null)
							value = "";
						else
							value = new String(String.valueOf(paramMap.get(alias)).trim());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (value.trim().length() == length) {                  // 值的长度达到规定要求
						cxtMap.put(name, value);                            // 与定长8583对应起来
					} else {
						value = fill(value, length, Constants.RIGHT, " ");  // 如果值的长度不够 右边补空格
						cxtMap.put(name, value);
					}
				}// end of if
				else {
					if (element.attributeValue(Constants.DEFAULT) != null) // 如果ISO8583没有传入值,但又不能为空
						value = element.attributeValue(Constants.DEFAULT); // 取默认值
					else
						value = fill(Constants.PE_NULL, length,
								Constants.RIGHT, Constants.PE_SPACE);      // 否则全部补空格
					cxtMap.put(name, value);
				}
			}
		}
		return cxtMap;
	}
	/**
	 *  填充字符串，达到指定的长度
	 * @param str  需要被填充的字符串
	 * @param length  要达到的长度
	 * @param direction  左或右填充
	 * @param content    填充的内容
	 * @return   填充后的字符串
	 */ 
	public static String fill(String str, int length, String direction,String content) {

		if (direction == Constants.LEFT)    //左边补齐
			for (int i = str.length(); i < length; i++) {
				str = content + str;
			}
		else								//右边补齐
			for (int i = str.length(); i < length; i++) {
				str = str + content;
			}
		return str;
	}
	/**
	 * 用途：用于检验传入报文所有域是否合法。
	 * 
	 * @param pramMap
	 *            ：传入存储待验证的各域信息的Map
	 * @return ： 返回不合法信息的Map对象
	 * add by chenshaoqi  
	 * date：2010.07.19
	 */
	@SuppressWarnings("unchecked")
	public static Map validFields(Map pramMap) {
		int fieldlength;
		String validMsg = "";
		String fieldName = null;
		String format = null;
		String value = null;
		boolean fixLength, nullable;
		Map respMap = new HashMap();

		Element root = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583); //解析XML文件，取得XML的根节点 
		Iterator it = root.elementIterator(); //迭代 
		
		while (it.hasNext()) {
			 value = "";
			Element element = (Element) it.next();			//遍历XML的各个节点 
			fieldName = (String) element.getName();			//解析XML文件，取得标签名（域名） 
			fieldlength = Integer.valueOf(element.attributeValue(Constants.LENGTH));//解析XML文件，取得域的规定长度的属性 
			format = element.attributeValue(Constants.FORMAT);//解析XML文件，取得域的内容格式的属性 
			fixLength = Boolean.valueOf(element.attributeValue(Constants.FIXED));//解析XML文件，取得域是否定长的属性 
			nullable = Boolean.valueOf(element.attributeValue(Constants.NULLABLE));//解析XML文件，取得与是否能为空的属性 
			if(pramMap.get(fieldName)!=null)
			value = pramMap.get(fieldName).toString();//从传入参数PramMap中取出域值 
			FieldBean bean = new FieldBean(fieldName,fixLength, fieldlength, format,nullable);//构造实体类，判断域值的合法性 
			System.out.println(fieldName + "= [" + value + "]");
			
			validMsg = fieldValid(value, bean);//开始检验各个域值的合法性 
			if (validMsg != null) {
				System.err.println(validMsg);
				respMap.put(fieldName, validMsg);      //如果验证有错误信息，添加到Map对象中。
			} else
				System.out.println("successful!");
			System.out.println("*********************************************************************");
		}
		return respMap;   //返回存储错误信息的Map对象
	}


	/**
	 * 用途：用于检验传入报文域值是符合属性要求。
	 * @author chenshq
	 * @param value ：传入待验证的域值
	 * @param bean  ：域属性的java bean
	 * @return ：返回不合法信息
	 */
	public static String fieldValid(String value, FieldBean bean) {
		String msgError = null;
		//当域值为空值时，做判断处理 
		if (Constants.PE_NULL.equals(value.trim())) {
			if (!bean.nullable) {	//必填域的值为空,直接返回错误信息 
				msgError = bean.fieldName + ": The filed isn't nullable!";
				return msgError;
			}
		}
		else {			//当域值不为空时的处理 
			if (bean.fixLength && (value.trim().length() != bean.fieldlength)) {//值的长度固定时，判断传入值是否符合要求 
				msgError = bean.fieldName +": The length of field should be fixed!";//传入参数的长度不合法时,直接返回错误信息 
				return msgError;
			}
			else {//判断传入参数长度符合要求 
				if (!Constants.PE_NULL.equals(bean.format) && !regex(value, bean)) {//当值域有格式限制时，格式匹配判断 
					msgError = bean.fieldName + ": The format of value is invalid!";//格式不匹配时，直接返回错误 信息 
					return msgError;
				}
			}
		}
		return msgError;
	}

	/**
	 * 利用正则表达式验证传入参数value， 是否符合规定的格式
	 * @author chenshq
	 * @param value：待验证的值
	 * @param bean ：域属性的java bean
	 * @return ：返回是否合法
	 */
	public static boolean regex(String value, FieldBean bean) {
		boolean matchs = false;
		final String regexNum = "([0-9]{0," + bean.fieldlength + "})"; //全部是数字，并且是定长的验证规则
		final String regexNS = "([0-9A-Z]{" + bean.fieldlength + "})"; //数字和字符组合的验证规则。
		final String regex35bit = "([0-9]{19}=[0-9]{15,16}\\=*\\s*)"; // 针对第二磁道的验证规则,35#。
		final String regexNSP = "([0-9]+\\s*)"; // 数字空格组合的验证规则。
		Pattern pattern = null;
		//根据每个的格式属性，匹配不同的验证规则 
		if ("n".equals(bean.format)) {// 数字
			System.out.println(regexNum);
			pattern = Pattern.compile(regexNum);
		}
		if ("na".equals(bean.format)) {// 数字字符组合
			System.out.println(regexNS);
			pattern = Pattern.compile(regexNS);
		}
		if ("35#".equals(bean.format)) {// 35#特定
			System.out.println(regex35bit);
			pattern = Pattern.compile(regex35bit);
		}
		if ("nsp".equals(bean.format)) {// 数字中带空格补齐
			System.out.println(regexNSP);
			pattern = Pattern.compile(regexNSP);
		}
		Matcher matcher = pattern.matcher(value);
		matchs = matcher.matches();
		return matchs;
	}
	
	
	/**
	 * @author chenshq
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

	/**
	 * 返回测试的Map数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map testMap() {
		Map map = new LinkedHashMap();
		map.put("BANKID", "94002710");
		map.put("MESG", "0200");
		map.put("PAN", "6221359010100034406");
		map.put("PROC", "010000");
		map.put("AMT", "000000005000");
		map.put("TRDT", "0610053649");
		map.put("TRAC", "531766");
		map.put("LOCT", "053649");
		map.put("LOCD", "0610");
		map.put("EXPI", "");
		map.put("SETT", "0610");
		map.put("MERC", "6011");
		map.put("POSE", "021");
		map.put("POSC", "02");
		map.put("POSP", "06");
		map.put("FEE", "");
		map.put("ACQU", "01006753");
		map.put("FORW", "01000000");
		map.put("TRACK2", "6221359010100034406=000020187599292=");
		map.put("TRACK3", "");
		map.put("RETR", "650021622550");
		map.put("AUTH", "");
		map.put("RESP", "");
		map.put("CATI", "65002111");
		map.put("CAID", "000000511722001");
		map.put("CANL", "CHN6753自动柜员机");
		map.put("ADDI", "");
		map.put("TRCU", "156");
		map.put("PIN", "57D1672518E4C0B2");
		map.put("SECU", "");
		map.put("RETU", "");
		map.put("RESE", "00000000010000");
		map.put("IDEN", "");
		map.put("ORIG", "");
		map.put("DEST", "04296510");
		map.put("ACCID1", "");
		map.put("ACCID2", "");
		map.put("NAIT_RESE", "20060610531766087452000100009999999900000000");
		map.put("ACQU_RESE", "00000000010000531766");
		map.put("DEST_RESE", "087452");
		map.put("MAC", "2263DDDFD7440B12");
		map.put("CACC", "00010000");
		map.put("ISS1", "94002710");
		map.put("ISS2", "87654321");
		return map;
	}

}
