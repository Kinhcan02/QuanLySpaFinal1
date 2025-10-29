package Controller;

import View.QuanLyDichVuView;
import Model.DichVu;
import Model.LoaiDichVu;
import Service.DichVuService;
import Service.LoaiDichVuService;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.List;

public class QuanLyDichVuController {

    private QuanLyDichVuView view;
    private DichVuService dichVuService;
    private LoaiDichVuService loaiDichVuService;

    // Màu sắc giống QuanLyKhachHangController
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff

    public QuanLyDichVuController(QuanLyDichVuView view) {
        this.view = view;
        this.dichVuService = new DichVuService();
        this.loaiDichVuService = new LoaiDichVuService();

        initController();
        loadAllDichVu();
        loadLoaiDichVuToComboBox();
    }

    private void initController() {
        view.getBtnThem().addActionListener(e -> themDichVu());
        view.getBtnSua().addActionListener(e -> suaDichVu());
        view.getBtnXoa().addActionListener(e -> xoaDichVu());
        view.getBtnLamMoi().addActionListener(e -> lamMoi());
        view.getBtnTimKiem().addActionListener(e -> timKiemDichVu());
        view.getBtnLoaiDichVu().addActionListener(e -> showQuanLyLoaiDichVu());
    }

    private void lamMoi() {
        loadAllDichVu();
        view.getTxtTimKiem().setText("");
        view.getCboLoaiFilter().setSelectedIndex(0);
    }

