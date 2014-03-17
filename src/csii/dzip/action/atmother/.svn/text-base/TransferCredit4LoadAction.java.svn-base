package csii.dzip.action.atmother;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.UpdateJoural;

/**
 * 非指定账户转账圈存
 * @author xiehai
 * @date 2013-11-4 下午03:51:58
 */
public class TransferCredit4LoadAction extends DzipBaseAction{
	private UpdateJoural updateJoural;
	@Override
	public void execute(Context ctx) throws PeException {
		logger.info("TransferCredot4LoadAction(他行非指定账户转账圈存交易)====================>Start!");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		ctx.setData(Constants.IN_RTXNSOURCECD, ctx.getData(Constants.PRI_SOURCE) + ATM);//交易来源
		if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){	//若返回响应码为：成功
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);//交易状态：成功
		}else{
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);//交易状态：失败
		}
		updateJoural.execute(ctx);// 修改流水交易状态
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}
	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
}
