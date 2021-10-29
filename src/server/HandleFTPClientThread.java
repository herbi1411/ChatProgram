package server;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class HandleFTPClientThread extends Thread{
	private Socket sock = null;
	private List<Socket> fsocklist = null;
	public HandleFTPClientThread(Socket fsocket, List<Socket> fsocklist) {
		this.sock = fsocket;
		this.fsocklist = fsocklist;
	}
	public void run()
	{
		BufferedInputStream up = null;
        DataInputStream buffereedReader = null;
        FileOutputStream toFile = null;
        BufferedOutputStream outFile = null;
        //
        FileInputStream fis = null;
        DataOutputStream dos = null;
        BufferedInputStream  bis = null;
        BufferedOutputStream toClient = null;
        
	try {
		up = new BufferedInputStream(sock.getInputStream());
		buffereedReader = new DataInputStream(up);
		synchronized(fsocklist)
		{
			fsocklist.add(sock);
		}
		while(true)
		{
			String fileName = buffereedReader.readUTF();
			System.out.println(fileName + "을 받습니다!");
			toFile = new FileOutputStream(fileName);
	        outFile = new BufferedOutputStream(toFile);
	        
	        long len;
	        len = buffereedReader.readLong();
	        System.out.println("파일 크기 = " + len);
	        int fn;
	        for(int i=0; i<len; i++)
	        {
	        	fn = up.read();
	        	outFile.write(fn);
	        }
	        outFile.flush();
	        toFile.flush();
	        outFile.close();
	        toFile.close();
	         synchronized(fsocklist)
	         {
	        	 File f = new File(fileName);
	        	 long fileSize = f.length();
	        	 for(Socket s:fsocklist)
	        	 {
	        		 if(s == sock) continue;
	        		 fis = new FileInputStream(f);
	    	         bis = new BufferedInputStream(fis);
	        		 toClient = new BufferedOutputStream(s.getOutputStream());
	        		 dos = new DataOutputStream(s.getOutputStream());
	        		 dos.writeUTF(fileName);
	        		 dos.writeLong(fileSize);
	        		 fn = 0;
	        		  while((fn = bis.read()) != -1) {
	   		           toClient.write(fn);       
	        		  }    
	        		  toClient.flush();    
	        		  fis.close();
	        		  bis.close();
	        	 }
	        	 System.out.println("Handle FTP client msg: 보내기 끝!");
	         }
		}  
	} catch (IOException e) {
		synchronized(fsocklist)
		{
			fsocklist.remove(sock);
		}
		try {
			if(outFile!=null)
			{
			outFile.flush();
			outFile.close();	
			}
		    buffereedReader.close();
		    sock.close(); 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}

}
