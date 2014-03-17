package csii.pe.service.comm.tcp.queue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;

import com.csii.pe.service.comm.CommunicationException;
import com.csii.pe.service.comm.Handler;
import com.csii.pe.service.comm.Server;
import com.csii.pe.service.event.EventDispatcher;
import com.csii.pe.service.event.support.EventAgent;
import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.stream.VariableCounter;

public class QueueTcpServer extends TcpServerSocketBean implements Server {
	
	private String name;
	private String queueStreamType;
	private int socketTimeout = 30;
	private int socketLinger = -1;

	private ServerSocket[] serverSocketList;
	private int[] intPort;
	private int serverSocketCount = 0;
	private Handler handler;
	private EventAgent eventAgent;
	private int eventId = 20000;
	private boolean alive = false;
	private String id = "QueueTcpServer";
	private Timer postEventTimer;
	private long delay = 0L;
	private String portList;

	private int headLength;
	private int offsetOfLengthField;
	private String type;
	private String encoding;
	private boolean headIncluded = true;
	private int lengthAdjust = 0;
	private VariableCounter variableCounter;

	private boolean debug = false;
	private String path;
	private String defaultParser;
	private String logname;
	private TcpServer[] tcpServer;
	private QueueTcpClient queueTcpClient;
	
	public void setQueueTcpClient(QueueTcpClient queueTcpClient) {
		this.queueTcpClient = queueTcpClient;
	}

	private QueueStream queueStream = null;

	@SuppressWarnings("unchecked")
	/**
	 * 
	 */
	protected void postEvent(ServerSocket serverSocket) {
		if (!(isAlive()))
			return;
		HashMap localHashMap = new HashMap();
		localHashMap.put("Id", this.id);
		if (serverSocket.getInetAddress().toString().equals("0.0.0.0/0.0.0.0"))
			localHashMap.put("Ip", com.csii.pe.common.util.CsiiUtils.getLocalAddresses()[0]);
		else
			localHashMap.put("Ip", serverSocket.getInetAddress().getHostAddress());
		localHashMap.put("Port", new Integer(serverSocket.getLocalPort()));
		this.eventAgent.post(this.eventId, localHashMap);
		if (!(this.log.isDebugEnabled()))
			return;
		this.log.debug("postEvent:" + localHashMap);
	}

	/**
	 * 
	 */
	public synchronized void start() {
		if (isAlive())
			return;
		this.alive = true;
//		 if (this.serverSocket != null) {
//		 this.log.info(this + " has already started.");
//		 return;
//		 }
		if (queueStream == null)
			queueStream = BeanQueueStreamLoader.getSpecialQueueStream(queueStreamType); 
		
		queueStream.setPath(path);
		queueStream.setDefaultParser(defaultParser);
		queueStream.setDebug(debug);
		serverSocketCount = 0;
		intPort = new int[10];
		String[] str = portList.split(",");//获取端口序列
		for (int i = 0; i < str.length; i++) {
			intPort[serverSocketCount] = Integer.valueOf(str[i]).intValue();
			serverSocketCount++;          //获得端口号个数
		}
		serverSocketList = new ServerSocket[serverSocketCount]; //初始化服务器端数组
		tcpServer = new TcpServer[serverSocketCount];
		for (int i = 0; i < serverSocketCount; i++) {			//循环建立服务端套接字.
			try {
				this.serverSocketList[i] = createServerSocket(intPort[i]);
			} catch (IOException localIOException) {
				this.log.error("could not create server socket.", localIOException);
				this.alive = false;
				return;
			}
			tcpServer[i] = new TcpServer(this.serverSocketList[i]);
			tcpServer[i].setAlive(true);
			Thread localThread = new Thread(tcpServer[i], toString() + "-main");
			this.log.info("QueueTcpServer Started... " + name);
			this.log.info("QueueTcpServer Started... " + "Thread_ID: " + localThread.getId() + "  Thread_Name: " + localThread.getName()); 
			localThread.start();
		}
		new TcpServerCheck().start();
	}

