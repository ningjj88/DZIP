package csii.pe.service.comm.tcp.queue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public abstract class AbstractQueueStream {

	protected Log log = LogFactory.getLog(super.getClass());

	private String name = null;
	private String keyName = null;
	private List headTxTypeList = null;
	private String handerName = null;
	protected BlockingQueue queueIn = null;
	protected BlockingQueue queueOut = null;
	private int queueSize=100;
	private boolean isAlive;
	private int headTxTypeOffset;
	private int headTxTypeLen;
	public InputStream getInputStream() {
		byte[] object = null;
		try {
			object = (byte[]) queueIn.take();
			//String HeadTxType = new String(object, 4+46, 4);
			String HeadTxType = new String(object, headTxTypeOffset, headTxTypeLen);   //在报文中取出消息类型
			String bankId = new String(object, 4, 8);   	//如果是定长包取出银行编号
			String msgLength = new String(object, 0, 4);   	//在报文中取出报文长度
			if("1446".equals(msgLength)&&"94002710".equals(bankId)){	//定长包的
				HeadTxType = new String(object, 12, 4);   	//在报文中取出消息类型
			}
			setKeyName(null);
			if(headTxTypeList != null || headTxTypeList.size() > 0){
				if(headTxTypeList.contains(HeadTxType)){
					setKeyName(handerName);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(object);

	}

	public OutputStream getOutputStream() {
		ByteArrayOutputStream byteArrayOutputStream;
		byteArrayOutputStream = new ByteArrayOutputStream();
		// try {
		// queueOut.put(byteArrayOutputStream);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		if (queueOut.offer(byteArrayOutputStream) == false) {
			System.out.println("queueOut.offer error!Data=" + byteArrayOutputStream.toByteArray().toString());
		}
		return byteArrayOutputStream;
	}

	public synchronized Object getQueueClient() {
		Object object1 = null;
		try {
			object1 = queueOut.peek();
			if (object1 != null && ((ByteArrayOutputStream) object1).size() > -1) {   //必须是大于-1
				object1 = queueOut.poll(1, TimeUnit.SECONDS);
			} else {
				object1 = null;
				Thread.sleep(5);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return object1;
	}

	public abstract boolean setQueueServer(Object o);

	public abstract Object QueueSubmit(Object o);

	public Object getQueue() {
		return queueIn.poll();
	}

	public boolean setQueue(Object o) {
		return queueOut.offer(o);
	}

	public Object getQueueTransport(String KeyName) {
		return queueIn.poll();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public List getHeadTxTypeList() {
		return headTxTypeList;
	}

	public void setHeadTxTypeList(List headTxTypeList) {
		this.headTxTypeList = headTxTypeList;
	}

	public String getHanderName() {
		return handerName;
	}

	public void setHanderName(String handerName) {
		this.handerName = handerName;
	}

	public int getHeadTxTypeOffset() {
		return headTxTypeOffset;
	}

	public void setHeadTxTypeOffset(int headTxTypeOffset) {
		this.headTxTypeOffset = headTxTypeOffset;
	}

	public int getHeadTxTypeLen() {
		return headTxTypeLen;
	}

	public void setHeadTxTypeLen(int headTxTypeLen) {
		this.headTxTypeLen = headTxTypeLen;
	}

}
