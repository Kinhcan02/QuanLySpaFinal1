package View;

import Controller.*;
import View.*;
import Service.CaLamService;
import Service.DatLichService;
import Service.KhachHangService;
import Service.NhanVienService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class MainView extends JFrame {

    private QuanLyDatLichView quanLyDatLichView;
    private QuanLyDatLichController quanLyDatLichController;
    private JDesktopPane desktopPane;
    private JButton btnThongBao, btnDatLich, btnQuanLyNguyenLieu,
            btnDatDichVu, btnQuanLyNhanVien, btnQuanLyCaLam,
            btnQuanLyKhachHang, btnQuanLyDichVu, btnThongKe,
            btnQuanLyTaiKhoan, btnThoat, btnQuanLyThuChi;
    private JLabel lblUserInfo, lblVersion;
    private QuanLyDichVuView quanLyDichVuView;
    private QuanLyDichVuController quanLyDichVuController;
    private MainViewController mainViewController;
    private QuanLyNguyenLieuController quanLyNguyenLieuController;
    private QuanLyNguyenLieuView quanLyNguyenLieuView;
    private QuanLyNhapNguyenLieuController quanLyNhapNguyenLieuController;
    private QuanLyNhapNguyenLieuView quanLyNhapNguyenLieuView;
    private ThongBaoView thongBaoView;
    private ThongBaoController thongBaoController;
    private ThongKeView thongKeView;
    private ThongKeController thongKeController;
    private QuanLyThuChiView quanLyThuChiView;
    private QuanLyThuChiController quanLyThuChiController;
    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_MENU = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_MENU_DARK = new Color(0x3A, 0x6B, 0x47);
    private final Color COLOR_MENU_LIGHT = new Color(0x5D, 0x9A, 0x67);
    private final Color COLOR_TEXT = Color.WHITE;
    private final Color COLOR_SUB_MENU = COLOR_MENU_DARK; // Sử dụng cùng màu với menu chính

    // Biến quản lý trạng thái menu
    private boolean isNguyenLieuExpanded = false;

    // Các nút submenu
    private JButton btnNguyenLieu, btnNhapNguyenLieu;
    private JPanel submenuNguyenLieuPanel;

    public MainView() {
        mainViewController = new MainViewController(this);
        initUI();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (xacNhanThoatChuongTrinh()) {
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

        setLayout(new BorderLayout());

        createHeader();
        createSidebar();
        createMainContent();

        setupMenuEvents();
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_MENU);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(COLOR_MENU);

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ SPA BEAUTY");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT);

        titlePanel.add(lblTitle);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(COLOR_MENU);

        lblUserInfo = new JLabel("Xin chào: Quản trị viên | Admin");
        lblUserInfo.setForeground(COLOR_TEXT);
        lblUserInfo.setFont(new Font("Arial", Font.PLAIN, 14));

        userPanel.add(lblUserInfo);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(COLOR_MENU_DARK);
        sidebarPanel.setPreferredSize(new Dimension(300, getHeight()));
        sidebarPanel.setLayout(new BorderLayout());

        // Logo/Title area
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(COLOR_MENU);
        logoPanel.setPreferredSize(new Dimension(300, 120));
        logoPanel.setLayout(new GridLayout(2, 1));

        JLabel lblMainTitle = new JLabel("SPA BEAUTY", JLabel.CENTER);
        lblMainTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblMainTitle.setForeground(COLOR_TEXT);

        JLabel lblSubTitle = new JLabel("Management System", JLabel.CENTER);
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubTitle.setForeground(COLOR_TEXT);

        logoPanel.add(lblMainTitle);
        logoPanel.add(lblSubTitle);

        // Navigation panel
        JPanel navPanel = new JPanel();
        navPanel.setBackground(COLOR_MENU_DARK);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Tạo các nút menu chính
        btnThongBao = createMenuButton("THÔNG BÁO");
        btnDatLich = createMenuButton("ĐẶT LỊCH");

        // Menu Quản lý Nguyên liệu (có submenu)
        btnQuanLyNguyenLieu = createMenuButtonWithArrow("QUẢN LÝ NGUYÊN LIỆU");

        // Submenu Nguyên liệu (ban đầu ẩn)
        submenuNguyenLieuPanel = createSubMenuPanel();
        btnNguyenLieu = createSubMenuButton("NGUYÊN LIỆU"); // Đổi thành chữ in hoa
        btnNhapNguyenLieu = createSubMenuButton("NHẬP NGUYÊN LIỆU"); // Đổi thành chữ in hoa

        submenuNguyenLieuPanel.add(btnNguyenLieu);
        submenuNguyenLieuPanel.add(btnNhapNguyenLieu);
        submenuNguyenLieuPanel.setVisible(false); // Ẩn ban đầu

        btnDatDichVu = createMenuButton("ĐẶT DỊCH VỤ");
        btnQuanLyNhanVien = createMenuButton("QUẢN LÝ NHÂN VIÊN");
        btnQuanLyCaLam = createMenuButton("QUẢN LÝ CA LÀM");
        btnQuanLyKhachHang = createMenuButton("QUẢN LÝ KHÁCH HÀNG");
        btnQuanLyDichVu = createMenuButton("QUẢN LÝ DỊCH VỤ");
        btnThongKe = createMenuButton("THỐNG KÊ");
        btnQuanLyTaiKhoan = createMenuButton("QUẢN LÝ TÀI KHOẢN");
        btnQuanLyThuChi = createMenuButton("QUẢN LÝ THU CHI");
        // Thêm các component vào navPanel theo đúng thứ tự
        navPanel.add(btnThongBao);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnDatLich);
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        navPanel.add(btnQuanLyNguyenLieu);
        navPanel.add(submenuNguyenLieuPanel);
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
        navPanel.add(btnQuanLyTaiKhoan);
        navPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        navPanel.add(btnQuanLyThuChi); // THÊM DÒNG NÀY
        navPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        // Separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setBackground(COLOR_MENU_LIGHT);
        separator.setForeground(COLOR_MENU_LIGHT);
        separator.setMaximumSize(new Dimension(270, 2));
        navPanel.add(separator);
        navPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        btnThoat = createMenuButton("THOÁT");
        navPanel.add(btnThoat);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(navPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(COLOR_MENU_DARK);
        scrollPane.getViewport().setBackground(COLOR_MENU_DARK);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Customize scroll bar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setBackground(COLOR_MENU_DARK);
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = COLOR_MENU_LIGHT;
                this.trackColor = COLOR_MENU_DARK;
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
        versionPanel.setBackground(COLOR_MENU);
        versionPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        lblVersion = new JLabel("Phiên bản 1.0 - SPA Management", JLabel.CENTER);
        lblVersion.setForeground(COLOR_TEXT);
        lblVersion.setFont(new Font("Arial", Font.PLAIN, 11));
        versionPanel.add(lblVersion);

        sidebarPanel.add(logoPanel, BorderLayout.NORTH);
        sidebarPanel.add(scrollPane, BorderLayout.CENTER);
        sidebarPanel.add(versionPanel, BorderLayout.SOUTH);

        add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton createMenuButton(String title) {
        JButton button = new JButton(title);
        button.setBackground(COLOR_MENU_DARK);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(270, 45));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_MENU_LIGHT);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_MENU_DARK);
            }
        });

        return button;
    }

    private JButton createMenuButtonWithArrow(String title) {
        JButton button = new JButton("<html>" + title + " &nbsp;&nbsp;&#9660;</html>");
        button.setBackground(COLOR_MENU_DARK);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(270, 45));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_MENU_LIGHT);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_MENU_DARK);
            }
        });

        return button;
    }

    private JPanel createSubMenuPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_MENU_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); // Giảm indent để đồng bộ
        panel.setMaximumSize(new Dimension(270, 200));
        return panel;
    }

    private JButton createSubMenuButton(String title) {
        JButton button = new JButton(title);
        button.setBackground(COLOR_MENU_DARK); // Sử dụng cùng màu nền với menu chính
        button.setForeground(COLOR_TEXT); // Sử dụng cùng màu chữ với menu chính
        button.setFont(new Font("Arial", Font.PLAIN, 14)); // Sử dụng cùng font size với menu chính
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20)); // Sử dụng cùng padding với menu chính
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(270, 45)); // Sử dụng cùng kích thước với menu chính
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_MENU_LIGHT); // Hiệu ứng hover giống menu chính
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(COLOR_MENU_DARK); // Trở về màu menu chính
            }
        });

        return button;
    }

    private void createMainContent() {
        desktopPane = new JDesktopPane();
        desktopPane.setBackground(COLOR_BACKGROUND);
        add(desktopPane, BorderLayout.CENTER);
    }

    private void setupMenuEvents() {
        // Các nút không có submenu
        btnThongBao.addActionListener(mainViewController);
        btnDatLich.addActionListener(mainViewController);
        btnDatDichVu.addActionListener(mainViewController);
        btnQuanLyNhanVien.addActionListener(mainViewController);
        btnQuanLyCaLam.addActionListener(mainViewController);
        btnQuanLyKhachHang.addActionListener(mainViewController);
        btnQuanLyDichVu.addActionListener(mainViewController);
        btnThongKe.addActionListener(e -> showThongKe());
        btnQuanLyTaiKhoan.addActionListener(mainViewController);
        btnThoat.addActionListener(mainViewController);
        btnDatLich.addActionListener(e -> showQuanLyDatLich());
        btnQuanLyThuChi.addActionListener(mainViewController);
        // Nút mở rộng menu Nguyên liệu
        btnQuanLyNguyenLieu.addActionListener(e -> toggleNguyenLieuMenu());

        // Submenu Nguyên liệu
        btnNguyenLieu.addActionListener(e -> showQuanLyNguyenLieu());
        btnNhapNguyenLieu.addActionListener(e -> showQuanLyNhapNguyenLieu());
    }

    private void toggleNguyenLieuMenu() {
        isNguyenLieuExpanded = !isNguyenLieuExpanded;
        submenuNguyenLieuPanel.setVisible(isNguyenLieuExpanded);

        // Cập nhật mũi tên
        if (isNguyenLieuExpanded) {
            btnQuanLyNguyenLieu.setText("<html>QUẢN LÝ NGUYÊN LIỆU &nbsp;&nbsp;&#9650;</html>");
        } else {
            btnQuanLyNguyenLieu.setText("<html>QUẢN LÝ NGUYÊN LIỆU &nbsp;&nbsp;&#9660;</html>");
        }

        // Refresh layout
        btnQuanLyNguyenLieu.getParent().revalidate();
        btnQuanLyNguyenLieu.getParent().repaint();
    }

    private void kiemTraThongBaoDatLich() {
        if (quanLyDatLichController != null) {
            // Gọi service để kiểm tra thông báo
            DatLichService datLichService = new DatLichService();
        }
    }

