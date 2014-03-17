package csii.pe.service.comm.tcp.queue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.service.comm.CommunicationException;
import com.csii.pe.service.comm.Transport;

public abstract class AbstractQueueTransport implements Transport {

	protected Log log = LogFactory.getLog(super.getClass());

	private long idleTime = 4000L;
	private boolean uibs = false;
	private int timeOut;

	public final Object submit(Object paramObject)throws CommunicationException {
		QueueStream localQueueStream =null;
		// QueueStreamFactory.getQueueStream_ATM();///////////////-YQ
		try {
			// localQueueStream.setTimeOut(this.getTimeOut());
			Object Object1 = localQueueStream.QueueSubmit(paramObject);
			Object Object2 = null;
			Object2 = readStream(new ByteArrayInputStream((byte[]) Object1));
			return Object2;
		} catch (Exception localException2) {
			this.log.error(super.getClass().getName() + ".submit", localException2);
			if (localException2 instanceof CommunicationException)
				throw ((CommunicationException) localException2);
			if (this.uibs)
				throw new CommunicationException("pe.submit_failed", localException2);
			if (!localQueueStream.isAlive())
				throw new CommunicationException("pe.submit_failed ", localException2);
			throw new CommunicationException(super.getClass().getName() + ".submit", localException2);
		}
	}

	protected abstract Object readStream(InputStream paramInputStream) throws CommunicationException, IOException;

	public void setIdleTime(long idleTime) {
		this.idleTime = idleTime;
	}

	public void setUibs(boolean uibs) {
		this.uibs = uibs;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
}
