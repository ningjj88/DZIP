package csii.pe.service.comm;

import java.io.UnsupportedEncodingException;

public class StringChange {

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
	 * 字符串异或函数
	 *
	 * @param str1
	 * @param str2
	 * @return str1^str2
	 */
	public static String StringXor(String str1, String str2) {
		StringBuffer strValue = new StringBuffer();
		int num1,num2,num;
		byte[] bstr1=null;
		byte[] bstr2=null;
		try {
			bstr1 = str1.getBytes("ISO8859-1");
			bstr2 = str2.getBytes("ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < str1.length(); i ++) {
			num1 = bstr1[i];
			if(num1 < 0)
				num1 += 256;
			num2 = bstr2[i];
			if(num2 < 0)
				num2 += 256;
			num  = (int) (num1^num2);
			strValue.append((char)num);
		}
		return strValue.toString();
	}

	public static void main(String[] args)
	{
	 String Hex="3131313131313030";
	 String Str;

	 System.out.println("hex="+Hex);
	 Str=StringChange.HexToString(Hex);
	 System.out.println("str="+Str);
	 Hex=StringChange.StringToHex(Str);
	 System.out.println("hex="+Hex);
	 Str=StringChange.HexToString(Hex);
	 System.out.println("str="+Str);
	}

}
