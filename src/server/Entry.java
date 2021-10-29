package server;


public class Entry {
	private String id;
	private String pw;
	private String nickname;
	
	public Entry(String i, String pw, String n)
	{
		this.id=i;
		this.pw=pw;
		this.nickname=n;
	}
	String setPW(String p)
	{
		String temp = this.pw;
		this.pw = p;
		return temp;
	}
	String setNickName(String n)
	{
		String temp = this.nickname;
		this.nickname = n;
		return temp;
	}
	public String getID() {
		return id;
	}
	public String getPW() {
		return pw;
	}
	public String getNickName() {
		return nickname;
	}
	
}
