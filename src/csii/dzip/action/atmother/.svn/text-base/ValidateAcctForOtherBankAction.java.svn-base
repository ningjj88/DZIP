package csii.dzip.action.atmother;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.UpdateJoural;
/**
 *@ClassName:&{type_name}
 *@Description:本带他账户验证交易
 *@author :徐锦
 *@date：2012-10-30
 */
public class ValidateAcctForOtherBankAction extends DzipBaseAction {
	private UpdateJoural updateJoural;
	public void execute(Context ctx) throws PeException {
		logger.info("ValidateAcctForOtherBankAction===================>Start");
		ctx.setData(Constants.IN_BUSITYP, Constants.PE_08); 	//业务类型:08_账户服务
		/* 如果响应码是 00，修改交易流水的状态为成功 ；0：表示成功； */
		if (ctx.getData(Constants.PE_HOST_RESP_CD).equals(Constants.PE_OK)){ 
			String idendInfo=ctx.getString(Constants.ISO8583_IDENNUMBE);
			int NM=idendInfo.indexOf(Constants.PE_NM);
			if(NM!=-1){
				 String strNM=idendInfo.substring(NM+2);
				 String str=idendInfo.substring(0, NM+2);
				 for(int i=0;i<strNM.length();i++){
					 if(strNM.substring(i,i+1).equals(Constants.PE_SPACE)){
						 String strName1=strNM.substring(0,i).trim();
						 ctx.setData(Constants.ISO8583_IDENNUMBE,str+strName1);
						 break;
					 }
				 }
			}
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_ZERO);       //成功状态 
		}else
			ctx.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT);      // 异常状态
		updateJoural.execute(ctx);
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}

}
