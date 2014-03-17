/*
 * 创建日期 2006-10-25
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package csii.dzip.action.util.secapipackage;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;



/**
 * @author zhaozheng
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */



public class SecSocket {

	protected Socket bi = null;
	String m_strServerAddress = null;
	String b_m_strServerAddress = null;
	int m_iServerPort = 0;
	BufferedInputStream bis = null;
	BufferedOutputStream bos = null;

	public SecSocket(String _serverAddress, String _b_serverAddress, int _serverPort) {
		setServerAddress( _serverAddress, _b_serverAddress,  _serverPort);
		
	}

	public SecSocket() {
		
		this( ShareVar.HsmConf.ip, ShareVar.HsmConf.bip, ShareVar.HsmConf.port);

	}

	public void connect() throws IOException {

//		try{
			bi = new Socket( m_strServerAddress, m_iServerPort);
//		}catch (Exception e) {
//			System.out.println(">>l���������["+m_strServerAddress+"]ʧ�ܣ�׼������l�ӱ��������["+b_m_strServerAddress+"]...");
//			try{
//			bi = new Socket(b_m_strServerAddress,m_iServerPort);
//			System.out.println(">>l�ӱ��������["+b_m_strServerAddress+"]�ɹ���");
//			}catch (Exception e1) {
//				System.out.println(">>l���������["+m_strServerAddress+"]�ͱ��������["+b_m_strServerAddress+"]��ʧ��");
//				throw new IOException(">>l���������["+m_strServerAddress+"]�ͱ��������["+b_m_strServerAddress+"]��ʧ��");
//			}
//			GetConf.SwitchMainAndBackIP(this);
//		}
		bis = new BufferedInputStream( bi.getInputStream());
		bos = new BufferedOutputStream( bi.getOutputStream());
		
		if(bi.getSoLinger() == -1) 
			bi.setSoLinger(true, 0);
		bi.setReuseAddress(true);

		
	}

	public int write( byte[] bs, int pos, int length) throws IOException {
		bos.write( bs, pos, length);
		bos.flush();
		
		return 0;
	}


	//read method
	public int read( byte[] bs, int pos, int length) throws IOException
	{
		int i = bis.read( bs, pos, length);
	  	return i;
	}

	/**
	 * @return
	 */
	public int getServerPort() {
		return m_iServerPort;
	}

	/**
	 * @return
	 */
	public String getServerAddress() {
		return m_strServerAddress;
	}

	/**
	 * @param i
	 */
	public void setServerPort(int i) {
		m_iServerPort = i;
	}

	/**
	 * @param string
	 */
	public void setServerAddress(String ip,String bip,int i) {
		m_strServerAddress = ip;
		b_m_strServerAddress = bip;
		m_iServerPort = i;
	}

	public void close() throws IOException {
		if(bos != null){
			bos.close();
		}
		if(bis != null){
			bis.close();
		}
		if ( bi != null) {
			bi.close();
//			System.out.println("closing...");
		}
	}
}





