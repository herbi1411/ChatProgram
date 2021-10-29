package client;
import java.awt.Canvas;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Client {

	private static String id = null;
	private static String nickname = null;
	private static String pw = null;
	private static char tryLogin = 0;
	private static final String server_ip = "192.168.56.1";;
	private static final short server_port = 9615;
	private static final short ftp_port = 9616;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Socket sock = new Socket();
		Socket fsock = new Socket();
		ChatGUI cg= null;
		Canvas c;
		try {
			sock.connect(new InetSocketAddress(server_ip, server_port));
			fsock.connect(new InetSocketAddress(server_ip, ftp_port));
			ClientAccess ca = new ClientAccess();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),StandardCharsets.UTF_8),true);
			BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8));
			//login process
			while(true)
			{
				String msg;
				while(tryLogin==0) {System.out.print("");}
				if(tryLogin=='l')
				{
					pw =testSHA256(pw);
					msg = "login:" + id + ":" + pw;
					writer.println(msg);
					System.out.println("ID: " + id);
					System.out.println("PW: " + pw);
					msg = input.readLine();
					if(msg.equals("ALREADY"))
					{
						ca.setNickname(msg);
					}
					else if(!msg.equals("fail"))
					{
						nickname = msg;
						ca.setNickname(nickname);
						ca.setTryLogin(true);
						break;
					}
					ca.setTryLogin(true);
				}
				else if(tryLogin =='r')
				{
					pw =testSHA256(pw);
					msg = "registration:" + nickname + ":" + id + ":" + pw;
					writer.println(msg);
					msg = input.readLine();
					ca.setTryRegistration(msg.charAt(0));
				}
				tryLogin = 0;
			}
			String request = "join:" + nickname + "\r\n";
			writer.println(request);
			
			String nickList = input.readLine();
			String[] tokens = nickList.split(":");

			cg = new ChatGUI(nickname,sock,tokens,fsock);
			new ReceiveFTPClient(fsock,cg).start();
			cg.show();
			c = cg.getCanvas();
			ChatClientReceive(sock,cg,c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				if(sock != null && !sock.isClosed())
					sock.close();
			}
			catch(IOException eq){
				eq.printStackTrace();
			}
		}
	}
	public static void setTryLogin(char val) {
		Client.tryLogin = val;
	}
	public static void setId(String id) {
		Client.id = id;
	}
	public static void setPw(String pw) {
		Client.pw = pw;
	}
	public static void setNickName(String nn) {
		Client.nickname = nn;
	}
	private static void ChatClientReceive(Socket sock, ChatGUI cg, Canvas c)
	{
			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8));
				while(true)
				{
					String msg;
						msg = br.readLine();
						String[] tokens = msg.split(">>");
						if("message".equals(tokens[0]))
						{
							cg.append(tokens[1]);
						}
						else if("paint".equals(tokens[0]))
						{
							if(tokens[1].equals("clearAll"))
							{
								cg.clearAll();
							}
							else
							{
								int px = Integer.parseInt(tokens[1]);
								int py= Integer.parseInt(tokens[2]);
								int x= Integer.parseInt(tokens[3]);
								int y= Integer.parseInt(tokens[4]);
								int r= Integer.parseInt(tokens[5]);
								int g= Integer.parseInt(tokens[6]);
								int b= Integer.parseInt(tokens[7]);
								int alpha= Integer.parseInt(tokens[8]);
								int mw = Integer.parseInt(tokens[9]);
							
								MyCanvas cc = ((MyCanvas)c);
								Color cr = new Color(r,g,b,alpha);
								cc.px = px;
								cc.py = py;
								cc.x = x;
								cc.y = y;
								cc.ycr = cr;
								cc.mw = mw;
								cc.repaint();
							}
						}
						else if("join".equals(tokens[0]))
						{
							cg.append(tokens[1] + tokens[2]);
							cg.joinuser(tokens[1]);
						}
						else if("quit".equals(tokens[0]))
						{
							cg.append(tokens[1] + tokens[2]);
							cg.exituser(tokens[1]);
						}
						System.out.println(msg);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				cg.append("서버와 연결이 끊어졌습니다!");
			}
			finally {
				try {
					if(sock!=null && sock.isClosed())
						sock.close();
				}
				catch(IOException e2)
				{
					e2.printStackTrace();
				}
			}
	}
	public static String testSHA256(String str){
		String SHA = ""; 
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			sh.update(str.getBytes()); 
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			SHA = null; 
		}
		return SHA;
	}

}
class ReceiveFTPClient extends Thread
{
	private Socket sock;	
	private DataInputStream dis;
	private FileOutputStream toFile = null;
    private BufferedInputStream bis = null;
    private BufferedOutputStream outFile = null;
	private ChatGUI cg = null;
    public ReceiveFTPClient(Socket s, ChatGUI cg)
	{
		this.sock = s;
		this.cg =cg;
	}
	public void run()
	{
			try {
				bis = new BufferedInputStream(sock.getInputStream());
				dis = new DataInputStream(bis);
				while(true)
				{
					String fileName = dis.readUTF();
					System.out.println(fileName + "을 받습니다!");
					long len = dis.readLong();
					toFile = new FileOutputStream(fileName);
			        outFile = new BufferedOutputStream(toFile);
			            int ch = 0;
			      for(int i=0; i<len; i++)
			      {
			    	  ch = bis.read();
			    	  outFile.write(ch);
			      }
			        outFile.flush();
			        toFile.flush();
			        cg.append("System:: \"" + fileName + "\"("+len+")Bytes를  수신했습니다!!");
			        outFile.close();
			        toFile.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("FTP Client : 서버와 연결이 끊어졌습니다!");
				try {
					if(outFile!=null)
					{
					outFile.flush();
					outFile.close();	
					}
					if(outFile!=null)
					{
						toFile.flush();
						toFile.close();
					}
				    dis.close();
				    bis.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			try {
				sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}	
}
