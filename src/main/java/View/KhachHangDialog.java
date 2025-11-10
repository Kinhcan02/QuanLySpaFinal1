package View;

import javax.swing.*;
import java.awt.*;

public class KhachHangDialog extends JDialog {

    private JTextField txtHoTen;
    private JTextField txtSoDienThoai;
    private JComboBox<String> cboLoaiKhach;
    private JTextArea txtGhiChu;
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
        setResizable(true); // Cho phép resize để to hơn
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 700)); // Tăng kích thước ưu tiên
        setSize(900, 700); // Tăng kích thước thực tế

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(COLOR_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40)); // Tăng padding

        // Panel tiêu đề
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(COLOR_BACKGROUND);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0)); // Tăng khoảng cách dưới

        JLabel lblTitle = new JLabel("THÊM KHÁCH HÀNG MỚI", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Tăng font size
        lblTitle.setForeground(COLOR_PRIMARY);

        JLabel lblSubtitle = new JLabel("Điền thông tin khách hàng mới", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Tăng font size
        lblSubtitle.setForeground(new Color(102, 102, 102));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 8)); // Tăng khoảng cách giữa các dòng
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
        JPanel panel = new JPanel(new GridLayout(4, 1, 25, 25)); // Tăng khoảng cách giữa các component
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0)); // Tăng padding

        panel.add(createFieldPanel("Họ tên *", txtHoTen = new JTextField()));
        panel.add(createFieldPanel("Số điện thoại *", txtSoDienThoai = new JTextField()));
        panel.add(createComboPanel("Loại khách", cboLoaiKhach = new JComboBox<>(new String[]{"Thường", "Thân thiết", "VIP"})));
        
        panel.add(createTextAreaPanel("Ghi chú", txtGhiChu = new JTextArea(6, 30))); // Tăng số dòng text area

        return panel;
    }

    private JPanel createFieldPanel(String label, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(15, 8)); // Tăng khoảng cách
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Tăng font size
        lbl.setForeground(new Color(51, 51, 51));

        styleTextField(textField);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createComboPanel(String label, JComboBox<String> comboBox) {
        JPanel panel = new JPanel(new BorderLayout(18, 8)); // Tăng khoảng cách
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Tăng font size
        lbl.setForeground(new Color(51, 51, 51));
        
        styleComboBox(comboBox);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTextAreaPanel(String label, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout(45, 8)); // Tăng khoảng cách
        panel.setBackground(COLOR_BACKGROUND);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Tăng font size
        lbl.setForeground(new Color(51, 51, 51));

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Tăng font size
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(0, 150)); // Tăng chiều cao scroll pane

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Tăng khoảng cách giữa các nút
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Tăng padding trên

        JButton btnHuy = createButton("HỦY", new Color(108, 117, 125), Color.WHITE);
        JButton btnLuu = createButton("LƯU", COLOR_PRIMARY, Color.WHITE);

        // Tăng kích thước nút
        btnHuy.setPreferredSize(new Dimension(120, 45));
        btnLuu.setPreferredSize(new Dimension(120, 45));

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
                BorderFactory.createEmptyBorder(14, 16, 14, 16) // Tăng padding
        ));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Tăng font size
        textField.setPreferredSize(new Dimension(0, 50)); // Tăng chiều cao
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16) // Tăng padding
        ));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Tăng font size
        comboBox.setPreferredSize(new Dimension(0, 50)); // Tăng chiều cao
    }

    private JButton createButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Tăng font size
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(12, 30, 12, 30) // Tăng padding
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
