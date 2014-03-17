/*
 * 
 */
package csii.dzip.action.util.ftp;

/**
 * ftp客戶端服务接口
 * 
 * @author shengzt
 */

public interface IFtpClient {

	public static final int FTP_OK = 10000;                               //成功
	public static final int FTP_EXCEPTION = 90000;                        //异常
	public static final int FTP_NOT_POSITIVE_COMPLETION = 90001;          //不正确结束
	public static final int FTP_LOGIN_ERROR = 90002;                      //登录错误
	public static final int FTP_CANT_SET_BINARY_TYPE = 90003;             //不能设置二进制类型
	public static final int FTP_DISCONNECT_ERROR = 90004;                 //FTP文件目录错误
	public static final int FTP_CONNECT_DISCONNECT_EXCEPTION = 90005;     //连接FTP文件目录错误
	public static final int FTP_CWD_ERROR = 90006;                        //打开FTP文件目录错误
	public static final int FTP_CWD_NULL_WORKINGDIRECTORY = 90007;        //打开FTP文件目录不存在
	public static final int FTP_MKDIR_ERROR = 90008;                      //创建FTP服务器目录错误
	public static final int FTP_MKDIR_NULL_DIRECTORY = 90009;             //创建FTP服务器目录不存在
	public static final int FTP_UPLOAD_ERROR = 90010;                     //上传FTP文件错误
	public static final int FTP_UPLOAD_OK_INPUT_CLOSE_EXCEPTION = 90011;  //上传FTP文件成功但异常
	public static final int FTP_UPLOAD_FILENAME_NULL = 90012;             //上传FTP文件不存在
	public static final int FTP_UPLOAD_FILE_NULL_OR_NOT_EXIST = 90013;    //上传FTP文件不存在或为空
	public static final int FTP_DOWNLOAD_FILE_NULL = 90014;               //下载FTP文件不存在
	public static final int FTP_DOWNLOAD_EXCEPTION = 90015;               //下载FTP文件异常
	public static final int FTP_DOWNLOAD_ERROR = 90016;                   //下载FTP文件错误
	public static final int FTP_DOWNLOAD_OK_OUT_CLOSE_EXCEPTION = 90017;  //下载FTP文件成功但异常
	public static final int FTP_REMOTE_FILE_NOT_EXIST = 90018;            //FTP服务器文件不存在
	public static final int FTP_LOCAL_FILE_EXIST = 90019;                 //本地文件已经存在
	public static final int FTP_CANT_GET_REMOTE_FILE_LIST = 90020;        //不能得到FTP服务器目录文件
	public static final int FTP_DELETE_ERROR = 90021;                     //删除FTP文件错误
	public static final int FTP_DELETE_NULL_FILE = 90022;                 //删除FTP文件不存在

	public abstract int connect(String s, String s1, String s2, int i,
			boolean flag);

	public abstract int connect(String s, String s1, String s2);

	public abstract int disconnect();

	public abstract int cwd(String s);

	public abstract int upload(String s);

	public abstract int upload(String s, String s1);

	public abstract int mkdir(String s);

	public abstract int download(String s, String s1);

	public abstract int delete(String s);

	public abstract String[] list();
}
