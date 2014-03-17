
package csii.dzip.core;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;
import org.xvolks.jnative.exceptions.NativeException;

import com.csii.pe.channel.stream.tcp.DefaultHandler;
import com.csii.pe.core.Context;
import com.csii.pe.core.LoggingInterceptor;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.Channel;
import com.csii.pe.service.id.IdFactory;
import com.csii.pe.transform.TransformException;

import csii.base.action.util.FileUtils;
import csii.base.constant.Constants;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.UpdateJoural;

/**
 * 临时功能类，发布是请删除
 * @author chenshq
 * @version 1.0.0
 * @since 2010-8-16
 */
public class JCKHandler_temp extends DefaultHandler{
	
	private boolean debug;
	private FixedLength8583Processor fixedLength8583Processor;
	private IdFactory peJournalNOIdFactory;
	private IdFactory peCupsNOIdFactory; 
	private DzipProcessTemplate dzipProcessTemplate;
	private UpdateJoural updateJoural;

	@SuppressWarnings("unchecked")
	protected Object parseStream(Channel paramChannel, byte[] paramArrayOfByte) throws TransformException {
		Map parsedMap = null;
		try {
			String formatedStr = new String(paramArrayOfByte,Constants.CHARSET_ISO_8859_1);
			log.info("recv Length =====>" + formatedStr.length());
			log.info("recv =====>" +new String(paramArrayOfByte, Constants.CHARSET_GBK));
			Element root = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
			parsedMap = fixedLength8583Processor.parse(paramArrayOfByte, root);
			String msgType = parsedMap.get(Constants.FIX8583_MESG).toString();
			String proc = parsedMap.get(Constants.FIX8583_PROC).toString();
			StringBuffer transactionId = new StringBuffer(msgType);
			transactionId.append(proc.substring(0, 2));
			if(Constants.PE_06.equals( parsedMap.get(Constants.FIX8583_POSC))|| 
					Constants.PE_60.equals( parsedMap.get(Constants.FIX8583_POSC)))//06:预授权; 60：追加预授权;
				transactionId.append(parsedMap.get(Constants.FIX8583_POSC));
			parsedMap.put(Constants.TransactionId, transactionId.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return parsedMap;
	}

	@SuppressWarnings("unchecked")
	protected byte[] handleInternal(Channel paramChannel, Object paramObject) {
		log.info(" handleInternal is Excuting..................................");
		Element element = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
		Map paramsMap = (Map) paramObject;
		log.info("recv message paramMap =====>" +paramsMap);
		Map journalMap = new HashMap();
		String peJournalNO = null;       //平台流水号
		String formatedMessage = null;   //返回消息
		String responcd=null;
		String pan = "";
		String pin = "";
		byte[] sendBuffer = null;
		byte[] respBuffer = null;		//返回报文
		String transactionId = getIdentityResolver().getIdentity(paramsMap);
		String sessionID = null; // 获得SessionID
		Context context = createContext(transactionId, paramsMap, sessionID, getSessionManager(), paramChannel);
		Init.initFieldsValue(context);//初始化域中的值
		Map checkMap = new HashMap();
		checkMap.put(Constants.PE_CHANN_ID, Constants.PE_JINCHENG);
		checkMap.put(Constants.TRANCD, paramsMap.get(Constants.TransactionId));
		checkMap.put(Constants.IN_CHANNELTYPCD, context.getString(Constants.IN_CHANNELTYPCD));
		if (dzipProcessTemplate.querychannTranSta(checkMap)) {      //判断渠道可用,交易是否可进行
			log.debug("The Channel JINCHEN　is  validate..................................");
			try {
				if (!Constants.PE_NULL.equals(String.valueOf(paramsMap.get(Constants.FIX8583_PIN)))) {
					pan = context.getData(Constants.FIX8583_PAN).toString();
					pin = context.getData(Constants.FIX8583_PIN).toString();
					String bankId = context.getData(Constants.FIX8583_BANKID).toString();
//					context.setData(Constants.FIX8583_PIN,JNative4CapDes.getCap_dis_pin(bankId, pin, pan));//锦城卡解密算法s
					/*模拟双机并行是，核心库里的数据密码都是"111111"*/ 
					context.setData(Constants.FIX8583_PIN,"111111");       //模拟结束后，该处代码删除
				}
				
				context.setData(Constants.RTXNCATCD,Constants.RTXNCATCD_03);		// 业务性质 ；他代本
				responcd =dzipProcessTemplate.getSysTranInfo(context,Constants.FIX8583); //获得系统交易参数信息
				if(Constants.PE_SYSTEM_ERROR.equals(responcd))
					throw new PeException("系统错误");	
				context.setData(Constants.IN_ORGNBR, InitData4Dzip.getJCOrgNbr()); //机构号-- 数据库配置参数
				log.info("机构号==================>" +  InitData4Dzip.getJCOrgNbr());
				context	.setData(Constants.IN_CASHBOXNBR, InitData4Dzip.getJCCashBoxNbr()); // 钱箱号,数据库配置参数
				log.info("钱箱号==================>" +  InitData4Dzip.getJCCashBoxNbr());
				context.setData(Constants.IN_ORIGPERSNBR, InitData4Dzip.getJCPersNbr()); // 柜员号，数据库配置参数
				log.info("柜员号==================>" + InitData4Dzip.getJCPersNbr());
				context.setData(Constants.IN_ORIGNTWKNODENBR, InitData4Dzip.getJCNtwkNodeNbr()); // 交易站点号，数据库配置参数
				log.info("交易站点号==================>" + InitData4Dzip.getYLNtwkNodeNbr());
				peJournalNO = peJournalNOIdFactory.generate().toString();           //获取交易流水
				context.setData(Constants.PE_JOURNAL_NO, peJournalNO);		        // 填充交易流水号
				context.setData(Constants.PE_CRDB, checkMap.get(Constants.PE_CRDB)); // 填充借贷标记
				context.setData(Constants.PE_REQ_CHANN, Constants.PE_JINCHENG);     //填充交易渠道
				context.setData(Constants.IN_AVAILMETHCD,Constants.AVAILMETHCD);    //填充可用方式
				context.setData(Constants.PRI_SOURCE, Constants.PRI_SOURCE_JCK_VALUE);// 填充交易来源前缀
				context.setData(Constants.PE_FIELD_22,context.getData(Constants.FIX8583_POSE));//第22域
				context.setData(Constants.PE_FIELD_35,context.getData(Constants.FIX8583_TRACK2));//第35域
				context.setData(Constants.PE_FIELD_52,context.getData(Constants.FIX8583_PIN));//第52域
				context.setData(Constants.PE_FIELD_61,context.getData(Constants.FIX8583_IDEN));//第61域
				journalMap = dzipProcessTemplate.getJournalMap4FIX(context.getDataMap(), journalMap); // 初始化journalMap
				context.setData(Constants.PE_TRAN_AMT,journalMap.get(Constants.PE_TRAN_AMT));//填充交易金额
				context.setData(Constants.PE_ACC_NO, journalMap.get(Constants.PE_ACC_NO));// 填充账号
				context.setData(Constants.PE_REACODE,journalMap.get(Constants.PE_REACODE));//填充自定义
				context.setData(Constants.PE_FOW_ORG_CD,journalMap.get(Constants.PE_FOW_ORG_CD));//填充受理机构号
				dzipProcessTemplate.insertJournal(journalMap); // 执行数据库操作，记入交易流水
				if(Constants.PE_OK.equals(responcd)){
					String hostchkcd=String.valueOf(context.getData(Constants.PE_HOSTCHKCD));
					if(Constants.PE_THREE.equals(hostchkcd)||Constants.PE_FOUR.equals(hostchkcd)){
						if(Constants.PE_06.equals( context.getData(Constants.FIX8583_POSC))|| 
								Constants.PE_60.equals( context.getData(Constants.FIX8583_POSC))){//06:预授权; 60：追加预授权;
							context.setData(Constants.FIX8583_RESP,Constants.PE_90);
							throw new PeException("系统正在跑批,不能做预授权类交易");
						}
					}
					getCoreController().execute(context);           // 关联本地Action 进行数据库等操作
					paramsMap = context.getDataMap();
					msgTypResponse(paramsMap,null);
				}else{
					paramsMap.put(Constants.FIX8583_RESP, responcd);
					paramsMap.put(Constants.FIX8583_CANL, "DataBase error");
					context.setData(Constants.PE_HOST_RESP_CD,responcd);
					msgTypResponse(paramsMap,context);
				}
			} catch (SQLException e) {        					//数据库操作异常
				log.error("系统出错========>"+e.getMessage());
				paramsMap.put(Constants.FIX8583_RESP, Constants.PE_ERROR);
				paramsMap.put(Constants.FIX8583_CANL, "DataBase error");
				context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_ERROR);
				msgTypResponse(paramsMap,context);
			} catch (PeException e) {							//Action执行抛出的自定义异常
				log.error("exec_core_controller_failed:", e);
				LoggingInterceptor.cleanUp();
				context.setState(0x1869f);
				paramsMap.put(Constants.FIX8583_RESP, ((String)context.getData(Constants.FIX8583_RESP)).trim().equals(Constants.PE_NULL)?Constants.PE_ERROR:context.getData(Constants.FIX8583_RESP));
				context.setData(Constants.PE_HOST_RESP_CD,paramsMap.get(Constants.FIX8583_RESP));
				msgTypResponse(paramsMap,context);
			}
			
		}
		else{    //判断渠道不可用
			paramsMap.put(Constants.FIX8583_RESP, Constants.PE_CHANN_INVALIDATE);
			paramsMap.put(Constants.FIX8583_CANL, "渠道不可用!");
			context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_CHANN_INVALIDATE);
			msgTypResponse(paramsMap,context);
		}
		try {
			formatedMessage = fixedLength8583Processor.format(paramsMap, element);// 组返回报文
			respBuffer = formatedMessage.getBytes(Constants.CHARSET_ISO_8859_1);
			Element e = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
			fixedLength8583Processor.parse(formatedMessage, e);
			log.debug("send message length =====>" + respBuffer.length);
			log.debug("send message=============>" + new String(respBuffer, Constants.CHARSET_GBK));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return respBuffer;
	}
		/**
	 * @param paramsMap
	 */
	@SuppressWarnings("unchecked")
	private void msgTypResponse(Map paramsMap,Context context) {
		String msgTyp = (String)paramsMap.get(Constants.FIX8583_MESG); 		//消息类型
		String repMsgTyp = dzipProcessTemplate.getResMsgTyp(msgTyp);
		if(Constants.PE_NULL.equals(repMsgTyp))
			paramsMap.put(Constants.FIX8583_MESG,msgTyp);						//返回消息类型
		else
			paramsMap.put(Constants.FIX8583_MESG,repMsgTyp);					//返回消息类型
		if(context!=null)
		    updateJoural.execute(context);
	}
	public FixedLength8583Processor getFixedLength8583Processor() {
		return fixedLength8583Processor;
	}

	public void setFixedLength8583Processor(
			FixedLength8583Processor fixedLength8583Processor) {
		this.fixedLength8583Processor = fixedLength8583Processor;
	}
	/**
	 * @return the peCupsNOIdFactory
	 */
	public IdFactory getPeCupsNOIdFactory() {
		return peCupsNOIdFactory;
	}

	/**
	 * @param peCupsNOIdFactory the peCupsNOIdFactory to set
	 */
	public void setPeCupsNOIdFactory(IdFactory peCupsNOIdFactory) {
		this.peCupsNOIdFactory = peCupsNOIdFactory;
	}

	/**
	 * @return the dzipProcessTemplate
	 */
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}

	/**
	 * @param dzipProcessTemplate the dzipProcessTemplate to set
	 */
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
	/**
	 * @return the peJournalNOIdFactory
	 */
	public IdFactory getPeJournalNOIdFactory() {
		return peJournalNOIdFactory;
	}

	/**
	 * @param peJournalNOIdFactory the peJournalNOIdFactory to set
	 */
	public void setPeJournalNOIdFactory(IdFactory peJournalNOIdFactory) {
		this.peJournalNOIdFactory = peJournalNOIdFactory;
	}
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
}