    public void loadLoaiDichVuToComboBox() {
        try {
            List<LoaiDichVu> listLoaiDV = loaiDichVuService.getAllLoaiDichVu();
            JComboBox<String> cboLoaiFilter = view.getCboLoaiFilter();
            cboLoaiFilter.removeAllItems();
            cboLoaiFilter.addItem("Tất cả");

            for (LoaiDichVu loaiDV : listLoaiDV) {
                cboLoaiFilter.addItem(loaiDV.getTenLoaiDV());
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải loại dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllDichVu() {
        try {
            List<DichVu> listDichVu = dichVuService.getAllDichVu();
            displayDichVuOnTable(listDichVu);
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải danh sách dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayDichVuOnTable(List<DichVu> listDichVu) {
        DefaultTableModel model = view.getModel();
        model.setRowCount(0);

        for (DichVu dv : listDichVu) {
            model.addRow(new Object[]{
                dv.getMaDichVu(),
                dv.getTenDichVu(),
                formatCurrency(dv.getGia()),
                formatThoiGian(dv.getThoiGian()),
                getTenLoaiDichVu(dv.getMaLoaiDV()),
                dv.getGhiChu()
            });
        }
    }

    private String formatThoiGian(Integer thoiGian) {
        if (thoiGian == null) {
            return "Không xác định";
        }
        return thoiGian + " phút";
    }

    private String getTenLoaiDichVu(Integer maLoaiDV) {
        if (maLoaiDV == null) {
            return "Chưa phân loại";
        }

        try {
            LoaiDichVu loaiDV = loaiDichVuService.getLoaiDichVuById(maLoaiDV);
            return loaiDV.getTenLoaiDV();
        } catch (Exception e) {
            return "Không xác định";
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return String.format("%,d VNĐ", amount.longValue());
    }

    private void showQuanLyLoaiDichVu() {
        try {
            QuanLyLoaiDichVuController loaiDichVuController = new QuanLyLoaiDichVuController(this);
            loaiDichVuController.showView();
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi mở quản lý loại dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themDichVu() {
        try {
            JDialog dialog = createDialog("Thêm Dịch Vụ Mới", 500, 500);
            JPanel mainPanel = createMainPanel();

            // Form nhập liệu với BorderLayout
            JPanel formPanel = new JPanel(new BorderLayout(10, 10));
            formPanel.setBackground(COLOR_BACKGROUND);

            // Panel cho các field thông thường
            JPanel basicInfoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            basicInfoPanel.setBackground(COLOR_BACKGROUND);

            JTextField txtTenDV = new JTextField();
            JTextField txtGia = new JTextField();
            JTextField txtThoiGian = new JTextField();
            JComboBox<String> cboLoaiDV = new JComboBox<>();

            // Load loại dịch vụ
            List<LoaiDichVu> listLoaiDV = loaiDichVuService.getAllLoaiDichVu();
            cboLoaiDV.addItem("-- Chọn loại dịch vụ --");
            for (LoaiDichVu loai : listLoaiDV) {
                cboLoaiDV.addItem(loai.getTenLoaiDV());
            }

            basicInfoPanel.add(createStyledLabel("Tên dịch vụ:"));
            basicInfoPanel.add(txtTenDV);
            basicInfoPanel.add(createStyledLabel("Giá dịch vụ:"));
            basicInfoPanel.add(txtGia);
            basicInfoPanel.add(createStyledLabel("Thời gian (phút):"));
            basicInfoPanel.add(txtThoiGian);
            basicInfoPanel.add(createStyledLabel("Loại dịch vụ:"));
            basicInfoPanel.add(cboLoaiDV);

            // Panel cho ghi chú
            JPanel ghiChuPanel = new JPanel(new BorderLayout(5, 5));
            ghiChuPanel.setBackground(COLOR_BACKGROUND);

            JLabel lblGhiChu = createStyledLabel("Ghi chú:");
            JTextArea txtGhiChu = new JTextArea(6, 30);
            txtGhiChu.setLineWrap(true);
            txtGhiChu.setWrapStyleWord(true);
            JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
            scrollGhiChu.setPreferredSize(new Dimension(400, 120));

            ghiChuPanel.add(lblGhiChu, BorderLayout.NORTH);
            ghiChuPanel.add(scrollGhiChu, BorderLayout.CENTER);

            // Thêm các panel vào form chính
            formPanel.add(basicInfoPanel, BorderLayout.NORTH);
            formPanel.add(ghiChuPanel, BorderLayout.CENTER);

            // Panel nút
            JPanel buttonPanel = createButtonPanel();
            JButton btnThem = createStyledButton("Thêm", COLOR_BUTTON);
            JButton btnHuy = createStyledButton("Hủy", COLOR_BUTTON);

            btnThem.addActionListener(e -> handleThemDichVu(dialog, txtTenDV, txtGia, txtThoiGian, cboLoaiDV, txtGhiChu, listLoaiDV));
            btnHuy.addActionListener(e -> dialog.dispose());

            buttonPanel.add(btnThem);
            buttonPanel.add(btnHuy);

            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi thêm dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleThemDichVu(JDialog dialog, JTextField txtTenDV, JTextField txtGia,
            JTextField txtThoiGian, JComboBox<String> cboLoaiDV, JTextArea txtGhiChu,
            List<LoaiDichVu> listLoaiDV) {

        // Validate dữ liệu
        if (txtTenDV.getText().trim().isEmpty()) {
            hienThiThongBao("Tên dịch vụ không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal gia;
        try {
            gia = new BigDecimal(txtGia.getText().replaceAll("[^\\d]", ""));
        } catch (NumberFormatException ex) {
            hienThiThongBao("Giá dịch vụ không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer thoiGian = null;
        if (!txtThoiGian.getText().trim().isEmpty()) {
            try {
                thoiGian = Integer.parseInt(txtThoiGian.getText().trim());
                if (thoiGian <= 0) {
                    hienThiThongBao("Thời gian phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                hienThiThongBao("Thời gian không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Integer maLoaiDV = null;
        if (cboLoaiDV.getSelectedIndex() > 0) {
            maLoaiDV = listLoaiDV.get(cboLoaiDV.getSelectedIndex() - 1).getMaLoaiDV();
        }

        DichVu dichVu = new DichVu(
                txtTenDV.getText().trim(),
                gia,
                thoiGian,
                maLoaiDV,
                txtGhiChu.getText().trim()
        );

        boolean success = dichVuService.addDichVu(dichVu);
        if (success) {
            hienThiThongBao("Thêm dịch vụ thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadAllDichVu();
            dialog.dispose();
        } else {
            hienThiThongBao("Thêm dịch vụ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaDichVu() {
        int selectedRow = view.getTblDichVu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một dịch vụ để sửa", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int maDichVu = (int) view.getModel().getValueAt(selectedRow, 0);
            String tenDichVu = (String) view.getModel().getValueAt(selectedRow, 1);

            boolean confirmed = hienThiXacNhan("Bạn có chắc muốn sửa dịch vụ '" + tenDichVu + "' không?");
            
            if (confirmed) {
                DichVu dichVu = dichVuService.getDichVuById(maDichVu);
                if (dichVu == null) {
                    hienThiThongBao("Không tìm thấy dịch vụ cần sửa", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                showEditDichVuForm(dichVu);
            }

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi sửa dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditDichVuForm(DichVu dichVu) {
        try {
            JDialog dialog = createDialog("Sửa Dịch Vụ", 500, 500);
            JPanel mainPanel = createMainPanel();

            // Form nhập liệu với BorderLayout
            JPanel formPanel = new JPanel(new BorderLayout(10, 10));
            formPanel.setBackground(COLOR_BACKGROUND);

            // Panel cho các field thông thường
            JPanel basicInfoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            basicInfoPanel.setBackground(COLOR_BACKGROUND);

            JTextField txtTenDV = new JTextField(dichVu.getTenDichVu());
            JTextField txtGia = new JTextField(dichVu.getGia().toString());
            JTextField txtThoiGian = new JTextField(dichVu.getThoiGian() != null ? dichVu.getThoiGian().toString() : "");
            JComboBox<String> cboLoaiDV = new JComboBox<>();

            // Load loại dịch vụ
            List<LoaiDichVu> listLoaiDV = loaiDichVuService.getAllLoaiDichVu();
            cboLoaiDV.addItem("-- Chọn loại dịch vụ --");

            int selectedIndex = 0;
            for (int i = 0; i < listLoaiDV.size(); i++) {
                LoaiDichVu loai = listLoaiDV.get(i);
                cboLoaiDV.addItem(loai.getTenLoaiDV());

                if (dichVu.getMaLoaiDV() != null && dichVu.getMaLoaiDV().equals(loai.getMaLoaiDV())) {
                    selectedIndex = i + 1;
                }
            }
            cboLoaiDV.setSelectedIndex(selectedIndex);

            basicInfoPanel.add(createStyledLabel("Tên dịch vụ:"));
            basicInfoPanel.add(txtTenDV);
            basicInfoPanel.add(createStyledLabel("Giá dịch vụ:"));
            basicInfoPanel.add(txtGia);
            basicInfoPanel.add(createStyledLabel("Thời gian (phút):"));
            basicInfoPanel.add(txtThoiGian);
            basicInfoPanel.add(createStyledLabel("Loại dịch vụ:"));
            basicInfoPanel.add(cboLoaiDV);

            // Panel cho ghi chú
            JPanel ghiChuPanel = new JPanel(new BorderLayout(5, 5));
            ghiChuPanel.setBackground(COLOR_BACKGROUND);

            JLabel lblGhiChu = createStyledLabel("Ghi chú:");
            JTextArea txtGhiChu = new JTextArea(6, 30);
            txtGhiChu.setText(dichVu.getGhiChu() != null ? dichVu.getGhiChu() : "");
            txtGhiChu.setLineWrap(true);
            txtGhiChu.setWrapStyleWord(true);
            JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
            scrollGhiChu.setPreferredSize(new Dimension(400, 120));

            ghiChuPanel.add(lblGhiChu, BorderLayout.NORTH);
            ghiChuPanel.add(scrollGhiChu, BorderLayout.CENTER);

            // Thêm các panel vào form chính
            formPanel.add(basicInfoPanel, BorderLayout.NORTH);
            formPanel.add(ghiChuPanel, BorderLayout.CENTER);

            // Panel nút
            JPanel buttonPanel = createButtonPanel();
            JButton btnCapNhat = createStyledButton("Cập nhật", COLOR_BUTTON);
            JButton btnHuy = createStyledButton("Hủy", COLOR_BUTTON);

            btnCapNhat.addActionListener(e -> handleCapNhatDichVu(dialog, dichVu, txtTenDV, txtGia, txtThoiGian, cboLoaiDV, txtGhiChu, listLoaiDV));
            btnHuy.addActionListener(e -> dialog.dispose());

            buttonPanel.add(btnCapNhat);
            buttonPanel.add(btnHuy);

            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi hiển thị form sửa dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCapNhatDichVu(JDialog dialog, DichVu dichVu, JTextField txtTenDV,
            JTextField txtGia, JTextField txtThoiGian, JComboBox<String> cboLoaiDV, JTextArea txtGhiChu,
            List<LoaiDichVu> listLoaiDV) {

        // Validate dữ liệu
        if (txtTenDV.getText().trim().isEmpty()) {
            hienThiThongBao("Tên dịch vụ không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal gia;
        try {
            gia = new BigDecimal(txtGia.getText().replaceAll("[^\\d]", ""));
        } catch (NumberFormatException ex) {
            hienThiThongBao("Giá dịch vụ không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer thoiGian = null;
        if (!txtThoiGian.getText().trim().isEmpty()) {
            try {
                thoiGian = Integer.parseInt(txtThoiGian.getText().trim());
                if (thoiGian <= 0) {
                    hienThiThongBao("Thời gian phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                hienThiThongBao("Thời gian không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Cập nhật thông tin dịch vụ
        dichVu.setTenDichVu(txtTenDV.getText().trim());
        dichVu.setGia(gia);
        dichVu.setThoiGian(thoiGian);
        dichVu.setGhiChu(txtGhiChu.getText().trim());

        // Cập nhật loại dịch vụ
        if (cboLoaiDV.getSelectedIndex() > 0) {
            dichVu.setMaLoaiDV(listLoaiDV.get(cboLoaiDV.getSelectedIndex() - 1).getMaLoaiDV());
        } else {
            dichVu.setMaLoaiDV(null);
        }

        try {
            boolean success = dichVuService.updateDichVu(dichVu);
            if (success) {
                hienThiThongBao("Cập nhật dịch vụ thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadAllDichVu();
                dialog.dispose();
            } else {
                hienThiThongBao("Cập nhật dịch vụ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi cập nhật dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaDichVu() {
        int selectedRow = view.getTblDichVu().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một dịch vụ để xóa", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int maDichVu = (int) view.getModel().getValueAt(selectedRow, 0);
            String tenDichVu = (String) view.getModel().getValueAt(selectedRow, 1);

            boolean confirmed = hienThiXacNhan("Bạn có chắc muốn xóa dịch vụ '" + tenDichVu + "' không?");
            
            if (confirmed) {
                boolean success = dichVuService.deleteDichVu(maDichVu);
                if (success) {
                    hienThiThongBao("Xóa dịch vụ thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadAllDichVu();
                } else {
                    hienThiThongBao("Xóa dịch vụ thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi xóa dịch vụ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiemDichVu() {
        try {
            String tuKhoa = view.getTxtTimKiem().getText().trim();
            String loaiFilter = (String) view.getCboLoaiFilter().getSelectedItem();

            List<DichVu> ketQua = !tuKhoa.isEmpty()
                    ? dichVuService.searchDichVuByTen(tuKhoa)
                    : dichVuService.getAllDichVu();

            // Lọc theo loại dịch vụ
            if (!"Tất cả".equals(loaiFilter)) {
                ketQua.removeIf(dv -> !loaiFilter.equals(getTenLoaiDichVu(dv.getMaLoaiDV())));
            }

            displayDichVuOnTable(ketQua);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CÁC PHƯƠNG THỨC HIỂN THỊ THÔNG BÁO GIỐNG QuanLyKhachHangController

    // PHƯƠNG THỨC HIỂN THỊ THÔNG BÁO CUSTOM VỚI MÀU XANH TRÀN VIỀN
    private void hienThiThongBao(String message, String title, int messageType) {
        JDialog dialog = createCustomDialog(message, title, messageType);
        dialog.setVisible(true);
    }

    // PHƯƠNG THỨC HIỂN THỊ XÁC NHẬN CUSTOM VỚI MÀU XANH TRÀN VIỀN
    private boolean hienThiXacNhan(String message) {
        JDialog dialog = createConfirmationDialog(message);
        final boolean[] result = {false};
        
        // Đợi dialog đóng
        dialog.setVisible(true);
        
        return result[0];
    }

    // PHƯƠNG THỨC TẠO CUSTOM DIALOG
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

    // PHƯƠNG THỨC TẠO DIALOG XÁC NHẬN
    private JDialog createConfirmationDialog(String message) {
        // Tạo custom buttons
        JButton btnCo = createStyledButton("Có", COLOR_BUTTON);
        JButton btnKhong = createStyledButton("Không", new Color(149, 165, 166)); // Màu xám cho nút "Không"
        
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

        return dialog;
    }

    // PHƯƠNG THỨC TẠO BUTTON STYLE
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
}