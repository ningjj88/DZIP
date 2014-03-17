package csii.dzip.action.cups.common;

import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;
/**
 *@ClassName:&{type_name}
 *@Description: 汇款
 *@author :xujin
 *@date：20012-10-22
 */
public class RemittanceAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS RemittanceAction(汇款) ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		if(ctx.getData(Constants.ISO8583_ORGDATA)!=null){
			Map resultMap=null;
			resultMap =utilProcessor.getOriSysTraNum(ctx);
		    logger.info("CUPS汇款上笔交易信息(resultMap)==================>"+resultMap);
			String settDate=(String)resultMap.get(Constants.PE_SETTLMTDATE);
			String tranStat = (String)resultMap.get(Constants.PE_TRANS_STAT);
			if(Constants.PE_ZERO.equals(tranStat)){//0：表示成功
				//汇款验证和汇款（联机）必须在同一清算日内发生
				if(settDate.equals(ctx.getString(Constants.ISO8583_SETDATE))){
					setErrorValue(ctx);
				}else{
					if(Constants.PE_ONE.equals((String)ctx.getData(Constants.PE_OUTSIDEFLAG))){//境外交易
						if(ctx.getData(Constants.ISO8583_CHDAMT)==null){//F6为null,交易金额为汇款交易金额	
							ctx.setData(Constants.PE_TRAN_AMT,resultMap.get(Constants.PE_TRAN_AMT));//填充交易金额
						}
					}
					utilProcessor.calcTranFee(ctx);//计算手续费
					ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS);
					ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     //资金类型
					ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);	    	//交易状态,正常、冲正：'C'	抹帐:'EC'
					ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志 3：内部帐消费撤销
					utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
					ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
					ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
				}
			}else{
				setErrorValue(ctx);
			}
		}else{
			setErrorValue(ctx);
		}
		updateJoural.execute(ctx);								// 修改流水交易状态;
	}
	
	public void setErrorValue(Context ctx){
		ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：异常
		ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_12);    //响应码
		ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_12);    //响应码
		ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}

	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}

}
