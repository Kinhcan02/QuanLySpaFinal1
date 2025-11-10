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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class QuanLyCaLamController {

    private QuanLyCaLamView view;
    private CaLamService caLamService;
    private NhanVienService nhanVienService;

    public QuanLyCaLamController(QuanLyCaLamView view, CaLamService caLamService, NhanVienService nhanVienService) {
        this.view = view;
        this.caLamService = caLamService;
        this.nhanVienService = nhanVienService;
        initController();
        loadNhanVienData();
        loadDataForSelectedDate();
        setCurrentDate(LocalDate.now());
    }

    private void initController() {
        // Table selection listener
        view.getTblCaLam().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = view.getTblCaLam().getSelectedRow();
                if (selectedRow >= 0) {
                    fillFormFromTable(selectedRow);
                }
            }
        });

        // Button listeners
        view.getBtnThem().addActionListener(e -> themCaLam());
        view.getBtnSua().addActionListener(e -> suaCaLam());
        view.getBtnXoa().addActionListener(e -> xoaCaLam());
        view.getBtnLamMoi().addActionListener(e -> lamMoiForm());
        view.getBtnThemTip().addActionListener(e -> themTip());

        // Calendar navigation
        view.getBtnThangTruoc().addActionListener(e -> navigateMonth(-1));
        view.getBtnThangSau().addActionListener(e -> navigateMonth(1));

        // Date selection callback
        view.setOnDateSelected(selectedDate -> {
            view.setSelectedDate(selectedDate);
            loadDataForSelectedDate();
        });

        // Date text field change
        view.getTxtNgayLam().addActionListener(e -> {
            try {
                LocalDate selectedDate = LocalDate.parse(view.getTxtNgayLam().getText());
                view.setSelectedDate(selectedDate);
                loadDataForSelectedDate();
            } catch (Exception ex) {
                showMessage("Định dạng ngày không hợp lệ (yyyy-MM-dd)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Auto calculate hours
        view.getTxtGioBatDau().addActionListener(e -> tinhSoGioLam());
        view.getTxtGioKetThuc().addActionListener(e -> tinhSoGioLam());
    }

    private void loadNhanVienData() {
        try {
            List<NhanVien> nhanViens = nhanVienService.getAllNhanVien();
            view.loadNhanVienList(nhanViens);
        } catch (Exception e) {
            showMessage("Lỗi khi tải danh sách nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDataForSelectedDate() {
        try {
            LocalDate selectedDate = view.getSelectedDate();
            List<CaLam> caLams = caLamService.getCaLamByNgay(selectedDate);
            updateTable(caLams);
        } catch (Exception e) {
            showMessage("Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<CaLam> caLams) {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (CaLam caLam : caLams) {
            String tenNhanVien = getTenNhanVien(caLam.getMaNhanVien());
            String ngayLam = caLam.getNgayLam().format(dateFormatter);
            String gioBatDau = caLam.getGioBatDau().format(timeFormatter);
            String gioKetThuc = caLam.getGioKetThuc().format(timeFormatter);
            String soGioLam = String.format("%.1f", caLam.getSoGioLam());
            String soGioTangCa = caLam.getSoGioTangCa() != null
                    ? String.format("%.1f", caLam.getSoGioTangCa()) : "0";
            String tienTip = formatCurrency(caLam.getTienTip());

            model.addRow(new Object[]{
                tenNhanVien, ngayLam, gioBatDau, gioKetThuc,
                soGioLam, soGioTangCa, tienTip
            });
        }
    }

    private void themCaLam() {
        try {
            CaLam caLam = getCaLamFromForm();
            if (caLam == null) {
                return;
            }

            if (caLamService.addCaLam(caLam)) {
                showMessage("Thêm ca làm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataForSelectedDate();
                lamMoiForm();
            } else {
                showMessage("Thêm ca làm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            showMessage("Lỗi khi thêm ca làm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaCaLam() {
        int selectedRow = view.getTblCaLam().getSelectedRow();
        if (selectedRow < 0) {
            showMessage("Vui lòng chọn ca làm cần sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            CaLam caLam = getCaLamFromForm();
            if (caLam == null) {
                return;
            }

            // Get original CaLam to preserve ID
            LocalDate selectedDate = view.getSelectedDate();
            List<CaLam> caLams = caLamService.getCaLamByNgay(selectedDate);
            CaLam originalCaLam = caLams.get(selectedRow);
            caLam.setMaCa(originalCaLam.getMaCa());

            if (caLamService.updateCaLam(caLam)) {
                showMessage("Cập nhật ca làm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataForSelectedDate();
                lamMoiForm();
            } else {
                showMessage("Cập nhật ca làm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            showMessage("Lỗi khi cập nhật ca làm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaCaLam() {
        int selectedRow = view.getTblCaLam().getSelectedRow();
        if (selectedRow < 0) {
            showMessage("Vui lòng chọn ca làm cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc chắn muốn xóa ca làm này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                LocalDate selectedDate = view.getSelectedDate();
                List<CaLam> caLams = caLamService.getCaLamByNgay(selectedDate);
                CaLam caLam = caLams.get(selectedRow);

                if (caLamService.deleteCaLam(caLam.getMaCa())) {
                    showMessage("Xóa ca làm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadDataForSelectedDate();
                    lamMoiForm();
                } else {
                    showMessage("Xóa ca làm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                showMessage("Lỗi khi xóa ca làm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void themTip() {
        int selectedRow = view.getTblCaLam().getSelectedRow();
        if (selectedRow < 0) {
            showMessage("Vui lòng chọn ca làm để thêm tip!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate selectedDate = view.getSelectedDate();
            List<CaLam> caLams = caLamService.getCaLamByNgay(selectedDate);
            CaLam caLam = caLams.get(selectedRow);

            String tenNhanVien = getTenNhanVien(caLam.getMaNhanVien());
            String currentTip = formatCurrency(caLam.getTienTip());

            String tienTipStr = JOptionPane.showInputDialog(
                    view,
                    "Nhân viên: " + tenNhanVien
                    + "\nNgày làm: " + caLam.getNgayLam()
                    + "\nTip hiện tại: " + currentTip
                    + "\n\nNhập số tiền tip mới (sẽ cộng dồn):",
                    "Thêm Tip",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (tienTipStr != null && !tienTipStr.trim().isEmpty()) {
                BigDecimal tienTipMoi = new BigDecimal(tienTipStr.trim());

                // Cộng dồn tip mới vào tip hiện tại
                BigDecimal tienTipTong = caLam.getTienTip().add(tienTipMoi);

                if (caLamService.updateTienTip(caLam.getMaCa(), tienTipTong)) {
                    showMessage("Thêm tip thành công!\nTổng tip hiện tại: " + formatCurrency(tienTipTong),
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadDataForSelectedDate();
                } else {
                    showMessage("Thêm tip thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            showMessage("Số tiền tip không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            showMessage("Lỗi khi thêm tip: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lamMoiForm() {
        view.clearForm();
        tinhSoGioLam();
    }

    private void navigateMonth(int months) {
        LocalDate currentDate = view.getCurrentDate();
        view.setCurrentDate(currentDate.plusMonths(months));
        loadDataForSelectedDate();
    }

    private void setCurrentDate(LocalDate date) {
        view.setCurrentDate(date);
        view.setSelectedDate(date);
    }

    private void tinhSoGioLam() {
        try {
            String gioBatDauStr = view.getTxtGioBatDau().getText().trim();
            String gioKetThucStr = view.getTxtGioKetThuc().getText().trim();

            if (!gioBatDauStr.isEmpty() && !gioKetThucStr.isEmpty()) {
                LocalTime gioBatDau = LocalTime.parse(gioBatDauStr);
                LocalTime gioKetThuc = LocalTime.parse(gioKetThucStr);

                if (gioBatDau.isAfter(gioKetThuc)) {
                    view.getTxtSoGioLam().setText("");
                    return;
                }

                long minutes = java.time.Duration.between(gioBatDau, gioKetThuc).toMinutes();
                double hours = minutes / 60.0;
                view.getTxtSoGioLam().setText(String.format("%.2f", hours));
            }
        } catch (DateTimeParseException e) {
            // Ignore parse errors during typing
        }
    }

    private CaLam getCaLamFromForm() {
        try {
            NhanVien selectedNV = (NhanVien) view.getCboNhanVien().getSelectedItem();
            if (selectedNV == null) {
                showMessage("Vui lòng chọn nhân viên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            LocalDate ngayLam = LocalDate.parse(view.getTxtNgayLam().getText());
            LocalTime gioBatDau = LocalTime.parse(view.getTxtGioBatDau().getText());
            LocalTime gioKetThuc = LocalTime.parse(view.getTxtGioKetThuc().getText());

            if (gioBatDau.isAfter(gioKetThuc)) {
                showMessage("Giờ bắt đầu phải trước giờ kết thúc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            BigDecimal soGioLam = new BigDecimal(view.getTxtSoGioLam().getText());
            BigDecimal soGioTangCa = new BigDecimal(view.getTxtSoGioTangCa().getText());
            BigDecimal tienTip = new BigDecimal(view.getTxtTienTip().getText());

            CaLam caLam = new CaLam();
            caLam.setMaNhanVien(selectedNV.getMaNhanVien());
            caLam.setNgayLam(ngayLam);
            caLam.setGioBatDau(gioBatDau);
            caLam.setGioKetThuc(gioKetThuc);
            caLam.setSoGioLam(soGioLam);
            caLam.setSoGioTangCa(soGioTangCa);
            caLam.setTienTip(tienTip);

            return caLam;
        } catch (Exception e) {
            showMessage("Dữ liệu không hợp lệ: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void fillFormFromTable(int row) {
        try {
            LocalDate selectedDate = view.getSelectedDate();
            List<CaLam> caLams = caLamService.getCaLamByNgay(selectedDate);
            CaLam caLam = caLams.get(row);

            // Select nhân viên trong combobox
            for (int i = 0; i < view.getCboNhanVien().getItemCount(); i++) {
                NhanVien nv = view.getCboNhanVien().getItemAt(i);
                if (nv.getMaNhanVien() == caLam.getMaNhanVien()) {
                    view.getCboNhanVien().setSelectedIndex(i);
                    break;
                }
            }

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            view.getTxtNgayLam().setText(caLam.getNgayLam().toString());
            view.getTxtGioBatDau().setText(caLam.getGioBatDau().format(timeFormatter));
            view.getTxtGioKetThuc().setText(caLam.getGioKetThuc().format(timeFormatter));
            view.getTxtSoGioLam().setText(String.format("%.2f", caLam.getSoGioLam()));
            view.getTxtSoGioTangCa().setText(String.format("%.2f", caLam.getSoGioTangCa()));
            BigDecimal tienTip = caLam.getTienTip();
            // Loại bỏ phần thập phân nếu là số nguyên
            if (tienTip.stripTrailingZeros().scale() <= 0) {
                view.getTxtTienTip().setText(tienTip.toBigInteger().toString());
            } else {
                view.getTxtTienTip().setText(tienTip.toString());
            }
        } catch (Exception e) {
            showMessage("Lỗi khi điền form: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getTenNhanVien(int maNhanVien) {
        try {
            List<NhanVien> nhanViens = nhanVienService.getAllNhanVien();
            for (NhanVien nv : nhanViens) {
                if (nv.getMaNhanVien() == maNhanVien) {
                    return nv.getHoTen();
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return String.format("%,.0f VND", amount);
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(view, message, title, messageType);
    }
}