	class TcpServerCheck extends  Thread{		
		public void run() {	
			while(true){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
				for(int i=0;i<serverSocketCount;i++){		
					if (!tcpServer[i].isAlive()){
						for(int j=0;j<serverSocketCount;j++){							
								tcpServer[j].setAlive(false);
								tcpServer[j]=new TcpServer(serverSocketList[j]);
								tcpServer[j].setAlive(true);
								tcpServer[j].start();
						}
						if(queueTcpClient != null)
							queueTcpClient.restart();
						log.info("tcpServer restart");
						break;
					}
				}
			}
		}		
	}
	/**
	 * 
	 * @author chenshq
	 * @version 1.0.0
	 * @since 2011-1-6
	 */
	class TcpServer implements Runnable {

		private ServerSocket localServerSocket = null;
		private Socket localSocket = null;
		private readSocketData readSocketData=null;
		private boolean alive = false;
		private Thread localThread;

		public boolean isAlive() {
			return alive;
		}

		public void setAlive(boolean alive) {
			this.alive = alive;
		}

		public void start(){
			localThread = new Thread(this,toString() + "-main");
			localThread.start();
		}

		public TcpServer(ServerSocket localServerSocket) {
			this.localServerSocket = localServerSocket;
			try {
				this.localServerSocket.setSoTimeout(0);
			} catch (SocketException e) {
			}
		}

		/**
		 * 
		 * @param serverSocket
		 * @return
		 */
		private synchronized Socket acceptSocket(ServerSocket serverSocket) {
			Socket localSocket=null;
			try {
				QueueTcpServer.this.log.info("Started... (*:" + serverSocket.getLocalPort() + ")");
				localSocket = serverSocket.accept();
				localSocket.setSoTimeout(socketTimeout );
				if (QueueTcpServer.this.socketLinger != -1)
					localSocket.setSoLinger(true, QueueTcpServer.this.socketLinger);
				QueueTcpServer.this.log.info(serverSocket.getLocalSocketAddress().toString().split("/")[1] + " Connect Success.");
			} catch (IOException localIOException) {
				QueueTcpServer.this.log.error(serverSocket.getLocalSocketAddress().toString().split("/")[1] + " closed.");
				if (localSocket != null) {
					try {
						localSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					localSocket = null;
				}
			}
			return localSocket;
		}

		private synchronized ServerSocket createSocket(int port) {
			// QueueTcpClient.this.setPort(port);
			try {
				localServerSocket = QueueTcpServer.this.createServerSocket(port);
			} catch (IOException e) {
				// log.info("Connect Remote port " + port +" TimeOut!");
				// e.printStackTrace();
			}
			return localServerSocket;
		}

		public void run() {		
			while ((isAlive()))
			{				
				Socket localSocketTmp=acceptSocket(localServerSocket);
				if(localSocketTmp != null)
				{
					if(localSocket !=null)
						try {
							localSocket.close();
						} catch (IOException e) {
						}
						localSocket=null;
						if(readSocketData !=null) 
							readSocketData.setAlive(false);
				
						localSocket=localSocketTmp;
						readSocketData = new readSocketData(localSocket);
						readSocketData.setAlive(true);
						new Thread(readSocketData,toString()+"TcpServerReadData").start();

				}
			}
			if(readSocketData != null)
				readSocketData.setAlive(false);
			if(localSocket != null)
				try {
					localSocket.close();
				} catch (IOException e) {
				}
		   
		}
	}

	class readSocketData implements Runnable
    {
		private Socket localSocket=null;
		private boolean alive = false;
	  
