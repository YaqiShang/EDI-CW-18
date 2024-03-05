package GUI;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;


public class CheckInInterface extends JFrame{
	private JTextField lastNameField;
	private JTextField bookingRefField;
	private JTextField baggageWeightField;
	private JTextField baggageDimensionsField;
	private JButton checkInButton;
	private JTextArea resultArea;
	
	public CheckInInterface() {
		setTitle("Electronic Check-In Kiosk");
		setSize(600, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initUI();
	}
	
	private void initUI() {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		

		lastNameField = new JTextField();
		bookingRefField = new JTextField();
		baggageWeightField = new JTextField();
		baggageDimensionsField = new JTextField();


		Dimension labelsize_short = new Dimension(130, 30);
		Dimension labelsize_long = new Dimension(150, 30);
		Color mihuang = new Color(0xffffeb);
		Color lanse = new Color(18,120,200);
		ImageIcon imageIcon = new ImageIcon("GUI/baggage.jpg");

		checkInButton = new JButton();
		resultArea = new JTextArea();
		resultArea.setEditable(false);
		JPanel panel_title = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel_title.setPreferredSize(new Dimension(600, 0));

		JLabel title = new JLabel("Make My Check-In");
		panel_title.add(title);

		
		// title.setPreferredSize(new Dimension(600,50));
		title.setFont(new Font(title.getFont().getName(), Font.PLAIN, 20)); // 设置字体大小为24
		// title.setHorizontalAlignment(SwingConstants.LEFT); // 设置文本水平居中
		panel_title.setOpaque(true);
		panel_title.setBackground(lanse);
		add(panel_title);
		title.setForeground(new Color(255, 255, 255));



		JPanel panel_fl = new JPanel();
		panel_fl.setPreferredSize(new Dimension(600, 0));

		JPanel panel_name = new JPanel();
		lastNameField.setPreferredSize(new Dimension(90, 30));

		JLabel label_name = new JLabel("         Last Name:");
		label_name.setPreferredSize(labelsize_short);
		panel_name.add(label_name);
		panel_name.add(lastNameField);

		JPanel panel_ref = new JPanel();
		bookingRefField.setPreferredSize(new Dimension(150, 30));
		JLabel label_ref = new JLabel("Booking Reference:");
		label_ref.setPreferredSize(labelsize_long);
		panel_ref.add(label_ref);
		panel_ref.add(bookingRefField);

		panel_fl.add(panel_name);
		panel_fl.add(panel_ref);
		add(panel_fl);

		JPanel panel_bweight = new JPanel();
		JPanel panel_bdimension = new JPanel();
		JPanel panel_sl = new JPanel();
		panel_sl.setPreferredSize(new Dimension(600, 0));

		JLabel label_bweight = new JLabel("Baggage Weight:");
		label_bweight.setPreferredSize(labelsize_short);

		panel_bweight.add(label_bweight);
		baggageWeightField.setPreferredSize(new Dimension(90, 30));
		panel_bweight.add(baggageWeightField);

		JLabel label_bdimension = new JLabel("Baggage Dimensions:");
		label_bdimension.setPreferredSize(labelsize_long);

		

		panel_bdimension.add(label_bdimension);
		baggageDimensionsField.setPreferredSize(new Dimension(120, 30));
		panel_bdimension.add(baggageDimensionsField);
		panel_sl.add(panel_bweight);
		panel_sl.add(panel_bdimension);
		add(panel_sl);
		
		// // 设置布局管理器
		// panel_fl.setLayout(new GridBagLayout());
		// panel_sl.setLayout(new GridBagLayout());
		// GridBagConstraints gbc = new GridBagConstraints();
		// gbc.gridwidth = GridBagConstraints.RELATIVE; // 设置这个组件是行中的倒数第二个
		// gbc.fill = GridBagConstraints.HORIZONTAL;    // 设置这个组件水平填充空间

		// // 第一行
		// bookingRefField.setPreferredSize(new Dimension(200, 30));
		// JLabel labelRef = new JLabel("Booking Reference:");
		// gbc.weightx = 0.1;  // 设置这个组件随窗体变化的权重
		// panel_fl.add(labelRef, gbc);

		// gbc.weightx = 1.0;  // 设置这个组件随窗体变化的权重
		// panel_fl.add(bookingRefField, gbc);

		// // 第二行
		// baggageWeightField.setPreferredSize(new Dimension(120, 30));
		// JLabel labelWeight = new JLabel("Baggage Weight:");
		// gbc.weightx = 0.1;  // 设置这个组件随窗体变化的权重
		// panel_sl.add(labelWeight, gbc);

		// gbc.weightx = 1.0;  // 设置这个组件随窗体变化的权重
		// panel_sl.add(baggageWeightField, gbc);

		// // 添加到主面板
		// add(panel_fl);
		// add(panel_sl);

		JPanel panel_tl = new JPanel();
		JLabel picture = new JLabel(imageIcon);
		add(picture);

		checkInButton.setPreferredSize(new Dimension(100, 40));
		checkInButton.setText("Check In");
		panel_tl.add(checkInButton);
		add(panel_tl);

		panel_fl.setBackground(mihuang);
		panel_sl.setBackground(mihuang);
		panel_bdimension.setBackground(mihuang);
		panel_bweight.setBackground(mihuang);
		panel_name.setBackground(mihuang);
		panel_ref.setBackground(mihuang);
		panel_tl.setBackground(mihuang);

		checkInButton.setBackground(lanse);
		checkInButton.setForeground(lanse);

		checkInButton.setFont(new Font(checkInButton.getFont().getName(), Font.PLAIN, 16));
		// lastNameField.setBackground(());


		// Border titledBorder = BorderFactory.createTitledBorder("Enter Text");
		// lastNameField.setBorder(titledBorder);

		// Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		// lastNameField.setBorder(lineBorder);

		
		
		// add(new JScrollPane(resultArea));

		checkInButton.addActionListener(e -> handleCheckIn());

	}

	private void handleCheckIn(){

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			CheckInInterface ci = new CheckInInterface();
			ci.setVisible(true);
		});
	}
	
	
	
}
