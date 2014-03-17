/**
 * ATM ISO8583
 */
package csii.dzip.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.xvolks.jnative.exceptions.NativeException;

import com.csii.pe.channel.stream.tcp.DefaultHandler;
import com.csii.pe.core.Context;
import com.csii.pe.core.LoggingInterceptor;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.Channel;
import com.csii.pe.service.id.IdFactory;
import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.stream.VirtualSegment;

import csii.base.action.util.FileUtils;
import csii.base.action.util.ISO8583MsgLogUtils;
import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DecryptSecApi;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.PinSecurityModule;
import csii.dzip.action.util.UpdateJoural;
import csii.pe.service.comm.tcp.queue.SingleTcpClient;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-6-10
 */
public class ISO8583Handler_temp extends DefaultHandler {
	
	private boolean debug;
	
	public static final String INVALID_PARAMSMAP = "invalidParamsMap";
	
	private DzipVirtualSegment dzipVirtualSegment;
	
	private DzipProcessTemplate dzipProcessTemplate;
	
	private FixedLength8583Processor fixedLength8583Processor;
	
	private DzipToCUPSTransport cupsTransport;
	
	private DzipJinCheng2HostTransport jinchengTransport;
	
	private IdFactory peJournalNOIdFactory;
	
	private SingleTcpClient singleTcpClient;
	
	private UpdateJoural updateJoural;
	
	private ActionUtilProcessor utilProcessor;
	
