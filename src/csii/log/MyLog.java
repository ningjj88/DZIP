/*
 * MyLog.java
 *
 * Created on 2007年7月11日, 上午10:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package csii.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import csii.log.customlog.CustomFileStreamHandler;
import csii.log.customlog.CustomFormatter;

/** */
/**
 * 日记记录器
 * 
 * @author twbcb
 */
public class MyLog {


	private String fileUrl;

	static {

	}

	/** */
	/**
	 * Creates a new instance of MyLog
	 */
	private MyLog() {

	}

	/** */
	/**
	 * 返回一个文件记录实例
	 */
	// public static synchronized Logger getFileLogger() {
	// return fileLogger;
	// }
	public void log(String logstr) {
		Logger fileLogger;
		fileLogger = Logger.getLogger("csii.log.customlog");
		fileLogger.setLevel(Level.INFO);
		Handler[] hs = fileLogger.getHandlers();
		for (Handler h : hs) {
			h.close();
			fileLogger.removeHandler(h);
		}
		try {
			// 文件 日志文件名为mylog 日志最大写入为4000个字节 保存5天内的日志文件
			// 如果文件没有达到规定大小则将日志文件添加到已有文件
			CustomFileStreamHandler fh = new CustomFileStreamHandler(
					getFileUrl(), 4000, 5, true);
			// CustomFileStreamHandler fh = new
			// CustomFileStreamHandler("d:\\log\\mylog\\", 1024, 5, true); //目录
			fh.setEncoding("UTF-8");
			fh.setFormatter(new CustomFormatter());
			fileLogger.setUseParentHandlers(false);
			fileLogger.addHandler(fh);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		fileLogger.log(Level.INFO, logstr);
	}

	 public static void main(String[] args) {
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          String trace = sdf.format(new Date().getTime());
          System.out.println(trace);
	 }
	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
}
