package csii.dzip.action.cups.atm;

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
 *@Description:他行柜面存款撤销
 *@author :xujin
 *@date：2012-03-23
 */
public class DepositATCDMCancelAction extends DzipBaseAction {
		private UpdateJoural updateJoural;
		private ActionUtilProcessor  utilProcessor;
		
		@SuppressWarnings("unchecked")
		public void execute(final Context ctx) throws PeException {
			logger.info("CUPS DepositATCDMCancelAction ==================>Start");
			ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
			Map procedureMap = new HashMap();
			if(ctx.getData(Constants.ISO8583_ORGDATA)!=null){
				int count=Integer.valueOf(utilProcessor.verifyOrigDate(ctx));//验证撤销是否已经发生
				if(count>0){
					ctx.setData(Constants.PE_HOST_RESP_CD, Constants.PE_22);          //响应码
					ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_22);          //响应码
					ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);  //卡有效期	
					ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
					updateJoural.execute(ctx);					                     // 修改流水交易状态;
				}else{
					Map resultMap =utilProcessor.getOriSysTraNum(ctx);
					logger.info("CUPS柜面存款撤销上笔交易信息(resultMap)==================>"+resultMap);
					String settDate=(String)resultMap.get(Constants.PE_SETTLMTDATE);
					//必须在同一清算日发生
					if(settDate.equals(ctx.getString(Constants.ISO8583_SETDATE))){
						ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：异常
						ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_12);    //响应码
						ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_12);    //响应码
						ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);
						updateJoural.execute(ctx);
					}else{
						ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+POS);
						Init.formatIdenAM(ctx);//组装域61中的AM用法
						if(Constants.PE_ONE.equals((String)ctx.getData(Constants.PE_OUTSIDEFLAG))){//境外交易
							if(ctx.getData(Constants.ISO8583_CHDAMT)==null)//F6为null,交易金额为存款交易金额
								ctx.setData(Constants.PE_TRAN_AMT,resultMap.get(Constants.PE_TRAN_AMT));//填充交易金额
						}
						ctx.setData(Constants.IN_MEDIAACCTSEQNO, Constants.rtxnseace_1002); //1002：POS消费撤销
						ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     //资金类型
						ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);	    	//交易状态,正常、冲正：'C'	抹帐:'EC'
						ctx.setData(Constants.IN_REVFLAG, Constants.PE_THREE);				//冲正标志 3：内部帐消费撤销
						ctx.setData(Constants.IN_RTXNNBR, resultMap.get(Constants.PE_HOST_SEQ_NO)); //原交易主机流水号
						ctx.setData(Constants.IN_ORIGTRACKNBR, resultMap.get(Constants.JOURNAL_NO)); //原平台流水号
						ctx.setData(Constants.IN_ORIGPOSTDATE,resultMap.get(Constants.POSTDATE)); //原始交易日期
						utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
						ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
						ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期	
						if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
							procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_ONE );    //交易状态: 已撤销
							procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO)); //原交易流水号 
//							procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
							ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));    //原交易流水号
						}
						if(Constants.PE_25.equals(ctx.getData(Constants.ISO8583_RESCODE))){
							procedureMap.put(Constants.PE_TRANS_STAT,Constants.PE_EIGHT );   //交易状态: 异常
							procedureMap.put(Constants.PE_JOURNAL_NO, resultMap.get(Constants.JOURNAL_NO));  //原交易流水号 
//							procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  //原 账务日期 
							ctx.setData(Constants.PE_RLTSEQNO,resultMap.get(Constants.JOURNAL_NO));  //原交易流水号
							ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_OK);          //响应码
							ctx.setData(Constants.ISO8583_EXPDAT,Constants.PE_EXPIREYEAR);   //卡有效期	
							ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);        //交易状态：异常
						}
						updateJoural.execute(ctx,procedureMap);								// 修改流水交易状态;
					}
				}
			}else{
				ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);  //交易状态：异常
				ctx.setData(Constants.ISO8583_RESCODE,Constants.PE_12);    //响应码
				ctx.setData(Constants.PE_HOST_RESP_CD,Constants.PE_12);    //响应码
				ctx.setData(Constants.ISO8583_EXPDAT, Constants.PE_EXPIREYEAR);
				updateJoural.execute(ctx);								  // 修改流水交易状态;
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