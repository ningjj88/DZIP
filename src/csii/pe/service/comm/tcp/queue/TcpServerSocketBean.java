package csii.pe.service.comm.tcp.queue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.common.socket.ServerSocketFactoryProvider;
import com.csii.pe.common.socket.ServerSocketFactoryProviderAware;

public class TcpServerSocketBean implements ServerSocketFactoryProviderAware {
	protected Log log = LogFactory.getLog(super.getClass());
	private String host;
	// private int port;
	private int backlog = 50;
	private int timeout = 0;
	private boolean clientAuth = false;
	private boolean clientMode = false;
	private boolean reuseAddress = false;
	private ServerSocketFactory serverSocketFactoryProvider = ServerSocketFactory.getDefault();
	public static boolean m = false;

	protected ServerSocket createServerSocket(int port) throws IOException {
		InetAddress localInetAddress = null;
		if ((this.host != null) && (this.host.length() > 0))
			localInetAddress = InetAddress.getByName(this.host);
		ServerSocket localServerSocket = this.serverSocketFactoryProvider
				.createServerSocket(port, this.backlog, localInetAddress);  //创建服务套接字
		localServerSocket.setReuseAddress(isReuseAddress());
		localServerSocket.setSoTimeout(this.timeout * 1000);
		if (localServerSocket instanceof SSLServerSocket) {
			SSLServerSocket localSSLServerSocket = (SSLServerSocket) localServerSocket;
			localSSLServerSocket.setUseClientMode(this.clientMode);   //控制接受 的连接是以SSL的 服务模式还是在SSL客户端模式工作
			if (!(this.clientMode))
				localSSLServerSocket.setNeedClientAuth(this.clientAuth);  //控制accept服务器模式的SSLSockets是否将在开始是配置为请求客户端验证
		}
		return localServerSocket;    //返回套接字对象
	}

	public String toString(ServerSocket serverSocket) {
		return "(" + (((this.host == null) || (this.host.length() == 0)) ? "*" : this.host) + ":" + serverSocket.getLocalPort() + ")";
	}

	public boolean isReuseAddress() {
		return this.reuseAddress;
	}

	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}
	
	public void setHost(String paramString) {
		this.host = paramString;
	}

	//
	// public void setPort(int port)
	// {
	// this.port = port;
	// }

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setClientAuth(boolean clientAuth) {
		this.clientAuth = clientAuth;
	}

	public void setClientMode(boolean clientMode) {
		this.clientMode = clientMode;
	}

	public void setServerSocketFactoryProvider(ServerSocketFactoryProvider paramServerSocketFactoryProvider) {
		this.serverSocketFactoryProvider = paramServerSocketFactoryProvider.getServerSocketFactory();
	}
	
}
