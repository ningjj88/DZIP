package csii.pe.service.comm.tcp.queue;

import com.csii.pe.service.comm.CommunicationException;
import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.stream.VariableCounter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

public class QueueTransport extends AbstractQueueTransport implements
		InitializingBean {

	protected Log log = LogFactory.getLog(super.getClass());
	private int headLength;
	private int offsetOfLengthField;
	private String type;
	private String encoding;
	private boolean headIncluded = true;
	private int lengthAdjust = 0;
	private VariableCounter variableCounter;
	private int timeout;

	protected Object readStream(InputStream paramInputStream) throws CommunicationException, IOException {
		byte[] arrayOfByte1 = new byte[this.headLength];
		int i = 0;
		while (i < this.headLength) {
			int j = paramInputStream.read(arrayOfByte1, i, this.headLength - i);
			if (j < 0)
				throw new CommunicationException("invalid_packet_head");
			i += j;
		}
		ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(
				arrayOfByte1, getOffsetOfLengthField(), this.headLength - getOffsetOfLengthField());
		try {
			if (this.variableCounter == null)
				try {
					afterPropertiesSet();
				} catch (Exception localException) {
					throw new CommunicationException("can't create variable counter", localException);
				}
			Integer localInteger = (Integer) this.variableCounter.parse(localByteArrayInputStream, null);
			int k;
			if (this.headIncluded)
				k = localInteger.intValue();
			else
				k = localInteger.intValue() + this.headLength;
			k += this.lengthAdjust;
			byte[] arrayOfByte2 = new byte[k];
			System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, this.headLength);
			int l = this.headLength;
			while (l < k) {
				int i1 = paramInputStream.read(arrayOfByte2, l, k - l);
				if (i1 >= 0) {
					l += i1;
				} else {
					this.log.error("the length of packet should be :" + k + " but encounter eof at offset:" + l);
					throw new CommunicationException("invalid_packet_data");
				}
			}
			return arrayOfByte2;
		} catch (TransformException localTransformException) {
			throw new CommunicationException(localTransformException.getMessageKey());
		}
	}

	public String getEncoding() {
		return this.encoding;
	}

	public int getHeadLength() {
		return this.headLength;
	}

	public int getOffsetOfLengthField() {
		return this.offsetOfLengthField;
	}

	public String getType() {
		return this.type;
	}

	public void setEncoding(String paramString) {
		this.encoding = paramString;
	}

	public void setHeadLength(int paramInt) {
		this.headLength = paramInt;
	}

	public void setOffsetOfLengthField(int paramInt) {
		this.offsetOfLengthField = paramInt;
	}

	public void setType(String paramString) {
		this.type = paramString;
	}

	public void afterPropertiesSet() throws Exception {
		this.variableCounter = new VariableCounter();
		this.variableCounter.setName("QueueTranport");
		this.variableCounter.setType(getType());
		this.variableCounter.setEncoding(getEncoding());
	}

	public boolean isHeadIncluded() {
		return this.headIncluded;
	}

	public void setHeadIncluded(boolean paramBoolean) {
		this.headIncluded = paramBoolean;
	}

	public void setLengthAdjust(int paramInt) {
		this.lengthAdjust = paramInt;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}