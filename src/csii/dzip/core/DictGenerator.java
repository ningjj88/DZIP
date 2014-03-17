/**
 * 项目数据字典生成类
 */
package csii.dzip.core;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-6-15
 */
public class DictGenerator {
	
	@SuppressWarnings("unchecked")
	static java.util.HashMap keys = new java.util.HashMap();

	@SuppressWarnings("unchecked")
	static java.util.HashMap comments = new java.util.HashMap();

	/**
	 * DictGenerator constructor comment.
	 */
	public DictGenerator() {
		super();
	}

	/**
	 * Generates a dictionary class from a properties-file Creation 
	 * 
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) {
		
		// generate naming dictionary.
		String fileName = "/config/msg/dictionary_zh_CN.properties";
		// dictionary and constants definition together.
		String targetName = "Dict,Constants";
		make(fileName, targetName);

		// generate error dictionary.
		fileName = "/config/msg/DzipErrors.properties";
		targetName = "Errors";
		make(fileName, targetName);
		
	}
	
	/*
	 * 获取真实的文件路径，生成的目标java文件和本文件是同一个子目录下。
	 * 
	 */
	private static String getFullName(String targetName) {
		if (targetName == null)
			return null;
		String path = "src/" + DictGenerator.class.getPackage().getName().replace('.', '/') + "/";
		String dictName = path + targetName + ".java";
		return dictName;
	}
	
	/*
	 * 真正生成文件的动作是这个make完成的。
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static void make(String fileName, String targetName) {
		try {
			// fetch properties from file.
			InputStream in = DictGenerator.class.getResourceAsStream(fileName);
			Properties fp = new KeyCheckProperties();
			fp.load(in);
			in.close();
			// if constants
			int delimitor = targetName.indexOf(',');
			String targetName1 = null;
			if (delimitor > 0) {
				targetName1 = targetName.substring(delimitor + 1);
				targetName = targetName.substring(0, delimitor);
			}
			// find the target java file.
			String dictName = getFullName(targetName);
			String consName = getFullName(targetName1);
			PrintStream prt = new PrintStream(new java.io.FileOutputStream(dictName));
			PrintStream conPrt = consName == null ? null : new PrintStream(new java.io.FileOutputStream(consName));
			String packageName = DictGenerator.class.getName();
			packageName = packageName.substring(0, packageName.lastIndexOf('.'));
			prt.println("package " + packageName + ";");
			prt.println();
			prt.println("/**");
			prt.println(" * Auto create from dictionary properties files.");
			prt.println(" * every item in properties has Format like this:");
			prt.println(" * key=comment<0:on;1:off>");
			prt.println();
			prt.println(" * Creation date: (" + new Date() + ")");
			prt.println(" * @author: CSII PowerEngine $Auto Generated$");
			prt.println(" */");
			prt.println("public class " + targetName + " {");
			if (conPrt != null) {
				conPrt.println("package " + packageName + ";");
				conPrt.println("/**");
				conPrt.println(" * Auto create from dictionary properties files.");
				conPrt.println(" * every item in properties has Format like this:");
				conPrt.println(" * key=comment<0:on;1:off>");
				conPrt.println();
				conPrt.println(" * Creation date: (" + new Date() + ")");
				conPrt.println(" * @author: CSII PowerEngine $Auto Generated$");
				conPrt.println(" */");
				conPrt.println("public class " + targetName1 + " {");
			}
			java.util.Set set = new TreeSet(fp.keySet());
			/*----------------------------------------------------------------------------------
			 * 
			 * 
			 * 
			 */
			for (Iterator i = set.iterator(); i.hasNext();) {
				String name = (String) i.next();//源文件中的key，就是dict的源名字。

				String value = fp.getProperty(name);//源文件中的value，包括注释、常量的值、后缀名。
				int conStart = value.indexOf('<');
				int conEnd = value.indexOf('>');
				String cons = null;//常量串
				if (conEnd > conStart && conStart > 0) {
					cons = value.substring(conStart + 1, conEnd);
					value = value.substring(0, conStart);//注释
				}
				String nameInDict = toCanonical(name);//dict类的属性名字。
				String nameValueDict = toValueCanonical(name);//dict类的属性名字。
				//skip index type name. Lawrence Dai 2002/11/4
				if (name.indexOf('[') == -1 && name.indexOf(']') == -1) {
					StringBuffer buffer = new StringBuffer("    public static final String ");
					buffer.append(nameInDict);
					buffer.append("=\"").append(nameValueDict).append("\";");
					int chars = 20 - buffer.length() % 20;
					if (chars == 20)
						chars = 0;
					buffer.append(nchar(' ', chars));
					String comment = new String(value.getBytes("ISO8859_1"));
					buffer.append("//").append(comment);
					prt.println(buffer);
				}
				// write cons type.
				if (cons != null && conPrt != null) {
					String[] items = cons.split(";");
					for (int j = 0; j < items.length; j++) {
						StringBuffer buffer = new StringBuffer("    public static final String ");
						String[] keypair = items[j].split(":");
						buffer.append(nameInDict + "_" + toCanonical(keypair[1]));
						buffer.append("=\"").append(keypair[0]).append("\";");
						conPrt.println(buffer);
					}
					conPrt.println();
				}
			}
			
