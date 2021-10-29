package server;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

public class HandleClientThread extends Thread{
	private String nickname = null;
	private Socket sock = null;
	private PrintWriter myPrintWriter = null; 
	private List<Writer_nickname> listWriters = null;
	private ArrayList<String> paintOperation;
	private List<String> nnlist = new ArrayList<String>();
	private Socket fsock = null;
	private List<Socket>fsocklist = null;
	public HandleClientThread(Socket socket, List<Writer_nickname> listWriters, ArrayList<String> paintOperation, List<String> nnlist, Socket fsock, List<Socket>fsocklist)
	{
		this.sock = socket;
		this.listWriters = listWriters;
		this.paintOperation = paintOperation;
		this.nnlist = nnlist;
		this.fsock = fsock;
		this.fsocklist = fsocklist;
	}
	public void run()
	{
		BufferedReader buffereedReader = null;
		PrintWriter printWriter = null;
		try {
			buffereedReader = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8));
			printWriter = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),StandardCharsets.UTF_8),true);
		
			//loginProcess
			myPrintWriter = printWriter;
			while(true)
			{
				Login l = new Login();
				String request = buffereedReader.readLine();
				String[] tokens = request.split(":");
				if("login".equals(tokens[0]))
				{
					this.nickname = l.access(tokens[1], tokens[2]);
					if(this.nickname==null) printWriter.println("fail");
					else
					{
						boolean kg= false;
						synchronized(nnlist) {
							for(String test:nnlist)
							{
								if(test.equals(nickname))
								{
									kg = true;
									break;
								}
							}
						}
							if(!kg)
							{
								printWriter.println(nickname);
								break;
							}
							else printWriter.println("ALREADY");
					}
				}
				else if("registration".equals(tokens[0]))
				{
					char r = l.addAccount(tokens[1], tokens[2], tokens[3]);
					printWriter.println(r);
					if(r=='s') l.saveData();
				}
				
			}	
			String nickList = "";
			synchronized(nnlist)
			{
				for(String test : nnlist)
				{
					nickList = nickList + test + ":"; 
				}
			}
			printWriter.println(nickList);
			doInitialPaint();
			//
			new HandleFTPClientThread(fsock,fsocklist).start();
			while(true)
			{
				String request = buffereedReader.readLine();
				System.out.println(request);
				String[] tokens = request.split(":");
				if("join".equals(tokens[0])) doJoin(tokens[1], printWriter);
				else if("message".equals(tokens[0])){
					if(tokens.length==2)
					doMessage(tokens[1]);
				}
				else if("quit".equals(tokens[0])) doQuit(printWriter);	
				else if("paint".equals(tokens[0])) {
					doPaint(tokens[1]);
				}
				else if("whisper".equals(tokens[0])) doWhisper(tokens[1],tokens[2]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(this.nickname + "¥‘¿Ã √§∆√πÊ¿ª ≥™∞¨Ω¿¥œ¥Ÿ!");
			if(this.nickname!=null)doQuit(printWriter);
		}
	}
	private void doMessage(String msg) {
		// TODO Auto-generated method stub
		broadcast("message>>" + this.nickname + ":" + msg);
	}
	private void doPaint(String msg)
	{
		broadcast("paint>>" + msg);
		synchronized(paintOperation)
		{
			if(msg.equals("clearAll"))
			{
				while(paintOperation.size()!=0)
					paintOperation.remove(0);
			}
			else paintOperation.add(msg);
		}
	}
	private void doInitialPaint()
	{
		synchronized(paintOperation)
		{
			int i = 0;
			for(i =0; i<paintOperation.size(); i++)
			{
				myPrintWriter.println("paint>>"+paintOperation.get(i));
				myPrintWriter.flush();
			}
		}
	}
	private void doWhisper(String nn, String msg)
	{
		broadcast(nn,"message>>(±”º”∏ª)"+this.nickname + ":" + msg);
	}
	private void doJoin(String nickname, PrintWriter writer) {
		// TODO Auto-generated method stub
		this.nickname = nickname;
		String msg = "join>>" + nickname + ">>¥‘¿Ã ¿‘¿Â«ﬂΩ¿¥œ¥Ÿ.";
		addWriter(writer,nickname);
		broadcast(msg);
	}
	private void addWriter(PrintWriter writer, String nn) {
		// TODO Auto-generated method stub
		synchronized(listWriters) {
			listWriters.add(new Writer_nickname(writer,nn));
		}
		synchronized(nnlist) {
			nnlist.add(nn);
		}
	}
	private void doQuit(PrintWriter writer) {
		// TODO Auto-generated method stub
		removeWriter(writer);
		String msg = "quit>>" + this.nickname +">>¥‘¿Ã ≈¿Â«ﬂΩ¿¥œ¥Ÿ.";
		broadcast(msg);
	}
	private void broadcast(String msg) {
		// TODO Auto-generated method stub
		synchronized(listWriters) {
			for(int i=0; i<listWriters.size(); i++){
				PrintWriter writer = listWriters.get(i).getpw();
				writer.println(msg);
				writer.flush();
			}
		}
	}
	private void broadcast(String nn, String msg)
	{
		synchronized(listWriters) {
			for(int i=0; i<listWriters.size(); i++){	
				if(nn.equals(listWriters.get(i).getnn()))
				{
					PrintWriter writer = listWriters.get(i).getpw();
					writer.println(msg);
					writer.flush();
					break;
				}
			}
		}
	}
	private void removeWriter(PrintWriter writer) {
		// TODO Auto-generated method stub
		synchronized(listWriters) 
		{
			int index = -1;
			for(int i=0; i<listWriters.size(); i++)
			{
				if(writer == listWriters.get(i).getpw())
				{
					index = i;
					break;
				}
			}
			listWriters.remove(index);
		}
		synchronized(nnlist)
		{
			nnlist.remove(nickname);
		}
	}
}
