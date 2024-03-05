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
	private JButton quitButton;
	private JTextArea resultArea;
	private JLabel feeLabel;
	
	public CheckInInterface() {
		setTitle("Electronic Check-In Kiosk");
		setSize(600, 320);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		initUI();
	}
	
	private void initUI() {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		

		lastNameField = new JTextField();
		bookingRefField = new JTextField();
		baggageWeightField = new JTextField();
		baggageDimensionsField = new JTextField();

		feeLabel = new JLabel();

		quitButton = new JButton();

		Dimension labelsize_short = new Dimension(130, 30);
		Dimension labelsize_long = new Dimension(150, 30);
		Color mihuang = new Color(0xffffeb);
		Color lanse = new Color(18,120,200);
		Color hongse = new Color(0x8c0606);
		ImageIcon imageIcon = new ImageIcon("GUI/baggage.jpg");
		Border border = BorderFactory.createLineBorder(lanse);
		JSeparator separator = new JSeparator();

		feeLabel.setText("( Total Fee: 0 )");
        

		JPanel panel_sep = new JPanel();

		separator.setForeground(lanse);
		panel_sep.setBackground(mihuang);
		panel_sep.setPreferredSize(new Dimension(600, 0));
		// separator.setPreferredSize(new Dimension(separator.getPreferredSize().width, 10)); // 将 10 改成您希望的粗细值




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
		panel_fl.setPreferredSize(new Dimension(600, 10));

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
		panel_sl.setPreferredSize(new Dimension(600, 20));

		JLabel label_bweight = new JLabel("Baggage Weight(kg):");
		label_bweight.setPreferredSize(labelsize_short);

		panel_bweight.add(label_bweight);
		baggageWeightField.setPreferredSize(new Dimension(90, 30));
		panel_bweight.add(baggageWeightField);

		JLabel label_bdimension = new JLabel("Baggage Size(LxWxH):");
		label_bdimension.setPreferredSize(labelsize_long);

		

		panel_bdimension.add(label_bdimension);
		baggageDimensionsField.setPreferredSize(new Dimension(120, 30));
		panel_bdimension.add(baggageDimensionsField);
		panel_sl.add(panel_bweight);
		panel_sl.add(panel_bdimension);
		add(panel_sl);

		JPanel panel_tl = new JPanel();

		panel_sep.add(feeLabel);
		// JLabel picture = new JLabel(imageIcon);
		// add(picture);

		// panel_sep.add(separator);
		panel_sep.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, lanse)); // 上边框作为分隔线
		add(panel_sep);

		checkInButton.setPreferredSize(new Dimension(100, 40));
		checkInButton.setText("Check In");
		panel_tl.add(checkInButton);
		add(panel_tl);

		quitButton.setPreferredSize(new Dimension(100, 40));
		quitButton.setText("Quit");
		panel_tl.add(quitButton);
		add(panel_tl);

		panel_fl.setBackground(mihuang);
		panel_sl.setBackground(mihuang);
		panel_bdimension.setBackground(mihuang);
		panel_bweight.setBackground(mihuang);
		panel_name.setBackground(mihuang);
		panel_ref.setBackground(mihuang);
		panel_tl.setBackground(mihuang);

		// lastNameField.setFont(new Font(lastNameField.getFont().getName(), Font.PLAIN, 15));
		feeLabel.setForeground(hongse);
		feeLabel.setFont(new Font(feeLabel.getFont().getName(), Font.PLAIN, 12));

		// panel_all.add(panel_fl);
		// panel_all.add(panel_sl);
		// add(panel_all);
		// panel_all.add(panel_fl);

		// panel_all.setBorder(border);
		// panel_ref.setBorder(border);
		quitButton.setBackground(lanse);
		quitButton.setForeground(hongse);

		checkInButton.setBackground(lanse);
		checkInButton.setForeground(lanse);

		checkInButton.setFont(new Font(checkInButton.getFont().getName(), Font.PLAIN, 16));
		quitButton.setFont(new Font(checkInButton.getFont().getName(), Font.PLAIN, 16));
		// lastNameField.setBackground(());


		// Border titledBorder = BorderFactory.createTitledBorder("Enter Text");
		// lastNameField.setBorder(titledBorder);

		// Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		// lastNameField.setBorder(lineBorder);

		
		
		// add(new JScrollPane(resultArea));

		checkInButton.addActionListener(e -> handleCheckIn());
		quitButton.addActionListener(e -> handleQuit());

	}

	private void handleCheckIn(){
		String weightText = baggageWeightField.getText();
        String dimensionsText = baggageDimensionsField.getText();
        double weight = Double.parseDouble(weightText);
        String[] dimensions = dimensionsText.split(" ");
        double length = Double.parseDouble(dimensions[0]);
        double width = Double.parseDouble(dimensions[1]);
        double height = Double.parseDouble(dimensions[2]);
        double volume = length * width * height;

		double extraWeightFee = 0;
        double extraVolumeFee = 0;

		if (weight > 20) {
            extraWeightFee = (weight - 20) * 10;
        }

        if (volume > 10) {
            extraVolumeFee = 10;
        }

        double totalFee = extraWeightFee + extraVolumeFee;
        feeLabel.setText("( Weight Fee: "+extraWeightFee+"    Total Fee: " + totalFee+"   Volume Fee: "+extraVolumeFee + " )");
	}

	private void handleQuit(){

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			CheckInInterface ci = new CheckInInterface();
			ci.setVisible(true);
		});
	}
	
	
	
}
