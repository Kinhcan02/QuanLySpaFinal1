package View;

import Controller.QuanLyDichVuController;
import Controller.QuanLyKhachHangController;
import Controller.QuanLyNhanVienController;
import Controller.MainViewController;
import Controller.QuanLyCaLamController;
import Service.CaLamService;
import Service.KhachHangService;
import Service.NhanVienService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainView extends JFrame {

    private JDesktopPane desktopPane;
    private JButton btnThongBao, btnQuanLyNguyenLieu, btnDatDichVu, btnQuanLyNhanVien, btnQuanLyCaLam, btnQuanLyKhachHang, btnQuanLyDichVu, btnThongKe, btnCaiDat, btnThoat;
    private JLabel lblUserInfo, lblVersion;
    private QuanLyDichVuView quanLyDichVuView;
    private QuanLyDichVuController quanLyDichVuController;
    private MainViewController mainViewController;

    // Màu sắc mới
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_MENU = new Color(0x4D, 0x8A, 0x57);      // Màu menu #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                      // Màu chữ #ffffff

    public MainView() {
        mainViewController = new MainViewController(this);
        initUI();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                xacNhanThoatChuongTrinh();
            }
        });

    }

    private void initUI() {
        setTitle("HỆ THỐNG QUẢN LÝ SPA");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main layout
        setLayout(new BorderLayout());

        createSidebar();
        createMainContent();
        createHeader();

        // Thiết lập sự kiện cho các nút menu
        setupMenuEvents();
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_MENU); // Màu menu #4d8a57
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Title
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ SPA BEAUTY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT); // Màu chữ #ffffff

        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(COLOR_MENU); // Màu menu #4d8a57

        lblUserInfo = new JLabel("Xin chào: Quản trị viên | Admin");
        lblUserInfo.setForeground(COLOR_TEXT); // Màu chữ #ffffff
        lblUserInfo.setFont(new Font("Arial", Font.PLAIN, 14));

        userPanel.add(lblUserInfo);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(COLOR_MENU.darker()); // Màu menu đậm hơn
        sidebarPanel.setPreferredSize(new Dimension(300, getHeight()));
        sidebarPanel.setLayout(new BorderLayout());

        // Logo/Title area
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(COLOR_MENU); // Màu menu #4d8a57
        logoPanel.setPreferredSize(new Dimension(300, 150));
        logoPanel.setLayout(new GridLayout(3, 1));

        JLabel lblLogo = new JLabel("💆", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblLogo.setForeground(COLOR_TEXT); // Màu chữ #ffffff

        JLabel lblMainTitle = new JLabel("SPA BEAUTY", JLabel.CENTER);
        lblMainTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblMainTitle.setForeground(COLOR_TEXT); // Màu chữ #ffffff

        JLabel lblSubTitle = new JLabel("Management System", JLabel.CENTER);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubTitle.setForeground(COLOR_TEXT); // Màu chữ #ffffff

        logoPanel.add(lblLogo);
        logoPanel.add(lblMainTitle);
        logoPanel.add(lblSubTitle);

        // Navigation buttons với ScrollPane
        JPanel navPanel = new JPanel();
        navPanel.setBackground(COLOR_MENU.darker()); // Màu menu đậm hơn
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Tạo các nút menu - THÊM 2 NÚT MỚI Ở ĐẦU
        btnThongBao = createNavButton("THÔNG BÁO", "Xem thông báo và cảnh báo hệ thống");
        btnQuanLyNguyenLieu = createNavButton("QUẢN LÝ NGUYÊN LIỆU", "Quản lý kho nguyên liệu");
        btnDatDichVu = createNavButton("ĐẶT DỊCH VỤ", "Đặt lịch và quản lý dịch vụ");
        btnQuanLyNhanVien = createNavButton("QUẢN LÝ NHÂN VIÊN", "Quản lý thông tin nhân viên");
        btnQuanLyCaLam = createNavButton("QUẢN LÝ CA LÀM", "Quản lý ca làm của nhân viên");
        btnQuanLyKhachHang = createNavButton("QUẢN LÝ KHÁCH HÀNG", "Quản lý thông tin khách hàng");
        btnQuanLyDichVu = createNavButton("QUẢN LÝ DỊCH VỤ", "Quản lý danh mục dịch vụ");
        btnThongKe = createNavButton("THỐNG KÊ", "Báo cáo và thống kê");
        btnCaiDat = createNavButton("CÀI ĐẶT", "Cài đặt hệ thống");

        // Thêm các nút vào panel với khoảng cách - THÊM 2 NÚT MỚI Ở ĐẦU
        navPanel.add(btnThongBao);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnQuanLyNguyenLieu);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnDatDichVu);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnQuanLyNhanVien);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnQuanLyCaLam);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnQuanLyKhachHang);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnQuanLyDichVu);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnThongKe);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnCaiDat);
        navPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Khoảng cách trước separator

        // Separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setBackground(COLOR_MENU.brighter());
        separator.setForeground(COLOR_MENU.brighter());
        separator.setMaximumSize(new Dimension(270, 2));
        navPanel.add(separator);
        navPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Khoảng cách sau separator

        btnThoat = createNavButton("THOÁT", "Đóng hệ thống");
        navPanel.add(btnThoat);

        // Tạo JScrollPane cho navigation panel
        JScrollPane scrollPane = new JScrollPane(navPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(COLOR_MENU.darker());
        scrollPane.getViewport().setBackground(COLOR_MENU.darker());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Customize scroll bar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16); // Tốc độ cuộn
        // SỬA MÀU SCROLLBAR CHO ĐỒNG BỘ
        verticalScrollBar.setBackground(COLOR_MENU.darker());
        verticalScrollBar.setForeground(COLOR_MENU.brighter());
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = COLOR_MENU.brighter();    // Màu phần di chuyển
                this.trackColor = COLOR_MENU.darker();      // Màu đường ray
                this.thumbDarkShadowColor = COLOR_MENU.brighter();
                this.thumbHighlightColor = COLOR_MENU.brighter();
                this.thumbLightShadowColor = COLOR_MENU.brighter();
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        // Version info
        JPanel versionPanel = new JPanel();
        versionPanel.setBackground(COLOR_MENU); // Màu menu #4d8a57
        versionPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        lblVersion = new JLabel("Phiên bản 1.0 - SPA Management", JLabel.CENTER);
        lblVersion.setForeground(COLOR_TEXT); // Màu chữ #ffffff
        lblVersion.setFont(new Font("Arial", Font.PLAIN, 11));
        versionPanel.add(lblVersion);

        sidebarPanel.add(logoPanel, BorderLayout.NORTH);
        sidebarPanel.add(scrollPane, BorderLayout.CENTER); // Sử dụng scrollPane thay vì navPanel trực tiếp
        sidebarPanel.add(versionPanel, BorderLayout.SOUTH);

        add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton createNavButton(String title, String tooltip) {
        JButton button = new JButton(title);
        button.setBackground(COLOR_MENU.darker()); // Màu menu đậm hơn
        button.setForeground(COLOR_TEXT); // Màu chữ #ffffff
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setFocusPainted(false);
        button.setToolTipText(tooltip);
        button.setMaximumSize(new Dimension(270, 50)); // Fixed width for scroll consistency
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_MENU.brighter()); // Màu sáng hơn khi hover
                button.setForeground(COLOR_TEXT);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_MENU.darker()); // Trở lại màu menu đậm
                button.setForeground(COLOR_TEXT);
            }
        });

        return button;
    }

    private void createMainContent() {
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(COLOR_BACKGROUND); // Màu nền #8cc980

        add(desktopPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void setupMenuEvents() {
        // Sử dụng controller để xử lý sự kiện
        btnThongBao.addActionListener(mainViewController);
        btnQuanLyNguyenLieu.addActionListener(mainViewController);
        btnDatDichVu.addActionListener(mainViewController);
        btnQuanLyNhanVien.addActionListener(mainViewController);
        btnQuanLyCaLam.addActionListener(mainViewController);
        btnQuanLyKhachHang.addActionListener(mainViewController);
        btnQuanLyDichVu.addActionListener(mainViewController);
        btnThongKe.addActionListener(mainViewController);
        btnCaiDat.addActionListener(mainViewController);
        btnThoat.addActionListener(mainViewController);
    }

    // PHƯƠNG THỨC HIỂN THỊ THÔNG BÁO CUSTOM
    public void hienThiThongBao(String message, String title, int messageType) {
        JDialog dialog = createCustomDialog(message, title, messageType);
        dialog.setVisible(true);
    }

    // PHƯƠNG THỨC XÁC NHẬN THOÁT CUSTOM
    public void xacNhanThoatChuongTrinh() {
        // Tạo custom buttons
        JButton btnCo = new JButton("Có");
        JButton btnKhong = new JButton("Không");

        // Style nút "Có" giống nút thoát (màu xám)
        styleExitButton(btnCo);
        // Style nút "Không" giống nút đăng nhập (màu xanh)
        styleLoginButton(btnKhong);

        // Tạo panel chứa nội dung
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tạo icon và message
        JLabel messageLabel = new JLabel("Bạn có chắc muốn thoát chương trình không?");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Icon question
        Icon icon = UIManager.getIcon("OptionPane.questionIcon");

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        contentPanel.setBackground(COLOR_BACKGROUND);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            contentPanel.add(iconLabel);
        }
        contentPanel.add(messageLabel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Panel chứa nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(COLOR_BACKGROUND);
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
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                result[0] = false;
                dialog.dispose();
            }
        });

        // Đặt nút "Không" làm default button
        dialog.getRootPane().setDefaultButton(btnKhong);

        dialog.setVisible(true);

        if (result[0]) {
            System.exit(0);
        }
    }

    // PHƯƠNG THỨC TẠO CUSTOM DIALOG
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

        // Tạo panel chứa nội dung
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
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
        contentPanel.setBackground(COLOR_BACKGROUND);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            contentPanel.add(iconLabel);
        }
        contentPanel.add(messageLabel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Panel chứa nút OK
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(COLOR_BACKGROUND);
        buttonPanel.add(okButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Tạo JDialog
        JDialog dialog = new JDialog(this, title, true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // Đặt nút OK làm default button
        dialog.getRootPane().setDefaultButton(okButton);

        return dialog;
    }

    // PHƯƠNG THỨC STYLE BUTTON ĐĂNG NHẬP
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

    // PHƯƠNG THỨC STYLE BUTTON THOÁT
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

    public void showQuanLyKhachHang() {
        try {
            // Tạo JInternalFrame để chứa QuanLyKhachHangView
            JInternalFrame internalFrame = new JInternalFrame(
                    "Quản Lý Khách Hàng",
                    true, // resizable
                    true, // closable
                    true, // maximizable
                    true // iconifiable
            );

            // Tạo QuanLyKhachHangView và controller
            QuanLyKhachHangView quanLyKhachHangView = new QuanLyKhachHangView();
            KhachHangService khachHangService = new KhachHangService();
            QuanLyKhachHangController quanLyKhachHangController = new QuanLyKhachHangController(quanLyKhachHangView, khachHangService);

            // Thiết lập internal frame
            internalFrame.setContentPane(quanLyKhachHangView);
            internalFrame.pack();

            // Hiển thị internal frame trong desktop pane
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở quản lý khách       hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showQuanLyNhanVien() {
        try {
            JInternalFrame internalFrame = new JInternalFrame(
                    "Quản Lý Nhân Viên",
                    true, true, true, true
            );

            QuanLyNhanVienView quanLyNhanVienView = new QuanLyNhanVienView();
            NhanVienService nhanVienService = new NhanVienService();
            QuanLyNhanVienController quanLyNhanVienController = new QuanLyNhanVienController(quanLyNhanVienView, nhanVienService);

            internalFrame.setContentPane(quanLyNhanVienView);
            internalFrame.pack();
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở quản lý nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showQuanLyCaLam() {
        try {
            JInternalFrame internalFrame = new JInternalFrame(
                    "Quản Lý Ca Làm",
                    true, true, true, true
            );

            QuanLyCaLamView quanLyCaLamView = new QuanLyCaLamView();
            CaLamService caLamService = new CaLamService();
            QuanLyCaLamController quanLyCaLamController = new QuanLyCaLamController(quanLyCaLamView, caLamService);

            internalFrame.setContentPane(quanLyCaLamView);
            internalFrame.pack();
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở quản lý ca làm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showQuanLyDichVu() {
        try {
            // Tạo JInternalFrame để chứa QuanLyDichVuView
            JInternalFrame internalFrame = new JInternalFrame(
                    "Quản Lý Dịch Vụ",
                    true, // resizable
                    true, // closable
                    true, // maximizable
                    true // iconifiable
            );

            // Tạo QuanLyDichVuView và controller
            quanLyDichVuView = new QuanLyDichVuView();
            quanLyDichVuController = new QuanLyDichVuController(quanLyDichVuView);

            // Thiết lập internal frame
            internalFrame.setContentPane(quanLyDichVuView);
            internalFrame.pack();

            // Hiển thị internal frame trong desktop pane
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở quản lý dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showInternalFrame(JInternalFrame internalFrame) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Kiểm tra desktopPane không null
                if (desktopPane == null) {
                    System.err.println("DesktopPane is null - recreating...");
                    createMainContent(); // Tạo lại desktopPane nếu null
                }

                // Đóng tất cả internal frame hiện có
                JInternalFrame[] frames = desktopPane.getAllFrames();
                for (JInternalFrame frame : frames) {
                    try {
                        frame.dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Đảm bảo internal frame có kích thước hợp lý
                Dimension desktopSize = desktopPane.getSize();
                if (desktopSize.width <= 0 || desktopSize.height <= 0) {
                    desktopSize = new Dimension(800, 600); // Kích thước mặc định
                }

                internalFrame.setSize(desktopSize);
                internalFrame.setLocation(0, 0);

                desktopPane.add(internalFrame);
                internalFrame.setVisible(true);

                // Đảm bảo internal frame được focus
                internalFrame.toFront();
                try {
                    internalFrame.setSelected(true);
                    internalFrame.setMaximum(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }

                // Refresh desktop pane
                desktopPane.revalidate();
                desktopPane.repaint();

            } catch (Exception e) {
                e.printStackTrace();
                hienThiThongBao("Lỗi khi hiển thị cửa sổ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Getter methods
    public JButton getBtnThongBao() {
        return btnThongBao;
    }

    public JButton getBtnQuanLyNguyenLieu() {
        return btnQuanLyNguyenLieu;
    }

    public JButton getBtnDatDichVu() {
        return btnDatDichVu;
    }

    public JButton getBtnQuanLyNhanVien() {
        return btnQuanLyNhanVien;
    }

    public JButton getBtnQuanLyCaLam() {
        return btnQuanLyCaLam;
    }

    public JButton getBtnQuanLyKhachHang() {
        return btnQuanLyKhachHang;
    }

    public JButton getBtnQuanLyDichVu() {
        return btnQuanLyDichVu;
    }

    public JButton getBtnThongKe() {
        return btnThongKe;
    }

    public JButton getBtnCaiDat() {
        return btnCaiDat;
    }

    public JButton getBtnThoat() {
        return btnThoat;
    }

    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }

    public void capNhatThongTinNguoiDung(String tenDangNhap, String vaiTro) {
        String vaiTroText = "";

        if ("ADMIN".equalsIgnoreCase(vaiTro)) {
            vaiTroText = "Quản trị viên";
        } else if ("THUNGAN".equalsIgnoreCase(vaiTro)) {
            vaiTroText = "Thu ngân";
        } else if ("NHANVIEN".equalsIgnoreCase(vaiTro)) {
            vaiTroText = "Nhân viên";
        } else {
            vaiTroText = vaiTro;
        }

        lblUserInfo.setText("Xin chào: " + tenDangNhap + " | " + vaiTroText);
    }

    // Phương thức hỗ trợ tách login
    public static void khoiChayUngDung() {
        // Khởi chạy ứng dụng
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainView().setVisible(true);
                System.out.println("Ứng dụng Quản Lý SPA đã khởi chạy thành công!");
            }
        });
    }

    // Phương thức main mới không tự động chạy
    public static void main(String[] args) {
        // Không tự động chạy nữa, chỉ để tham khảo
        System.out.println("Vui lòng sử dụng phương thức khoiChayUngDung() từ lớp Login");
    }
}
