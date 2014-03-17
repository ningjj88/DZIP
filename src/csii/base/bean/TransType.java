package csii.base.bean;

import csii.base.action.util.AbstractEnumProperties;

/**
 * 交易类型
 * 
 * @author shengzt
 *
 */
public class TransType extends AbstractEnumProperties{
	private static final long serialVersionUID = 1L;
	
	public static final String WITHDRAWAL = "C";
	public static final String DEPOSIT = "D";
	public static final String TRANSFER = "X";
	public static final String QUERY = "Q";
	public static final String OTHER = "O";
	
	private static final String[][] initValue = {
		{WITHDRAWAL, "取款"},
		{DEPOSIT,    "存款"},
		{TRANSFER,   "转帐"},
		{QUERY,      "查询"},
		{OTHER,      "其他"}		
	};

	public TransType() {	
		super();
		super.init(TransType.initValue);		
	}
	
}
