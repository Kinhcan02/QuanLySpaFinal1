package Controller;

import View.QuanLyTaiKhoanView;
import Model.TaiKhoan;
import Model.NhanVien;
import Service.AuthService;
import Service.NhanVienService;
import Repository.TaiKhoanRepository;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class QuanLyTaiKhoanController {
    private QuanLyTaiKhoanView view;
    private AuthService authService;
    private NhanVienService nhanVienService;
    private TaiKhoanRepository taiKhoanRepository;
    
    // Màu sắc giống QuanLyDichVuController
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff
    
    public QuanLyTaiKhoanController(QuanLyTaiKhoanView view) {
        this.view = view;
        this.authService = new AuthService();
        this.nhanVienService = new NhanVienService();
        this.taiKhoanRepository = new TaiKhoanRepository();
        initController();
        loadDataToTable();
        loadVaiTroFilter();
    }
    
    private void initController() {
        // Thêm sự kiện cho các nút
        view.getBtnThem().addActionListener(e -> themTaiKhoan());
        view.getBtnSua().addActionListener(e -> suaTaiKhoan());
        view.getBtnXoa().addActionListener(e -> xoaTaiKhoan());
        view.getBtnLamMoi().addActionListener(e -> lamMoi());
        view.getBtnTimKiem().addActionListener(e -> timKiem());
        view.getBtnResetPassword().addActionListener(e -> resetMatKhau());
        
        // Sự kiện double click trên bảng
        view.getTblTaiKhoan().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    suaTaiKhoan();
                }
            }
        });
    }
    
    private void loadDataToTable() {
        DefaultTableModel model = view.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ
        
        List<TaiKhoan> danhSachTaiKhoan = authService.layTatCaTaiKhoan();
        
        for (TaiKhoan tk : danhSachTaiKhoan) {
            Object[] rowData = {
                tk.getMaTaiKhoan(),
                tk.getTenDangNhap(),
                "********", // Ẩn mật khẩu
                tk.getVaiTro(),
                getTenNhanVien(tk.getMaNhanVien())
            };
            model.addRow(rowData);
        }
    }
    
    private String getTenNhanVien(Integer maNhanVien) {
        if (maNhanVien == null || maNhanVien == 0) {
            return "Chưa liên kết";
        }
        
        try {
            NhanVien nhanVien = nhanVienService.getNhanVienById(maNhanVien);
            return nhanVien != null ? nhanVien.getHoTen() : "Không xác định";
        } catch (Exception e) {
            return "Không xác định";
        }
    }
    
    private void loadVaiTroFilter() {
        JComboBox<String> cboVaiTroFilter = view.getCboVaiTroFilter();
        cboVaiTroFilter.removeAllItems();
        cboVaiTroFilter.addItem("Tất cả");
        cboVaiTroFilter.addItem("Admin");
        cboVaiTroFilter.addItem("NhanVien");
    }
    
    private void themTaiKhoan() {
        try {
            JDialog dialog = createDialog("Thêm Tài Khoản Mới", 400, 450);
            JPanel mainPanel = createMainPanel();

            // Form nhập liệu với BorderLayout
            JPanel formPanel = new JPanel(new BorderLayout(10, 10));
            formPanel.setBackground(COLOR_BACKGROUND);

            // Panel cho các field thông thường
            JPanel basicInfoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            basicInfoPanel.setBackground(COLOR_BACKGROUND);

            JTextField txtTenDangNhap = new JTextField();
            JPasswordField txtMatKhau = new JPasswordField();
            JComboBox<String> cboVaiTro = new JComboBox<>();
            JComboBox<String> cboNhanVien = new JComboBox<>();

            // Load vai trò
            cboVaiTro.addItem("-- Chọn vai trò --");
            cboVaiTro.addItem("Admin");
            cboVaiTro.addItem("NhanVien");

            // Load nhân viên
            loadNhanVienToComboBox(cboNhanVien);

            basicInfoPanel.add(createStyledLabel("Tên đăng nhập:"));
            basicInfoPanel.add(txtTenDangNhap);
            basicInfoPanel.add(createStyledLabel("Mật khẩu:"));
            basicInfoPanel.add(txtMatKhau);
            basicInfoPanel.add(createStyledLabel("Vai trò:"));
            basicInfoPanel.add(cboVaiTro);
            basicInfoPanel.add(createStyledLabel("Nhân viên:"));
            basicInfoPanel.add(cboNhanVien);

            // Thêm panel vào form chính
            formPanel.add(basicInfoPanel, BorderLayout.CENTER);

            // Panel nút
            JPanel buttonPanel = createButtonPanel();
            JButton btnThem = createStyledButton("Thêm", COLOR_BUTTON);
            JButton btnHuy = createStyledButton("Hủy", new Color(149, 165, 166));

            btnThem.addActionListener(e -> handleThemTaiKhoan(dialog, txtTenDangNhap, txtMatKhau, cboVaiTro, cboNhanVien));
            btnHuy.addActionListener(e -> dialog.dispose());

            buttonPanel.add(btnThem);
            buttonPanel.add(btnHuy);

            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi thêm tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleThemTaiKhoan(JDialog dialog, JTextField txtTenDangNhap, JPasswordField txtMatKhau,
                                   JComboBox<String> cboVaiTro, JComboBox<String> cboNhanVien) {
        
        // Validate dữ liệu
        if (txtTenDangNhap.getText().trim().isEmpty()) {
            hienThiThongBao("Tên đăng nhập không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String matKhau = new String(txtMatKhau.getPassword());
        if (matKhau.trim().isEmpty()) {
            hienThiThongBao("Mật khẩu không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cboVaiTro.getSelectedIndex() == 0) {
            hienThiThongBao("Vui lòng chọn vai trò", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String vaiTro = (String) cboVaiTro.getSelectedItem();
        Integer maNhanVien = null;
        
        // Lấy mã nhân viên nếu có chọn
        if (cboNhanVien.getSelectedIndex() > 0) {
            String selectedNhanVien = (String) cboNhanVien.getSelectedItem();
            maNhanVien = getMaNhanVienFromComboBox(selectedNhanVien);
        }
        
        // Tạo đối tượng tài khoản mới
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(txtTenDangNhap.getText().trim());
        taiKhoan.setMatKhauHash(matKhau); // Service sẽ hash mật khẩu
        taiKhoan.setVaiTro(vaiTro);
        taiKhoan.setMaNhanVien(maNhanVien);
        
        // Thêm vào database
        boolean success = authService.themTaiKhoan(taiKhoan);
        if (success) {
            hienThiThongBao("Thêm tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataToTable();
            dialog.dispose();
        } else {
            hienThiThongBao("Thêm tài khoản thất bại! Tên đăng nhập có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void suaTaiKhoan() {
        int selectedRow = view.getTblTaiKhoan().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn tài khoản cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            DefaultTableModel model = view.getModel();
            int maTaiKhoan = (int) model.getValueAt(selectedRow, 0);
            
            // Lấy thông tin tài khoản từ database
            TaiKhoan taiKhoan = authService.layTaiKhoanTheoMa(maTaiKhoan);
            if (taiKhoan == null) {
                hienThiThongBao("Không tìm thấy tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            showEditTaiKhoanForm(taiKhoan);
            
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi sửa tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showEditTaiKhoanForm(TaiKhoan taiKhoan) {
        try {
            JDialog dialog = createDialog("Sửa Tài Khoản", 400, 300);
            JPanel mainPanel = createMainPanel();

            // Form nhập liệu với BorderLayout
            JPanel formPanel = new JPanel(new BorderLayout(10, 10));
            formPanel.setBackground(COLOR_BACKGROUND);

            // Panel cho các field thông thường
            JPanel basicInfoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            basicInfoPanel.setBackground(COLOR_BACKGROUND);

            JTextField txtTenDangNhap = new JTextField(taiKhoan.getTenDangNhap());
            JComboBox<String> cboVaiTro = new JComboBox<>();
            JComboBox<String> cboNhanVien = new JComboBox<>();

            // Load vai trò
            cboVaiTro.addItem("Admin");
            cboVaiTro.addItem("NhanVien");
            cboVaiTro.setSelectedItem(taiKhoan.getVaiTro());

            // Load nhân viên
            
            // Chọn nhân viên hiện tại nếu có
            if (taiKhoan.getMaNhanVien() != 0 && taiKhoan.getMaNhanVien() != 0) {
                String tenNhanVienHienTai = getTenNhanVien(taiKhoan.getMaNhanVien());
                for (int i = 0; i < cboNhanVien.getItemCount(); i++) {
                    if (cboNhanVien.getItemAt(i).equals(tenNhanVienHienTai)) {
                        cboNhanVien.setSelectedIndex(i);
                        break;
                    }
                }
            }

            basicInfoPanel.add(createStyledLabel("Tên đăng nhập:"));
            basicInfoPanel.add(txtTenDangNhap);
            basicInfoPanel.add(createStyledLabel("Vai trò:"));
            basicInfoPanel.add(cboVaiTro);
            // Thêm panel vào form chính
            formPanel.add(basicInfoPanel, BorderLayout.CENTER);

            // Panel nút
            JPanel buttonPanel = createButtonPanel();
            JButton btnCapNhat = createStyledButton("Cập nhật", COLOR_BUTTON);
            JButton btnHuy = createStyledButton("Hủy", new Color(149, 165, 166));

            btnCapNhat.addActionListener(e -> handleCapNhatTaiKhoan(dialog, taiKhoan, txtTenDangNhap, cboVaiTro));
            btnHuy.addActionListener(e -> dialog.dispose());

            buttonPanel.add(btnCapNhat);
            buttonPanel.add(btnHuy);

            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi hiển thị form sửa tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleCapNhatTaiKhoan(JDialog dialog, TaiKhoan taiKhoan, JTextField txtTenDangNhap,
                                      JComboBox<String> cboVaiTro) {
        
        // Validate dữ liệu
        if (txtTenDangNhap.getText().trim().isEmpty()) {
            hienThiThongBao("Tên đăng nhập không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Cập nhật thông tin
        taiKhoan.setTenDangNhap(txtTenDangNhap.getText().trim());
        taiKhoan.setVaiTro((String) cboVaiTro.getSelectedItem());
        
        
        // Cập nhật database
        boolean success = authService.capNhatTaiKhoan(taiKhoan);
        if (success) {
            hienThiThongBao("Cập nhật tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataToTable();
            dialog.dispose();
        } else {
            hienThiThongBao("Cập nhật thất bại! Tên đăng nhập có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaTaiKhoan() {
        int selectedRow = view.getTblTaiKhoan().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn tài khoản cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            DefaultTableModel model = view.getModel();
            int maTaiKhoan = (int) model.getValueAt(selectedRow, 0);
            String tenDangNhap = (String) model.getValueAt(selectedRow, 1);
            
            boolean confirmed = hienThiXacNhan("Bạn có chắc chắn muốn xóa tài khoản '" + tenDangNhap + "'?");
            
            if (confirmed) {
                boolean success = authService.xoaTaiKhoan(maTaiKhoan);
                if (success) {
                    hienThiThongBao("Xóa tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadDataToTable();
                } else {
                    hienThiThongBao("Xóa thất bại! Không thể xóa tài khoản đang đăng nhập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi xóa tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetMatKhau() {
        int selectedRow = view.getTblTaiKhoan().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn tài khoản cần reset mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            DefaultTableModel model = view.getModel();
            int maTaiKhoan = (int) model.getValueAt(selectedRow, 0);
            String tenDangNhap = (String) model.getValueAt(selectedRow, 1);
            
            // Hiển thị dialog nhập mật khẩu mới
            JDialog dialog = createDialog("Reset Mật Khẩu", 300, 200);
            JPanel mainPanel = createMainPanel();
            
            JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
            formPanel.setBackground(COLOR_BACKGROUND);
            
            formPanel.add(createStyledLabel("Nhập mật khẩu mới cho '" + tenDangNhap + "':"));
            JPasswordField txtMatKhauMoi = new JPasswordField();
            formPanel.add(txtMatKhauMoi);
            
            JPanel buttonPanel = createButtonPanel();
            JButton btnReset = createStyledButton("Reset", COLOR_BUTTON);
            JButton btnHuy = createStyledButton("Hủy", new Color(149, 165, 166));
            
            btnReset.addActionListener(e -> {
                String matKhauMoi = new String(txtMatKhauMoi.getPassword());
                if (matKhauMoi.trim().isEmpty()) {
                    hienThiThongBao("Mật khẩu không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean success = authService.doiMatKhau(maTaiKhoan, matKhauMoi.trim());
                if (success) {
                    hienThiThongBao("Reset mật khẩu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    hienThiThongBao("Reset mật khẩu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnHuy.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(btnReset);
            buttonPanel.add(btnHuy);
            
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(mainPanel);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi khi reset mật khẩu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void timKiem() {
        String tuKhoa = view.getTxtTimKiem().getText().trim().toLowerCase();
        String vaiTroFilter = (String) view.getCboVaiTroFilter().getSelectedItem();
        
        DefaultTableModel model = view.getModel();
        model.setRowCount(0);
        
        List<TaiKhoan> danhSachTaiKhoan = authService.layTatCaTaiKhoan();
        
        for (TaiKhoan tk : danhSachTaiKhoan) {
            // Lọc theo từ khóa
            boolean matchTuKhoa = tuKhoa.isEmpty() ||
                tk.getTenDangNhap().toLowerCase().contains(tuKhoa) ||
                tk.getVaiTro().toLowerCase().contains(tuKhoa);
            
            // Lọc theo vai trò
            boolean matchVaiTro = "Tất cả".equals(vaiTroFilter) ||
                tk.getVaiTro().equals(vaiTroFilter);
            
            if (matchTuKhoa && matchVaiTro) {
                Object[] rowData = {
                    tk.getMaTaiKhoan(),
                    tk.getTenDangNhap(),
                    "********",
                    tk.getVaiTro(),
                    getTenNhanVien(tk.getMaNhanVien())
                };
                model.addRow(rowData);
            }
        }
    }
    
    private void lamMoi() {
        view.getTxtTimKiem().setText("");
        view.getCboVaiTroFilter().setSelectedIndex(0);
        loadDataToTable();
    }
    
    // CÁC PHƯƠNG THỨC HỖ TRỢ
    
    private void loadNhanVienToComboBox(JComboBox<String> cboNhanVien) {
        try {
            List<NhanVien> listNhanVien = nhanVienService.getAllNhanVien();
            cboNhanVien.removeAllItems();
            cboNhanVien.addItem("-- Chọn nhân viên --");
            
            for (NhanVien nv : listNhanVien) {
                cboNhanVien.addItem(nv.getMaNhanVien() + " - " + nv.getHoTen());
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải danh sách nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Integer getMaNhanVienFromComboBox(String selectedNhanVien) {
        if (selectedNhanVien == null || selectedNhanVien.equals("-- Chọn nhân viên --")) {
            return null;
        }
        
        try {
            // Lấy mã nhân viên từ chuỗi "Mã - Tên"
            String[] parts = selectedNhanVien.split(" - ");
            if (parts.length > 0) {
                return Integer.parseInt(parts[0].trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // CÁC PHƯƠNG THỨC HIỂN THỊ THÔNG BÁO GIỐNG QuanLyDichVuController
    
    private void hienThiThongBao(String message, String title, int messageType) {
        JDialog dialog = createCustomDialog(message, title, messageType);
        dialog.setVisible(true);
    }
    
   private boolean hienThiXacNhan(String message) {
    final boolean[] result = {false};
    JDialog dialog = createConfirmationDialog(message, result);
    dialog.setVisible(true);
    return result[0];
}
    
    private JDialog createCustomDialog(String message, String title, int messageType) {
        // Tạo custom button OK
        JButton okButton = createStyledButton("OK", COLOR_BUTTON);
        okButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(okButton);
            if (window != null) {
                window.dispose();
            }
        });

        // Tạo panel chứa nội dung với màu nền xanh tràn viền
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tạo icon và message
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(COLOR_TEXT);
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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), title, true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(view);
        dialog.setResizable(false);

        // Đặt nút OK làm default button
        dialog.getRootPane().setDefaultButton(okButton);

        return dialog;
    }

   private JDialog createConfirmationDialog(String message, final boolean[] result) {
    // Tạo custom buttons
    JButton btnCo = createStyledButton("Có", COLOR_BUTTON);
    JButton btnKhong = createStyledButton("Không", new Color(149, 165, 166));
    
    // Tạo panel chứa nội dung với màu nền xanh tràn viền
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBackground(COLOR_BACKGROUND);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Tạo icon và message
    JLabel messageLabel = new JLabel(message);
    messageLabel.setForeground(COLOR_TEXT);
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
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Xác nhận", true);
    dialog.setContentPane(panel);
    dialog.pack();
    dialog.setLocationRelativeTo(view);
    dialog.setResizable(false);

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

    return dialog;
}

    // Các phương thức hỗ trợ tạo giao diện
    private JDialog createDialog(String title, int width, int height) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(view);
        dialog.setModal(true);
        return dialog;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(COLOR_BACKGROUND);
        return panel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(COLOR_TEXT);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_TEXT);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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
}