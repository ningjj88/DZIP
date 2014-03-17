/**
 * 单元测试类的基类，用于ISO8583格式报文的收发工作
 */
package csii.test;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Element;

import com.csii.pe.service.comm.Transport;
import com.csii.pe.service.comm.tcp.TcpTransport;

import csii.base.action.util.FileUtils;
import csii.base.constant.Constants;
import csii.dzip.core.FixedLength8583Processor;

/**
 * @author chenshq
 * @version 1.0.0
 * @since 2010-8-16
 */
public abstract class  Abstract4TestCUPSToHost {

	private static String HOST_IP = "127.0.0.1";
	private static int HOST_PORT = 28001;
	public static int count = 4;
	private static final String TESTMESSAGE = "1446940027100200622135901010003440601000000000000500006100536495317660536490610    061060110210206         01006753   01000000   6221359010100034406=000020187599292=                                                                                                         650021622550        65002111000000511722001CHN6753自动柜员机                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       15657D1672518E4C0B2                                                        00000000010000                                                                                         04296510                                                           20060610531766087452000100009999999900000000                                                        00000000010000531766                                                                                087452                                                                                              2263DDDFD7440B12000100009400271087654321";

	public Transport getTransport() {
		TcpTransport transport = new TcpTransport();
	    transport.setHost(HOST_IP);
		transport.setPort(HOST_PORT);
		transport.setHeadLength(4);
		transport.setHeadIncluded(false);
		transport.setOffsetOfLengthField(0);
		transport.setType("LLLL");
		transport.setTimeout(1000*15);
		return transport;
	}

	@SuppressWarnings("unchecked")
	public void send(Map map) throws Exception {
		FixedLength8583Processor parser = new FixedLength8583Processor();
		Element element = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
		String formatedMessage = parser.format(map, element);
		System.err.println("formatedMessage length =====>" + formatedMessage.length());
		System.err.println("formatedMessage =====>" + new String(formatedMessage.getBytes(Constants.CHARSET_ISO_8859_1),Constants.CHARSET_GBK));

		byte[] sendBuffer = formatedMessage.getBytes(Constants.CHARSET_ISO_8859_1);
		System.err.println("sendBuffer length =====>" + sendBuffer.length);
		System.err.println("client send length =====>" + formatedMessage.length());
		System.err.println("client send =====>" + new String(sendBuffer,Constants.CHARSET_GBK));
		Transport transport = getTransport();
		DateFormat current = DateFormat.getDateTimeInstance();
		DateFormat current1 = DateFormat.getDateTimeInstance();
		Date now = new Date();
		System.out.println(current.format(now) + ": start");
		byte[] rcvBuffer = (byte[]) transport.submit(sendBuffer);
		Date now1 = new Date();
		System.out.println(current1.format(now1) + ": end" );
		System.err.println("client recv length =====>" + rcvBuffer.length);
		String reciveMessage = new String(rcvBuffer,Constants.CHARSET_ISO_8859_1);
		System.err.println("client recv =====>" +new String(reciveMessage.getBytes(Constants.CHARSET_ISO_8859_1),Constants.CHARSET_GBK));
		parseFixMessage(reciveMessage,Constants.FIXED_LENGTH_8583);
	}
	public static Map<String, String> parseFixMessage(String respMsg, String fileName) throws Exception {
		Map<String, String> cxtMap = new HashMap<String, String>();
		String name;
		String Msg = null;
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
			System.out.println(current.format(now) + ": " + name + "=[" + value + "]");
			cxtMap.put(name, value);
			count = count + fieldLength;
		}
		return cxtMap;
	}

	public Abstract4TestCUPSToHost() {
		super();
	}
	public void test() throws Exception {
		Map map = prepare();
		// map.put("_TransactionTimestamp", Util.getCurrentTimestamp());
		this.send(map);
	}
	public static void main(String[] args)throws Exception{
		Abstract4TestFixedLength8583ToHost test = new Abstract4TestFixedLength8583ToHost();
		test.send(null);
	}
	protected abstract Map prepare();
	
	
}
