package Controller;

import Model.NhanVien;
import Service.NhanVienService;
import View.QuanLyNhanVienView;
import View.PhanTramDichVuView;
import java.awt.Frame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

public class QuanLyNhanVienController {
    private final QuanLyNhanVienView view;
    private final NhanVienService service;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public QuanLyNhanVienController(QuanLyNhanVienView view) {
        this.view = view;
        this.service = new NhanVienService();
        initController();
        loadAllNhanVien();
    }

    private void initController() {
        // Sự kiện cho nút Thêm mới
        view.getBtnThem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themNhanVien();
            }
        });

        // Sự kiện cho nút Sửa
        view.getBtnSua().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaNhanVien();
            }
        });

        // Sự kiện cho nút Xóa
        view.getBtnXoa().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaNhanVien();
            }
        });

        // Sự kiện cho nút Làm mới
        view.getBtnLamMoi().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lamMoiForm();
                loadAllNhanVien();
            }
        });

        // Sự kiện cho nút Tìm kiếm
        view.getBtnTimKiem().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timKiemNhanVien();
            }
        });

        // Sự kiện cho nút Phần trăm dịch vụ
        view.getBtnPhanTramDichVu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moQuanLyPhanTramDichVu();
            }
        });

        // Sự kiện click trên bảng
        view.getTblNhanVien().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiThongTinNhanVien();
            }
        });
    }

    private void loadAllNhanVien() {
        try {
            List<NhanVien> list = service.getAllNhanVien();
            hienThiDanhSachNhanVien(list);
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách nhân viên: " + e.getMessage());
        }
    }

    private void hienThiDanhSachNhanVien(List<NhanVien> list) {
        DefaultTableModel model = view.getModel();
        model.setRowCount(0);

        for (NhanVien nv : list) {
            Object[] row = {
                nv.getMaNhanVien(),
                nv.getHoTen(),
                nv.getNgaySinh() != null ? nv.getNgaySinh().format(dateFormatter) : "",
                nv.getSoDienThoai(),
                nv.getDiaChi(),
                nv.getChucVu(),
                nv.getNgayVaoLam() != null ? nv.getNgayVaoLam().format(dateFormatter) : "",
                formatLuong(nv.getLuongCanBan()),
                nv.getThamNien()
            };
            model.addRow(row);
        }
    }

    private String formatLuong(BigDecimal luong) {
        if (luong == null) return "0.0";
        // Format để chỉ hiển thị .0 thay vì .000
        return String.format("%.1f", luong);
    }

    private void themNhanVien() {
        try {
            NhanVien nv = layThongTinNhanVienTuForm();
            if (nv == null) return;

            if (service.addNhanVien(nv)) {
                showSuccess("Thêm nhân viên thành công!");
                lamMoiForm();
                loadAllNhanVien();
            } else {
                showError("Thêm nhân viên thất bại!");
            }
        } catch (Exception e) {
            showError("Lỗi khi thêm nhân viên: " + e.getMessage());
        }
    }

    private void suaNhanVien() {
        try {
            String maNVStr = view.getTxtMaNhanVien().getText().trim();
            if (maNVStr.isEmpty()) {
                showError("Vui lòng chọn nhân viên cần sửa!");
                return;
            }

            int maNhanVien = Integer.parseInt(maNVStr);
            NhanVien nv = layThongTinNhanVienTuForm();
            if (nv == null) return;

            nv.setMaNhanVien(maNhanVien);

            if (service.updateNhanVien(nv)) {
                showSuccess("Cập nhật nhân viên thành công!");
                lamMoiForm();
                loadAllNhanVien();
            } else {
                showError("Cập nhật nhân viên thất bại!");
            }
        } catch (Exception e) {
            showError("Lỗi khi cập nhật nhân viên: " + e.getMessage());
        }
    }

    private void xoaNhanVien() {
        try {
            String maNVStr = view.getTxtMaNhanVien().getText().trim();
            if (maNVStr.isEmpty()) {
                showError("Vui lòng chọn nhân viên cần xóa!");
                return;
            }

            int maNhanVien = Integer.parseInt(maNVStr);
            int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc chắn muốn xóa nhân viên này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (service.deleteNhanVien(maNhanVien)) {
                    showSuccess("Xóa nhân viên thành công!");
                    lamMoiForm();
                    loadAllNhanVien();
                } else {
                    showError("Xóa nhân viên thất bại!");
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi xóa nhân viên: " + e.getMessage());
        }
    }

    private void timKiemNhanVien() {
        try {
            String tuKhoa = view.getTxtTimKiem().getText().trim();
            String chucVu = (String) view.getCboChucVuFilter().getSelectedItem();

            List<NhanVien> ketQua;

            if ("Tất cả".equals(chucVu)) {
                if (tuKhoa.isEmpty()) {
                    ketQua = service.getAllNhanVien();
                } else {
                    ketQua = service.searchNhanVienByHoTen(tuKhoa);
                }
            } else {
                if (tuKhoa.isEmpty()) {
                    ketQua = service.getNhanVienByChucVu(chucVu);
                } else {
                    // Tìm kiếm kết hợp
                    List<NhanVien> theoHoTen = service.searchNhanVienByHoTen(tuKhoa);
                    ketQua = theoHoTen.stream()
                            .filter(nv -> chucVu.equals(nv.getChucVu()))
                            .toList();
                }
            }

            hienThiDanhSachNhanVien(ketQua);
            if (ketQua.isEmpty()) {
                showInfo("Không tìm thấy nhân viên phù hợp!");
            }
        } catch (Exception e) {
            showError("Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    private void hienThiThongTinNhanVien() {
        int selectedRow = view.getTblNhanVien().getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = view.getModel();
            view.getTxtMaNhanVien().setText(model.getValueAt(selectedRow, 0).toString());
            view.getTxtHoTen().setText(model.getValueAt(selectedRow, 1).toString());
            
            // Hiển thị ngày sinh lên JDateChooser
            String ngaySinhStr = model.getValueAt(selectedRow, 2).toString();
            if (!ngaySinhStr.isEmpty()) {
                LocalDate ngaySinh = LocalDate.parse(ngaySinhStr, dateFormatter);
                Date date = Date.from(ngaySinh.atStartOfDay(ZoneId.systemDefault()).toInstant());
                view.getDateChooserNgaySinh().setDate(date);
            } else {
                view.getDateChooserNgaySinh().setDate(null);
            }
            
            view.getTxtSoDienThoai().setText(model.getValueAt(selectedRow, 3).toString());
            view.getTxtDiaChi().setText(model.getValueAt(selectedRow, 4).toString());
            view.getCboChucVu().setSelectedItem(model.getValueAt(selectedRow, 5).toString());
            
            // Hiển thị ngày vào làm lên JDateChooser
            String ngayVaoLamStr = model.getValueAt(selectedRow, 6).toString();
            if (!ngayVaoLamStr.isEmpty()) {
                LocalDate ngayVaoLam = LocalDate.parse(ngayVaoLamStr, dateFormatter);
                Date date = Date.from(ngayVaoLam.atStartOfDay(ZoneId.systemDefault()).toInstant());
                view.getDateChooserNgayVaoLam().setDate(date);
            } else {
                view.getDateChooserNgayVaoLam().setDate(null);
            }
            
            view.getTxtLuongCanBan().setText(model.getValueAt(selectedRow, 7).toString());
        }
    }

    private NhanVien layThongTinNhanVienTuForm() {
        try {
            String hoTen = view.getTxtHoTen().getText().trim();
            String soDienThoai = view.getTxtSoDienThoai().getText().trim();
            String diaChi = view.getTxtDiaChi().getText().trim();
            String chucVu = (String) view.getCboChucVu().getSelectedItem();
            String luongCanBanStr = view.getTxtLuongCanBan().getText().trim();

            // Validate required fields
            if (hoTen.isEmpty()) {
                showError("Họ tên không được để trống!");
                return null;
            }
            if (soDienThoai.isEmpty()) {
                showError("Số điện thoại không được để trống!");
                return null;
            }
            if (diaChi.isEmpty()) {
                showError("Địa chỉ không được để trống!");
                return null;
            }
            if (view.getDateChooserNgayVaoLam().getDate() == null) {
                showError("Ngày vào làm không được để trống!");
                return null;
            }

            // Parse dates từ JDateChooser
            LocalDate ngaySinh = null;
            if (view.getDateChooserNgaySinh().getDate() != null) {
                ngaySinh = view.getDateChooserNgaySinh().getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            }

            LocalDate ngayVaoLam = view.getDateChooserNgayVaoLam().getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();

            // Parse lương cơ bản
            BigDecimal luongCanBan = new BigDecimal("0");
            if (!luongCanBanStr.isEmpty()) {
                luongCanBan = new BigDecimal(luongCanBanStr);
                if (luongCanBan.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Lương cơ bản phải >= 0");
                }
            }

            return new NhanVien(null, hoTen, ngaySinh, soDienThoai, diaChi, chucVu, ngayVaoLam, luongCanBan);

        } catch (NumberFormatException e) {
            showError("Lương cơ bản phải là số hợp lệ");
            return null;
        } catch (Exception e) {
            showError("Lỗi dữ liệu: " + e.getMessage());
            return null;
        }
    }

    private void moQuanLyPhanTramDichVu() {
        try {
            String maNVStr = view.getTxtMaNhanVien().getText().trim();
            if (maNVStr.isEmpty()) {
                showError("Vui lòng chọn nhân viên trước!");
                return;
            }

            int maNhanVien = Integer.parseInt(maNVStr);
            String tenNhanVien = view.getTxtHoTen().getText().trim();
            
            if (tenNhanVien.isEmpty()) {
                showError("Không tìm thấy thông tin nhân viên!");
                return;
            }
            
            // Mở dialog quản lý phần trăm dịch vụ
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), 
                "Quản lý phần trăm dịch vụ - " + tenNhanVien, true);
            
            PhanTramDichVuView phanTramView = new PhanTramDichVuView();
            new Controller.PhanTramDichVuController(phanTramView, maNhanVien);
            
            dialog.setContentPane(phanTramView);
            dialog.pack();
            dialog.setLocationRelativeTo(view);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            showError("Lỗi khi mở quản lý phần trăm dịch vụ: " + e.getMessage());
        }
    }

    private void lamMoiForm() {
        view.getTxtMaNhanVien().setText("");
        view.getTxtHoTen().setText("");
        view.getDateChooserNgaySinh().setDate(null);
        view.getTxtSoDienThoai().setText("");
        view.getTxtDiaChi().setText("");
        view.getCboChucVu().setSelectedIndex(0);
        view.getDateChooserNgayVaoLam().setDate(null);
        view.getTxtLuongCanBan().setText("0.0");
        view.getTxtTimKiem().setText("");
        view.getCboChucVuFilter().setSelectedIndex(0);
        view.getTblNhanVien().clearSelection();
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(view, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(view, message, "Thông tin", JOptionPane.INFORMATION_MESSAGE);
    }
}