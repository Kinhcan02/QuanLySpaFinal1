package View;

import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

public class KhachHangDialog extends JDialog {

    private JTextField txtHoTen;
    private JTextField txtSoDienThoai;
    private JComboBox<String> cboLoaiKhach;
    private JTextArea txtGhiChu;
    private JDateChooser dateChooserNgaySinh;
    private boolean confirmed = false;

    // Màu sắc đồng bộ
    private final Color COLOR_PRIMARY = new Color(74, 138, 87);
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_BORDER = new Color(222, 226, 230);

    public KhachHangDialog(Frame parent) {
        super(parent, "Thêm Khách Hàng Mới", true);
        initComponents();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false); // Không cho phép resize để giữ layout ổn định
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 650)); // Kích thước vừa phải
        setSize(500, 650);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(COLOR_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // Padding vừa phải

        // Panel tiêu đề
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(COLOR_BACKGROUND);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel lblTitle = new JLabel("THÊM KHÁCH HÀNG MỚI", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_PRIMARY);

        JLabel lblSubtitle = new JLabel("Điền thông tin khách hàng mới", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(102, 102, 102));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(COLOR_BACKGROUND);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);

        titlePanel.add(textPanel, BorderLayout.CENTER);

        // Panel form
        JPanel formPanel = createFormPanel();

        // Panel nút
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0); // Khoảng cách giữa các component

        // Họ tên
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createFieldPanel("Họ tên *", txtHoTen = new JTextField()), gbc);

        // Số điện thoại
        gbc.gridy = 1;
        panel.add(createFieldPanel("Số điện thoại *", txtSoDienThoai = new JTextField()), gbc);

        // Ngày sinh
        gbc.gridy = 2;
        panel.add(createDatePanel("Ngày sinh", dateChooserNgaySinh = new JDateChooser()), gbc);

        // Loại khách
        gbc.gridy = 3;
        panel.add(createComboPanel("Loại khách", cboLoaiKhach = new JComboBox<>(new String[]{"Thân thiết", "Thường xuyên", "Mới"})), gbc);
        
        // Ghi chú
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(createTextAreaPanel("Ghi chú", txtGhiChu = new JTextArea(4, 20)), gbc);

        return panel;
    }

    private JPanel createFieldPanel(String label, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(51, 51, 51));

        styleTextField(textField);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDatePanel(String label, JDateChooser dateChooser) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(51, 51, 51));

        styleDateChooser(dateChooser);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(dateChooser, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createComboPanel(String label, JComboBox<String> comboBox) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(51, 51, 51));
        
        styleComboBox(comboBox);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTextAreaPanel(String label, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(51, 51, 51));

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(0, 100));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton btnHuy = createButton("HỦY", new Color(108, 117, 125), Color.WHITE);
        JButton btnLuu = createButton("LƯU", COLOR_PRIMARY, Color.WHITE);

        // Kích thước nút vừa phải
        btnHuy.setPreferredSize(new Dimension(100, 40));
        btnLuu.setPreferredSize(new Dimension(100, 40));

        panel.add(btnHuy);
        panel.add(btnLuu);

        // Sự kiện cho nút
        btnLuu.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        btnHuy.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        return panel;
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setPreferredSize(new Dimension(0, 40));
    }

    private void styleDateChooser(JDateChooser dateChooser) {
        dateChooser.setBackground(Color.WHITE);
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateChooser.setPreferredSize(new Dimension(0, 40));
        
        // Định dạng ngày tháng
        dateChooser.setDateFormatString("dd/MM/yyyy");
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(0, 40));
    }

    private JButton createButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    // Getter methods
    public String getHoTen() {
        return txtHoTen.getText().trim();
    }

    public String getSoDienThoai() {
        return txtSoDienThoai.getText().trim();
    }

    public java.util.Date getNgaySinh() {
        return dateChooserNgaySinh.getDate();
    }

    public String getLoaiKhach() {
        return (String) cboLoaiKhach.getSelectedItem();
    }

    public String getGhiChu() {
        return txtGhiChu.getText().trim();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}