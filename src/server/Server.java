package server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server {
	public static void main(String[] args)
	{
		final short port = 9615;
		final short fport = 9616;
		ServerSocket serverSock = null;
		ServerSocket fserverSock = null;
		ArrayList<String> paintOperation = new ArrayList<String>();
		List<Writer_nickname> listWriters = new ArrayList<Writer_nickname>();
		List<String> nnlist = new ArrayList<String>();
		List<Socket>fsocklist = new ArrayList<Socket>();
		try {
			serverSock = new ServerSocket();
			fserverSock = new ServerSocket();
			String localAddress = InetAddress.getLocalHost().getHostAddress();
			//String localAddress = "192.168.35.166";
			
			serverSock.bind(new InetSocketAddress(localAddress, port));
			fserverSock.bind(new InetSocketAddress(localAddress, fport));
			System.out.println("binding success!!\naddress:" + localAddress + " , port: " + port);
			System.out.println("fbinding success!!\naddress:" + localAddress + " , port: " + fport);
			while(true)
			{
				Socket socket = serverSock.accept();
				Socket fsocket = fserverSock.accept();
				new HandleClientThread(socket,listWriters,paintOperation,nnlist,fsocket,fsocklist).start();
				InetSocketAddress client = (InetSocketAddress)socket.getRemoteSocketAddress();
				String clientIP = client.getAddress().getHostAddress();
				int clientPort = client.getPort();
				System.out.println("Connect Success!!\nClient address: " + clientIP + ", port:" + clientPort);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if(serverSock != null && !serverSock.isClosed())
					serverSock.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

}
