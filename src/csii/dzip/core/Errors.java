package csii.dzip.core;

/**
 * Auto create from dictionary properties files.
 * every item in properties has Format like this:
 * key=comment<0:on;1:off>

 * Creation date: (Wed Oct 27 10:49:31 CST 2010)
 * @author: CSII PowerEngine $Auto Generated$
 */
public class Errors {
    public static final String AUDIT_HAS_OCCURED="Audit_has_occured";           //已经对账，不再重复对账
    public static final String AUDIT_NOT_OCCURED="Audit_not_occured";           //系统没有对账
    public static final String CONNECT_FTP_FAILED="Connect_ftp_failed";         //连接ftp服务器失败
    public static final String CORERTXN_NO_EXIST_DATA="Corertxn_no_exist_data"; //核心流水表不存在该转账记录
    public static final String COULD_NOT_GET_JDB_C__CONNECTION="Could_not_get_JDBC_Connection";     //数据库连接错误
    public static final String DATEBASE_ERROR="Datebase_error";                 //数据库执行错误
    public static final String DOWN_LOAD_FTP_FILE_FAILED="DownLoad_ftp_file_failed";                //下载对账文件失败
    public static final String DOWNLOAD_TASK_HAS_EXCUTED="Download_task_has_excuted";               //对账文件已经下载成功
    public static final String EXPORT_CORE_JOURAL_FAILED="Export_core_joural_failed";               //导出核心交易流水失败
    public static final String FTP_FILE_IS_NOT_EXIST="Ftp_file_is_not_exist";   //选择日期的对账文件不存在
    public static final String GET__JOURNAL__HIST_FAILED="Get_Journal_Hist_failed";                 //历史交易流水读取失败
    public static final String GET__JOURNAL_FAILED="Get_Journal_failed";        //交易流水读取失败
    public static final String GET_CHANNEL__TRAN_FAILED="Get_channel_Tran_failed";                  //渠道交易关系读取失败
    public static final String GET_CHANNEL_INFO_FAILED="Get_channel_info_failed";                   //渠道信息读取失败
    public static final String GET_CORE_POST_DATE_FAILED="Get_core_post_date_failed";               //没有找到账务日期
    public static final String GET_MONITOR_INFO_FAILED="Get_monitor_info_failed";                   //实时信息读取失败
    public static final String GET_SYSTEM_DATE_FAILED="Get_system_date_failed"; //当前账务日期读取失败
    public static final String HISTORY_DATE_NOT_CLEAR="History_date_not_clear"; //系统历史数据没有清理
    public static final String IMPORT_CORE_JOURAL_FAILED="Import_core_joural_failed";               //导入核心交易流水失败
    public static final String IMPORT_FTP_FILE_DATA_FAILED="Import_ftp_file_data_failed";           //装载对账文件任务失败
    public static final String IMPORT_FTP_FILE_DATA_HAS_EXCUTED="Import_ftp_file_data_has_excuted"; //对账文件已经装载成功
    public static final String INSERT__CHECK_TRANS__REC_FAILED="Insert_CheckTrans_Rec_failed";      //记录账户冻结信息失败
    public static final String INSERT_FTP_FILE_INFO_FAILED="Insert_ftp_file_info_failed";           //记录FTP文件信息失败
    public static final String INSERT_TASK_LIST_FAILED="Insert_taskList_failed";//导入任务列表失败
    public static final String LOGGED_IN_FTP_FAILED="Logged_in_ftp_failed";     //Ftp用户名或密码错误，登录失败
    public static final String NO_CLEAR_DATA="No_clear_data";                   //没有数据要清理
    public static final String PARSE_DOWN_LOAD_FILE_FAILED="Parse_downLoad_file_failed";            //解析下载文件失败
    public static final String PARSE_TASK_LIST_FAILED="Parse_taskList_failed";  //解析任务列表失败
    public static final String PRIV_TASK_IS_UNSUCCESSFUL="PrivTask_is_unsuccessful";                //上个任务没有执行成功
    public static final String QUERY_AUDIT_INFO_FAILED="Query_audit_info_failed";                   //查询对账信息失败
    public static final String QUERY_FILE_INFO_FAILED="Query_file_info_failed"; //查询对账文件信息失败
    public static final String QUERY_IMPORT_FILE_INFO_FAILED="Query_import_file_info_failed";       //查询导入文件信息失败
    public static final String QUERY_TASK_LIST_FAILED="Query_task_list_failed"; //查询任务列表信息失败
    public static final String SYSTEM_ERROR="System_error"; //系统错误
    public static final String SYSTEM_UNDEFINED_TRANSACTION="System_undefined_transaction";         //没有找到交易定义
    public static final String TASK_HAS_EXCUTED="Task_has_excuted";             //任务已经执行过
    public static final String TASK_IS_UNENFORCEABLE="Task_is_unenforceable";   //该任务是"不可执行"状态
    public static final String TASK_LIST_HAS_EXSIT="Task_list_has_exsit";       //任务列表已存在，请确认是否已经执行过该操作
    public static final String TASK_LIST_NOT_EXSIT="Task_list_not_exsit";       //任务列表不存在，请核对。
    public static final String TRAN_COULD_NOT_OCCURED="Tran_could_not_occured"; //交易暂时无法执行
    public static final String UPDATE_CHANNEL__TRAN_FAILED="Update_channel_Tran_failed";            //更新渠道交易关系失败
    public static final String UPDATE_CHANNEL_INFO_FAILED="Update_channel_info_failed";             //更新渠道失败
    public static final String RE_IMPORT_JOURNAL_BATCH="Re_import_journal_batch";                   //已经导出过流水信息
    public static final String NO_IMPORT_JOURNAL_BATCH="No_import_journal_batch";                   //没有流水信息
    public static final String MORE_IMPORT_JOURNAL_BATCH="More_import_journal_batch";//导出流水数据太多，请缩小截止日期时间
    public static final String RE_EXEC_JOURNAL_BATCH="Re_exec_journal_batch";                       //已经记账
    public static final String NO_EXEC_JOURNAL_BATCH="No_exec_journal_batch";                       //没有数据记账
    public static final String NO_JCK_AUDIT_JOURNAL="No_jck_audit_journal";                       // 该日期没有成商对账明细
    public static final String EXP_PREAHTUCOMP_NOT_HANDLE="Exp_preauthcomp_not_handle";// 异常的离线预授权完成没有补入
    public static final String EXP_PREAHTUCOMP_HANDLE_STATUS="Exp_preauthcomp_handle_status";// 总共补入{0}笔，补入成功{1}笔,补入失败{2}笔.
    public static final String EXIST_EXP_PREAHTUCOMP_NOT_HANDLE="Exist_exp_preauthcomp_not_handle";//存在{2}笔异常的离线预授权完成交易没有补入，请到离线预授权完成补入界面补入！
    public static final String QUERY_YLSJ_JOURNAL_INFO_FAILED = "Query_YLSJ_Journal_Info_Failed";//查询银联数据交易流水出错
}
