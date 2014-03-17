/**
 * 银联 定长8583
 */
package csii.dzip.core;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;

import com.csii.pe.channel.stream.tcp.DefaultHandler;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.Channel;
import com.csii.pe.service.id.IdFactory;
import com.csii.pe.transform.TransformException;

import csii.base.action.util.FileUtils;
import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;

/**
 * @author chenshq
 * @version 1.0.0
 * @since 2010-8-16
 */
public class CUPSHandler extends DefaultHandler{
	
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
			log.debug("recv message length ============>" + formatedStr.length());
			log.debug("recv message ==============>" + new String(paramArrayOfByte, Constants.CHARSET_GBK));
			Element e = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
			parsedMap = fixedLength8583Processor.parse(formatedStr, e);
			String msgType = parsedMap.get(Constants.FIX8583_MESG).toString();
			String proc = parsedMap.get(Constants.FIX8583_PROC).toString();
			StringBuffer transactionId = new StringBuffer(msgType);
			transactionId.append(proc.substring(0, 2));
			if("06".equals( parsedMap.get(Constants.FIX8583_POSC)) || "60".equals( parsedMap.get(Constants.FIX8583_POSC))) //06:预授权; 60：追加预授权;
				transactionId.append(parsedMap.get(Constants.FIX8583_POSC));
			parsedMap.put(Constants.TransactionId, transactionId.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return parsedMap;
	}

