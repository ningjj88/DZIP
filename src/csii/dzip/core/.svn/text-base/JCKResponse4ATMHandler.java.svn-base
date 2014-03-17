/**
 * ATM ISO8583
 */
package csii.dzip.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;

import com.csii.pe.channel.stream.tcp.DefaultHandler;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.Channel;
import com.csii.pe.transform.TransformException;

import csii.base.action.util.FileUtils;
import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.DzipDataProcessor;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.UpdateJoural;
import csii.pe.service.comm.tcp.queue.SingleTcpClient;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-6-10
 */
public class JCKResponse4ATMHandler extends DefaultHandler {

	private boolean debug;

	public static final String INVALID_PARAMSMAP = "invalidParamsMap";


	private DzipProcessTemplate dzipProcessTemplate;

	private DzipDataProcessor dataProcessor;
	
	private ActionUtilProcessor utilProcessor;
	
	private FixedLength8583Processor fixedLength8583Processor;

	private SingleTcpClient singleTcpClient;
	
	private UpdateJoural updateJoural;

	@SuppressWarnings("unchecked")
	protected Object parseStream(Channel paramChannel, byte[] paramArrayOfByte)
			throws TransformException {
		Map parsedMap = null;
		try {
			String formatedStr = new String(paramArrayOfByte,
					Constants.CHARSET_ISO_8859_1);
			log.info("recv Length =====>" + formatedStr.length());
			log.info("recv =====>" +new String(paramArrayOfByte, Constants.CHARSET_GBK));
			Element e = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
			parsedMap = fixedLength8583Processor.parse(formatedStr, e);
			String msgType = parsedMap.get(Constants.FIX8583_MESG).toString();
			String proc = parsedMap.get(Constants.FIX8583_PROC).toString();
			StringBuffer transactionId = new StringBuffer(msgType);
			transactionId.append(proc.substring(0, 2));
			if(Constants.PE_06.equals( parsedMap.get(Constants.FIX8583_POSC))||Constants.PE_60.equals( parsedMap.get(Constants.FIX8583_POSC)))//06:预授权; 60：追加预授权;
				transactionId.append(parsedMap.get(Constants.FIX8583_POSC));
			parsedMap.put(Constants.TransactionId, transactionId.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return parsedMap;
	}

	@SuppressWarnings("unchecked")
	protected byte[] handleInternal(Channel paramChannel, Object paramObject) {
		log.info("=======================>handleInternal is Excuting");
		Map paramsMap = (Map) paramObject;
		log.info("recv message paramMap ===============>" + paramsMap);
		Map outputMap = new HashMap(); // 传出参数Map
		String transactionId = getIdentityResolver().getIdentity(paramsMap);
		String sessionID = null; // 获得SessionID
		String outtranid = null;
		String responcd=null;
		Context context = createContext(transactionId, paramsMap, sessionID, getSessionManager(), paramChannel);
		String formatName = resolveFormatName(context);   // 返回报文格式定义文件名
		String errorFormatName = resolveFormatName(context); // 错误报文格式定义文件名
		try {
			String peJournalNO =context.getData(Constants.FIX8583_TRAC).toString();  //转发报文中的系统跟踪号是平台流水号。		
			context.setData(Constants.PE_JOURNAL_NO,peJournalNO);					 //平台流水号
			String traceNum = dzipProcessTemplate.queryTraceNum(peJournalNO);     	 //通过平台流水号查询ATM发送的系统跟踪号。
			context.setData(Constants.RTXNCATCD,Constants.RTXNCATCD_02);			 //业务性质 ；本代他
			context.setData(Constants.FIX8583_TRAC,traceNum);						 //将系统跟踪号还原
			context.setData(Constants.PE_REQ_CHANN, Constants.PE_ATMP);  			 //通信渠道
			context.setData(Constants.IN_AVAILMETHCD, Constants.AVAILMETHCD);        // 填充可用方式 	
			context.setData(Constants.PRI_SOURCE, Constants.PRI_SOURCE_JCK_VALUE);   //填充交易来源前缀
			if(Constants.TRANCD_021030.equals(paramsMap.get(Constants.TransactionId))){
				responcd=Constants.PE_OK;
			}else{
				responcd =dzipProcessTemplate.getSysTranInfo(context,Constants.FIX8583); //获得系统交易参数信息
				if(Constants.PE_SYSTEM_ERROR.equals(responcd)){
					context.setData(Constants.FIX8583_RESP, responcd);
					throw new PeException("系统错误");
				}
			}
			context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.FIX8583_RESP));   //银联返回的响应码
			if(Constants.PE_NULL.equals(String.valueOf(context.getData(Constants.FIX8583_AMT)).trim())  //防止交易金额取空，发生异常
					|| context.getData(Constants.FIX8583_AMT) ==null)                                          
				context.setData(Constants.FIX8583_AMT, Constants.TRANAMT_DEFAULT);				//交易金额
				BigDecimal amt = Util.getAmount(String.valueOf(context.getData(Constants.FIX8583_AMT))); 
				amt = amt.movePointLeft(2);
				context.setData(Constants.PE_TRAN_AMT, amt);// 填充交易金额
			paramsMap.put(Constants.FIX8583_TRAC,traceNum);							 //将系统跟踪号还原
//			if(paramsMap.get(Constants.FIX8583_ORIG)!=null 
//					&& !Constants.PE_NULL.equals(paramsMap.get(Constants.FIX8583_ORIG))){
//				String origData =(String)paramsMap.get(Constants.FIX8583_ORIG);
//				traceNum = origData.substring(4,10);
//				String origTracNum =utilProcessor. getOriSysTraNum(context);
//				paramsMap.put(Constants.FIX8583_ORIG,origData.replace(traceNum, origTracNum));
//				context.setData(Constants.FIX8583_ORIG,origData.replace(traceNum, origTracNum));
//			}
//			context.setDataMap(paramsMap); 
			if(Constants.PE_OK.equals(responcd)){
				log.info("==================>begin excute : " + context.getTransactionId());
				getCoreController().execute(context); // 关联本地Action 进行数据库等操作
				log.info("==================>excute : " + context.getTransactionId() + " ok!");
			}else{
				context.setData(Constants.FIX8583_RESP, responcd);
				throw new PeException("不能交易当前响应吗为"+responcd);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			String message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
			if (e.getMessage().contains(Errors.SYSTEM_UNDEFINED_TRANSACTION))
				message = Errors.SYSTEM_UNDEFINED_TRANSACTION;
			if(Constants.PE_OK.equals(context.getData(Constants.FIX8583_RESP)))
		        context.setData(Constants.FIX8583_RESP,Constants.PE_30);
			context.setData(Constants.PE_HOST_RESP_CD,context.getData(Constants.FIX8583_RESP));
			return exceptionProcessor(errorFormatName, outtranid, context,message);// 异常处理
		}
		if (context.getState() == -1)
			return null;
		outputMap = FixedLength8583Processor.convert8583(context.getDataMap(), Constants.FIX8583);// 定长8583——>ISO8583的转换
		outputMap.put(Constants.TransactionId, context.getData(Constants.TransactionId));
		log.info("outputMap==================>" + outputMap);
		try {
			log.info("==================>return response message!");
			byte[] responMessage = formatStream(outputMap, formatName, context);
			log.info("发送到ATMP数据["+ new String(responMessage)+"]");
			Socket localSocket = getSingleTcpClient().getLocalSocket();
			if(localSocket.isConnected()){
				log.info("==================>ATMP主机：" +localSocket.getInetAddress() +"， 端口：  "+localSocket.getPort());
				localSocket.getOutputStream().write(responMessage);
				localSocket.getOutputStream().flush();
				log.info("==================>数据发送成功");
			}
			return null; // 返回结果报文
		}catch (IOException e) {
			log.error("=============>发送ATMP数据失败" );
			context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_92);
			updateJoural.executeRespond(context);
		}
		catch (TransformException e) {
			log.error("=============>发送ATMP数据组包失败" );
			context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_30);
			updateJoural.executeRespond(context);
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
		Map outputMap = null;
		// context.setData(Constants.ISO8583_PRO_CODE, outtranid); //
		// 将交易码恢复成外部交易码
		//context.setData(Constants.ISO8583_ADDDATA, message); // 将交易码恢复成外部交易码
		outputMap = context.getDataMap();
		try {
			context.setData(Constants.PE_TRANS_STAT, Constants.PE_EIGHT); // 交易状态
			updateJoural.execute(context);
			outputMap = FixedLength8583Processor.convert8583(context.getDataMap(), Constants.FIX8583);// 定长8583——>ISO8583的转换
			outputMap.put(Constants.TransactionId, context.getData(Constants.TransactionId));
			log.info("outputMap==================>"+outputMap);
			byte[] responMessage = formatStream(outputMap, formatName, context);// 返回错误报文
			Socket localSocket = getSingleTcpClient().getLocalSocket();
			if (localSocket.isConnected()) {
				log.info("==================>ATMP主机：" +localSocket.getInetAddress() +"， 端口：  "+localSocket.getPort());
				localSocket.getOutputStream().write(responMessage);
				localSocket.getOutputStream().flush();
				log.info("==================>数据发送成功");
			}
			return null; // 返回结果报文
		}catch (IOException e) {
			log.error("=============>发送ATMP数据失败" );
			context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_92);
			updateJoural.executeRespond(context);
		}
		catch (TransformException e) {
			log.error("=============>发送ATMP数据组包失败" );
			context.setData(Constants.PE_HOST_RESP_CD,Constants.PE_30);
			updateJoural.executeRespond(context);
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

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}


	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}

	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}

	public DzipDataProcessor getDataProcessor() {
		return dataProcessor;
	}

	public void setDataProcessor(DzipDataProcessor dataProcessor) {
		this.dataProcessor = dataProcessor;
	}

	public FixedLength8583Processor getFixedLength8583Processor() {
		return fixedLength8583Processor;
	}

	public void setFixedLength8583Processor(
			FixedLength8583Processor fixedLength8583Processor) {
		this.fixedLength8583Processor = fixedLength8583Processor;
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
/**
 * 
 * @param b 需要被转换的数据
 * @return 转换后的结果
 */
	public static String byte2Hex(byte[] b) {
		StringBuffer strBuffer = new StringBuffer();
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
		String s = "";
		for (int i = 0; i < pin.length; i++) {
			s += Integer.toHexString(pin[i]);
		}
		return s;
	}


	public static String padLeft(int num, String str) {
		String result = str;
		while (num > result.length()) {
			result = "0" + result;
		}
		return result;

	}
}
