
package csii.dzip.action.cups.PreAuthorization;

import java.util.HashMap;
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
 *@Description：预授权完成撤销
 *@author :xujin
 *@date：2010-08-16
 */
public class PreAuthCompCancelAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS PreAuthCompleteCancelAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_03); 	//业务类型:03_消费及预授权
		int count=Integer.valueOf(utilProcessor.verifyOrigDate(ctx));//验证撤销是否已经发生
		if(count>0){
			ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_22);          //响应码
			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_22);          //响应码
			ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);  //卡有效期	
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
			updateJoural.execute(ctx);					                     // 修改流水交易状态;
		}else{
			Map resultMap =utilProcessor.getOriSysTraNum(ctx);
			logger.info("CUPS预授权完成撤销上笔交易信息(resultMap)==================>"+resultMap);
			Map procedureMap = new HashMap();
			String tranStat = String.valueOf(resultMap.get(Constants.TRANSTAT));
			if(Constants.PE_ZERO.equals(tranStat)){
				if(Constants.PE_ONE.equals((String)ctx.getData(Constants.PE_OUTSIDEFLAG))){//境外交易
					if(ctx.getData(Constants.ISO8583_CHDAMT)==null)//F6为null,交易金额为预授权交易金额
						ctx.setData(Constants.PE_TRAN_AMT,resultMap.get(Constants.PE_TRAN_AMT));//填充交易金额
				}
				String authrid =String.valueOf(resultMap.get(Constants.PE_RLTSEQNO));
				Map sqlMap = utilProcessor.getOptNbr(authrid);	//获得帐户冻结编号和状态
				logger.info("CUPS预授权完成撤销上笔的上笔交易信息(sqlMap)==================>"+sqlMap);
				String in_optNbr = String.valueOf(sqlMap.get(Constants.PE_OPTNBR));
				ctx.setData(Constants.IN_HOLDFALG, Constants.FREEZE); 	//冻结解冻标识:0：冻结 1：解冻
				ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS); //交易来源
				ctx.setData(Constants.IN_OPTNBR,in_optNbr);  		//in_holdFlag标识判断是否必输项 0：为空	1：必输
				Init.initPreAuthTranAmtAndHoldAmt(String.valueOf(sqlMap.get(Constants.PE_TRAN_AMT)),ctx);
				ctx.setData(Constants.PE_EXECYN, Constants.PE_Y);                       //执行冻结帐户功能
				
				ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS);
				ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     // 资金类型
				ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO)); // 原交易主机流水号
				ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO)); // 原平台流水号
				ctx.setData(Constants.IN_ORIGPOSTDATE,resultMap.get(Constants.POSTDATE)); //原始交易日期
				ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
				ctx.setData(Constants.IN_REVFLAG, Constants.PE_THREE);				//冲正标志 3：消费撤销
				ctx.setData(Constants.IN_MEDIAACCTSEQNO, Constants.rtxnseace_1006);  //1006：预授权完成撤销
				utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
				ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
				ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期
				if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){	
					sqlMap.clear();
					sqlMap.put(Constants.PE_TRANS_STAT, Constants.PE_ZERO);  	//交易状态
					sqlMap.put(Constants.PE_JOURNAL_NO,authrid);  	//交易状态
					sqlMap.put(Constants.PE_OPTNBR,ctx.getData(Constants.PE_OPTNBR));  	//交易状态
					updateJoural.execute(sqlMap);  //更新预授权交易中冻结编号
					procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_ONE );    //交易状态: 已撤销
					procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO)); //原交易流水号 
//						procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
					ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO)); //原交易流水号
					ctx.setData(Constants.PE_OPTNBR,Constants.PE_ZERO);				//抹去冻结编号
				}
				if(Constants.PE_25.equals(ctx.getData(Constants.ISO8583_RESCODE))){
					procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_EIGHT );   //交易状态: 异常
					procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO)); //原交易流水号 
//					procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
					ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO)); //原交易流水号
					ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_OK);          //响应码
					ctx.setData(Constants.ISO8583_EXPDAT,Constants.PE_EXPIREYEAR);   //卡有效期	
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
				}
				updateJoural.execute(ctx,procedureMap);					// 修改流水交易状态;
			}else{
				switch(Integer.valueOf(tranStat)){
				case 1:						//1:表示预授权完成撤销已经发生
					logger.info("==================>CUPS预授权完成撤销上笔交易已经撤销");
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：失败
					ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_22);    //响应码
					ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_22);    //响应码
					ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);
					break;								
				default :
					logger.info("==================>CUPS预授权完成撤销上笔交易失败");
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：失败
					ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_12);    //响应码
					ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_12);   //响应码
					ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);
					break;
				}
				updateJoural.execute(ctx);						// 修改流水交易状态;
			}
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
