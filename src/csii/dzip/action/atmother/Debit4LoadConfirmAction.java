package csii.dzip.action.atmother;

import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
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
			logger.info("Debit4LoadConfirmAction(本代本圈提确认)=======================>Start");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01 现金及转账
			ctx.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);//交易设置为本代本
			if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){
				String resp = dzipProcessTemplate.getSysTranInfo(ctx, Constants.ISO8583);
				if(Constants.PE_OK.equals(resp)){
					Map map = new HashMap();
					map = utilProcessor.getJournalInfo(String.valueOf(ctx.getData(Constants.PE_JOURNAL_NO)));
					logger.info("获得本行圈提确认交易信息(map)==================>"+map);
					Map resultMap = new HashMap();
					Map sqlMap = new HashMap();
					sqlMap.put(Constants.PE_CHANNELID, ctx.getData(Constants.PE_REQ_CHANN));//请求渠道
					sqlMap.put(Constants.PE_ORIGSYSTRANUMBER, String.valueOf(map.get(Constants.PE_ORIGTRACENUM)));//系统跟踪号
					sqlMap.put(Constants.PE_ORIGTRANDATETIME, String.valueOf(map.get(Constants.PE_ORIGDATETIME)));//交易日期时间
					sqlMap.put(Constants.PE_ORIGACQUINSTITUIDCD, String.valueOf(map.get(Constants.PE_ORIGREQORGCD)));//原交易受理机构
					sqlMap.put(Constants.PE_ORIGIFORWINSTITUIDCD, String.valueOf(map.get(Constants.PE_ORIGFWDORGCD)));//原交易发送机构
					sqlMap.put(Constants.PE_PAN, ctx.getData(Constants.ISO8583_ACCTNO));//帐号
					int count = Integer.valueOf(utilProcessor.verifyOrigDate(sqlMap));//验证冲正交易是否已经发生
					if(count > 0){
						ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_22);//圈提确认已经发生
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
					}else{
						resultMap = utilProcessor.getJournalNO(sqlMap);
						logger.info("获得本行圈提确认上笔交易信息(resultMap)==================>"+resultMap);
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
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);//交易异常
				}
			}else{
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
