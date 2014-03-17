/**
 * 
 */
package csii.dzip.action.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import csii.base.bean.AuditStatus;
import csii.base.bean.AuditType;
import csii.base.bean.ChannAlias;
import csii.base.bean.ChannStatus;
import csii.base.bean.CheckFlag;
import csii.base.bean.RtxnCat;
import csii.base.bean.TranNewStatus;
import csii.base.bean.TransStatus;
import csii.base.bean.TransType;
import csii.base.bean.TransTypeDef;

import csii.base.constant.Constants;

public class ConvertUtilProcessor {

	/**
	 * 将传过来list进行转换返回
	 * 
	 * @author shengzt
	 *
	 */
	@SuppressWarnings("unchecked")
	public static List convertList(List list) {
		TransStatus transStatus = new TransStatus();
		TransType transType = new TransType();
		ChannAlias channAlias = new ChannAlias();
		ChannStatus channStatus = new ChannStatus();
		
		Map map = new HashMap();
		List result = new ArrayList();		
		for(Iterator iterator = list.iterator();iterator.hasNext();){
			map = (Map) iterator.next();
			if(null != map.get(Constants.CHANNNAME))
				map.put(Constants.CHANNNAME, channAlias.get(map.get(Constants.CHANNNAME))==null?map.get(Constants.CHANNNAME):channAlias.get(map.get(Constants.CHANNNAME)).getDescription());
			
			if(null != map.get(Constants.TRANSTAT))
				map.put(Constants.TRANSTAT, transStatus.get(map.get(Constants.TRANSTAT))==null?map.get(Constants.TRANSTAT):transStatus.get(map.get(Constants.TRANSTAT)).getDescription());
					
			if(null != map.get(Constants.PE_CREDIT_DEBIT))
				map.put(Constants.PE_CREDIT_DEBIT, transType.get(map.get(Constants.PE_CREDIT_DEBIT))==null?map.get(Constants.PE_CREDIT_DEBIT):transType.get(map.get(Constants.PE_CREDIT_DEBIT)).getDescription());
			
			if(null != map.get(Constants.CHANNSTAT))
				map.put(Constants.CHANNSTAT, channStatus.get(map.get(Constants.CHANNSTAT))==null?map.get(Constants.CHANNSTAT):channStatus.get(map.get(Constants.CHANNSTAT)).getDescription());
			result.add(map);
		}
		return result;
	}
	
	/**
	 * 将传过来list进行转换返回
	 * 
	 * @author shengzt
	 *
	 */
	@SuppressWarnings("unchecked")
	public static List BatchList(List list) {
		TransType transType = new TransType();
		ChannAlias channAlias = new ChannAlias();
		RtxnCat     rtxnCat=new RtxnCat(); 
		TranNewStatus  tranNewStatus=new TranNewStatus(); 
		Map map = new HashMap();
		List result = new ArrayList();		
		for(Iterator iterator = list.iterator();iterator.hasNext();){
			map = (Map) iterator.next();
			if(null != map.get(Constants.CHANNNAME))
				map.put(Constants.CHANNNAME, channAlias.get(map.get(Constants.CHANNNAME))==null?map.get(Constants.CHANNNAME):channAlias.get(map.get(Constants.CHANNNAME)).getDescription());
			if(null != map.get(Constants.TRANSTAT))
				map.put(Constants.TRANSTAT, tranNewStatus.get(map.get(Constants.TRANSTAT))==null?map.get(Constants.TRANSTAT):tranNewStatus.get(map.get(Constants.TRANSTAT)).getDescription());		
			if(null != map.get(Constants.PE_CREDIT_DEBIT))
				map.put(Constants.PE_CREDIT_DEBIT, transType.get(map.get(Constants.PE_CREDIT_DEBIT))==null?map.get(Constants.PE_CREDIT_DEBIT):transType.get(map.get(Constants.PE_CREDIT_DEBIT)).getDescription());
			if(null != map.get(Constants.RTXNCATCD))
				map.put(Constants.RTXNCATCD, rtxnCat.get(map.get(Constants.RTXNCATCD))==null?map.get(Constants.RTXNCATCD):rtxnCat.get(map.get(Constants.RTXNCATCD)).getDescription());
			if(null != map.get(Constants.TRANSTATNEW))
				map.put(Constants.TRANSTATNEW, tranNewStatus.get(map.get(Constants.TRANSTATNEW))==null?map.get(Constants.TRANSTATNEW):tranNewStatus.get(map.get(Constants.TRANSTATNEW)).getDescription());
			result.add(map);
		}
		return result;
	}
	