	private PinSecurityModule pinSecurityModule ;
	private DecryptSecApi decryptSecApi ;
	private String fwrdCd;
	private String acqrCd;
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
		try {
			log.info("recv Length =====>" + paramArrayOfByte.length);
			log.info("recv =====>" +new String(paramArrayOfByte));
			map = (Map) dzipVirtualSegment.parse(new ByteArrayInputStream(paramArrayOfByte), null);
			String acqrCd=InitData4Dzip.getAcqOrgCd();
			String fwrdCd=InitData4Dzip.getForwOrgCd();
			map.put(Constants.ISO8583_ACQCODE,acqrCd);      //受理机构转换
			map.put(Constants.ISO8583_FORWARDCODE,fwrdCd);  //发送机构转换
			if(map.get(Constants.ISO8583_ORGDATA)!=null     //原始数据元转换
					&& !Constants.PE_NULL.equals(map.get(Constants.ISO8583_ORGDATA).toString().trim())){
				String origData =(String)map.get(Constants.ISO8583_ORGDATA);
				String orgAcqOrgCd=Util.fill(acqrCd,11,Constants.LEFT,Constants.PE_ZERO);
				String orgFwdOrgCd=Util.fill(fwrdCd,11,Constants.LEFT,Constants.PE_ZERO);
				origData=origData.substring(0,20)+orgAcqOrgCd+orgFwdOrgCd;
				map.put(Constants.ISO8583_ORGDATA,origData);
			}
		} catch (TransformException e) {
			map.put(Constants.ISO8583_RESCODE,Constants.PE_ERROR);
			throw new TransformException("invalid_packet_data");
		}
		return map;
	}
	@SuppressWarnings("unchecked")
	protected byte[] handleInternal(Channel paramChannel, Object paramObject) {
		log.info("===========================>handleInternal is Excuting");
		String formatName = null; // 返回报文格式定义文件名
		String errorFormatName = null; // 错误报文格式定义文件名
		String cardType = null; // 卡种名称
		String pin=null;
		String responcd=null;
		String peJournalNO = null; // 交易流水号
		Map outputMap = new HashMap(); // 传出参数Map
		Map journalMap = new HashMap(); // 记录交易流水的参数Map
		Map paramsMap = (Map) paramObject; // 传入的参数Map
		ISO8583MsgLogUtils.logFormat(paramsMap,"收到ATMP端发来的报文", log);
		String outtranid = null;
		String origData ="";				//临时变量，存放原始数据
		String transactionId = getIdentityResolver().getIdentity(paramsMap); // 取得交易ID交易的唯一标识
		String sessionID = (String) paramsMap.get(Constants.ISO8583_SESSIONID); // 获得SessionID
		log.info("=====================>transactionId is :" + transactionId);
		Context context = createContext(transactionId, paramsMap, sessionID, getSessionManager(), paramChannel);// 创建上下文
		log.info("=====================> create Context ok!");
		formatName = resolveFormatName(context);
		log.info("=====================>formatName :" + resolveFormatName(context));
		errorFormatName = resolveFormatName(context);// 取得返回报文格式文件名称
		Map checkMap = new HashMap();
		checkMap.put(Constants.PE_CHANN_ID, Constants.PE_ATMP);
		checkMap.put(Constants.TRANCD, paramsMap.get(Constants.TransactionId));
	    if (dzipProcessTemplate.querychannTranSta(checkMap)) { // 判断渠道可用
			try {
				peJournalNO = peJournalNOIdFactory.generate().toString(); // 交易流水赋值
				log.info("=====================>peJournalNO is: "	+ peJournalNO);
				String AccNum = paramsMap.get(Constants.ISO8583_ACCTNO)	.toString(); // 取卡号
				log.info("==================>check the Card is our card ?");
				cardType = dzipProcessTemplate.queryCardType(AccNum); //验证卡号 是否属于达州商行本行卡
				log.info("==================>CardType is : " + cardType);
				context.setData(Constants.PE_CARD_TYPE, cardType);
				context.setData(Constants.RTXNCATCD,Constants.RTXNCATCD_0);// 业务性质 ；本代本
				if (Constants.CUPS_CARD.equals(cardType)||Constants.JINCHENG_CARD.equals(cardType)){
					context.setData(Constants.ISO8583_RCVCODE,InitData4Dzip.getRcvgCd()); //接收方机构码
					context.setData(Constants.RTXNCATCD,Constants.RTXNCATCD_02);// 业务性质 ；本代他
				}
				responcd =dzipProcessTemplate.getSysTranInfo(context,Constants.ISO8583); //获得系统交易参数信息
				if(Constants.PE_SYSTEM_ERROR.equals(responcd)){
					context.setData(Constants.ISO8583_RESCODE, responcd);
					throw new PeException("系统错误");
				}
				context.setData(Constants.PE_CRDB, checkMap.get(Constants.PE_CRDB)); 	// 填充借贷标记
				context.setData(Constants.PE_JOURNAL_NO, peJournalNO);					//平台流水号
				context.setData(Constants.PE_REQ_CHANN, Constants.PE_ATMP);  			//通信渠道
				journalMap = dzipProcessTemplate.getJournalMap4ISO(context.getDataMap(), journalMap); // 初始化journalMap
				context.setData(Constants.PE_TRAN_AMT, journalMap.get(Constants.PE_TRAN_AMT)); // 填充交易金额
				context.setData(Constants.PE_ACC_NO, journalMap.get(Constants.PE_ACC_NO));// 填充账号
				dzipProcessTemplate.insertJournal(journalMap); // 执行数据库操作，记入流水
				context.setData(Constants.PE_TRAN_AMT, journalMap.get(Constants.PE_TRAN_AMT)); // 填充交易金额
				context.setData(Constants.PE_ACC_NO, journalMap.get(Constants.PE_ACC_NO));// 填充账号
				context.setData(Constants.IN_AVAILMETHCD, Constants.AVAILMETHCD); // 填充可用方式
				context.setData(Constants.PE_FOW_ORG_CD,journalMap.get(Constants.PE_FOW_ORG_CD));//填充转发机构号
				log.info("=====================>insertJournal " + peJournalNO + " successful");
				if(!Constants.PE_OK.equals(responcd)){ //验证系统是否离线
					context.setData(Constants.ISO8583_RESCODE, responcd);
					context.setData(Constants.PE_HOST_RESP_CD,responcd);
					return exceptionProcessor(errorFormatName, outtranid, context, "系统属于离线状态");// 异常处理
				}
//				String check = dataProcessor.validateParams( paramsMap); // 验证每个域的合法性
				if (context.getData(Constants.ISO8583_PINDATA) !=null&&!Constants.PE_NULL.equals(context.getData(Constants.ISO8583_PINDATA))) {
//					String pan = (String) context.getData(Constants.ISO8583_ACCTNO);  
////					pin=decryptSecApi.HSMAPIDecryptPIN((String)context.getData(Constants.ISO8583_PINDATA), pan); //解密ATM发来的密码
//					pin=(String)context.getData(Constants.ISO8583_PINDATA);
//					if(pin==null){
//						context.setData(Constants.ISO8583_RESCODE, Constants.PE_55);
//						context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_55);
//						return exceptionProcessor(errorFormatName, outtranid, context, "解密出错");// 异常处理
//					}	
					context.setData(Constants.IN_PIN,context.getData(Constants.ISO8583_PINDATA));  //解密后的密码放入context中
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				String message = Errors.SYSTEM_ERROR;
				context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE)==null?Constants.PE_ERROR:context.getData(Constants.ISO8583_RESCODE));
				return exceptionProcessor(errorFormatName, outtranid, context, message);// 异常处理
			}
			/******************************** 达州商行本行卡 ************************************/
			if (Constants.OUR_COMMON_CARD.equals(cardType)
					|| Constants.OUR_JINCHENG_CARD.equals(cardType)) {  // 如果是本行卡直接调用核心存储记账 
				context.setData(Constants.IS_OUR_CARD, Constants.PE_Y); // 填充是否本行卡标识
				try {
					log.info("==================>begin excute : " + context.getTransactionId());
					this.executeConcreteAction(context);
					log.info("==================>excute " + context.getTransactionId() + " ok!");
				} catch (Exception e) {
					log.error(e.getMessage());
					String message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
					if (e.getMessage().contains(Errors.SYSTEM_UNDEFINED_TRANSACTION))
						message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
					context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
					return exceptionProcessor(errorFormatName, outtranid, context, message);// 异常处理
				}
				context.setData(Constants.IN_PIN, "");//去掉防止打印明文密码
				if(context.getData("_dataMap")!=null){
					((Map)context.getData("_dataMap")).put(Constants.IN_PIN,"");
				}
				if (context.getState() == -1)
					return null;
				outputMap = context.getDataMap();     // 获得返回数据
				ISO8583MsgLogUtils.logFormat(outputMap,"发送报文到ATMP端", log);
				try {
					byte[] responMessageresponMessage = formatStream(outputMap, formatName, context);
				} catch (TransformException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				try {
//					log.info("==================>return response message!");
//					byte[] responMessage;
//					responMessage = formatStream(outputMap, formatName, context);
//					log.info("发送到ATMP数据["+ new String(responMessage)+"]");
//					Socket localSocket = getSingleTcpClient().getLocalSocket();
//					if (localSocket.isConnected()&&!localSocket.isClosed()) {
//						    log.info("==================>ATMP主机："+ localSocket.getInetAddress() + "， 端口：  " + localSocket.getPort());
//							localSocket.getOutputStream().write(responMessage);
//							localSocket.getOutputStream().flush();
//							log.info("==================>数据发送成功");
//					} else
//						return null;
//				}catch (IOException e) {
//					log.error("=============>发送ATMP数据失败" );
//				}
//				catch (TransformException e) {
//					log.error("=============>组包失败" );
//				}
				return null; // 返回结果报文
			}
		} else {
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
			context.setData(Constants.IN_PIN, "");//去掉防止打印明文密码
			if(context.getData("_dataMap")!=null){
				((Map)context.getData("_dataMap")).put(Constants.IN_PIN,"");
			}
			outputMap = context.getDataMap();
			byte[] responMessage = formatStream(outputMap,formatName, context);// 返回错误报文
			Socket localSocket = getSingleTcpClient().getLocalSocket();
			if(localSocket!=null)
				if(localSocket.isConnected()){
					log.info("==================>发送到ATMP!" +localSocket.getInetAddress() +": "+localSocket.getPort());
					localSocket.getOutputStream().write(responMessage);
					localSocket.getOutputStream().flush();
					log.info("==================>数据发送成功");
				}
				else
					return null; // 返回结果报文
		}catch (IOException e) {
			log.error("=============>发送到ATMP数据失败" );
			e.printStackTrace();
		}catch (TransformException e) {
			log.error("=============>组包失败" );
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 执行具体的Action
	 * 
	 * @param context
	 * @throws Exception 
	 * @throws PeException
	 */
	private void executeConcreteAction(Context context) throws Exception {
		try {
			getCoreController().execute(context);                   // 关联本地Action 进行数据库等操作
		} catch (PeException exception) {
			LoggingInterceptor.cleanUp();
			context.setState(0x1869f);
	        throw exception;
		}
	}
/**
 * 
 * @param b
 * @return
 */
	public static String byte2Hex(byte[] b) {
		StringBuffer  strBuffer = new StringBuffer();
		String str = null;
		for (int n = 0; n < b.length; n++) {
			str = String.format(("%02x"), b[n]);
			strBuffer.append(str);
			}
		return strBuffer.toString();
	}
/**
 * 
 * @param pin
 * @return
 */
	public static String toString(byte[] pin) {
		String s ="";
		for (int i = 0; i < pin.length; i++) {
			s += Integer.toHexString(pin[i]);
		}
		return s;
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

	public FixedLength8583Processor getFixedLength8583Processor() {
		return fixedLength8583Processor;
	}

	public void setFixedLength8583Processor(FixedLength8583Processor fixedLength8583Processor) {
		this.fixedLength8583Processor = fixedLength8583Processor;
	}
	
	public DzipToCUPSTransport getCupsTransport() {
		return cupsTransport;
	}

	public void setCupsTransport(DzipToCUPSTransport cupsTransport) {
		this.cupsTransport = cupsTransport;
	}

	public DzipJinCheng2HostTransport getJinchengTransport() {
		return jinchengTransport;
	}

	public void setJinchengTransport(DzipJinCheng2HostTransport jinchengTransport) {
		this.jinchengTransport = jinchengTransport;
	}
	public SingleTcpClient getSingleTcpClient() {
		return singleTcpClient;
	}

	public void setSingleTcpClient(SingleTcpClient singleTcpClient) {
		this.singleTcpClient = singleTcpClient;
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
	public PinSecurityModule getPinSecurityModule() {
		return pinSecurityModule;
	}

	public void setPinSecurityModule(PinSecurityModule pinSecurityModule) {
		this.pinSecurityModule = pinSecurityModule;
	}
	
	public DecryptSecApi getDecryptSecApi() {
		return decryptSecApi;
	}

	public void setDecryptSecApi(DecryptSecApi decryptSecApi) {
		this.decryptSecApi = decryptSecApi;
	}
	public void setFwrdCd(String fwrdCd){
		this.fwrdCd=fwrdCd;
	}
	public String getFwrdCd(){
		return this.fwrdCd;
	}
	public void setAcqrCd(String acqrCd){
		this.acqrCd=acqrCd;
	}
	public String getAcqrCd(){
		return this.acqrCd;
	}
}

