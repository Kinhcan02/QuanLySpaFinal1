package View;

import Controller.QuanLyDichVuController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainView extends JFrame {

    private JDesktopPane desktopPane;
    private JButton btnDatDichVu, btnQuanLyNhanVien, btnQuanLyKhachHang, btnQuanLyDichVu, btnThongKe, btnCaiDat, btnThoat;
    private JLabel lblUserInfo, lblVersion;
    private QuanLyDichVuView quanLyDichVuView;
    private QuanLyDichVuController quanLyDichVuController;

    // Màu sắc mới
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_MENU = new Color(0x4D, 0x8A, 0x57);      // Màu menu #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                      // Màu chữ #ffffff

    public MainView() {
        initUI();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        MainView.this,
                        "Bạn có chắc muốn thoát chương trình không?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
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

        // Navigation buttons
        JPanel navPanel = new JPanel();
        navPanel.setBackground(COLOR_MENU.darker()); // Màu menu đậm hơn
        navPanel.setLayout(new GridLayout(9, 1, 0, 2));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Tạo các nút menu
        btnDatDichVu = createNavButton("ĐẶT DỊCH VỤ", "Đặt lịch và quản lý dịch vụ");
        btnQuanLyNhanVien = createNavButton("QUẢN LÝ NHÂN VIÊN", "Quản lý thông tin nhân viên");
        btnQuanLyKhachHang = createNavButton("QUẢN LÝ KHÁCH HÀNG", "Quản lý thông tin khách hàng");
        btnQuanLyDichVu = createNavButton("QUẢN LÝ DỊCH VỤ", "Quản lý danh mục dịch vụ");
        btnThongKe = createNavButton("THỐNG KÊ", "Báo cáo và thống kê");
        btnCaiDat = createNavButton("CÀI ĐẶT", "Cài đặt hệ thống");

        // Separator
        JPanel separator = new JPanel();
        separator.setBackground(COLOR_MENU); // Màu menu #4d8a57
        separator.setPreferredSize(new Dimension(270, 2));

        btnThoat = createNavButton("THOÁT", "Đóng hệ thống");

        // Thêm các nút vào panel
        navPanel.add(btnDatDichVu);
        navPanel.add(btnQuanLyNhanVien);
        navPanel.add(btnQuanLyKhachHang);
        navPanel.add(btnQuanLyDichVu);
        navPanel.add(btnThongKe);
        navPanel.add(btnCaiDat);
        navPanel.add(separator);
        navPanel.add(btnThoat);

        // Version info
        JPanel versionPanel = new JPanel();
        versionPanel.setBackground(COLOR_MENU); // Màu menu #4d8a57
        versionPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        lblVersion = new JLabel("Phiên bản 1.0 - SPA Management", JLabel.CENTER);
        lblVersion.setForeground(COLOR_TEXT); // Màu chữ #ffffff
        lblVersion.setFont(new Font("Arial", Font.PLAIN, 11));
        versionPanel.add(lblVersion);

        sidebarPanel.add(logoPanel, BorderLayout.NORTH);
        sidebarPanel.add(navPanel, BorderLayout.CENTER);
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
        // Sự kiện cho nút Quản lý dịch vụ
        btnQuanLyDichVu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showQuanLyDichVu();
            }
        });

        // Sự kiện cho nút Thoát
        btnThoat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        MainView.this,
                        "Bạn có chắc muốn thoát chương trình không?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Sự kiện cho các nút khác
        btnDatDichVu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainView.this, "Tính năng Đặt dịch vụ đang phát triển");
            }
        });

        btnQuanLyNhanVien.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainView.this, "Tính năng Quản lý nhân viên đang phát triển");
            }
        });

        btnQuanLyKhachHang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainView.this, "Tính năng Quản lý khách hàng đang phát triển");
            }
        });

        btnThongKe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainView.this, "Tính năng Thống kê đang phát triển");
            }
        });

        btnCaiDat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainView.this, "Tính năng Cài đặt đang phát triển");
            }
        });
    }

    private void showQuanLyDichVu() {
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
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi mở quản lý dịch vụ: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Lỗi khi hiển thị cửa sổ: " + e.getMessage());
            }
        });
    }

    // Getter methods
    public JButton getBtnDatDichVu() {
        return btnDatDichVu;
    }

    public JButton getBtnQuanLyNhanVien() {
        return btnQuanLyNhanVien;
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

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi chạy ứng dụng
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainView();
                System.out.println("Ứng dụng Quản Lý SPA đã khởi chạy thành công!");
            }
        });
    }
}
