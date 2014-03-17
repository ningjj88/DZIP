package csii.dzip.action.scheduler;


import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.csii.pe.core.Context;
import com.csii.pe.core.PeException;

import csii.base.action.util.Util;
import csii.base.constant.Constants;
import csii.dzip.action.DzipBaseAction;
import csii.dzip.action.util.CollectData;
import csii.dzip.action.util.DzipProcessTemplate;
import csii.dzip.action.util.FileOperate;
import csii.dzip.core.InitData4Dzip;

public class SchedulerAction extends DzipBaseAction {
	private DzipProcessTemplate dzipProcessTemplate;
	private CollectData collectData;
	private String localFilepath;
	private String tmpFilepath;
	public void execute(final Context ctx) throws PeException {
//		String postdate;
//		String prePostDate;
//		try {
//			postdate = dzipProcessTemplate.queryPostDate();
//			String strFile=dzipProcessTemplate.queryParam("CupsAuditFileType");
//			String localFileName =null;
//			for(int i=-1;i>-6;i--){
//				Date date = Util.getDate(postdate, "yyyyMMdd") ;
//				Calendar c = Calendar.getInstance();
//				c.setTime(date);
//				c.add(Calendar.DATE, i);
//				Date matdate = c.getTime();
//				prePostDate=Util.getDateString(matdate,"yyyyMMdd");
//				localFileName = localFilepath +"INOYYYYMMDDXSUMCN.txt";
//				if(strFile.lastIndexOf(Constants.PE_YYYYMMDDX)>-1)
//					localFileName = localFileName.replace(Constants.PE_YYYYMMDDX,prePostDate+InitData4Dzip.getAcqOrgCd());
//				else
//					localFileName = localFileName.replace(Constants.PE_YYYYMMDDX,Util.getFormatString(prePostDate,"yyMMdd")+InitData4Dzip.getAcqOrgCd());		
//				//collectData.excute(localFileName,postdate);
//				prePostDate=prePostDate+"/";
//				try {
//					collectData.uploadFile("2_银联清算通知单",localFileName,prePostDate);
//				}catch (Exception e) {
//					// TODO Auto-generated catch block
//					logger.error("上传银联清算通知单失败,日期:"+postdate+"==>"+e.getMessage());
//				}
//			}
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		try {
			FileOperate.delAllFile(tmpFilepath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("删除临时文件失败=====>"+e.getMessage());
		}
	}
	
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}

	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
	
	public CollectData getCollectData() {
		return collectData;
	}
	public void setCollectData(CollectData collectData) {
		this.collectData = collectData;
	}
	
	public String getLocalFilepath() {
		return localFilepath;
	}

	public void setLocalFilepath(String localFilepath) {
		this.localFilepath = localFilepath;
	}
	
	public String getTmpFilepath() {
		return tmpFilepath;
	}

	public void setTmpFilepath(String tmpFilepath) {
		this.tmpFilepath = tmpFilepath;
	}
	
}
