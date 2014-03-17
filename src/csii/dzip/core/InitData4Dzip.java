/**
 *
 */
package csii.dzip.core;

import java.math.BigDecimal;

import org.springframework.beans.factory.InitializingBean;

import csii.base.action.util.Util;
import csii.dzip.action.util.DzipProcessTemplate;


/**
 * @author  csii
 * @function 在程序启动时查询本项目所需的初始化数据
 * @version 1.0.0
 * @since 2010-9-15
 */
public class InitData4Dzip implements InitializingBean{
	private static String JCOrgNbr;   		 	// 查询参数 ：锦城卡机构号
	private static String YLOrgNbr;    			// 查询参数 ：银联机构号
	private static String JCCashBoxNbr;    		// 查询参数 ：锦城卡钞箱号
	private static String YLCashBoxNbr;    		// 查询参数 ：银联钞箱号
	private static String JCPersNbr;    		// 查询参数 ：锦城卡柜员号
	private static String YLPersNbr;    		// 查询参数 ：银联柜员号
	private static String JCNtwkNodeNbr;    	// 查询参数 ：锦程卡发送交易站点号
	private static String YLNtwkNodeNbr;    	// 查询参数 ：银联前置发送交易站点号
	private static String DataStoredDays;       // 查询参数 ：流水数据保存天数
	private static String AcqOrgCd;             //查询参数  ：受理机构代码
	private static String ForwOrgCd;            //查询参数  ：发送机构代码
	private static String AcqOrgCd4Ic;			//查询参数:受理机构代码(IC卡)
	private static String ForwOrgCd4Ic;			//查询参数:发送机构代码(IC卡)
	private static String RcvgCd;               //查询参数  ：接收机构代码
	private static int MaxHoldDays;    	     // 查询参数 ：预授权交易最大冻结天数
	private static BigDecimal PreAuthCompProp ;     // 查询参数 ：预授权完成比例

	private DzipProcessTemplate dzipProcessTemplate;


	public InitData4Dzip() {

	}


	public void afterPropertiesSet() throws Exception {
		this.setJCOrgNbr();
		this.setYLOrgNbr();
		this.setJCCashBoxNbr();
		this.setYLCashBoxNbr();
		this.setJCPersNbr();
		this.setYLPersNbr();
		this.setJCNtwkNodeNbr();
		this.setYLNtwkNodeNbr();
		this.setMaxHoldDays();
		this.setPreAuthCompProp();
		this.setDataStoredDays();
		this.setAcqOrgCd();
		this.setForwOrgCd();
		this.setAcqOrgCd4Ic();
		this.setForwOrgCd4Ic();
		this.setRcvgCd();
	}

	public static String getJCNtwkNodeNbr() {
		return JCNtwkNodeNbr;
	}


	public void setJCNtwkNodeNbr() {
		JCNtwkNodeNbr = dzipProcessTemplate.queryParam("JCNtwkNodeNbr"); // 查询参数 ：锦城卡前置站点号
	}

	public static String getYLNtwkNodeNbr() {
		return YLNtwkNodeNbr;
	}


	public void setYLNtwkNodeNbr() {
		YLNtwkNodeNbr = dzipProcessTemplate.queryParam("YLNtwkNodeNbr"); // 查询参数 ：银联前置站点号
	}


	public static String getJCOrgNbr() {
		return JCOrgNbr;
	}


	public void setJCOrgNbr() {
		JCOrgNbr = dzipProcessTemplate.queryParam("JCOrgNbr"); // 查询参数 ：锦城卡前置机构号
	}


	public static String getYLOrgNbr() {
		return YLOrgNbr;
	}


	public void setYLOrgNbr() {
		YLOrgNbr = dzipProcessTemplate.queryParam("YLOrgNbr"); // 查询参数 ：银联前置机构号
	}



	public static String getJCCashBoxNbr() {
		return JCCashBoxNbr;
	}


	public void setJCCashBoxNbr() {
		JCCashBoxNbr = dzipProcessTemplate.queryParam("JCCashBoxNbr"); // 查询参数 ：锦城卡前置钱箱号
	}


	public static String getYLCashBoxNbr() {
		return YLCashBoxNbr;
	}


	public  void setYLCashBoxNbr() {
		YLCashBoxNbr = dzipProcessTemplate.queryParam("YLCashBoxNbr"); // 查询参数 ：银联前置钱箱号
	}


	public static String getJCPersNbr() {
		return JCPersNbr;
	}


	public  void setJCPersNbr() {
		JCPersNbr = dzipProcessTemplate.queryParam("JCPersNbr"); // 查询参数 ：锦城卡前置柜员号
	}


	public static String getYLPersNbr() {
		return YLPersNbr;
	}


	public  void setYLPersNbr() {
		YLPersNbr = dzipProcessTemplate.queryParam("YLPersNbr"); // 查询参数 ：银联前置柜员号
	}



	public static String getDataStoredDays() {
		return DataStoredDays;
	}

	public  void setDataStoredDays() {
		DataStoredDays = dzipProcessTemplate.queryParam("DataStoredDays");// 查询参数 ：流水数据保存天数
	}

	public static String getAcqOrgCd() {
		return AcqOrgCd;
	}

	public  void setAcqOrgCd() {
		AcqOrgCd = dzipProcessTemplate.queryParam("AcqOrgCd"); //查询参数  ：受理机构代码
	}

	public static String getForwOrgCd() {
		return ForwOrgCd;
	}

	public  void setForwOrgCd() {
		ForwOrgCd = dzipProcessTemplate.queryParam("ForwOrgCd");//查询参数  ：发送机构代码
	}

	public static String getRcvgCd() {
		return RcvgCd;
	}

	public  void setRcvgCd() {
		RcvgCd = dzipProcessTemplate.queryParam("RcvgCd");//查询参数  ：接收机构代码
	}


	public static int getMaxHoldDays() {
		return MaxHoldDays;
	}

	public  void setMaxHoldDays() {
		MaxHoldDays =Integer.parseInt(dzipProcessTemplate.queryParam("MaxHoldDays")); //查询参数 ：预授权交易最大冻结天数
	}
	public static BigDecimal getPreAuthCompProp() {
		return PreAuthCompProp;
	}

	public  void setPreAuthCompProp() {
		PreAuthCompProp =Util.getAmount(dzipProcessTemplate.queryParam("PreAuthCompProp")); //查询参数 ：预授权交易最大冻结金额
	}

	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}

	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}


	public static String getAcqOrgCd4Ic() {
		return AcqOrgCd4Ic;
	}


	public void setAcqOrgCd4Ic() {
		AcqOrgCd4Ic = this.dzipProcessTemplate.queryParam("AcqOrgCd4Ic");
	}


	public static String getForwOrgCd4Ic() {
		return ForwOrgCd4Ic;
	}


	public void setForwOrgCd4Ic() {
		ForwOrgCd4Ic = this.dzipProcessTemplate.queryParam("ForwOrgCd4Ic");
	}


}
