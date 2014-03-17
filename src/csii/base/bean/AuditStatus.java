package csii.base.bean;

import csii.base.action.util.AbstractEnumProperties;

/**
 * 任务状态
 * 
 * @author chenshq
 *
 */
public class AuditStatus extends AbstractEnumProperties{
	private static final long serialVersionUID = 1L;
	
	public static final String SUCCESSFUL = "00";// 执行完成
	public static final String INITIAL = "01";// 初始化
	public static final String EXCUTING = "02";// 正在执行
	public static final String FAILED = "99";// 执行失败

	
	private static final String[][] initValue = {
		{SUCCESSFUL,"执行完成"},
		{INITIAL, "初始化"},
		{EXCUTING,"正在执行"},
		{FAILED, "执行失败"}
	};

	public AuditStatus() {	
		super();
		super.init(AuditStatus.initValue);		
	}
	
}
