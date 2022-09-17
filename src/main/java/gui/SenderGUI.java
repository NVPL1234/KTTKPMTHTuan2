package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;
import java.awt.SystemColor;
import java.awt.Color;

public class SenderGUI extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTextField txtID;
	private JButton btnSender;
	private JTextField txtName;
	private JTextField txtBirth;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SenderGUI frame = new SenderGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SenderGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 100, 818, 407);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.window);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtID = new JTextField();
		txtID.setFont(new Font("Times New Roman", Font.BOLD, 24));
		txtID.setBounds(432, 24, 270, 38);
		contentPane.add(txtID);
		txtID.setColumns(10);

		btnSender = new JButton("Gửi");
		btnSender.setForeground(new Color(0, 0, 0));
		btnSender.setBackground(new Color(0, 0, 0));
		btnSender.setFont(new Font("Times New Roman", Font.BOLD, 24));
		btnSender.setBounds(488, 252, 162, 38);
		contentPane.add(btnSender);

		JLabel lblID = new JLabel("M\u00E3 s\u1ED1:");
		lblID.setFont(new Font("Times New Roman", Font.BOLD, 24));
		lblID.setBounds(165, 23, 88, 38);
		contentPane.add(lblID);

		JLabel lblName = new JLabel("H\u1ECD t\u00EAn: ");
		lblName.setFont(new Font("Times New Roman", Font.BOLD, 24));
		lblName.setBounds(165, 92, 88, 38);
		contentPane.add(lblName);

		txtName = new JTextField();
		txtName.setFont(new Font("Times New Roman", Font.BOLD, 24));
		txtName.setBounds(432, 93, 270, 38);
		contentPane.add(txtName);
		txtName.setColumns(10);

		JLabel lblBirth = new JLabel("Ng\u00E0y sinh\r\n:");
		lblBirth.setFont(new Font("Times New Roman", Font.BOLD, 24));
		lblBirth.setBounds(166, 171, 116, 38);
		contentPane.add(lblBirth);

		txtBirth = new JTextField();
		txtBirth.setFont(new Font("Times New Roman", Font.BOLD, 24));
		txtBirth.setBounds(432, 172, 270, 38);
		contentPane.add(txtBirth);
		txtBirth.setColumns(10);

		btnSender.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		Object object = e.getSource();
		if (object.equals(btnSender)) {
			try {
				send();

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void send() throws Exception {
		long id = Long.parseLong(txtID.getText().trim());
		String name = txtName.getText().trim();
		LocalDate birth = LocalDate.parse(txtBirth.getText());
		BasicConfigurator.configure();
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		Context ctx = new InitialContext(settings);
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
		Connection con = factory.createConnection("admin", "admin");
		con.start();
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(destination);
		Message msg = session.createTextMessage("Hello mesage from ActiveMQ");
		producer.send(msg);

		Person perso = new Person(id, name, birth);
		String xml = new XMLConvert<Person>(perso).object2XML(perso);
		msg = session.createTextMessage(xml);
		producer.send(msg);
		session.close();
		con.close();
		System.out.println("Finished...");
	}
}