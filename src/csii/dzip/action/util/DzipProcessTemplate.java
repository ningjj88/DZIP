/**
 *
 */
package csii.dzip.action.util;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.base.constant.SqlMaps;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-7-20
 */
public class DzipProcessTemplate {


	private JdbcAccessAwareProcessor dzipdbAccess;   		//前置数据库连接
	private JdbcAccessAwareProcessor corebankAccess;		//核心数据库连接
	protected final Log logger = LogFactory.getLog(getClass());
	private String channID;
	/**
	 * 查询渠道状态
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean queryChannlSta(String channid){
		Map map =new HashMap();
		map.put(Constants.PE_CHANN_ID,channid);
        try {
			map=(Map) dzipdbAccess.getSqlMap().queryForObject("common.querychannSat",map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return false;
		}
		String channstat = (String) map.get(Constants.PE_CHANN_STAT);
		if (Constants.PE_ONE.equals(channstat))  //渠道状态：1、可用；0、不可用
			return true;
		else
			return false;
	}
	/**
	 * 判断交易是否可以进行
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean queryTranSta(final Map map ){
		String transtat=null;
		Map result = new HashMap();
        try {
        	result=(Map) dzipdbAccess.getSqlMap().queryForObject("common.queryTranSat",map);
        	map.put(Constants.PE_CRDB, result.get(Constants.PE_CRDB));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return false;
		}
		if(result != null &&result.get(Constants.PE_TRANS_STAT)!=null){
			transtat = (String) result.get(Constants.PE_TRANS_STAT);
		}
		if (Constants.PE_ONE.equals(transtat))  //渠道交易状态：1、可用；0、不可用
			return true;
		else
			return false;
	}
	/**
	 * 判断交易是否可以进行
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean querychannTranSta(final Map map ){
		String channstat=null;
		String transtat=null;
		String channelTypStat=null;
		Map result = new HashMap();
        try {
        	result=(Map) dzipdbAccess.getSqlMap().queryForObject("common.querychannTranSat",map);
        	map.put(Constants.PE_CRDB, result.get(Constants.PE_CRDB));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return false;
		}
		if(result != null &&result.get(Constants.PE_TRANS_STAT)!=null){
			channstat = (String) result.get(Constants.PE_CHANN_STAT);
			transtat = (String) result.get(Constants.PE_TRANS_STAT);
			channelTypStat=(String) result.get(Constants.PE_CHANNELTYPSTAT);
		}
		if (Constants.PE_ONE.equals(channstat)&&Constants.PE_ONE.equals(transtat)
				&&Constants.PE_ONE.equals(channelTypStat))  //渠道状态,渠道交易状态,渠道类型状态：1、可用；0、不可用
			return true;
		else
			return false;
	}
	/**
	 * 查询核心系统状态为"ONLINE"的"PDAT"日期
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String queryPostDate()throws SQLException{
		Map map = (Map) corebankAccess.getSqlMap().queryForObject("common.querySysstat");
		String postDate = (String)map.get(Constants.PE_POST_DATE);
		return postDate;
	}

	/**
	 * 查询核心系统状态为"BATCH"的"CDAT"日期
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String queryPostDateBatch()throws SQLException{
		Map map = (Map) corebankAccess.getSqlMap().queryForObject("common.querySysstatBatch");
		String postDate = (String)map.get(Constants.PE_POST_DATE);
		return postDate;
	}
	/**
	 * 收集对账文件数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getCheckDetail(final String postDate){
		final Map paramMap = new HashMap();
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				paramMap.put(Constants.IN_POSTDATE, postDate);
				dzipdbAccess.getSqlMap().queryForObject("cups.getCheckDetail", paramMap);
				return null;
			}
		});
		return paramMap;
	}

	public String queryAreaRegion(String areaRegioncd){
		String areaRegionDes = null;
		try {
			areaRegionDes = (String) dzipdbAccess.getSqlMap().queryForObject("common.queryAreaRegion", areaRegioncd);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("查询地区代码信息出错" + e.getMessage());
		}
		return areaRegionDes;
	}

	/**
	 * 查询平台原交易号
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String queryTraceNum(String peJournalNO)throws Exception{
		Map map=new HashMap();
	    String traceNum=null;
	    try {
			map = (Map) dzipdbAccess.getSqlMap().queryForObject("common.queryTraceNum",peJournalNO);
	    } catch (Exception e) {
	    	throw new Exception(e.getMessage());
	    }
	    if(map==null){
	    	throw new Exception("没有找到系统跟踪号");
	    }
	    traceNum = String.valueOf(map.get(Constants.SYSTRACENUM));
		return traceNum;
	}

	/**
	 * 查询系统日期和状态
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getHostStatPostDate(final Context ctx){
		String responcd=null;
		Map  resultMap =new HashMap();
		 try {
			 corebankAccess.getSqlMap().update("common.getSysStatDate",resultMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(resultMap==null){
				return null;
			}else {
				int errornbr=Integer.valueOf(String.valueOf(resultMap.get(Constants.OUT_ERROR_NBR)));
				if(errornbr==0){
					ctx.setData(Constants.PE_POST_DATE, resultMap.get(Constants.OUT_POSTDATE)); // 填充账务日期
					ctx.setData(Constants.PE_HOSTCHKCD,resultMap.get(Constants.OUT_SYSTEMSTAT));//主机对账标识
				}
				responcd=(String)resultMap.get(Constants.OUT_RESPONCD);
			return responcd;
			}
	}

	/**
	 * 获得系统交易参数信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getSysTranInfo(final Context ctx,String type8583){
		String responcd=null;
		Map  resultMap =new HashMap();
		String in_cardnbr=null;
		String terminalCd=null;
		if(Constants.ISO8583.equals(type8583)){
			in_cardnbr = (String)ctx.getData(Constants.ISO8583_ACCTNO);
			terminalCd=(String)ctx.getData(Constants.ISO8583_CARDACCID);
		}
		if(Constants.FIX8583.equals(type8583)){
			in_cardnbr = (String)ctx.getData(Constants.FIX8583_PAN);
			terminalCd=(String)ctx.getData(Constants.FIX8583_CATI);
		}
		resultMap.put(Constants.IN_ACCTNBR,in_cardnbr);
		resultMap.put(Constants.IN_TERMINALCD, terminalCd);
		resultMap.put(Constants.IN_RTXNCATCD, ctx.getData(Constants.RTXNCATCD));
		 try {
			 corebankAccess.getSqlMap().update("common.getSysTranInfo",resultMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(resultMap==null){
				return Constants.PE_SYSTEM_ERROR;
			}else {
				int errornbr=Integer.valueOf(String.valueOf(resultMap.get(Constants.OUT_ERROR_NBR)));
				if(errornbr==0){
					ctx.setData(Constants.PE_POST_DATE, resultMap.get(Constants.OUT_POSTDATE)); // 填充账务日期
					ctx.setData(Constants.PE_HOSTCHKCD,resultMap.get(Constants.OUT_SYSTEMSTAT));//主机对账标识
					ctx.setData(Constants.IN_ORGNBR, resultMap.get(Constants.OUT_ORGNBR)); //机构号数据库配置
					ctx.setData(Constants.IN_CASHBOXNBR, resultMap.get(Constants.OUT_CASHBOXNBR)); // 钱箱号,数据库配置参数
					ctx.setData(Constants.IN_ORIGPERSNBR,  resultMap.get(Constants.OUT_PERSNBR)); // 柜员号，数据库配置参数
					ctx.setData(Constants.IN_ORIGNTWKNODENBR, resultMap.get(Constants.OUT_NTWKNODENBR)); // 交易站点号，数据库配置参数
					ctx.setData(Constants.TAXRPTFORPERSNBR, resultMap.get(Constants.OUT_CUSTOMID));      // 填充客户号

				}
				responcd=(String)resultMap.get(Constants.OUT_RESPONCD);
			    return responcd;
			}
	}

	/**
	 * 查询系统日期和状态
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getHostStatPostDate(){
		Map  resultMap =new HashMap();
		 try {
			 corebankAccess.getSqlMap().update("common.getSysStatDate",resultMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(resultMap==null){
				return null;
			}else {
			    return resultMap;
			}
	}
	/**
	 * 获得系统状态和帐务日期
	 * @return
	 */
	public boolean queryHostStat(){
		String  flagYN ="";
		 try {
			 flagYN = (String)corebankAccess.getSqlMap().queryForObject("common.queryHostStat");
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		if("N".equals(flagYN))
			return true;
		else
			return false;
	}

	/**
	 * 为实现7*24小时我们在表【BANKOPTION】中新增系统参数【UCOD】用于通知外围系统是否取下一个工作日(如果【UCOD】="Y",取下一个工作日)
	 * @return
	 */
	public String querysysdate(){
		String systemDate=null;
		 try {
			 systemDate = (String)dzipdbAccess.getSqlMap().queryForObject("common.query_sysdate");
			} catch (Exception e) {
				e.printStackTrace();
			}
		 return systemDate;
	}
	/**
	 * 查询客户号
	 * @return
	 */
	public String getTaxrptForPersNbr(final Context ctx,String type8583 ){
		String in_cardnbr = "";
		if(Constants.ISO8583.equals(type8583))
			in_cardnbr = (String)ctx.getData(Constants.ISO8583_ACCTNO);
		if(Constants.FIX8583.equals(type8583))
			in_cardnbr = (String)ctx.getData(Constants.FIX8583_PAN);
		String taxrptForPersNbr=null;
		 try {
			 taxrptForPersNbr = (String)corebankAccess.getSqlMap().queryForObject("common.query_taxrptForPersNbr",in_cardnbr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(taxrptForPersNbr ==null)
			{
				if(Constants.ISO8583.equals(type8583))
				    ctx.setData(Constants.ISO8583_RESCODE, "14");//如果没有客户号，返回：无效卡号
			    if(Constants.FIX8583.equals(type8583))
			    	ctx.setData(Constants.FIX8583_RESP, "14");//如果没有客户号，返回：无效卡号
			}
			return taxrptForPersNbr;
	}
	/**
	 * 查询客户号<BR>
	 * 不会返回多行
	 * @return
	 */
	public String queryTaxrptForPersNbr(final Context ctx,String type8583 ){
		String in_cardnbr = "";
		if(Constants.ISO8583.equals(type8583))
			in_cardnbr = (String)ctx.getData(Constants.ISO8583_ACCTNO);
		if(Constants.FIX8583.equals(type8583))
			in_cardnbr = (String)ctx.getData(Constants.FIX8583_PAN);
		String taxrptForPersNbr=null;
		 try {
			 taxrptForPersNbr = (String)corebankAccess.getSqlMap().queryForObject("common.queryTaxrptForPersNbr",in_cardnbr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(taxrptForPersNbr ==null)
			{
				if(Constants.ISO8583.equals(type8583))
				    ctx.setData(Constants.ISO8583_RESCODE, "14");//如果没有客户号，返回：无效卡号
			    if(Constants.FIX8583.equals(type8583))
			    	ctx.setData(Constants.FIX8583_RESP, "14");//如果没有客户号，返回：无效卡号
			}
			return taxrptForPersNbr;
	}
	/**
	 * 查询ATM本代他是终端号对应的钱箱，柜员，机构号。
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getNtwkNodeInfo(final Context ctx,String type8583,String terminalCd){
		Map  resultMap =new HashMap();
		 try {
			 resultMap.put(Constants.IN_TERMINALCD, terminalCd);
			 resultMap.put(Constants.IN_POSTDATE, ctx.getData(Constants.PE_POST_DATE));
			 corebankAccess.getSqlMap().update("common.getNtwkNodeInfo",resultMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(resultMap==null){
				if(Constants.ISO8583.equals(type8583))
				    ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_SYSTEM_ERROR);
			    if(Constants.FIX8583.equals(type8583))
			    	ctx.setData(Constants.FIX8583_RESP,Constants.PE_SYSTEM_ERROR);
				return null;
			}
			else{
				if(!Constants.PE_OK.equals(resultMap.get(Constants.OUT_RESPONCD))){
					if(Constants.ISO8583.equals(type8583))
					    ctx.setData(Constants.ISO8583_RESCODE,(String)resultMap.get(Constants.OUT_RESPONCD));
				    if(Constants.FIX8583.equals(type8583))
				    	ctx.setData(Constants.FIX8583_RESP,(String)resultMap.get(Constants.OUT_RESPONCD));
					return null;
				}
				else
					return resultMap;
			}
	}
	/**
	 * 查询ATM本代他是终端号对应的钱箱，柜员，机构号。
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getNtwkNodeInfo(String terminalCd,String postDate){
		Map  resultMap =new HashMap();
		 try {
			 resultMap.put(Constants.IN_TERMINALCD, terminalCd);
			 resultMap.put(Constants.IN_POSTDATE, postDate);
			 corebankAccess.getSqlMap().update("common.getNtwkNodeInfo",resultMap);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			if(resultMap==null){
				return null;
			}
			else{
				return resultMap;
			}
	}
	/**
	 * 更改前置表中核心主机状态
	 *
	 * @param hostLogStat
	 */
	public void updateHostStat(String hostLogStat)throws SQLException{
		dzipdbAccess.getSqlMap().update("common.updateHostStat", hostLogStat);
	}
	/**
	 * 查数据库配置的参数值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String queryParam(String paramName){
		Map map = (Map) dzipdbAccess.getSqlMap().queryForObject("common.queryParam",paramName);
		String paramValue = (String) map.get(Constants.PEARAMVALUE); //获得参数的值
		return paramValue;
	}

	/**
	 * 获取机构号和科目号
	 * @return
	 * @throws PeException
	 */
	@SuppressWarnings("unchecked")
	public List query_OrgNbr_GlAcctTitleNbr(Map paramMap) throws PeException{
		 List list = new ArrayList();
		 try {
			  list = dzipdbAccess.getSqlMap().queryForList("common.query_orgNbr_glAcctTitlrNbr",paramMap);
		 } catch (Exception e) {
				// TODO Auto-generated catch block
			    logger.error(e.getMessage());
				throw new PeException("查询分录配置发生错误");
			}
		 if(list==null ||list.isEmpty() )
			 throw new PeException("没有相应的分录配置");
		return list;
	}
	/**
	 * 获取内部帐号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String query_directPostAcctNbr(Map paramMap){
		Map map = (Map) corebankAccess.getSqlMap().queryForObject("common.query_directPostAcctNbr",paramMap);
		String directPostAcctNbr =null;
		if(map !=null&& map.get("directPostAcctNbr")!=null)
			directPostAcctNbr=String.valueOf(map.get("directPostAcctNbr")); //获得参数的值
		return directPostAcctNbr;
	}
	/**
	 * 查询响应码对照表
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getResp(String coreRespCD)throws SQLException{
		Map param = new HashMap();
		param.put(Constants.RESPCD, coreRespCD);
		Map map = (Map) dzipdbAccess.getSqlMap().queryForObject("common.getResp", param);
		return map;
	}

	/**
	 * @author chenshaoqi
	 * @date 2010.08.26
	 * 构造借贷标记
	 * 根据处理码生成借贷标记
	 * @return
	 */
	public String generateCRDB(String porccessCode) {

		String crdb = Constants.CRDB_O;    // 定义借贷标记
		if (!Constants.PE_NULL.equals(porccessCode)) {   //是否为空判断
			int subStr = Integer.valueOf(porccessCode.substring(0, 1)); // 去处理码的第一位
			switch (subStr) {
			case Constants.PE_NUM_ZERO: // 00-19 DEBITS 借记 C
			case Constants.PE_NUM_ONE:
				crdb = Constants.CRDB_C;
				break;
			case Constants.PE_NUM_TWO: // 20-29 CREDIT 贷记 D
				crdb = Constants.CRDB_D;
				break;
			case Constants.PE_NUM_THREE: // 30-39 QUERY 查询   Q
				crdb = Constants.CRDB_Q;
				break;
			case Constants.PE_NUM_FOUR: // 40-49 TRANSFER 转账  X
				crdb = Constants.CRDB_X;
				break;
			default:
				crdb = Constants.CRDB_O;    //其他 O
				break;
			}
		}
		return crdb;

	}

	/**
	 * 外部交易码转换内部交易码
	 *
	 * @return instrancd
	 */
	@SuppressWarnings("unchecked")
	public Map converOutTrancd2InsTrancd(String outTrancd)throws SQLException {
		Map resultMap = (Map) dzipdbAccess.getSqlMap().queryForObject("common.queryInstrancd", outTrancd);
		return resultMap;
	}

	/**
	 * @author ：chenshq
	 * @param ：String AcctNum 卡号
	 * @return ： 返回查询结果
	 */
	@SuppressWarnings("unchecked")
	public String queryCardType(String AcctNum)throws SQLException {
		Map paramMap = new HashMap();
		String out_YNFlag,cardType = Constants.CUPS_CARD;
		int out_ErrorNbr;
		paramMap.put(Constants.IN_PAN, AcctNum);          //将卡号填到参数Map对象中
		corebankAccess.getSqlMap().queryForList("atm.ChkOurCardYN", paramMap);//调用核心存储过程验证是否本行卡。
	    out_YNFlag = String.valueOf(paramMap.get(Constants.OUT_YN_FLAG));  //是否本行卡标志。 11:本行锦城卡,12:本行其他卡；21：外行锦城卡 ，22：外行其他卡
		out_ErrorNbr = Integer.valueOf(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)));   //获得错误信息的条数
		if (out_ErrorNbr == 0) {
			switch (Integer.valueOf(out_YNFlag)) {
			case Constants.ELEVEN:                        //本行锦城卡标识
				cardType = Constants.OUR_JINCHENG_CARD;
				break;
			case Constants.TWELVE:                       //本行普通卡标识
				cardType = Constants.OUR_COMMON_CARD;
				break;
			case Constants.THIRTEEN:
				cardType = Constants.OUR_IC_CARD;	//本行IC卡
				break;
			default:
				if(this.channID.equals(Constants.PE_JINCHENG)){
					cardType = Constants.JINCHENG_CARD;
				}else{
					cardType = Constants.CUPS_CARD;
				}
				break;
			}
		}else{
			throw new SQLException(String.valueOf(paramMap.get(Constants.OUT_ERROR_MSG)));
		}
		return cardType;

	}

	/**
	 * @author chenshq
	 * @since 2010.08.26
	 * @用途：他代本交易的验证密码。
	 * @param pan: 账号
	 * @param pin: 密码
	 * @param procedureMap
	 */
	@SuppressWarnings("unchecked")
	public String  checkPin(Map paramMap){
		String respcd=null;
		corebankAccess.getSqlMap().update("common.CheckPin", paramMap);
		if(Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR))))   //验证密码是否正确？
			respcd = (String)paramMap.get(Constants.OUT_RESPONCE);                   //不发生错误时返回相应码
		return respcd;

	}
	/**
	 * 检查ATM交易金额是否合法
	 * @author chenshq
	 * @param tranAmt
	 * @return
	 */
//	public boolean validateTranAmt(double tranAmt,String outsideFlag) {
//		if(Constants.PE_ZERO.equals(outsideFlag)){
//			if(tranAmt%100>0.0)
//				return false;
//		}
//		return true;
//	}

