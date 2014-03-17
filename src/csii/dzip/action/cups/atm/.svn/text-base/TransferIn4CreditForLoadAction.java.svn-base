package csii.dzip.action.cups.atm;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;

/**
 * 他带本非指定账户转入圈存
 * @author xiehai
 * @date 2013-11-4 下午04:02:35
 */
public class TransferIn4CreditForLoadAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	@Override
	public void execute(Context ctx) throws PeException {
		logger.info("TransferIn4CreditForLoadAction(他带本非指定账户转入圈存交易)====================>Start!");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE) + CLM);//交易来源
		utilProcessor.valTranLimit(ctx);//圈存不大于1000
		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     // 资金类型
		ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
		ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
		dzipProcessTemplate.getIcCardExpDate(ctx);//设置IC卡有效期
		if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){//若返回响应码为：成功
			utilProcessor.deductTranAMT(ctx, Constants.ISO8583);				//执行数据库操作
			ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO); 		//交易状态：成功
			}
		}else{
			ctx.setData(Constants.ISO8583_RESCODE,ctx.getData(Constants.PE_HOST_RESP_CD)); //获得响应码，并返回
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); //交易状态：失败
		}
		updateJoural.execute(ctx);// 修改流水交易状态
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
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
}
