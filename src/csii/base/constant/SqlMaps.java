package csii.base.constant;
/**
 *  交易中调用ibatis中定义的sqlMap
 * @author CHENSHAOQI
 * @version 1.0.0
 * @since 2010-9-17
 */
public class SqlMaps {

	/***************** Common 命名空间的SQL *****************/
	public static final String COMMON_QUERYCHANNLID = "common.querychannID"; // 查询外围系统编码
	public static final String COMMON_QUERYINSTRANCD = "common.queryInstrancd"; // 内外部交易的转换
	public static final String COMMON_QUERYPARAM = "common.queryParam"; // 获得配置参数值
	public static final String COMMON_QUERYSYSSTAT = "common.querySysstat"; // 查询平台账务日期
	public static final String COMMON_INSERTJOURNAL = "common.insertJournal"; // 记录交易流水操作
	public static final String COMMON_QUERYINPROCD = "common.queryInprocd"; // 查询内部处理码和返回消息类型
	public static final String COMMON_UPDATETRANSTATUS = "common.updateTranStatus"; // 交易结束后更改交易状态
	public static final String COMMON_CHECKPIN = "common.CheckPin"; // 密码验证
	/**通过原交易流水号找到当前交易流水号*/
	public static final String COMMON_GET_SYSSEQNO_BY_RLTSEQNO = "common.getSysseqByRltseqno";
	/**通过转入圈存冲正找到转账圈存冲正交易信息*/
	public static final String COMMON_GETJOUNAL_BY_TRANSINREVER = "common.getJournalInfoByTransferInReversal";
	/**T_ICCLOSINGPAY插入或更新记录*/
	public static final String COMMON_INSERTORUPDATE_CLOSING_PAY = "common.insertOrUpdateClosingPay";
	/**银联数据应答后更新ICCLOSINGPAY*/
	public static final String COMMON_UPDATEICCLOSINGPAY = "common.updateIcClosingPay";
	/**获得柜面可用的电子现金交易名*/
	public static final String COMMON_GETACTIVEICTRAN4ONLI = "common.getActiveIcTran4Onli";
	/**获得圈存上限返回给柜面*/
	public static final String COMMON_GETCREDIT4LOADLIMIT = "common.getCredit4LoadLimit";
	/**查询IC卡销户登记记录*/
	public static final String COMMON_QUERYCLOSEACCOUNTRETISTER = "common.queryCloseAccountRegister";
	/**COMMON_UPDATECARDICEWALLETCTL*/
	public static final String COMMON_UPDATE_CARDICEWALLETCTL = "common.updateICEWALLETCTL";
	/**查询指定卡号的待补登金额*/
	public static final String COMMON_QUERY_ADDITIONAL_AMT = "common.queryAdditionalAmt";

	/*******************atm 命名空间的SQL********************/
	public static final String ATM_TRANSFERINBRCH = "atm.TransferInBrch"; // ATM存款/取款,查询交易，调用核心存储过程
	public static final String ATM_CONFIRMDEPOSIT = "atm.ConfirmDeposit"; // ATM 存款确认交易
	public static final String ATM_UPDATECARDPASSWORD = "atm.UpdCardPassword"; // ATM 改密交易
	public static final String ATM_CHECKOURCARDYN = "atm.ChkOurCardYN"; // ATM ATM 验证是否达州商行本行卡
	/**更新核心表CARDICEWALLETCTL待补登金额*/
	public static final String ATM_ICCARD_BAL = "atm.getIcCardBal";

	/*******************audit 命名空间的SQL********************/
	public static final String AUDIT_INSERTTASKLIST = "Audit.insertTaskList";  //记录日终任务表
	public static final String AUDIT_UPDATETASKSTATUS = "Audit.updateTaskStatus";  //更新日终任务状态
	public static final String AUDIT_INSERTFILEINFO = "Audit.insertFileInfo";  //记录文件处理信息
	public static final String AUDIT_UPDATEFILESTATUS = "Audit.updateFileStatus";  //更新文件处理状态
	public static final String AUDIT_INSERTJOURNAL = "Audit.importJournal";  //导入前置交易流水
	public static final String AUDIT_GETRTXN = "Audit.getRTXN";  //导出核心交易流水
	public static final String AUDIT_IMPORTRTXN = "Audit.importRTXN";  //导入核心交易流水
	public static final String AUDIT_CHECKCOREANDJCK = "Audit.Check_CoreAndJCK";  //核对前置流水与核心流水
	public static final String AUDIT_ADDCHECKTRANSREC = "Audit.Add_CheckTransRec";  //核对前置流水与渠道流水
	public static final String AUDIT_GETCHECKTRANSREC = "Audit.GetCheckTransRec";  //导出不一致信息表
	public static final String AUDIT_ADDACCTACCTHOLD = "Audit.Add_AcctAcctHold";  //记录账户冻结信息


	public static final String AUDIT_QUERYTASKSTATUS = "Audit.queryTaskStatus";  //查询任务状态
	public static final String AUDIT_QUERYFILEINFO = "Audit.queryFileInfo";  //查询对账文件
	public static final String AUDIT_QUERYAUDITINFO = "Audit.queryAuditInfo";  //对账查询
	public static final String AUDIT_QUERYTASKISVALIDATE = "Audit.queryTaskIsvalidate";  //验证任务是否可以执行

	/*******************platformAdmin 命名空间的SQL********************/
	public static final String 	PLATFORMADMIN_GETMONITOR = "platformAdmin.getMonitor";     //实时监控/查询流水交易表
	public static final String 	PLATFORMADMIN_GETCHANNEL = "platformAdmin.GetChannel";     //查询渠道信息
	public static final String 	PLATFORMADMIN_UPDCHANNEL = "platformAdmin.UpdChannel";     //查询渠道信息
	public static final String 	PLATFORMADMIN_GETCHANNELTRAN = "platformAdmin.GetChannelTran";     //查询渠道交易关系
	public static final String 	PLATFORMADMIN_UPDCHANNELTRAN = "platformAdmin.UpdChannelTran";     //更改渠道交易关系
	public static final String 	PLATFORMADMIN_INQJOURNALINFO = "platformAdmin.InqJournalInfo";     //流水信息当日查询
	public static final String 	PLATFORMADMIN_INQJOURNALHISTINFO = "platformAdmin.InqJournalHistInfo";     //流水信息历史查询

	/*******************DaySwitch 命名空间的SQL********************/
	public static final String DAYSWITCH_GETCOREPOSTDATE = "DaySwitch.getCorePostDate";     //获得核心的账务日期
	public static final String DAYSWITCH_DAYENDTASK = "DaySwitch.dayEndTask";     //日切
	public static final String DAYSWITCH_QUERYSYSSTAT= "DaySwitch.querySysstat";     //查询平台账务日期
	public static final String DAYSWITCH_CLEAR_ONLINE_JOURNAL= "DaySwitch.clear_Online_Journal";     //联机数据清理

	/*******************POS 命名空间的SQL********************/
	public static final String POS_SELECTACCTINFO = "pos.SelectAcctInfo";     //查询账户信息交易
	public static final String POS_VAL_DEPWTH = "pos.Val_DepWth";     //存/取款验证交易
	public static final String POS_DEPWTH = "pos.DepWth";     //存/取款验证交易

	/*******************IC 命名空间的SQL********************/
	/**查询前置差错处理的会计科目*/
	public static final String IC_QUERY_DZIP_GLACCT = "ic.queryDzipGlacct";
}