	/**
	 * @author chenshq
	 * @since 2010.8.19
	 * @param paramsMap   传入参数
	 * @param postDate    传入参数
	 * @param peJournalNO 传入参数
	 * @param journalMap  传入参数
	 */
	@SuppressWarnings({"unchecked" })
	public Map getJournalMap4ISO(Map paramsMap, Map journalMap) {
		String tranDate = Util.getDateString(Constants.PE_YYYY).concat((String)paramsMap.get(Constants.ISO8583_LOCTRANSDATE));//交易日期
		journalMap.put(Constants.PE_POST_DATE, paramsMap.get(Constants.PE_POST_DATE));     		//填充帐务日期
		journalMap.put(Constants.PE_JOURNAL_NO, paramsMap.get(Constants.PE_JOURNAL_NO));   		//填充交易流水号
		journalMap.put(Constants.PE_TRANS_STAT, Constants.PE_INIT);                         	//填充交易状态
		journalMap.put(Constants.PE_ACC_NO, paramsMap.get(Constants.ISO8583_ACCTNO));       	//填充账号
		journalMap.put(Constants.PE_TRAN_DATE,tranDate);  								   		//填充交易日期
		journalMap.put(Constants.PE_TRAN_TIME, paramsMap.get(Constants.ISO8583_LOCTRANSTIME)); 	//填充交易时间
		journalMap.put(Constants.PE_CRDB, paramsMap.get(Constants.PE_CRDB)); 					//填充借贷标记
		journalMap.put(Constants.PE_CUST_FEE, paramsMap.get(Constants.ISO8583_TRANSFEE));   	//填充交易手续费
		journalMap.put(Constants.PE_AUTH_CD, paramsMap.get(Constants.ISO8583_AUTRESCOD)); 	  	//授权码
		journalMap.put(Constants.PE_CURR_CD, paramsMap.get(Constants.ISO8583_CURCODE));     	//填充交易币种码
		journalMap.put(Constants.PE_REQ_ORG_CD, paramsMap.get(Constants.ISO8583_ACQCODE));  	//填充受理机构号
		journalMap.put(Constants.PE_FOW_ORG_CD, paramsMap.get(Constants.ISO8583_FORWARDCODE)); 	//填充转发机构号
		journalMap.put(Constants.PE_REQ_CHANN, paramsMap.get(Constants.PE_REQ_CHANN));        	//填充渠道
		journalMap.put(Constants.PE_TRM_CD, paramsMap.get(Constants.ISO8583_CARDACCID));        //终端代码
		journalMap.put(Constants.PE_SYS_TRACE_NUM, paramsMap.get(Constants.ISO8583_SYSTRACENUM));//填充系统跟踪号
		journalMap.put(Constants.PE_OUT_TRANCD, paramsMap.get(Constants.TransactionId));   		//填充外部交易
		journalMap.put(Constants.PE_MSG_TYP, paramsMap.get(Constants.ISO8583_HEAD_TX_TYPE));   	//消息类型
		journalMap.put(Constants.PE_TRACK2, paramsMap.get(Constants.ISO8583_TRACK2_DATA)); 	    //填充二磁信息
		journalMap.put(Constants.PE_REF_CD, paramsMap.get(Constants.ISO8583_RTVREFNUM));        // 填充系统参考号#37域
		journalMap.put(Constants.PE_MER_TYPE, paramsMap.get(Constants.ISO8583_MERCHANTTYPE));   //填充商户类型
		journalMap.put(Constants.PE_TRANDATETIME, paramsMap.get(Constants.ISO8583_TRANSDTTIME));//交易日期时间
		journalMap.put(Constants.PE_SETTLMTDATE, paramsMap.get(Constants.ISO8583_SETDATE)); 	//清算日期
		journalMap.put(Constants.PE_PROCESSCODE, paramsMap.get(Constants.ISO8583_PRO_CODE)); 	//交易码
		journalMap.put(Constants.PE_ACCPID, paramsMap.get(Constants.ISO8583_CARDACCCODE)); 		//受卡方标识码
		journalMap.put(Constants.PE_ACCPADDR, paramsMap.get(Constants.ISO8583_CARDACCNAME)); 	//受卡方名称地址
		journalMap.put(Constants.PE_POSCONDCODE, paramsMap.get(Constants.ISO8583_SERCONDCODE)); //服务点条件码
		journalMap.put(Constants.PE_RCVCODE, paramsMap.get(Constants.ISO8583_RCVCODE)); 		//接收方机构码
		journalMap.put(Constants.PE_POSENTRYCODE, paramsMap.get(Constants.ISO8583_SERENTRYMODE));//服务点输入方式
		journalMap.put(Constants.PE_HOSTCHKCD, paramsMap.get(Constants.PE_HOSTCHKCD));   		// 主机状态标记
		journalMap.put(Constants.PE_PERSNBR, paramsMap.get(Constants.IN_ORIGPERSNBR));   		// 柜员号
		journalMap.put(Constants.PE_REACODE, paramsMap.get(Constants.ISO8583_REACODE));    // 自定义域
		journalMap.put(Constants.PE_RTXNCATCD, paramsMap.get(Constants.RTXNCATCD));    // 业务代理方向
		journalMap.put(Constants.PE_HOST_RESP_CD,Constants.PE_09);                     // 响应吗
		journalMap.put(Constants.PE_ADDDATAPRI,paramsMap.get(Constants.ISO8583_ADDDATAPRI));//48# 附加数据－私有

		if(paramsMap.get(Constants.ISO8583_ACCIDE_N1)!=null
				&& paramsMap.get(Constants.ISO8583_ACCIDE_N2)!=null){
			journalMap.put(Constants.PE_ACCTID1, paramsMap.get(Constants.ISO8583_ACCIDE_N1));   //转出卡号
			journalMap.put(Constants.PE_ACCTID2, paramsMap.get(Constants.ISO8583_ACCIDE_N2));   //转入卡号
		}
		if(paramsMap.get(Constants.ISO8583_ORGDATA)!=null
				&&String.valueOf(paramsMap.get(Constants.ISO8583_ORGDATA)).trim().length() == 42){
			String origDataElement = String.valueOf(paramsMap.get(Constants.ISO8583_ORGDATA));  //获得90#上笔交易的信息
			String origMsgTyp = origDataElement.substring(0, 4); 			  					//原始报文消息类型
			String origSysTraNumber = origDataElement.substring(4, 10); 			  			//原交易系统跟踪号
			String origDateTime = origDataElement.substring(10, 20); 			  				//原始交易的日期时间
			String origAcquInstituIdCd = origDataElement.substring(23, 31);            			//原交易受理机构标识码
			String origiForwInstituIdCd = origDataElement.substring(34, 42);           			//原交易发送机构标识码
		   journalMap.put(Constants.PE_ORIGMSGTYP, origMsgTyp);    								//原始报文消息类型
		   journalMap.put(Constants.PE_ORIGTRACENUM, origSysTraNumber);  						//原始交易的系统跟踪号
		   journalMap.put(Constants.PE_ORIGDATETIME, origDateTime);  							//原始交易的日期时间
		   journalMap.put(Constants.PE_ORIGREQORGCD, origAcquInstituIdCd);  					//原始受理机构号
		   journalMap.put(Constants.PE_ORIGFWDORGCD, origiForwInstituIdCd);  					//原始受理机构号
		}
		if(Constants.PE_NULL.equals(String.valueOf(paramsMap.get(Constants.ISO8583_TRANAMT)).trim())
				|| paramsMap.get(Constants.ISO8583_TRANAMT) ==null){
			paramsMap.put(Constants.ISO8583_TRANAMT,Constants.TRANAMT_DEFAULT); //防止交易金额取空，发生异常
		}
		BigDecimal tranAmt = Util.getAmount(String.valueOf(paramsMap.get(Constants.ISO8583_TRANAMT)));
		tranAmt = tranAmt.movePointLeft(2);
		journalMap.put(Constants.PE_TRAN_AMT,tranAmt);//填充交易金额
		if(Constants.PE_ONE.equals((String)paramsMap.get(Constants.PE_OUTSIDEFLAG))){//境外交易
			if(paramsMap.get(Constants.ISO8583_CHDAMT)!=null
					&&!Constants.PE_NULL.equals(((String)paramsMap.get(Constants.ISO8583_CHDAMT)).trim())){//F6不为null,交易金额为持卡人扣账金额
				tranAmt = Util.getAmount(String.valueOf(paramsMap.get(Constants.ISO8583_CHDAMT)));
				tranAmt = tranAmt.movePointLeft(2);
				journalMap.put(Constants.PE_TRAN_AMT,tranAmt);//填充交易金额
			}
			if( paramsMap.get(Constants.ISO8583_SETAMT)!=null&&
					!Constants.PE_NULL.equals(String.valueOf(paramsMap.get(Constants.ISO8583_SETAMT)).trim())){  //防止清算金额取空，发生异常
				tranAmt = Util.getAmount(String.valueOf(paramsMap.get(Constants.ISO8583_SETAMT)));
				tranAmt = tranAmt.movePointLeft(2);
				journalMap.put(Constants.PE_SET_AMT,tranAmt);//清算金额
			}
			journalMap.put(Constants.PE_COUNTRYCD,paramsMap.get(Constants.ISO8583_COUNTRYCD));//国家代码
		}
		if(paramsMap.get(Constants.ISO8583_TRANSFEE)!=null&&
				String.valueOf(paramsMap.get(Constants.ISO8583_TRANSFEE)).trim().length() == 9){
			BigDecimal customFee =  Util.getAmount(String.valueOf(paramsMap.get(Constants.ISO8583_TRANSFEE).toString().substring(1, 9)));
			journalMap.put(Constants.PE_CUST_FEE, customFee.movePointLeft(2));//填充交易手续费
		}else
			journalMap.put(Constants.PE_CUST_FEE, Constants.TRANSFEE_DEFAULT);
		return journalMap;
	}
	/**
	 * @author chenshq
	 * @since 2010.8.19
	 * @param paramsMap
	 * @param journalMap
	 * @param postDate
	 * @param peJournalNO
	 */
	@SuppressWarnings("unchecked" )
	public Map getJournalMap4FIX(Map paramsMap, Map journalMap) {
		String tranDate = Util.getDateString(Constants.PE_YYYY).concat((String) paramsMap.get(Constants.FIX8583_LOCD));//交易日期
		journalMap.put(Constants.PE_POST_DATE, paramsMap.get(Constants.PE_POST_DATE));    //填充帐务日期
		journalMap.put(Constants.PE_JOURNAL_NO, paramsMap.get(Constants.PE_JOURNAL_NO));  //填充交易流水号
		journalMap.put(Constants.PE_TRANS_STAT, Constants.PE_INIT);                       //填充交易状态
		journalMap.put(Constants.PE_ACC_NO, paramsMap.get(Constants.FIX8583_PAN));        //填充账号
		journalMap.put(Constants.PE_TRAN_DATE,tranDate);  								  //填充交易日期
		journalMap.put(Constants.PE_TRAN_TIME, paramsMap.get(Constants.FIX8583_LOCT));    //填充交易时间
		journalMap.put(Constants.PE_CRDB, paramsMap.get(Constants.PE_CRDB)); 			  //填充借贷标记
		journalMap.put(Constants.PE_AUTH_CD, paramsMap.get(Constants.FIX8583_AUTH)); 	  //授权码
		journalMap.put(Constants.PE_CURR_CD, paramsMap.get(Constants.FIX8583_TRCU));      //填充交易币种码
		journalMap.put(Constants.PE_REQ_ORG_CD, paramsMap.get(Constants.FIX8583_ACQU));   //填充受理机构号
		journalMap.put(Constants.PE_FOW_ORG_CD, paramsMap.get(Constants.FIX8583_FORW));   //填充转发机构号
		journalMap.put(Constants.PE_REQ_CHANN, paramsMap.get(Constants.PE_REQ_CHANN));    //填充渠道
		journalMap.put(Constants.PE_TRM_CD, paramsMap.get(Constants.FIX8583_CATI));  //终端代码
		journalMap.put(Constants.PE_SYS_TRACE_NUM, paramsMap.get(Constants.FIX8583_TRAC));//填充系统跟踪号
		journalMap.put(Constants.PE_OUT_TRANCD, paramsMap.get(Constants.TransactionId));  //填充外部交易
		journalMap.put(Constants.PE_MSG_TYP, paramsMap.get(Constants.FIX8583_MESG));      //消息类型
		journalMap.put(Constants.PE_TRACK2, paramsMap.get(Constants.FIX8583_TRACK2));     //填充二磁信息
		journalMap.put(Constants.PE_REF_CD, paramsMap.get(Constants.FIX8583_RETR));       //填充系统参考号#37域
		journalMap.put(Constants.PE_MER_TYPE, paramsMap.get(Constants.FIX8583_MERC));     // 填充商户类型
		journalMap.put(Constants.PE_TRANDATETIME, paramsMap.get(Constants.FIX8583_TRDT)); //交易日期时间
		journalMap.put(Constants.PE_SETTLMTDATE, paramsMap.get(Constants.FIX8583_SETT));  //清算日期
		journalMap.put(Constants.PE_PROCESSCODE, paramsMap.get(Constants.FIX8583_PROC));  //交易码
		journalMap.put(Constants.PE_ACCPID, paramsMap.get(Constants.FIX8583_CAID)); 	  //受卡方标识码
		journalMap.put(Constants.PE_ACCPADDR, paramsMap.get(Constants.FIX8583_CANL)); 	  //受卡方名称地址
		journalMap.put(Constants.PE_POSCONDCODE, paramsMap.get(Constants.FIX8583_POSC));  //服务点条件码
		journalMap.put(Constants.PE_RCVCODE, paramsMap.get(Constants.FIX8583_DEST)); 	  //接收方机构码
		journalMap.put(Constants.PE_POSENTRYCODE, paramsMap.get(Constants.FIX8583_POSE)); //服务点输入方式
		journalMap.put(Constants.PE_HOSTCHKCD, paramsMap.get(Constants.PE_HOSTCHKCD));    // 主机状态标记
		journalMap.put(Constants.PE_PERSNBR, paramsMap.get(Constants.IN_ORIGPERSNBR));    // 柜员号
		journalMap.put(Constants.PE_REACODE, paramsMap.get(Constants.FIX8583_RESE));    // 自定义域
		journalMap.put(Constants.PE_RTXNCATCD, paramsMap.get(Constants.RTXNCATCD));    // 业务代理方向
		journalMap.put(Constants.PE_HOST_RESP_CD,Constants.PE_09);                     // 响应吗
		journalMap.put(Constants.PE_ADDDATAPRI,paramsMap.get(Constants.FIX8583_ADDI));//48# 附加数据－私有
		if(paramsMap.get(Constants.FIX8583_ACCI_D1)!=null
				&& paramsMap.get(Constants.FIX8583_ACCI_D2)!=null){
			journalMap.put(Constants.PE_ACCTID1, paramsMap.get(Constants.FIX8583_ACCI_D1));//转出卡号
			journalMap.put(Constants.PE_ACCTID2, paramsMap.get(Constants.FIX8583_ACCI_D1));//转入卡号
		}
		if(paramsMap.get(Constants.FIX8583_ORIG)!=null
				&&String.valueOf(paramsMap.get(Constants.FIX8583_ORIG)).trim().length() == 42){
			String origDataElement = String.valueOf(paramsMap.get(Constants.FIX8583_ORIG)); // 获得90#上笔交易的信息
			String origMsgTyp = origDataElement.substring(0, 4); 			  				//原始报文消息类型
			String origSysTraNumber = origDataElement.substring(4, 10); 			  		// 原交易系统跟踪号
			String origDateTime = origDataElement.substring(10, 20); 			  			//原始交易的日期时间
			String origAcquInstituIdCd = origDataElement.substring(23, 31);            		// 原交易受理机构标识码
			String origiForwInstituIdCd = origDataElement.substring(34, 42);           		// 原交易发送机构标识码
		   journalMap.put(Constants.PE_ORIGMSGTYP, origMsgTyp);    							//原始报文消息类型
		   journalMap.put(Constants.PE_ORIGTRACENUM, origSysTraNumber);  					//原始交易的系统跟踪号
		   journalMap.put(Constants.PE_ORIGDATETIME, origDateTime);  						//原始交易的日期时间
		   journalMap.put(Constants.PE_ORIGREQORGCD, origAcquInstituIdCd);  				//原始受理机构号
		   journalMap.put(Constants.PE_ORIGFWDORGCD, origiForwInstituIdCd);  				//原始受理机构号
		}
		if(Constants.PE_NULL.equals(String.valueOf(paramsMap.get(Constants.FIX8583_AMT)).trim())
				|| paramsMap.get(Constants.FIX8583_AMT) ==null)                                 //防止交易金额取空，发生异常
			paramsMap.put(Constants.FIX8583_AMT, Constants.TRANAMT_DEFAULT);
		BigDecimal amt = Util.getAmount(String.valueOf(paramsMap.get(Constants.FIX8583_AMT)));
		amt = amt.movePointLeft(2);
		journalMap.put(Constants.PE_TRAN_AMT,amt);      //填充交易金额
		if(Constants.PE_ONE.equals((String)paramsMap.get(Constants.PE_OUTSIDEFLAG))){//境外交易
			if(paramsMap.get(Constants.FIX8583_CHDAMT)!=null
					&&!Constants.PE_NULL.equals(((String)paramsMap.get(Constants.FIX8583_CHDAMT)).trim())){//F6不为null,交易金额为持卡人扣账金额
				amt = Util.getAmount(String.valueOf(paramsMap.get(Constants.FIX8583_CHDAMT)));
				amt = amt.movePointLeft(2);
				journalMap.put(Constants.PE_TRAN_AMT,amt);      //填充交易金额
			}
			if( paramsMap.get(Constants.FIX8583_SETAMT)!=null&&
					!Constants.PE_NULL.equals(String.valueOf(paramsMap.get(Constants.FIX8583_SETAMT)).trim())){  //防止清算金额取空，发生异常
				amt =  Util.getAmount(String.valueOf(paramsMap.get(Constants.FIX8583_SETAMT)));
				amt = amt.movePointLeft(2);
				journalMap.put(Constants.PE_SET_AMT,amt);//清算金额
			}
			journalMap.put(Constants.PE_COUNTRYCD,paramsMap.get(Constants.FIX8583_COUNTRYCD));//国家代码
		}
		if(paramsMap.get(Constants.FIX8583_FEE)!=null&&
				String.valueOf(paramsMap.get(Constants.FIX8583_FEE)).trim().length() == 9){
			BigDecimal customFee =  Util.getAmount(String.valueOf(paramsMap.get(Constants.FIX8583_FEE).toString().substring(1, 9)));
			journalMap.put(Constants.PE_CUST_FEE, customFee.movePointLeft(2));
		}//填充交易手续费
		else
			journalMap.put(Constants.PE_CUST_FEE, Constants.TRANSFEE_DEFAULT);

		return journalMap;
	}