			//----------------------------------------------------------------------------------------------
			
			prt.println("}");
			prt.flush();
			prt.close();
			if (conPrt != null) {
				conPrt.println("}");
				conPrt.flush();
				conPrt.close();
			}
			System.out.println("finished dictionary generation.\njava file:" + dictName + "\nproperties:" + fileName);
			if (consName != null) {
				System.out.println("finished constants generation.\njava file:" + consName + "\nproperties:" + fileName);
			}
			if (keys.size() > 0) {
				System.err.println("[WARN] --- Found " + keys.size() + " duplicate key.");
				for (java.util.Iterator i = keys.entrySet().iterator(); i.hasNext();) {
					java.util.Map.Entry entry = (java.util.Map.Entry) i.next();
					System.err.println(entry.getKey() + " = " + entry.getValue());
				}
				keys.clear();
			}
			if (comments.size() > 0) {
				System.err.println("[WARN] --- Found " + comments.size() + "duplidate comment.");
				for (java.util.Iterator i = comments.entrySet().iterator(); i.hasNext();) {
					java.util.Map.Entry entry = (java.util.Map.Entry) i.next();
					System.err.println(entry.getKey() + " = " + entry.getValue());
				}
				comments.clear();
			}
			Thread.sleep(50);
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 把一个dictname的名字编程java类的属性名字，规则是全部变成大写字母，如果源名字包括大写字母，就加“_”下划线。
	 * 其他字母、符号照写。
	 */
	private static String toValueCanonical(String name) {
		StringBuffer buf = new StringBuffer();
		String strbegin=Character.toString(name.charAt(0));
		if(strbegin.equals("$")){
			buf = new StringBuffer().append(":"+name.charAt(1));
		    for (int i = 2; i < name.length(); i++) {
		    	buf.append(name.charAt(i));
		    }
		    buf.append(":");
		}else if(strbegin.equals("@")) {
			buf = new StringBuffer().append(name.charAt(1));
		    for (int i = 2; i < name.length(); i++) {				
		    	buf.append(name.charAt(i));
		    }
		}else {
			buf = new StringBuffer().append(name.charAt(0));
			for (int i = 1; i < name.length(); i++) {
				buf.append(name.charAt(i));
			}
		}		
		return buf.toString();
	}
	
