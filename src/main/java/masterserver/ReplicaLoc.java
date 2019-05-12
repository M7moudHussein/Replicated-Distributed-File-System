package masterserver;


public class ReplicaLoc {
	private String ip, dir;
	private int port;
	
	public ReplicaLoc(String ip, String dir,int port) {
		this.setIp(ip);
		this.setDir(dir);
		this.setPort(port);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
	
}