	/**
	 * 记平台流水
	 *
	 * @param paramMap
	 */
	@SuppressWarnings("unchecked")
	public void insertJournal(final Map paramMap)throws SQLException{
		dzipdbAccess.getTransactionTemplate().execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus arg0) {
						dzipdbAccess.getSqlMap().insert("common.insertJournal", paramMap);
						return null;
					}
				});
	}

	/**
	 * 修改平台流水
	 *
	 * @param paramMap
	 */
	@SuppressWarnings("unchecked")
	public void updJournal(final Map paramMap) throws SQLException {
		dzipdbAccess.getTransactionTemplate().execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus arg0) {
						dzipdbAccess.getSqlMap().update("common.updateTranStatus", paramMap);
						return null;
					}
				});
	}

	/**
	 * 修改批量流水的执行状态
	 *
	 * @param paramMap
	 */
	@SuppressWarnings("unchecked")
	public void updJournalBatch(final Map paramMap) throws SQLException {
		dzipdbAccess.getTransactionTemplate().execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus arg0) {
						dzipdbAccess.getSqlMap().update("common.updateBatchExecYN", paramMap);
						return null;
					}
				});
	}

	/**
	 * 修改IC记账流水
	 *
	 * @param paramMap
	 */
	@SuppressWarnings("unchecked")
	public void updICJournal(final Map paramMap) throws SQLException {
		dzipdbAccess.getTransactionTemplate().execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus arg0) {
						dzipdbAccess.getSqlMap().update("common.updICJournal", paramMap);
						return null;
					}
				});
	}
	/**
	 * @author chenshaoqi
	 * @since 2010.09.30
	 * @dispcription 查询pos交易的返回消息类型
	 * @return 返回消息类型
	 */
	@SuppressWarnings("unchecked")
	public String getResMsgTyp(String msgTyp){
		Map resultMap = new HashMap();
		String resMsgTyp;
		logger.info("msgTyp===============>" + msgTyp);
		resultMap = (Map) dzipdbAccess.getSqlMap().queryForObject("pos.queryResMsgTyp", msgTyp); // 调用相关的SQL
		if(  resultMap != null && resultMap.get(Constants.RESP_MSG_TYP) !=null){
			resMsgTyp = (String)resultMap.get(Constants.RESP_MSG_TYP);// 返回信息类型
		}else{
			resMsgTyp = Constants.PE_NULL;
		}
		logger.info("resMsgTyp==================>" + resMsgTyp);
		return resMsgTyp;
	}
	/**
	 * 获得开通无卡自助状态
	 * @return
	 */
	public String getOpenNoCardSelf(String acctNo){
		String tranStat = (String) dzipdbAccess.getSqlMap().queryForObject("common.getOpenNoCardSelf",acctNo);
		if(tranStat==null)
			return null;
		return tranStat;
	}

	/**
	 * @author xujin
	 * @since 2011.04.01
	 * @dispcription 查询ATM的当日取款次数
	 * @return 当日取款次数
	 */
	@SuppressWarnings("unchecked")
	public int getWithDrawCount(final Context ctx){
		Map paramMap = new HashMap();
		paramMap.put(Constants.PE_ACC_NO, ctx.getData(Constants.PE_ACC_NO));   //账号
		paramMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE)); //账务日期
		String count = (String) dzipdbAccess.getSqlMap().queryForObject("common.getWithDrawCount", paramMap); // 调用相关的SQL
		return Integer.parseInt(count);
	}

	/**
	 * @author xujin
	 * @since 2011.04.01
	 * @dispcription 获取每日ATM取款金额
	 * @return 获取每日ATM取款金额
	 */
	@SuppressWarnings("unchecked")
	public int getDailyWithDrawAmt(final Context ctx){
		Map paramMap = new HashMap();
		paramMap.put(Constants.PE_ACC_NO, ctx.getData(Constants.PE_ACC_NO));   //账号
		paramMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE)); //账务日期
		paramMap.put(Constants.TRANCD, ctx.getData(Constants.TransactionId)); //账务日期
		String sumAMT = (String) dzipdbAccess.getSqlMap().queryForObject("common.getDailyWithDrawAmt", paramMap); // 调用相关的SQL
		return Integer.parseInt(sumAMT);
	}
	/**
	 * @author xujin
	 * @since 2012.02.24
	 * @dispcription 记账欺诈次数
	 */
	@SuppressWarnings("unchecked")
	public void updFraudInfo(final Context ctx){
		Map procedureMap = new HashMap();
		procedureMap.put(Constants.IN_ACCTNO, ctx.getData(Constants.PE_ACC_NO));   //交易账号
		procedureMap.put(Constants.IN_POSTDATE, ctx.getData(Constants.PE_POST_DATE)); //账务日期
		if(ctx.getData(Constants.ISO8583_TRACK2_DATA)!=null){
			procedureMap.put(Constants.IN_TRACK2,ctx.getData(Constants.ISO8583_TRACK2_DATA));//二磁信息
			procedureMap.put(Constants.IN_TRACK3,ctx.getData(Constants.ISO8583_TRACK3_DATA));//三磁信息
		}else{
			 procedureMap.put(Constants.IN_TRACK2,ctx.getData(Constants.FIX8583_TRACK2));//二磁信息
			 procedureMap.put(Constants.IN_TRACK3,ctx.getData(Constants.FIX8583_TRACK3));//三磁信息
		}
		procedureMap.put(Constants.IN_RESPONCD, ctx.getData(Constants.PE_HOST_RESP_CD)); //交易响应码
		try {
			dzipdbAccess.getSqlMap().update("common.UpdFraudInfo",procedureMap); // 调用欺诈存储过程
			int errornbr=Integer.valueOf(String.valueOf(procedureMap.get(Constants.OUT_ERROR_NBR)));
			if(errornbr==0){
				ctx.setData(Constants.PE_HOST_RESP_CD, procedureMap.get(Constants.OUT_RESPONCD)); // 填充响应吗
				ctx.setData(Constants.ISO8583_RESCODE, procedureMap.get(Constants.OUT_RESPONCD)); // 填充响应吗
				ctx.setData(Constants.FIX8583_RESP, procedureMap.get(Constants.OUT_RESPONCD)); // 填充响应吗
			}
		} catch (DataAccessException e) {
				logger.error("调用欺诈存储过程(common.UpdFraudInfo)执行失败:"+e.getMessage());
		}
	}

	/**
	 * 获得指定帐号开通无卡自助关联的手机号码
	 * @param acctno 帐号
	 * @return
	 */
	public String getNoCardSelfPhonenbr(String acctno){
		try {
			String phonenbr = (String)dzipdbAccess.getSqlMap().queryForObject("common.getNoCardSelfPhonenbr", acctno);
			return phonenbr;
		} catch (DataAccessException e) {
			e.printStackTrace();
			logger.error("获得无卡自助的电话出错:" + e.getMessage());
		}
		return null;
	}

	/**
	 * @author xujin
	 * @since 2012.02.24
	 * @dispcription 记录开通无卡自助信息
	 */
	@SuppressWarnings("unchecked")
	public void OpenNoCardSelf(final Context ctx){
		Map procedureMap = new HashMap();
		procedureMap.put(Constants.PE_ACC_NO, ctx.getData(Constants.PE_ACC_NO));   //交易账号
		procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE)); //账务日期
		String field61 = ctx.getData(Constants.PE_FIELD_61).toString();
		String phonenbr;
		if(field61 == null || field61.equals("")){
			phonenbr = "";
		}else{
			int amIndex = field61.indexOf("AM");
			if(amIndex == -1){
				phonenbr = "";
			}else{//解析出61#中的电话号码
				phonenbr = field61.substring(amIndex+1+19+1, amIndex+1+19+11+1);
			}
		}
		String tranId=(String)ctx.getData(Constants.TransactionId);
		try {
			if(Constants.TRANCD_010089.equals(tranId)||Constants.TRANCD_042092.equals(tranId)){//建立委托
				procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_ONE); //交易状态
				logger.info("无卡自己业务,获得电话号码为:" + phonenbr);
				procedureMap.put(Constants.PE_PHONENBR, phonenbr);
				if(getOpenNoCardSelf((String)procedureMap.get(Constants.PE_ACC_NO))!=null){
					dzipdbAccess.getSqlMap().update("common.updOpenNoCardSelf",procedureMap); // 修改开通无卡自助状态
				}else{
					dzipdbAccess.getSqlMap().update("common.insertOpenNoCardSelf",procedureMap); // 记录开通无卡自助
				}
			}else if(Constants.TRANCD_042089.equals(tranId)||Constants.TRANCD_010092.equals(tranId)){
				procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO); //交易状态
				procedureMap.put(Constants.PE_PHONENBR, "");//解除委托和建立委托冲正将电话更新为空
			    dzipdbAccess.getSqlMap().update("common.updOpenNoCardSelf",procedureMap); // 修改开通无卡自助状态
			}
		} catch (DataAccessException e) {
				logger.error("记录开通无卡自助信息(common.insertOpenNoCardSelf)执行失败:"+e.getMessage());
		}
	}

	/**
	 * 开通小额临时支付 设定交易状态为2
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void openSmallAmountPayment(final Context ctx){
		Map procedureMap = new HashMap();
		procedureMap.put(Constants.PE_ACC_NO, ctx.getData(Constants.PE_ACC_NO));   //交易账号
		procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE)); //账务日期

		//小额支付不要求电话返回,所以不对phonenbr进行处理
		try{
			procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_TWO); //设定交易状态
			if(getOpenNoCardSelf((String)procedureMap.get(Constants.PE_ACC_NO)) != null){
				procedureMap.put(Constants.PE_PHONENBR, "");//更新电话为空 如果状态从1->0转换
				dzipdbAccess.getSqlMap().update("common.updOpenNoCardSelf", procedureMap); // 修改开通无卡自助状态
			}else{
				dzipdbAccess.getSqlMap().update("common.insertOpenNoCardSelf", procedureMap); // 记录开通小额支付
			}
		}catch(DataAccessException e){
			e.printStackTrace();
			logger.error("记录开通小额临时支付执行失败:" + e.getMessage());
		}
	}

	/**
	 * 根据帐号查询是否有电子钱包余额记录
	 * @param pan
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getElectronicCashBalInfo(String pan){
		Map map = new HashMap();
		try {
			map = (Map) dzipdbAccess.getSqlMap().queryForObject("common.getElectronicCashBalInfo", pan);
		} catch (Exception e) {
			logger.error("查询电子钱包余额记录出错, pan = " + pan + ", errorMessage = " + e.getMessage());
		}
		return map;
	}

	/**
	 * 更新或插入IC卡圈存、圈提、冲正、消费的金额历史记录
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void updateElectronicCashBal(final Context ctx){
		Map resultMap = new HashMap();
		Map updateMap = new HashMap();
		resultMap = getElectronicCashBalInfo(ctx.getString(Constants.ISO8583_ACCTNO));
		updateMap.put(Constants.STAT, Constants.PE_ZERO);//默认状态为0
		updateMap.put(Constants.PAN, ctx.getString(Constants.ISO8583_ACCTNO));//帐号
		if(null == resultMap){//不存在记录就插入一条新纪录
			//当记录不存在时,只可能是在做圈存
			updateMap.put(Constants.EWALLETBALAMT, ctx.getString(Constants.PE_TRAN_AMT));//电子钱包余额
			updateMap.put(Constants.TOTALLDAMT, ctx.getString(Constants.PE_TRAN_AMT));//圈存总额
			updateMap.put(Constants.TOTALUNLDAMT, Constants.PE_ZERO);//圈提总额
			updateMap.put(Constants.TOTALPOSAMT, Constants.PE_ZERO);//消费总额
			try {
				dzipdbAccess.getSqlMap().insert("common.insertElectronicCashBal", updateMap);
			} catch (Exception e) {
				logger.error("插入电子钱包余额记录出错, updateMap=" + updateMap + ", errorMessage = " + e.getMessage());
			}
		}else{//更新当前记录
			BigDecimal ewalletbalamt = new BigDecimal(String.valueOf(resultMap.get(Constants.EWALLETBALAMT)));//当前电子钱包余额
			BigDecimal totalldamt = new BigDecimal((String.valueOf(resultMap.get(Constants.TOTALLDAMT))));//当前电子钱包圈存总额
			BigDecimal totalunldamt = new BigDecimal((String.valueOf(resultMap.get(Constants.TOTALUNLDAMT))));//当前电子钱包圈提总额
			BigDecimal totalposamt = new BigDecimal((String.valueOf(resultMap.get(Constants.TOTALPOSAMT))));//当前电子钱包消费总额
			BigDecimal amt = new BigDecimal(ctx.getString(Constants.PE_TRAN_AMT));//当前交易的金额
			logger.info("电子钱包更新, transactionId = "+ ctx.getString(Constants.TransactionId) +", ewalletbalamt = " + ewalletbalamt + ", totalldamt = " + totalldamt + ", totalunldamt = " + totalunldamt +
						", totalpsoamt = " + totalposamt + ", amt = " + amt);
			if(Constants.TRANCD_020060.equals(ctx.getData(Constants.TransactionId))){//圈存
				totalldamt = totalldamt.add(amt);
				ewalletbalamt = ewalletbalamt.add(amt);
			}else if(Constants.TRANCD_042060.equals(ctx.getData(Constants.TransactionId))){//圈存冲正
				totalldamt = totalldamt.subtract(amt);
				ewalletbalamt = ewalletbalamt.subtract(amt);
			}else if (Constants.TRANCD_020061.equals(ctx.getData(Constants.TransactionId))){//圈提
				totalunldamt = totalunldamt.add(amt);
				ewalletbalamt = ewalletbalamt.subtract(amt);
			}else{//脱机消费
				totalposamt = totalposamt.add(amt);
				ewalletbalamt = ewalletbalamt.subtract(amt);
			}
			updateMap.put(Constants.EWALLETBALAMT, String.valueOf(ewalletbalamt));
			updateMap.put(Constants.TOTALLDAMT, String.valueOf(totalldamt));
			updateMap.put(Constants.TOTALUNLDAMT, String.valueOf(totalunldamt));
			updateMap.put(Constants.TOTALPOSAMT, String.valueOf(totalposamt));
			try {
				dzipdbAccess.getSqlMap().update("common.updateElectronicCashBal", updateMap);
			} catch (Exception e) {
				logger.error("更新电子钱包余额出错, updateMap = " + updateMap + ", errorMessage = " + e.getMessage());
			}
		}
	}

	/**
	 * 获得IC卡的有效期
	 * @param ctx
	 */
	public void getIcCardExpDate(Context ctx){
		try{
			String expdat = (String )corebankAccess.getSqlMap().queryForObject("common.getIcCardExpDate", ctx.getData(Constants.ISO8583_ACCTNO));
			if(null == expdat || "".equals(expdat)){
				ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);
			}else{
				ctx.setData(Constants.ISO8583_EXPDAT, expdat);
			}

		}catch(Exception e){
			logger.error("获得IC卡有效期失败!");
			e.printStackTrace();
		}
	}

	/**
	 * 向表T_ICCLOSING插入新纪录<BR>
	 * 存储过程PE.INSERTORUPDATEICCLOSIONGPAY
	 * @param paramMap
	 */
	@SuppressWarnings("unchecked")
	public void insertIcClosingPay(final Map paramMap){
		dzipdbAccess.getTransactionTemplate().execute(
				new TransactionCallback() {
					public Object doInTransaction(TransactionStatus arg0) {
						dzipdbAccess.getSqlMap().update(SqlMaps.COMMON_INSERTORUPDATE_CLOSING_PAY, paramMap);
						return null;
					}
				});
	}

	/**
	 * 银联数据应答后更新T_ICCLOSINGPAY表
	 * @param paramMap
	 */
	@SuppressWarnings("unchecked")
	public void updateIcClosingPay(final Map paramMap){
		try {
			dzipdbAccess.getSqlMap().update(SqlMaps.COMMON_UPDATEICCLOSINGPAY, paramMap);
		} catch (Exception e) {
			logger.error("更新T_ICCLOSINGPAY出错!" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 通过现金箱号获得现金箱所在的机构号
	 * @param cashboxnbr
	 * @return
	 */
	public String getOrgNbrByCashboxnbr(String cashboxnbr){
		String orgnbr = null;
		try {
			orgnbr = String.valueOf(corebankAccess.getSqlMap().queryForObject("common.getOrgNbrByCashboxnbr", cashboxnbr));
		} catch (Exception e) {
			logger.error("通过现金箱号获得现金箱所在的机构号出错!" + e.getMessage());
			e.printStackTrace();
		}
		return orgnbr;
	}

	/**
	 * @return the dzipdbAccess
	 */
	public JdbcAccessAwareProcessor getDzipdbAccess() {
		return dzipdbAccess;
	}
	/**
	 * @param dzipdbAccess the dzipdbAccess to set
	 */
	public void setDzipdbAccess(JdbcAccessAwareProcessor dzipdbAccess) {
		this.dzipdbAccess = dzipdbAccess;
	}
	/**
	 * @return the corebankAccess
	 */
	public JdbcAccessAwareProcessor getCorebankAccess() {
		return corebankAccess;
	}
	/**
	 * @param corebankAccess the corebankAccess to set
	 */
	public void setCorebankAccess(JdbcAccessAwareProcessor corebankAccess) {
		this.corebankAccess = corebankAccess;
	}
	public void setChannID(String channID){
		this.channID=channID;
	}
	public String getChannID(){
		return this.channID;
	}
}
