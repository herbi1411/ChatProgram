package client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ChatGUI{
	private String nickname;
	private List<String> nnlist;
	private JFrame frame;
	private JPanel pannel,pannel2;
	private MyDrawing md;
	private JButton buttonSend;
	private JButton fileSend;
	private JTextField textField;
	private TextArea textArea;
	private Socket sock;
	private Socket fsock;
	private String[] ntokens;
	private ChatGUI iam = this;
	public ChatGUI(String nn, Socket s, String[] nt, Socket fs)
	{
		this.nickname = nn;
		this.frame = new JFrame(nickname);
		frame.setDefaultCloseOperation(0);
		this.pannel = new JPanel();
		this.pannel2 = new JPanel();
		this.md = new MyDrawing();
		this.buttonSend = new JButton("Send");
		this.fileSend = new JButton("FileSend");
		this.sock = s;
		this.textField = new JTextField();
		this.textArea = new TextArea(null, 30,40,TextArea.SCROLLBARS_VERTICAL_ONLY);
		this.ntokens = nt;
		this.nnlist = new ArrayList<String>();
		this.fsock = fs;
	}
	public void show()
	{
		frame.setLayout(new BorderLayout(10,10));
		frame.setSize(1080,720);
		frame.setResizable(false);
		buttonSend.setBackground(Color.BLACK);
		buttonSend.setForeground(Color.YELLOW);
		buttonSend.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 10));
		buttonSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				sendMessage();
			}
		});
		
		textField.setColumns(50);
		textField.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 12));
		textField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if(keyCode == KeyEvent.VK_ENTER) sendMessage();
			}
		});
		
		fileSend.setBackground(Color.BLACK);
		fileSend.setForeground(Color.magenta);
		fileSend.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 10));
		fileSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				  new FileSendThread(fsock,iam).start();
			}
		});

		pannel.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		pannel.add(fileSend);
		frame.add(BorderLayout.SOUTH,pannel);
		
		textArea.setEditable(false);
		textArea.setFont(new Font("바탕",Font.PLAIN,20));
		pannel2.add(textArea,BorderLayout.CENTER);
		frame.add(BorderLayout.WEST, pannel2);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.add(BorderLayout.CENTER, md);
		frame.setVisible(true);
		if(!ntokens[0].equals("")) for(int i=0; i<ntokens.length; i++) joinuser(ntokens[i]);
	}
	
	private void sendMessage() {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),StandardCharsets.UTF_8),true);
			String message = textField.getText();
			String request;
			String[] tokens = message.split(" ",3);
			if(tokens[0].equals("/w"))
			{
				boolean isExist = false;
				String nn = tokens[1];
				for(int i=0; i<nnlist.size(); i++)
				{
					if(nnlist.get(i).equals(tokens[1]))
					{
						isExist = true;
						break;
					}
				}
				if(!isExist)
				{
					append("System:: " + tokens[1] + " 유저가 없습니다!");
					return;
				}
				request = "whisper:" + tokens[1]+":" + tokens[2];
				append("(귓속말->"+tokens[1]+")" + tokens[2]);
			}
			else request = "message: " + message;
			pw.println(request);
			textField.setText("");
			textField.requestFocus();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(sock!=null && sock.isClosed())
					sock.close();
			}
			catch(IOException e2){
			}
			}
	}
	public void append(String msg)
	{
		textArea.append(msg);
		textArea.append("\n");
	}
	public Canvas getCanvas() {
		// TODO Auto-generated method stub
		return md.getCanvas();
	}
	public void clearAll(){
		md.clearAll();
	}
	public void joinuser(String un) {md.joinuser(un); nnlist.add(un);}
	public void exituser(String un) {md.exituser(un); nnlist.remove(un);}

	
	
	public class MyDrawing extends JPanel {
		 
		 JPanel p0,p1,p2;
		 JButton btBL,btR, btG, btB, btOpen;
		 Canvas can; 
		 PaintToolFrame pt;
		 
		 JTable table;
		 String header[] = {"참여 목록"};
		 DefaultTableModel dtm;
		 
		 public MyDrawing(){
		  this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		  table = new JTable(new DefaultTableModel(null, header));
		  dtm = (DefaultTableModel) table.getModel();
		  p0 = new JPanel();
		  p0.setLayout(new BorderLayout(0,0));
		  table.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 20));
		  table.setRowHeight(25);
		  JScrollPane scrollPane = new JScrollPane(table);
		  scrollPane.setPreferredSize(new Dimension(50,100));
		  p0.add(scrollPane);
		  this.add(p0);
	
		  pt=new PaintToolFrame();
		  p1=new JPanel(); this.add(p1);
		  p2=new JPanel(){ // 여백주기
		   public Insets getInsets(){
		    return new Insets(10,10,10,10);
		   }
		  }; 
		  this.add(p2);
		  p2.setBackground(Color.lightGray);
		  
		  btBL=new JButton("검정"); btBL.setBackground(Color.BLACK); btBL.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 10)); btBL.setForeground(Color.WHITE);p1.add(btBL); 
		  btR=new JButton("빨강"); btR.setBackground(Color.RED); btR.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 10)); btR.setForeground(Color.WHITE);p1.add(btR); 
		  btG=new JButton("초록"); btG.setBackground(Color.GREEN); btG.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 10)); btG.setForeground(Color.WHITE);p1.add(btG);
		  btB=new JButton("파랑"); btB.setBackground(Color.BLUE); btB.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 10)); btB.setForeground(Color.WHITE);p1.add(btB);
		  btOpen=new JButton("Paint Tool"); p1.add(btOpen);
		  btOpen.setBackground(Color.black);
		  btOpen.setForeground(Color.yellow);
		  can=new MyCanvas(); 
		  can.setSize(400, 400); 
		  can.setBackground(Color.white); 
		  p2.add(can);
		  
		  //리스너 부착 
		  MyHandler my=new MyHandler();
		  can.addMouseMotionListener((MouseMotionListener) my); 
		  btBL.addActionListener(my);
		  btR.addActionListener((ActionListener) my);
		  btG.addActionListener(my);
		  btB.addActionListener(my);
		  btOpen.addActionListener(my);
		    
		  pt.btPlus.addActionListener(my);
		  pt.btMinus.addActionListener(my);
		  pt.btClear.addActionListener(my);
		  pt.btAllClear.addActionListener(my);
		  pt.btColor.addActionListener(my);
		  pt.btClose.addActionListener(my);
		
		  setVisible(true);  
		 }
		 private ImageIcon setImageIcon(String path)
		 {
			 return new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH));
		 }
		 public void updateCanvas(MyCanvas c) {this.can = c;}
		 public void joinuser(String un)
		 {
			 dtm.addRow(new Object[] {un});
		 }
		 public void exituser(String un)
		 {
			 int index = -1;
			 int rowcount = table.getRowCount();
			 for(int i=0; i<rowcount; i++)
			 {
				 if(un.equals(table.getValueAt(i, 0)))
				 {
					 index =i;
					 break;
				 }
			 }
			 dtm.removeRow(index);	 
		 }
		 class MyHandler implements MouseMotionListener, ActionListener{
		  
		  public void mouseDragged(MouseEvent e){
			 int px,py,x,y;
			 int r,g,b,alpha,mw;
			Color cr;
			 px = ((MyCanvas)can).px;
			 py = ((MyCanvas)can).py;
			 x = e.getX();
			 y = e.getY();
			 cr = ((MyCanvas)can).cr;
			 r = cr.getRed();
			 b = cr.getBlue();
			 g = cr.getGreen();
			 alpha = cr.getAlpha();
			 mw = ((MyCanvas)can).mw;
			 
			 String msg;
			 PrintWriter pw;
			 try {
				 		pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),StandardCharsets.UTF_8),true);
				 		msg = "paint" + ":" + Integer.toString(px) + ">>" + Integer.toString(py) + ">>" + Integer.toString(x) + ">>" + Integer.toString(y) + ">>"
							+ Integer.toString(r)+ ">>" + Integer.toString(g)+ ">>" + Integer.toString(b)+ ">>" + Integer.toString(alpha) + ">>" + Integer.toString(mw); 
						pw.println(msg);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		 }	
		  @Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
			  ((MyCanvas)can).px = e.getX();
			  ((MyCanvas)can).py = e.getY();
				
			}
		  public void actionPerformed(ActionEvent e){
		   Object o=e.getSource();
		   MyCanvas can2 = (MyCanvas) can;
		   if(o==btBL) {
			   can2.cr = Color.black;
		   }
		   else if(o==btR){
		    can2.cr=Color.red;
		   }else if(o==btG){
		    can2.cr=Color.GREEN;
		   }else if(o==btB){
		    can2.cr=Color.blue;
		   }else if(o==btOpen){
		    pt.pack(); // 크기를 압축해서 보여줌
		    pt.setLocation(getWidth(),0); //x축만큼 오른쪽으로 창 이동
		    pt.setVisible(true);
		   }else if(o==pt.btPlus){
		    can2.mw +=2;
		   }else if(o==pt.btMinus){
		    if(can2.mw>3){ // 버튼을 계속 누르면 아예 안나옴. 최소한의 크기 설정
		     can2.mw -=3; 
		    }
		   }else if(o==pt.btClear)
		    can2.cr=can.getBackground();
		   else if(o==pt.btAllClear){
			   PrintWriter pw;
			   String msg;
				try {
					pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),StandardCharsets.UTF_8),true);
					msg = "paint" + ":" + "clearAll"; 
					pw.println(msg);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		   }else if(o==pt.btColor){
		    Color selCr = JColorChooser.showDialog(null, "색선정", Color.blue); 
		    can2.cr=selCr; 
		   }else if(o==pt.btClose){
		    pt.dispose(); 		   }
		  }
		 }
		 public Canvas getCanvas() {return can;}
		 public void clearAll()
		 {
			 Graphics g=((MyCanvas)can).getGraphics();
			    g.clearRect(0, 0, can.getWidth(), can.getHeight()); 
		 }
		 
		 class PaintToolFrame extends JFrame{

		 JButton btPlus, btMinus, btClear, btAllClear, btClose, btColor;
		 JPanel p;
		 public PaintToolFrame(){
		  super("::PaintToolFrame::");
		  Container c=getContentPane();
		  p=new JPanel();
		  p.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
		  c.add(p, "Center");
		  p.add(btPlus=new JButton("크게"));
		  p.add(btMinus=new JButton("작게"));
		  p.add(btClear=new JButton("지우기"));
		  p.add(btAllClear=new JButton("모두지우기"));
		  p.add(btColor=new JButton("색상"));
		  p.add(btClose=new JButton("닫기"));
		  
		  setFont(btPlus);
		  setFont(btMinus);
		  setFont(btClear);
		  setFont(btAllClear);
		  setFont(btClose);
		  setFont(btColor);
		 }
		 private void setFont(JButton temp)
		 {
			 temp.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 20));
		 }
		}
	}
}

