/*
 * 
 */
package csii.dzip.action.util.ftp;

import it.sauronsoftware.ftp4j.FTPClient;
import java.io.File;


/**
 * ftp客戶端服务具体实现类
 * 
 * @author shengzt
 */
public class CFtpClientUsing implements IFtpClient {
	private FTPClient ftpClient;
	public CFtpClientUsing() {
	}

	/**
	 * 将FTP服务器文件下载到本地
	 * 
	 * @param remoteFile FTP服务器文件名称
	 * @param localFile 本地文件名称
	 * 
	 * @return int
	 */
	public int download(String remoteFile, String localFile) {
		int flag = FTP_OK;
		if (remoteFile == null || localFile == null)
			return FTP_DOWNLOAD_FILE_NULL;
		File outfile = new File(localFile);
		if (!outfile.exists()) {
			String names[] = null;
			try {
				names = ftpClient.listNames();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (names == null)
				return FTP_CANT_GET_REMOTE_FILE_LIST;
			boolean lFlag = false;
			int i = 0;
			do {
				if (i >= names.length)
					break;
				if (names[i].equals(remoteFile)) {
					lFlag = true;
					break;
				}
				i++;
			} while (true);
			if (lFlag)
				try {
					ftpClient.download(remoteFile, outfile);
				} catch (Exception e) {
					flag = FTP_DOWNLOAD_EXCEPTION;
				}
			else
				flag = FTP_REMOTE_FILE_NOT_EXIST;
		} else {
			flag = FTP_LOCAL_FILE_EXIST;
		}
		return flag;
	}

	/**
	 * 将本地文件上传到FTP服务器文件
	 * 
	 * @param fileName 文件名称 
	 * 
	 * @return int
	 */
	public int upload(String fileName) {
		return upload(fileName, fileName);
	}

	/**
	 * 将本地文件上传到FTP服务器文件
	 * 
	 * @param localFile 本地文件名称 
	 * @param remoteFile FTP服务器文件名称
	 * 
	 * @return int
	 */
	public int upload(String localFileName, String remoteFileName) {
		int isUpload = FTP_OK;
		if (localFileName != null) {
			File file = new File(localFileName);
			if (file != null && file.exists())
				try {
					ftpClient.upload(file);
				} catch (Exception e) {
					isUpload = FTP_EXCEPTION;
				}
			else
				return FTP_UPLOAD_FILE_NULL_OR_NOT_EXIST;
		} else {
			return FTP_UPLOAD_FILENAME_NULL;
		}
		return isUpload;
	}

	/**
	 * 连接FTP服务器
	 * 
	 * @param host FTP服务器地址 
	 * @param userName 用户名
	 * @param password 用户密码
	 * @param port 端口
	 * @param passiveMode 访问模式（true：可访问）
	 * 
	 * @return int
	 */
	public int connect(String host, String userName, String password, int port,
			boolean passiveMode) {
		int isConnectionSuccess = FTP_OK;
		try {
			if (ftpClient == null)
				ftpClient = new FTPClient();
			ftpClient.connect(host, port);
			ftpClient.login(userName, password);
			ftpClient.setType(2);
			ftpClient.setPassive(passiveMode);
		} catch (Exception e) {
			isConnectionSuccess = disconnect();
			if (isConnectionSuccess == FTP_OK)
				isConnectionSuccess = FTP_EXCEPTION;
			else
				isConnectionSuccess = FTP_CONNECT_DISCONNECT_EXCEPTION;
		}
		return isConnectionSuccess;
	}

	/**
	 * 连接FTP服务器
	 * 
	 * @param host FTP服务器地址 
	 * @param userName 用户名
	 * @param password 用户密码
	 * 
	 * @return int
	 */
	public int connect(String host, String userName, String password) {
		return connect(host, userName, password, 21, true);
	}

	/**
	 * 打开FTP服务器目录
	 * 
	 * @param workingDirectory 目录 名
	 * 
	 * @return int
	 */
	public int cwd(String workingDirectory) {
		int lFlag = FTP_OK;
		try {
			if (workingDirectory != null)
				ftpClient.changeDirectory(workingDirectory);
			else
				lFlag = FTP_CWD_NULL_WORKINGDIRECTORY;
		} catch (Exception e) {
			lFlag = FTP_EXCEPTION;
		}
		return lFlag;
	}

	/**
	 * 关闭服务器连接
	 * 
	 * @return int
	 */
	public int disconnect() {
		int isDisconnectionSuccess = FTP_OK;
		try {
			if (ftpClient != null && ftpClient.isConnected())
				ftpClient.disconnect(true);
		} catch (Exception e) {
			isDisconnectionSuccess = FTP_DISCONNECT_ERROR;
		}
		return isDisconnectionSuccess;
	}

	/**
	 * 创建服务器目录
	 * 
	 * @param newDirectory 要创建目录 名
	 * 
	 * @return int
	 */
	public int mkdir(String newDirectory) {
		int lFlag = FTP_OK;
		try {
			if (newDirectory != null)
				ftpClient.createDirectory(newDirectory);
			else
				lFlag = FTP_MKDIR_NULL_DIRECTORY;
		} catch (Exception e) {
			lFlag = FTP_EXCEPTION;
		}
		return lFlag;
	}

	/**
	 * 删去服务器文件
	 * 
	 * @param newDirectory 要删去文件 名
	 * 
	 * @return int
	 */
	public int delete(String remoteFile) {
		int lFlag = FTP_OK;
		try {
			if (remoteFile != null)
				ftpClient.deleteFile(remoteFile);
			else
				lFlag = FTP_DELETE_NULL_FILE;
		} catch (Exception e) {
			lFlag = FTP_EXCEPTION;
		}
		return lFlag;
	}

	/**
	 * 获取服务器目录下文件名
	 * 
	 * @return String[]
	 */
	public String[] list() {
		String lFileNameList[] = null;
		try {
			lFileNameList = ftpClient.listNames();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lFileNameList;
	}	
}
