package csii.dzip.action.atmother;

import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;

/**
 * 指定账户圈存(本代本,本代他)
 * @author xiehai
 * @date 2013-11-4 上午10:25:34
 */
public class Credit4LoadAction extends DzipBaseAction {
	private ActionUtilProcessor utilProcessor;
	private UpdateJoural updateJoural;
	private DzipProcessTemplate dzipProcessTemplate;
	@Override
	@SuppressWarnings("unchecked")
	public void execute(Context ctx) throws PeException {
//		if(dzipProcessTemplate.queryParam(Constants.RCVG_CD_YLSJ).equals(ctx.getData(Constants.ISO8583_SOURID))){
		if("00010000".equals(ctx.getData(Constants.ISO8583_SOURID))){
			//根据原地址判断是银联数据过来的报文,则是本代本的电子现金交易
			logger.info("Credit4LoadAction(本代本指定账户圈存/柜面补登圈存) for our card=======================>Start");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_01);//业务类型：现金及转账
			ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);
			String responcd = Constants.PE_OK;
			if(!Init.isTransactionFromOnli(ctx)){//交易来自atm
				responcd = utilProcessor.getTranParamInfo(ctx, Constants.ISO8583); //获得交易参数信息
			}else{//交易来自柜面
				//设置柜面站点号
				ctx.setData(Constants.IN_ORIGNTWKNODENBR, ctx.getData(Constants.ISO8583_CARDACCID));
			}
			if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))
					&& Constants.PE_OK.equals(responcd)){	//若银联数据卡验证响应码为：成功
				//指定账户圈存金额限制
				utilProcessor.valTranLimit(ctx);
				//设置卡的有效期
				dzipProcessTemplate.getIcCardExpDate(ctx);
				ctx.setData(Constants.IN_RTXNSOURCECD, CLM); //交易来源
				ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);//资金类型：电子现金
				ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_C);//交易状态,正常、冲正：'C' 抹帐:'EC'
				ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);//冲正标志:0：正常 1：冲正 2：抹帐 ,5:境外
				Map<String, Object> map = utilProcessor.getJournalInfo(String.valueOf(ctx.getData(Constants.PE_JOURNAL_NO)));
				if(null != map.get(Constants.PE_ACCTID1)){//判断交易是否是来自柜面的补登圈存
					logger.info("=================>柜面补登圈存!");
					ctx.setData(Constants.TransactionId, Constants.TRANCD_02106001);
					ctx.setData(Constants.ISO8583_ACCIDE_N1, map.get(Constants.PE_ACCTID1));
				}
				utilProcessor.deductTranAMT(ctx, Constants.ISO8583);
				ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
				ctx.setData(Constants.ISO8583_SETDATE, ctx.getString(Constants.PE_POST_DATE).substring(4, 8));//获得清算日期
			}else{
				ctx.setData(Constants.ISO8583_ICCSYSRELDATA, Constants.PE_NULL);
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);               //交易状态：失败
				if(!Constants.PE_OK.equals(responcd)){
					ctx.setData(Constants.ISO8583_RESCODE, responcd);
				}else {
					ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
				}
			}
		}else {//否则,是本代他的电子现金交易
			logger.info("Credit4LoadAction(本代他指定账户圈存交易) for other card====================>Start!");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
			if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){	//若返回响应码为：成功
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);//交易状态：成功
			}else{
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);//交易状态：失败
			}
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
