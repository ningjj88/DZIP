package csii.base.bean;

import csii.base.action.util.AbstractEnumProperties;

/**
 * 交易状态
 * 
 * @author shengzt
 *
 */
public class TransStatus extends AbstractEnumProperties{
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
		{CANCEL,   "取消"},
		{FINISH,   "完成"},
		{RETURNED, "退货"},
		{REVERSAL_PRO, "冲正中"},
		{EXCEPTION,    "异常"},
		{REVERSAL_FIN, "已冲正"},
		{JOURNAL_REP,  "补流水"},
		{BEGING,    "处理中"}
	};

	public TransStatus() {	
		super();
		super.init(TransStatus.initValue);		
	}
	
	public static void main(String[] args) {
		TransStatus c = new TransStatus();
		System.out.println(c.get(SUCCESS).getValue());
		System.out.println(c.get(SUCCESS).getDescription());
	}

}
