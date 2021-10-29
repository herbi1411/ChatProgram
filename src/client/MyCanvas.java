package client;
import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
class MyCanvas extends Canvas {
	 
	 public int x=-50, y=-50;
	 public int px=-50,py=-50;
	 public int mw = 5;
	 public Color cr=Color.black; //���� ����
	 public Color ycr=Color.black; //��밡 ������ ��
	 @Override
	 public void paint(Graphics g){
	  g.setColor(ycr);
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(mw,1,1));
		g2.drawLine(px, py, x, y);
		px = x; py = y;
	 }
	 
	 @Override
	 public void update(Graphics g){
		  	paint(g);
	 }
	}
