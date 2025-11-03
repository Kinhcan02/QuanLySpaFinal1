package Controller;

import Model.CaLam;
import Model.NhanVien;
import Service.CaLamService;
import Service.NhanVienService;
import View.QuanLyCaLamView;
import java.awt.Color;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class QuanLyCaLamController {

    private QuanLyCaLamView view;
    private CaLamService caLamService;
    private NhanVienService nhanVienService;

    // Colors
    private final Color COLOR_BACKGROUND = new Color(0x8C, 0xC9, 0x80);
    private final Color COLOR_BUTTON = new Color(0x4D, 0x8A, 0x57);
    private final Color COLOR_TEXT = Color.WHITE;

    public QuanLyCaLamController(QuanLyCaLamView view, CaLamService caLamService, NhanVienService nhanVienService) {
        this.view = view;
        this.caLamService = caLamService;
        this.nhanVienService = nhanVienService;
        initController();
        loadNhanVienData();
        loadDataForSelectedDate();
        setInitialState();
        setupAutoFeatures();
    }

    private void initController() {
        // Calendar navigation
        view.getBtnThangTruoc().addActionListener(e -> navigateMonth(-1));
        view.getBtnThangSau().addActionListener(e -> navigateMonth(1));

        // Date selection callback
        view.setOnDateSelected(this::onDateSelected);

        // Table selection listener
        view.getTblCaLam().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCaLamFromTable();
            }
        });

        // Button listeners
        view.getBtnThem().addActionListener(e -> themCaLam());
        view.getBtnSua().addActionListener(e -> suaCaLam());
        view.getBtnXoa().addActionListener(e -> xoaCaLam());
        view.getBtnLamMoi().addActionListener(e -> lamMoi());
        view.getBtnThemTip().addActionListener(e -> themTip());
        view.getBtnXemLichSuTip().addActionListener(e -> xemLichSuTip());

        // Enter key listeners for quick input
        addEnterKeyListener(view.getTxtGioBatDau(), () -> view.getTxtGioKetThuc().requestFocus());
        addEnterKeyListener(view.getTxtGioKetThuc(), () -> autoCalculateHours());
        addEnterKeyListener(view.getTxtSoGioTangCa(), () -> view.getTxtSoLuongKhach().requestFocus());
        addEnterKeyListener(view.getTxtSoLuongKhach(), () -> view.getTxtTienTip().requestFocus());
        addEnterKeyListener(view.getTxtTienTip(), () -> {
            if (view.getCboNhanVien().getSelectedItem() != null) {
                themCaLam();
            }
        });
    }

    private void addEnterKeyListener(JTextField textField, Runnable action) {
        textField.addActionListener(e -> action.run());
    }

    private void setupAutoFeatures() {
        // Auto calculate hours when start/end time changes
        view.getTxtGioBatDau().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                autoCalculateHours();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                autoCalculateHours();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                autoCalculateHours();
            }
        });

        view.getTxtGioKetThuc().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                autoCalculateHours();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                autoCalculateHours();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                autoCalculateHours();
            }
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
        view.setFormEditable(true);
        view.setButtonState(true, false, false);
        view.clearForm();
    }

    private void loadNhanVienData() {
        try {
            List<NhanVien> nhanVienList = nhanVienService.getAllNhanVien();
            view.loadNhanVienList(nhanVienList);
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách nhân viên: " + e.getMessage());
        }
    }

    private void loadDataForSelectedDate() {
        try {
            List<CaLam> danhSachCaLam = caLamService.getCaLamByNgay(view.getSelectedDate());
            updateTable(danhSachCaLam);
        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void onDateSelected(LocalDate selectedDate) {
        loadDataForSelectedDate();
    }

    private void navigateMonth(int months) {
        LocalDate newDate = view.getCurrentDate().plusMonths(months);
        view.setCurrentDate(newDate);
        loadDataForSelectedDate();
    }

    private void updateTable(List<CaLam> danhSachCaLam) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (CaLam caLam : danhSachCaLam) {
            model.addRow(new Object[]{
                caLam.getMaCa(),
                getTenNhanVien(caLam.getMaNhanVien()),
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

    private String getTenNhanVien(int maNhanVien) {
        try {
            NhanVien nv = nhanVienService.getNhanVienById(maNhanVien);
            return nv != null ? nv.getHoTen() : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

    private void selectCaLamFromTable() {
        int selectedRow = view.getTblCaLam().getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = view.getTableModel();

            view.getTxtMaCa().setText(model.getValueAt(selectedRow, 0).toString());

            // Find and select the correct employee in combobox
            String tenNhanVien = model.getValueAt(selectedRow, 1).toString();
            for (int i = 0; i < view.getCboNhanVien().getItemCount(); i++) {
                NhanVien nv = view.getCboNhanVien().getItemAt(i);
                if (nv.getHoTen().equals(tenNhanVien)) {
                    view.getCboNhanVien().setSelectedIndex(i);
                    break;
                }
            }

            // Parse date from display format
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

            view.setButtonState(true, true, true);
        }
    }

    private void themCaLam() {
        try {
            if (!validateForm()) {
                return;
            }

            CaLam caLam = getCaLamFromForm();
            boolean success = caLamService.addCaLam(caLam);

            if (success) {
                showMessage("Đã thêm ca làm mới thành công");
                loadDataForSelectedDate();
                view.clearForm();
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
            if (!validateForm()) {
                return;
            }

            CaLam caLam = getCaLamFromForm();
            boolean success = caLamService.updateCaLam(caLam);

            if (success) {
                showMessage("Đã cập nhật ca làm thành công");
                loadDataForSelectedDate();
                setInitialState();
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
                boolean success = caLamService.deleteCaLam(maCa);

                if (success) {
                    showMessage("Đã xóa ca làm thành công");
                    loadDataForSelectedDate();
                    setInitialState();
                } else {
                    showError("Xóa ca làm thất bại");
                }
            } catch (Exception e) {
                showError("Lỗi khi xóa ca làm: " + e.getMessage());
            }
        }
    }

    private void themTip() {
        if (view.getTxtMaCa().getText().isEmpty()) {
            showError("Vui lòng chọn ca làm để thêm tip");
            return;
        }

        try {
            String tienTipStr = view.getTxtTienTip().getText().trim();
            String ghiChu = view.getTxtGhiChuTip().getText().trim();

            if (tienTipStr.isEmpty()) {
                showError("Vui lòng nhập số tiền tip");
                return;
            }

            BigDecimal tienTip = new BigDecimal(tienTipStr);
            if (tienTip.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Số tiền tip phải lớn hơn 0");
                return;
            }

            int maCa = Integer.parseInt(view.getTxtMaCa().getText());

            // Get current ca lam to calculate new total
            CaLam caLam = caLamService.getCaLamById(maCa);
            BigDecimal tongTipMoi = caLam.getTienTip().add(tienTip);

            // Update tip in database
            boolean success = caLamService.updateTienTip(maCa, tongTipMoi);

            if (success) {
                showMessage("Đã thêm tip " + String.format("%,d", tienTip.intValue()) + " VND thành công");
                loadDataForSelectedDate();
                view.getTxtTienTip().setText("0");
                view.getTxtGhiChuTip().setText("");
            } else {
                showError("Thêm tip thất bại");
            }

        } catch (Exception e) {
            showError("Lỗi khi thêm tip: " + e.getMessage());
        }
    }

    private void xemLichSuTip() {
        // This would show a dialog with tip history
        // For now, just show a message
        showMessage("Tính năng xem lịch sử tip đang được phát triển");
    }

    private void lamMoi() {
        loadDataForSelectedDate();
        setInitialState();
    }

    private boolean validateForm() {
        if (view.getCboNhanVien().getSelectedItem() == null) {
            showError("Vui lòng chọn nhân viên");
            view.getCboNhanVien().requestFocus();
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

    private CaLam getCaLamFromForm() throws Exception {
        NhanVien selectedNV = (NhanVien) view.getCboNhanVien().getSelectedItem();
        int maNhanVien = selectedNV.getMaNhanVien();

        LocalDate ngayLam = LocalDate.parse(view.getTxtNgayLam().getText().trim());
        LocalTime gioBatDau = LocalTime.parse(view.getTxtGioBatDau().getText().trim());
        LocalTime gioKetThuc = LocalTime.parse(view.getTxtGioKetThuc().getText().trim());

        BigDecimal soGioLam = new BigDecimal(view.getTxtSoGioLam().getText().trim());
        BigDecimal soGioTangCa = new BigDecimal(view.getTxtSoGioTangCa().getText().trim());
        int soLuongKhach = Integer.parseInt(view.getTxtSoLuongKhach().getText().trim());
        BigDecimal tienTip = new BigDecimal(view.getTxtTienTip().getText().trim());

        // If has maCa (editing), create CaLam with maCa
        if (!view.getTxtMaCa().getText().isEmpty()) {
            int maCa = Integer.parseInt(view.getTxtMaCa().getText());
            return new CaLam(maCa, maNhanVien, ngayLam, gioBatDau, gioKetThuc,
                    soGioLam, soGioTangCa, soLuongKhach, tienTip, null, null);
        } else {
            // If no maCa (adding new)
            return new CaLam(null, maNhanVien, ngayLam, gioBatDau, gioKetThuc,
                    soGioLam, soGioTangCa, soLuongKhach, tienTip, null, null);
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
