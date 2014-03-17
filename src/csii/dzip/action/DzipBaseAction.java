/**
 *
 */
package csii.dzip.action;

import com.csii.pe.service.comm.Transport;

import csii.base.action.BaseAction;
import csii.base.constant.Constants;

/**
 * @author 张永庆
 * @version 1.0.0
 * @since 2010-5-28
 */
public abstract class DzipBaseAction extends BaseAction{
	/**
	 * POS机
	 */
	public final static String POS = "POS";
	/**
	 * ATM机
	 */
	public final static String ATM = "ATM";
	/**
	 * 圈存机(Credit for Load Machine)
	 */
	public final static String CLM = "CLM";

	/**
	 * 获取Tranport2Host
	 *
	 * @return
	 */
	protected Transport getTransport2Host() {
		Transport transPort = (Transport) this.getService(Constants.DzipJinCheng2HostTransport);
		return transPort;
	}

	/**
	 * 获取平台序列
	 *
	 * @return
	 */
	protected String getPEJournalNO(){
		String peJournalNO = getIdFactory(Constants.PE_JOURNAL_NO).generate().toString();
		return peJournalNO;
	}

	/**
	 * 获取银联业务的序列
	 *
	 * @return
	 */
	protected String getPECupsNO(){
		String peCupsNO = getIdFactory(Constants.PE_CUPS_NO).generate().toString();
		return peCupsNO;
	}

}