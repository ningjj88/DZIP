/*
 * 创建日期 2006-1-16
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package csii.base.action.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.core.PeRuntimeException;

/**
 * @author db2admin
 *
 * TODO 要更改此生成的类型注释的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
public class CsiiNumberUtils {
	

	private static final Log log = LogFactory.getLog(CsiiNumberUtils.class);
	private static final char ZERO_CHAR = '0';
	private static final char PATTERN_PREFIX = '#';
	public static final BigDecimal HUNDRED = new BigDecimal(100);
	public final static BigDecimal ZERO = new BigDecimal(0);

	public static final int toInt(String str) {
		return toInt(str, 0);
	}
	public static final int toInt(String str, int defaultValue) {
		if (CsiiStringUtils.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(str.trim());
		} catch (Throwable t) {
			log.error("toInt(" + str + ") error:" + t.getMessage(), t);
			return defaultValue;
		}
	}
	public static final BigDecimal toBigDecimalWithDot(String str,
			BigDecimal defaultValue) {
		if (CsiiStringUtils.isEmpty(str)) {
			return defaultValue;
		}
		try {
			return new BigDecimal(str.trim());
		} catch (NumberFormatException e) {
			log.error("toBigDeciaml(" + str + ") error:" + e.getMessage(), e);
			return defaultValue;
		}
	}

	public static final BigDecimal toBigDecimalWithDot(String str) {
		return toBigDecimalWithDot(str, ZERO);
	}

	public static final BigDecimal toBigDecimalWithoutDot(String str,
			int decimal, BigDecimal defaultValue) {
		if (CsiiStringUtils.isEmpty(str)) {
			return defaultValue;
		}
		str = str.trim();
		if (str.length() < decimal) {
			throw new NumberFormatException("the length of string " + str
					+ " less than " + decimal);
		}
		str = str.substring(0, str.length() - decimal) + "."
				+ str.substring(str.length() - decimal);
		return toBigDecimalWithDot(str, defaultValue);
	}

	public static final BigDecimal toBigDecimalWithoutDot(String str,
			int decimal) {
		return toBigDecimalWithoutDot(str, decimal, ZERO);
	}
	public static final Integer toInteger(String str) {
		return Integer.valueOf(str);
	}
	

	/**
	 * 格式化整数为len长的字符串,如不够长度,前面补空格
	 * 
	 * @param number
	 * @param len
	 * @return
	 */
	public static String formatIntegarNumber(Object number, int len) {
		return formatAmountWithoutDot(number, len, 0,"0");
	}

	
	/**
	 * 将浮点数格式化为总长为len,小数位为decimal长的格式串,包括小数点,不够长度,前面加空格
	 * 
	 * @param number
	 * @param len
	 * @param decimal
	 * @return
	 */
	public static String formatAmountWithDot(Object number, int len, int decimal,String fillValue) {
		String pattern = "0." + CsiiStringUtils.repeat("0", decimal);
		DecimalFormat df = new DecimalFormat(pattern);
		String value = null;
		if (number instanceof Number) {
			value = df.format((Number) number);
		} else {
			value = df.format(new BigDecimal(number.toString()));
		}
		return CsiiStringUtils.repeat(fillValue, len - value.length()) + value;
	}
	/**
	 * 将浮点数格式化为总长为len,小数位为decimal长的格式串,不包括小数点,不够长度,前面加0
	 * 
	 * @param number
	 * @param len
	 * @param decimal
	 * @return
	 */
	public static String formatAmountWithoutDot(Object number, int len,int decimal,String fillValue) {
		String value = (number == null) ? "" : number.toString().trim();
		boolean negative = value.startsWith("-");
		if (negative) {
			value = value.substring(1);
		}
		int pos = value.indexOf('.');
		String left = value;
		if (pos != -1) {
			left = value.substring(0, pos);
		}
		if (left.length() == 0) {
			left = "0";
		}
		String right = "";
		if (pos != -1) {
			right = value.substring(pos + 1);
		}
		if (decimal >= 0) {
			if (right.length() > decimal) {
				right = right.substring(0, decimal);
			}
			int rightLen = right.length();
			for (int i = 0; i < decimal - rightLen; i++) {
				right += "0";
			}
		}
		value = left + right;
		String msg = number.toString()+ " is bigger,can't be fromated to string with length " + len	+ ",decimal length " + decimal;
		if (value.length() > len) {
			throw new IllegalArgumentException(msg);
		}
		value = CsiiStringUtils.repeat("0", len - value.length()) + value;
		if (negative) {
			if (value.startsWith("0")) {
				value = "-" + value.substring(1);
			} else {
				throw new IllegalArgumentException(msg);
			}
		}
		return value;
	}

	public static BigDecimal getBigDecimal(Object object) {
		if (object == null)
			return null;

		BigDecimal result;

		if (object instanceof BigDecimal)
			result = (BigDecimal) object;
		else if (object instanceof String) {
			result = new BigDecimal((String) object);
		} else if (object instanceof Number) {
			result = new BigDecimal(((Number) object).toString());
		} else
			throw new PeRuntimeException(
				"unsupported_bigdecimal_convert",
				new Object[] { object });

		return result;

	}

	public static BigDecimal getTrsAmount(Object object) {
		BigDecimal result = getBigDecimal(object);
		if (result == null)
			return null;
		if (result.compareTo(ZERO) == 0)
			return null;
		else
			return result;

	}	



}
