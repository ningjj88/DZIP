package csii.pe.service.comm.tcp.queue;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.csii.pe.service.comm.CommunicationException;
import com.csii.pe.service.comm.Transport;
import com.csii.pe.transform.Formatter;
import com.csii.pe.transform.Parser;
import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactoryInterface;

public class QueueTransformTransport implements Transport {
	private TransformerFactoryInterface transformerFactoryInterface;
	private String formatName = "FormatOutbound";
	private String parseName = "ParseOutbound";
	protected Transport transport;
	private boolean debug = false;
	private String dumpPath = "/tmp";
	private Formatter headFormatter;
	private Formatter streamFormatter;
	private Parser streamParser;
	private Parser afterParser;
	private long jdField_int = 0L;
	private static ThreadLocal threadLocal = new ThreadLocal() {
		protected Object initialValue() {
			return new ArrayList();
		}
	};

	public static Map getInputData() {
		List localList = (List) threadLocal.get();
		int i = localList.size();
		return ((i == 0) ? null : (Map) localList.get(i - 1));
	}

	public static void setInputData(Map paramMap) {
		List localList = (List) threadLocal.get();
		localList.add(paramMap);
	}

	public static void unsetInputData() {
		List localList = (List) threadLocal.get();
		if (localList.size() <= 0)
			return;
		localList.remove(localList.size() - 1);
	}

	public Object submit(Object paramObject) throws CommunicationException {
		setInputData((Map) paramObject);
		try {
			Transformer localTransformer1 = this.transformerFactoryInterface.getTransformer(this.formatName);
			if (this.headFormatter != null)
				paramObject = this.headFormatter.format(paramObject, (Map) paramObject);
			Object localObject1 = localTransformer1.format(paramObject, (Map) paramObject);
			if (this.streamFormatter != null)
				localObject1 = this.streamFormatter.format(localObject1, (Map) paramObject);
			long l = this.jdField_int++;
			if (this.debug)
				try {
					FileOutputStream localFileOutputStream1 = new FileOutputStream(
							this.dumpPath + '/' + this.formatName
									+ l + new Time(System.currentTimeMillis()).toString().replace(':', '_'));
					localFileOutputStream1.write((byte[]) localObject1);
					localFileOutputStream1.flush();
					localFileOutputStream1.close();
				} catch (Exception localException1) {
					localException1.printStackTrace();
				}

			Object localObject2 = this.transport.submit(localObject1);
			localObject2 = getResult(localObject2);
			if (this.debug)
				try {
					FileOutputStream localFileOutputStream2 = new FileOutputStream(
							this.dumpPath + '/' + this.parseName
									+ l + new Time(System.currentTimeMillis()).toString().replace(':', '_'));
					localFileOutputStream2.write((byte[]) localObject2);
					localFileOutputStream2.flush();
					localFileOutputStream2.close();
				} catch (Exception localException2) {
					localException2.printStackTrace();
				}
			if (this.streamParser != null)
				localObject2 = this.streamParser.parse(localObject2, (Map) paramObject);
			ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream((byte[]) localObject2);
			Transformer localTransformer2 = this.transformerFactoryInterface.getTransformer(this.parseName);
			Object localObject3;
			Object localObject5;
			if (this.debug) {
				if (this.afterParser != null) {
					localObject3 = localTransformer2.parse(localByteArrayInputStream, (Map) paramObject);
					localObject3 = this.afterParser.parse(localObject3, (Map) paramObject);
					System.err.println("result:" + localObject3);
					localObject5 = localObject3;
					return localObject5;
				}
				localObject3 = localTransformer2.parse(localByteArrayInputStream, (Map) paramObject);
				System.err.println("result:" + localObject3);
				localObject5 = localObject3;
				return localObject5;
			}
			if (this.afterParser != null) {
				localObject3 = localTransformer2.parse(localByteArrayInputStream, (Map) paramObject);
				localObject5 = this.afterParser.parse(localObject3, (Map) paramObject);
				return localObject5;
			}
			localObject5 = localTransformer2.parse(localByteArrayInputStream, (Map) paramObject);
			return localObject5;
		} catch (TransformException localTransformException) {
			
		} finally {
			unsetInputData();
		}
		return paramObject;
	}

	protected Object getResult(Object paramObject) throws CommunicationException {
		return paramObject;
	}

	public void setTransformerFactory(TransformerFactoryInterface paramTransformerFactoryInterface) {
		this.transformerFactoryInterface = paramTransformerFactoryInterface;
	}

	public void setTransport(Transport paramTransport) {
		this.transport = paramTransport;
	}

	public void setFormatName(String paramString) {
		this.formatName = paramString;
	}

	public void setParseName(String paramString) {
		this.parseName = paramString;
	}

	public void setDebug(boolean paramBoolean) {
		this.debug = paramBoolean;
	}

	public void setDumpPath(String paramString) {
		this.dumpPath = paramString;
	}

	public void setHeadFormatter(Formatter paramFormatter) {
		this.headFormatter = paramFormatter;
	}

	public void setStreamFormatter(Formatter paramFormatter) {
		this.streamFormatter = paramFormatter;
	}

	public void setStreamParser(Parser paramParser) {
		this.streamParser = paramParser;
	}

	public void setAfterParser(Parser paramParser) {
		this.afterParser = paramParser;
	}
}