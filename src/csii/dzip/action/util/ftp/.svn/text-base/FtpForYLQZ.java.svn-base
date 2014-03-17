package csii.dzip.action.util.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.net.TelnetInputStream;
import sun.net.ftp.FtpClient;

public class FtpForYLQZ {
	private String localPath;          //下载文件存放目录
	private String remotePath;         //ftp服务器的文件目录
	private String ftpServerIP;        //ftp服务器地址
	private int fetpServerPort;        //ftp通信端口号
	private String userName;           //用户名
	private String passWord;           //密码
	private FtpClient ftpClient;
	protected final Log logger = LogFactory.getLog(getClass());
	protected final int ARRYSIZE =1024; //定义数组的大小


	/***
	 * 建立与银联前置的FTP服务器连接
	 * @author chenshaoqi
	 * @param ip 服务器地址
	 * @param port 通信端口号
	 * @param user 用户名
	 * @param password 密码
	 * @param path  服务器的文件目录
	 */
	public void connectServer() {
		try {
			ftpClient = new FtpClient(); 									 //创建对象
			logger.info("ftpServerIP===================>" + ftpServerIP);
			logger.info("fetpServerPort================>" + fetpServerPort);
			ftpClient.openServer(ftpServerIP, fetpServerPort);     			 //建立连接
			logger.info("...........................Connnect successful!");
			logger.info("userName===================>" + userName);
			logger.info("passWord===================>" + passWord);
			logger.info("...........................Start to login !");
			ftpClient.login(userName, passWord); 			 		 	     //登陆
			logger.info("...........................login successful!");
			if (remotePath.length() != 0){     	   
				ftpClient.cd(remotePath);       			                 //定位到文件存放目录
			}
			ftpClient.binary();  				                             //将传输格式设置位二进制
		} catch (IOException ex) {
			logger.info("...........................login failed!");
			logger.info(ex);
		}
	}
	
	/**
	 * Ftp建立连接后，从制定目录下载文件到本地目录
	 * @author chenshaoqi
	 * @param remoteFile 服务器上的文件名
	 * @param localFile  本地命名
	 */
	public boolean download(String remoteFile, String localFile) {
		this.connectServer();         										   //启动服务连接
		try {
			TelnetInputStream telnetInputStream = ftpClient.get(remoteFile);   //读取远程文件
			localFile = this.localPath +localFile; 				               //文件路径
			File fileLocal = new File(localFile);
			FileOutputStream outputStream = new FileOutputStream(fileLocal);   //建立本地文件
			byte[] bytes = new byte[ARRYSIZE];                                 //定义缓冲
			int count,fileSize = 0;    										   //定义计数器,文件大小统计
			logger.info("...........................Start to download !");
			while ((count = telnetInputStream.read(bytes)) != -1) {            //判断是否读完文件
//				 System.out.println((char)is.read());
//				 System.out.println(file_in);
				outputStream.write(bytes, 0, count);      					   //下载文件处理
				fileSize +=count;                         					   //计算文件的大小
			}
			logger.info("...........................download successful!");
			outputStream.close();                                              //关闭文件流
			telnetInputStream.close(); 										   //关闭文件流			
		} catch (IOException ex) {
			logger.info("...........................download failed!");
			logger.info(ex);
			return false;
		}
		this.closeConnect();												  //关闭FTP服务
		return true;
	}
	
	/**
	 * 关闭服务器连接
	 * @author chenshaoqi
	 */
	public void closeConnect() {
		try {
			if(ftpClient != null)
			ftpClient.closeServer();                                          //关闭FTP服务
		} catch (IOException ex) {
			logger.info("...........................disconnect failed!");
			logger.info(ex);
		}
	}
	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassWord() {
		return passWord;
	}


	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public String getFtpServerIP() {
		return ftpServerIP;
	}

	public void setFtpServerIP(String ftpServerIP) {
		this.ftpServerIP = ftpServerIP;
	}

	public int getFetpServerPort() {
		return fetpServerPort;
	}

	public void setFetpServerPort(int fetpServerPort) {
		this.fetpServerPort = fetpServerPort;
	}

}
