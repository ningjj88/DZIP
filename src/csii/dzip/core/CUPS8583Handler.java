/**
 * 变长 ISO8583
 */
package csii.dzip.core;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;


import com.csii.pe.channel.stream.tcp.DefaultHandler;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.Channel;
import com.csii.pe.service.id.IdFactory;
import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.stream.VirtualSegment;

import csii.base.action.util.ISO8583MsgLogUtils;
import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.PinSecurityModule;
import csii.dzip.action.util.UpdateJoural;
import csii.log.MyLog;
import csii.pe.service.comm.tcp.queue.BeanQueueStreamLoader;
import csii.pe.service.comm.tcp.queue.SingleTcpClient;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-6-10
 */
public class CUPS8583Handler extends DefaultHandler {
	
	private boolean debug;
	
	public static final String INVALID_PARAMSMAP = "invalidParamsMap";
	private DzipVirtualSegment dzipVirtualSegment;
	private DzipProcessTemplate dzipProcessTemplate;
	private MyLog csiiLog;
	private IdFactory peJournalNOIdFactory;
	private UpdateJoural updateJoural;
	private SingleTcpClient singleTcpClient;
	private PinSecurityModule pinSecurityModule ;
	
	@SuppressWarnings("unchecked")
	protected Object parseStream(Channel paramChannel, byte[] paramArrayOfByte) throws TransformException {
		String s = null;
		if (getParserResolver() == null) {
			s = getDefaultParser();
		} else {
			s = getParserResolver().getParser(paramChannel, paramArrayOfByte);
		}
		Transformer transformer = getTransformerFactory().getTransformer(s);
		VirtualSegment vs = (VirtualSegment) transformer;
		dzipVirtualSegment.setVirtualSegment(vs);
		Map map = new HashMap();
		log.info("recv Length =====>" + paramArrayOfByte.length);
		log.info("recv =====>" +new String(paramArrayOfByte));
		try {
			map = (Map) dzipVirtualSegment.parse(new ByteArrayInputStream(paramArrayOfByte), null);
		} catch (TransformException e) {
			map.put(Constants.ISO8583_RESCODE,Constants.PE_30);
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	protected byte[] handleInternal(Channel paramChannel, Object paramObject) {
		log.info("=======================>handleInternal is Excuting");
		Map paramsMap = (Map) paramObject;
		ISO8583MsgLogUtils.logFormat(paramsMap,"收到银联发来的请求报文", log);
		Map journalMap = new HashMap();
		String outtranid = null;
		Map outputMap = new HashMap(); // 传出参数Map
		String peJournalNO = null; // 平台流水号
		String formatName = null; // 返回报文格式定义文件名
		String errorFormatName = null; // 错误报文格式定义文件名
		String responcd=null;
		String transactionId = getIdentityResolver().getIdentity(paramsMap);
		String sessionID = null; // 获得SessionID
		Context context = createContext(transactionId, paramsMap, sessionID, getSessionManager(), paramChannel);
		formatName = resolveFormatName(context);     //返回报文格式
		errorFormatName = resolveFormatName(context);// 取得返回报文格式文件名称
		Init.initFieldsValue(context);//初始化域中的值
		Map checkMap = new HashMap();
		checkMap.put(Constants.PE_CHANN_ID, Constants.PE_CUPS);
		checkMap.put(Constants.TRANCD, paramsMap.get(Constants.TransactionId));
		checkMap.put(Constants.IN_CHANNELTYPCD, context.getString(Constants.IN_CHANNELTYPCD));
		if (dzipProcessTemplate.querychannTranSta(checkMap)) { // 银联前置渠道可用
			log.info("================>The Channel CUPS(Yin Lian Qianzhi)　is  validate");
			try {
				if(Constants.PE_Y.equals(context.getString(Constants.CHECK_PIN))){//有密码交易
					String enPass = (String) context.getData(Constants.ISO8583_PINDATA);
					if (enPass != null){
						String pan=(String) (String) context.getData(Constants.ISO8583_ACCTNO);
						if(pan != null){
							try {
								String bankDePass=pinSecurityModule.bankPinDecrypt(enPass, pan);//调用银联解密算法
								if(bankDePass.length()>6)
								   bankDePass =bankDePass.substring(0, 6);
								context.setData(Constants.IN_PIN, bankDePass);
							} catch (Exception e) {
								log.error("银联行内解密出错========>"+e.getMessage());
								context.setData(Constants.ISO8583_RESCODE,Constants.PE_99);
								throw new PeException("银联行内解密出错,PIN格式不对");
							}
						}else{
							log.error("有密码无主账号!!!");
						}
					}
				}
				context.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_03);// 业务性质 ；他代本
				responcd =dzipProcessTemplate.getSysTranInfo(context,Constants.ISO8583); //获得系统交易参数信息
				if(Constants.PE_SYSTEM_ERROR.equals(responcd)){
					context.setData(Constants.ISO8583_RESCODE, responcd);
					throw new PeException("系统错误");
				}	
				context.setData(Constants.IN_ORGNBR, InitData4Dzip.getYLOrgNbr()); //机构号数据库配置参数
				log.info("机构号==================>" + InitData4Dzip.getYLOrgNbr());
				context.setData(Constants.IN_CASHBOXNBR, InitData4Dzip.getYLCashBoxNbr()); // 钱箱号,数据库配置参数
				log.info("钱箱号==================>" + InitData4Dzip.getYLCashBoxNbr());
				context.setData(Constants.IN_ORIGPERSNBR, InitData4Dzip.getYLPersNbr()); // 柜员号，数据库配置参数
				log.info("柜员号==================>" + InitData4Dzip.getYLPersNbr());
				context.setData(Constants.IN_ORIGNTWKNODENBR, InitData4Dzip.getYLNtwkNodeNbr()); // 交易站点号，数据库配置参数
				log.info("交易站点号==================>" + InitData4Dzip.getYLNtwkNodeNbr());
				peJournalNO = peJournalNOIdFactory.generate().toString(); // 获取交易流水
				context.setData(Constants.PE_JOURNAL_NO, peJournalNO); // 填充交易流水号
				context.setData(Constants.PE_CRDB, checkMap.get(Constants.PE_CRDB)); // 填充借贷标记
				context.setData(Constants.PE_REQ_CHANN, Constants.PE_CUPS); // 填充交易渠道
				context.setData(Constants.IN_AVAILMETHCD, Constants.AVAILMETHCD); // 填充可用方式
				context.setData(Constants.PRI_SOURCE, Constants.PRI_SOURCE_YLQZ_VALUE);// 填充交易来源前缀
				context.setData(Constants.PE_FIELD_22,context.getData(Constants.ISO8583_SERENTRYMODE));//第22域
				context.setData(Constants.PE_FIELD_35,context.getData(Constants.ISO8583_TRACK2_DATA));//第35域
				context.setData(Constants.PE_FIELD_48,context.getData(Constants.ISO8583_ADDDATAPRI));//第48域
				context.setData(Constants.PE_FIELD_52,context.getData(Constants.ISO8583_PINDATA));//第52域
				context.setData(Constants.PE_FIELD_61,context.getData(Constants.ISO8583_IDENNUMBE));//第61域
				context.setData(Constants.PE_FOW_ORG_CD,context.getData(Constants.ISO8583_FORWARDCODE));//填充转发机构号
				if(!Constants.TRANCD_0522.equals(context.getData(Constants.ISO8583_HEAD_TX_TYPE))
					&&!Constants.TRANCD_0520.equals(context.getData(Constants.ISO8583_HEAD_TX_TYPE))){	
					Init.initOutSideFlag(context);//通过F33后四位是0344判断为境外交易
					journalMap = dzipProcessTemplate.getJournalMap4ISO(context.getDataMap(), journalMap); // 初始化journalMap
					context.setData(Constants.PE_TRAN_AMT,journalMap.get(Constants.PE_TRAN_AMT));// 填充交易金额
					context.setData(Constants.PE_ACC_NO, journalMap.get(Constants.PE_ACC_NO));// 填充账号
					context.setData(Constants.PE_REACODE,journalMap.get(Constants.PE_REACODE));//填充自定义
					dzipProcessTemplate.insertJournal(journalMap); // 执行数据库操作，记入交易流水
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				String message = Errors.SYSTEM_ERROR;
				if(context.getData(Constants.ISO8583_RESCODE)==null)
					context.setData(Constants.ISO8583_RESCODE,Constants.PE_ERROR);
				context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
				return exceptionProcessor(errorFormatName, outtranid, context, message);// 异常处理
			}
			try {
				if(Constants.PE_OK.equals(responcd)){
					String hostchkcd=String.valueOf(context.getData(Constants.PE_HOSTCHKCD));
					if(Constants.PE_THREE.equals(hostchkcd)||Constants.PE_FOUR.equals(hostchkcd)){
						if(Constants.PE_06.equals( context.getData(Constants.ISO8583_SERCONDCODE)) ||
								Constants.PE_60.equals(context.getData(Constants.ISO8583_SERCONDCODE))){ //06:预授权; 60：追加预授权;
							context.setData(Constants.ISO8583_RESCODE,Constants.PE_90);
							throw new PeException("系统正在跑批,不能做预授权类交易");
						}
					}
					log.info("==================>begin excute : "+ context.getTransactionId());
					getCoreController().execute(context); // 关联本地Action 进行数据库等操作
					log.info("==================>excute : " + context.getTransactionId() + " ok!");
				}else{
					context.setData(Constants.ISO8583_RESCODE, responcd);
					throw new PeException("不能交易当前响应吗为"+responcd);
				}
				String msgTyp = (String)context.getData(Constants.ISO8583_HEAD_TX_TYPE); 		//消息类型
				String repMsgTyp = dzipProcessTemplate.getResMsgTyp(msgTyp);
				if(Constants.PE_NULL.equals(repMsgTyp))
					context.setData(Constants.ISO8583_HEAD_TX_TYPE,msgTyp);						//返回消息类型
				else
					context.setData(Constants.ISO8583_HEAD_TX_TYPE,repMsgTyp);					//返回消息类型
				log.info("==================>excute : " + context.getTransactionId() + " ok!");
			} catch (Exception exception) {
				log.error(exception.getMessage());
				String message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
				if (exception.getMessage().contains(Errors.SYSTEM_UNDEFINED_TRANSACTION))
					message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
				if(context.getData(Constants.ISO8583_RESCODE)==null)
					context.setData(Constants.ISO8583_RESCODE,Constants.PE_ERROR);
				context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
				return exceptionProcessor(errorFormatName, outtranid, context, message);// 异常处理
			}
			FillISO8583Value(context);
			if (context.getState() == -1)
				return null;
			outputMap = context.getDataMap();     // 获得返回数据
			ISO8583MsgLogUtils.logFormat(outputMap,"返回请求报文到银联", log);
			try {
				log.info("==================>return response message!");
				byte[] responMessage = formatStream(outputMap, formatName, context);
				log.info("The length of message :"+ responMessage.length);
				log.info("Data to CUP Host:["+ new String(responMessage)+"]");
				BeanQueueStreamLoader.getSpecialQueueStream("cupsIso").QueueSubmit(responMessage);
				return null; // 返回结果报文
			}catch (TransformException e) {
				log.error("=============>发送到银联数据组包失败" );
				context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_30);
				updateJoural.executeRespond(context);
			}catch (Exception e) {
				log.error("=============>发送银联数据失败" );
				context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_30);
				updateJoural.executeRespond(context);
			}
		}else { // 渠道不可用
			context.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
			context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
			return exceptionProcessor(errorFormatName, outtranid, context,"渠道不可用");// 渠道不可用
		}
		return null;
	}
	/**
	 * @param formatName
	 * @param outtranid
	 * @param context
	 * @param e
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	private byte[] exceptionProcessor(String formatName, String outtranid,
		Context context, String message) {
		Map outputMap = null;
		try {
			String msgTyp = (String)context.getData(Constants.ISO8583_HEAD_TX_TYPE); 		//消息类型
			String repMsgTyp = dzipProcessTemplate.getResMsgTyp(msgTyp);
			if(Constants.PE_NULL.equals(repMsgTyp))
				context.setData(Constants.ISO8583_HEAD_TX_TYPE,msgTyp);						//返回消息类型
			else
				context.setData(Constants.ISO8583_HEAD_TX_TYPE,repMsgTyp);					//返回消息类型
			context.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); // 交易状态
			updateJoural.execute(context);
			FillISO8583Value(context);
			outputMap = context.getDataMap();
			ISO8583MsgLogUtils.logFormat(outputMap,"返回请求报文到银联", log);
			byte[] responMessage = formatStream(outputMap, formatName, context);
			log.info("发送到CUPS数据["+ new String(responMessage)+"]");
			BeanQueueStreamLoader.getSpecialQueueStream("cupsIso").QueueSubmit(responMessage);
		}catch (TransformException e) {
			log.error("=============>发送到银联组包失败" );
			context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_30);
			updateJoural.executeRespond(context);
		}catch (Exception e) {
			log.error("=============>发送银联数据失败" );
			context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_30);
			updateJoural.executeRespond(context);
		}
		return null;
	}
	
	public void FillISO8583Value(final Context context)
	{
		context.setData(Constants.IN_PIN, "");//去掉防止打印明文密码
		if(context.getData(Constants.ISO8583_REACODE)!=null)
		{
			if(context.getData(Constants.ISO8583_REACODE).toString().length()==27)
			{
				String reaCode=Util.fill((String)context.getData(Constants.ISO8583_REACODE),30,Constants.RIGHT,Constants.PE_ZERO);
				context.setData(Constants.ISO8583_REACODE,reaCode);//向右不齐零到30位
			}
		}else{
			context.setData(Constants.ISO8583_REACODE, Constants.CUPS_REACODE30);
		}
		if(context.getData(Constants.ISO8583_IDENNUMBE)!=null)
		{
			String idenInfo=(String)context.getData(Constants.ISO8583_IDENNUMBE);
			if(idenInfo.length()<30)				
			    idenInfo=Util.fill(idenInfo,30,Constants.RIGHT,Constants.PE_ZERO);
			StringBuffer sb=new StringBuffer(idenInfo);
			sb.replace(0, 22, Constants.CUPS_IDENNUMBE22);
			sb.replace(27,30, Constants.CUPS_IDENNUMBE3);
			context.setData(Constants.ISO8583_IDENNUMBE,sb.toString());
		}
	}
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public IdFactory getPeJournalNOIdFactory() {
		return peJournalNOIdFactory;
	}

	public void setPeJournalNOIdFactory(IdFactory peJournalNOIdFactory) {
		this.peJournalNOIdFactory = peJournalNOIdFactory;
	}
	
	public DzipVirtualSegment getDzipVirtualSegment() {
		return dzipVirtualSegment;
	}

	public void setDzipVirtualSegment(DzipVirtualSegment dzipVirtualSegment) {
		this.dzipVirtualSegment = dzipVirtualSegment;
	}

	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}

	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}

	public SingleTcpClient getSingleTcpClient() {
		return singleTcpClient;
	}

	public void setSingleTcpClient(SingleTcpClient singleTcpClient) {
		this.singleTcpClient = singleTcpClient;
	}
	
	public MyLog getCsiiLog() {
		return csiiLog;
	}
	public UpdateJoural getUpdateJoural() {
		return updateJoural;
	}

	public void setUpdateJoural(UpdateJoural updateJoural) {
		this.updateJoural = updateJoural;
	}
	public void setCsiiLog(MyLog csiiLog) {
		this.csiiLog = csiiLog;
	}
	
	public PinSecurityModule getPinSecurityModule() {
		return pinSecurityModule;
	}

	public void setPinSecurityModule(PinSecurityModule pinSecurityModule) {
		this.pinSecurityModule = pinSecurityModule;
	}
	public static String toHexString(byte[] pin) {
		String s ="";
		for (int i = 0; i < pin.length; i++) {
			s += Integer.toHexString(pin[i] & 0xff);
		}
		return s;
	}
	public static String byte2Hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs;

	}
	public static String toBinaryString(byte b){
		String s = Integer.toBinaryString(b);
		int length=s.length();
		while(length<8)
		{
			s="0"+s;
			length++;
		}return s;
	}
}
