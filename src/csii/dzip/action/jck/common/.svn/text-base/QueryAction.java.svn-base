
package csii.dzip.action.jck.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;

/**
 *@ClassName:&{type_name}
 *@Description:他行余额查询
 *@author :chenshq
 *@date：2010-08-16
 */
public class QueryAction extends DzipBaseAction {

	/* (non-Javadoc)
	 * @see csii.base.action.BaseAction#execute(com.csii.pe.core.Context)
	 */
	private UpdateJoural updateJoural;
	private JdbcAccessAwareProcessor corebankAccess;
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;


	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		// TODO Auto-generated method stub
		logger.info("JCK QueryAction ==================>Start");
		String msgTyp = String.valueOf(ctx.getData(Constants.FIX8583_MESG)); //消息类型
		String repMsgTyp =utilProcessor.getResMsgTyp(msgTyp);
		if(Constants.PE_NULL.equals(repMsgTyp))
			ctx.setData(Constants.FIX8583_MESG,msgTyp);						//返回消息类型
		else
			ctx.setData(Constants.FIX8583_MESG,repMsgTyp);					//返回消息类型
		corebankAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				HashMap procedureMap = new HashMap();
				ctx.setData(Constants.IN_ACCTNO,ctx.getData(Constants.FIX8583_PAN));
				ctx.setData(Constants.IN_TRACK2,ctx.getData(Constants.FIX8583_TRACK2));
				ctx.setData(Constants.IN_RTXNTYPCD,Constants.TRS_TYPE_DINQ);
				Init.initSelectAcctInfo(ctx, procedureMap,Constants.FIX8583);      //填充调用核心查询账户信息存储过程传入参数
				try {
					logger.info("==================================>Start To Call Core procedure ----- SelectAcctInfo");
					corebankAccess.getSqlMap().update("pos.SelectAcctInfo",	procedureMap);  // 调用核心存储过程。
					logger.info("==================================>End  ----- SelectAcctInfo");
					//输出存储过程响应的信息
					logger.info(Constants.OUT_RESPONCD + "===========>" + procedureMap.get(Constants.OUT_RESPONCD));//核心响应码
					logger.info(Constants.OUT_ERRORNBR + "===========>" + procedureMap.get(Constants.OUT_ERRORNBR));//异常信息码
					logger.info(Constants.OUT_ERRORMSG + "===========>" + procedureMap.get(Constants.OUT_ERRORMSG));//异常信息
					logger.info(Constants.OUT_ORAERRORMSG  + "===========>" + procedureMap.get(Constants.OUT_ORAERRORMSG));//数据库异常信息
					//输出完毕
				    ctx.setData(Constants.FIX8583_RESP, procedureMap.get(Constants.OUT_RESPONCD));    //响应码
					ctx.setData(Constants.PE_HOST_RESP_CD, procedureMap.get(Constants.OUT_RESPONCD)); //响应码
					ctx.setData(Constants.FIX8583_EXPI, procedureMap.get(Constants.OUT_EXPIREYEAR));    //响应码
				} catch (Exception e) {
					logger.error("procedure SelectAcctInfo=======>"+e.getMessage());
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);           //交易状态：失败
					ctx.setData(Constants.FIX8583_RESP, Constants.PE_ERROR);        	//响应码：失败
				}
				// 余额查询成功，修改交易流水的状态为成功
				if (Constants.PE_OK.equals(String.valueOf(procedureMap.get(Constants.OUT_RESPONCD)))
						&& procedureMap.get(Constants.OUT_ERRORMSG)==null) {
					Map formatAmtMap = new HashMap();
					formatAmtMap.put(Constants.ACCTTYPCD, procedureMap.get(Constants.OUT_ACCTTYPCD)); //帐户类型
					formatAmtMap.put(Constants.BALAMT, procedureMap.get(Constants.OUT_BALAMT)); //账面余额
					formatAmtMap.put(Constants.AVAILBALAMT, procedureMap.get(Constants.OUT_AVAILBALAMT)); //可用余额
					String availBalAmt = Util.formatAmt(formatAmtMap);  //返回余额格式化
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);    //交易状态：成功
					ctx.setData(Constants.FIX8583_RETU, availBalAmt);           //余额
					ctx.setData(Constants.FIX8583_EXPI, procedureMap.get(Constants.OUT_EXPIREYEAR));   //有效期
				}else{
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);    //交易状态：失败
					ctx.setData(Constants.FIX8583_EXPI,Constants.PE_EXPIREYEAR); //卡有效期	
				}
				return null;
			}
		});
		updateJoural.execute(ctx);        //更新交易流水表交易状态
	}
	
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public JdbcAccessAwareProcessor getCorebankAccess() {
		return corebankAccess;
	}

	public void setCorebankAccess(JdbcAccessAwareProcessor corebankAccess) {
		this.corebankAccess = corebankAccess;
	}
	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}

	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}

 }
