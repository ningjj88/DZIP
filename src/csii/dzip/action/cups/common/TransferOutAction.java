
package csii.dzip.action.cups.common;

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
 *@ClassName:&{type_name}
 *@Description: 他行转出记账：ATM，CDM
 *@author :xujin
 *@date：2010-09-07
 */
public class TransferOutAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	private ActionUtilProcessor  utilProcessor;
	private DzipProcessTemplate dzipProcessTemplate;

	@SuppressWarnings("unchecked")
	public void execute(final Context ctx) throws PeException {
		logger.info("CUPS TransferOutAction ==================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_01); 	//业务类型:01_现金及转账
		ctx.setData(Constants.IN_RTXNSOURCECD,ctx.getData(Constants.PRI_SOURCE)+ATM);
		validTranField(ctx);
		utilProcessor.calcTranFee(ctx);//计算手续费
		utilProcessor.valTranLimit(ctx);//验证交易限制
		Init.formatIdenAM(ctx);//组装域61中的AM用法
        Init.initForIdenNM(ctx);
		ctx.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_EL);     // 资金类型
		ctx.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
		ctx.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
//		ctx.setData(Constants.CHECK_PIN, Constants.PE_Y);                //需要验证密码
		if(Constants.PE_OK.equals(ctx.getData(Constants.PE_HOST_RESP_CD))){
			utilProcessor.deductTranAMT(ctx,Constants.ISO8583);					//执行数据库操作
			ctx.setData(Constants.ISO8583_RESCODE, ctx.getData(Constants.PE_HOST_RESP_CD)); //响应码
			ctx.setData(Constants.ISO8583_EXPDAT, ctx.getData(Constants.PE_CARD_EXPRI));   	//卡有效期
			if(Constants.PE_OK.equals(ctx.getData(Constants.ISO8583_RESCODE))){
				Map formatAmtMap = new HashMap();
				formatAmtMap.put(Constants.ACCTTYPCD,  ctx.getData(Constants.ACCTTYPCD));		//帐户类型
				formatAmtMap.put(Constants.BALAMT, ctx.getData(Constants.BALAMT));				//账面余额
				formatAmtMap.put(Constants.AVAILBALAMT,  ctx.getData(Constants.AVAILBALAMT));  	//可用余额
				String availBalAmt = Util.formatAmt(formatAmtMap); 				//返回余额格式化
				ctx.setData(Constants.ISO8583_BALAMT, availBalAmt);           					//余额
//				String acctName=Util.formatAcctName((String)ctx.getData(Constants.ACCTNAME));
				String acctName=(String)ctx.getData(Constants.ACCTNAME);
				ctx.setData(Constants.ISO8583_IDENNUMBE, Constants.CUPS_IDENNUMBENM30+acctName); //名字
			}
		}else{
			ctx.setData(Constants.ISO8583_RESCODE,ctx.getData(Constants.PE_HOST_RESP_CD)); //获得响应码，并返回
			ctx.setData(Constants.ISO8583_EXPDAT,Constants.PE_EXPIREYEAR);   					//卡有效期
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); 							//交易状态：失败
		}
		updateJoural.execute(ctx);															// 修改流水交易状态
	}

	/**
	 * 验证该交易域是否符合规范
	 * @param ctx
	 * @throws PeException
	 */
	public void validTranField(final Context ctx) throws PeException{
		String field48 = (String) ctx.getData(Constants.PE_FIELD_48);
		//如果48域为空，则返回A8
		if(null == field48 || "".equals(field48)){
//			ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_A8);
//			ctx.setData(Constants.FIX8583_RESP, Constants.PE_A8);
//			throw new PeException("转出交易必须上送48.");
			//2013年 10 月 27 日之前，银联不在转账交易报文 前，银联不在转账交易报文 48 域中填写转入卡归属地区信息， 48域中填写转入卡归属地区信息， 也不主动发送 48 域；
			//2013年 10 月 27 日以及 之后，银联 将在转账交 易报文 48 域中 填写 转入卡归属地区 信息， 转出方可 采取 读取 转账交 易报文 48 域的 IA 用法，获得转入卡归属地区信 息。
			ctx.setData(Constants.IS_CITYWIDE_TRAN, Constants.PE_NUM_ONE);
			ctx.setData(Constants.AREAREGIONCD, Constants.AREA_STAR+Constants.AREA_DZS);
		}else{
			int index = field48.indexOf("ASIA");
			if(index != -1){//如果存在ASIA用法
				//4     3	 4       4
				//ASIA 长度 机构编码 地区编码
				String areaRegioncd = field48.substring(index+4+3+4, index+4+3+4+4);
				if(Constants.AREA_CUPS.equals(areaRegioncd)){
					//如果地区码为0000直接按照同城交易处理
					ctx.setData(Constants.IS_CITYWIDE_TRAN, Constants.PE_NUM_ONE);
					ctx.setData(Constants.AREAREGIONCD, Constants.AREA_STAR+Constants.AREA_DZS);
				}else{
					String areaRegionDes = dzipProcessTemplate.queryAreaRegion(areaRegioncd);
					
					if(null == areaRegionDes || "".equals(areaRegionDes)){
						ctx.setData(Constants.ISO8583_RESCODE, Constants.PE_A8);
						ctx.setData(Constants.FIX8583_RESP, Constants.PE_A8);
						throw new PeException("转出交易,地区代码不存在");
					}
					//是否同城交易
					if(areaRegioncd.equals(Constants.AREA_DZS)
					  || areaRegioncd.equals(Constants.AREA_DX)
					  || areaRegioncd.equals(Constants.AREA_XHX)
					  || areaRegioncd.equals(Constants.AREA_KJX)
					  || areaRegioncd.equals(Constants.AREA_DZX)
					  || areaRegioncd.equals(Constants.AREA_WYS)
					  || areaRegioncd.endsWith(Constants.AREA_QX)){
						ctx.setData(Constants.IS_CITYWIDE_TRAN, Constants.PE_NUM_ONE);
						ctx.setData(Constants.AREAREGIONCD, Constants.AREA_STAR+areaRegioncd);
					}else {
						ctx.setData(Constants.IS_CITYWIDE_TRAN, Constants.PE_NUM_ZERO);
					}
				}
			}else{//如果48#没有ASIA用法 则按同城交易处理
				ctx.setData(Constants.IS_CITYWIDE_TRAN, Constants.PE_NUM_ONE);
				ctx.setData(Constants.AREAREGIONCD, Constants.AREA_STAR+Constants.AREA_DZS);
			}
		}
	}

	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}

	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
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
