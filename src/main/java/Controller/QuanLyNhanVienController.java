package Controller;

import Model.NhanVien;
import Service.NhanVienService;
import View.QuanLyNhanVienView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
                nv.getHeSoLuong(),
                nv.getThamNien()
            };
            model.addRow(row);
        }
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
            view.getTxtNgaySinh().setText(model.getValueAt(selectedRow, 2).toString());
            view.getTxtSoDienThoai().setText(model.getValueAt(selectedRow, 3).toString());
            view.getTxtDiaChi().setText(model.getValueAt(selectedRow, 4).toString());
            view.getCboChucVu().setSelectedItem(model.getValueAt(selectedRow, 5).toString());
            view.getTxtNgayVaoLam().setText(model.getValueAt(selectedRow, 6).toString());
            view.getTxtHeSoLuong().setText(model.getValueAt(selectedRow, 7).toString());
        }
    }

    private NhanVien layThongTinNhanVienTuForm() {
        try {
            String hoTen = view.getTxtHoTen().getText().trim();
            String ngaySinhStr = view.getTxtNgaySinh().getText().trim();
            String soDienThoai = view.getTxtSoDienThoai().getText().trim();
            String diaChi = view.getTxtDiaChi().getText().trim();
            String chucVu = (String) view.getCboChucVu().getSelectedItem();
            String ngayVaoLamStr = view.getTxtNgayVaoLam().getText().trim();
            String heSoLuongStr = view.getTxtHeSoLuong().getText().trim();

            // Parse dates
            LocalDate ngaySinh = null;
            if (!ngaySinhStr.isEmpty()) {
                ngaySinh = LocalDate.parse(ngaySinhStr, dateFormatter);
            }

            LocalDate ngayVaoLam = LocalDate.parse(ngayVaoLamStr, dateFormatter);

            // Parse hệ số lương
            BigDecimal heSoLuong = new BigDecimal("1.0");
            if (!heSoLuongStr.isEmpty()) {
                heSoLuong = new BigDecimal(heSoLuongStr);
                if (heSoLuong.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Hệ số lương phải >= 0");
                }
            }

            return new NhanVien(null, hoTen, ngaySinh, soDienThoai, diaChi, chucVu, ngayVaoLam, heSoLuong);

        } catch (DateTimeParseException e) {
            showError("Định dạng ngày không hợp lệ. Vui lòng sử dụng định dạng yyyy-MM-dd");
            return null;
        } catch (NumberFormatException e) {
            showError("Hệ số lương phải là số hợp lệ");
            return null;
        } catch (Exception e) {
            showError("Lỗi dữ liệu: " + e.getMessage());
            return null;
        }
    }

    private void lamMoiForm() {
        view.getTxtMaNhanVien().setText("");
        view.getTxtHoTen().setText("");
        view.getTxtNgaySinh().setText("");
        view.getTxtSoDienThoai().setText("");
        view.getTxtDiaChi().setText("");
        view.getCboChucVu().setSelectedIndex(0);
        view.getTxtNgayVaoLam().setText("");
        view.getTxtHeSoLuong().setText("1.0");
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