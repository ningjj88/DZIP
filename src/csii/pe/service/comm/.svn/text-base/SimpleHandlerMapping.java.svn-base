/**
 * 
 */
package csii.pe.service.comm;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.pe.service.comm.Handler;
import com.csii.pe.service.comm.HandlerMapping;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2011-3-8
 */
public class SimpleHandlerMapping implements HandlerMapping {

	private static Log log;
	private Map<?, ?> handlers;

	static 
	{
		log = LogFactory.getLog(com.csii.pe.service.comm.SimpleHandlerMapping.class);
	}

	/*
	 * 通过名字获取Handler
	 * 
	 * @see com.csii.pe.service.comm.HandlerMapping#getHandler(java.lang.String)
	 */
	public Handler getHandler(String handlerName) {
		Handler handler = null;
		if (handlers != null) {
			handler = (Handler) handlers.get(handlerName);
			if (handler == null) {
				handler = (Handler) handlers.get("*");
				if (handler != null && log.isDebugEnabled())
					log.debug("found handler by *.");
			} else if (log.isDebugEnabled())
				log.debug("found handler by " + handlerName +  ".");
		}
		if (handler == null) {
			log.error("no handler found, you must define handler first.");
			throw new IllegalArgumentException("no handler found.");
		} else {
			return handler;
		}
	}

	public Map getHandlers() {
		return handlers;
	}

	public void setHandlers(Map handlers) {
		this.handlers = handlers;
	}

}
