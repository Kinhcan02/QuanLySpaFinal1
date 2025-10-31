package Controller;

import View.QuanLyKhachHangView;
import Model.KhachHang;
import Service.KhachHangService;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.toedter.calendar.JDateChooser;

public class QuanLyKhachHangController {

    private QuanLyKhachHangView view;
    private KhachHangService service;

    // Màu sắc
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80); // Màu nền #8cc980
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);     // Màu nút #4d8a57
    private final Color COLOR_TEXT = Color.WHITE;                       // Màu chữ #ffffff

    public QuanLyKhachHangController(QuanLyKhachHangView view) {
        this.view = view;
        this.service = new KhachHangService();

        initController();
        loadAllKhachHang();
    }

    private void initController() {
        view.getBtnThem().addActionListener(e -> themKhachHang());
        view.getBtnSua().addActionListener(e -> suaKhachHang());
        view.getBtnXoa().addActionListener(e -> xoaKhachHang());
        view.getBtnLamMoi().addActionListener(e -> lamMoi());
        view.getBtnTimKiem().addActionListener(e -> timKiemKhachHang());
    }

    private void lamMoi() {
        loadAllKhachHang();
        view.getTxtTimKiem().setText("");
        view.getCboLoaiFilter().setSelectedIndex(0);
    }

    private void loadAllKhachHang() {
        try {
            List<KhachHang> listKhachHang = service.getAllKhachHang();
            displayKhachHangOnTable(listKhachHang);
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tải danh sách khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayKhachHangOnTable(List<KhachHang> listKhachHang) {
        DefaultTableModel model = view.getModel();
        model.setRowCount(0);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        for (KhachHang kh : listKhachHang) {
            model.addRow(new Object[]{
                kh.getMaKhachHang(),
                kh.getHoTen(),
                kh.getNgaySinh() != null ? kh.getNgaySinh().format(dateFormatter) : "",
                kh.getLoaiKhach(),
                kh.getSoDienThoai(),
                kh.getGhiChu(),
                kh.getNgayTao() != null ? kh.getNgayTao().format(dateTimeFormatter) : "",
                kh.getDiemTichLuy()
            });
        }
    }

    private void themKhachHang() {
        try {
            JDialog dialog = createDialog("Thêm Khách Hàng Mới", 500, 550);
            JPanel mainPanel = createMainPanel();

            // Form nhập liệu với BorderLayout
            JPanel formPanel = new JPanel(new BorderLayout(10, 10));
            formPanel.setBackground(COLOR_BACKGROUND);

            // Panel cho các field thông thường
            JPanel basicInfoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            basicInfoPanel.setBackground(COLOR_BACKGROUND);

            JTextField txtHoTen = new JTextField();
            JDateChooser dateChooserNgaySinh = view.createStyledDateChooser();
            JComboBox<String> cboLoaiKhach = new JComboBox<>(new String[]{"Thân thiết", "Thường xuyên", "Mới"});
            JTextField txtSoDienThoai = new JTextField();

            basicInfoPanel.add(createStyledLabel("Họ tên:"));
            basicInfoPanel.add(txtHoTen);
            basicInfoPanel.add(createStyledLabel("Ngày sinh:"));
            basicInfoPanel.add(dateChooserNgaySinh);
            basicInfoPanel.add(createStyledLabel("Loại khách:"));
            basicInfoPanel.add(cboLoaiKhach);
            basicInfoPanel.add(createStyledLabel("Số điện thoại:"));
            basicInfoPanel.add(txtSoDienThoai);

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

            btnThem.addActionListener(e -> handleThemKhachHang(dialog, txtHoTen, dateChooserNgaySinh, cboLoaiKhach, txtSoDienThoai, txtGhiChu));
            btnHuy.addActionListener(e -> dialog.dispose());

            buttonPanel.add(btnThem);
            buttonPanel.add(btnHuy);

            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi thêm khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleThemKhachHang(JDialog dialog, JTextField txtHoTen, JDateChooser dateChooserNgaySinh,
            JComboBox<String> cboLoaiKhach, JTextField txtSoDienThoai, JTextArea txtGhiChu) {

        // Validate dữ liệu
        if (txtHoTen.getText().trim().isEmpty()) {
            hienThiThongBao("Họ tên không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtSoDienThoai.getText().trim().isEmpty()) {
            hienThiThongBao("Số điện thoại không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate số điện thoại trước khi gọi service
        String soDienThoai = txtSoDienThoai.getText().trim();
        if (!isValidPhoneNumber(soDienThoai)) {
            hienThiThongBao("Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại có 10-11 chữ số", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Parse ngày sinh từ JDateChooser
        LocalDate ngaySinh = null;
        if (dateChooserNgaySinh.getDate() != null) {
            ngaySinh = dateChooserNgaySinh.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        KhachHang khachHang = new KhachHang(
                txtHoTen.getText().trim(),
                ngaySinh,
                (String) cboLoaiKhach.getSelectedItem(),
                soDienThoai,
                txtGhiChu.getText().trim()
        );

        boolean success = service.addKhachHang(khachHang);
        if (success) {
            hienThiThongBao("Thêm khách hàng thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadAllKhachHang();
            dialog.dispose();
        } else {
            hienThiThongBao("Thêm khách hàng thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        String cleanedPhone = phone.trim().replaceAll("\\s+", "").replaceAll("-", "").replaceAll("\\+", "");
        return cleanedPhone.matches("\\d{9,11}");
    }

    private void suaKhachHang() {
        int selectedRow = view.getTblKhachHang().getSelectedRow();
        if (selectedRow == -1) {
            hienThiThongBao("Vui lòng chọn một khách hàng để sửa", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int maKhachHang = (int) view.getModel().getValueAt(selectedRow, 0);
            KhachHang khachHang = service.getKhachHangById(maKhachHang);
            if (khachHang == null) {
                hienThiThongBao("Không tìm thấy khách hàng cần sửa", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showEditKhachHangForm(khachHang);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi sửa khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditKhachHangForm(KhachHang khachHang) {
        try {
            JDialog dialog = createDialog("Sửa Khách Hàng", 500, 550);
            JPanel mainPanel = createMainPanel();

            // Form nhập liệu với BorderLayout - GIỐNG FORM THÊM
            JPanel formPanel = new JPanel(new BorderLayout(10, 10));
            formPanel.setBackground(COLOR_BACKGROUND);

            // Panel cho các field thông thường - GIỐNG FORM THÊM
            JPanel basicInfoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            basicInfoPanel.setBackground(COLOR_BACKGROUND);

            JTextField txtHoTen = new JTextField(khachHang.getHoTen());
            JDateChooser dateChooserNgaySinh = view.createStyledDateChooser();
            if (khachHang.getNgaySinh() != null) {
                dateChooserNgaySinh.setDate(Date.from(khachHang.getNgaySinh().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            JComboBox<String> cboLoaiKhach = new JComboBox<>(new String[]{"Thân thiết", "Thường xuyên", "Mới"});
            cboLoaiKhach.setSelectedItem(khachHang.getLoaiKhach());
            JTextField txtSoDienThoai = new JTextField(khachHang.getSoDienThoai());

            // SẮP XẾP THEO ĐÚNG THỨ TỰ GIỐNG FORM THÊM
            basicInfoPanel.add(createStyledLabel("Họ tên:"));
            basicInfoPanel.add(txtHoTen);
            basicInfoPanel.add(createStyledLabel("Ngày sinh:"));
            basicInfoPanel.add(dateChooserNgaySinh);
            basicInfoPanel.add(createStyledLabel("Loại khách:"));
            basicInfoPanel.add(cboLoaiKhach);
            basicInfoPanel.add(createStyledLabel("Số điện thoại:"));
            basicInfoPanel.add(txtSoDienThoai);

            // Panel cho ghi chú - GIỐNG FORM THÊM
            JPanel ghiChuPanel = new JPanel(new BorderLayout(5, 5));
            ghiChuPanel.setBackground(COLOR_BACKGROUND);

            JLabel lblGhiChu = createStyledLabel("Ghi chú:");
            JTextArea txtGhiChu = new JTextArea(6, 30);
            txtGhiChu.setText(khachHang.getGhiChu() != null ? khachHang.getGhiChu() : "");
            txtGhiChu.setLineWrap(true);
            txtGhiChu.setWrapStyleWord(true);
            JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
            scrollGhiChu.setPreferredSize(new Dimension(400, 120));

            ghiChuPanel.add(lblGhiChu, BorderLayout.NORTH);
            ghiChuPanel.add(scrollGhiChu, BorderLayout.CENTER);

            // Thêm các panel vào form chính - GIỐNG FORM THÊM
            formPanel.add(basicInfoPanel, BorderLayout.NORTH);
            formPanel.add(ghiChuPanel, BorderLayout.CENTER);

            // Panel nút - GIỐNG FORM THÊM
            JPanel buttonPanel = createButtonPanel();
            JButton btnCapNhat = createStyledButton("Cập nhật", COLOR_BUTTON);
            JButton btnHuy = createStyledButton("Hủy", COLOR_BUTTON);

            btnCapNhat.addActionListener(e -> handleCapNhatKhachHang(dialog, khachHang, txtHoTen, dateChooserNgaySinh, cboLoaiKhach, txtSoDienThoai, txtGhiChu));
            btnHuy.addActionListener(e -> dialog.dispose());

            buttonPanel.add(btnCapNhat);
            buttonPanel.add(btnHuy);

            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi hiển thị form sửa khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCapNhatKhachHang(JDialog dialog, KhachHang khachHang, JTextField txtHoTen,
            JDateChooser dateChooserNgaySinh, JComboBox<String> cboLoaiKhach, JTextField txtSoDienThoai, JTextArea txtGhiChu) {

        // Validate dữ liệu
        if (txtHoTen.getText().trim().isEmpty()) {
            hienThiThongBao("Họ tên không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtSoDienThoai.getText().trim().isEmpty()) {
            hienThiThongBao("Số điện thoại không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Parse ngày sinh từ JDateChooser
        LocalDate ngaySinh = null;
        if (dateChooserNgaySinh.getDate() != null) {
            ngaySinh = dateChooserNgaySinh.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        // Cập nhật thông tin khách hàng
        khachHang.setHoTen(txtHoTen.getText().trim());
        khachHang.setNgaySinh(ngaySinh);
        khachHang.setLoaiKhach((String) cboLoaiKhach.getSelectedItem());
        khachHang.setSoDienThoai(txtSoDienThoai.getText().trim());
        khachHang.setGhiChu(txtGhiChu.getText().trim());

        try {
            boolean success = service.updateKhachHang(khachHang);
            if (success) {
                hienThiThongBao("Cập nhật khách hàng thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadAllKhachHang();
                dialog.dispose();
            } else {
                hienThiThongBao("Cập nhật khách hàng thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi cập nhật khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhachHang() {
        try {
            int selectedRow = view.getTblKhachHang().getSelectedRow();
            if (selectedRow == -1) {
                hienThiThongBao("Vui lòng chọn khách hàng cần xóa", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int maKhachHang = (int) view.getModel().getValueAt(selectedRow, 0);
            String tenKhachHang = (String) view.getModel().getValueAt(selectedRow, 1);
            
            // Kiểm tra dữ liệu liên quan
            Map<String, Integer> duLieuLienQuan = service.kiemTraDuLieuLienQuan(maKhachHang);
            
            if (!duLieuLienQuan.isEmpty()) {
                StringBuilder message = new StringBuilder();
                message.append("Không thể xóa khách hàng '").append(tenKhachHang).append("' vì có dữ liệu liên quan:\n\n");
                
                for (Map.Entry<String, Integer> entry : duLieuLienQuan.entrySet()) {
                    message.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" bản ghi\n");
                }
                
                message.append("\nVui lòng xóa các dữ liệu liên quan trước khi xóa khách hàng.");
                hienThiThongBao(message.toString(), "Không thể xóa", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Xác nhận xóa - SỬA LẠI PHẦN NÀY
            int confirmed = JOptionPane.showConfirmDialog(
                view, 
                "Bạn có chắc muốn xóa khách hàng '" + tenKhachHang + "' không?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirmed == JOptionPane.YES_OPTION) {
                try {
                    boolean success = service.deleteKhachHang(maKhachHang);
                    
                    if (success) {
                        hienThiThongBao("Xóa khách hàng thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadAllKhachHang(); // Refresh danh sách
                    } else {
                        hienThiThongBao("Xóa khách hàng thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    hienThiThongBao("Lỗi khi xóa khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            hienThiThongBao("Lỗi khi xóa khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void timKiemKhachHang() {
        try {
            String tuKhoa = view.getTxtTimKiem().getText().trim();
            String loaiFilter = (String) view.getCboLoaiFilter().getSelectedItem();

            List<KhachHang> ketQua = !tuKhoa.isEmpty()
                    ? service.searchKhachHangByHoTen(tuKhoa)
                    : service.getAllKhachHang();

            // Lọc theo loại khách hàng
            if (!"Tất cả".equals(loaiFilter)) {
                ketQua.removeIf(kh -> !loaiFilter.equals(kh.getLoaiKhach()));
            }

            displayKhachHangOnTable(ketQua);

        } catch (Exception e) {
            hienThiThongBao("Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CÁC PHƯƠNG THỨC HIỂN THỊ THÔNG BÁO
    private void hienThiThongBao(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(view, message, title, messageType);
    }

    private JDialog createDialog(String title, int width, int height) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(view);
        dialog.setResizable(false);
        return dialog;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(COLOR_BACKGROUND);
        return mainPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(COLOR_BACKGROUND);
        return buttonPanel;
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