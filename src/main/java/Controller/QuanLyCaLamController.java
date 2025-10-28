package Controller;

import Model.CaLam;
import Service.CaLamService;
import View.QuanLyCaLamView;
import java.awt.Color;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class QuanLyCaLamController {
    
    private QuanLyCaLamView view;
    private CaLamService service;
    
    // Màu sắc (giống QuanLyNhanVienController)
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;
    
    public QuanLyCaLamController(QuanLyCaLamView view, CaLamService service) {
        this.view = view;
        this.service = service;
        initController();
        loadData();
        setInitialState();
        setupAutoFeatures();
    }
    
    private void initController() {
        // Table selection listener - tự động điền form khi click
        view.getTblCaLam().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCaLamFromTable();
            }
        });
        
        // Button listeners - CHỈ CÒN 4 NÚT CHÍNH
        view.getBtnThem().addActionListener(e -> themCaLam());
        view.getBtnSua().addActionListener(e -> suaCaLam());
        view.getBtnXoa().addActionListener(e -> xoaCaLam());
        view.getBtnLamMoi().addActionListener(e -> lamMoi());
        view.getBtnTimKiem().addActionListener(e -> timKiemCaLam());
        view.getBtnTinhGioTuDong().addActionListener(e -> tinhGioLamTuDong());
        
        // Enter key listeners for quick input - ENTER SẼ TỰ ĐỘNG THÊM KHI NHẬP XONG
        addEnterKeyListener(view.getTxtMaNhanVien(), () -> view.getTxtGioBatDau().requestFocus());
        addEnterKeyListener(view.getTxtGioBatDau(), () -> view.getTxtGioKetThuc().requestFocus());
        addEnterKeyListener(view.getTxtGioKetThuc(), () -> tinhGioLamTuDong());
        addEnterKeyListener(view.getTxtSoGioTangCa(), () -> view.getTxtSoLuongKhach().requestFocus());
        addEnterKeyListener(view.getTxtSoLuongKhach(), () -> view.getTxtTienTip().requestFocus());
        addEnterKeyListener(view.getTxtTienTip(), () -> {
            // Nếu đang có dữ liệu trong form, Enter sẽ tự động thêm mới
            if (!view.getTxtMaNhanVien().getText().trim().isEmpty()) {
                themCaLam();
            }
        });
    }
    
    private void addEnterKeyListener(JTextField textField, Runnable action) {
        textField.addActionListener(e -> action.run());
    }
    
    private void setupAutoFeatures() {
        // Tự động tính giờ khi thay đổi giờ bắt đầu/kết thúc
        view.getTxtGioBatDau().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { autoCalculateHours(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { autoCalculateHours(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { autoCalculateHours(); }
        });
        
        view.getTxtGioKetThuc().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { autoCalculateHours(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { autoCalculateHours(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { autoCalculateHours(); }
        });
    }
    
    private void autoCalculateHours() {
        try {
            String gioBatDauStr = view.getTxtGioBatDau().getText().trim();
            String gioKetThucStr = view.getTxtGioKetThuc().getText().trim();
            
            if (!gioBatDauStr.isEmpty() && !gioKetThucStr.isEmpty()) {
                LocalTime gioBatDau = LocalTime.parse(gioBatDauStr);
                LocalTime gioKetThuc = LocalTime.parse(gioKetThucStr);
                
                if (gioBatDau.isBefore(gioKetThuc)) {
                    long minutes = java.time.Duration.between(gioBatDau, gioKetThuc).toMinutes();
                    double hours = minutes / 60.0;
                    view.showAutoCalculatedHours(hours);
                }
            }
        } catch (Exception e) {
            // Ignore parsing errors during typing
        }
    }
    
    private void setInitialState() {
        view.setFormEditable(true); // Form luôn editable để nhập nhanh
        view.setButtonState(true, false, false); // Chỉ nút Thêm enabled
        view.clearForm();
    }
    
    private void loadData() {
        try {
            List<CaLam> danhSachCaLam = service.getAllCaLam();
            updateTable(danhSachCaLam);
        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }
    
    private void updateTable(List<CaLam> danhSachCaLam) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (CaLam caLam : danhSachCaLam) {
            model.addRow(new Object[]{
                caLam.getMaCa(),
                caLam.getMaNhanVien(),
                caLam.getNgayLam().format(dateFormatter),
                caLam.getGioBatDau().format(timeFormatter),
                caLam.getGioKetThuc().format(timeFormatter),
                String.format("%.1f", caLam.getSoGioLam()),
                caLam.getSoGioTangCa() != null ? String.format("%.1f", caLam.getSoGioTangCa()) : "0",
                caLam.getSoLuongKhachPhucVu(),
                caLam.getTienTip() != null ? String.format("%,d", caLam.getTienTip().intValue()) : "0"
            });
        }
    }
    
    private void selectCaLamFromTable() {
        int selectedRow = view.getTblCaLam().getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = view.getTableModel();
            
            view.getTxtMaCa().setText(model.getValueAt(selectedRow, 0).toString());
            view.getTxtMaNhanVien().setText(model.getValueAt(selectedRow, 1).toString());
            
            // Parse date from display format (dd/MM/yyyy) back to input format (yyyy-MM-dd)
            String displayDate = model.getValueAt(selectedRow, 2).toString();
            try {
                LocalDate date = LocalDate.parse(displayDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                view.getTxtNgayLam().setText(date.toString());
            } catch (Exception e) {
                view.getTxtNgayLam().setText(displayDate);
            }
            
            view.getTxtGioBatDau().setText(model.getValueAt(selectedRow, 3).toString());
            view.getTxtGioKetThuc().setText(model.getValueAt(selectedRow, 4).toString());
            view.getTxtSoGioLam().setText(model.getValueAt(selectedRow, 5).toString());
            view.getTxtSoGioTangCa().setText(model.getValueAt(selectedRow, 6).toString());
            view.getTxtSoLuongKhach().setText(model.getValueAt(selectedRow, 7).toString());
            
            // Remove formatting from tip for editing
            String tipValue = model.getValueAt(selectedRow, 8).toString().replace(",", "");
            view.getTxtTienTip().setText(tipValue);
            
            view.setButtonState(true, true, true); // Bật cả 3 nút khi chọn dòng
        }
    }
    
    private void themCaLam() {
        try {
            if (!validateForm()) return;
            
            CaLam caLam = getCaLamFromForm();
            boolean success = service.addCaLam(caLam);
            
            if (success) {
                showMessage("Đã thêm ca làm mới thành công");
                loadData();
                view.clearForm(); // Clear form sau khi thêm thành công
                view.getTxtMaNhanVien().requestFocus(); // Focus lại ô đầu tiên
            } else {
                showError("Thêm ca làm thất bại");
            }
            
        } catch (Exception e) {
            showError("Lỗi khi thêm ca làm: " + e.getMessage());
        }
    }
    
    private void suaCaLam() {
        if (view.getTxtMaCa().getText().isEmpty()) {
            showError("Vui lòng chọn ca làm cần sửa");
            return;
        }
        
        try {
            if (!validateForm()) return;
            
            CaLam caLam = getCaLamFromForm();
            boolean success = service.updateCaLam(caLam);
            
            if (success) {
                showMessage("Đã cập nhật ca làm thành công");
                loadData();
                setInitialState(); // Quay về trạng thái ban đầu
            } else {
                showError("Cập nhật ca làm thất bại");
            }
            
        } catch (Exception e) {
            showError("Lỗi khi cập nhật ca làm: " + e.getMessage());
        }
    }
    
    private void xoaCaLam() {
        if (view.getTxtMaCa().getText().isEmpty()) {
            showError("Vui lòng chọn ca làm cần xóa");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            view, 
            "Bạn có chắc chắn muốn xóa ca làm này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int maCa = Integer.parseInt(view.getTxtMaCa().getText());
                boolean success = service.deleteCaLam(maCa);
                
                if (success) {
                    showMessage("Đã xóa ca làm thành công");
                    loadData();
                    setInitialState();
                } else {
                    showError("Xóa ca làm thất bại");
                }
            } catch (Exception e) {
                showError("Lỗi khi xóa ca làm: " + e.getMessage());
            }
        }
    }
    
    private boolean validateForm() {
        if (view.getTxtMaNhanVien().getText().trim().isEmpty()) {
            showError("Vui lòng nhập mã nhân viên");
            view.getTxtMaNhanVien().requestFocus();
            return false;
        }
        
        if (view.getTxtNgayLam().getText().trim().isEmpty()) {
            showError("Vui lòng nhập ngày làm");
            view.getTxtNgayLam().requestFocus();
            return false;
        }
        
        if (view.getTxtGioBatDau().getText().trim().isEmpty()) {
            showError("Vui lòng nhập giờ bắt đầu");
            view.getTxtGioBatDau().requestFocus();
            return false;
        }
        
        if (view.getTxtGioKetThuc().getText().trim().isEmpty()) {
            showError("Vui lòng nhập giờ kết thúc");
            view.getTxtGioKetThuc().requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void lamMoi() {
        loadData();
        setInitialState();
        view.getTxtTimKiem().setText("");
        view.getCboTimKiem().setSelectedIndex(0);
    }
    
    private void timKiemCaLam() {
        try {
            String loaiTimKiem = view.getCboTimKiem().getSelectedItem().toString();
            String giaTriTimKiem = view.getTxtTimKiem().getText().trim();
            
            List<CaLam> ketQua;
            
            switch (loaiTimKiem) {
                case "Theo mã NV":
                    if (giaTriTimKiem.isEmpty()) {
                        loadData();
                        return;
                    }
                    int maNV = Integer.parseInt(giaTriTimKiem);
                    ketQua = service.getCaLamByMaNhanVien(maNV);
                    break;
                    
                case "Theo ngày":
                    if (giaTriTimKiem.isEmpty()) {
                        ketQua = service.getCaLamByNgay(Date.valueOf(LocalDate.now()));
                    } else {
                        Date ngayLam = Date.valueOf(giaTriTimKiem);
                        ketQua = service.getCaLamByNgay(ngayLam);
                    }
                    break;
                    
                case "Theo tháng":
                    if (giaTriTimKiem.isEmpty()) {
                        YearMonth currentMonth = YearMonth.now();
                        LocalDate startDate = currentMonth.atDay(1);
                        LocalDate endDate = currentMonth.atEndOfMonth();
                        ketQua = getAllCaLamInRange(startDate, endDate);
                    } else {
                        YearMonth month = parseMonth(giaTriTimKiem);
                        LocalDate startDate = month.atDay(1);
                        LocalDate endDate = month.atEndOfMonth();
                        ketQua = getAllCaLamInRange(startDate, endDate);
                    }
                    break;
                    
                default:
                    ketQua = service.getAllCaLam();
                    break;
            }
            
            updateTable(ketQua);
            
        } catch (Exception e) {
            showError("Lỗi tìm kiếm: " + e.getMessage());
        }
    }
    
    private List<CaLam> getAllCaLamInRange(LocalDate startDate, LocalDate endDate) {
        return service.getAllCaLam().stream()
                .filter(caLam -> !caLam.getNgayLam().isBefore(startDate) && !caLam.getNgayLam().isAfter(endDate))
                .toList();
    }
    
    private YearMonth parseMonth(String monthStr) {
        try {
            if (monthStr.contains("/")) {
                return YearMonth.parse(monthStr, DateTimeFormatter.ofPattern("MM/yyyy"));
            } else {
                return YearMonth.parse(monthStr, DateTimeFormatter.ofPattern("yyyy-MM"));
            }
        } catch (DateTimeParseException e) {
            return YearMonth.now();
        }
    }
    
    private void tinhGioLamTuDong() {
        autoCalculateHours();
        view.getTxtSoGioTangCa().requestFocus();
    }
    
    private void chonNgayHienTai() {
        view.getTxtNgayLam().setText(LocalDate.now().toString());
    }
    
    private CaLam getCaLamFromForm() throws Exception {
        int maNhanVien = Integer.parseInt(view.getTxtMaNhanVien().getText().trim());
        LocalDate ngayLam = LocalDate.parse(view.getTxtNgayLam().getText().trim());
        LocalTime gioBatDau = LocalTime.parse(view.getTxtGioBatDau().getText().trim());
        LocalTime gioKetThuc = LocalTime.parse(view.getTxtGioKetThuc().getText().trim());
        
        BigDecimal soGioLam = new BigDecimal(view.getTxtSoGioLam().getText().trim());
        BigDecimal soGioTangCa = new BigDecimal(view.getTxtSoGioTangCa().getText().trim());
        int soLuongKhach = Integer.parseInt(view.getTxtSoLuongKhach().getText().trim());
        BigDecimal tienTip = new BigDecimal(view.getTxtTienTip().getText().trim());
        
        // Nếu có mã ca (đang sửa), tạo CaLam với mã ca
        if (!view.getTxtMaCa().getText().isEmpty()) {
            int maCa = Integer.parseInt(view.getTxtMaCa().getText());
            return new CaLam(maCa, maNhanVien, ngayLam, gioBatDau, gioKetThuc, 
                           soGioLam, soGioTangCa, soLuongKhach, tienTip);
        } else {
            // Nếu không có mã ca (đang thêm mới)
            return new CaLam(maNhanVien, ngayLam, gioBatDau, gioKetThuc, 
                           soGioLam, soGioTangCa, soLuongKhach, tienTip);
        }
    }
    
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}