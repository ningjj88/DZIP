package csii.base.bean;

import csii.base.action.util.AbstractEnumProperties;

/**
 * 交易类型
 * 
 * @author shengzt
 *
 */
public class TransTypeDef extends AbstractEnumProperties{
	private static final long serialVersionUID = 1L;
	
	public static final String ISS = "1";
	public static final String CACC = "2";
	public static final String OTHER = "0";
	
	private static final String[][] initValue = {
		{ISS, "发卡方交易"},
		{CACC,    "受理方交易"},
		{OTHER,      "未知"}		
	};

	public TransTypeDef() {	
		super();
		super.init(TransTypeDef.initValue);		
	}
	
}