	      public boolean isAlive() {
			return alive;
		  }
		  public void setAlive(boolean alive) {
			this.alive = alive;
		  }
		  public readSocketData(Socket localSocket){
			  this.localSocket=localSocket;;
		  }
		  public void run()
		  {
	    	  while ((isAlive()))
				{
	    		  if(localSocket ==null || localSocket.isClosed()){
	    			  	setAlive(false);
						break;
	    		  }else{
			    	  byte[] data=null;
						try {
							data=(byte[])readStream(localSocket.getInputStream());
							if(data==null)
								continue;
							if(((byte[])data).length<= 40){
								log.info("收到通迅检测报文["+new String((byte[])data)+"]");
								continue;
							}
							queueStream.setQueueServer(data);
						} catch (CommunicationException e) {
							if (localSocket != null)
								log.error(localSocket.getLocalSocketAddress().toString().split("/")[1]
									+ " break.CommunicationException");
							try {
								localSocket.close();
							} catch (IOException e1) {
							}							
						} catch (IOException e) {
							if (localSocket != null)
								log.error(localSocket.getLocalSocketAddress().toString().split("/")[1]
							        + " break. IOException!");
							try {
								localSocket.close();
							} catch (IOException e1) {
							}
						}
	    		  }
				}
	    	  if(localSocket !=null)
					try {
						localSocket.close();
					} catch (IOException e) {
					}
					localSocket=null;
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
			for(int i=0;i<serverSocketCount;i++)
			{
				this.serverSocketList[i].close();
				this.serverSocketList[i] = null;
				tcpServer[i].setAlive(false);
			}
			if (this.postEventTimer != null) {
				this.postEventTimer.cancel();
				this.postEventTimer = null;
			}
		} catch (IOException localIOException) {
			this.log.error("close server socket error.", localIOException);
		}
	}

	public boolean isAlive() {
		return this.alive;
	}

	public String toString() {
		return ((this.name != null) ? this.name : "TcpServer")
				+ super.toString();
	}

	public static void main(String[] paramArrayOfString) throws Exception {
		QueueTcpServer localTcpServer = new QueueTcpServer();
		localTcpServer.setBacklog(30);
		// localTcpServer.setPort(7777);
		localTcpServer.setPortList("7001,7002,7003,7004");
		// localTcpServer.setExecutor(new PooledExecutor());
		localTcpServer.start();
		pause();
	}

	private static void pause() {
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Handler getHandler() {
		return this.handler;
	}

	public void setHandler(Handler paramHandler) {
		this.handler = paramHandler;
	}

	public long getDelay() {
		return this.delay;
	}

	public void setDelay(long paramLong) {
		this.delay = paramLong;
	}

	public void setPeriod(long paramLong) {
	}

	public void setEventId(int paramInt) {
		this.eventId = paramInt;
	}

	public String getPortList() {
		return portList;
	}

	public void setPortList(String portList) {
		this.portList = portList;
	}

	protected Object readStream(InputStream paramInputStream) throws CommunicationException, IOException {
		byte[] arrayOfByte1 = new byte[this.headLength];
		int i = 0;
	
			while (i < this.headLength) {
				int j = paramInputStream.read(arrayOfByte1, i, this.headLength - i);
				/******************************************************************csq*/
				if (j < 0)
					throw new CommunicationException("invalid_packet_head");
//					return null;
				i += j;
			}
			ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte1, getOffsetOfLengthField(), this.headLength - getOffsetOfLengthField());
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
			// 如果只有报文长度，直接返回
			if (k <= this.headLength) {
				return null;
			}
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setReuseSocket(boolean paramBoolean) {
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public void setSocketLinger(int socketLinger) {
		this.socketLinger = socketLinger;
	}

	public void setEventDispatcher(EventDispatcher eventAgent) {
		if (eventAgent == null)
			return;
		this.eventAgent = new EventAgent(eventAgent, this, getId());
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
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

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setDefaultParser(String defaultParser) {
		this.defaultParser = defaultParser;
	}

	public String getLogname() {
		return logname;
	}

	public void setLogname(String logname) {
		this.logname = logname;
	}

	public String getQueueStreamType() {
		return queueStreamType;
	}

	public void setQueueStreamType(String queueStreamType) {
		this.queueStreamType = queueStreamType;
	}

}