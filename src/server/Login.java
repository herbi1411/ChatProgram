package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
public class Login {
	private ArrayList<Entry> list;
	private static String Filename = "DataBase.txt";
	public Login() 
	{
		this.list = new ArrayList<Entry>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(Filename));
			String line;
			while((line=br.readLine())!= null)
			{
				StringTokenizer st = new StringTokenizer(line," ");
				list.add(new Entry(st.nextToken(),st.nextToken(),st.nextToken()));
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				new FileWriter(Filename);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public char addAccount(String NickName, String id, String pw)  //'s': success //'i': id conflict //'n': nickname conflict
	{
		int size = list.size();
		Entry temp;
		for(int i =0; i<size; i++)
		{
			temp = list.get(i);
			if(id.equals(temp.getID())) return 'i';
			if(NickName.equals(temp.getNickName())) return 'n';
		}
		list.add(new Entry(id,pw,NickName));
		return's';
	}
	public String access(String id, String pw)
	{
		int size = this.list.size();
		for(int i =0; i<size; i++)
		{
			Entry temp = list.get(i);
			if(temp.getID().contentEquals(id))
			{
				if(temp.getPW().equals(pw)) return temp.getNickName();
				else return null;
			}
		}
		return null;
	}
	public void saveData() throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(Filename));
		int size = list.size();
		String line;
		Entry temp;
		for(int i=0; i<size; i++)
		{
			temp = list.get(i);
			line = temp.getID() + " " + temp.getPW() + " " + temp.getNickName();
			bw.write(line);
			bw.newLine();
		}
		bw.close();
	}
	
	public void printAll()
	{
		int size = list.size();
		Entry temp;
		for(int i=0; i<size; i++)
		{
			temp = list.get(i);
			System.out.println("ID: " + temp.getID() + ", PW: " + temp.getPW() + ",  NickName: " + temp.getNickName());
		}
	}
}
	