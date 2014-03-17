package csii.base.bean;

import csii.base.action.util.AbstractEnumProperties;

/**
 * 新交易状态
 * 
 * @author 徐锦
 *
 */
public class TranNewStatus extends AbstractEnumProperties{
	private static final long serialVersionUID = 1L;
	
	public static final String SUCCESS = "0";
	public static final String CANCEL = "1";
	public static final String FINISH = "2";
	public static final String RETURNED = "3";
	public static final String REVERSAL_PRO = "4";
	public static final String EXCEPTION = "8";
	public static final String REVERSAL_FIN = "9";
	public static final String JOURNAL_REP = "A";
	public static final String BEGING = "Z";
	
	private static final String[][] initValue = {
		{SUCCESS,  "成功"},
		{CANCEL,   "成功"},
		{FINISH,   "成功"},
		{RETURNED, "成功"},
		{REVERSAL_PRO, "失败"},
		{EXCEPTION,    "失败"},
		{REVERSAL_FIN, "成功"},
		{JOURNAL_REP,  "成功"},
		{BEGING,    "失败"}
	};

	public TranNewStatus() {	
		super();
		super.init(TranNewStatus.initValue);		
	}
	
	public static void main(String[] args) {
		TransStatus c = new TransStatus();
		System.out.println(c.get(SUCCESS).getValue());
		System.out.println(c.get(SUCCESS).getDescription());
	}

}
