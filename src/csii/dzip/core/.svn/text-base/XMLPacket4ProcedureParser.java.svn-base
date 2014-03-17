/**
 * 用于组包、解析与核心存储过程通信的XML格式报文
 */
package csii.dzip.core;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import csii.base.action.util.FileUtils;
import csii.base.action.util.Util;
import csii.base.constant.Constants;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-6-20
 */
public class XMLPacket4ProcedureParser {
	
//	private static final String DEFAULT_ROOTELEMENT_NAME = "Message";
//	private static final String DEFAULT_ATTRIBUTE_NAME = "Value";
	private static final String FILENAME = "Message.xml";

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		XMLPacket4ProcedureParser xmlParser = new XMLPacket4ProcedureParser();
		List list = xmlParser.createXMLTestTemplate();
		Map map = new HashMap();
		map.put("Username", "andony");
		map.put("Name", "zhangyongqing");
		map.put("Sex", "mail");
		map.put("Age", "28");
		map.put("Email", "zhangyongqing@csii.com.cn");
//		String xmlMessage = xmlParser.format(map);
		String xmlMessage = xmlParser.format(map, list);
		System.out.println(xmlMessage);
	}
	
	/**
	 * 创建用于测试的XML格式模板
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List createXMLTestTemplate(){
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(Constants.DEFAULT_ROOTELEMENT_NAME);
		@SuppressWarnings("unused")
		Element username = root.addElement("Username");
		@SuppressWarnings("unused")
		Element name = root.addElement("Name");
		@SuppressWarnings("unused")
		Element sex = root.addElement("Sex");
		@SuppressWarnings("unused")
		Element age = root.addElement("Age");
		@SuppressWarnings("unused")
		Element email = root.addElement("Email");
		System.out.println(document.asXML());
		FileUtils.fileWriter(document, FILENAME);
		List list = getElementNames(FileUtils.getRootElement(FILENAME));
		return list;
	}
	
	/**
	 * 获得该节点下所有子节点的名称
	 * 
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List getElementNames(Element element){
		List list = new ArrayList();
		Iterator<Element> it = element.elementIterator();
		while(it.hasNext()){
			Element e = it.next();
			String name = e.getName();
			list.add(name);
		}
		return list;
	}
	
	/**
	 * 设置根节点名称（缺省名称）
	 * 
	 * @return
	 */
	public Element setRootElement() {
		return setRootElement(Constants.DEFAULT_ROOTELEMENT_NAME);
	}

	/**
	 * 设置根节点名称
	 * 
	 * @param elementName
	 * @return
	 */
	public Element setRootElement(String elementName) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(elementName);
		return root;
	}
	
	/**
	 * 组用于传入存储过程的报文
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String format(Map map) {
		return format(map, null, null);
	}

	/**
	 * 组用于传入存储过程的报文
	 * 
	 * @param map
	 * @param elementName
	 * @param attributeName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String format(Map map, String elementName, String attributeName) {
		Element root = null;
		if (Util.checkString(elementName)) {
			root = setRootElement(elementName);
		}
		root = setRootElement();
		if (root != null) {
			if (map instanceof Map) {
				Iterator it = map.entrySet().iterator();
				while (it.hasNext()) {
					Entry entry = (Entry) it.next();
					Element e = root.addElement((String) entry.getKey());
					if (Util.checkString(attributeName)) {
						e.addAttribute(attributeName, (String) entry.getValue());
					}
					e.addAttribute(Constants.DEFAULT_ATTRIBUTE_NAME, (String) entry.getValue());
				}
				return getElementContent(root);
			} else {
				return Constants.PE_NULL;
			}
		} else {
			return Constants.PE_NULL;
		}
	}
	
	/**
	 * 根据XML文件定义，组用于传入存储过程的报文
	 * 
	 * @param map
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String format(Map map, List list) {
		return format(map, list, null, null);
	}
	
	/**
	 * 根据XML文件定义，组用于传入存储过程的报文
	 * 
	 * @param map
	 * @param list
	 * @param elementName
	 * @param attributeName
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public String format(Map map, List list, String elementName, String attributeName) {
		if(list == null)
			return format(map, null, null);
		Element root = null;
		if (Util.checkString(elementName)) {
			root = setRootElement(elementName);
		}
		root = setRootElement();
		Iterator<String> it = list.iterator();
		if (root != null) {
			if (map instanceof Map) {
				while(it.hasNext()){
					String name = it.next();
					Element e = root.addElement(name);
					if(map.containsKey(name)){
						if (Util.checkString(attributeName)) {
							e.addAttribute(attributeName, (String) map.get(name));
						}
						e.addAttribute(Constants.DEFAULT_ATTRIBUTE_NAME, (String) map.get(name));
					}else{
						if (Util.checkString(attributeName)) {
							e.addAttribute(attributeName, Constants.PE_NULL);
						}
						e.addAttribute(Constants.DEFAULT_ATTRIBUTE_NAME, Constants.PE_NULL);
					}
				}
				return getElementContent(root);
			} else {
				return Constants.PE_NULL;
			}
		} else {
			return Constants.PE_NULL;
		}
	}
	
	/**
	 * 根据存储过程的返回报文，解析并取得数据
	 */
	@SuppressWarnings("unchecked")
	public Map parse(String respMsg){
		Map respMap = new HashMap();
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try
		{
			document = saxReader.read(new StringReader(respMsg));   
		}catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
		Element root = document.getRootElement();
		for(Iterator it = root.elementIterator(); it.hasNext();)
		{
			Element element = (Element)it.next();
			respMap.put((String)element.getName(), element.attribute(Constants.DEFAULT_ATTRIBUTE_NAME).getValue()) ;//获取
		}
		
		return respMap;
	}
	
	/**
	 * 获得该元素下的内容
	 * 
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getElementContent(Element element){
		StringBuffer sb = new StringBuffer();
		for (Iterator i = element.elementIterator(); i.hasNext();){
			Element e = (Element) i.next();
			sb.append(e.asXML());
		}
		return sb.toString();
	}
	
	/**
	 * 获得该元素下的内容（包括根节点）
	 * 
	 * @param root
	 * @return
	 */
	public String getElementContentIncludeRoot(Element root){
		return root.asXML();
	}
	
}