	/**
	 * 将渠道交易关系传过来list的交易状态转换成中文描述返回
	 * 
	 * @author shengzt
	 *
	 */
	@SuppressWarnings("unchecked")
	public static List tranList(List list) {
		ChannAlias channAlias = new ChannAlias();
		ChannStatus channStatus = new ChannStatus();
		Map map = new HashMap();
		List result = new ArrayList();
		for(Iterator iterator = list.iterator();iterator.hasNext();){
			map = (Map) iterator.next();
			if(null != map.get(Constants.CHANNNAME))
				map.put(Constants.CHANNNAME, channAlias.get(map.get(Constants.CHANNNAME))==null?map.get(Constants.CHANNNAME):channAlias.get(map.get(Constants.CHANNNAME)).getDescription());
			if(null != map.get(Constants.TRANSTAT)){
				map.put(Constants.TRANSTAT, channStatus.get(map.get(Constants.TRANSTAT))==null?map.get(Constants.TRANSTAT):channStatus.get(map.get(Constants.TRANSTAT)).getDescription());
			result.add(map);
		  }	
		}
		return result;
	}
	/**
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List auditConvert(List list) {
		AuditType auditType = new AuditType();
		AuditStatus auditStatus = new AuditStatus();
		TransTypeDef transTypeDef = new TransTypeDef();
		CheckFlag checkFlag = new CheckFlag();
		
		Map map = new HashMap();
		List result = new ArrayList();
		for(Iterator iterator = list.iterator();iterator.hasNext();){
			map = (Map) iterator.next();
			if(null != map.get(Constants.AUDITTYPE))
				map.put(Constants.AUDITTYPE, auditType.get(map.get(Constants.AUDITTYPE))==null?map.get(Constants.AUDITTYPE):auditType.get(map.get(Constants.AUDITTYPE)).getDescription());	
			if(null != map.get(Constants.STATUS))
				map.put(Constants.STATUS, auditStatus.get(map.get(Constants.STATUS))==null?map.get(Constants.STATUS):auditStatus.get(map.get(Constants.STATUS)).getDescription());		
			if(null != map.get(Constants.TASKSTAT))
				map.put(Constants.TASKSTAT, auditStatus.get(map.get(Constants.TASKSTAT))==null?map.get(Constants.TASKSTAT):auditStatus.get(map.get(Constants.TASKSTAT)).getDescription());	
			if(null != map.get(Constants.TRANTYPEDEF))
				map.put(Constants.TRANTYPEDEF, transTypeDef.get(map.get(Constants.TRANTYPEDEF))==null?map.get(Constants.TRANTYPEDEF):transTypeDef.get(map.get(Constants.TRANTYPEDEF)).getDescription());			
			if(null != map.get(Constants.AUDITSTATUS))
				map.put(Constants.AUDITSTATUS, checkFlag.get(map.get(Constants.AUDITSTATUS))==null?map.get(Constants.AUDITSTATUS):checkFlag.get(map.get(Constants.AUDITSTATUS)).getDescription());			
			if(null != map.get(Constants.TRACENO)&& "0".equals(map.get(Constants.TRACENO)))
				map.put(Constants.TRACENO, "未知");			
			result.add(map);
		  }	
		
		return result;
	}
}
