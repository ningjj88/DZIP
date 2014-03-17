package csii.pe.service.comm.tcp.queue;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.service.comm.AbstractChannel;

public class QueueChannel extends AbstractChannel {

	private Socket localSocket;
	private static Log log = LogFactory.getLog(QueueChannel.class);
	protected String type = "req";

	public QueueChannel(QueueStream paramQueueStream) throws IOException {
		super(paramQueueStream.getInputStream(), paramQueueStream.getOutputStream());
		String keyName = paramQueueStream.getKeyName();
		if(keyName != null){
			setType(keyName);
		}
//		initAttributes(paramQueueStream);
	}

	protected void initAttributes(QueueStream paramQueueStream) {
//		setAttribute("channel.source", localSocket.getInetAddress());
//		setAttribute("tcpchannel.ip", localSocket.getInetAddress().getHostAddress());
//		setAttribute("tcpchannel.localport", new Integer(localSocket.getLocalPort()));
//		setAttribute("tcpchannel.port", new Integer(localSocket.getPort()));
//		if (!(localSocket instanceof SSLSocket))
//			return;
//		setAttribute("tcpchannel.ssl", "true");
//		SSLSocket localSSLSocket = (SSLSocket) localSocket;
//		SSLSession localSSLSession = localSSLSocket.getSession();
//		if (localSSLSession == null)
//			return;
//		try {
//			X509Certificate[] arrayOfX509Certificate = localSSLSession.getPeerCertificateChain();
//			if (arrayOfX509Certificate == null)
//				return;
//			setAttribute("javax.net.ssl.peer_certificates", arrayOfX509Certificate);
//		} catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException) {
//			log.error(localSocket, localSSLPeerUnverifiedException);
//		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
