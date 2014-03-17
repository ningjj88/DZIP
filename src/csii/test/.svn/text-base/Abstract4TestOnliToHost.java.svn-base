package csii.test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.comm.Transport;
import com.csii.pe.service.comm.tcp.SimpleXMLTcpTransport;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactoryInterface;
import com.csii.pe.transform.XmlTransformerFactory;
import com.csii.pe.transform.stream.xml.XmlStreamParser;

import csii.base.action.util.Util;
import csii.dzip.action.DzipBaseAction;
/**
 * 单元测试类的基类，用于XML格式报文的发送
 * @author xiehai
 * @date 2013-12-31 下午03:14:41
 */
public abstract class Abstract4TestOnliToHost{

	private static String HOST_IP = "192.168.20.221";
	private static int HOST_PORT = 20001;
	private static int TIMEOUT = 1000 * 300;
	private static String PATH = "/packets/dzip/fromonli";
	private static String TAG_CLASS_MAPPING_PATH = PATH + "/xmltagmapping.properties";
	private static String TAG_ALIAS_MAPPING_PATH= PATH + "/xmlaliasmapping.properties";

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
		transport.setEndTagName("Message");
		return transport;
	}

	@SuppressWarnings("unchecked")
	public void send(Map map) throws Exception {
		TransformerFactoryInterface tf = getTransformerFactory();
		Transformer f = tf.getTransformer("OutboundPacket");
		byte[] sndBuffer = null;
		try {
			sndBuffer = (byte[]) f.format(map, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("send =====>" + new String(sndBuffer, "UTF-8"));
		Transport transport = getTransport();
		System.err.println("transport =====>" + transport.toString());
		byte[] rcvBuffer = {};
		try {
			rcvBuffer = (byte[]) transport.submit(sndBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("recv =====>" + new String(rcvBuffer, "UTF-8"));
		Transformer p = tf.getTransformer("xmlPacketParser");
		Map result = (Map) p.parse(new ByteArrayInputStream(rcvBuffer), null);
//		System.err.println("result:" + result);
	}

	@SuppressWarnings("unchecked")
	public void test() throws Exception {
		Map map = prepare();
		map.put("IBSTrsTimestamp", Util.getCurrentTimestamp());
		this.send(map);
	}

	@SuppressWarnings("unchecked")
	protected abstract Map prepare();

	public Abstract4TestOnliToHost() {
		super();
	}
}
