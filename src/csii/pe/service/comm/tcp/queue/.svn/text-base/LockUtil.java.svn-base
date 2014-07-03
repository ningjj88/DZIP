package csii.pe.service.comm.tcp.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.core.PeRuntimeException;

/**
 * 考虑使用ThreadLocal
 *
 * @author Liuxianbo
 */
@SuppressWarnings("unchecked")
public abstract class LockUtil {
	static Log log = LogFactory.getLog(LockUtil.class);
	public static final String PFLAG = "PFLAG";
	public static final String PFLAG_EXCEPTION = "PFLAG_EXCEPTION";
	public static final String PMESG = "PMESG";

	private String key;
	private long microSeconds;
	private Object object;
	public abstract void doLockWaitAction(Object object) throws Exception;

	public LockUtil(String key, long microSeconds, Object object) {
		this.key = key;
		this.microSeconds = microSeconds;
		this.object = object;
	}
	/**
	 *
	 * @param key
	 *            关键值,应保证接收到应答报文后也能取到此值,并用以唤醒等待线程.
	 * @param microSeconds
	 *            毫秒
	 * @throws InterruptedException
	 * @throws TimeoutException
	 */
	public static final Map lock(String key, long microSeconds)
			throws InterruptedException, TimeoutException {
		/*
		 * 将lock分成两个函数
		 */
		Map lock = setLock(key);
		waitLock(key, microSeconds, lock);
		return lock;
	}
	/**
	 *
	 * @param key
	 *            关键值,应保证接收到应答报文后也能取到此值.
	 */
	private static final Map setLock(String key) {
		Map lock = new HashMap(3);
		synchronized (lockerMap) {
			if (lockerMap.containsKey(key))
				throw new PeRuntimeException("请勿重复提交,key=" + key);
			lockerMap.put(key, lock);
		}
		return lock;
	}
	/**
	 *
	 * @param key
	 *            关键值,应保证接收到应答报文后也能取到此值,并用以唤醒等待线程.
	 * @param microSeconds
	 *            毫秒
	 * @throws InterruptedException
	 * @throws TimeoutException
	 */
	private static final void waitLock(String key, long microSeconds, Map lock)
			throws InterruptedException, TimeoutException {
		long time = System.nanoTime();
		try {
			synchronized (lock) {
				log.info("实时报文key=" + key);
				if (lockerMap.get(key) != null)
					lock.wait(microSeconds);
			}
		} finally {
			// 如传入microSeconds=6000ms则实际等待时间为5999.xms,故判断超时计时时需要加上<1ms.为保险此处加上10ms,需要在多个机器上测试
			time = System.nanoTime() - time + 100000000;
			lockerMap.remove(key);
		}
		// 发送实时报文如123后，如果990或900报文中标志为处理失败，应该立即结束交易。根据PCODE决定是否抛出异常。
		// if(PFLAG_EXCEPTION.equals(lock.get(PFLAG)))
		// throw new PeRuntimeException("处理失败："+lock.get(PMESG));
		if (time > (microSeconds * 1000 * 1000))// 等待超时
			throw new TimeoutException("timeout:" + microSeconds + "ms,key="
					+ key);
		return;
	}

	/**
	 * 用key解锁,如果失败或已经被解锁则抛出异常
	 *
	 * @param key
	 */
	public static final void unLock(String key) {
		Map lock = getLock(key);
		synchronized (lock) {
			lockerMap.remove(key);
			lock.notifyAll();
		};
	}

	/**
	 * 用key解锁,如果失败或已经被解锁则抛出异常
	 *
	 * @param key
	 * @param replyMap
	 *            实时回执数据
	 */
	public static final void unLock(String key, Map replyMap) {
		Map lock = getLock(key);
		synchronized (lock) {
			lockerMap.remove(key);
			lock.putAll(replyMap);
			lock.notifyAll();
		};
	}

	/**
	 * 取锁,如果失败或已经被解锁则抛出异常
	 *
	 * @param key
	 * @return
	 */
	public static final Map getLock(String key) {
		Map lock = lockerMap.get(key);
		if (lock == null)
			throw new PeRuntimeException("原交易线程已唤醒或已超时,key=" + key);
		return lock;
	}
	//
	// protected static final synchronized boolean checkUnlocked(String key){
	// return lockerMap.containsKey(key);
	// }
	/** 钥匙-锁 对 */
	private static final Map<String, Map> lockerMap = new ConcurrentHashMap<String, Map>();

	public static Map execute(LockUtil LockUtil) throws Exception {
		/*
		 * 当网络环境较差时，MQ关闭时间较长（最长15秒），还没设置LOCK时，应答报文已接收， UNLOCK时报错。
		 * 调整为发送报文前设置LOCK，发送完检查是否收到应答，如果已收到应答，线程不等待。
		 *
		 */
		Map lock = setLock(LockUtil.key);
		LockUtil.doLockWaitAction(LockUtil.object);
		waitLock(LockUtil.key, LockUtil.microSeconds, lock);
		return lock;
	}

	// 测试
	public static void main(String[] args) throws InterruptedException {
		new Thread() {
			@Override
			public void run() {
				try {
					lock("k", 100000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				} catch (TimeoutException e) {
					log.error(e.getMessage(), e);
				}
				System.out.println("notify");

			}
		}.start();
		System.out.println("2343");
		TimeUnit.MILLISECONDS.sleep(1000);
		System.out.println("2");
		new Thread() {
			@Override
			public void run() {
				unLock("k");
				unLock("k");
			}
		}.start();

		TimeUnit.MILLISECONDS.sleep(5000);
	}

}
