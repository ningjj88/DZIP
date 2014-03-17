package csii.base.action;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csii.ibs.action.AbstractIbsAction;
import com.csii.pe.action.Executable;
import com.csii.pe.config.support.ServicesAware;
import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;
import com.csii.pe.service.Service;
import com.csii.pe.service.id.IdFactory;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-5-20
 * 
 */
public abstract class BaseAction extends AbstractIbsAction implements Executable, ServicesAware {

	private String transId = null; 

	private Map services = null;
	
	private Map validators = null;
	
	protected final Log logger = LogFactory.getLog(getClass());

	public abstract void execute(Context ctx) throws PeException;

	/**
	 * @return
	 */
	public String getTransId() {
		return transId;
	}
	
	/**
	 * @param string
	 */
	public void setTransId(String string) {
		transId = string;
	}

	/**
	 * 获取本系统提供的Services
	 */
	public Map getServices() {
		return services;
	}

	public void setServices(Map services) {
		this.services = services;
	}

	/**
	 * 获取validators
	 * @return
	 */
	public Map getValidators() {
		return validators;
	}

	/**
	 * 设置 validators
	 * @param validators
	 */
	public void setValidators(Map validators) {
		this.validators = validators;
	}
	
	/**
	 * 
	 * @param Id
	 * @return
	 */
	protected IdFactory getIdFactory(String Id) {
		Map services = this.getServices();
		IdFactory idFactory = (IdFactory) this.getServices().get(Id);
		return idFactory;
	}

	public Service getService(String id) {
		return (Service) getServices().get(id);
	}
	
	protected String getSeqMsgSeq() {
		return null;
	}
	
}