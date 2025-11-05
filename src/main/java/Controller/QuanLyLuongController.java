package Controller;

import Model.LuongNhanVien;
import Model.NhanVien;
import Service.LuongNhanVienService;
import View.QuanLyLuongView;
import javax.swing.*;
import java.util.List;

public class QuanLyLuongController {
    private QuanLyLuongView view;
    private LuongNhanVienService service;

    public QuanLyLuongController(QuanLyLuongView view) {
        this.view = view;
        this.service = new LuongNhanVienService();
        initController();
        loadData();
        loadNhanVienComboBox();
    }

    private void initController() {
        view.getBtnTinhLuong().addActionListener(e -> tinhLuong());
        view.getBtnCapNhat().addActionListener(e -> capNhatTrangThai());
        view.getBtnXoa().addActionListener(e -> xoaLuong());
        view.getBtnLamMoi().addActionListener(e -> loadData());
        view.getBtnDong().addActionListener(e -> dongView());
        
        view.getCboThang().addActionListener(e -> filterData());
        view.getCboNam().addActionListener(e -> filterData());
        view.getCboNhanVien().addActionListener(e -> filterData());
    }

    private void dongView() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        if (topFrame != null) {
            topFrame.dispose();
        }
    }

    private void loadData() {
        try {
            List<LuongNhanVien> danhSachLuong = service.getAllLuong();
            hienThiDuLieu(danhSachLuong);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNhanVienComboBox() {
        try {
            List<NhanVien> danhSachNhanVien = service.getAllNhanVien();
            view.getCboNhanVien().removeAllItems();
            view.getCboNhanVien().addItem("Tất cả");
            
            for (NhanVien nv : danhSachNhanVien) {
                view.getCboNhanVien().addItem(nv.getHoTen() + " - " + nv.getMaNhanVien());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterData() {
        try {
            Integer thang = (Integer) view.getCboThang().getSelectedItem();
            Integer nam = (Integer) view.getCboNam().getSelectedItem();
            String nhanVienSelected = (String) view.getCboNhanVien().getSelectedItem();

            List<LuongNhanVien> danhSachLuong;

            if ("Tất cả".equals(nhanVienSelected)) {
                danhSachLuong = service.getLuongByThangNam(thang, nam);
            } else {
                // Extract maNhanVien from combobox text (format: "HoTen - MaNhanVien")
                String[] parts = nhanVienSelected.split(" - ");
                if (parts.length >= 2) {
                    Integer maNhanVien = Integer.parseInt(parts[parts.length - 1]);
                    danhSachLuong = service.getLuongByNhanVienThangNam(maNhanVien, thang, nam);
                } else {
                    danhSachLuong = service.getLuongByThangNam(thang, nam);
                }
            }

            hienThiDuLieu(danhSachLuong);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi lọc dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hienThiDuLieu(List<LuongNhanVien> danhSachLuong) {
        view.getTableModel().setRowCount(0);
        
        for (LuongNhanVien luong : danhSachLuong) {
            Object[] row = {
                luong.getMaLuong(),
                luong.getMaNhanVien(),
                luong.getNhanVien() != null ? luong.getNhanVien().getHoTen() : "N/A",
                luong.getThang(),
                luong.getNam(),
                String.format("%,.0f VND", luong.getTongLuong()),
                luong.getNgayTinhLuong() != null ? 
                    luong.getNgayTinhLuong().toLocalDate().toString() : "N/A",
                luong.getTrangThai()
            };
            view.getTableModel().addRow(row);
        }
        
        // Hiển thị tổng số bản ghi
        JOptionPane.showMessageDialog(view, "Tìm thấy " + danhSachLuong.size() + " bản ghi lương", 
                                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void tinhLuong() {
        try {
            Integer thang = (Integer) view.getCboThang().getSelectedItem();
            Integer nam = (Integer) view.getCboNam().getSelectedItem();
            
            if (thang == null || nam == null) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn tháng và năm", 
                                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(
                view,
                "Bạn có chắc chắn muốn tính lương tháng " + thang + "/" + nam + "?\n" +
                "Hệ thống sẽ tính lương cho tất cả nhân viên chưa có lương trong tháng này.",
                "Xác nhận tính lương",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = service.tinhLuongThang(thang, nam);
                if (success) {
                    JOptionPane.showMessageDialog(view, 
                        "Tính lương tháng " + thang + "/" + nam + " thành công!\n" +
                        "Lương đã được tính cho các nhân viên có doanh thu trong tháng.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(view, 
                        "Không có nhân viên nào cần tính lương trong tháng " + thang + "/" + nam + 
                        "\nhoặc tất cả nhân viên đã được tính lương.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi tính lương: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatTrangThai() {
        int selectedRow = view.getTblLuong().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một bản ghi lương", 
                                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer maLuong = (Integer) view.getTableModel().getValueAt(selectedRow, 0);
        String tenNhanVien = (String) view.getTableModel().getValueAt(selectedRow, 2);
        String currentStatus = (String) view.getTableModel().getValueAt(selectedRow, 7);
        
        String[] trangThaiOptions = {"Chưa thanh toán", "Đã thanh toán", "Đã hủy"};
        String newStatus = (String) JOptionPane.showInputDialog(
            view,
            "Chọn trạng thái mới cho lương của " + tenNhanVien + ":",
            "Cập nhật trạng thái",
            JOptionPane.QUESTION_MESSAGE,
            null,
            trangThaiOptions,
            currentStatus
        );
        
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            boolean success = service.capNhatTrangThai(maLuong, newStatus);
            if (success) {
                JOptionPane.showMessageDialog(view, "Cập nhật trạng thái thành công!", 
                                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật trạng thái thất bại!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xoaLuong() {
        int selectedRow = view.getTblLuong().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một bản ghi lương để xóa", 
                                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer maLuong = (Integer) view.getTableModel().getValueAt(selectedRow, 0);
        String tenNhanVien = (String) view.getTableModel().getValueAt(selectedRow, 2);
        Integer thang = (Integer) view.getTableModel().getValueAt(selectedRow, 3);
        Integer nam = (Integer) view.getTableModel().getValueAt(selectedRow, 4);
        
        int confirm = JOptionPane.showConfirmDialog(
            view,
            "Bạn có chắc chắn muốn xóa bản ghi lương này?\n\n" +
            "Thông tin lương cần xóa:\n" +
            "- Nhân viên: " + tenNhanVien + "\n" +
            "- Thời gian: Tháng " + thang + "/" + nam + "\n" +
            "- Mã lương: " + maLuong + "\n\n" +
            "Hành động này không thể hoàn tác!",
            "XÁC NHẬN XÓA LƯƠNG",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = service.xoaLuong(maLuong);
            if (success) {
                JOptionPane.showMessageDialog(view, 
                    "Xóa bản ghi lương thành công!\n" +
                    "Đã xóa lương của " + tenNhanVien + " - Tháng " + thang + "/" + nam,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(view, 
                    "Xóa bản ghi lương thất bại!\n" +
                    "Vui lòng thử lại hoặc liên hệ quản trị viên.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}