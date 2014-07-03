package csii.dzip.action.atmother;

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

/**
 * 圈提确认(本行)
 * @author xiehai
 * @date 2013-12-9 下午02:24:00
 */
public class Debit4LoadConfirmAction extends DzipBaseAction {
	private ActionUtilProcessor utilProcessor;
	private UpdateJoural updateJoural;
	private DzipProcessTemplate dzipProcessTemplate;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		if(dzipProcessTemplate.queryParam(Constants.RCVG_CD_YLSJ).equals(ctx.getData(Constants.ISO8583_SOURID))){//报文来自银联数据
			logger.info("Debit4LoadConfirmAction(本代本圈提确认/转待补登确认)=======================>Start");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01 现金及转账
			ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);//交易设置为本代本
			if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){
				String responcd = Constants.PE_OK;
				if(!Init.isTransactionFromOnli(ctx)){//交易来自atm
					responcd = utilProcessor.getTranParamInfo(ctx, Constants.ISO8583); //获得交易参数信息
				}else{//交易来自柜面
					//设置柜面站点号
					ctx.setData(Constants.IN_ORIGNTWKNODENBR, ctx.getData(Constants.ISO8583_CARDACCID));
				}
				if(Constants.PE_OK.equals(responcd)){
					Map map = utilProcessor.getOrgData(ctx.getData(Constants.PE_JOURNAL_NO));
					map.put(Constants.PE_CHANNELID, ctx.getData(Constants.PE_REQ_CHANN));
					map.put(Constants.PE_PAN, ctx.getData(Constants.ISO8583_ACCTNO));
					Map resultMap = new HashMap();
					int count = Integer.valueOf(utilProcessor.verifyOrigDate(map));//验证冲正交易是否已经发生
					if(count > 0){
						ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_22);//圈提确认已经发生
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
					}else{
						resultMap = utilProcessor.getJournalNO(map);
						if(null != resultMap.get(Constants.PE_ACCTID1)
								&& !Constants.PE_NULL.equals(resultMap.get(Constants.PE_ACCTID1))){//判断交易是否是补登圈存冲正
							logger.info("=================>转待补登确认!");
							ctx.setData(Constants.TransactionId, Constants.TRANCD_02106101);
							ctx.setData(Constants.ISO8583_ACCIDE_N1, resultMap.get(Constants.PE_ACCTID1));
						}
						logger.info("获得本行圈提确认/转待补登确认上笔交易信息(resultMap)==================>"+resultMap);
						ctx.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
						ctx.setData(Constants.IN_RTXNSOURCECD, CLM); //交易来源
						ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   // 资金类型
						ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);		//交易状态,正常、冲正：'C'	抹帐:'EC'
						ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
						try {
							utilProcessor.deductTranAMT(ctx, Constants.ISO8583);
							ctx.setData(Constants.ISO8583_SETDATE, ctx.getString(Constants.PE_POST_DATE).substring(4, 8));//获得清算日期
						} catch (Exception e) {
							ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_OK);
						}
					}
				}else{
					ctx.setData(Constants.ISO8583_RESCODE, responcd);
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);//交易异常
				}
			}else{
				ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);//交易异常
			}
			updateJoural.execute(ctx);
		}
		//没有本代他的圈提确认
	}

	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}
	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
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
