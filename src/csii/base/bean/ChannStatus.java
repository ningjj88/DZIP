package csii.base.bean;

import csii.base.action.util.AbstractEnumProperties;

/**
 * 渠道状态
 * 
 * @author shengzt
 *
 */
public class ChannStatus extends AbstractEnumProperties{
	private static final long serialVersionUID = 1L;
	
	public static final String USABLE = "1";
	public static final String DISABLED = "0";
	
	private static final String[][] initValue = {
		{USABLE,   "可用"},
		{DISABLED, "不可用"}
	};

	public ChannStatus() {	
		super();
		super.init(ChannStatus.initValue);		
	}
	
}
