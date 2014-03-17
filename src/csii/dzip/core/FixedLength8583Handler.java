/**
 * 
 */
package csii.dzip.core;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.dom4j.Element;

import com.csii.pe.channel.stream.tcp.DefaultHandler;
import com.csii.pe.service.comm.Channel;
import com.csii.pe.transform.TransformException;

import csii.base.action.util.FileUtils;
import csii.base.constant.Constants;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-7-2
 */
public class FixedLength8583Handler extends DefaultHandler{
	
//	private static final String TESTMESSAGE = "1446940027100200622135901010003440601000000000000500006100536495317660536490610    061060110210206         01006753   01000000   6221359010100034406=000020187599292=                                                                                                         650021622550        65002111000000511722001CHN6753自动柜员机                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       15657D1672518E4C0B2                                                        00000000010000                                                                                         04296510                                                           20060610531766087452000100009999999900000000                                                        00000000010000531766                                                                                087452                                                                                              2263DDDFD7440B12000100009400271087654321";
	
	private FixedLength8583Processor fixedLength8583Processor;
	
	@SuppressWarnings("unchecked")
	protected Object parseStream(Channel paramChannel, byte[] paramArrayOfByte) throws TransformException {
		Map parsedMap = null;
		try {
			String formatedStr = new String(paramArrayOfByte,Constants.CHARSET_ISO_8859_1);
			System.err.println("recv message length =====>" + formatedStr.length());
			System.err.println("recv message =====>" +  new String(paramArrayOfByte,Constants.CHARSET_GBK));
			Element e = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);
			parsedMap = fixedLength8583Processor.parse(formatedStr, e);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return parsedMap;
	}

	@SuppressWarnings("unchecked")
	protected byte[] handleInternal(Channel paramChannel, Object paramObject) {
		Element element = FileUtils.getRootElement(Constants.FIXED_LENGTH_8583);

		Map paramMap=(Map)paramObject;
		System.err.println("recv message paramMap =====>" +paramMap);
		paramMap.put(Constants.FIX8583_MESG, Constants.MSG_TYPE_0210);
		paramMap.put(Constants.FIX8583_RESP, Constants.PE_OK);
		paramMap.put(Constants.FIX8583_CANL, "Successful!");
		paramMap.put(Constants.FIX8583_RETU, "00000001");
		String formatedMessage = null;
		try {
			formatedMessage = fixedLength8583Processor.format(paramMap, element);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		byte[] sendBuffer = null;
		try {
			formatedMessage = new String(formatedMessage.getBytes(Constants.CHARSET_GBK),Constants.CHARSET_ISO_8859_1);
			sendBuffer = formatedMessage.getBytes(Constants.CHARSET_ISO_8859_1);
			formatedMessage = new String(formatedMessage.getBytes(Constants.CHARSET_ISO_8859_1),Constants.CHARSET_GBK);
			System.err.println("send message length =====>" + formatedMessage.length());
			System.err.println("send message =====>" + new String(formatedMessage.getBytes(Constants.CHARSET_ISO_8859_1),Constants.CHARSET_GBK));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sendBuffer;
	}

	public FixedLength8583Processor getFixedLength8583Processor() {
		return fixedLength8583Processor;
	}

	public void setfixedLength8583Processor(FixedLength8583Processor fixedLength8583Processor) {
		this.fixedLength8583Processor = fixedLength8583Processor;
	}
	
}
