package csii.dzip.action.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.csii.pe.core.PeException;

import csii.base.constant.Constants;
import csii.dzip.action.util.ftp.Ftp;

/**
 * 
 * @author chenshq
 * @version 1.0.0
 * @since 2011-3-2
 */
public class CollectData {
	private DzipProcessTemplate dzipProcessTemplate;
	private Ftp ftpForCupsDetail;
	/**
	 * 
	 * @param paramMap
	 * @param path
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private synchronized Map geteData(String postDate) {
		Map paramMap = dzipProcessTemplate.getCheckDetail(postDate);   //获取明细数据
		if(paramMap!=null)
			return paramMap;
		else
			return null;
	}

	/**
	 * 
	 * @param paramMap
	 * @param path
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	private synchronized void writeDataToFile(Map paramMap,String localFileName) throws IOException {
		List listRe = new ArrayList();
		Map record = new LinkedHashMap();
		listRe = (List) paramMap.get(Constants.OUT_REFCURSOR);  //获得结果集
		FileOutputStream fileOutputStream1 = new FileOutputStream(localFileName , false); //根据时间创建文件
		for (Iterator it = listRe.iterator(); it.hasNext();) {  //遍历数据结果集
			StringBuffer data = new StringBuffer();				//定义变量，存放记录信息
			record = (Map) it.next();							//获取单条记录
			Iterator iterator = record.entrySet().iterator();	//获取迭代
			while (iterator.hasNext()) {						//循环取出记录中各列的数据
				Map.Entry entry = (Map.Entry) iterator.next();  //
				if (entry.getValue() != null)
					data.append(entry.getValue());				//获取数据并生成文本串
				else
					data.append("");							//如果数据为空
				data.append("|");								//用"|"分隔每个数据
			}
			try {
				fileOutputStream1.write((byte[]) data.toString().getBytes()); 	//将数据信息写入文件流中
				fileOutputStream1.write((byte[]) "\n".getBytes());			 	//写完每条数据后换行
				fileOutputStream1.flush();
			} catch (Exception localException1) {
				localException1.printStackTrace();
				break;
			}
			data = null;
		}
		fileOutputStream1.close();												//关闭输出流
	}
/**
 * 
 * @param path
 * @throws PeException 
 * @throws IOException
 */
	@SuppressWarnings("unchecked")
	public synchronized void excute(String localFileName,String postdate) throws PeException {
		Map paramMap = geteData(postdate);
			try {
				if(paramMap!=null)
					writeDataToFile(paramMap, localFileName);
			} catch (IOException e) {
				e.printStackTrace();
				throw new PeException("写入文件失败");
			}
	}
	/**
	 * 
	 * @param path
	 * @throws PeException 
	 * @throws IOException
	 */
		public synchronized void uploadFile(String remoteFileName, String localFileName) throws PeException {
			ftpForCupsDetail.upload(remoteFileName, localFileName);
		}
		
	/**
	 * 
	 * @param path
	 * @throws PeException 
	 * @throws IOException
	 */
	public synchronized void uploadFile(String remoteFileName, String localFileName,String postDate) throws PeException {
		ftpForCupsDetail.upload(remoteFileName, localFileName,postDate);
	}
	
	public DzipProcessTemplate getDzipProcessTemplate() {
		return dzipProcessTemplate;
	}

	public void setDzipProcessTemplate(DzipProcessTemplate dzipProcessTemplate) {
		this.dzipProcessTemplate = dzipProcessTemplate;
	}
	public Ftp getFtpForCupsDetail() {
		return ftpForCupsDetail;
	}

	public void setFtpForCupsDetail(Ftp ftpForCupsDetail) {
		this.ftpForCupsDetail = ftpForCupsDetail;
	}

}
