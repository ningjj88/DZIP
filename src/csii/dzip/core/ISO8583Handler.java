/**
 * ATM ISO8583
 */
package csii.dzip.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

import csii.base.action.util.ISO8583MsgLogUtils;
import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DecryptSecApi;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.PinSecurityModule;
import csii.dzip.action.util.UpdateJoural;
import csii.pe.service.comm.tcp.queue.SingleTcpClient;

/**
 * @author 徐锦
 * @version 1.0.0
 * @since 2012-10-30
 */
public class ISO8583Handler extends DefaultHandler {

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
			map.put(Constants.ISO8583_RESCODE,Constants.PE_30);
			throw new TransformException("invalid_packet_data");
		}
		return map;
	}
	@SuppressWarnings("unchecked")
	protected byte[] handleInternal(Channel paramChannel, Object paramObject) {
		log.info("===========================>handleInternal is Excuting");
		String formatName = null; // 返回报文格式定义文件名
		String errorFormatName = null; // 错误报文格式定义文件名
		String pin=null;
		String responcd=null;
		String peJournalNO = null; // 交易流水号
		String cardType = null; // 卡种名称
		Map journalMap = new HashMap(); // 记录交易流水的参数Map
		Map paramsMap = (Map) paramObject; // 传入的参数Map
		ISO8583MsgLogUtils.logFormat(paramsMap,"收到ATMP端发来的报文", log);
		String outtranid = null;
		String transactionId = getIdentityResolver().getIdentity(paramsMap); // 取得交易ID交易的唯一标识
		String sessionID = (String) paramsMap.get(Constants.ISO8583_SESSIONID); // 获得SessionID
		log.info("=====================>transactionId is :" + transactionId);
		Context context = createContext(transactionId, paramsMap, sessionID, getSessionManager(), paramChannel);// 创建上下文
		log.info("=====================> create Context ok!");
		formatName = resolveFormatName(context);// 取得返回报文格式文件名称
		log.info("=====================>formatName :" + formatName);
		errorFormatName = resolveFormatName(context);// 取得返回错误报文格式文件名称
		log.info("=====================>errorFormatName :" + errorFormatName);
		try {
			Init.initFieldsValue(context);//初始化域中的值
			Map checkMap = new HashMap();
			checkMap.put(Constants.PE_CHANN_ID, Constants.PE_ATMP);
			checkMap.put(Constants.TRANCD, paramsMap.get(Constants.TransactionId));
			checkMap.put(Constants.IN_CHANNELTYPCD, context.getString(Constants.IN_CHANNELTYPCD));
		    if (dzipProcessTemplate.querychannTranSta(checkMap)) { // 判断渠道可用
				peJournalNO = peJournalNOIdFactory.generate().toString(); // 交易流水赋值
				log.info("=====================>peJournalNO is: "	+ peJournalNO);
				context.setData(Constants.RTXNCATCD,Constants.RTXNCATCD_0);// 业务性质 ；本代本
				log.info("==================>check the Card is our card ?");
				String AccNum = (String)paramsMap.get(Constants.ISO8583_ACCTNO); // 取卡号
				cardType = dzipProcessTemplate.queryCardType(AccNum); //验证卡号 是否属于达州商行本行卡
				//ATM转账交易需要再验证转入卡是否属于本行卡
				if(Constants.TRANCD_020040.equals(context.getString(Constants.TransactionId))
						&&(Constants.OUR_COMMON_CARD.equals(cardType)|| Constants.OUR_JINCHENG_CARD.equals(cardType) || Constants.OUR_IC_CARD.equals(cardType))){
					AccNum=(String)paramsMap.get(Constants.ISO8583_ACCIDE_N2); // 取转入卡号
					cardType = dzipProcessTemplate.queryCardType(AccNum); //验证转入卡号 是否属于达州商行本行卡
				}else if(Constants.TRANCD_010033.equals(context.getString(Constants.TransactionId))//账户验证关联转账
						 && Constants.PE_07.equals(context.getString(Constants.IN_ASSBUSTYP))
						 && (Constants.OUR_COMMON_CARD.equals(cardType) || Constants.OUR_JINCHENG_CARD.equals(cardType) || Constants.OUR_IC_CARD.equals(cardType))){	//账户验证(转账关联)交易：如果他行卡转本行卡，需要发到银联
					String tranOutNo = (String) paramsMap.get(Constants.ISO8583_ACCIDE_N1);
					if(tranOutNo != null && !Constants.PE_NULL.equals(tranOutNo)) {	//如果未上送102和103域，刚按行内转账处理
						cardType = dzipProcessTemplate.queryCardType(tranOutNo);
					}
				}else if((Constants.TRANCD_020021.equals(context.getString(Constants.TransactionId))
						|| Constants.TRANCD_022021.equals(context.getString(Constants.TransactionId)))//他行有卡存款或存款确认
						&& !(Constants.OUR_COMMON_CARD.equals(cardType) || Constants.OUR_JINCHENG_CARD.equals(cardType) || Constants.OUR_IC_CARD.equals(cardType))){
					String field22 = context.getString(Constants.ISO8583_SERENTRYMODE);
					//如果是有卡存款
					if(Constants.PE_02.equals(field22.substring(0, 2))){	//磁条卡
						context.setData(Constants.ISO8583_SERENTRYMODE, "022");//有卡不包含pin
//						context.setData(Constants.ISO8583_PINDATA, null);	//密码置为空
//						context.setData(Constants.CHECK_PIN, Constants.PE_N);
					} else if(Constants.PE_05.equals(field22.substring(0, 2))) {	//IC卡
						context.setData(Constants.ISO8583_SERENTRYMODE, "052");//有卡不包含pin
					}
				}else if((Constants.TRANCD_020062.equals(context.getData(Constants.TransactionId))
						|| Constants.TRANCD_042062.equals(context.getData(Constants.TransactionId)))//电子现金转账圈存或转账圈存冲正交易
						&& (Constants.OUR_COMMON_CARD.equals(cardType)|| Constants.OUR_JINCHENG_CARD.equals(cardType) || Constants.OUR_IC_CARD.equals(cardType))){
						AccNum = String.valueOf(paramsMap.get(Constants.ISO8583_ACCIDE_N2));// 取转入卡号
						cardType = dzipProcessTemplate.queryCardType(AccNum); //验证转入卡号 是否属于达州商行本行卡
						if(Constants.OUR_IC_CARD.equals(cardType)){//如果转入卡是本行IC卡,发送到银联数据
							//设置接受机构码 报文头目标地址发往银联数据
							context.setData(Constants.ISO8583_DESTID, dzipProcessTemplate.queryParam(Constants.RCVG_CD_YLSJ));
							context.setData(Constants.PE_IS_ICCARD_TRANS, Constants.OUR_IC_CARD);//设置为IC卡电子现金交易
						}else{//转入卡是他行IC卡,发往银联
							cardType = Constants.CUPS_CARD;
						}

				}
				context.setData(Constants.ISO8583_RCVCODE, InitData4Dzip.getAcqOrgCd()); //接收方机构码
				log.info("==================>CardType is : " + cardType);
				context.setData(Constants.PE_CARD_TYPE, cardType);
				if(Constants.OUR_IC_CARD.equals(cardType)){//如果是本行IC卡,则判断IC卡做的交易是否是电子现金交易
					List<String> listCode = Arrays.asList(Constants.TRANCD_020060, Constants.TRANCD_042060,
							Constants.TRANCD_020061, Constants.TRANCD_020063, Constants.TRANCD_042063,
							Constants.TRANCD_020017, Constants.TRANCD_042017, Constants.TRANCD_010061,
							Constants.TRANCD_020061);//电子现金交易的交易码
					List<String> listPinInputType = Arrays.asList(Constants.PE_05, Constants.PE_07, Constants.PE_91, Constants.PE_95, Constants.PE_98);//22#
					String svrCode = String.valueOf(context.getData(Constants.ISO8583_SERENTRYMODE));//22#
					String pinInputType = svrCode.substring(0, 2);
					if(listCode.contains(context.getData(Constants.TransactionId)) && listPinInputType.contains(pinInputType)){
						//设置接受机构码 报文头目标地址发往银联数据
						context.setData(Constants.ISO8583_DESTID, dzipProcessTemplate.queryParam(Constants.RCVG_CD_YLSJ));
						context.setData(Constants.PE_IS_ICCARD_TRANS, Constants.OUR_IC_CARD);//设置为IC卡电子现金交易
					}
				}
				if (Constants.CUPS_CARD.equals(cardType)||Constants.JINCHENG_CARD.equals(cardType)){
					context.setData(Constants.ISO8583_DESTID, InitData4Dzip.getRcvgCd());  //目标地址
					context.setData(Constants.ISO8583_RCVCODE, InitData4Dzip.getRcvgCd()); //接收方机构码
					context.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_02);// 业务性质 ；本代他
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
				context.setData(Constants.IN_AVAILMETHCD, Constants.AVAILMETHCD); // 填充可用方式
				context.setData(Constants.PE_FOW_ORG_CD,journalMap.get(Constants.PE_FOW_ORG_CD));//填充转发机构号
				Init.initOutSideFlag(context);//通过F33后四位是0344判断为境外交易
				dzipProcessTemplate.insertJournal(journalMap); // 执行数据库操作，记入流水
				log.info("=====================>insertJournal " + peJournalNO + " successful");
				if(!Constants.PE_OK.equals(responcd)){ //验证系统是否离线
					context.setData(Constants.ISO8583_RESCODE, responcd);
					context.setData(Constants.PE_HOST_RESP_CD,responcd);
					return exceptionProcessor(errorFormatName, outtranid, context, "不能交易当前响应吗为"+responcd);// 异常处理
				}
				//测试不验密码
//				context.setData(Constants.CHECK_PIN, Constants.PE_N);
				if(Constants.PE_Y.equals(context.getString(Constants.CHECK_PIN))){//有密码交易
					String pan = (String) context.getData(Constants.ISO8583_ACCTNO);
					pin=decryptSecApi.HSMAPIDecryptPIN(context.getString(Constants.ISO8583_PINDATA), pan); //解密ATM发来的密码

//					String key = "";
//					if(pan.length()>=8)
//						key = pan.substring(pan.length()-8,pan.length());
//					pin= Decrypt4Des.decryptPWD(key, (String)context.getData(Constants.ISO8583_PINDATA));  //解密ATM发来的密码

					if(pin==null){
						context.setData(Constants.ISO8583_RESCODE, Constants.PE_99);
						context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_99);
						return exceptionProcessor(errorFormatName, outtranid, context, "解密出错");// 异常处理
					}
					context.setData(Constants.IN_PIN,pin);  //解密后的密码放入context中
				}
				return handleTransaction(errorFormatName,outtranid,context,paramsMap);
		    } else {
				context.setData(Constants.ISO8583_RESCODE,Constants.PE_CHANN_INVALIDATE);
				context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
				return exceptionProcessor(errorFormatName, outtranid, context,"渠道不可用");// 渠道不可用
			}
	    } catch (Exception e) {
			log.error(e.getMessage());
			String message = Errors.SYSTEM_ERROR;
			if(context.getData(Constants.ISO8583_RESCODE)==null)
				context.setData(Constants.ISO8583_RESCODE,Constants.PE_30);
			context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
			return exceptionProcessor(errorFormatName, outtranid, context, message);// 异常处理
		}
	}

	/**
	 * 处理交易:如果是本行卡直接做交易,
	 * 否则如果是锦城卡 转到锦城卡前置,
	 * 否则转发给银联前置
	 * @param formatName
	 * @param outtranid
	 * @param context
	 * @param paramsMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private byte[] handleTransaction(String formatName, String outtranid,Context context,Map paramsMap){
		Map outputMap = new HashMap(); // 传出参数Map
		String origData ="";			//临时变量，存放原始数据
		String pin=context.getString(Constants.IN_PIN);//获得解密后的密码
		String peJournalNO=context.getString(Constants.PE_JOURNAL_NO);//获得平台流水号
		String cardType=context.getString(Constants.PE_CARD_TYPE);//获得是否属于达州商行本行卡标志
		/******************************** 达州商行本行卡 ************************************/
		if (Constants.OUR_COMMON_CARD.equals(cardType)
				|| Constants.OUR_JINCHENG_CARD.equals(cardType)// 如果是本行卡直接调用核心存储记账
				|| (Constants.OUR_IC_CARD.equals(cardType) && !Constants.OUR_IC_CARD.equals(context.getData(Constants.PE_IS_ICCARD_TRANS)))) {
			try {
				log.info("==================>begin excute : " + context.getTransactionId());
				this.executeConcreteAction(context);
				String msgTyp = (String)context.getData(Constants.ISO8583_HEAD_TX_TYPE); 		//消息类型
				String repMsgTyp = dzipProcessTemplate.getResMsgTyp(msgTyp);
				if(Constants.PE_NULL.equals(repMsgTyp))
					context.setData(Constants.ISO8583_HEAD_TX_TYPE,msgTyp);						//返回消息类型
				else
					context.setData(Constants.ISO8583_HEAD_TX_TYPE,repMsgTyp);					//返回消息类型
				log.info("==================>excute " + context.getTransactionId() + " ok!");
			} catch (Exception e) {
				log.error(e.getMessage());
				String message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
				if (e.getMessage().contains(Errors.SYSTEM_UNDEFINED_TRANSACTION))
					message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
				context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
				return exceptionProcessor(formatName, outtranid, context, message);// 异常处理
			}
			context.setData(Constants.IN_PIN, "");//去掉防止打印明文密码
			if (context.getState() == -1)
				return null;
			outputMap = context.getDataMap();     // 获得返回数据
			ISO8583MsgLogUtils.logFormat(outputMap,"返回报文到ATMP端", log);
			try {
				log.info("==================>return response message!");
				byte[] responMessage;
				responMessage = formatStream(outputMap, formatName, context);
				log.info("发送到ATMP数据["+ new String(responMessage)+"]");
				Socket localSocket = getSingleTcpClient().getLocalSocket();
				if (localSocket.isConnected()&&!localSocket.isClosed()) {
				    log.info("==================>ATMP主机："+ localSocket.getInetAddress() + "， 端口：  " + localSocket.getPort());
				    localSocket.getOutputStream().write(responMessage);
					localSocket.getOutputStream().flush();
					log.info("==================>数据发送成功");
				}
			}catch (IOException e) {
				log.error("=============>发送ATMP数据失败" );
				context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_92);
				updateJoural.executeRespond(context);
			}
			catch (TransformException e) {
				log.error("=============>发送到ATMP数据组包失败" );
				context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_30);
				updateJoural.executeRespond(context);
			}
		}
		/******************************* 非达州商行本行卡 ************************************/
		else {
			if (cardType.equals(Constants.JINCHENG_CARD)) { // 如果是锦城卡 转到锦城卡前置。
				try {
					context.setData(Constants.IS_OUR_CARD, Constants.PE_N);
					Map mapFix8583 = FixedLength8583Processor.convert8583(paramsMap, Constants.ISO8583);// ISO8583——>定长8583的转换
					String bankId =String.valueOf(mapFix8583.get(Constants.FIX8583_BANKID)).trim(); //"94002710";
					String pan =String.valueOf(mapFix8583.get(Constants.FIX8583_PAN)).trim();
					mapFix8583.put(Constants.FIX8583_TRAC, peJournalNO);     //替换系统跟踪号
					if(Constants.PE_Y.equals(context.getString(Constants.CHECK_PIN))){//有密码交易
						if(pan != null)	{
							try {
								mapFix8583.put(Constants.FIX8583_PIN,JNative4CapDes.getCap_enc_pin(bankId, pin, pan));//锦城卡加密算法
							} catch (NativeException e) {
								// TODO Auto-generated catch block
								log.error("成商加密出错========>"+e.getMessage());
								throw new PeException("系统错误");
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								log.error("成商加密出错========>"+e.getMessage());
								throw new PeException("系统错误");
							}
						}else{
							log.error("有密码无主账号!!!");
						}
					}
					mapFix8583.put(Constants.FIX8583_RESE, Constants.PE_EXPIREYEAR);//自定义域暂时填0000
					context.setData(Constants.MAP_FIX_8583, mapFix8583);
					if(mapFix8583.get(Constants.FIX8583_ORIG)!=null
							&& !Constants.PE_NULL.equals(mapFix8583.get(Constants.FIX8583_ORIG).toString().trim())){
						origData =String.valueOf(mapFix8583.get(Constants.FIX8583_ORIG));
						Map resultMap =utilProcessor.getOriSysTraNum(context);
						String origTracNum =String.valueOf(resultMap.get(Constants.JOURNAL_NO));
						origData=origData.substring(0, 4)+origTracNum+origData.substring(10, 42);
						mapFix8583.put(Constants.FIX8583_ORIG,origData);
					}
					context.setDataMap(mapFix8583);
					jinchengTransport.submit(context); // 转发到锦城卡前置
				} catch (PeException e) {
					e.printStackTrace();
					String message = Errors.SYSTEM_ERROR;
					context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
					return exceptionProcessor(formatName, outtranid, context, message);// 异常处理
				} catch (Exception e) {
					e.printStackTrace();
					String message = Errors.SYSTEM_ERROR;
					context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
					return exceptionProcessor(formatName, outtranid, context, message);// 异常处理
				}
			 } else { // 如果不是锦城卡，转到银联前置
				//账户验证关联交易是自助存款暂时不支持
				if(Constants.TRANCD_010033.equals(context.getString(Constants.ISO8583_TRANSACTION_ID))
						&&Constants.PE_06.equals(context.getString(Constants.IN_ASSBUSTYP))){
					context.setData(Constants.ISO8583_RESCODE,Constants.PE_57);
					context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
					return exceptionProcessor(formatName, outtranid, context, "他行无卡存款暂时不开通，不允许持卡人进行交易！");
				}
				//不支持他行IC卡存款转账
				if(Constants.TRANCD_020040.equals(context.getString(Constants.ISO8583_TRANSACTION_ID))
					   || Constants.TRANCD_020021.equals(context.getString(Constants.ISO8583_TRANSACTION_ID))){
						String svrCode = (String) context.getData(Constants.ISO8583_SERENTRYMODE);//22#
						if(svrCode != null && !Constants.PE_NULL.equals(svrCode)){
							String code = svrCode.substring(0, 2);
							List<String> list = Arrays.asList(Constants.PE_05, Constants.PE_07, Constants.PE_91, Constants.PE_95, Constants.PE_98);
							if(list.contains(code)){
								context.setData(Constants.ISO8583_RESCODE,Constants.PE_57);
								context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
								return exceptionProcessor(formatName, outtranid, context, "不开通他行IC卡存款转账，不允许持卡人进行交易！");
							}
						}
				}
				if(Constants.OUR_IC_CARD.equals(context.getData(Constants.PE_IS_ICCARD_TRANS))){//电子现金交易密码二磁验证
					if(Constants.PE_Y.equals(context.getData(Constants.CHECK_PIN))
							|| Constants.PE_Y.equals(context.getData(Constants.CHECK_TRACK2))){
						if(null != context.getData(Constants.ISO8583_TRACK2_DATA)
								&& !Constants.PE_NULL.equals(context.getData(Constants.ISO8583_TRACK2_DATA))){
							context.setData(Constants.IN_TRACK2, context.getData(Constants.ISO8583_TRACK2_DATA));
						}
						Map result = utilProcessor.selectAcctInfo(context, Constants.ISO8583);
						if(!Constants.PE_OK.equals(result.get(Constants.OUT_RESPONCD))){
							return exceptionProcessor(formatName, outtranid, context, "本行电子现金交易密码或二磁验证不通过");
						}
					}
					if(Constants.TRANCD_020062.equals(context.getData(Constants.TransactionId))){//如果是本代本的非指定账户转账圈存交易
						//验证转出卡的密码二磁
						String rlstseq = String.valueOf(context.getData(Constants.PE_JOURNAL_NO));//获得原交易(转账圈存)流水号
						peJournalNO = peJournalNOIdFactory.generate().toString();
						//转入圈存需要修改的域
						context.setData(Constants.TransactionId, Constants.TRANCD_020065);//交易码
						context.setData(Constants.ISO8583_PRO_CODE, Constants.PROCODE_650000);
						//将主帐号设置为电子现金帐号,102#和103#同转账圈存
						context.setData(Constants.ISO8583_ACCTNO, context.getData(Constants.ISO8583_ACCIDE_N2));
						context.setData(Constants.PE_JOURNAL_NO, peJournalNO);
						//22#通过取得转账圈存48#的值
						String field48 = String.valueOf(context.getData(Constants.ISO8583_ADDDATAPRI));
						int index = field48.indexOf(Constants.PE_PB);
						String service = field48.substring(index+2, index+2+3);
						context.setData(Constants.ISO8583_SERENTRYMODE, service);//通过转账圈存48#获取
						//去掉转入圈存不需要的域
						context.setData(Constants.ISO8583_TRACK2_DATA, Constants.PE_NULL);
						context.setData(Constants.ISO8583_TRACK3_DATA, Constants.PE_NULL);
						try {
							Map checkMap = new HashMap();
							checkMap.put(Constants.PE_CHANN_ID, Constants.PE_ATMP);
							checkMap.put(Constants.TRANCD, context.getData(Constants.TransactionId));
							checkMap.put(Constants.IN_CHANNELTYPCD, context.getData(Constants.IN_CHANNELTYPCD));
							if(dzipProcessTemplate.querychannTranSta(checkMap)){//判断渠道是否可用
								Map journalMap = new HashMap();
								context.setData(Constants.PE_CRDB, checkMap.get(Constants.PE_CRDB));
								journalMap = dzipProcessTemplate.getJournalMap4ISO(context.getDataMap(), journalMap); // 重新初始化journalMap
								Init.initOutSideFlag(context);//通过F33后四位是0344判断为境外交易
								dzipProcessTemplate.insertJournal(journalMap);//插入转入圈存流水
								log.info("=====================>insert journal " + peJournalNO + " successful");
							}
							context.setData(Constants.PE_RLTSEQNO, rlstseq);//更新原交易流水号
							updateJoural.execute(context);//更新转入圈存流水的原交易
							log.info("=====================>update journal " + peJournalNO + " successful");
						} catch (Exception e) {
							log.error(e.getMessage() + "转账圈存转换转入圈存出错!");
							String message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
							if (e.getMessage().contains(Errors.SYSTEM_UNDEFINED_TRANSACTION))
								message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
							context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
							return exceptionProcessor(formatName, outtranid, context, message);// 异常处理
						}
					}
					if(Constants.TRANCD_042062.equals(context.getData(Constants.TransactionId))){//如果是本代本的非指定账户转账圈存冲正交易
						try {
							//通过90#获得原交易(转账圈存)流水号
							Map resultMap = utilProcessor.getOriSysTraNum(context);
							context.setData(Constants.PE_RLTSEQNO, resultMap.get(Constants.JOURNAL_NO));//原交易(转账圈存)流水号
							//根据转账圈存交易流水查询是否存折转入圈存流水是否存在
							Map map = utilProcessor.getSysseqByRltseqno(String.valueOf(resultMap.get(Constants.JOURNAL_NO)), Constants.TRANCD_020065);
							updateJoural.execute(context);//更新转账圈存冲正流水
							//转入圈存冲正需要修改的域
							context.setData(Constants.TransactionId, Constants.TRANCD_042065);//交易码
							context.setData(Constants.ISO8583_PRO_CODE, Constants.PROCODE_650000);//3#
							context.setData(Constants.ISO8583_HEAD_TX_TYPE, Constants.MSG_TYPE_0420);//报文类型
							//将主帐号设置为电子现金帐号,102#和103#同转账圈存
							context.setData(Constants.ISO8583_ACCTNO, context.getData(Constants.ISO8583_ACCIDE_N2));//2#
							peJournalNO = peJournalNOIdFactory.generate().toString();
							context.setData(Constants.PE_JOURNAL_NO, peJournalNO);
							context.setData(Constants.ISO8583_SERENTRYMODE, map.get(Constants.PE_POSENTRYCODE));//22#
							//去掉不需要的域
							context.setData(Constants.ISO8583_TRACK2_DATA, Constants.PE_NULL);
							context.setData(Constants.ISO8583_TRACK3_DATA, Constants.PE_NULL);
							context.setData(Constants.ISO8583_ACCIDE_N1, Constants.PE_NULL);
							context.setData(Constants.ISO8583_ACCIDE_N2, Constants.PE_NULL);
							//组装转入圈存冲正流水
							Map checkMap = new HashMap();
							checkMap.put(Constants.PE_CHANN_ID, Constants.PE_ATMP);
							checkMap.put(Constants.TRANCD, context.getData(Constants.TransactionId));
							checkMap.put(Constants.IN_CHANNELTYPCD, context.getData(Constants.IN_CHANNELTYPCD));
							if(dzipProcessTemplate.querychannTranSta(checkMap)){//判断渠道是否可用
								Map journalMap = new HashMap();
								context.setData(Constants.PE_CRDB, checkMap.get(Constants.PE_CRDB));
								journalMap = dzipProcessTemplate.getJournalMap4ISO(context.getDataMap(), journalMap); // 重新初始化journalMap
								Init.initOutSideFlag(context);//通过F33后四位是0344判断为境外交易
								dzipProcessTemplate.insertJournal(journalMap);//插入转入圈存冲正流水
								log.info("=====================>insert journal " + peJournalNO + " successful");
							}
							context.setData(Constants.PE_RLTSEQNO, map.get(Constants.PE_SYSSEQNO));//更新原交易流水号
							updateJoural.execute(context);//更新转入圈存冲正流水的原交易
							log.info("=====================>update journal " + peJournalNO + " successful");
						} catch (Exception e) {
							log.error(e.getMessage() + "转账圈存冲正转换转入圈存冲正出错!");
							String message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
							if (e.getMessage().contains(Errors.SYSTEM_UNDEFINED_TRANSACTION))
								message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
							context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
							return exceptionProcessor(formatName, outtranid, context, message);// 异常处理
						}
					}
				}
				try {
					if (context.getData(Constants.ISO8583_PINDATA)!=null) {
						String pan = (String) context.getData(Constants.ISO8583_ACCTNO);
						if(Constants.PE_Y.equals(context.getString(Constants.CHECK_PIN))){//有密码交易
							if(pan != null)	{
								try {
							        String bankEnPass=pinSecurityModule.bankPinEncrypt(pin, pan);//调用银联加密算法
									context.setData(Constants.ISO8583_PINDATA, bankEnPass);
								} catch (Exception e) {
									log.error("银联行内加密出错========>"+e.getMessage());
									context.setData(Constants.ISO8583_RESCODE,Constants.PE_99);
									throw new PeException("银联行内加密出错,PIN格式不对");
								}
							}else{
								log.error("有密码无主账号!!!");
							}
						}
					}
					context.setData(Constants.ISO8583_SYSTRACENUM, peJournalNO);       //替换系统跟踪号
					if(context.getData(Constants.ISO8583_ORGDATA)!=null
							&& !Constants.PE_NULL.equals(context.getString(Constants.ISO8583_ORGDATA))){
						origData =(String)context.getData(Constants.ISO8583_ORGDATA);
						Map resultMap =utilProcessor.getOriSysTraNum(context);
						String origTracNum =String.valueOf(resultMap.get(Constants.JOURNAL_NO));
						origData=origData.substring(0, 4)+origTracNum+origData.substring(10, 42);
						context.setData(Constants.ISO8583_ORGDATA,origData);
					}
					cupsTransport.submit(context); // 转到银联前置
				} catch (Exception e){
					log.error(e.getMessage());
					String message = Errors.SYSTEM_ERROR;
					context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.ISO8583_RESCODE));
					return exceptionProcessor(formatName, outtranid, context, message);// 异常处理
				}
			  }
		}
		return null; // 返回结果报文
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
		log.info("==================>异常："+message);
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
			outputMap = context.getDataMap();
			ISO8583MsgLogUtils.logFormat(outputMap,"返回报文到ATMP端", log);
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
			log.error("=============>发送到ATMP数据失败,方法(ISO8583Handler.exceptionProcessor)" );
		}catch (TransformException e) {
			log.error("=============>发送到ATMP数据组包失败,方法(ISO8583Handler.exceptionProcessor)" );
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

}