// Từ giao diện chính, gọi như sau:
    private void moCheDoSuaHoaDon(Integer maHoaDon) {
        DatDichVuView view = new DatDichVuView();
        DatDichVuController controller = new DatDichVuController(view);
        controller.khoiTaoCheDoChinhSua(maHoaDon);

        // Hiển thị view trong frame
        JFrame frame = new JFrame("Sửa Hóa Đơn");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

// Hoặc có thể thêm dialog để nhập mã hóa đơn
    private void showDialogSuaHoaDon() {
        String maHoaDonStr = JOptionPane.showInputDialog(
                this,
                "Nhập mã hóa đơn cần sửa:",
                "Sửa hóa đơn",
                JOptionPane.QUESTION_MESSAGE
        );

        if (maHoaDonStr != null && !maHoaDonStr.trim().isEmpty()) {
            try {
                Integer maHoaDon = Integer.parseInt(maHoaDonStr.trim());
                moCheDoSuaHoaDon(maHoaDon);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Mã hóa đơn không hợp lệ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void showThongBao() {
        try {
            // Kiểm tra nếu thongBaoView đã tồn tại nhưng bị dispose
            if (thongBaoView != null && thongBaoView.isDisplayable()) {
                // Nếu đang hiển thị thì đưa lên trước
                thongBaoView.toFront();
                try {
                    thongBaoView.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
            } else {
                // Tạo mới nếu chưa có hoặc đã bị dispose
                thongBaoView = new ThongBaoView();
                thongBaoController = new ThongBaoController(thongBaoView);

                // Thêm thongBaoView vào desktopPane
                desktopPane.add(thongBaoView);

                // Đặt vị trí ở góc trên bên phải
                Dimension desktopSize = desktopPane.getSize();
                Dimension frameSize = thongBaoView.getSize();
                thongBaoView.setLocation(
                        desktopSize.width - frameSize.width - 20,
                        20
                );
            }

            // Đảm bảo hiển thị
            thongBaoView.setVisible(true);
            thongBaoView.toFront();

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở thông báo: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showDatDichVu() {
        try {
            JInternalFrame internalFrame = new JInternalFrame(
                    "Đặt Dịch Vụ",
                    true, true, true, true
            );

            DatDichVuView datDichVuView = new DatDichVuView();
            DatDichVuController datDichVuController = new DatDichVuController(datDichVuView);

            internalFrame.setContentPane(datDichVuView);
            internalFrame.pack();
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở đặt dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showQuanLyDatLich() {
        try {
            JInternalFrame internalFrame = new JInternalFrame(
                    "Quản Lý Đặt Lịch",
                    true, true, true, true
            );

            quanLyDatLichView = new QuanLyDatLichView();
            quanLyDatLichController = new QuanLyDatLichController(quanLyDatLichView);

            internalFrame.setContentPane(quanLyDatLichView);
            internalFrame.pack();
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở quản lý đặt lịch: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CÁC PHƯƠNG THỨC HIỂN THỊ CHỨC NĂNG
    public void showQuanLyNguyenLieu() {
        try {
            JInternalFrame internalFrame = new JInternalFrame(
                    "Quản Lý Nguyên Liệu",
                    true, true, true, true
            );

            quanLyNguyenLieuView = new QuanLyNguyenLieuView();
            quanLyNguyenLieuController = new QuanLyNguyenLieuController(quanLyNguyenLieuView);

            internalFrame.setContentPane(quanLyNguyenLieuView);
            internalFrame.pack();
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở quản lý nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showQuanLyNhapNguyenLieu() {
        try {
            JInternalFrame internalFrame = new JInternalFrame(
                    "Quản Lý Nhập Nguyên Liệu",
                    true, true, true, true
            );

            quanLyNhapNguyenLieuView = new QuanLyNhapNguyenLieuView();
            quanLyNhapNguyenLieuController = new QuanLyNhapNguyenLieuController(quanLyNhapNguyenLieuView);

            internalFrame.setContentPane(quanLyNhapNguyenLieuView);
            internalFrame.pack();
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở quản lý nhập nguyên liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showQuanLyKhachHang() {
        try {
            JInternalFrame internalFrame = new JInternalFrame(
                    "Quản Lý Khách Hàng",
                    true, true, true, true
            );

            QuanLyKhachHangView quanLyKhachHangView = new QuanLyKhachHangView();
            KhachHangService khachHangService = new KhachHangService();
            QuanLyKhachHangController quanLyKhachHangController = new QuanLyKhachHangController(quanLyKhachHangView);

            internalFrame.setContentPane(quanLyKhachHangView);
            internalFrame.pack();
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở quản lý khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            QuanLyNhanVienController quanLyNhanVienController = new QuanLyNhanVienController(quanLyNhanVienView);

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
            NhanVienService nhanVienService = new NhanVienService();
            QuanLyCaLamController quanLyCaLamController = new QuanLyCaLamController(quanLyCaLamView, caLamService, nhanVienService);

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
            JInternalFrame internalFrame = new JInternalFrame(
                    "Quản Lý Dịch Vụ",
                    true, true, true, true
            );

            quanLyDichVuView = new QuanLyDichVuView();
            quanLyDichVuController = new QuanLyDichVuController(quanLyDichVuView);

            internalFrame.setContentPane(quanLyDichVuView);
            internalFrame.pack();
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở quản lý dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showQuanLyTaiKhoan() {
        try {
            JInternalFrame internalFrame = new JInternalFrame(
                    "Quản Lý Tài Khoản",
                    true, true, true, true
            );

            QuanLyTaiKhoanView quanLyTaiKhoanView = new QuanLyTaiKhoanView();
            QuanLyTaiKhoanController quanLyTaiKhoanController = new QuanLyTaiKhoanController(quanLyTaiKhoanView);

            internalFrame.setContentPane(quanLyTaiKhoanView);
            internalFrame.pack();
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở quản lý tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showThongKe() {
        try {
            JInternalFrame internalFrame = new JInternalFrame(
                    "Thống Kê",
                    true, true, true, true
            );

            thongKeView = new ThongKeView();
            thongKeController = new ThongKeController(thongKeView);

            internalFrame.setContentPane(thongKeView);
            internalFrame.pack();
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở thống kê: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showQuanLyThuChi() {
        try {
            JInternalFrame internalFrame = new JInternalFrame(
                    "QUẢN LÝ THU CHI - SPA/BEAUTY",
                    true, true, true, true
            );

            quanLyThuChiView = new QuanLyThuChiView();
            quanLyThuChiController = new QuanLyThuChiController(quanLyThuChiView);

            internalFrame.setContentPane(quanLyThuChiView);
            internalFrame.pack();
            showInternalFrame(internalFrame);

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi mở quản lý thu chi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showInternalFrame(JInternalFrame internalFrame) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (desktopPane == null) {
                    createMainContent();
                }

                JInternalFrame[] frames = desktopPane.getAllFrames();
                for (JInternalFrame frame : frames) {
                    try {
                        frame.dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Dimension desktopSize = desktopPane.getSize();
                if (desktopSize.width <= 0 || desktopSize.height <= 0) {
                    desktopSize = new Dimension(800, 600);
                }

                internalFrame.setSize(desktopSize);
                internalFrame.setLocation(0, 0);

                desktopPane.add(internalFrame);
                internalFrame.setVisible(true);

                internalFrame.toFront();
                try {
                    internalFrame.setSelected(true);
                    internalFrame.setMaximum(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }

                desktopPane.revalidate();
                desktopPane.repaint();

            } catch (Exception e) {
                e.printStackTrace();
                hienThiThongBao("Lỗi khi hiển thị cửa sổ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void hienThiThongBao(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public boolean xacNhanThoatChuongTrinh() {
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

    private void styleLoginButton(JButton button) {
        Color mainColor = new Color(77, 138, 87);
        Color hoverColor = new Color(67, 118, 77);
        Color borderColor = new Color(57, 98, 67);

        button.setBackground(mainColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());

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
        Color mainColor = new Color(149, 165, 166);
        Color hoverColor = new Color(127, 140, 141);
        Color borderColor = new Color(107, 120, 121);

        button.setBackground(mainColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());

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

    // GETTER METHODS
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

    public JButton getBtnQuanLyTaiKhoan() {
        return btnQuanLyTaiKhoan;
    }

    public JButton getBtnThoat() {
        return btnThoat;
    }

    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }

    public JButton getBtnDatLich() {
        return btnDatLich;
    }

    public JButton getBtnThongKe() {
        return btnThongKe;
    }

    public JButton getBtnQuanLyThuChi() {
        return btnQuanLyThuChi;
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

    public static void khoiChayUngDung() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainView().setVisible(true);
                System.out.println("Ứng dụng Quản Lý SPA đã khởi chạy thành công!");
            }
        });
    }

    public static void main(String[] args) {
        System.out.println("Vui lòng sử dụng phương thức khoiChayUngDung() từ lớp Login");
    }

}