	@SuppressWarnings("unchecked")
	protected byte[] handleInternal(Channel paramChannel, Object paramObject) {
		log	.debug("=======================>handleInternal is Excuting");
		Element element = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
		Map paramsMap = (Map) paramObject;
		log.debug("recv message paramMap ===============>" + paramsMap);
		Map journalMap = new HashMap();
		String postDate = null; // 账务日期
		String peJournalNO = null; 	// 平台流水号
		String formatedMessage = null; // 返回消息
		byte[] respBuffer = null;		//返回报文
		Map checkMap = new HashMap();
		checkMap.put(Constants.PE_CHANN_ID, Constants.PE_CUPS);
		checkMap.put(Constants.TRANCD, paramsMap.get(Constants.TransactionId));
		if(dzipProcessTemplate.queryHostStat()){
			if (dzipProcessTemplate.querychannTranSta(checkMap)) { // 银联前置渠道可用
				log	.debug("================>The Channel CUPS(Yin Lian Qianzhi)　is  validate");
				String transactionId = getIdentityResolver().getIdentity(paramsMap);
				String sessionID = null; // 获得SessionID
				if (!Constants.PE_NULL.equals(String.valueOf(paramsMap.get(Constants.FIX8583_PIN)))) {
					String pin = paramsMap.get(Constants.FIX8583_PIN).toString();
					pin = Util.HexToString(pin).substring(0, 6);
					paramsMap.put(Constants.FIX8583_PIN, pin);
				}
				Context context = createContext(transactionId, paramsMap, sessionID, getSessionManager(), paramChannel);
				try {
					dzipProcessTemplate.updateHostStat(Constants.PE_ONE);  //将平台库表中的主机状态置为： 签到 （1）
					context.setData(Constants.IN_ORGNBR, InitData4Dzip.getYLOrgNbr()); //机构号数据库配置参数
					log.debug("机构号==================>" + InitData4Dzip.getYLOrgNbr());
					context.setData(Constants.IN_CASHBOXNBR, InitData4Dzip.getYLCashBoxNbr()); // 钱箱号,数据库配置参数
					log.debug("钱箱号==================>" + InitData4Dzip.getYLCashBoxNbr());
					context.setData(Constants.IN_ORIGPERSNBR, InitData4Dzip.getYLPersNbr()); // 柜员号，数据库配置参数
					log.debug("柜员号==================>" + InitData4Dzip.getYLPersNbr());
					String  taxrptForPersNbr = dzipProcessTemplate.getTaxrptForPersNbr(context,Constants.FIX8583);
					if(taxrptForPersNbr==null)
						throw new PeException("没有对应的客户号");
					context.setData(Constants.TAXRPTFORPERSNBR, taxrptForPersNbr);      // 填充客户号
					peJournalNO = peJournalNOIdFactory.generate().toString(); // 获取交易流水
					context.setData(Constants.PE_JOURNAL_NO, peJournalNO); // 填充交易流水号
					postDate = dzipProcessTemplate.queryPostDate(); // 查询账务日期
					context.setData(Constants.PE_POST_DATE, postDate); // 填充账务日期
					context.setData(Constants.PE_CRDB, checkMap.get(Constants.PE_CRDB)); // 填充借贷标记
					context.setData(Constants.PE_REQ_CHANN, Constants.PE_CUPS); // 填充交易渠道
					context.setData(Constants.IN_AVAILMETHCD, Constants.AVAILMETHCD); // 填充可用方式
					context.setData(Constants.PRI_SOURCE, Constants.PRI_SOURCE_YLQZ_VALUE);// 填充交易来源前缀
					context.setData(Constants.RTXNCATCD, Constants.RTXNCATCD_03);// 业务性质 ；他代本
					journalMap = dzipProcessTemplate.getJournalMap4FIX(context.getDataMap(), journalMap); // 初始化journalMap
					context.setData(Constants.PE_TRAN_AMT, journalMap.get(Constants.PE_TRAN_AMT));// 填充交易金额
					dzipProcessTemplate.insertJournal(journalMap); // 执行数据库操作，记入交易流水
				} catch (Exception e) {
					System.err.println(e.getMessage());
					paramsMap.put(Constants.FIX8583_RESP, Constants.PE_ERROR);
					paramsMap.put(Constants.FIX8583_CANL, "DataBase error");
				}
				try {
					log.debug("==================>begin excute : "+ context.getTransactionId());
					getCoreController().execute(context); // 关联本地Action 进行数据库等操作
					String msgTyp = (String)context.getData(Constants.FIX8583_MESG); 		//消息类型
					String repMsgTyp = dzipProcessTemplate.getResMsgTyp(msgTyp);
					if(Constants.PE_NULL.equals(repMsgTyp))
						context.setData(Constants.FIX8583_MESG,msgTyp);						//返回消息类型
					else
						context.setData(Constants.FIX8583_MESG,repMsgTyp);					//返回消息类型
					log.debug("==================>excute : " + context.getTransactionId() + " ok!");
					paramsMap = context.getDataMap();
					if (!Constants.PE_NULL.equals(String.valueOf(paramsMap.get(Constants.FIX8583_PIN)))) {
						String pin = paramsMap.get(Constants.FIX8583_PIN).toString();
						pin = Util.StringToHex(pin);
						paramsMap.put(Constants.FIX8583_PIN, pin);
					}
					paramsMap.put(Constants.FIX8583_POSP, "06");
				} catch (Exception exception) {
					System.err.println(exception.getMessage());
					context.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); // 交易状态
					updateJoural.execute(context);									// 修改流水交易状态;			
					String msgTyp = (String)context.getData(Constants.FIX8583_MESG); 		//消息类型
					String repMsgTyp = dzipProcessTemplate.getResMsgTyp(msgTyp);
					if(Constants.PE_NULL.equals(repMsgTyp))
						paramsMap.put(Constants.FIX8583_MESG,msgTyp);						//返回消息类型
					else
						paramsMap.put(Constants.FIX8583_MESG,repMsgTyp);					//返回消息类型
					paramsMap.put(Constants.FIX8583_RESP, context.getData(Constants.FIX8583_RESP));
					paramsMap.put(Constants.FIX8583_ADDI, context.getData(Constants.FIX8583_ADDI));
					paramsMap.put(Constants.FIX8583_EXPI, "0000");   //有效期
//					paramsMap.put(Constants.FIX8583_ADDI,exception.getMessage());
				}
				if (context.getState() == -1)
					return null;
				if (isDebug()) {
					log.debug("output-> " + context.getDataMap());
				}
			}
			else { // 渠道不可用
				String msgTyp = (String)paramsMap.get(Constants.FIX8583_MESG); 		//消息类型
				String repMsgTyp = dzipProcessTemplate.getResMsgTyp(msgTyp);
				if(Constants.PE_NULL.equals(repMsgTyp))
					paramsMap.put(Constants.FIX8583_MESG,msgTyp);						//返回消息类型
				else
					paramsMap.put(Constants.FIX8583_MESG,repMsgTyp);					//返回消息类型
				paramsMap.put(Constants.FIX8583_RESP, Constants.PE_CHANN_INVALIDATE);
				paramsMap.put(Constants.FIX8583_ADDI, "请求的功能尚不支持");
			}
			
		}
		else{
			try {
				String msgTyp = (String)paramsMap.get(Constants.FIX8583_MESG); 		//消息类型
				String repMsgTyp = dzipProcessTemplate.getResMsgTyp(msgTyp);
				if(Constants.PE_NULL.equals(repMsgTyp))
					paramsMap.put(Constants.FIX8583_MESG,msgTyp);						//返回消息类型
				else
					paramsMap.put(Constants.FIX8583_MESG,repMsgTyp);					//返回消息类型
				paramsMap.put(Constants.FIX8583_RESP, Constants.PE_ERROR);
				paramsMap.put(Constants.FIX8583_CANL, "核心主机不可用");
				dzipProcessTemplate.updateHostStat(Constants.PE_ZERO);//将平台库表中的主机状态置为： 签退 （0）
				formatedMessage = fixedLength8583Processor.format(paramsMap, element);// 组返回报文
			} catch (Exception e) {
				paramsMap.put(Constants.FIX8583_RESP, Constants.PE_ERROR);
				paramsMap.put(Constants.FIX8583_CANL, "DataBase error");
			}  
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
