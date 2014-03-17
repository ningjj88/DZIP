package csii.base.bean;

import csii.base.action.util.AbstractEnumProperties;

/**
 * 对账标志
 * 
 * @author shengzt
 *
 */
public class CheckFlag extends AbstractEnumProperties{
	private static final long serialVersionUID = 1L;
	
	public static final String FLAG_0 = "0";
	public static final String FLAG_1 = "1";
	public static final String FLAG_2 = "2";
	public static final String FLAG_3 = "3";
	public static final String FLAG_4 = "4";
	public static final String FLAG_5 = "5";
	
	private static final String[][] initValue = {
		{FLAG_0,   "未对账"},
		{FLAG_1,   "对平账"},
		{FLAG_2,   "不平账(状态不一致)"},
		{FLAG_3,   "不平账(金额不一致)"},
		{FLAG_4,   "不平账(渠道多)"},
		{FLAG_5,   "不平账(核心多)"}
	};

	public CheckFlag() {	
		super();
		super.init(CheckFlag.initValue);		
	}
	
}
