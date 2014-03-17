package csii.pe.service.comm.tcp.queue;

import java.io.IOException;
import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.service.comm.Handler;
import com.csii.pe.service.comm.HandlerMapping;
import com.csii.pe.service.comm.Server;
import com.csii.pe.service.event.EventDispatcher;
import com.csii.pe.service.event.support.EventAgent;
import com.csii.pe.service.executor.Executor;

public class QueueServer implements Server {

	protected Log log = LogFactory.getLog(super.getClass());

	private String name;
	private String queueStreamType;
	private HandlerMapping handlerMapping;
	private long threadNo;
	private Executor executor;
	private Handler handler;
	private EventAgent eventAgent;
	private boolean alive = false;
	private String id = "QueueServer";
	private Timer timer;
	private long delay = 0L;
	protected QueueStream queueStream = null;
	
	public synchronized void start() {
		if (isAlive())
			return;
		this.alive = true;
		if(queueStream == null)
		    queueStream = BeanQueueStreamLoader.getSpecialQueueStream(queueStreamType); 
		Runnable local1 = new Runnable() {
			public void run() {
				while ((QueueServer.this.isAlive())) {
					try {
						Handler localHandler = handlerMapping.getHandler("default");
						log.info("Handler... " + "Thread_ID: " + Thread.currentThread().getId() + "  Thread_Name: " + Thread.currentThread().getName()); 
						QueueServer.this.localHandler(queueStream, localHandler);
					} catch (InterruptedException localInterruptedException) {
						QueueServer.this.log.error(" interupted error", localInterruptedException);
					}
				}
			}
		};
		Thread localThread = new Thread(local1, toString() + "-main");
		localThread.start();
		this.log.info("QueueServer Started... " + name);
		this.log.info("QueueServer Started... " + "Thread_ID: " + localThread.getId() + "  Thread_Name: " + localThread.getName()); 
	}
	
	private void localHandler(final QueueStream queueStream2, final Handler paramHandler) throws InterruptedException {
		if (executor == null)
			localHandlerProcess(queueStream2, paramHandler);
		else {
			this.executor.execute(new Runnable() {
				public void run() {
					localHandlerProcess(queueStream2, paramHandler);
				}
			});
		}
	}
	
	private Handler getHandler(String handlerName) {
		Handler handler = handlerMapping.getHandler(handlerName);
		return handler;
	}

	private void localHandlerProcess(QueueStream paramQueueStream, Handler paramHandler) {
		QueueChannel localQueueChannel = null;
		try {
			localQueueChannel = new QueueChannel(paramQueueStream);
			String type = localQueueChannel.getType();
			if (this.log.isDebugEnabled())
				this.log.debug("Accepting Queue " + " in " + Thread.currentThread().getName() + ":" + (++this.threadNo));
			if(paramHandler != null){
				if(!"req".equals(type)){
					paramHandler = getHandler(type);
				}
				this.log.debug("Accepting Queue  type   :" + localQueueChannel.getType()+ " ;");
			} 
			paramHandler.handle(localQueueChannel);
		} catch (Exception localException1) {
			this.log.error(paramQueueStream + " channel handle error.", localException1);
		} finally {

		}
	}

	public synchronized void restart() {
		shutdown();
		start();
	}

	public synchronized void shutdown() {
		if (!(isAlive()))
			return;
		this.alive = false;
		if (this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		}
	}

	private static void pause() {
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isAlive() {
		return this.alive;
	}

	public String toString() {
		return ((this.name != null) ? this.name : "QueueServer") + super.toString();
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setExecutor(Executor paramExecutor) {
		this.executor = paramExecutor;
	}

	public void setHandlerMapping(HandlerMapping paramHandlerMapping) {
		this.handlerMapping = paramHandlerMapping;
	}

	public void setEventDispatcher(EventDispatcher paramEventDispatcher) {
		if (paramEventDispatcher == null)
			return;
		this.eventAgent = new EventAgent(paramEventDispatcher, this, getId());
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Handler getHandler() {
		return this.handler;
	}

	public void setHandler(Handler paramHandler) {
		this.handler = paramHandler;
	}

	public String getQueueStreamType() {
		return queueStreamType;
	}

	public void setQueueStreamType(String queueStreamType) {
		this.queueStreamType = queueStreamType;
	}

	public long getDelay() {
		return this.delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	

}