/**
 * 单元测试类的基类，用于ISO8583格式报文的收发工作
 */
package csii.test;

import java.util.Map;

import org.dom4j.Element;

import com.csii.pe.service.comm.Transport;
import com.csii.pe.service.comm.tcp.TcpTransport;

import csii.base.action.util.FileUtils;
import csii.base.constant.Constants;
import csii.dzip.core.FixedLength8583Processor;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-6-10
 */
public  class Abstract4TestFixedLength8583ToHost {

	private static String HOST_IP = "127.0.0.1";
	private static int HOST_PORT = 20029;
	
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
		String formatedMessage = parser.format(parser.testMap(), element);
//		String formatedMessage = realMessage;
		byte[] sendBuffer = formatedMessage.getBytes("iso-8859-1");
		System.err.println("sendBuffer length =====>" + sendBuffer.length);
		System.err.println("client send length =====>" + formatedMessage.length());
		System.err.println("client send =====>" + formatedMessage);
		Transport transport = getTransport();
		byte[] rcvBuffer = (byte[]) transport.submit(sendBuffer);
		System.err.println("client recv length =====>" + rcvBuffer.length);
		System.err.println("client recv =====>" + new String(rcvBuffer));
	}

	public Abstract4TestFixedLength8583ToHost() {
		super();
	}
	
	public static void main(String[] args)throws Exception{
		Abstract4TestFixedLength8583ToHost test = new Abstract4TestFixedLength8583ToHost();
		test.send(null);
	}

}
