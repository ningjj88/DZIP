package csii.dzip.action.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.Context;
import csii.base.constant.Constants;

public class UpdateJoural {
	protected final Log logger = LogFactory.getLog(getClass());
	private JdbcAccessAwareProcessor dzipdbAccess;
	private DzipProcessTemplate dzipProcessTemplate;


	/**
	 * @author chenshq
	 * @since 2010.07.22
	 * @用途：更新有原始交易关联的交易流水状态
	 * @param sqlMap
	 */
	@SuppressWarnings("unchecked")
	public void execute(final Map sqlMap )throws DataAccessException {
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					dzipProcessTemplate.updJournal(sqlMap); // 更新交易流水中的状态
					logger.info("Update the status of "+ sqlMap.get(Constants.PE_JOURNAL_NO)	+ " Reversal/Cancel!");
				} catch (SQLException e) {
					logger.error("更新表(T_JOURNAL)失败:"+e.getMessage());
				}
				return null;
			}});
	}
	/**
	 * @author chenshq
	 * @since 2010.07.22
	 * @用途：修改流水交易状态为成功状态。
	 * @param ctx
	 * @param procedureMap
	 */
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx ){
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				//如果是欺诈并且不是本带他交易就调用欺诈存储过程记账欺诈次数并返回响应码
				if(Constants.PE_59.equals(ctx.getData(Constants.PE_HOST_RESP_CD))
						&&!Constants.RTXNCATCD_02.equals(ctx.getData(Constants.RTXNCATCD)))
				{
					dzipProcessTemplate.updFraudInfo(ctx);
				}
				String tranTyp=ctx.getString(Constants.IN_ASSBUSTYP);
				//如果是委托类的自主识别无卡自助,就开通或关闭无卡自助
//				if(Constants.PE_14.equals(tranTyp))
//				    dzipProcessTemplate.OpenNoCardSelf(ctx);
				Map procedureMap =new HashMap();
				procedureMap.put(Constants.PE_TRANS_STAT, ctx.getData(Constants.PE_TRANS_STAT));  	//交易状态
				procedureMap.put(Constants.PE_RTXNCATCD, ctx.getData(Constants.RTXNCATCD));  	    //交易性质
				procedureMap.put(Constants.PE_JOURNAL_NO, ctx.getData(Constants.PE_JOURNAL_NO));  	//交易流水号
				if(Constants.RTXNCATCD_02.equals(ctx.getData(Constants.RTXNCATCD))){//本带他交易更新账务日期
			    	procedureMap.put(Constants.PE_POST_DATE, ctx.getData(Constants.PE_POST_DATE));  // 账务日期
				}
				procedureMap.put(Constants.PE_REMAIN_AMT, ctx.getData(Constants.PE_TRAN_AMT));  //交易金额
				procedureMap.put(Constants.PE_CUST_FEE, ctx.getData(Constants.PE_CUST_FEE));  //个人手续费
				procedureMap.put(Constants.TRAN_FEE, ctx.getData(Constants.TRAN_FEE));  //交易手续费
				procedureMap.put(Constants.PE_RLTSEQNO,ctx.getData(Constants.PE_RLTSEQNO));       	//原交易流水号
				procedureMap.put(Constants.PE_RLTACCTNO,ctx.getData(Constants.PE_RLTACCTNO));       //相关账号
				procedureMap.put(Constants.PE_HOST_RESP_CD,ctx.getData(Constants.PE_HOST_RESP_CD)); //主机响应码
				procedureMap.put(Constants.PE_OPTNBR,ctx.getData(Constants.PE_OPTNBR)); //预授权冻结编号
				procedureMap.put(Constants.PE_HOST_SEQ_NO,ctx.getData(Constants.PE_HOST_SEQ_NO)); 	//主机流水号
				procedureMap.put(Constants.PE_PERSNBR,ctx.getData(Constants.IN_ORIGPERSNBR)); 	    //柜员号
				procedureMap.put(Constants.PE_CASHBOXNBR,ctx.getData(Constants.IN_CASHBOXNBR));     //钱箱号
				procedureMap.put(Constants.PE_CRDB, ctx.getData(Constants.PE_CRDB));  	//借贷标记
				procedureMap.put(Constants.PE_RTXNPLATCD, ctx.getData(Constants.IN_BUSITYP));  	//业务类型
				procedureMap.put(Constants.PE_SETTLMTDATE,ctx.getData(Constants.PE_SETTLMTDATE)); //清算日期
				try {
					dzipProcessTemplate.updJournal(procedureMap);                      //更新交易流水中的状态
					logger.info("Update the t_journal of " + ctx.getData(Constants.PE_JOURNAL_NO)+ " successful!");
				} catch (SQLException e) {
					logger.info("Update the t_journal of " +ctx.getData(Constants.PE_JOURNAL_NO)+ " error,error message="+e.getMessage());
				}
				return null;
			}});
		this.convertRespCD(ctx);//响应信息
	}

	/**
	 * @author xujin
	 * @since 2011.10.19
	 * @用途：修改流水交易响应吗。
	 * @param ctx
	 * @param procedureMap
	 */
	@SuppressWarnings("unchecked")
	public void executeRespond(final Context ctx ){
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				Map procedureMap =new HashMap();
				procedureMap.put(Constants.PE_JOURNAL_NO, ctx.getData(Constants.PE_JOURNAL_NO));  	//交易流水号
				procedureMap.put(Constants.PE_HOST_RESP_CD,ctx.getData(Constants.PE_HOST_RESP_CD)); //主机响应码
				try {
					dzipProcessTemplate.updJournal(procedureMap);                      //更新交易流水中的状态
					logger.info("Update the responcd of " + ctx.getData(Constants.PE_JOURNAL_NO)+ " successful!");
				} catch (SQLException e) {
					logger.error("更新表(T_JOURNAL)失败:"+e.getMessage());
				}
				return null;
			}});
	}

	/**
	 * @author chenshq
	 * @since 2010.07.22
	 * @用途：更新有原始交易关联的交易流水状态
	 * @param ctx
	 * @param procedureMap
	 */
	@SuppressWarnings("unchecked")
	public void execute(final Context ctx,final Map procedureMap )throws DataAccessException {
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					if(procedureMap!=null&&procedureMap.size()>0)
					     dzipProcessTemplate.updJournal(procedureMap); // 更新交易流水中的状态
					logger.info("Update the status of "+ procedureMap.get(Constants.PE_JOURNAL_NO)	+ " Reversal/Cancel!");
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
				return null;
			}});
		execute(ctx);// 修改交易流水状态
	}

	/**
	 * @author chenshaoqi
	 * @since 2010.09.30
	 * @dispcription 查询pos交易的返回消息类型
	 * @return 返回消息类型
	 */
	@SuppressWarnings("unchecked")
	public void convertRespCD(Context ctx){
		Map paramMap = new HashMap();
		if(ctx.getData(Constants.ISO8583_RESCODE)!=null){
			paramMap.put(Constants.RESPCD, ctx.getData(Constants.ISO8583_RESCODE));
			paramMap = (Map) dzipdbAccess.getSqlMap().queryForObject("common.getResp", paramMap); // 调用相关的SQL
			ctx.setData(Constants.ISO8583_RESCODE, paramMap.get(Constants.CHANNRESPCD));
			ctx.setData(Constants.ISO8583_ADDDATA, paramMap.get(Constants.RESPNAME));
		}
		if(ctx.getData(Constants.FIX8583_RESP)!=null){
			paramMap.put(Constants.RESPCD, ctx.getData(Constants.FIX8583_RESP));
			paramMap = (Map) dzipdbAccess.getSqlMap().queryForObject("common.getResp", paramMap); // 调用相关的SQL
			ctx.setData(Constants.FIX8583_RESP, paramMap.get(Constants.CHANNRESPCD));
			ctx.setData(Constants.FIX8583_ADDI, paramMap.get(Constants.RESPNAME));
		}
	}

	/**
	 * @author xujin
	 * @since 2011.04.25
	 * @用途：更新有原始交易关联的交易流水状态
	 * @param sqlMap
	 */
	@SuppressWarnings("unchecked")
	public void executeBatch(final Map procedureMap,final Map origMap)throws DataAccessException {
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				if(origMap !=null&&!origMap.isEmpty()&&origMap.get(Constants.PE_JOURNAL_NO)!=null){
					try {
						dzipProcessTemplate.updJournalBatch(origMap); // 更新交易流水中的状态
						dzipProcessTemplate.updJournal(origMap); // 更新交易流水中的状态
						logger.info("Update the status of  "+ origMap.get(Constants.PE_JOURNAL_NO)	+ " sucess!");
					} catch (SQLException e) {
						logger.error(e.getMessage());
					}
				}
				try {
					dzipProcessTemplate.updJournalBatch(procedureMap); // 更新交易流水中的状态
					if(procedureMap.get(Constants.PE_OPTNBR)!=null||
							procedureMap.get(Constants.PE_HSTSEQNONEW)!=null){
					   procedureMap.put(Constants.PE_HOST_SEQ_NO,procedureMap.get(Constants.PE_HSTSEQNONEW));
					   dzipProcessTemplate.updJournal(procedureMap); // 更新交易流水中的状态
					}
					logger.info("Update the status of  "+ procedureMap.get(Constants.PE_JOURNAL_NO)	+ " sucess!");
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
				return null;
			}});
	}

	/**
	 * @return the dzipdbAccess
	 */
	public JdbcAccessAwareProcessor getDzipdbAccess() {
		return dzipdbAccess;
	}
	/**
	 * @param dzipdbAccess the dzipdbAccess to set
	 */
	public void setDzipdbAccess(JdbcAccessAwareProcessor dzipdbAccess) {
		this.dzipdbAccess = dzipdbAccess;
	}
	/**
	 * @return the dzipProcessTemplate
	 */
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}
	/**
	 * @param dzipProcessTemplate the dzipProcessTemplate to set
	 */
	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
}