class FileSendThread extends Thread{
	  private Socket s;
	  private ChatGUI cg;
	  FileSendThread(Socket sk, ChatGUI cg){this.s = sk; this.cg = cg;}
	  public void run()
	  {
		  String filedir = null;
		  String fileName;
		  File f = null;
		  long fileSize;
		  BufferedOutputStream toServer = null;
		  BufferedInputStream bis = null;
		  FileInputStream fis = null;
		  DataOutputStream dos = null;
		// TODO Auto-generated method stub
		 try{
			   filedir = FileChooser.FileChooser();
			   if(filedir.equals("")) return;
			   String backslash = "\\\\";
			   String tokens[] = filedir.split(backslash);
			   fileName = tokens[tokens.length-1];
			   toServer = new BufferedOutputStream(s.getOutputStream());
			   dos = new DataOutputStream(s.getOutputStream());
			   dos.writeUTF(fileName);
			   f = new File(filedir);
			   fileSize = f.length();
			   System.out.println("fileSize : " + fileSize);
			   fis = new FileInputStream(f);
			   bis = new BufferedInputStream(fis);
			   dos.writeLong(fileSize);
			   int fn;
			   while((fn=bis.read())!= -1)
			   {
				   toServer.write(fn);
			   }
			   System.out.println("Client sender: 보내기 끝!!");
			   toServer.flush();  
			   cg.append("System:: \"" + fileName + "\"("+fileSize+")Bytes 를 전송했습니다!!");
			  }catch(FileNotFoundException fnfe){
			   fnfe.printStackTrace();
			  }catch(IOException ioe){
			   ioe.printStackTrace();
			  }     
	  }
}

