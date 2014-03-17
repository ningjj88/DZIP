package csii.dzip.action.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.csii.pe.config.support.JdbcAccessAwareProcessor;
import com.csii.pe.core.PeException;

import csii.base.action.util.FileUtils;
import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.util.ftp.Ftp;
import csii.dzip.action.util.ftp.FtpForYLSJ;
import csii.dzip.core.Errors;
/**
 * @author chenshaoqi
 * @version 1.0.0
 * @since 2010-9-07
 */
public class AuditProcessor {
	
	protected final Log logger = LogFactory.getLog(AuditProcessor.class);
	protected final int ARRYSIZE =1024; //定义数组的大小
	protected  JdbcAccessAwareProcessor dzipdbAccess;  //前置数据库连接
	protected  JdbcAccessAwareProcessor corebankAccess;//核心数据库连接

	private  Ftp ftpForJck;   //锦城卡FTP服务器对象
	private  Ftp ftpForYlqz;  //银联前置FTP服务器对象
	private  FtpForYLSJ ftpForYlsj;  //银联数据FTP服务器对象

	public FtpForYLSJ getFtpForYlsj() {
		return ftpForYlsj;
	}
	public void setFtpForYlsj(FtpForYLSJ ftpForYlsj) {
		this.ftpForYlsj = ftpForYlsj;
	}
	/**
	 * @param auditType
	 * @return
	 */
	public String getTaskType(String auditType) {
		String taskType="";
		if(auditType.equals(Constants.AUDIT_JCK))  //判断对账类型
			taskType=Constants.TASKTYPE_JCK;       //锦城卡
		if(auditType.equals(Constants.AUDIT_YLQZ)) 
			taskType=Constants.TASKTYPE_YLQZ;      //银联
		if(auditType.equals(Constants.AUDIT_YLSJ)) 
			taskType=Constants.TASKTYPE_YLSJ;      //银联
		return taskType;
	}
//	/**
//	 * 验证是否已经对账
//	 * @author chenshq
//	 * @param paramMap
//	 * @return 执行结果
//	 */
//	@SuppressWarnings("unchecked")
//	public boolean checkAuditedYN( String postDate,  String taskTyp){
//		boolean flag = true;
//		Map  paramMap= new HashMap();
//		paramMap.put(Constants.POSTDATE,postDate);
//		paramMap.put(Constants.TASKTYP,taskTyp);
//		paramMap.put(Constants.TASKNAME, Constants.TASK_6);
//		List resultList = new ArrayList();
//		try {
//			logger.debug("checkAuditedYN ===============================>Start!");
//			resultList = localdb.getSqlMap().queryForList("Audit.queryAuditIsOccured", paramMap);
//			logger.debug("checkAuditedYN result =========================>"	+ resultList);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (resultList != null && resultList.size() > 0)
//			flag = false;
//		return flag;      																	//返回是否成功
//	}
	/**
	 * 获取导入数据文件名称
	 * @param paramMap
	 * @return
	 * @throws PeException 
	 */
	@SuppressWarnings("unchecked")
	public List getFileName(Map paramMap) throws PeException{
		logger.info("queryFileName ==================>Start!" );
		List list = new ArrayList();
		try {
			list = dzipdbAccess.getSqlMap().queryForList("Audit.queryFileInfo", paramMap); 
		} catch (Exception e) {
			if(e.getMessage().contains("Could not get JDBC Connection"))
				throw new PeException(Errors.COULD_NOT_GET_JDB_C__CONNECTION);
			else
				throw new PeException(Errors.DATEBASE_ERROR);
		}
		return list;      												 //返回结果
	}
	/**
	 * @author chenshq
	 * @date 2010.09.08
	 * @desc 解析任务列表文件
	 */
	@SuppressWarnings("unchecked")
	public List parseAuditTaskList(String taskType) {
		List taskList = new ArrayList();
		String filePath=Constants.TaskListXML;
		if(Constants.TASKTYPE_YLSJ.equals(taskType)){
			filePath=Constants.YLSJTaskListXML;
		}
		Element root = FileUtils.getRootElement(filePath);
		//解析任务列表文件，将任务项读出
		for (Iterator it = root.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();
			Map taskMap = new HashMap();
			taskMap.put(Constants.TASKNAME,  element.attributeValue(Constants.TASKNAME)); 	 //任务名称
			taskMap.put(Constants.TASKSEQNUM, element.attributeValue(Constants.TASKSEQNUM)); //任务序号
			taskMap.put(Constants.TASKSTAT, element.attributeValue(Constants.TASKSTAT));  	 //任务状态
			taskMap.put(Constants.TASKEXECYN, element.attributeValue(Constants.TASKEXECYN)); //任务是否可以执行
			taskMap.put(Constants.TASKPRIVSEQNUM, element.attributeValue(Constants.TASKPRIVSEQNUM));//前一个任务的序号
			taskMap.put(Constants.TASKNEXTSEQNUM, element.attributeValue(Constants.TASKNEXTSEQNUM)); //后续任务
			taskMap.put(Constants.TASKDESC, element.attributeValue(Constants.TASKDESC));     //任务描述
			taskList.add(taskMap);
		}
		return taskList;         //返回任务列表
	}
	/**
	 * 检验是否已经导入任务列表
	 * @param paramMap
	 * @return 
	 * @throws PeException 
	 */
	@SuppressWarnings("unchecked")
	public  boolean checkInsertTaskListYN( String postDate,  String taskTyp) throws PeException
	{
		logger.info("checkinsertTaskListYN ==============>Start!");
		List list = new ArrayList();
		Map paramMap = new HashMap();
		paramMap.put(Constants.POSTDATE, postDate);
		paramMap.put(Constants.TASKTYP, taskTyp);
		try {
			list = dzipdbAccess.getSqlMap().queryForList("Audit.queryTaskStatus", paramMap);
		} catch (Exception e) {
			if(e.getMessage().contains("Could not get JDBC Connection"))
				throw new PeException(Errors.COULD_NOT_GET_JDB_C__CONNECTION);
			else
				throw new PeException(Errors.DATEBASE_ERROR);
		}
		if (list == null || list.size() == 0)
			return true;
		else
			return false;
	}
	/**
	 * @author chenshq
	 * @date 2010.09.08
	 * @desc 执行日终前先把任务列表写入数据库中
	 * @param taskList参数
	 */
	@SuppressWarnings("unchecked")
	public  void insertTaskList(final List taskList,final String postDate, final String taskTyp)
	{
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				for(Iterator iterator = taskList.iterator();iterator.hasNext();){	 // 遍历任务列表，将任务导入数据库
					Map paramMap = (Map) iterator.next();
					paramMap.put(Constants.POSTDATE, postDate); // 账务日期
					paramMap.put(Constants.TASKTYP, taskTyp); // 对账类型
					dzipdbAccess.getSqlMap().insert("Audit.insertTaskList", paramMap);
				}
				return null;
			}
		});
	}

	/**
	 * @author chenshq
	 * @date 2010.09.08
	 * @desc 任务执行后，更新任务状态
	 * @param taskList参数
	 */
	@SuppressWarnings("unchecked")
	public  void updateTaskStatus(final Map paramMap )
	{
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
					try {
						dzipdbAccess.getSqlMap().update("Audit.updateTaskStatus", paramMap);
					} catch (Exception e) {
						e.printStackTrace();
					}
				return null;
			}
		});
	}
