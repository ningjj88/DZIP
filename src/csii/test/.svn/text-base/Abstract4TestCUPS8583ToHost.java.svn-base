/**
 * 单元测试类的基类，用于ISO8583格式报文的收发工作
 */
package csii.test;

import java.io.ByteArrayInputStream;
import java.util.Map;

import com.csii.pe.service.comm.Transport;
import com.csii.pe.service.comm.tcp.TcpTransport;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactory;
import com.csii.pe.transform.TransformerFactoryInterface;

import csii.base.constant.Constants;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-6-10
 */
public abstract class Abstract4TestCUPS8583ToHost {

	private static String HOST_IP = "127.0.0.1";
	private static int HOST_PORT = 28001;
	private static String PATH = "/packets/dzip/fromCUPS";
	private static String TAG_CLASS_MAPPING_PATH = PATH + "/xmltagmapping.properties";
	private static String TAG_ALIAS_MAPPING_PATH= PATH + "/xmlaliasmapping.properties";

	private String sessionId = "";

	public TransformerFactoryInterface getTransformerFactory() throws Exception {
		TransformerFactory tf = new com.csii.pe.transform.TransformerFactory();
		tf.setPath(PATH);
		tf.setDebug(true);
//		Map pmap = new HashMap();
//		XmlStreamParser xmlParser = new com.csii.pe.transform.stream.xml.XmlStreamParser();
//		xmlParser.setDebug(true);
//		xmlParser.setTagClassMapping(TAG_CLASS_MAPPING_PATH);
//		xmlParser.setTagAliasMapping(TAG_ALIAS_MAPPING_PATH);
//		xmlParser.afterPropertiesSet();
//		pmap.put("xmlPacketParser", xmlParser);
//		tf.setParsers(pmap);
		return tf;
	}

	public Transport getTransport() {
		TcpTransport transport = new TcpTransport();
		transport.setHost(HOST_IP);
		transport.setPort(HOST_PORT);
		transport.setTimeout(10000 * 100);
		transport.setHeadLength(4);
		transport.setHeadIncluded(false);
		transport.setOffsetOfLengthField(0);
		// read the end flag
		// transport.setLengthAdjust(1);
		transport.setType("LLLL");
		transport.setEncoding("UTF-8");
		return transport;
	}

	@SuppressWarnings("unchecked")
	public void send(Map map) throws Exception {
		TransformerFactoryInterface tf = getTransformerFactory();
		Transformer f = tf.getTransformer("OutboundPacket");
		map.put("_SessionId", sessionId);
//		int num = 8;
		byte[] sndBuffer = (byte[]) f.format(map, null);
//		byte[] head = new byte[num];
//		byte[] bitMap = new byte[num];
//		byte[] body = new byte[sndBuffer.length - 16];
//		for (int i = 0; i < num; i++) {
//			head[i] = sndBuffer[i];
//		}
//		for (int i = 0; i < num; i++) {
//			bitMap[i] = sndBuffer[i + 8];
//		}
//		for (int i = 0; i < sndBuffer.length - 16; i++) {
//			body[i] = sndBuffer[i + 16];
//		}
//		StringBuffer sb = new StringBuffer();
//		for (int j = 0; j < bitMap.length; j++) {
//			sb.append(toHexString(bitMap[j]));
//		}
//		String temp="0200E23844C1A8E098000000000000000040196221359010100444092300000061808441878454608441806186011021020608000422100800012210366221359010100444092=000020166536401000244081816807010006000129000000000_CHNGDGZSGRGBANKING_                    156D9D2299E9791BE8020000000000000000";
//		System.err.println("send =====>" + new String(head, "GB18030")
//				+ sb.toString() + new String(body, "GB18030"));
		System.err.println("sndBuffer =====>" +new String(sndBuffer,"GB18030"));
		Transport transport = getTransport();
		byte[] rcvBuffer = (byte[]) transport.submit(sndBuffer);
		System.err.println(map.get(Constants.ISO8583_TRANSACTION_ID));
		System.err.println("recv =====>" +new String(rcvBuffer,"GB18030"));
		Transformer p = tf.getTransformer("OutboundPacketRes");
		Map result = (Map) p.parse(new ByteArrayInputStream(rcvBuffer), map);
		System.err.println("result:" + result);
		sessionId = (String) result.get("_SessionId");
	}

	public static String toHexString(byte b) {
		String s = Integer.toHexString(b & 0xff);
		if (s.length() == 1) {
			return "0" + s;
		} else {
			return s;
		}
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

	@SuppressWarnings("unchecked")
	public void test() throws Exception {
		Map map = prepare();
		// map.put("_TransactionTimestamp", Util.getCurrentTimestamp());
		this.send(map);
	}

	@SuppressWarnings("unchecked")
	protected abstract Map prepare();

	public Abstract4TestCUPS8583ToHost() {
		super();
	}

}
