package csii.dzip.core;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

import com.csii.pe.channel.stream.tcp.SimpleXMLHandler;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.Channel;
import com.csii.pe.service.id.IdFactory;
import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.Transformer;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.Init;
import csii.dzip.action.util.PinSecurityModule;
import csii.dzip.action.util.UpdateJoural;
/**
 * XML报文转换为8583报文
 * @author xiehai
 * @date 2013-12-31 下午05:59:57
 */
public class XML2ISO8583Handler extends SimpleXMLHandler {
	private DzipProcessTemplate dzipProcessTemplate;
	private IdFactory peJournalNOIdFactory;
	private IdFactory peOnliNoIdFactory;
	private UpdateJoural updateJoural;
	private ActionUtilProcessor utilProcessor;
	private DzipToCUPSTransport cupsTransport;
	private PinSecurityModule pinSecurityModule ;
	private PinSecurityModule pinSecurityModule4Onli;
	@Override
	@SuppressWarnings("unchecked")
	protected Object parseStream(Channel paramChannel, byte[] paramArrayOfByte)
			throws TransformException {
		String s = null;
		if (getParserResolver() == null) {
			s = getDefaultParser();
		} else {
			s = getParserResolver().getParser(paramChannel, paramArrayOfByte);
		}
		Transformer transformer = getTransformerFactory().getTransformer(s);
		log.info("recv Length =====>" + paramArrayOfByte.length);
		log.info("recv xml message=====>" +new String(paramArrayOfByte));
		Map<String, Object> map = (Map<String, Object>)transformer.parse(new ByteArrayInputStream(paramArrayOfByte), null);
		return map;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected byte[] handleInternal(Channel paramChannel, Object paramObject) {
		log.info("===================>XML2ISO8583Handler handleInternal is executing!");
		String formatName = null; // 返回报文格式定义文件名
		String errorFormatName = null; // 错误报文格式定义文件名
		String pin = null;
		String peJournalNO = null; // 交易流水号
		String peOnliNo = null;//37#
		String cardType = null; // 卡种名称
		Map<String, Object> journalMap = new HashMap<String, Object>(); // 记录交易流水的参数Map
		Map<String, Object> paramsMap = (Map<String, Object>) paramObject; // 传入的参数Map
		log.info("=====================>paramsMap before init:" + paramsMap);
		Init.initXml2Iso8583(paramsMap);
		String transactionId = getIdentityResolver().getIdentity(paramsMap); // 取得交易ID交易的唯一标识
		log.info("=====================>transactionId is :" + transactionId);
		Context context = createContext(transactionId, paramsMap, null, getSessionManager(), paramChannel);// 创建上下文
		log.info("=====================>context:" + context.getDataMap());
		formatName = getDefaultFormat();// 取得返回报文格式文件名称
		log.info("=====================>formatName :" + formatName + " " + context.getDataMap());
		errorFormatName = getDefaultErrorFormat();// 取得返回错误报文格式文件名称
		log.info("=====================>errorFormatName :" + errorFormatName);
		try {
			Init.initFieldsValue(context);
			Map checkMap = new HashMap();
			checkMap.put(Constants.PE_CHANN_ID, Constants.PE_ATMP);//本行
			checkMap.put(Constants.TRANCD, paramsMap.get(Constants.TransactionId));
			checkMap.put(Constants.IN_CHANNELTYPCD, Constants.PE_06);//渠道:柜面 #60.2.5
			if (dzipProcessTemplate.querychannTranSta(checkMap)) { // 判断渠道可用
				log.info("=====================>the channel is available!");
				peJournalNO = String.valueOf(peJournalNOIdFactory.generate()); // 交易流水赋值
				log.info("=====================>peJournalNO is: "	+ peJournalNO);
				peOnliNo = String.valueOf(peOnliNoIdFactory.generate());
				context.setData(Constants.ISO8583_RTVREFNUM, peOnliNo);
				context.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_0);// 业务性质 ；本代本
				log.info("=====================>check the Card is our card ?");
				String AccNum = String.valueOf(paramsMap.get(Constants.ISO8583_ACCTNO)); // 取卡号
				cardType = dzipProcessTemplate.queryCardType(AccNum); //验证卡号 是否属于达州商行本行卡
				log.info("=====================>CardType is : " + cardType);
				if ((Constants.TRANCD_020062.equals(context.getData(Constants.TransactionId))
					|| Constants.TRANCD_042062.equals(context.getData(Constants.TransactionId)))
					&& (Constants.OUR_COMMON_CARD.equals(cardType)
						|| Constants.OUR_JINCHENG_CARD.equals(cardType)
						|| Constants.OUR_IC_CARD.equals(cardType))) {
					AccNum = String.valueOf(paramsMap.get(Constants.ISO8583_ACCIDE_N2));// 取转入卡号
					cardType = dzipProcessTemplate.queryCardType(AccNum); //验证转入卡号 是否属于达州商行本行卡
				}
				//设置接受机构码 报文头目标地址发往银联数据
				context.setData(Constants.ISO8583_DESTID, "00010000"/*dzipProcessTemplate.queryParam(Constants.RCVG_CD_YLSJ)*/);
				context.setData(Constants.ISO8583_RCVCODE, InitData4Dzip.getAcqOrgCd()); //接收方机构码
				context.setData(Constants.PE_CARD_TYPE, cardType);
				if(Constants.PE_NULL.equals(context.getData(Constants.TransactionId))
				  || !Constants.OUR_IC_CARD.equals(cardType)){//如果交易码在Init中不存在或不是IC卡电子现金交易
					return exceptionProcessor(errorFormatName, context, "柜面目前只支持电子现金交易");
				}
				//设置交易信息
				context.setData(Constants.PE_POST_DATE, dzipProcessTemplate.queryPostDate());//账务日期
				context.setData(Constants.PE_HOSTCHKCD, Constants.PE_ONE);//主机对账标识
				context.setData(Constants.TAXRPTFORPERSNBR,
						dzipProcessTemplate.queryTaxrptForPersNbr(context, Constants.ISO8583));//客户号
				context.setData(Constants.IN_ORIGNTWKNODENBR, context.getData(Constants.ISO8583_CARDACCID));//终端号ntwknodenbr

				context.setData(Constants.PE_CRDB, checkMap.get(Constants.PE_CRDB)); 	// 填充借贷标记
				context.setData(Constants.PE_JOURNAL_NO, peJournalNO);					//平台流水号
				context.setData(Constants.PE_REQ_CHANN, Constants.PE_ATMP);  			//通信渠道
				journalMap = dzipProcessTemplate.getJournalMap4ISO(context.getDataMap(), journalMap); // 初始化journalMap
				context.setData(Constants.PE_TRAN_AMT, journalMap.get(Constants.PE_TRAN_AMT)); // 填充交易金额
				context.setData(Constants.PE_ACC_NO, journalMap.get(Constants.PE_ACC_NO));// 填充账号
				context.setData(Constants.IN_AVAILMETHCD, Constants.AVAILMETHCD); // 填充可用方式
				context.setData(Constants.PE_FOW_ORG_CD, journalMap.get(Constants.PE_FOW_ORG_CD));//填充转发机构号
				Init.initOutSideFlag(context);//通过F33后四位是0344判断为境外交易
				journalMap.put(Constants.PE_CASHBOXNBR, context.getData(Constants.IN_CASHBOXNBR));//现金箱号
				dzipProcessTemplate.insertJournal(journalMap); // 执行数据库操作，记入流水
				log.info("=====================>insertJournal " + peJournalNO + " successful");
				//测试不验密码
//				context.setData(Constants.CHECK_PIN, Constants.PE_N);
				if(Constants.OUR_IC_CARD.equals(cardType)) {
					if(Constants.PE_Y.equals(context.getString(Constants.CHECK_PIN))) {//有密码交易
						pin = String.valueOf(context.getData(Constants.ISO8583_PINDATA));
						String timeStamp = String.valueOf(context.getData(Dict.IBS_TIME_STAMP));//PE时间戳
						int len = timeStamp.length();
						String pinKey = timeStamp.substring(len-8, len);//取时间戳的后8位
						context.setData(Constants.IN_PIN, Init.getPinFromOnli(pinKey, pin));
					}
					if(null != context.getData(Constants.ISO8583_TRACK2_DATA)
							&& !Constants.PE_NULL.equals(context.getData(Constants.ISO8583_TRACK2_DATA))
							&& Constants.PE_Y.equals(context.getData(Constants.CHECK_TRACK2))){
						context.setData(Constants.IN_TRACK2, context.getData(Constants.ISO8583_TRACK2_DATA));
					}
					//账户验证
					context.setData(Constants.PE_ACC_NO, context.getData(Constants.ISO8583_ACCTNO));
					context.setData(Constants.IN_CASHBOXNBR, InitData4Dzip.getYLCashBoxNbr());
					log.info("==========================>check the card info!");
					Map<String, Object> resultMap = utilProcessor.selectAcctInfo(context, Constants.ISO8583);
					if(!Constants.PE_OK.equals(String.valueOf(resultMap.get(Constants.OUT_RESPONCD)))){//若密码验证不通过,直接返回前台
						log.error("==========================>check pin error and the respondcd is " + resultMap.get(Constants.OUT_RESPONCD));
						Map<String, Object> resMap = dzipProcessTemplate.getResp(String.valueOf(resultMap.get(Constants.OUT_RESPONCD)));
						String errorMsg = new String(String.valueOf(resMap.get(Constants.RESPNAME)));
						return exceptionProcessor(errorFormatName, context, errorMsg);
					}
					log.info("==========================>check card is ok!");
				}
				handleTransaction(errorFormatName, context);
				if(null != context.getData(Constants.PE_MESSAGE_CODE)){//如果报文还未转发到银联就出现了异常
					return null;
				}
				//若核心记账成功或银联数据返回不成功则返回报文给柜面
				log.info("==================>response message to onli!");
				byte[] responMessage = formatStream(context.getDataMap(), formatName, context);
				String msg = new String(responMessage);
				if (msg != null){
					Time localTime = new Time(System.currentTimeMillis());
					FileOutputStream localFileOutputStream = new FileOutputStream("C:/tmpOnli/" + "snd" + Thread.currentThread().getName() + localTime.toString().replace(':', '_'));
					localFileOutputStream.write(msg.getBytes());
					localFileOutputStream.flush();
					localFileOutputStream.close();
					writeStream(paramChannel, msg.getBytes());
			    }
				msg = new String(msg.getBytes(Constants.CHARSET_GBK), Constants.CHARSET_UTF8);
				log.info("发送到ONLI数据["+ msg +"]");
				return null;
			}else{// 渠道不可用
				return exceptionProcessor(errorFormatName, context, "渠道不可用");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			String message = Errors.SYSTEM_ERROR;
			return exceptionProcessor(errorFormatName, context, message);
		}
	}

	/**
	 * 交易处理,目前只有电子现金类交易
	 * @param formatName
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private byte[] handleTransaction(String formatName, Context context) {
		String origData = "";			//临时变量，存放原始数据
		String pin = context.getString(Constants.IN_PIN);//获得解密后的密码
		String peJournalNO = context.getString(Constants.PE_JOURNAL_NO);//获得平台流水号
		if(Constants.TRANCD_020062.equals(context.getData(Constants.TransactionId))){//如果是本代本的非指定账户转账圈存交易
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
				String message ="转账圈存转换转入圈存出错!";
				return exceptionProcessor(formatName, context, message);// 异常处理
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
				String message = "转账圈存冲正转换转入圈存冲正出错!";
				return exceptionProcessor(formatName, context, message);// 异常处理
			}
		}
		try {
			if (context.getData(Constants.ISO8583_PINDATA)!=null) {
				String pan = String.valueOf(context.getData(Constants.ISO8583_ACCTNO));
				if(Constants.PE_Y.equals(context.getString(Constants.CHECK_PIN))){//有密码交易
					if(pan != null)	{
						try {
					        String bankEnPass = pinSecurityModule.bankPinEncrypt(pin, pan);//调用银联加密算法
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
			if(context.getData(Constants.ISO8583_ORGDATA) != null
					&& !Constants.PE_NULL.equals(context.getString(Constants.ISO8583_ORGDATA))){
				origData =(String)context.getData(Constants.ISO8583_ORGDATA);
				Map resultMap = utilProcessor.getOriSysTraNum(context);
				String origTracNum = String.valueOf(resultMap.get(Constants.JOURNAL_NO));
				origData=origData.substring(0, 4)+origTracNum+origData.substring(10, 42);
				context.setData(Constants.ISO8583_ORGDATA, origData);
				context.setData(Constants.ISO8583_RTVREFNUM, resultMap.get(Constants.PE_REF_CD));//填充原交易37#
			}
			if(!Constants.PE_NULL.equals(context.getData(Constants.ISO8583_ICCSYSRELDATA))){
				context.setData(Constants.ISO8583_ICCSYSRELDATA,
						Util.HexToString(String.valueOf(context.getData(Constants.ISO8583_ICCSYSRELDATA))));
			}
			context.setData(Constants.ISO8583_TRANAMT,
					Util.getAmt(String.valueOf(context.getData(Constants.ISO8583_TRANAMT))));//格式化前台的交易金额
			cupsTransport.submit(context); // 转到银联前置
		} catch (Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
			return exceptionProcessor(formatName, context, e.getMessage());// 异常处理
		}
		return null;
	}

	/**
	 * 返回错误XML报文到柜面
	 * @param formatName
	 * @param message
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private byte[] exceptionProcessor(String formatName, Context context, String message) {
		log.info("==================>异常：" + message);
		Init.getResponseTransactionID(context);
		context.setData(Constants.PE_MESSAGE_CODE, Constants.Error_99999);
		context.setData(Constants.PE_ERROR_MESSAGE, message);
		Map<String, Object> outputMap = context.getDataMap();
		try {
			byte[] responMessage = formatStream(outputMap, formatName, null);// 返回错误报文
			String msg = new String(responMessage);
			msg = new String(msg.getBytes(Constants.CHARSET_GBK), Constants.CHARSET_UTF8);
			log.info("发送到柜面的数据" + msg);
			return responMessage;
		} catch (Exception e) {
			log.error("=============>发送到柜面数据失败,方法(XML2ISO8583Handler.exceptionProcessor)" );
		}
		return null;
	}

	public IdFactory getPeOnliNoIdFactory() {
		return peOnliNoIdFactory;
	}

	public void setPeOnliNoIdFactory(IdFactory peOnliNoIdFactory) {
		this.peOnliNoIdFactory = peOnliNoIdFactory;
	}

	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}

	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}

	public IdFactory getPeJournalNOIdFactory() {
		return peJournalNOIdFactory;
	}

	public void setPeJournalNOIdFactory(IdFactory peJournalNOIdFactory) {
		this.peJournalNOIdFactory = peJournalNOIdFactory;
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

	public DzipToCUPSTransport getCupsTransport() {
		return cupsTransport;
	}

	public void setCupsTransport(DzipToCUPSTransport cupsTransport) {
		this.cupsTransport = cupsTransport;
	}

	public PinSecurityModule getPinSecurityModule() {
		return pinSecurityModule;
	}

	public void setPinSecurityModule(PinSecurityModule pinSecurityModule) {
		this.pinSecurityModule = pinSecurityModule;
	}

	public PinSecurityModule getPinSecurityModule4Onli() {
		return pinSecurityModule4Onli;
	}

	public void setPinSecurityModule4Onli(PinSecurityModule pinSecurityModule4Onli) {
		this.pinSecurityModule4Onli = pinSecurityModule4Onli;
	}
}
