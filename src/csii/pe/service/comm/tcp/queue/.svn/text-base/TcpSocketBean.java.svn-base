package csii.pe.service.comm.tcp.queue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.common.socket.SocketFactoryProvider;
import com.csii.pe.common.socket.SocketFactoryProviderAware;

public class TcpSocketBean implements SocketFactoryProviderAware {
	
	protected Log log = LogFactory.getLog(super.getClass());
	
	private String host;
	// private int port;
	private String localHost;
	private int localPort;
	private int receiveBufferSize = 8192;
	private int sendBufferSize = 8192;
	private int timeout = 0;
	private int linger = -1;
	private boolean isKeepAlive = false;
	private boolean noDelay = false;
	private boolean reuseAddress = false;
	private boolean clientAuth = false;
	private boolean clientMode = true;
	private int connectRetryCounter = 10;
	private SocketFactoryProvider socketFactoryProvider;
	private SocketFactory socketFactory = null;

	protected Socket createSocket(int port) throws IOException {
		InetAddress localInetAddress = null;
		if ((this.localHost != null) && (this.localHost.length() > 0))
			localInetAddress = InetAddress.getByName(this.localHost);
		if (this.socketFactory == null)
			if (this.socketFactoryProvider == null)
				this.socketFactory = SocketFactory.getDefault();
			else
				this.socketFactory = this.socketFactoryProvider.getSocketFactory();
		Socket localSocket = null;
		int i = this.connectRetryCounter;
		try {
			localSocket = (localInetAddress == null) ? this.socketFactory.createSocket(this.host, port) : this.socketFactory.createSocket(this.host, port, localInetAddress, this.localPort);
		} catch (SocketException localSocketException) {
			if (i > 0)
				--i;
			throw localSocketException;
		}
		localSocket.setReceiveBufferSize(this.receiveBufferSize);
		localSocket.setSendBufferSize(this.sendBufferSize);
		localSocket.setSoTimeout(this.timeout);
		if (this.linger != -1)
			localSocket.setSoLinger(true, this.linger);
		localSocket.setKeepAlive(this.isKeepAlive);
		localSocket.setTcpNoDelay(this.noDelay);
		if (localSocket instanceof SSLSocket) {
			SSLSocket localSSLSocket = (SSLSocket) localSocket;
			localSSLSocket.setUseClientMode(this.clientMode);
			if (!(this.clientMode))
				localSSLSocket.setNeedClientAuth(this.clientAuth);
		}
		return localSocket;
	}

	protected void destroySocket(Socket paramSocket) throws IOException {
		paramSocket.close();
	}

	public String toString(Socket paramSocket) {
		return "(" + this.host + ":" + paramSocket.getPort() + (((this.localHost == null) || (this.localHost.length() == 0)) ? ""
						: new StringBuffer("/").append(this.localHost).append(":").append(this.localPort).toString()) + ")";
	}
	
	public void setHost(String paramString) {
		this.host = paramString;
	}

	// public void setPort(int port)
	// {
	// //this.port = port;
	// }

	public void setLocalHost(String localHost) {
		this.localHost = localHost;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}

	public int getReceiveBufferSize() {
		return this.receiveBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public int getSendBufferSize() {
		return this.sendBufferSize;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public boolean isKeepAlive() {
		return this.isKeepAlive;
	}

	public void setKeepAlive(boolean isKeepAlive) {
		this.isKeepAlive = isKeepAlive;
	}

	public void setNoDelay(boolean noDelay) {
		this.noDelay = noDelay;
	}

	public void setLinger(int linger) {
		this.linger = linger;
	}

	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}

	public void setClientAuth(boolean clientAuth) {
		this.clientAuth = clientAuth;
	}

	public void setClientMode(boolean clientMode) {
		this.clientMode = clientMode;
	}

	public void setSocketFactoryProvider(SocketFactoryProvider socketFactoryProvider) {
		this.socketFactoryProvider = socketFactoryProvider;
	}

	public int getConnectRetryCounter() {
		return this.connectRetryCounter;
	}

	public void setConnectRetryCounter(int connectRetryCounter) {
		this.connectRetryCounter = connectRetryCounter;
	} 

}
