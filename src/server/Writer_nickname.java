package server;
import java.io.PrintWriter;

public class Writer_nickname {
	private PrintWriter pw;
	private String nickName;
	
	public Writer_nickname(PrintWriter pw)
	{
		this.pw = pw;
		this.nickName = null;
	}
	public Writer_nickname(PrintWriter pw, String nn) {
		this.pw = pw;
		this.nickName = nn;
	}
	void SetNickName(String nn) {this.nickName = nn;} 
	PrintWriter getpw() {return pw;}
	String getnn() {return nickName;}
}
