/**
 * 单元测试类的基类，用于XML格式报文的收发工作
 */
package csii.test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import com.csii.pe.service.comm.Transport;
import com.csii.pe.service.comm.tcp.SimpleXMLTcpTransport;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactoryInterface;
import com.csii.pe.transform.XmlTransformerFactory;
import com.csii.pe.transform.stream.xml.XmlStreamParser;

import csii.base.action.util.Util;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-6-21
 */
public abstract class Abstract4TestCounterXMLToHost {

	private static String HOST_IP = "127.0.0.1";
	private static int HOST_PORT = 19999;
	private static int TIMEOUT = 1000 * 150;
	private static String PATH = "/packets/dzip/fromcounter";
	private static String TAG_CLASS_MAPPING_PATH = PATH + "/xmltagmapping.properties";
	private static String TAG_ALIAS_MAPPING_PATH= PATH + "/xmlaliasmapping.properties";

	private String sessionId = "";

	@SuppressWarnings("unchecked")
	public TransformerFactoryInterface getTransformerFactory() throws Exception {
		XmlTransformerFactory tf = new com.csii.pe.transform.XmlTransformerFactory();
		tf.setPath(PATH);
		tf.setDebug(true);
		Map pmap = new HashMap();
		XmlStreamParser xmlParser = new com.csii.pe.transform.stream.xml.XmlStreamParser();
		xmlParser.setDebug(true);
		xmlParser.setTagClassMapping(TAG_CLASS_MAPPING_PATH);
		xmlParser.setTagAliasMapping(TAG_ALIAS_MAPPING_PATH);
		xmlParser.afterPropertiesSet();
		pmap.put("xmlPacketParser", xmlParser);
		tf.setParsers(pmap);
		return tf;
	}

	public Transport getTransport() {
		SimpleXMLTcpTransport transport = new SimpleXMLTcpTransport();
		transport.setHost(HOST_IP);
		transport.setPort(HOST_PORT);
		transport.setTimeout(TIMEOUT);
		transport.setEndTagName("ROOT");
		return transport;
	}

	@SuppressWarnings("unchecked")
	public void send(Map map) throws Exception {
		TransformerFactoryInterface tf = getTransformerFactory();
		Transformer f = tf.getTransformer("OutboundPacket");
		map.put("_SessionId", sessionId);
		byte[] sndBuffer = (byte[]) f.format(map, map);
		System.err.println("send =====>" + new String(sndBuffer, "UTF-8"));
		Transport transport = getTransport();
		byte[] rcvBuffer = (byte[]) transport.submit(sndBuffer);
		System.err.println("recv =====>" + new String(rcvBuffer, "UTF-8"));
		Transformer p = tf.getTransformer("xmlPacketParser");
		Map result = (Map) p.parse(new ByteArrayInputStream(rcvBuffer), null);
		System.err.println("result:" + result);
		sessionId = (String) result.get("_SessionId");
	}

	@SuppressWarnings("unchecked")
	public void test() throws Exception {
		Map map = prepare();
		//map.put("_TransactionTimestamp", Util.getCurrentTimestamp());
		this.send(map);
	}

	@SuppressWarnings("unchecked")
	protected abstract Map prepare();

	public Abstract4TestCounterXMLToHost() {
		super();
	}
	
}
