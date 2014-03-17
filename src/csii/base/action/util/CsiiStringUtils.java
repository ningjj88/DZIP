/*
 * 创建日期 2006-1-16
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package csii.base.action.util;


/**
 * @author db2admin
 *
 * TODO 要更改此生成的类型注释的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
public class CsiiStringUtils {

	/**
	 * 取中文字符串前len个字节,如此长度的字节刚好位于一个中文字符的中间,返回的字符串的字节长度 将是len-1.
	 * 
	 * @param gbString
	 *            中文字符串
	 * @param len
	 *            长度-指字节长度
	 * @return
	 */
	public static String divideGBString(String gbString, int len) {
		if (gbString == null) {
			return "";
		}

		try {
			byte[] bytes = gbString.getBytes();

			if (len >= bytes.length) {
				return gbString;
			}

			byte[] nbytes = new byte[len];
			boolean hz = false; //用以标明是否刚好取到一个汉字中间
			boolean hasSapce = false; //最后是否有空值
			int i = 0;

			while (i < len) {
				if ((bytes[i] & 0x80) > 0) {
					//汉字
					if (!hz) {
						//汉字的第一个字节
						if (i == (len - 1)) {
							//达到截取长度,还只是汉字的第一字节,退出循环
							hasSapce = true;

							break;
						}

						nbytes[i] = bytes[i];
						hz = true;
					} else {
						//汉字的第二个字节
						nbytes[i] = bytes[i];
						hz = false;
					}
				} else {
					//非汉字
					nbytes[i] = bytes[i];
					hz = false;
				}
				i++;
			}

			byte[] copy = null;

			if (hasSapce) {
				//去掉最后一个空值
				copy = new byte[len - 1];
				System.arraycopy(nbytes, 0, copy, 0, len - 1);
			} else {
				copy = nbytes;
			}

			return new String(copy);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * 将字符串填充到len长,不足len长后面加空格,超过截断
	 * 
	 * @param str
	 * @param len
	 *            是指字节长度
	 * @return
	 */
	public static String formatString(Object object, int len) {
		String str = (object == null) ? "" : object.toString().trim() ;

		while (str.getBytes().length > len) {
			str = str.substring(0, str.length() - 1);
		}

		int strLen = str.getBytes().length;

		str = str + repeat(" ", len - strLen);
		return str;
	}
	
	
	/**
	 * 将字符串填充到len长,不足len长前面加0,超过截断
	 * lanxiaobin
	 * @param str
	 * @param len
	 *            是指字节长度
	 * @return
	 */
	public static String formatISoString(Object object, int len) {
		String str = (object == null) ? "" : object.toString().trim() ;

		while (str.getBytes().length > len) {
			str = str.substring(0, str.length() - 1);
		}

		int strLen = str.getBytes().length;

		str = repeat("0", len - strLen) + str;
		return str;
	}
	
	
	
	public static String repeat(String str, int num) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < num; i++) {
			buf.append(str);
		}
		return buf.toString();
	}
	/**
	 * 字符串替换,用replaceWith替换str中的replaced
	 * 
	 * @param str
	 * @param replaced
	 * @param replaceWith
	 * @return
	 */
	public static String replace(String str, String replaced, String replaceWith) {
		if (str == null) {
			return null;
		}
		if (replaced == null || replaceWith == null) {
			return str;
		}
		StringBuffer buf = new StringBuffer();

		int pos = str.indexOf(replaced);
		if (pos > -1) {
			String left = str.substring(0, pos);
			String right = str.substring(pos + replaced.length());
			buf.append(left);
			buf.append(replaceWith);
			buf.append(replace(right, replaced, replaceWith));
		} else {
			buf.append(str);
		}

		return buf.toString();
	}
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
	public static final String getParamString(String[] fields) {
		return getParamString(fields.length);
	}

	public static final String getParamString(int length) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				buf.append("?");
			} else {
				buf.append(",");
				buf.append("?");
			}
		}
		return buf.toString();
	}

	public static String format(java.util.List list, String[] keyNames)
	{
		StringBuffer buffer = new StringBuffer();		
		for(java.util.Iterator it = list.iterator(); it.hasNext(); )
		{
			java.util.Map map = (java.util.Map)it.next();
			for(int i=0; i<keyNames.length; i++)
			{
				String value = format(map.get(keyNames[i]));
				buffer.append('"').append(value).append('"');
				if(i < keyNames.length -1 )
					buffer.append(',');	
			}
			buffer.append('\n');
		}
		return buffer.toString();
	}
	
	public static String format(Object obj)
	{
		if(obj == null)
			return "";
		return obj.toString();
	}
	public static String conversionSpecialCharacters(String fileRootPath)
	{
		return fileRootPath.replaceAll("\\","\\\\");
	}
	public static void main(String[] args) {
		java.util.List a = new java.util.ArrayList();
		java.util.Map b = new java.util.HashMap();
		b.put("a","test1");
		b.put("b","test2");
		b.put("c","test3");
		b.put("d","test4");
		b.put("e","test5");
		a.add(b);
		a.add(b);
		a.add(b);
		System.err.println(format(a, new String[]{"a","b","c","d","e"}));
		System.out.println(formatISoString("9",8));
		System.out.println(conversionSpecialCharacters("C:\\"));
		
	
	}
}
