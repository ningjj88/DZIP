/**
 * 
 */
package csii.dzip.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import csii.pe.service.comm.tcp.queue.BeanQueueStreamLoader;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.FileUtils;
import csii.base.constant.Constants;
import csii.base.constant.TranName;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.pe.service.comm.tcp.queue.SingleTcpClient;

/**
 * @author CHENSHQ
 * @version 1.0.0
 * @since 2010-8-10
 */
public class DzipJinCheng2HostTransport{
	
	private int timeout ;
	private int port ;

	private String host ;
	private SingleTcpClient singleTcpClient;
	private FixedLength8583Processor fixedLength8583Processor;
	private ActionUtilProcessor  utilProcessor;
	protected final Log logger = LogFactory.getLog(getClass());

	@SuppressWarnings("unchecked")
	public Object submit(Object content) throws Exception{
		logger.info("====================>Here is Sending  to JINCHENG ");
		Context context = (Context)content;
		Map map = (Map)context.getData(Constants.MAP_FIX_8583);
//		String bankId = "94002710";
//		String pan = map.get(Constants.FIX8583_PAN).toString();
//		String pin = map.get(Constants.FIX8583_PIN).toString();
//		map.put(Constants.FIX8583_PIN,JNative4CapDes.getCap_enc_pin(bankId, pin, pan));//锦城卡加密算法
		String formatedMessage = null;
		if (TranName.dzipATM_Deposit_Code.equals(context.getData(Constants.ISO8583_TRANSACTION_ID))
				|| TranName.dzipATM_Deposit.equals(context.getTransactionId())) {
			context.setData(Constants.IN_RTXNSOURCECD,context.getData(Constants.PRI_SOURCE)+"ATM");
			context.setData(Constants.IN_FUNDTYPCD, Constants.TRS_FUND_TYP_CASH);   // 资金类型
			context.setData(Constants.IN_RTXNSTATCD,Constants.RTXNSTAT_C);			//交易状态,正常、冲正：'C'	抹帐:'EC'
			context.setData(Constants.IN_REVFLAG, Constants.PE_ZERO);				//冲正标志:0：正常   1：冲正 	2：抹帐
//			utilProcessor.deductTranAMT(context);									//执行数据库操作
			if (!Constants.PE_OK.equals(context.getData(Constants.FIX8583_RESP))){ //如果核心记账失败直接返回交易失败
				throw new PeException("本代他存款内部账失败！");
			}
		}
		try{
			Element element = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
			formatedMessage = fixedLength8583Processor.format(map, element);   //组定长包
			logger.info("Send Message ========>" + new String(formatedMessage.getBytes(Constants.CHARSET_ISO_8859_1),Constants.CHARSET_GBK));
			parseFixMessage(formatedMessage,Constants.FIXED_LENGTH_8583);
			byte[] sendBuffer = formatedMessage.getBytes(Constants.CHARSET_ISO_8859_1);
			logger.info("The length of message :"+ sendBuffer.length);
			logger.info("Data to JCK Host:["+ new String(sendBuffer)+"]");
			BeanQueueStreamLoader.getSpecialQueueStream("jincheng").QueueSubmit(sendBuffer);
		}catch(UnsupportedEncodingException e){	
			context.setData(Constants.ISO8583_RESCODE,Constants.PE_30);
			throw new PeException("与锦城卡组包失败");
		}catch(Exception e){	
			context.setData(Constants.ISO8583_RESCODE,Constants.PE_92);
			throw new PeException("与锦城卡前置通信失败");
		}
		return null;
	}
	
	/**
	 * 读取输入流并转换为字符串
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String inputStream2String(InputStream is)throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while((i = is.read()) != -1){
			baos.write(i);
		}
		String resp= baos.toString();
		return resp;
		
	}
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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
	
	public Map<String, String> parseFixMessage(String respMsg, String fileName) throws Exception {
		Map<String, String> cxtMap = new HashMap<String, String>();
		int count = 4;
		String name;
		String value = Constants.PE_NULL;
		@SuppressWarnings("unused")
		int fieldLength, msgLength;
//		try {
//			Msg = new String(readTxt(respMsg).getBytes(GBK_CHARSET), DEFAULT_CHARSET);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		msgLength = Integer.valueOf(respMsg.substring(0, count));

		Element root = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
		Date now = new Date();
		DateFormat current = DateFormat.getDateTimeInstance();
		for (Iterator it = root.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();
			name = (String) element.getName();
			fieldLength = Integer.parseInt(element.attributeValue(Constants.LENGTH));
			value = respMsg.substring(count, count + fieldLength);
			value = new String(value.getBytes(Constants.CHARSET_ISO_8859_1),Constants.CHARSET_GBK).trim();
			logger.info(current.format(now) + ": " + name + "=[" + value + "]");
			cxtMap.put(name, value);
			count = count + fieldLength;
		}
		return cxtMap;
	}
}
