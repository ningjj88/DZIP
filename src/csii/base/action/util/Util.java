package csii.base.action.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.csii.pe.common.util.CsiiUtils;
import com.sun.org.apache.bcel.internal.generic.DDIV;

import csii.base.constant.Constants;
import csii.dzip.core.Decrypt4Des;


/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-6-3
 */
public class Util {
	/**
	 *
	 * @param s
	 * @return
	 */
	public static final String formatDateTime(String s) {
		if (s == null || s.equals(""))
			return "&nbsp;";
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
			java.util.Date date = simpledateformat.parse(s,
					new ParsePosition(0));
			simpledateformat.applyPattern("yyyy-MM-dd");
			return simpledateformat.format(date);
		} catch (Exception exception) {
		}
		try {
			SimpleDateFormat simpledateformat1 = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			java.util.Date date1 = simpledateformat1.parse(s,
					new ParsePosition(0));
			simpledateformat1.applyPattern("yyyy-MM-dd");
			return simpledateformat1.format(date1);
		} catch (Exception exception1) {
		}
		try {
			SimpleDateFormat simpledateformat2 = new SimpleDateFormat("HHmmss");
			java.util.Date date2 = simpledateformat2.parse(s,
					new ParsePosition(0));
			simpledateformat2.applyPattern("HH:mm");
			return simpledateformat2.format(date2);
		} catch (Exception exception2) {
		}
		return s;
	}

	/**
	 * 获得当前时间的时间戳
	 *
	 * @author 张永庆
	 * @return CurrentTimestamp
	 */
	public static Timestamp getCurrentTimestamp() {
		Timestamp current = new Timestamp(System.currentTimeMillis());
		return current;
	}

	/**
	 * 根据日期字符串和日期格式得到时间戳
	 *
	 * @author 张永庆
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public static Timestamp getTimestamp(String dateString, String pattern) {
		SimpleDateFormat simpledateformat1;
		if (dateString == null)
			return getCurrentTimestamp();
		if (pattern == null)
			simpledateformat1 = new SimpleDateFormat("yyyyMMdd");
		else
			simpledateformat1 = new SimpleDateFormat(pattern);
		java.util.Date date1 = simpledateformat1.parse(dateString,
				new ParsePosition(0));
		simpledateformat1.applyPattern("yyyy-MM-dd HH:mm:ss");
		return Timestamp.valueOf(simpledateformat1.format(date1));
	}

	/**
	 * 根据日期字符串和日期格式得到Date对象
	 *
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public static Date getDate(String dateString, String pattern) {
		SimpleDateFormat simpledateformat1;
		if (dateString == null)
			return new Timestamp(System.currentTimeMillis());
		if (pattern == null)
			simpledateformat1 = new SimpleDateFormat("yyyyMMdd");
		else
			simpledateformat1 = new SimpleDateFormat(pattern);
		java.util.Date date1 = simpledateformat1.parse(dateString,
				new ParsePosition(0));
		return date1;
	}

	/**
	 *
	 * @param pattern
	 * @return
	 */
	public static final String getDateString(String pattern) {
		return getDateString(null, pattern);
	}

	/**
	 *
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static final String getDateString(Date date, String pattern) {
		if (date == null)
			date = new Date(System.currentTimeMillis());
		if (pattern == null || pattern.equals(""))
			pattern = "yyyyMMdd";
		String dataString = "";
		try {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(pattern);
			dataString = simpledateformat.format(date);
			return dataString;
		} catch (Exception exception) {
		}
		return dataString;
	}
	/**
	 *
	 * @param dateString
	 * @param pattern
	 * @return
	 */
	public static final String getFormatString(String dateString, String pattern) {

		try {
			SimpleDateFormat simpledateformat= new SimpleDateFormat("yyyyMMdd");
			Date date =  simpledateformat.parse(dateString);
			return getDateString(date ,pattern);

		} catch (Exception exception) {
			return null;
		}
	}

	/**
	 *
	 * @param s
	 * @return
	 */
	public static String formatDateString(String s) {
		SimpleDateFormat simpledateformat1 = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date1 = simpledateformat1.parse(s, new ParsePosition(0));
		simpledateformat1.applyPattern("yyyyMMdd");
		return simpledateformat1.format(date1);
	}

	/**
	 * 获得当前时间
	 *
	 * @return
	 */
	public static String getCurrentTime() {
		return DateFormat.getTimeInstance().format(new Date());
	}

	/**
	 * 获得当前行号
	 *
	 * @return
	 */
	public static int getLineNumber() {
		return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}

	/**
	 * 调试用，没有实际意义
	 * @return
	 */
	public static String info() {
		return Util.getCurrentTime() + "," + Util.getLineNumber()+" DEBUG";
	}

	/**
	 *
	 * @return
	 */
	public static String getAcctMsgId() {
		long i = System.currentTimeMillis();
		String str = String.valueOf(i);
		return str.substring(str.length() - 12, str.length());
	}

	/**
	 *
	 * @param targetStr
	 * @param strLength
	 * @return
	 */
	public static String SubStringByteTrim(String targetStr, int strLength) {
		int curLength = targetStr.getBytes().length;
		if (targetStr != null && curLength > strLength)
			targetStr = SubStringByte(targetStr, strLength);
		String newString = "";
		int cutLength = strLength - targetStr.getBytes().length;
		for (int i = 0; i < cutLength; i++)
			newString = newString + "0";
		return targetStr + newString;
	}

	/**
	 *
	 * @param targetStr
	 * @param strLength
	 * @return
	 */
	public static String SubStringByte(String targetStr, int strLength) {
		for (; targetStr.getBytes().length > strLength; targetStr = targetStr
				.substring(0, targetStr.length() - 1))
			;
		return targetStr;
	}

	public static String getAmountString(String amount) {
		if (amount.indexOf(".") != -1) {
			amount = amount.substring(0, amount.indexOf("."))
					+ amount.substring(amount.indexOf(".") + 1);
		} else {
			amount = amount + "00";
		}
		amount = "RMB" + CsiiUtils.prefixZero(amount, 15);
		return amount;
	}

	/**
	 * 校验字符串
	 *
	 * @param str
	 * @return 合法返回true ，否则返回false
	 */
	public static boolean checkString(String str) {
		if (str == null || str.trim().length() < 1) {
			return false;
		}
		return true;
	}

	/**
	 * 多个字符串连接
	 *
	 * @param String
	 *            ... strings
	 * @return String
	 */
	public static String appendString(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String str : strings)
			sb.append(str);
		return sb.toString();
	}
	/**
	 * 字符串转换为
	 * @param strValue
	 * @return
	 */
	  public static String StringToHex(String strValue) {
			byte byteData[] = null;
			int intHex = 0;
			String strHex = "";
			String strReturn = "";
			try {
				byteData = strValue.getBytes("ISO8859-1");
				for (int intI = 0; intI < byteData.length; intI++) {
					intHex = (int) byteData[intI];
					if (intHex < 0)
						intHex += 256;
					if (intHex < 16)
						strHex += "0" + Integer.toHexString(intHex).toUpperCase();
					else
						strHex += Integer.toHexString(intHex).toUpperCase();
				}
				strReturn = strHex;

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return strReturn;
		}
/**
 *
 * @param strValue
 * @return
 */
	  public static String HexToString(String strValue) {
			int intCounts = strValue.length() / 2;
			String strReturn = "";
			String strHex = "";
			int intHex = 0;
			byte byteData[] = new byte[intCounts];
			try {
				for (int intI = 0; intI < intCounts; intI++) {
					strHex = strValue.substring(0, 2);
					strValue = strValue.substring(2);
					intHex = Integer.parseInt(strHex, 16);
					if (intHex > 128)
						intHex = intHex - 256;
					byteData[intI] = (byte) intHex;
				}
				strReturn = new String(byteData,"ISO8859-1");
			} catch (Exception ex) {
			ex.printStackTrace();
		}
		return strReturn;
	}

	/**
	 * @author xujin
	 * @desc   格式化姓名（张三 -> *三 ； 张三四 -> *三四 ； 爱新觉罗*方平 -> *方平 ）
	 * @date    2012.03.24
	 */
	public static String formatAcctName(String acctName){
		if(acctName!=null&&!Constants.PE_NULL.equals(acctName)){
			int len=acctName.length();
			if(len>2)
				acctName=acctName.substring(len-2);
			else
				acctName=acctName.substring(len-1);
			acctName="*".concat(acctName);//把姓转为*
		}
		return acctName;
	}

	/**
	 * 格式化持卡人的手机号码 长度指定为11
	 * @param phoneNumber 手机号码
	 * @return 格式化后的电话字符串
	 */
	public static String formatePhoneNumber(String phoneNumber){
		StringBuffer sBuffer = new StringBuffer(phoneNumber);
		sBuffer.replace(3, 7, "****");
		return sBuffer.toString();
	}

	/**
	 *
	 * @param pan
	 * @param pin
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public static String  formatAmt(Map map){
		StringBuffer balAmtBuf = new StringBuffer() ;
		StringBuffer availBalAmtBuf = new StringBuffer() ;
		BigDecimal balAmt = BigDecimal.ZERO;
		BigDecimal availBalAmt = BigDecimal.ZERO;
		if(map.get(Constants.BALAMT)!=null)
			balAmt = Util.getAmount(String.valueOf(map.get(Constants.BALAMT))).movePointRight(2);
//			balAmt =(Double.parseDouble(String.valueOf(map.get(Constants.BALAMT)))*100) ;
		if(map.get(Constants.AVAILBALAMT) !=null)
			availBalAmt = Util.getAmount(String.valueOf(map.get(Constants.AVAILBALAMT))).movePointRight(2);
//			availBalAmt = (long)(Double.parseDouble(String.valueOf(map.get(Constants.AVAILBALAMT)))*100) ;
		String balAmtStr = String.valueOf(balAmt);
		String availBalAmtStr = String.valueOf(availBalAmt);
		if(map.get(Constants.ACCTTYPCD) != null){
			balAmtBuf.append(map.get(Constants.ACCTTYPCD));
			availBalAmtBuf.append(map.get(Constants.ACCTTYPCD));
		}else{
			balAmtBuf.append("00");
			availBalAmtBuf.append("00");
		}
		balAmtBuf.append("01");
		availBalAmtBuf.append("02");
		balAmtBuf.append("156");
		availBalAmtBuf.append("156");
		balAmtBuf.append(getCDFlag(balAmtStr));
		availBalAmtBuf.append(getCDFlag(availBalAmtStr));
		if(balAmtStr.length()>12){
			balAmtBuf.append(balAmtStr.substring(0, 12));
		}else{
		    balAmtBuf.append(fill(balAmtStr,12,Constants.LEFT,Constants.PE_ZERO));
		}
		if(String.valueOf(availBalAmt.abs()).length()>12){
			availBalAmtBuf.append(String.valueOf(availBalAmt.abs()).substring(0, 12));
		}else{
			availBalAmtBuf.append(fill(String.valueOf(availBalAmt.abs()),12,Constants.LEFT,Constants.PE_ZERO));
		}
		return balAmtBuf.append(availBalAmtBuf).toString();
	}

	/**
	 *  填充字符串，达到指定的长度
	 * @param str  需要被填充的字符串
	 * @param length  要达到的长度
	 * @param direction  左或右填充
	 * @param content    填充的内容
	 * @return   填充后的字符串
	 */
	public static String fill(String str, int length, String direction, String content) {

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
	 * 产生借贷标记
	 * @param data
	 * @return
	 */
	public static String  getCDFlag(String data){
		if(getAmount(data).signum() > 0)
				return "C";
			else
				return "D";

	}
	/**
	 * 转换字符串123.50为Money类型
	 */
	public static BigDecimal getAmount(String amountText){
		if(amountText==null || amountText.trim().isEmpty())
			return BigDecimal.ZERO;
		else
			return new BigDecimal(amountText);
	}

	/**
	 * 将交易金额格式化成8583#3格式
	 * @param amt
	 * @return
	 */
	public static String getAmt(String amt){
		BigDecimal bdAmt = new BigDecimal(amt);
		bdAmt = bdAmt.multiply(new BigDecimal(100));

		return fill(String.valueOf(bdAmt.intValue()), 12, Constants.LEFT, Constants.PE_ZERO);
	}

	public static void main(String[] arg) throws ParseException {
//		SimpleDateFormat simpledateformat= new SimpleDateFormat("MMdd");
//		Date date=  simpledateformat.parse("0208");
//		Date date = getDate("20110114", "yyyyMMdd");
//		System.out.println(date);
//		System.out.println(Util.getFormatString("1124" ,"MMdd"));
//			System.out.println(Util.getDateString("yyyy"));
//			System.out.println(Util.getDateString("MM-dd"));
//			System.out.println(Util.getDateString("HH:mm:ss"));
//		System.out.println(Util.getDateString("yyyyMMddHHmmss"));
	//	System.out.println(Util.StringToHex("111111111"));
//		System.out.println(CsiiStringUtils.formatString("的", 20));
//		System.out.println("1323===="+(0.0==0));
//		StringBuffer subKeyValue  =new StringBuffer("");
//		String prefixName ="";
//		System.out.println("=====>"+prefixName+subKeyValue.toString());
//		String strValue=HexToString("A20A636739937B95");
//		System.out.println("StringToHex=====>"+StringToHex("¢\ncg9{ "));//¢\ncg9{
//		System.out.println("HexToString=====>"+HexToString("A20A636739937B95"));
//
//		String acqOrgCd="05576750";
//		int beginIndex=0;
//		int endIndex=(beginIndex+8)>acqOrgCd.length()?acqOrgCd.length():beginIndex+8;
//		String value =acqOrgCd.substring(beginIndex, endIndex);//在报文中取出
//		value = value.trim();
//		beginIndex = endIndex;
//		System.out.println("value1=====>"+value);
//		endIndex=(beginIndex+8)>acqOrgCd.length()?acqOrgCd.length():beginIndex+8;
//		value =acqOrgCd.substring(beginIndex, endIndex);//在报文中取出
//		value = value.trim();
//		beginIndex = endIndex;
		System.out.println(getAmt("10"));
	}
}
