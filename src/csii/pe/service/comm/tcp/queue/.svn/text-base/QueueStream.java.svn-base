package csii.pe.service.comm.tcp.queue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.csii.pe.service.comm.CommunicationException;
import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.Transformer;
import com.csii.pe.transform.TransformerFactory;

import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

public class QueueStream extends AbstractQueueStream{

	private boolean debug = true;
	private String path = "/tmp";
	private String defaultParser = "";
	private String name = null;
	
	public QueueStream(){
		queueIn = new LinkedBlockingQueue();
		queueOut = new LinkedBlockingQueue();
//		mapClient = new HashMap();
//		mapClientTime = new HashMap();
	}
	
//	private boolean putClientQueue(Object o, Map mapparam) {
//		String TraceNum = null;
//		String LastTime = null;
//		long CurTime;
//		TraceNum = (String) mapparam.get("bit11");
//		LastTime = (String) mapClientTime.get(TraceNum);
//		if (LastTime == null)
//			return false;
//		CurTime = new Long(LastTime).longValue();
//		CurTime = System.currentTimeMillis() - CurTime;
//		// System.out.println("CurTime=" + CurTime);
//		if (CurTime > 60 * 1000)
//			return false;
//		mapClient.put(TraceNum, o);
//		return true;
//	}

	public boolean setQueueServer(Object object) {
//		String RejectCode = new String((byte[]) object, 45, 5);
		log.info("queueStream name: " + name);
		log.info("Thread_ID: " + Thread.currentThread().getId() + "  Thread_Name: " + Thread.currentThread().getName());
		return queueIn.offer(object);
	}

	public Object QueueSubmit(final Object paramObject) {
		ByteArrayOutputStream queueout = new ByteArrayOutputStream();
		try {
			queueout.write((byte[]) paramObject);
			queueout.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			queueout.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		queueOut.offer(queueout);
		Object retObject =null;
		return retObject;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private Map defaultParser(Object paramObject, Map mapParam) throws CommunicationException {
		Object localObject5 = null;
		// setInputData((Map)paramObject);
		try {
			ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream((byte[]) paramObject);
			TransformerFactory tf = new com.csii.pe.transform.TransformerFactory();
			tf.setPath(path);
			tf.setDebug(debug);
			Transformer localTransformer2 = tf.getTransformer(defaultParser);
			localObject5 = localTransformer2.parse(localByteArrayInputStream, (Map) mapParam);
			if (this.debug) {
				System.err.println("result:" + localObject5);
			}
		} catch (TransformException localTransformException) {
			localTransformException.printStackTrace();
		} finally {
			// unsetInputData();
		}
		return (Map) localObject5;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setDefaultParser(String defaultParser) {
		this.defaultParser = defaultParser;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHeadTxTypeList(List headTxTypeList) {
		super.setHeadTxTypeList(headTxTypeList);
	}

	public void setKeyName(String keyName) {
		super.setKeyName(keyName);
	}
	
}
