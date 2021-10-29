package client;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;

public class ClientAccess extends JFrame {
	private String id;
	private String pw;
	private String nickname = null;
	private Registration reg;
	private JPanel contentPane;
	private boolean tryLogin = false;
	private JTextField id_f;
	private JPasswordField pw_f;

	public ClientAccess() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 395, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton logIn = new JButton("\uB85C\uADF8\uC778");
		logIn.setBackground(new Color(255, 0, 255));
		logIn.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
		logIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				id = id_f.getText();
				pw = pw_f.getText();
				id_f.setText("");
				pw_f.setText("");
				Client.setId(id);
				Client.setPw(pw);
				Client.setTryLogin('l');
				while(!tryLogin) {System.out.print("");}
				if(nickname == null)
				{
					JOptionPane.showMessageDialog(null, "회원 정보가 일치하지 않습니다!!");
					tryLogin = false;
				}
				else if(nickname.equals("ALREADY"))
				{
					JOptionPane.showMessageDialog(null, "회원이 이미 접속중입니다!!");
					tryLogin = false;
				}
				else
				{
					JOptionPane.showMessageDialog(null, "환영합니다 " + nickname + " 님!");
					dispose();
				}
			}
		});
		logIn.setBounds(253, 40, 90, 71);
		contentPane.add(logIn);
		
		JButton signUp = new JButton("\uD68C\uC6D0\uAC00\uC785");
		signUp.setBackground(new Color(255, 255, 0));
		signUp.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
		signUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reg = new Registration();
			}
		});
		signUp.setBounds(253, 123, 90, 71);
		contentPane.add(signUp);
		
		id_f = new JTextField();
		id_f.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
		id_f.setBounds(94, 63, 116, 24);
		contentPane.add(id_f);
		id_f.setColumns(10);
		
		pw_f = new JPasswordField();
		pw_f.setFont(new Font("굴림", Font.PLAIN, 15));
		pw_f.setBounds(94, 134, 116, 24);
		contentPane.add(pw_f);
		
		JLabel id_l = new JLabel("ID");
		id_l.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
		id_l.setHorizontalAlignment(SwingConstants.CENTER);
		id_l.setBounds(18, 66, 62, 18);
		contentPane.add(id_l);
		
		JLabel pw_l = new JLabel("PW");
		pw_l.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
		pw_l.setHorizontalAlignment(SwingConstants.CENTER);
		pw_l.setBounds(18, 137, 62, 18);
		contentPane.add(pw_l);
		this.setVisible(true);
	}
	public void setNickname(String n) {this.nickname = n;}
	public void setTryLogin(boolean b) {this.tryLogin = b;}
	public void setTryRegistration(char m) {reg.setTryRegistration(m);}  //'s': success //'i': id conflict //'n': nickname conflict //'p': password length fault
	class Registration extends JFrame {

		private JPanel contentPane;
		private JTextField nn_t;
		private JTextField id_t;
		private JPasswordField pw_t;
		private char tryRegistration = 0;
		public Registration() {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 502, 291);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			nn_t = new JTextField();
			nn_t.setBounds(131, 51, 116, 24);
			nn_t.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
			contentPane.add(nn_t);
			nn_t.setColumns(10);
			
			id_t = new JTextField();
			id_t.setBounds(131, 105, 116, 24);
			id_t.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
			contentPane.add(id_t);
			id_t.setColumns(10);
			
			pw_t = new JPasswordField();
			pw_t.setBounds(131, 162, 116, 24);
			contentPane.add(pw_t);
			
			JLabel nn_l = new JLabel("닉네임");
			nn_l.setHorizontalAlignment(SwingConstants.CENTER);
			nn_l.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
			nn_l.setBounds(39, 54, 78, 18);
			contentPane.add(nn_l);
			
			JLabel id_l = new JLabel("아이디");
			id_l.setHorizontalAlignment(SwingConstants.CENTER);
			id_l.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
			id_l.setBounds(39, 108, 78, 18);
			contentPane.add(id_l);
			
			JLabel pw_l = new JLabel("비밀번호");
			pw_l.setHorizontalAlignment(SwingConstants.CENTER);
			pw_l.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
			pw_l.setBounds(39, 165, 78, 18);
			contentPane.add(pw_l);
			
			JButton registration_b = new JButton("\uD68C\uC6D0\uAC00\uC785");
			registration_b.addActionListener(new ActionListener() { //'s': success //'i': id conflict //'n': nickname conflict //'p': password length fault
				public void actionPerformed(ActionEvent arg0) {
					Client.setNickName(nn_t.getText());
					Client.setId(id_t.getText());
					Client.setPw(pw_t.getText());
					Client.setTryLogin('r');
					nn_t.setText("");
					id_t.setText("");
					pw_t.setText("");
					while(tryRegistration==0) {System.out.println("");}
					if(tryRegistration=='s') 
					{
						JOptionPane.showMessageDialog(null, "회원가입에 성공했습니다!!");
						dispose();
					}
					else if(tryRegistration=='i')
					{
						JOptionPane.showMessageDialog(null, "아이디 중복입니다!!");
					}
					else if(tryRegistration=='n')
					{
						JOptionPane.showMessageDialog(null, "닉네임 중복입니다!!");
					}
					tryRegistration = 0;
				}
			});
			registration_b.setBounds(303, 54, 116, 72);
			registration_b.setBackground(new Color(255, 0, 255));
			registration_b.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
			contentPane.add(registration_b);
			
			JButton exit_b = new JButton("\uB098\uAC00\uAE30");
			exit_b.setBackground(new Color(255, 255, 0));
			exit_b.setFont(new Font("210 맨발의청춘 B", Font.PLAIN, 15));
			exit_b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			exit_b.setBounds(303, 138, 116, 72);
			contentPane.add(exit_b);
			this.setVisible(true);
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		}
		public void setTryRegistration(char a) {this.tryRegistration = a;}
	}
}
