package csii.dzip.action.cups.ec;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;
import csii.dzip.core.InitData4Dzip;

/**
 * 圈存机电子现金指定账户圈存冲正
 * @author xiehai
 * @version 1.0.0
 * @date 2013-8-26 下午05:31:27
 */
public class Credit4LoadReversalAction extends DzipBaseAction {
	private ActionUtilProcessor actionUtilProcessor;
	private UpdateJoural updateJoural;
	private DzipProcessTemplate dzipProcessTemplate;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		logger.info("Credit4LoadReversalAction=======================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01);//关联业务类型：01 现金及转账
		//设置卡的有效期
		//Init.setIcCardExpDate(ctx);
		dzipProcessTemplate.getIcCardExpDate(ctx);
		int count = Integer.valueOf(actionUtilProcessor.verifyOrigDate(ctx));//验证冲正交易是否已经发生
		if(count > 0){//冲正已经发生
			logger.debug("交易指定账户圈存冲正已经发生!");
			ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_22); //响应码
			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK); //响应码
//			ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);  //卡有效期
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			updateJoural.execute(ctx);// 修改流水交易状态
		}else{
			Map resultMap = actionUtilProcessor.getOriSysTraNum(ctx);
			//根据受理机构码判断是否是本行圈存机
			if(InitData4Dzip.getAcqOrgCd().equals(ctx.getData(Constants.ISO8583_ACQCODE))
			   || InitData4Dzip.getAcqOrgCd4Ic().equals(ctx.getData(Constants.ISO8583_ACQCODE))){//如果是本行圈存机
				ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);//设置交易来源为本行圈存机
				ctx.setData(Constants.IN_RTXNSOURCECD, CLM);//交易来源
			}else{//他行圈存机发起的交易
				ctx.setData(Constants.IN_RTXNSOURCECD, ctx.getData(Constants.PRI_SOURCE) + CLM);//交易来源
			}
//			ctx.setData(Constants.MEDIUM_TYPE, Constants.CARD_ACCT_FLAG_2); 	// 介质号：2 卡号
			ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   //资金类型
			ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO)); 	// 原交易主机流水号
			ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO)); 	// 原平台流水号
			ctx.setData(Constants.IN_ORIGPOSTDATE, resultMap.get(Constants.POSTDATE)); //原始交易日期
			ctx.setData(Constants.IN_RTXNSTATCD, Constants.RTXNSTAT_EC);	//交易状态,正常、冲正：'C'	抹帐:'EC'
			ctx.setData(Constants.IN_REVFLAG, Constants.PE_TWO);				//冲正标志:0：正常   1：冲正 	2：抹帐
			actionUtilProcessor.deductTranAMT(ctx, Constants.ISO8583);					//执行数据库操作
			ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
//			ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期
			Map procedureMap = new HashMap();
			//冲正成功
			if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
				procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_NINE );   //交易状态: 已冲正
				procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
				ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO)); //原交易流水号
//				将更新电子钱包余额移至核心处理
//				dzipProcessTemplate.updateElectronicCashBal(ctx);//更新电子钱包余额
			}

			//无法找到原始交易
			if(Constants.PE_25.equals(ctx.getData(Constants.ISO8583_RESCODE))){
				procedureMap.put(Constants.PE_TRANS_STAT, Constants.PE_EIGHT );   //交易状态: 已冲正
				procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
				ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));           	//原交易流水号
				ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK);          //响应码
				ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);   //卡有效期
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			}
			updateJoural.execute(ctx, procedureMap);
		}
	}

	public ActionUtilProcessor getActionUtilProcessor() {
		return actionUtilProcessor;
	}
	public void setActionUtilProcessor(ActionUtilProcessor actionUtilProcessor) {
		this.actionUtilProcessor = actionUtilProcessor;
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}
	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
}
