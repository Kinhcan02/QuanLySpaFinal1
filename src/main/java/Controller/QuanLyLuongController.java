package Controller;

import Model.LuongNhanVien;
import Model.NhanVien;
import Service.LuongNhanVienService;
import View.LichSuTinhLuongPanel;
import View.QuanLyLuongView;
import java.awt.Cursor;
import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

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
        view.getBtnXemLichSu().addActionListener(e -> xemLichSuTinhLuong());
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
            // CHỈ LẤY BẢN GHI MỚI NHẤT CỦA MỖI NHÂN VIÊN
            List<LuongNhanVien> danhSachLuong = getAllLuongMoiNhat();
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

            if (thang != null && nam != null) {
                // LẤY BẢN GHI MỚI NHẤT CHO MỖI NHÂN VIÊN TRONG THÁNG ĐÃ CHỌN
                danhSachLuong = getLuongMoiNhatTheoThang(thang, nam);
            } else {
                // LẤY TẤT CẢ BẢN GHI MỚI NHẤT CỦA MỖI NHÂN VIÊN
                danhSachLuong = getAllLuongMoiNhat();
            }

            // LỌC THEO NHÂN VIÊN NẾU CÓ CHỌN
            if (nhanVienSelected != null && !"Tất cả".equals(nhanVienSelected)) {
                String[] parts = nhanVienSelected.split(" - ");
                if (parts.length >= 2) {
                    Integer maNhanVien = Integer.parseInt(parts[parts.length - 1]);
                    danhSachLuong = danhSachLuong.stream()
                        .filter(luong -> luong.getMaNhanVien().equals(maNhanVien))
                        .collect(Collectors.toList());
                }
            }

            hienThiDuLieu(danhSachLuong);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi lọc dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // LẤY TẤT CẢ BẢN GHI LƯƠNG MỚI NHẤT CỦA MỖI NHÂN VIÊN
    private List<LuongNhanVien> getAllLuongMoiNhat() {
        List<LuongNhanVien> allRecords = service.getAllLuong();
        Map<Integer, LuongNhanVien> latestRecords = new HashMap<>();
        
        for (LuongNhanVien luong : allRecords) {
            Integer maNV = luong.getMaNhanVien();
            if (!latestRecords.containsKey(maNV)) {
                latestRecords.put(maNV, luong);
            } else {
                // So sánh ngày tính lương để lấy bản ghi mới nhất
                LuongNhanVien currentLatest = latestRecords.get(maNV);
                if (luong.getNgayTinhLuong().isAfter(currentLatest.getNgayTinhLuong())) {
                    latestRecords.put(maNV, luong);
                }
            }
        }
        
        return new ArrayList<>(latestRecords.values());
    }

    // LẤY BẢN GHI LƯƠNG MỚI NHẤT CHO MỖI NHÂN VIÊN TRONG THÁNG
    private List<LuongNhanVien> getLuongMoiNhatTheoThang(Integer thang, Integer nam) {
        List<LuongNhanVien> allRecords = service.getLuongByThangNam(thang, nam);
        Map<Integer, LuongNhanVien> latestRecords = new HashMap<>();
        
        for (LuongNhanVien luong : allRecords) {
            Integer maNV = luong.getMaNhanVien();
            if (!latestRecords.containsKey(maNV)) {
                latestRecords.put(maNV, luong);
            } else {
                // So sánh ngày tính lương để lấy bản ghi mới nhất trong tháng
                LuongNhanVien currentLatest = latestRecords.get(maNV);
                if (luong.getNgayTinhLuong().isAfter(currentLatest.getNgayTinhLuong())) {
                    latestRecords.put(maNV, luong);
                }
            }
        }
        
        return new ArrayList<>(latestRecords.values());
    }

    private void hienThiDuLieu(List<LuongNhanVien> danhSachLuong) {
        view.getTableModel().setRowCount(0);
        
        // Sắp xếp theo ngày tính lương giảm dần (mới nhất lên đầu)
        danhSachLuong.sort((l1, l2) -> l2.getNgayTinhLuong().compareTo(l1.getNgayTinhLuong()));
        
        for (LuongNhanVien luong : danhSachLuong) {
            Object[] row = {
                luong.getMaLuong(),
                luong.getMaNhanVien(),
                luong.getNhanVien() != null ? luong.getNhanVien().getHoTen() : "N/A",
                luong.getThang(),
                luong.getNam(),
                String.format("%,.0f VND", luong.getLuongCanBan()),
                String.format("%,.0f VND", luong.getTongTienDichVu()),
                String.format("%,.0f VND", luong.getTongLuong()),
                luong.getNgayTinhLuong() != null ? 
                    luong.getNgayTinhLuong().toLocalDate().toString() : "N/A",
                luong.getTrangThai()
            };
            view.getTableModel().addRow(row);
        }
        
        // Hiển thị tổng số bản ghi
        view.getLblTongSo().setText("Tổng số: " + danhSachLuong.size() + " bản ghi");
        
        // Thêm thông tin về bản ghi mới nhất
        if (!danhSachLuong.isEmpty()) {
            System.out.println("✅ Hiển thị " + danhSachLuong.size() + " bản ghi lương mới nhất");
        }
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
                "Hệ thống sẽ tính lương cho tất cả nhân viên chưa có lương trong tháng này.\n\n" +
                "Công thức tính:\n" +
                "Tổng Lương = Lương Cơ Bản + Tổng Tiền Dịch Vụ",
                "Xác nhận tính lương",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = service.tinhLuongThang(thang, nam);
                if (success) {
                    JOptionPane.showMessageDialog(view, 
                        "Tính lương tháng " + thang + "/" + nam + " thành công!\n" +
                        "Lương đã được tính cho các nhân viên có lương trong tháng.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData(); // Tự động làm mới để hiển thị bản ghi mới nhất
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
        String currentStatus = (String) view.getTableModel().getValueAt(selectedRow, 9);
        
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
                loadData(); // Tự động làm mới để hiển thị bản ghi mới nhất
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật trạng thái thất bại!", 
                                            "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xoaLuong() {
        int selectedRow = view.getTblLuong().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một bản ghi lương", 
                                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
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
                // Hiển thị loading
                view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                boolean success = service.xoaLuong(maLuong);
                
                // Reset cursor
                view.setCursor(Cursor.getDefaultCursor());
                
                if (success) {
                    JOptionPane.showMessageDialog(view, 
                        "Xóa bản ghi lương thành công!\n" +
                        "Đã xóa lương của " + tenNhanVien + " - Tháng " + thang + "/" + nam,
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData(); // Tự động làm mới để hiển thị bản ghi mới nhất
                } else {
                    JOptionPane.showMessageDialog(view, 
                        "Xóa bản ghi lương thất bại!\n" +
                        "Có thể bản ghi đã bị xóa trước đó hoặc có lỗi database.\n" +
                        "Vui lòng thử lại hoặc liên hệ quản trị viên.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            view.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(view, 
                "Lỗi nghiêm trọng khi xóa lương:\n" + e.getMessage() +
                "\n\nVui lòng kiểm tra kết nối database hoặc liên hệ quản trị viên.",
                "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void xemLichSuTinhLuong() {
        try {
            // Tạo frame mới để chứa panel
            JFrame lichSuFrame = new JFrame("Lịch Sử Tính Lương");
            LichSuTinhLuongPanel lichSuPanel = new LichSuTinhLuongPanel(service);
            
            lichSuFrame.setContentPane(lichSuPanel);
            lichSuFrame.setSize(1100, 600);
            lichSuFrame.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(view));
            lichSuFrame.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi mở lịch sử tính lương: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}