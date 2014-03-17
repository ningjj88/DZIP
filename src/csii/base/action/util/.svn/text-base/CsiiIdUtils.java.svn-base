/*
 * 创建日期 2008-8-2
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package csii.base.action.util;

import java.sql.Date;

/**
 * @author lanxiaobin
 *
 * TODO 要更改此生成的类型注释的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
public class CsiiIdUtils {
	
	/**
	 * date 是指日期java.sql.date 
	 * 将字符串填充到len长,不足len长前面加0,超过截断
	 * lanxiaobin
	 * @param str
	 * @param len
	 *  是指字节长度
	 * @return
	 */
	public static String generateId(Date date,Object object, int len) {
		return CsiiDateUtils.formatCompactDate(date) 
		        +CsiiStringUtils.formatISoString(object,len);	
	}
	
	
	/**
	 * date 是指日期java.sql.date 
	 * 将字符串填充到len长,不足len长前面加0,超过截断
	 * lanxiaobin
	 * @param str
	 * @param len
	 *  是指字节长度
	 * @return
	 */
	public static String generateId(String flag,Date date,Object object, int len) {
		return flag
		        +CsiiDateUtils.formatCompactDate(date) 
		        +CsiiStringUtils.formatISoString(object,len);	
	}
}
