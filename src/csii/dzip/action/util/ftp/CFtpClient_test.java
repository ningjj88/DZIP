package csii.dzip.action.util.ftp;

public class CFtpClient_test {

	public static void main(String args[]) {
		String host = "192.168.1.191";
		String userName = "shengzt";
		String password = "csii";
		int port = 21;
		boolean passiveMode = true;
		
		IFtpClient lClient = CFtpClient.getFtp4jClient();
		int lFlag = lClient.connect(host, userName, password, port, passiveMode);
		System.out.println("连接 lFlag====>>>" + lFlag);
		if (lFlag == 10000) {		
			if (lClient.cwd("/pub/test/") == 10000) {
				lFlag = lClient.upload("E:\\Test.java");
				System.out.println("上传文件 lFlag====>>>" + lFlag);		
				lFlag = lClient.mkdir("testhh");
				System.out.println("创建目录 lFlag====>>>" + lFlag);		
				lFlag = lClient.download("Test.java", "D:\\ftp.java");
				System.out.println("下载文件 lFlag====>>>" + lFlag);		
				String[] lFileNameList = lClient.list();
				for(String fileName:lFileNameList)
					System.out.println("目录下文件名::" + fileName);				
			}
			lClient.disconnect();
		}
	}
}
