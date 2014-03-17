package csii.dzip.action.util.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.csii.pe.core.PeException;

import csii.base.constant.Constants;

public class Ftp {
	private String localPath;          //下载文件存放目录
	private String remotePath;         //ftp服务器的文件目录
	private String ftpServerIP;        //ftp服务器地址
	private int fetpServerPort;        //ftp通信端口号
	private String userName;           //用户名
	private String passWord;           //密码
	private FTPClient ftpClient;       //FTP服务对象


	protected final Log logger = LogFactory.getLog(getClass());
	protected final int ARRAYSIZE =1024; //定义数组的大小


	/***
	 * 建立与FTP服务器连接
	 * @author xujin
	 * @param ip 服务器地址
	 * @param port 通信端口号
	 * @param user 用户名
	 * @param password 密码
	 * @param path  服务器的文件目录
	 * @throws PeException 
	 */
	public void connectServer() throws PeException {
		try {
			ftpClient = this.getFtpClient(); 							    //创建对象
			logger.info("ftpServerIP===================>" + ftpServerIP);
			logger.info("fetpServerPort================>" + fetpServerPort);
			ftpClient.connect(ftpServerIP, fetpServerPort);     			//建立连接
			logger.info("...........................Connnect successful!");
			logger.info("userName===================>" + userName);
			logger.info("passWord===================>" + passWord);
			logger.info("...........................Start to login !");
			ftpClient.login(userName, passWord); 			 		 	    //登录
			int reply=ftpClient.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)){
				this.closeConnect();
				throw new PeException("Not logged in");
			}
			logger.debug("...........................login successful!");
			if (remotePath.length() != 0){
				ftpClient.changeWorkingDirectory(remotePath);               //定位到文件存放目录
			}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  			 //将传输格式设置位二进制
		} catch (IOException ex) {
			logger.info("...........................login failed!");
			logger.info(ex.getMessage());
			throw new PeException(ex.getMessage());
		}
	}
     
	/***
	 * 建立与FTP服务器连接
	 * @author xujin
	 * @param ip 服务器地址
	 * @param port 通信端口号
	 * @param user 用户名
	 * @param password 密码
	 * @param path  服务器的文件目录
	 * @throws PeException 
	 */
	public void connectServer(String postDate) throws PeException {
		try {
			ftpClient = this.getFtpClient(); 							    //创建对象
			logger.info("ftpServerIP===================>" + ftpServerIP);
			logger.info("fetpServerPort================>" + fetpServerPort);
			ftpClient.connect(ftpServerIP, fetpServerPort);     			//建立连接
			logger.info("...........................Connnect successful!");
			logger.info("userName===================>" + userName);
			logger.info("passWord===================>" + passWord);
			logger.info("...........................Start to login !");
			ftpClient.login(userName, passWord); 			 		 	    //登录
			int reply=ftpClient.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)){
				this.closeConnect();
				throw new PeException("Not logged in");
			}
			logger.debug("...........................login successful!");
			if (remotePath.length() != 0||postDate.length()!=0){
				String filePath=null;
				if(remotePath.length() != 0&&remotePath.endsWith("/")){
					filePath = remotePath+postDate; 
	            } 
	            else {
	            	filePath = remotePath+ "/"+postDate; 
	            } 
				if(!ftpClient.changeWorkingDirectory(filePath)){
					if(!createDir(filePath))
						throw new PeException("在当前目录下创建文件夹失败");
				}
			}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  			 //将传输格式设置位二进制		                            //将传输格式设置位二进制
		} catch (IOException ex) {
			logger.info("...........................login failed!");
			logger.info(ex.getMessage());
			throw new PeException(ex.getMessage());
		}
	}
	
	  /** 
     * 在当前目录下创建文件夹 
     * @author xujin
     * @param dir 
     * @return 
     * @throws Exception 
     */ 
     private boolean createDir(String dir){   
         try{   
        	 ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);  				  
            StringTokenizer s = new StringTokenizer(dir, "/"); //sign   
            s.countTokens();   
            String pathName ="";  
            while(s.hasMoreElements()){   
                pathName = pathName + "/" + (String) s.nextElement();   
                if(ftpClient.makeDirectory(pathName))
                	ftpClient.changeWorkingDirectory(pathName);       
                else
                	return false;
            }  
            return true;   
        }catch (IOException e1){   
            e1.printStackTrace();   
            return false;   
        }   
     }   
     
	/**
	 * Ftp建立连接后，从制定目录下载文件到本地目录
	 * @author xujin
	 * @param remoteFile 服务器上的文件名
	 * @param localFile  本地命名
	 * @throws PeException 
	 */
	public boolean download(String remoteFile, String localFile) throws PeException {
		boolean flag =true;
		try {
			this.connectServer();//启动服务连接
			remoteFile=new String(remoteFile.getBytes(Constants.CHARSET_GBK),Constants.CHARSET_ISO_8859_1);
			localFile = this.localPath +localFile; 				               //文件路径
			FTPFile[] fs=ftpClient.listFiles(remoteFile);
			boolean b=false;
			for(FTPFile ff:fs){
				if(ff.getName().equals(remoteFile))
					b=true;
			}
			if(!b)
				throw new PeException("No such file or directory");
			File fileLocal = new File(localFile);							   //建立本地文件
			FileOutputStream outputStream = new FileOutputStream(fileLocal);   //建立本地文件
			ftpClient.retrieveFile(remoteFile, outputStream);
			logger.info("...........................download successful!");
			outputStream.close();                                              //关闭文件流
		} catch (IOException ex) {
			logger.info("...........................download failed!");
			flag = false;
			logger.info(ex.getMessage());
			throw new PeException(ex.getMessage());
		}
		this.closeConnect();												  //关闭FTP服务
		return flag;
	}
	/**
	 * Ftp建立连接后，从指定目录上传文件到远程服务器目录
	 * @author xujin
	 * @param remoteFile 服务器上的文件名
	 * @param localFile  本地命名
	 * @throws PeException
	 */
    public void upload(String remoteFile, String localFile) throws PeException{
    	
        try {
        	this.connectServer();//启动服务连接
         	remoteFile=new String(remoteFile.getBytes(Constants.CHARSET_GBK),Constants.CHARSET_ISO_8859_1);
            File file_in = new File(localFile);
            FileInputStream fileInputStream = new FileInputStream(file_in);
            ftpClient.storeFile(remoteFile, fileInputStream);
        	logger.info("...........................upload successful!");
            fileInputStream.close();
        	this.closeConnect();												  //关闭FTP服务
        } catch (IOException ex){
        	this.closeConnect();												  //关闭FTP服务
        	logger.info("...........................upload failed!");
        	logger.info(ex.getMessage());
			throw new PeException(ex.getMessage());
        }
    }

	/**
	 * Ftp建立连接后，从指定目录上传文件到远程服务器目录，如果远程没有改目录就创建
	 * @author xujin
	 * @param remoteFile 服务器上的文件名
	 * @param localFile  本地命名
	 * @throws PeException
	 */
    public void upload(String remoteFile, String localFile,String postDate) throws PeException{
    	
        try {
        	this.connectServer(postDate);//启动服务连接
         	remoteFile=new String(remoteFile.getBytes(Constants.CHARSET_GBK),Constants.CHARSET_ISO_8859_1);
            File file_in = new File(localFile);
            FileInputStream fileInputStream = new FileInputStream(file_in);
            ftpClient.storeFile(remoteFile, fileInputStream);
        	logger.info("...........................upload successful!");
            fileInputStream.close();
        	this.closeConnect();												  //关闭FTP服务										  //关闭FTP服务
        } catch (IOException ex){
        	this.closeConnect();												  //关闭FTP服务
        	logger.info("...........................upload failed!");
        	logger.info(ex.getMessage());
			throw new PeException(ex.getMessage());
        }
    }
	/**
	 * 关闭服务器连接
	 * @author xujin
	 */
	public void closeConnect() {
		try {
			if(ftpClient != null)
			ftpClient.disconnect();                                          //关闭FTP服务
		} catch (IOException ex) {
			logger.info("...........................disconnect failed!");
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
	
	public FTPClient getFtpClient() {
		return new FTPClient();
	}

}
