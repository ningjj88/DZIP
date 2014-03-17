package csii.dzip.action.cups.atm;

import java.util.HashMap;
import java.util.Map;

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
 * 他带本非指定账户转出圈存
 * @author xiehai
 * @date 2013-11-5 上午09:10:18
 */
public class TransferOut4CreditForLoadAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;
	@Override
	public void execute(Context ctx) throws PeException {
		logger.info("TransferOut4CreditForLoadAction(他带本非指定账户转出圈存交易)====================>Start!");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		ctx.setData(Constants.IN_RTXNSOURCECD, ctx.getData(Constants.PRI_SOURCE) + CLM);//交易来源
		utilProcessor.calcTranFee(ctx);//计算手续费
		utilProcessor.valTranLimit(ctx);//验证交易限制
		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     // 资金类型
		ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
		ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
		if(Constants.PE_NULL.equals(ctx.getData(Constants.ISO8583_EXPDAT))){//如果14#为空 则初始化卡的有效期
			dzipProcessTemplate.getIcCardExpDate(ctx);//设置IC卡有效期
		}
		if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){//若返回响应码为：成功
			if(null != ctx.getData(Constants.PE_FIELD_61)){//如果有61#
				Init.formatIdenAM(ctx);//组装域61中的AM用法
		        Init.initForIdenNM(ctx);
			}
			utilProcessor.deductTranAMT(ctx, Constants.ISO8583);					//执行数据库操作
			ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
				Map<String, Object> formatAmtMap = new HashMap<String, Object>();
				formatAmtMap.put(Constants.ACCTTYPCD, ctx.getData(Constants.ACCTTYPCD));		//帐户类型
				formatAmtMap.put(Constants.BALAMT, ctx.getData(Constants.BALAMT));				//账面余额
				formatAmtMap.put(Constants.AVAILBALAMT, ctx.getData(Constants.AVAILBALAMT));  	//可用余额
				String availBalAmt = Util.formatAmt(formatAmtMap); 								//返回余额格式化
				ctx.setData(Constants.ISO8583_BALAMT, availBalAmt);           					//余额
//				String acctName=Util.formatAcctName((String)ctx.getData(Constants.ACCTNAME));
				String acctName=(String)ctx.getData(Constants.ACCTNAME);
				ctx.setData(Constants.ISO8583_IDENNUMBE, Constants.CUPS_IDENNUMBENM30+acctName); //名字
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO); 						//交易状态：成功
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
