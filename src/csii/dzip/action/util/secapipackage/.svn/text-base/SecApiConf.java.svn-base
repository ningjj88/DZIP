package csii.dzip.action.util.secapipackage;

public class SecApiConf {
	
	private String csmp;
	private int port;
	private String clnt;
	private int timeout;
	private int headlen;
	private int taillen;
	private int errlv;
	private String logpath;
	public SecApiConf(){
	}
	public void getHsmConf(){
			ShareVar.HsmConf.port =this.port;
			
			ShareVar.HsmConf.ip =this.csmp;
			ShareVar.HsmConf.bip =this.csmp;	
			ShareVar.HsmConf.clientip =this.clnt;
			ShareVar.HsmConf.timeout =this.timeout;
			if(ShareVar.HsmConf.timeout > ShareVar.MAXTIMEOUT) {
				ShareVar.HsmConf.timeout = ShareVar.MAXTIMEOUT;
			}
			ShareVar.HsmConf.errlv =this.errlv;
			ShareVar.HsmConf.headlen =this.headlen;
			ShareVar.HsmConf.taillen =this.taillen;
			ShareVar.HsmConf.logpath =this.logpath;
	}
	
	public void setCsmp(String csmp) {
		this.csmp = csmp;
	}
	public String getCsmp() {
		return this.csmp ;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getPort() {
		return this.port ;
	}
	public void setClnt(String clnt) {
		this.clnt = clnt;
	}
	public String getClnt() {
		return this.clnt ;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public int getTimeout() {
		return this.timeout ;
	}
	public void setHeadlen(int headlen) {
		this.headlen = headlen;
	}
	public int getHeadlen() {
		return this.headlen ;
	}
	public void setTaillen(int taillen) {
		this.taillen = taillen;
	}
	public int getTaillen() {
		return this.taillen ;
	}
	public void setErrlv(int errlv) {
		this.errlv = errlv;
	}
	public int getErrlv() {
		return this.errlv ;
	}
	public void setLogpath(String logpath) {
		this.logpath = logpath;
	}
	public String getLogpath() {
		return this.logpath ;
	}
}
