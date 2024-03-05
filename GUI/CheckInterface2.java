package GUI;

import javax.swing.*;
import java.awt.*;

public class CheckInterface2 extends JPanel {
    public CheckInterface2() {
        setLayout(new GridBagLayout()); // 使用 GridBagLayout 布局管理器
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2); // 设置组件之间的间距

        // 创建和添加标签
        JLabel lblDepartureDate = new JLabel("Departure Date");
        JLabel lblFlightNumber = new JLabel("Flight Number");
        JLabel lblReservationNumber = new JLabel("Reservation Number");
        JLabel lblLastName = new JLabel("Last Name:");
        JLabel lblFirstName = new JLabel("First Name:");

        // 创建和添加下拉列表
        JComboBox<String> cbYear = new JComboBox<>(new String[]{"2024", "2025"});
        JComboBox<String> cbMonth = new JComboBox<>(new String[]{"January", "February", "March"});
        JComboBox<String> cbDay = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});

        // 创建和添加文本字段
        JTextField txtFlightNumber = new JTextField(5);
        JTextField txtReservationNumber = new JTextField(5);
        JTextField txtLastName = new JTextField(10);
        JTextField txtFirstName = new JTextField(10);

        // 添加组件到面板
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblDepartureDate, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(cbYear, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        add(cbMonth, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        add(cbDay, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblFlightNumber, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(txtFlightNumber, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        add(lblReservationNumber, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        add(txtReservationNumber, gbc);

        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        add(lblLastName, gbc);

        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        add(txtLastName, gbc);

        gbc.gridx = 4;
        gbc.gridy = 1;
        add(lblFirstName, gbc);

        gbc.gridx = 5;
        gbc.gridy = 1;
        add(txtFirstName, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Reservation Information");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new CheckInterface2());
            frame.pack();
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }
}


