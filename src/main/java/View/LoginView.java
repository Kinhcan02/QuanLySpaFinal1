package View;

import Controller.AuthController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;

public class LoginView extends JFrame {

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private JButton btnThoat;
    private AuthController authController;

    public LoginView(AuthController authController) {
        // Set global Look and Feel trước khi khởi tạo components
        setGlobalLookAndFeel();
        this.authController = authController;
        initComponents();
        setupUI();
        getRootPane().setDefaultButton(btnDangNhap);
    }

    private void setGlobalLookAndFeel() {
        try {
            // Sử dụng CrossPlatform Look and Feel cho toàn bộ ứng dụng
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // QUAN TRỌNG: Override các setting để buttons hiển thị đúng màu
            UIManager.put("Button.background", new Color(77, 138, 87));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.opaque", true);
            UIManager.put("Button.contentAreaFilled", true);
            UIManager.put("Button.borderPainted", false);
            UIManager.put("Button.focusPainted", false);

            // Fix cho các component khác
            UIManager.put("Panel.background", new Color(0x8C, 0xC9, 0x80));
            UIManager.put("Label.foreground", Color.WHITE);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.foreground", Color.BLACK);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        // Set up main frame
        setTitle("Đăng Nhập Hệ Thống");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // Main panel with background color
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        titlePanel.add(lblTitle);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblTenDangNhap = new JLabel("Tên đăng nhập:");
        lblTenDangNhap.setForeground(Color.WHITE);
        lblTenDangNhap.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblTenDangNhap, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        txtTenDangNhap = new JTextField(18);
        styleTextField(txtTenDangNhap);
        formPanel.add(txtTenDangNhap, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        lblMatKhau.setForeground(Color.WHITE);
        lblMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblMatKhau, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        txtMatKhau = new JPasswordField(18);
        styleTextField(txtMatKhau);
        formPanel.add(txtMatKhau, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Login button - Sử dụng màu RGB(77, 138, 87)
        btnDangNhap = new JButton("Đăng Nhập");
        styleLoginButton(btnDangNhap);
        btnDangNhap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyDangNhap();
            }
        });

        // Exit button
        btnThoat = new JButton("Thoát");
        styleExitButton(btnThoat);
        btnThoat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyThoat();
            }
        });

        buttonPanel.add(btnDangNhap);
        buttonPanel.add(btnThoat);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                xuLyThoat();
            }
        });

        // Enter key listener for login
        txtMatKhau.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyDangNhap();
            }
        });
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x6BAF6D), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
    }

    private void styleLoginButton(JButton button) {
        // Màu chính RGB(77, 138, 87)
        Color mainColor = new Color(77, 138, 87);
        Color hoverColor = new Color(67, 118, 77);
        Color borderColor = new Color(57, 98, 67);

        // Thiết lập UI cho nút
        button.setBackground(mainColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // QUAN TRỌNG: Đảm bảo nút hiển thị màu nền
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);

        // Sử dụng BasicButtonUI để tránh bị ghi đè
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(mainColor);
                }
            }
        });
    }

    private void styleExitButton(JButton button) {
        // Màu cho nút thoát
        Color mainColor = new Color(149, 165, 166);
        Color hoverColor = new Color(127, 140, 141);
        Color borderColor = new Color(107, 120, 121);

        // Thiết lập UI cho nút
        button.setBackground(mainColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // QUAN TRỌNG: Đảm bảo nút hiển thị màu nền
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);

        // Sử dụng BasicButtonUI để tránh bị ghi đè
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(mainColor);
                }
            }
        });
    }

    private void setupUI() {
        pack();
        setSize(450, 350);
        setLocationRelativeTo(null);
    }

    private void xuLyDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            hienThiThongBao("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi controller để xử lý đăng nhập
        authController.xuLyDangNhap(tenDangNhap, matKhau);
    }

    private void xuLyThoat() {
        authController.xuLyThoat();
    }

    // Các phương thức hiển thị thông báo với nút tùy chỉnh
    public void hienThiThongBaoDangNhapThanhCong() {
        JDialog dialog = createCustomDialog(
                "Đăng nhập thành công! Chào mừng " + txtTenDangNhap.getText(),
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
        );
        dialog.setVisible(true);
    }

    public void hienThiThongBaoDangNhapThatBai() {
        JDialog dialog = createCustomDialog(
                "Tên đăng nhập hoặc mật khẩu không đúng!",
                "Lỗi đăng nhập",
                JOptionPane.ERROR_MESSAGE
        );
        dialog.setVisible(true);

        txtMatKhau.setText("");
        txtTenDangNhap.requestFocus();
    }

    public void hienThiThongBao(String message, String title, int messageType) {
        JDialog dialog = createCustomDialog(message, title, messageType);
        dialog.setVisible(true);
    }

    public boolean xacNhanThoat() {
        // Tạo custom buttons
        JButton btnCo = new JButton("Có");
        JButton btnKhong = new JButton("Không");

        // Style nút "Có" giống nút thoát (màu xám)
        styleExitButton(btnCo);
        // Style nút "Không" giống nút đăng nhập (màu xanh)
        styleLoginButton(btnKhong);

        // Tạo panel chứa nội dung
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(0x8C, 0xC9, 0x80));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tạo icon và message
        JLabel messageLabel = new JLabel("Bạn có chắc chắn muốn thoát chương trình?");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Icon question
        Icon icon = UIManager.getIcon("OptionPane.questionIcon");

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        contentPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            contentPanel.add(iconLabel);
        }
        contentPanel.add(messageLabel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Panel chứa nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        buttonPanel.add(btnCo);
        buttonPanel.add(btnKhong);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Tạo JDialog
        JDialog dialog = new JDialog(this, "Xác nhận thoát", true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // Biến để lưu kết quả
        final boolean[] result = {false};

        // Xử lý sự kiện cho nút
        btnCo.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });

        btnKhong.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });

        // Xử lý khi đóng cửa sổ
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                result[0] = false;
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
        return result[0];
    }

    private JDialog createCustomDialog(String message, String title, int messageType) {
        // Tạo custom button OK
        JButton okButton = new JButton("OK");
        styleLoginButton(okButton);
        okButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(okButton);
            if (window != null) {
                window.dispose();
            }
        });

        // THÊM SỰ KIỆN ENTER CHO NÚT OK
        // Tạo Action cho phím Enter
        Action enterAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window window = SwingUtilities.getWindowAncestor(okButton);
                if (window != null) {
                    window.dispose();
                }
            }
        };

        // Đăng ký phím Enter cho nút OK
        okButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterAction");
        okButton.getActionMap().put("enterAction", enterAction);

        // Tạo panel chứa nội dung
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(0x8C, 0xC9, 0x80));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tạo icon và message
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Icon tùy theo loại message
        Icon icon = null;
        switch (messageType) {
            case JOptionPane.ERROR_MESSAGE:
                icon = UIManager.getIcon("OptionPane.errorIcon");
                break;
            case JOptionPane.INFORMATION_MESSAGE:
                icon = UIManager.getIcon("OptionPane.informationIcon");
                break;
            case JOptionPane.WARNING_MESSAGE:
                icon = UIManager.getIcon("OptionPane.warningIcon");
                break;
            case JOptionPane.QUESTION_MESSAGE:
                icon = UIManager.getIcon("OptionPane.questionIcon");
                break;
        }

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        contentPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            contentPanel.add(iconLabel);
        }
        contentPanel.add(messageLabel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Panel chứa nút OK
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0x8C, 0xC9, 0x80));
        buttonPanel.add(okButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Tạo JDialog
        JDialog dialog = new JDialog(this, title, true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // QUAN TRỌNG: Đặt nút OK làm default button cho dialog
        dialog.getRootPane().setDefaultButton(okButton);

        return dialog;
    }

    public void hienThiManHinhDangNhap() {
        setVisible(true);
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        txtTenDangNhap.requestFocus();
    }

    public void dongManHinh() {
        dispose();
    }

    public static void main(String[] args) {
        // Khởi chạy ứng dụng
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Khởi tạo controller và view
                AuthController authController = new AuthController();
                authController.khoiDong();
            }
        });
    }
}