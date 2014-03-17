/**
 * 
 */
package csii.pe.service.comm.tcp.queue;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2011-3-8
 */
public class BeanQueueStreamLoader implements ApplicationContextAware{

	private static ApplicationContext applicationContext;
	
	private static final String DOT = ".";
	
	private static final String POSTFIX_QUEUESTREAM = "QueueStream";

	public void setApplicationContext(ApplicationContext applicationcontext) throws BeansException {
		this.applicationContext = applicationcontext;
	}
	
	protected static ApplicationContext getApplicationContext(){
		return applicationContext;
	}

	protected static Object getBean(String name){
		 return applicationContext.getBean(name);
	}
	
	/**
	 * 获取指定的 queuestream
	 * 
	 * @param namespace
	 * @return
	 */
	public static QueueStream getSpecialQueueStream(String namespace){
		String fullName = null;
		if(namespace == null){
			fullName = POSTFIX_QUEUESTREAM;
		}else{
			fullName = namespace + DOT + POSTFIX_QUEUESTREAM;
		}
		return (QueueStream) getBean(fullName);
	}

}
