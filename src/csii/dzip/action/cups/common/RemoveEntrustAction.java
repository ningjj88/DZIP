package csii.dzip.action.cups.common;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.UpdateJoural;
/**
 *@ClassName:&{type_name}
 *@Description: 解除委托
 *@author :xujin
 *@date：20112-03-017
 */
public class RemoveEntrustAction  extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS RemoveEntrustAction ==================>Start");
		validTranField(ctx);
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
		ctx.setData(Constants.IN_TRACK2,ctx.getData(Constants.ISO8583_TRACK2_DATA));
		ctx.setData(Constants.IN_RTXNTYPCD,Constants.TRS_TYPE_DINQ);
		utilProcessor.selectAcctInfo(ctx, Constants.ISO8583);
		updateJoural.execute(ctx);        //更新交易流水表交易状态
	}

	/**
	 * 验证解除委托的域是否符合交易规范
	 * @param ctx
	 * @throws PeException
	 */
	public void validTranField(final Context ctx) throws PeException{
		//解除委托时，现场（即非自助）可以不输密码，但自助方式时必须输密码
		//60.3.5# 第23位 0未知 1为现场 2为自助 3联机代理 4批量代理
		if(!ctx.getData(Constants.ISO8583_REACODE).toString().substring(22, 23).equals("1")
			&& null == ctx.getData(Constants.ISO8583_PINDATA)){
			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_05);
			ctx.setData(Constants.FIX8583_RESP, Constants.PE_05);
			throw new PeException("自助(非现场)解除委托时必须输入密码!");
		}
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
