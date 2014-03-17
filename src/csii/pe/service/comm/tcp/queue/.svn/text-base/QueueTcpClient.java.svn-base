package csii.pe.service.comm.tcp.queue;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.service.comm.Server;
public class QueueTcpClient extends TcpSocketBean implements Server {
	protected Log log = LogFactory.getLog(super.getClass());
	protected Socket savedSocket = null;
	private long idleTime = 4000L;
	private String idleString=null;
	private boolean uibs = false;
	private boolean a;
	private boolean alive = false;
	private String portList;
	private Socket localSocket;
	private int countPort;
	private String logname = "";
	private String name = null;
	private String queueStreamType;
	private TcpClient[] tcpClient;
	
	
	private QueueStream queueStream = null;

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
		if(queueStream == null)
			queueStream = BeanQueueStreamLoader.getSpecialQueueStream(queueStreamType); 
		countPort = 0;
		int[] intPort = new int[10];
		String[] str = portList.split(",");
		for (int i = 0; i < str.length; i++) {
			intPort[countPort] = Integer.valueOf(str[i]).intValue();
			countPort++;
		}
		tcpClient = new TcpClient[countPort];
		for (int i = 0; i < countPort; i++) {
			tcpClient[i] = new TcpClient(intPort[i]);
			tcpClient[i].setAlive(true);
			Thread localThread = new Thread(tcpClient[i], toString() + "-main");
			localThread.start();
		}
	}

	class TcpClientCheck implements  Runnable {
		
		private Socket localSocketThread;
		private boolean alive = false;

		public boolean isAlive() {
			return alive;
		}
		public void setAlive(boolean alive) {
			this.alive = alive;
		}
		public TcpClientCheck(Socket localSocketThread) {
			this.localSocketThread = localSocketThread;
		}
		public void run() {
			while (true) {
				if (localSocketThread == null || localSocketThread.isClosed()) {
					break;
				} else {
					try {
						if(localSocketThread.isConnected())
						{
							int i=localSocketThread.getInputStream().read();
							if (i < 0)
							{
								localSocketThread.close();
								localSocketThread=null;
								break;
							}
						}
					}catch(SocketTimeoutException se)
					{
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
						Thread.sleep(5000);
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
		private TcpClientCheck tcpClientCheck=null;
		private byte[] paramByte=null;
		private boolean alive = false;

		public boolean isAlive() {
			return alive;
		}

		public void setAlive(boolean alive) {
			this.alive = alive;
		}

		public TcpClient(int port) {
			portThread = port;
		}

		private synchronized Socket createSocket(int port) {
			// QueueTcpClient.this.setPort(port);
			try {
				// log.info("Connect Remote Port "+port+" ...");
				localSocketThread = QueueTcpClient.this.createSocket(port);
			} catch (IOException e) {
				// log.info("Connect Remote port " + port +" TimeOut!");
				// e.printStackTrace();
			}
			return localSocketThread;
		}

		public synchronized void run() {

			while ((isAlive())) {
				if (localSocketThread == null || localSocketThread.isClosed()) {
					if (localSocketThread != null){
						try {
							System.err.println("localSocketThread  isConnected 1 : "+localSocketThread.isConnected());
							System.err.println("localSocketThread  isClosed 1 : "+localSocketThread.isClosed());
							log.info("Remote Host "+ localSocketThread.getRemoteSocketAddress().toString().split("/")[1] + " break!");
							QueueTcpClient.this.destroySocket(localSocketThread);
							localSocketThread = null;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					localSocketThread = createSocket(portThread);
					if (localSocketThread != null && localSocketThread.isConnected()) {
						if (tcpClientCheck != null)
							tcpClientCheck.setAlive(false);
						tcpClientCheck = new TcpClientCheck(localSocketThread);
						tcpClientCheck.setAlive(true);
						Thread localThread= new Thread(tcpClientCheck,toString()+"tcpClientCheck");
						localThread.start();
						if (idleTime != 0)
							currentTime = 0;  //System.currentTimeMillis();
						log.info(localSocketThread.getRemoteSocketAddress().toString().split("/")[1] + " Connect Success");
					}
					if (localSocketThread == null) {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}else{
					if(localSocketThread.isOutputShutdown()){
						localSocketThread = null;
						System.err.println("localSocketThread.isOutputShutdown() : "+localSocketThread.isOutputShutdown());
						continue;
					}
					if(idleTime != 0 && System.currentTimeMillis()-currentTime > idleTime){
						currentTime=System.currentTimeMillis();
						paramByte=idleString.getBytes();
					}else{
						Object paramObject=queueStream.getQueueClient();
						if(paramObject==null){
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							continue;
						}
						paramByte = ((ByteArrayOutputStream) paramObject).toByteArray();
					}
					if(paramByte.length <= 0){
						continue;
					}
					try {
//						System.err.println("localSocketThread  isConnected 2 : "+localSocketThread.isConnected());
						log.info("Send to " + localSocketThread.getRemoteSocketAddress().toString().split("/")[1]);
						localSocketThread.getOutputStream().write(paramByte);
						localSocketThread.getOutputStream().flush();
						continue;
					} catch (Exception localException1) {
						try {
							localSocketThread.close();
						} catch (IOException e) {
							log.error(e);
						}
						localSocketThread=null;
						localException1.printStackTrace();
					}
				}
			}
			if (tcpClientCheck!= null)
				tcpClientCheck.setAlive(false);
		}
	}

	public synchronized void restart() {
		shutdown();
		start();
		log.info("tcpClient restart");
	}

	public synchronized void shutdown() {
		if (!(isAlive()))
			return;
		this.alive = false;
		for(int i=0;i<countPort;i++)
		{
			tcpClient[i].setAlive(false);
			tcpClient[i] = null;
		}
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
		return portList;
	}

	public void setPortList(String portList) {
		this.portList = portList;
	}

	public void setIdleString(String idleString) {
		this.idleString = idleString;
	}
	
	public String getLogname() {
		return logname;
	}

	public void setLogname(String logname) {
		this.logname = logname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQueueStreamType() {
		return queueStreamType;
	}

	public void setQueueStreamType(String queueStreamType) {
		this.queueStreamType = queueStreamType;
	}

}