	/*
	 * 把一个dict的名字编程java类的属性名字，规则是全部变成大写字母，如果源名字包括大写字母，就加“_”下划线。
	 * 其他字母、符号照写。
	 */
	private static String toCanonical(String name) {
		StringBuffer buf = new StringBuffer();
		String strbegin=Character.toString(name.charAt(0));
		if(strbegin.equals("$")) {
		   buf = new StringBuffer().append("T"+name.charAt(1));
		   for (int i = 2; i < name.length(); i++) {
			   
//				if (Character.isUpperCase(name.charAt(i))) {
//					/*
//					 * 如果是大写字母，并且不是最后一个字母，而且紧跟后面的字母不是大写字母，就加“_”下划线。
//					 * 比如，SystemError --->  SYSTEM_ERROR
//					 * SystemOK      ---->   SYSTEM_OK  
//					 */
//					if (i < name.length() - 1 && !Character.isUpperCase(name.charAt(i + 1)))
//						buf.append('_');
//					
//						
//				}
			
				String str=Character.toString(name.charAt(i));
				if(str.equals(".")){
					buf.append('_');
					continue;
				}
				buf.append(name.charAt(i));
		   }
		}else if(strbegin.equals("@")) {
			   buf = new StringBuffer().append("IN"+name.charAt(1));
			   for (int i = 2; i < name.length(); i++) {
				   
//				if (Character.isUpperCase(name.charAt(i))) {
//					/*
//					 * 如果是大写字母，并且不是最后一个字母，而且紧跟后面的字母不是大写字母，就加“_”下划线。
//					 * 比如，SystemError --->  SYSTEM_ERROR
//					 * SystemOK      ---->   SYSTEM_OK  
//					 */
//					if (i < name.length() - 1 && !Character.isUpperCase(name.charAt(i + 1)))
//						buf.append('_');
//					
//						
//				}
				
				String str=Character.toString(name.charAt(i));
				if(str.equals("."))
				{
					buf.append('_');
					 continue;
				}
				buf.append(name.charAt(i));
			}
		}
		else {
			buf = new StringBuffer().append(name.charAt(0));	
			for (int i = 1; i < name.length(); i++) {
				if (Character.isUpperCase(name.charAt(i))) {
					/*
					 * 如果是大写字母，并且不是最后一个字母，而且紧跟后面的字母不是大写字母，就加“_”下划线。
					 * 比如，SystemError --->  SYSTEM_ERROR
					 * SystemOK      ---->   SYSTEM_OK  
					 */
					if (i < name.length() - 1 && !Character.isUpperCase(name.charAt(i + 1)))
						buf.append('_');
				}
				String str=Character.toString(name.charAt(i));
				if(str.equals(".")) {
					buf.append('_');
					 continue;
				}
				buf.append(name.charAt(i));
			}
		}		
		return buf.toString().toUpperCase();
	}
	
	/*
	 * 把字符累加n次，作为string返回。
	 */
	private static StringBuffer nchar(char c, int n) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < n; i++)
			b.append(c);
		return b;
	}

	/*
	 * @author zsg
	 *
	 * 是一个内部类，扩展了Properties类的put方法。
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected static class KeyCheckProperties extends Properties {
		private java.util.Map comm = new java.util.HashMap();
		public Object put(Object key, Object value) {
			String valStr = null;
			try {
				valStr = new String(((String) value).getBytes("ISO8859_1")).trim();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (super.containsKey(key)) {
				java.util.List l = (java.util.List) keys.get(key);
				if (l == null) {
					l = new java.util.ArrayList();
					keys.put(key, l);
					try {
						String valStr0 = new String(((String) get(key)).getBytes("ISO8859_1"));
						l.add(valStr0);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				l.add(valStr);
			}
			String val0 = valStr;
			if(val0.indexOf('<') > 0){
				val0 = val0.substring(0, val0.indexOf('<'));
			}
			if (comm.containsValue(val0)) {
				java.util.List l = (java.util.List) comments.get(val0);
				if (l == null) {
					l = new java.util.ArrayList();
					comments.put(val0, l);
					for (java.util.Iterator i = comm.entrySet().iterator(); i.hasNext();) {
						java.util.Map.Entry entry = (java.util.Map.Entry) i.next();
						if (entry.getValue().equals(val0)) {
							l.add(entry.getKey());
						}
					}
				}
				l.add(key);
			}
			comm.put(key, val0);
			return super.put(key, value);
		}
	}
	
}
