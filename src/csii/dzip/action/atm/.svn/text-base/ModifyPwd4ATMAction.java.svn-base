package csii.dzip.action.atm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.dzip.action.util.DecryptSecApi;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;

/**
 *@ClassName:&{type_name}
 *@Description:TODO
 *@author :xujin
 *@date：2010-7-29
 */

public class ModifyPwd4ATMAction extends DzipBaseAction {

	private JdbcAccessAwareProcessor corebankAccess;
	private UpdateJoural updateJoural;
	private DecryptSecApi decryptSecApi ;
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("ModifyPwd4ATMAction=======================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
		ctx.setData(Constants.IN_RTXNSOURCECD,ATM);
		corebankAccess.getTransactionTemplate().execute(new TransactionCallback() {
		public Object doInTransaction(TransactionStatus arg0) {
		Map procedureMap = new HashMap();
	    try {
			if (ctx.getData(Constants.ISO8583_ADDDATAPRI) !=null&&!Constants.PE_NULL.equals(ctx.getString(Constants.ISO8583_ADDDATAPRI))) {
				String pan = (String) ctx.getData(Constants.ISO8583_ACCTNO);  
				String pin=decryptSecApi.HSMAPIDecryptPIN(ctx.getString(Constants.ISO8583_ADDDATAPRI), pan); //解密ATM发来的密码
				if(pin==null){
					ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_55);
					ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_55);
					throw new PeException("解密出错");
				}		
				ctx.setData(Constants.IN_NEWPASSWORD, pin);
			}else{
				ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_30);
				ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_30);
				throw new PeException("没有输入新密码");
			}
			procedureMap=Init.initModifyPwd4ATM(ctx,procedureMap);
			//调用核心存储过程。
			logger.info( "===========>excute procedure atm.UpdCardPassword,Paramer:" + procedureMap); 
			corebankAccess.getSqlMap().update("atm.UpdCardPassword", procedureMap);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("ModifyPwd4ATMAction:"+e.getMessage());
		}
		ctx.setData(Constants.ISO8583_RESCODE, procedureMap.get(Constants.OUT_RESPONCODE));  //响应码
		ctx.setData(Constants.PE_HOST_RESP_CD, procedureMap.get(Constants.OUT_RESPONCODE));  //响应码
		Map map=new HashMap();
		if (Constants.PE_OK.equals(procedureMap.get(Constants.OUT_RESPONCODE))) { //修改交易流水状态为成功
			logger.info("===================>Modify passsword success!");
			map.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);                    //成功 状态 
			ctx.setData(Constants.ISO8583_ADDDATA, "Modify passsword success!");    
		}else{                                                                          //返回错误信息时
			logger.error("Modify passsword faild,paramers:"+procedureMap);
			map.put(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);                       //失败状态 
			ctx.setData(Constants.ISO8583_ADDDATA, "Modify passsword faild!");   
		}
		return null;
			}
		});
		updateJoural.execute(ctx);// 修改交易流水状态
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
	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}
	public DecryptSecApi getDecryptSecApi() {
		return decryptSecApi;
	}

	public void setDecryptSecApi(DecryptSecApi decryptSecApi) {
		this.decryptSecApi = decryptSecApi;
	}
}