//	/**
//	 * 检验是否已经下载对账文件
//	 * @param paramMap
//	 * @return 是否已经下载过文件
//	 */
//	@SuppressWarnings("unchecked")
//	public  boolean checkDownLoadYN(Map paramMap)
//	{
//		List list = this.queryFileInfo(paramMap);
//		if(list==null || list.size()==0)
//			return true;
//		else 
//			return false;
//	}
	/**
	 * 下载对账文件
	 * @param remoteFile
	 * @param localFile
	 * @param taskTyp
	 * @return
	 * @throws PeException 
	 */
	public  boolean downLoad(String remoteFile, String localFile,String taskTyp) throws PeException
	{
		boolean flag =false;
		if(taskTyp.equals(Constants.AUDIT_JCK))  				//判断对账类型 01：锦城卡 02 银联
			flag = ftpForJck.download(remoteFile, localFile);   //通过锦城卡的FTP服务器下载对账文件
		else
			flag = ftpForYlqz.download(remoteFile, localFile);  //通过银联前置的FTP服务器下载对账文件
		return flag;
	}
	
	public  boolean downLoad(String remoteFile, String localFile,String taskTyp, String postData) throws PeException
	{
		boolean flag =false;
		flag = ftpForYlsj.download(remoteFile, localFile, postData);	//通过银联数据的FTP服务器下载对账文件
		return flag;
	}
	/**
	 * @author chenshq
	 * @throws PeException 
	 * @date 2010.09.08
	 * @desc 检验是否已经记录对账文件信息
	 */
	@SuppressWarnings("unchecked")
	public boolean checkInsertFileInfoYN(Map paramMap) throws PeException {
		logger.info("checkInsertFileInfoYN ===============================>Start!");
		List list = new ArrayList();
		try {
			list = dzipdbAccess.getSqlMap().queryForList("Audit.checkFileInfo", paramMap);  
		} catch (Exception e) {
			if(e.getMessage().contains("Could not get JDBC Connection"))
				throw new PeException(Errors.COULD_NOT_GET_JDB_C__CONNECTION);
			else
				throw new PeException(Errors.DATEBASE_ERROR);
		}
		if (list == null || list.size() == 0)
			return true;
		else
			return false;
	}
	/**
	 * @author chenshq
	 * @date 2010.09.08
	 * @desc 将导入的文件信息记入文件处理表
	 */
	@SuppressWarnings("unchecked")
	public boolean insertFileInfo(final Map paramMap) {
		final Map flag = new HashMap();
		flag.put("flag","true");     //存放是否成功的状态
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
					try {
						dzipdbAccess.getSqlMap()	.insert("Audit.insertFileInfo", paramMap);
					} catch (DataAccessException e) {
						flag.put("flag","false");     //发生异常，返回false
					}
				return null;
			}
		});
		return Boolean.valueOf(flag.get("flag").toString());       //返回是否成功
	}
	
	/**
	 * @author chenshq
	 * @date 2010.09.08
	 * @desc 将导入的文件信息从文件处理表删除
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteFileInfo(final Map paramMap) {
		final Map flag = new HashMap();
		flag.put("flag","true");     //存放是否成功的状态
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
					try {
						dzipdbAccess.getSqlMap().delete("Audit.deleteFileInfo", paramMap);
					} catch (DataAccessException e) {
						flag.put("flag","false");     //发生异常，返回false
					}
				return null;
			}
		});
		return Boolean.valueOf(flag.get("flag").toString());       //返回是否成功
	}
	/**
	 * @author chenshq
	 * @date 2010.09.08
	 * @desc 导出数据后，更新文件的处理状态
	 */
	@SuppressWarnings("unchecked")
	public void updateFileStatus(final Map paramMap) {
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
					dzipdbAccess.getSqlMap().update("Audit.updateFileStatus", paramMap);
				return null;
			}
		});
	}
	/**
	 * 解析excel文件，
	 * @author chenshaoqi
	 * @param filePath 待解析文件的路径
	 * @return 返回存放记录的list
	 */
	@SuppressWarnings("unchecked")
	public static List excelReader(String filePath) {
		List listRecord= new ArrayList();
		Map recordMap = new HashMap();
		try {
			Workbook workBook = Workbook.getWorkbook(new File(filePath));    //读取XLS文件
			Sheet sheet = workBook.getSheet(0);                              //获取工作空间
			int columns = sheet.getColumns();								 //获得列数
			int rows = sheet.getRows();                                      //获得行数
			for (int row = 1; row < rows; row++) {							 //循环读取XLS文件
				Element root = FileUtils.getRootElement(Constants.JckJournalXML);
				int col = 1;
				for (Iterator it = root.elementIterator();  it.hasNext() && col < columns ; col++) {
					Element element = (Element) it.next();
					Cell cell1 = sheet.getCell(col, row);
					String value = cell1.getContents();
					recordMap.put(element.attributeValue("name"), value);
				}
				listRecord.add(recordMap);
			}
			workBook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listRecord;
	}
	/**
	 * 解析文本文件
	 * @author chenshaoqi
	 * @param filePath 待解析文件的路径
	 * @return 返回存放记录的list
	 */
	@SuppressWarnings("unchecked")
	public List TxtReader(String filePath) {
		List listRecord= new ArrayList();
		String str = "";
		String[] array= null;
		Element root = FileUtils.getRootElement(Constants.JckJournalXML);  					 	//读取XML文件
		try {
			logger.info("Parse Txt file===============================>Start!" );
			BufferedReader bufReader =new BufferedReader(new FileReader(filePath));     //创建文件缓存
			String record  = bufReader.readLine();										//读取TXT文件		 	
			while(record != null){                                                      //循环读取文件
				str = record;
			    int i =0;
				array = str .split("\\|");												 //字符串按照 "|"分割成数组；
				Map recordMap = new HashMap();
				for (Iterator it = root.elementIterator();  it.hasNext() &&i< array.length ; i++) {
					Element element = (Element) it.next();								 //遍历数组，将值存入Map对象中
					String value = array[i];											 //取出数组的值
					recordMap.put(element.attributeValue("name"), value.trim());         //去空格
				}
				listRecord.add(recordMap);                                               //Map对象放入返回对象List中
				record = bufReader.readLine();                                           //继续读取下一行
			}
			bufReader.close();
			logger.info("Parse Txt file===============================>Successful!" );
		} catch (Exception e) {
			logger.error("Parse Txt file Failed=======================>"+e.getMessage() );
			listRecord = null;           												//如发生异常返回空List
		}
		return listRecord;																//返回list结果集
	}
	
	/**
	 * 解析银联对账文件
	 * @author xujin
	 * @param filePath 待解析文件的路径
	 * @return 返回存放记录的list
	 */
	@SuppressWarnings("unchecked")
	public List YLQZAuditJournalReader(String filePath,String fileName) {
		List listRecord= new ArrayList();
		String flag=null;
		Element root = FileUtils.getRootElement(Constants.YLQZJournalXML);  //读取XML文件
		try {
			logger.info("Parse YLQZ Audit file===============================>Start!" );
			BufferedReader bufReader =new BufferedReader(new FileReader(filePath));     //创建文件缓存
			String record  = bufReader.readLine();										//读取TXT文件		 	
			while(record != null){                                                      //循环读取文件
			    int i =0;
				Map recordMap = new HashMap();
				int fieldLength;
				int count = 0;
				String value = Constants.PE_NULL;	//临时变量
				for (Iterator it = root.elementIterator();  it.hasNext();) {
					Element element = (Element) it.next();								 //遍历数组，将值存入Map对象中
					String formatValue=element.attributeValue(Constants.FORMAT);	
					fieldLength = Integer.parseInt(element.attributeValue(Constants.LENGTH));//域值的长度
					int digitLength = Integer.parseInt(element.attributeValue(Constants.DIGIT));//小数点的长度
					/**
					 * formatValue的意思：<ALL:所有文件,COM:文件名称有COM的所有文件,
					 * TFL:文件名称有TFL的所有文件,
					 * ACOMATFL:ACOM和ATFL两个文件,ICOMIOTFL:ICOM、ITFL和OTFL三个文件>
					 */
					if(formatValue.equals("ALL")){
						value =record.substring(count, count+fieldLength);//在报文中取出
						value = value.trim();
						if(digitLength!=0){
							value=value.replace("C", "").replace("D", "");
						    value = String.valueOf(Util.getAmount(value).divide(new BigDecimal(digitLength))); 
						}
						count = count + fieldLength+1; 
						recordMap.put(element.attributeValue("name"),value); 
						++i;
					}else if(formatValue.equals("COM")&&fileName.indexOf("COM")!=-1){
						value =record.substring(count, count+fieldLength);//在报文中取出
						value = value.trim();
						if(digitLength!=0){
							value=value.replace("C", "").replace("D", "");
							value = String.valueOf(Util.getAmount(value).divide(new BigDecimal(digitLength))); 
						}
						count = count + fieldLength+1; 
						recordMap.put(element.attributeValue("name"),value); 
						++i;
					}else if(formatValue.equals("TFL")&&fileName.indexOf("TFL")!=-1){
						value =record.substring(count, count+fieldLength);//在报文中取出
						value = value.trim();
						if(digitLength!=0){
							value=value.replace("C", "").replace("D", "");
							value = String.valueOf(Util.getAmount(value).divide(new BigDecimal(digitLength))); 
						}
						count = count + fieldLength+1; 
						recordMap.put(element.attributeValue("name"),value);
						++i;
					}else if(formatValue.equals("ACOMATFL")&&(fileName.indexOf("ATFL")!=-1||fileName.indexOf("ACOM")!=-1)){
						value =record.substring(count, count+fieldLength);//在报文中取出
						value = value.trim();
						if(digitLength!=0){
							value=value.replace("C", "").replace("D", "");
							value = String.valueOf(Util.getAmount(value).divide(new BigDecimal(digitLength)));
						}
						count = count + fieldLength+1; 
						recordMap.put(element.attributeValue("name"),value); 
						++i;
					}else if(formatValue.equals("ICOMIOTFL")&&(fileName.indexOf("ICOM")!=-1||fileName.indexOf("OTFL")!=-1||
							fileName.indexOf("ITFL")!=-1)){
						value =record.substring(count, count+fieldLength);//在报文中取出
						value = value.trim();
						if(digitLength!=0){
							value=value.replace("C", "").replace("D", "");
							value = String.valueOf(Util.getAmount(value).divide(new BigDecimal(digitLength)));
						}
						count = count + fieldLength+1; 
						recordMap.put(element.attributeValue("name"),value);
						++i;
					}
				}
				/**
				 * 以下信息主要用在对账
				 * flag用法:第1,2位渠道号<域60.2.5>，
				 *          第3位交易平台<3:银联>，
				 *          第4位交易类别<1:他带本,2:本代它>
				 * CACC：达商行号94002710本代它
				 */
				flag=(String)recordMap.get("TTPY");
				flag+=Constants.PE_THREE;
				if(fileName.indexOf("ATFL")!=-1||fileName.indexOf("ACOM")!=-1){
					flag+=Constants.PE_TWO;
					recordMap.put("CACC","94002710");
				}else{
					flag+=Constants.PE_ONE;
				}
				recordMap.put("FLG",flag);
				listRecord.add(recordMap);                                               //Map对象放入返回对象List中
				record = bufReader.readLine();                                           //继续读取下一行
				flag=null;
			}
			bufReader.close();
			logger.info("Parse YLQZ Audit file===============================>Successful!" );
		} catch (Exception e) {
			logger.error("Parse YLQZ Audit file Failed=======================>"+e.getMessage() );
			listRecord = null;           												//如发生异常返回空List
		}
		return listRecord;																//返回list结果集
	}
	/**
	 * 解析银联品牌服务费文件
	 * @author xujin
	 * @param filePath 待解析文件的路径
	 * @return 返回存放记录的list
	 */
	@SuppressWarnings("unchecked")
	public List YLQZLFeeJournalReader(String filePath,String fileName) {
		List listRecord= new ArrayList();
		String flag=null;
		Element root = FileUtils.getRootElement(Constants.YLQZLFeeJournalXML);  //读取XML文件
		try {
			logger.info("Parse YLQZ Fee file===============================>Start!" );
			BufferedReader bufReader =new BufferedReader(new FileReader(filePath));     //创建文件缓存
			String record  = bufReader.readLine();										//读取TXT文件		 	
			while(record != null){                                                      //循环读取文件
			    int i =0;
				Map recordMap = new HashMap();
				int fieldLength;
				int count = 0;
				String value = Constants.PE_NULL;	//临时变量
				for (Iterator it = root.elementIterator();  it.hasNext();) {
					Element element = (Element) it.next();								 //遍历数组，将值存入Map对象中
					String formatValue=element.attributeValue(Constants.FORMAT);	
					fieldLength = Integer.parseInt(element.attributeValue(Constants.LENGTH));//域值的长度
					int digitLength = Integer.parseInt(element.attributeValue(Constants.DIGIT));//小数点的长度
					/**
					 * formatValue的意思：<ALL:所有文件,ALFEE:文件名称有ALFEE的所有文件,
					 * ILFEE:文件名称有ILFEE的所有文件，
					 */
					if(formatValue.equals("ALL")){
						value =record.substring(count, count+fieldLength);//在报文中取出
						value = value.trim();
						if(digitLength!=0){
							value=value.replace("C", "").replace("D", "");
						    value = String.valueOf(Util.getAmount(value).divide(new BigDecimal(digitLength))); 
						}
						count = count + fieldLength+1; 
						recordMap.put(element.attributeValue("name"),value); 
						++i;
					}else if(formatValue.equals("ALFEE")&&fileName.indexOf("ALFEE")!=-1){
						value =record.substring(count, count+fieldLength);//在报文中取出
						value = value.trim();
						if(digitLength!=0){
							value=value.replace("C", "").replace("D", "");
							value = String.valueOf(Util.getAmount(value).divide(new BigDecimal(digitLength)));
						}
						count = count + fieldLength+1; 
						recordMap.put(element.attributeValue("name"),value); 
						++i;
					}else if(formatValue.equals("ILFEE")&&fileName.indexOf("ILFEE")!=-1){
						value =record.substring(count, count+fieldLength);//在报文中取出
						value = value.trim();
						if(digitLength!=0){
							value=value.replace("C", "").replace("D", "");
							value = String.valueOf(Util.getAmount(value).divide(new BigDecimal(digitLength)));
						}
						count = count + fieldLength+1; 
						recordMap.put(element.attributeValue("name"),value);
						++i;
					}
				}
				
				/**
				 * 以下信息主要用在对账
				 * flag用法:第1,2位渠道号<域60.2.5>，
				 *          第3位交易平台<3:银联>，
				 *          第4位交易类别<1:他带本,2:本代它>
				 * CACC：达商行号94002710本代它
				 * CHKFLG：1表示不参与对账
				 */
				flag=Constants.PE_OK;
				flag+=Constants.PE_THREE;
				if(fileName.indexOf("ALFEE")!=-1){
					flag+=Constants.PE_TWO;
					recordMap.put("CACC","94002710");
				}else{
					flag+=Constants.PE_ONE;
				}
				recordMap.put("FLG",flag);
				recordMap.put("CHKFLG","1");			
				listRecord.add(recordMap);                                               //Map对象放入返回对象List中
				record = bufReader.readLine();                                           //继续读取下一行
				flag=null;
			}
			bufReader.close();
			logger.info("Parse YLQZ Fee file===============================>Successful!" );
		} catch (Exception e) {
			logger.error("Parse YLQZ Fee file Failed=======================>"+e.getMessage() );
			listRecord = null;           												//如发生异常返回空List
		}
		return listRecord;																//返回list结果集
	}
	
	/**
	 * 解析银联数据对账文件
	 * @author xujin
	 * @param filePath 待解析文件的路径
	 * @return 返回存放记录的list
	 */
	@SuppressWarnings("unchecked")
	public List YLSJAuditJournalReader(String filePath,String fileName) {
		List listRecord= new ArrayList();
		String flag=null;
		Element root = FileUtils.getRootElement(Constants.YLSJJournalXML);  //读取XML文件
		try {
			logger.info("Parse YLSJ Audit file===============================>Start!" );
			BufferedReader bufReader =new BufferedReader(new FileReader(filePath));     //创建文件缓存
			String record  = bufReader.readLine();										//读取TXT文件		 	
			while(record != null){                                                      //循环读取文件
				int recordLength=record.length();
				Map recordMap = new HashMap();
				int fieldLength=0;
				int beginIndex = 0;
				String value = Constants.PE_NULL;	//临时变量
				for (Iterator it = root.elementIterator();  it.hasNext();) {
					Element element = (Element) it.next();								 //遍历数组，将值存入Map对象中
					fieldLength = Integer.parseInt(element.attributeValue(Constants.LENGTH));//域值的长度
					int digitLength = Integer.parseInt(element.attributeValue(Constants.DIGIT));//小数点的长度
					int endIndex=(beginIndex+fieldLength)>recordLength?recordLength:beginIndex+fieldLength;
					value =record.substring(beginIndex, endIndex);//在报文中取出
					value = value.trim();
					if(digitLength!=0){
					    value = String.valueOf(Util.getAmount(value).divide(new BigDecimal(digitLength))); 
					}
					beginIndex = endIndex; 
					recordMap.put(element.attributeValue("name"),value); 
				}
				listRecord.add(recordMap);                                               //Map对象放入返回对象List中
				record = bufReader.readLine();                                           //继续读取下一行
				flag=null;
			}
			bufReader.close();
			logger.info("Parse YLSJ Audit file===============================>Successful!" );
		} catch (Exception e) {
			logger.error("Parse YLSJ Audit file Failed=======================>"+e.getMessage() );
			listRecord = null;           												//如发生异常返回空List
		}
		return listRecord;																//返回list结果集
	}
	
	/**
	 * 解析银联文本文件
	 * @author xujin
	 * @param filePath 待解析文件的路径
	 * @return 返回存放记录的list
	 */
	@SuppressWarnings("unchecked")
	public List YLQZTxtReader(String filePath,String fileName) {
		if(fileName.indexOf("LFEE")>-1)
			return YLQZLFeeJournalReader(filePath,fileName);
		else
		    return YLQZAuditJournalReader(filePath,fileName);
	}
	
	/**
	 * 解析银联数据文本文件
	 * @author xujin
	 * @param filePath 待解析文件的路径
	 * @return 返回存放记录的list
	 */
	@SuppressWarnings("unchecked")
	public List YLSJTxtReader(String filePath,String fileName) {
		return YLSJAuditJournalReader(filePath,fileName);
	}
	/**
	 * 将数据导入数据库
	 * @author chenshq
	 * @param listRecord
	 * @return 执行结果
	 */
	@SuppressWarnings("unchecked")
	public boolean importDB(final List listRecord,final String postDate){
		final Map flag = new HashMap();
		flag.put("flag","true");     							//存放是否成功的状态
		logger.info("import Audit data from txt to DataBase===============================>Start!" );
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				Map paramMap = new HashMap();
				for(Iterator iterator = listRecord.iterator();iterator.hasNext();){	    //遍历参数List 将数据插入对账流水表中
					try {
						paramMap = (Map) iterator.next();         //获得插入数据
						paramMap.put(Constants.PE_POST_DATE, postDate);
						dzipdbAccess.getSqlMap().insert("Audit.importJournal", paramMap);
					} catch (Exception e) {
						flag.put("flag","fasle");				//发送异常，返回false
						e.printStackTrace();
					}
				}
					return null;
			}
		});
		return Boolean.valueOf(flag.get("flag").toString());    //返回是否成功
	}
	
	/**
	 * 将银联数据导入IC卡银联数据交易流水表
	 * @author xujin
	 * @param listRecord
	 * @return 执行结果
	 */
	@SuppressWarnings("unchecked")
	public boolean importCUPSDJournal(final List listRecord,final String postDate){
		final Map flag = new HashMap();
		flag.put("flag","true");     							//存放是否成功的状态
		logger.info("import YLSJ Audit data from txt to T_CUPSDJOURNAL===============================>Start!" );
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				Map paramMap = new HashMap();
				for(Iterator iterator = listRecord.iterator();iterator.hasNext();){	    //遍历参数List 将数据插入对账流水表中
					try {
						paramMap = (Map) iterator.next();         //获得插入数据
						paramMap.put(Constants.PE_POST_DATE, postDate);
						dzipdbAccess.getSqlMap().insert("Audit.importCUPSDJournal", paramMap);
					} catch (Exception e) {
						flag.put("flag","fasle");				//发送异常，返回false
						e.printStackTrace();
					}
				}
					return null;
			}
		});
		return Boolean.valueOf(flag.get("flag").toString());    //返回是否成功
	}
	
	/**
	 * @author xujin
	 * @date 2011.12.02
	 * @desc 将导入的文件流水信息从渠道前置流水表删除
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteDB(final Map paramMap) {
		final Map flag = new HashMap();
		flag.put("flag","true");     //存放是否成功的状态
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
					try {
						dzipdbAccess.getSqlMap().delete("Audit.deleteJournal", paramMap);
					} catch (DataAccessException e) {
						flag.put("flag","false");     //发生异常，返回false
					}
				return null;
			}
		});
		return Boolean.valueOf(flag.get("flag").toString());       //返回是否成功
	}
	
	/**
	 * @author xujin
	 * @date 2013.09.03
	 * @desc 将导入的文件流水信息从IC卡银联数据交易流水删除
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteCUPSDJournal(final Map paramMap) {
		final Map flag = new HashMap();
		flag.put("flag","true");     //存放是否成功的状态
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
					try {
						dzipdbAccess.getSqlMap().delete("Audit.deleteCUPSDJournal", paramMap);
					} catch (DataAccessException e) {
						flag.put("flag","false");     //发生异常，返回false
					}
				return null;
			}
		});
		return Boolean.valueOf(flag.get("flag").toString());       //返回是否成功
	}
	
	/**
	 * 导出核心流水
	 * @author chenshq
	 * @param paramMap
	 * @return 流水记录
	 */
	@SuppressWarnings("unchecked")
	public List exportCoreDB(final Map paramMap){
		List listRe = new ArrayList();
		corebankAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				logger.info("get transaction joural===============================>Start!" );
				corebankAccess.getSqlMap().queryForObject("Audit.getRTXN", paramMap);
				return null;
			}
		});
		if (Constants.PE_ZERO.equals(String.valueOf(paramMap.get(Constants.OUT_ERROR_NBR)))) {
			listRe = (List) paramMap.get(Constants.OUT_REFCURSOR);     				//获得结果集
		}
		else
			listRe =null;															//发生异常返回null
		return listRe;    															//返回结果集
	}
	
	/**
	 * 导入核心流水
	 * @author chenshq
	 * @param listRecord
	 * @return 执行结果
	 */
	@SuppressWarnings("unchecked")
	public boolean importCoreToDB(final List listRecord){
		final Map flag = new HashMap();
		flag.put("flag","true");   									//存放是否成功标识
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				Map paramMap = new HashMap();
				logger.info("import transaction joural to DataBase ==================>Start!" );
				for(Iterator iterator = listRecord.iterator();iterator.hasNext();){		//遍历参数List 将数据插入对账表中
					try {
						paramMap = (Map) iterator.next(); 		//获得插入数据
						dzipdbAccess.getSqlMap().insert("Audit.importRTXN", paramMap);
					} catch (Exception e) {
						flag.put("flag","fasle");					//发生异常返回false
						e.printStackTrace();
					}
				};
				return null;										
			}
		});
		return Boolean.valueOf(flag.get("flag").toString());        //返回是否成功
	}
	/**
	 * 核对前置流水与核心流水
	 * @author xujin
	 * @param paramMap
	 * @return 执行结果
	 */
	@SuppressWarnings("unchecked")
	public Map cmpCoreToPE(final Map paramMap){
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				logger.info(" procedure Compare Core And PE================================>Start!" );
				if(Constants.TASKTYPE_YLSJ.equals((String)paramMap.get(Constants.TASKTYP))){
					dzipdbAccess.getSqlMap().insert("Audit.Check_CoreAndJCKTranForIC", paramMap);
				}else{
					dzipdbAccess.getSqlMap().insert("Audit.Check_CoreAndJCK", paramMap);
				}
				return null;
			}
		});
		return paramMap; //返回执行结果
	}
	/**
	 * 如果是银联对账就核对前置流水与渠道流水，否则是银联数据对账就核对前置流水与银联数据流水
	 * @author xujin
	 * @param paramMap
	 * @return 执行结果
	 */
	@SuppressWarnings("unchecked")
	public Map cmpPEToChannel(final Map paramMap) {
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				logger.info("procedure Compare PE And Channel================================>Start!");
				if(Constants.TASKTYPE_YLSJ.equals((String)paramMap.get(Constants.TASKTYP))){
					dzipdbAccess.getSqlMap().insert("Audit.Add_CheckTransRecForIC", paramMap);
				}else{
					dzipdbAccess.getSqlMap().insert("Audit.Add_CheckTransRec", paramMap);
				}
				return null;
			}
		});
		return paramMap; 																		//返回执行结果
	}
	/**
	 *获得对账不一致列表
	 * @author chenshq
	 * @param paramMap
	 * @return 执行结果
	 */
	@SuppressWarnings("unchecked")
	public Map getCheckTransRec(final Map paramMap) {
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				logger.info("procedure GetCheckTransRec===============================>Start!" );
				dzipdbAccess.getSqlMap().queryForObject("Audit.GetCheckTransRec", paramMap);   //调用存储过程
				return null;
			}
		});
		return paramMap;
	}
	/**
	 *记录账户冻结信息
	 * @author chenshq
	 * @param paramMap
	 * @return 执行结果
	 */
	@SuppressWarnings("unchecked")
	public void add_AcctAcctHold(final List recordList) {
		corebankAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				logger.info("procedure Add_AcctAcctHold===============================>Start!" );
				Map paramMap = new HashMap();
				for(Iterator iterator = recordList.iterator();iterator.hasNext();){
					paramMap= (Map)iterator.next();
					corebankAccess.getSqlMap().queryForObject("Audit.Add_AcctAcctHold", paramMap);   //调用存储过程
				}
				return null;
			}
		});
	}
	/**
	 * 验证任务是否可以执行
	 * @author chenshq
	 * @param paramMap
	 * @return 执行结果
	 * @throws PeException 
	 */
	@SuppressWarnings("unchecked")
	public Map checkTaskExeYN(Map paramMap) throws PeException{
		boolean flag = false;
		try {
			logger.info("procedure checkTaskExeYN ===============================>Start!" );
			paramMap=(Map)dzipdbAccess.getSqlMap().queryForObject("Audit.queryTaskIsvalidate", paramMap);
//			logger.debug("checkTaskExeYN result =========================>"+paramMap );
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage().contains("Could not get JDBC Connection"))
				throw new PeException(Errors.COULD_NOT_GET_JDB_C__CONNECTION);
			else
				throw new PeException(Errors.DATEBASE_ERROR);
				
		}
		if(paramMap.get(Constants.PRIVTASKSTAT)!=null&&(paramMap.get(Constants.PRIVTASKSTAT).equals(Constants.STAT_OK))    //上个任务状态不为空且为成功
				   &&( paramMap.get(Constants.TASKEXECYN)!=null &&paramMap.get(Constants.TASKEXECYN).equals(Constants.EXEC_Y))//本任务执行状态不为空且是可执行状态
				   &&( paramMap.get(Constants.TASKSTAT)!=null&&!paramMap.get(Constants.TASKSTAT).equals(Constants.STAT_OK) ))//本任务的状态是非且非成功状态
					flag = true;							
		paramMap.put(Constants.FLAG, flag);
		return paramMap;      																	//返回是否成功
	}
	/**
	 * 查询任务状态
	 * @author chenshq
	 * @param listRecord
	 * @return listRecord
	 * @throws PeException 
	 */
	@SuppressWarnings("unchecked")
	public List queryTaskStatus( Map paramMap) throws PeException{
		List listRecord = new ArrayList();
		try {
			logger.info("procedure queryTaskStatus ===============================>Start!" );
			listRecord=	dzipdbAccess.getSqlMap().queryForList("Audit.queryTaskStatus", paramMap);
		} catch (Exception e) {
			if(e.getMessage().contains("Could not get JDBC Connection"))
				throw new PeException(Errors.COULD_NOT_GET_JDB_C__CONNECTION);
			else
				throw new PeException(Errors.DATEBASE_ERROR);
		}
		return listRecord;      			//返回结果集
	}
	/**
	 *查询对账文件信息
	 * @author chenshq
	 * @param listRecord
	 * @return listRecord
	 * @throws PeException 
	 */
	@SuppressWarnings("unchecked")
	public List queryFileInfo(Map paramMap) throws PeException{
		List listRecord = new ArrayList();
		logger.info("procedure queryFileInfo ==================>Start!" );
		try {
			listRecord=	dzipdbAccess.getSqlMap().queryForList("Audit.queryFileInfo", paramMap);  
		} catch (Exception e) {
			if(e.getMessage().contains("Could not get JDBC Connection"))
				throw new PeException(Errors.COULD_NOT_GET_JDB_C__CONNECTION);
			else
				throw new PeException(Errors.DATEBASE_ERROR);
		}
		return listRecord;       //返回结果集
	}
	
	/**
	 *获得需要下载的对账文件
	 * @author xujin
	 * @return listRecord
	 * @throws PeException 
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryCupsAuditFile(String taskType) throws PeException{
		List<Map<String, Object>> listRecord =  new ArrayList<Map<String, Object>>();
		logger.info("procedure queryCupsAuditFile ==================>Start!" );
		try {
			listRecord=	dzipdbAccess.getSqlMap().queryForList("Audit.queryCupsAuditFile",taskType);  
		} catch (Exception e) {
			if(e.getMessage().contains("Could not get JDBC Connection"))
				throw new PeException(Errors.COULD_NOT_GET_JDB_C__CONNECTION);
			else
				throw new PeException(Errors.DATEBASE_ERROR);
		}
		return listRecord;       //返回结果集
	}
	/**
	 *查询对账信息
	 * @author chenshq
	 * @param listRecord
	 * @return listRecord
	 * @throws PeException 
	 */
	@SuppressWarnings("unchecked")
	public Map queryAuditInfo(final Map paramMap) throws PeException {
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				if(Constants.TASKTYPE_YLSJ.equals((String)paramMap.get(Constants.TASKTYP))){
					dzipdbAccess.getSqlMap().queryForList("Audit.queryIcJournalInfo", paramMap);
				}else{
					dzipdbAccess.getSqlMap().queryForList("Audit.queryAuditInfo", paramMap);
				}
				return null;
			}
		});
		return paramMap; // 返回结果集
	}
	
	/**
	 *查询核心账务日期
	 * @author chenshq
	 * @param listRecord
	 * @return String PostDate
	 */
	@SuppressWarnings("unchecked")
	public Map getCorePostDate(final Map paramMap){
		logger.info("procedure getCorePostDate ==================>Start!" );
		corebankAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					corebankAccess.getSqlMap().queryForList("DaySwitch.getCorePostDate", paramMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		return paramMap;       //返回结果集
	}
	/**
	 *日切
	 * @author chenshq
	 * @param listRecord
	 * @return String PostDate
	 */
	@SuppressWarnings("unchecked")
	public Map daySwitch(final Map paramMap){
		logger.info("procedure dayEndTask ==================>Start!" );
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					dzipdbAccess.getSqlMap().queryForList("DaySwitch.dayEndTask", paramMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		return paramMap;       //返回结果集
	}
	/**
	 *查询平台账务日期
	 * @author chenshq
	 * @param listRecord
	 * @return String PostDate
	 * @throws PeException 
	 */
	@SuppressWarnings("unchecked")
	public Map getPEPostDate(Map paramMap) throws PeException{
		logger.info("procedure getPEPostDate ==================>Start!");
		try {
			paramMap = (Map) dzipdbAccess.getSqlMap().queryForObject("DaySwitch.querySysstat");
		} catch (Exception e) {
			if (e.getMessage().contains("Could not get JDBC Connection"))
				throw new PeException(Errors.COULD_NOT_GET_JDB_C__CONNECTION);
			else
				throw new PeException(Errors.DATEBASE_ERROR);
		}
		return paramMap; // 返回结果集
	}
	/**
	 *联机数据清理
	 * @author chenshq
	 * @param listRecord
	 * @return String PostDate
	 */
	@SuppressWarnings("unchecked")
	public Map clearOnlineJournal(final Map paramMap){
		logger.info("procedure clearOnlineJournal ==================>Start!" );
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					dzipdbAccess.getSqlMap().update("DaySwitch.clear_Online_Journal",paramMap );
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		return paramMap;       //返回结果集
	}
	
	/**
	 *历史数据清理
	 * @author xujin
	 * @param listRecord
	 * @return String PostDate
	 */
	@SuppressWarnings("unchecked")
	public Map clearHistoryJournal(final Map paramMap){
		logger.info("procedure clearHistoryJournal ==================>Start!" );
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					dzipdbAccess.getSqlMap().update("DaySwitch.clear_History_Journal",paramMap );
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		return paramMap;       //返回结果集
	}
	
	
	/**
	 * 查数据库配置的参数值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String queryParam(String paramName){
		Map map = (Map) dzipdbAccess.getSqlMap().queryForObject("common.queryParam",paramName);
		String paramValue = (String) map.get(Constants.PEARAMVALUE); //获得参数的值
		return paramValue;
	}
	
	/**
	 *流水表导出到批量流水表
	 * @author xujin
	 * @param listRecord
	 * @return int ErrorNbr
	 */
	@SuppressWarnings("unchecked")
	public Map ImportJournalBatch(final Map paramMap){
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					dzipdbAccess.getSqlMap().update("platformAdmin.ImportJournalBatch",paramMap );
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		return paramMap;       //返回结果集
	}
	
	/**
	 * 更新最大日期时间到bankoption表
	 * @author xujin
	 * @param  maxDateTime
	 */
	public void UnloadChalTranMaxTime(final String maxdatetime){
		dzipdbAccess.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					corebankAccess.getSqlMap().update("common.UnloadChalTranMaxTime",maxdatetime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	/**
	 * @desc 获的批量导出数据的最大日期时间

	 * @return  执行标记
	 */
	public String  getUnloadChalTranMaxTime() {
		String maxdatetime=null;
		try {
			maxdatetime =(String) corebankAccess.getSqlMap().queryForObject("common.getUCTMValue"); // 调用相关的SQL
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
        return maxdatetime;
	}
	
	public static void main(String[] args) throws Exception {
		AuditProcessor test = new AuditProcessor();
//		AuditProcessor.parseAuditTaskList();
		test.TxtReader("94002710MX0907");
	}
	
    /**
     * 获得对帐文件路径
     * @param auditType
     * @return
     */
    public String getLocalFilePath(String auditType) {
		String filePath = "";
		if (auditType.equals(Constants.AUDIT_JCK)){
			filePath = ftpForJck.getLocalPath();
		}else if(auditType.equals(Constants.AUDIT_YLQZ)){
			filePath = ftpForYlqz.getLocalPath();
		}else if(auditType.equals(Constants.AUDIT_YLSJ)){
			filePath = ftpForYlsj.getLocalPath();
		}
		return filePath;
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
	 * @return the corebankAccess
	 */
	public JdbcAccessAwareProcessor getCorebankAccess() {
		return corebankAccess;
	}
	/**
	 * @param corebankAccess the corebankAccess to set
	 */
	public void setCorebankAccess(JdbcAccessAwareProcessor corebankAccess) {
		this.corebankAccess = corebankAccess;
	}
	/**
	 * @return the ftpForJck
	 */
	public Ftp getFtpForJck() {
		return ftpForJck;
	}
	/**
	 * @param ftpForJck the ftpForJck to set
	 */
	public void setFtpForJck(Ftp ftpForJck) {
		this.ftpForJck = ftpForJck;
	}
	/**
	 * @return the ftpForYlqz
	 */
	public Ftp getFtpForYlqz() {
		return ftpForYlqz;
	}
	/**
	 * @param ftpForYlqz the ftpForYlqz to set
	 */
	public void setFtpForYlqz(Ftp ftpForYlqz) {
		this.ftpForYlqz = ftpForYlqz;
	}
}
