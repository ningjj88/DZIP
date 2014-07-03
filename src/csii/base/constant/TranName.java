package csii.base.constant;

/**
 * 交易名
 * @author xiehai
 * @date 2014-1-3 下午01:24:38
 */
public class TranName {

	public static final String dzipATM_Deposit  = "dzipATM.Deposit";
	public static final String dzipATM_Deposit_Code  = "020021";
	public static final String dzipCounter_Deposit  = "dzipCounter.Deposit";
	public static final String dzipCounter_Deposit_Code  = "019702";

	/**********************柜面电子现金交易的transactionID******************************/
	/**********************namespace=onli******************************/
	/**柜面电子现金交易指定账户圈存*/
	public static final String ONLI_CREDIT4LOAD = "onli.Credit4Load";
	/**柜面电子现金交易指定账户圈存冲正*/
	public static final String ONLI_CREDIT4LOADREVERSAL = "onli.Credit4LoadReversal";
	/**柜面电子现金交易指定账户圈提*/
	public static final String ONLI_DEBIT4LOAD = "onli.Debit4Load";
	/**柜面电子现金交易指定账户圈提确认*/
	public static final String ONLI_DEBIT4LOADCONFIRM = "onli.Debit4LoadConfirm";
	/**柜面电子现金交易现金充值圈存*/
	public static final String ONLI_CASHRECHARGE4CREDIT4LOAD = "onli.CashRecharge4Credit4Load";
	/**柜面电子现金交易现金充值圈存冲正*/
	public static final String ONLI_CASHRECHARGE4CREDIT4LOADREVERSAL = "onli.CashRecharge4Credit4LoadReversal";
	/**柜面电子现金交易现金充值圈存撤销*/
	public static final String ONLI_CASHRECHARGE4CREDIT4LOADCANCEL = "onli.CashRecharge4Credit4LoadCancel";
	/**柜面电子现金交易现金充值圈存撤销冲正*/
	public static final String ONLI_CASHRECHARGE4CREDIT4LOADCANCELREVERSAL = "onli.CashRecharge4Credit4LoadCancelReversal";
	/**柜面电子现金交易非指定账户转账圈存*/
	public static final String ONLI_TRANSFERCREDIT4LOAD = "onli.TransferCredit4Load";
	/**柜面电子现金交易非指定账户转账圈存冲正*/
	public static final String ONLI_TRANSFERCREDIT4LOADREVERSAL = "onli.TransferCredit4LoadReversal";
	/**柜面电子现金交易脚本通知*/
	public static final String ONLI_SCRIPT_NOTICE = "onli.ScriptNotice";
	/**柜面电子现金交易补登圈存*/
	public static final String ONLI_ADDITIONAL_CREDIT4LOAD = "onli.AdditionalCredit4Load";
	/**柜面电子现金交易补登圈存冲正*/
	public static final String ONLI_ADDITIONAL_CREDIT4LOADREVERSAL = "onli.AdditionalCredit4LoadReversal";
	/**柜面电子现金交易转待补登*/
	public static final String ONLI_SWITCH_ADDITIONAL_CREDIT4LOAD = "onli.SwitchAdditionalCredit4Load";
	/**柜面电子现金交易转待补登确认*/
	public static final String ONLI_SWITCH_ADDITIONAL_CREDIT4LOAD_CONFIRM = "onli.SwitchAdditionalCredit4LoadConfirm";
	/**********************namespace=platformAdmin******************************/
	/**柜面IC卡销户结清*/
	public static final String PLATFORM_CLOSE_ACCOUNT_SETTLE = "platformAdmin.CloseAccountSettle";
	/**柜面IC卡销户结清冲正*/
	public static final String PLATFORM_CLOSE_ACCOUNT_SETTLE_REVERSAL = "platformAdmin.CloseAccountSettleReversal";
}
