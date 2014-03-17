/**
 *
 */
package csii.dzip.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactory;
import com.csii.pe.transform.TransformerFactoryInterface;

import csii.base.action.util.ISO8583MsgLogUtils;
import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.util.ActionUtilProcessor;
import csii.dzip.action.util.Init;
import csii.pe.service.comm.tcp.queue.BeanQueueStreamLoader;
import csii.pe.service.comm.tcp.queue.LockUtil;

/**
 * @author CHENSHQ
 * @version 1.0.0
 * @since 2010-8-10
 */
public class DzipToCUPSTransport{

	private int timeout;
	private int port;
	private String host;
	private String path;
	private FixedLength8583Processor fixedLength8583Processor;
	private ActionUtilProcessor  utilProcessor;

	protected final Log logger = LogFactory.getLog(getClass());
	@SuppressWarnings("unchecked")
	public Object submit(Object content) throws PeException {
		logger.info("====================>Here is Sending  to CUPS!");
		Context context = (Context)content;
		try{
			String transactionId=context.getString(Constants.ISO8583_TRANSACTION_ID);
			//如果是无卡存款时把处理码转换为210000发给银联
			if(Constants.TRANCD_020022.equals(transactionId)){
				context.setData(Constants.ISO8583_PRO_CODE,Constants.PROCODE_210000);
			}
			//如果是本带他ATM脚本处理结果交易并且F4为空,就需要转换交易ID
			if(Constants.MSG_TYPE_0620.equals(transactionId)){
//				if(context.getData(Constants.PE_TRAN_AMT)==null
//						||Double.valueOf(context.getString(Constants.PE_TRAN_AMT))==0){
				if(Util.getAmount(context.getString(Constants.PE_TRAN_AMT)).signum()==0){
					//本带他ATM脚本处理结果交易(原始交易没有F4)tranAmt
					context.setData(Constants.ISO8583_TRANSACTION_ID,Constants.TRANCD_0621);
				}
			}
			context.setData(Constants.IN_PIN,"");//去掉防止打印明文密码
			TransformerFactoryInterface tf = getTransformerFactory();
			Transformer f = tf.getTransformer("OutboundPacket");
			Map outputMap=context.getDataMap();
			byte[] sndBuffer = (byte[]) f.format(outputMap, null);
			if(Init.isTransactionFromOnli(context)){
				ISO8583MsgLogUtils.logFormat(outputMap,"转柜面报文报文到银联", logger);
				logger.info("The length of message :"+ sndBuffer.length);
				logger.info("Data to CUPS Host:["+ new String(sndBuffer)+"]");
				String key="TraceNo="+context.getData(Constants.ISO8583_SYSTRACENUM);
				Map replyMap=LockUtil.execute(new LockUtil(key,timeout,sndBuffer){
					public void doLockWaitAction(Object sndBuffer) throws Exception {
						BeanQueueStreamLoader.getSpecialQueueStream("cupsIso").QueueSubmit(sndBuffer);
					}
				});
				context.setDataMap(replyMap);
			}else{
				ISO8583MsgLogUtils.logFormat(outputMap,"转发ATMP端报文到银联", logger);
				logger.info("The length of message :"+ sndBuffer.length);
				logger.info("Data to CUPS Host:["+ new String(sndBuffer)+"]");
				BeanQueueStreamLoader.getSpecialQueueStream("cupsIso").QueueSubmit(sndBuffer);
			}
		}
		catch (TransformException e) {
			context.setData(Constants.ISO8583_RESCODE,Constants.PE_30);
			throw new PeException("发送银联前置组包失败");
		} catch (Exception e) {
			context.setData(Constants.ISO8583_RESCODE,Constants.PE_30);
			throw new PeException("发送银联前置失败");
		}
		return context;                                //返回响应报文
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
		return baos.toString();
	}
	/**
	 *
	 * @return
	 * @throws Exception
	 */
	public TransformerFactoryInterface getTransformerFactory() throws Exception {
		TransformerFactory tf = new com.csii.pe.transform.TransformerFactory();
		tf.setPath(path);
		tf.setDebug(false);
		return tf;
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
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ActionUtilProcessor getUtilProcessor() {
		return utilProcessor;
	}

	public void setUtilProcessor(ActionUtilProcessor utilProcessor) {
		this.utilProcessor = utilProcessor;
	}

	public FixedLength8583Processor getFixedLength8583Processor() {
		return fixedLength8583Processor;
	}

	public void setFixedLength8583Processor(
			FixedLength8583Processor fixedLength8583Processor) {
		this.fixedLength8583Processor = fixedLength8583Processor;
	}
}
