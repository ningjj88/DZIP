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
 * 非指定账户转入圈存(本代本)
 * @author xiehai
 * @date 2013-12-19 下午03:09:06
 */
public class TransferIn4Credit4LoadAction extends DzipBaseAction {
	private ActionUtilProcessor utilProcessor;
	private UpdateJoural updateJoural;
	private DzipProcessTemplate dzipProcessTemplate;
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Context ctx) throws PeException {
		if(dzipProcessTemplate.queryParam(Constants.RCVG_CD_YLSJ).equals(ctx.getData(Constants.ISO8583_SOURID))){//判断报文是否来自银联数据
			logger.info("TransferIn4Credit4LoadAction(本代本转入圈存)=======================>Start");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01 现金及转账
			ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);//交易设置为本代本
			Map paramMap = new HashMap();
			Map sqlMap = utilProcessor.getJournalInfo(String.valueOf(ctx.getData(Constants.PE_JOURNAL_NO)));
			paramMap.put(Constants.PE_JOURNAL_NO, sqlMap.get(Constants.PE_RLTSEQNO));
			if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){
				ctx.setData(Constants.ISO8583_ACCTNO, ctx.getData(Constants.ISO8583_ACCIDE_N1));//设置记账账户为转出卡
				String responcd = Constants.PE_OK;
				if(!Init.isTransactionFromOnli(ctx)){//交易来自atm
					responcd = utilProcessor.getTranParamInfo(ctx, Constants.ISO8583); //获得交易参数信息
				}else{//交易来自柜面
					//设置柜面站点号
					ctx.setData(Constants.IN_ORIGNTWKNODENBR, ctx.getData(Constants.ISO8583_CARDACCID));
				}
				if(Constants.PE_OK.equals(responcd)){
					//设置卡的有效期
					dzipProcessTemplate.getIcCardExpDate(ctx);
					//为了方便对账 设置渠道为CLM
					ctx.setData(Constants.IN_RTXNSOURCECD, CLM); //交易来源
					ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);   // 资金类型
					ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);							//交易状态,正常、冲正：'C'	抹帐:'EC'
					ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
					ctx.setData(Constants.TransactionId, Constants.TRANCD_021062);
					utilProcessor.deductTranAMT(ctx, Constants.ISO8583);					//执行数据库操作
					ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
					ctx.setData(Constants.ISO8583_SETDATE, ctx.getString(Constants.PE_POST_DATE).substring(4, 8));//获得清算日期
					if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){
						paramMap.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);
					}else{
						paramMap.put(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
					}
				}else {
					ctx.setData(Constants.ISO8583_RESCODE, responcd);
				}
			}else{
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
				paramMap.put(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);
				ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			}
			paramMap.put(Constants.IN_BUSITYP, Constants.PE_01); //在更新原交易时,更新原交易的业务类型
			updateJoural.execute(ctx, paramMap);
			ctx.setData(Constants.ISO8583_ACCTNO, ctx.getData(Constants.ISO8583_ACCIDE_N2));//转账圈存的主帐号是转出交易账户
			ctx.setData(Constants.ISO8583_PRO_CODE, Constants.PROCODE_620000);//报文返回ATMP更改为转账圈存交易码
		}
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
