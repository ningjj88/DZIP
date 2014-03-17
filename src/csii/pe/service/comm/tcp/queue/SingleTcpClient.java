package csii.pe.service.comm.tcp.queue;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.service.comm.Server;

public class SingleTcpClient extends TcpSocketBean implements Server {
	protected Log log = LogFactory.getLog(super.getClass());
	protected Socket savedSocket = null;
	private long idleTime = 4000L;
	private String idleString = null;
	private boolean uibs = false;
	private boolean a;
	private boolean alive = false;
	private String atmpServerPort;
	private List localSocketList;
	private Socket localSocket;
	
	private QueueStream queueStream = null;

	// private int countPort;
	private String logname = "";
	private String name = null;
	private String queueStreamType;

	public String getQueueStreamType() {
		return queueStreamType;
	}

	public void setQueueStreamType(String queueStreamType) {
		this.queueStreamType = queueStreamType;
	}

	public void setShutDownOutput(boolean paramBoolean) {
		this.a = paramBoolean;
		if (!(paramBoolean))
			return;
		setKeepAlive(false);
		this.log.warn("keepAlive force to false due to shutDownOutput is true");
	}

	public final void start() {
		if (isAlive())
			return;
		this.alive = true;
		if (queueStream == null)
			queueStream = BeanQueueStreamLoader.getSpecialQueueStream(queueStreamType); 
		TcpClient tcpClient = new TcpClient(Integer.valueOf(atmpServerPort));
		Thread localThread = new Thread(tcpClient, toString() + "-main");
		localThread.start();
	}

	class TcpClientCheck extends Thread {
		private Socket localSocketThread;

		public TcpClientCheck(Socket localSocketThread) {
			this.localSocketThread = localSocketThread;
		}

		public void run() {
			while (true) {
				if (localSocketThread == null || localSocketThread.isClosed()) {
					break;
				} else {
					try {
						if (localSocketThread.isConnected()) {
							int i=localSocketThread.getInputStream().read();
							if (i < 0)
							{
								localSocketThread.close();
								localSocketThread=null;
								break;
							}
						}
					} catch (SocketTimeoutException se) {
					} catch (IOException e) {
						try {
							localSocketThread.close();
							localSocketThread=null;
							break;
						} catch (IOException e1) {
						}
//						System.err.println("网路连接失败");
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}
	}

	class TcpClient implements Runnable {
		private Socket localSocketThread = null;
		private int portThread;
		private long currentTime = 0L;
		private TcpClientCheck tcpClientCheck = null;
		private Object paramObject = null;

		public TcpClient(int port) {
			portThread = port;
		}

		private synchronized Socket createSocket(int port) {
			// QueueTcpClient.this.setPort(port);
			try {
				// log.info("Connect Remote Port "+port+" ...");
				localSocketThread = SingleTcpClient.this.createSocket(port);
			} catch (IOException e) {
				// log.info("Connect Remote port " + port +" TimeOut!");
				// e.printStackTrace();

			}
			return localSocketThread;
		}

		public synchronized void run() {

			while ((isAlive())) {
				if (localSocketThread == null || localSocketThread.isClosed()) {
					if (localSocketThread != null)
						try {
							log.error("Connect Remote Port " + portThread + " break!");
							SingleTcpClient.this.destroySocket(localSocketThread);
							localSocketThread = null;
						} catch (IOException e) {
							e.printStackTrace();
						}

					localSocketThread = createSocket(portThread);

					if (localSocketThread != null && localSocketThread.isConnected()) {
						if (tcpClientCheck != null)
							tcpClientCheck.stop();
						tcpClientCheck = new TcpClientCheck(localSocketThread);
						tcpClientCheck.start();
						log.info(localSocketThread.getRemoteSocketAddress().toString().split("/")[1] + " Connect Success");
					}
					if (localSocketThread == null) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				} else {
								
					if(!localSocketThread.isConnected()){
						
						try {
							localSocketThread.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						localSocketThread=null;
					}
					setLocalSocket(localSocketThread);
				}
			}
		}
	}

	public synchronized void restart() {
		shutdown();
		start();
	}

	public synchronized void shutdown() {
		if (!(isAlive()))
			return;
		try {
			this.alive = false;
			Iterator localIterator = localSocketList.iterator();
			while (localIterator.hasNext()) {
				localSocket = (Socket) localIterator.next();
				destroySocket(localSocket);
				this.localSocket = null;
			}
			localSocketList.removeAll(localSocketList);
			localSocketList = null;

		} catch (IOException localIOException) {
			this.log.error("close server socket error.", localIOException);
		}
	}
	
	public String getLogname() {
		return logname;
	}

	public void setLogname(String logname) {
		this.logname = logname;
	}

	public void setIdleTime(long paramLong) {
		this.idleTime = paramLong;
	}

	public boolean isAlive() {
		return this.alive;
	}

	public void setUibs(boolean paramBoolean) {
		this.uibs = paramBoolean;
	}

	public String getPortList() {
		return atmpServerPort;
	}

	public void setPortList(String portList) {
		this.atmpServerPort = portList;
	}

	public void setIdleString(String idleString) {
		this.idleString = idleString;
	}

	public Socket getLocalSocket() {
		return localSocket;
	}

	public void setLocalSocket(Socket localSocket) {
		this.localSocket = localSocket;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
