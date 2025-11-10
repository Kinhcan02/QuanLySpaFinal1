package Controller;

import View.QuanLyThuChiView;
import View.QuanLyThuNhapPanel;
import View.QuanLyChiTieuPanel;
import View.TongQuanThuChiPanel;
import Service.ThuNhapService;
import Service.ChiTieuService;
import Model.ThuNhap;
import Model.ChiTieu;
import java.awt.Component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class QuanLyThuChiController {
    private QuanLyThuChiView mainView;
    private ThuNhapService thuNhapService;
    private ChiTieuService chiTieuService;
    
    // Các controller con
    private QuanLyThuNhapController thuNhapController;
    private QuanLyChiTieuController chiTieuController;
    private TongQuanThuChiController tongQuanController;

    public QuanLyThuChiController(QuanLyThuChiView mainView) {
        this.mainView = mainView;
        this.thuNhapService = new ThuNhapService();
        this.chiTieuService = new ChiTieuService();
        
        initControllers();
        initEventHandlers();
        refreshAllData();
    }

    private void initControllers() {
        // Khởi tạo các controller cho từng panel
        thuNhapController = new QuanLyThuNhapController(mainView.getThuNhapPanel());
        chiTieuController = new QuanLyChiTieuController(mainView.getChiTieuPanel());
        tongQuanController = new TongQuanThuChiController(mainView.getTongQuanPanel(),chiTieuController );
    }

    private void initEventHandlers() {
        // Xử lý sự kiện khi chuyển tab - sử dụng JTabbedPane trực tiếp từ view
        JTabbedPane tabbedPane = getTabbedPaneFromView();
        if (tabbedPane != null) {
            tabbedPane.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int selectedIndex = tabbedPane.getSelectedIndex();
                    onTabChanged(selectedIndex);
                }
            });
        }
    }

    private JTabbedPane getTabbedPaneFromView() {
        // Tìm JTabbedPane trong view bằng reflection hoặc kiểm tra component
        try {
            // Phương thức này giả sử rằng tabbedPane là component duy nhất trong view
            Component[] components = mainView.getComponents();
            for (Component comp : components) {
                if (comp instanceof JTabbedPane) {
                    return (JTabbedPane) comp;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void onTabChanged(int tabIndex) {
        switch (tabIndex) {
            case 0: // Tab Thu nhập
                refreshThuNhapData();
                break;
            case 1: // Tab Chi tiêu
                refreshChiTieuData();
                break;
            case 2: // Tab Tổng quan
                refreshTongQuanData();
                break;
        }
    }

    // Phương thức refresh dữ liệu thu nhập
    private void refreshThuNhapData() {
        try {
            // Gọi service trực tiếp để load dữ liệu
            List<ThuNhap> thuNhapList = thuNhapService.getAllThuNhap();
            updateThuNhapTable(thuNhapList);
            
            // Cập nhật thống kê
            int currentMonth = LocalDate.now().getMonthValue();
            int currentYear = LocalDate.now().getYear();
            updateThuNhapThongKe(currentMonth, currentYear);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainView, "Lỗi khi tải dữ liệu thu nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức refresh dữ liệu chi tiêu
    private void refreshChiTieuData() {
        try {
            // Gọi service trực tiếp để load dữ liệu
            int currentMonth = LocalDate.now().getMonthValue();
            int currentYear = LocalDate.now().getYear();
            List<ChiTieu> chiTieuList = chiTieuService.getChiTieuByThangNam(currentMonth, currentYear);
            updateChiTieuTable(chiTieuList);
            
            // Cập nhật thống kê
            updateChiTieuThongKe(currentMonth, currentYear);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainView, "Lỗi khi tải dữ liệu chi tiêu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức refresh dữ liệu tổng quan
    private void refreshTongQuanData() {
        try {
            // Kích hoạt thống kê tổng quan bằng cách trigger button
            JButton btnThongKe = mainView.getTongQuanPanel().getBtnThongKeTongQuan();
            if (btnThongKe != null) {
                btnThongKe.doClick();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainView, "Lỗi khi tải dữ liệu tổng quan: " + e.getMessage());
            e.printStackTrace();
        }
    }
 public BigDecimal getTongChiTheoNam(int nam) {
        try {
            return chiTieuService.tinhTongChiTieuNam(nam);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    // Thêm phương thức để lấy tổng chi theo tháng, năm
    public BigDecimal getTongChiTheoThangNam(int thang, int nam) {
        try {
            return chiTieuService.tinhTongChiTieuThang(thang, nam);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    // Phương thức refresh toàn bộ dữ liệu
    public void refreshAllData() {
        refreshThuNhapData();
        refreshChiTieuData();
        refreshTongQuanData();
    }

    // Phương thức tính toán tổng quan cho một tháng/năm cụ thể
    public void thongKeTheoThangNam(int thang, int nam) {
        try {
            // Cập nhật thống kê thu nhập
            updateThuNhapThongKe(thang, nam);
            
            // Cập nhật thống kê chi tiêu
            updateChiTieuThongKe(thang, nam);
            
            // Cập nhật spinners trong tổng quan
            mainView.getTongQuanPanel().getSpnThangTongQuan().setValue(thang);
            mainView.getTongQuanPanel().getSpnNamTongQuan().setValue(nam);
            
            // Trigger thống kê tổng quan
            refreshTongQuanData();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainView, "Lỗi khi thống kê: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Cập nhật bảng thu nhập - ĐÃ SỬA: Bỏ cột "Thu khác"
    private void updateThuNhapTable(List<ThuNhap> thuNhapList) {
        try {
            DefaultTableModel model = mainView.getThuNhapPanel().getModelThuNhap();
            model.setRowCount(0);

            for (ThuNhap tn : thuNhapList) {
                model.addRow(new Object[]{
                    tn.getMaThu(),
                    tn.getThang(),
                    tn.getNam(),
                    String.format("%,.0f VND", tn.getTongDoanhThuDichVu()),
                    String.format("%,.0f VND", tn.getTongLuongNhanVien()),
                    String.format("%,.0f VND", tn.getThuNhapThuc()),
                    tn.getNgayTinhThuNhap(),
                    tn.getGhiChu()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật thống kê thu nhập - ĐÃ SỬA: Bỏ "Thu khác"
    private void updateThuNhapThongKe(int thang, int nam) {
        try {
            List<ThuNhap> list = thuNhapService.getThuNhapByThangNam(thang, nam);
            BigDecimal tongDoanhThu = BigDecimal.ZERO;
            BigDecimal tongLuong = BigDecimal.ZERO;
            BigDecimal tongThuNhapThuc = BigDecimal.ZERO;

            for (ThuNhap tn : list) {
                tongDoanhThu = tongDoanhThu.add(tn.getTongDoanhThuDichVu());
                tongLuong = tongLuong.add(tn.getTongLuongNhanVien());
                tongThuNhapThuc = tongThuNhapThuc.add(tn.getThuNhapThuc());
            }

            mainView.getThuNhapPanel().getLblTongDoanhThu().setText(String.format("%,.0f VND", tongDoanhThu));
            mainView.getThuNhapPanel().getLblTongLuong().setText(String.format("%,.0f VND", tongLuong));
            mainView.getThuNhapPanel().getLblThuNhapThuc().setText(String.format("%,.0f VND", tongThuNhapThuc));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật bảng chi tiêu
    private void updateChiTieuTable(List<ChiTieu> chiTieuList) {
        try {
            DefaultTableModel model = mainView.getChiTieuPanel().getModelChiTieu();
            model.setRowCount(0);

            for (ChiTieu ct : chiTieuList) {
                model.addRow(new Object[]{
                    ct.getMaChi(),
                    ct.getNgayChi(),
                    String.format("%,.0f VND", ct.getSoTien()),
                    ct.getMucDich(),
                    ct.getLoaiChi(),
                    ct.getNgayTao()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật thống kê chi tiêu
    private void updateChiTieuThongKe(int thang, int nam) {
        try {
            // Tính từng loại chi phí theo danh sách mới
            BigDecimal chiNguyenLieu = chiTieuService.tinhTongNhapNguyenLieuThang(thang, nam);
            BigDecimal chiDien = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Điện");
            BigDecimal chiNuoc = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Nước");
            BigDecimal chiVeSinh = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Vệ sinh");
            BigDecimal chiWifi = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Wifi");
            BigDecimal chiKhac = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Khác");

            // Tổng chi = tổng tất cả chi phí
            BigDecimal tongChi = chiNguyenLieu
                .add(chiDien)
                .add(chiNuoc)
                .add(chiVeSinh)
                .add(chiWifi)
                .add(chiKhac);

            // Cập nhật giao diện với các label mới
            mainView.getChiTieuPanel().getLblTongChi().setText(String.format("%,.0f VND", tongChi));
            mainView.getChiTieuPanel().getLblChiNguyenLieu().setText(String.format("%,.0f VND", chiNguyenLieu));
            mainView.getChiTieuPanel().getLblChiDien().setText(String.format("%,.0f VND", chiDien));
            mainView.getChiTieuPanel().getLblChiNuoc().setText(String.format("%,.0f VND", chiNuoc));
            mainView.getChiTieuPanel().getLblChiVeSinh().setText(String.format("%,.0f VND", chiVeSinh));
            mainView.getChiTieuPanel().getLblChiWifi().setText(String.format("%,.0f VND", chiWifi));
            mainView.getChiTieuPanel().getLblChiKhac().setText(String.format("%,.0f VND", chiKhac));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Phương thức lấy báo cáo tổng quan - ĐÃ SỬA: Bỏ "Thu khác"
    public String getBaoCaoTongQuan(int thang, int nam) {
        try {
            StringBuilder baoCao = new StringBuilder();
            
            // Lấy dữ liệu thu nhập
            List<ThuNhap> thuNhapList = thuNhapService.getThuNhapByThangNam(thang, nam);
            BigDecimal tongThu = BigDecimal.ZERO;
            BigDecimal tongLuong = BigDecimal.ZERO;
            BigDecimal thuNhapThuc = BigDecimal.ZERO;
            
            for (ThuNhap tn : thuNhapList) {
                tongThu = tongThu.add(tn.getTongDoanhThuDichVu());
                tongLuong = tongLuong.add(tn.getTongLuongNhanVien());
                thuNhapThuc = thuNhapThuc.add(tn.getThuNhapThuc());
            }
            
            // Lấy dữ liệu chi tiêu
            BigDecimal chiNguyenLieu = chiTieuService.tinhTongNhapNguyenLieuThang(thang, nam);
            BigDecimal chiDien = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Điện");
            BigDecimal chiNuoc = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Nước");
            BigDecimal chiVeSinh = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Vệ sinh");
            BigDecimal chiWifi = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Wifi");
            BigDecimal chiKhac = chiTieuService.tinhTongChiTieuTheoLoai(thang, nam, "Khác");
            
            // Tổng chi = tổng tất cả chi phí
            BigDecimal tongChi = chiNguyenLieu
                .add(chiDien)
                .add(chiNuoc)
                .add(chiVeSinh)
                .add(chiWifi)
                .add(chiKhac);
            
            // Tính lợi nhuận
            BigDecimal loiNhuan = thuNhapThuc.subtract(tongChi);
            
            // Tạo báo cáo
            baoCao.append("BÁO CÁO TỔNG QUAN THÁNG ").append(thang).append("/").append(nam).append("\n\n");
            
            baoCao.append("=== THU NHẬP ===\n");
            baoCao.append(String.format("Tổng doanh thu: %,d VND\n", tongThu.intValue()));
            baoCao.append(String.format("Tổng lương: %,d VND\n", tongLuong.intValue()));
            baoCao.append(String.format("Thu nhập thực: %,d VND\n\n", thuNhapThuc.intValue()));
            
            baoCao.append("=== CHI TIÊU ===\n");
            baoCao.append(String.format("Tổng chi: %,d VND\n", tongChi.intValue()));
            baoCao.append(String.format("- Nguyên liệu: %,d VND\n", chiNguyenLieu.intValue()));
            baoCao.append(String.format("- Điện: %,d VND\n", chiDien.intValue()));
            baoCao.append(String.format("- Nước: %,d VND\n", chiNuoc.intValue()));
            baoCao.append(String.format("- Vệ sinh: %,d VND\n", chiVeSinh.intValue()));
            baoCao.append(String.format("- Wifi: %,d VND\n", chiWifi.intValue()));
            baoCao.append(String.format("- Khác: %,d VND\n\n", chiKhac.intValue()));
            
            baoCao.append("=== KẾT QUẢ ===\n");
            baoCao.append(String.format("Lợi nhuận: %,d VND\n\n", loiNhuan.intValue()));
            
            // Phân tích
            if (thuNhapThuc.compareTo(BigDecimal.ZERO) > 0) {
                double tyLeChi = tongChi.multiply(BigDecimal.valueOf(100))
                        .divide(thuNhapThuc, 1, BigDecimal.ROUND_HALF_UP).doubleValue();
                double tyLeLoiNhuan = loiNhuan.multiply(BigDecimal.valueOf(100))
                        .divide(thuNhapThuc, 1, BigDecimal.ROUND_HALF_UP).doubleValue();
                
                baoCao.append("=== PHÂN TÍCH ===\n");
                baoCao.append(String.format("Tỷ lệ chi/thu nhập: %.1f%%\n", tyLeChi));
                baoCao.append(String.format("Tỷ lệ lợi nhuận: %.1f%%\n", tyLeLoiNhuan));
                
                // Đánh giá
                baoCao.append("\n=== ĐÁNH GIÁ ===\n");
                if (loiNhuan.compareTo(BigDecimal.ZERO) > 0) {
                    if (tyLeLoiNhuan > 20) {
                        baoCao.append("✓ Kinh doanh rất hiệu quả\n");
                    } else if (tyLeLoiNhuan > 10) {
                        baoCao.append("✓ Kinh doanh hiệu quả\n");
                    } else {
                        baoCao.append("○ Kinh doanh có lãi\n");
                    }
                } else if (loiNhuan.compareTo(BigDecimal.ZERO) < 0) {
                    baoCao.append("✗ Kinh doanh bị lỗ\n");
                } else {
                    baoCao.append("○ Kinh doanh hòa vốn\n");
                }
                
                if (tyLeChi > 80) {
                    baoCao.append("⚠ Chi phí quá cao, cần kiểm soát chặt chẽ\n");
                } else if (tyLeChi > 60) {
                    baoCao.append("⚠ Chi phí tương đối cao\n");
                }
            }
            
            return baoCao.toString();
            
        } catch (Exception e) {
            return "Lỗi khi tạo báo cáo: " + e.getMessage();
        }
    }

    // Getter methods để truy cập các controller từ bên ngoài
    public QuanLyThuNhapController getThuNhapController() {
        return thuNhapController;
    }

    public QuanLyChiTieuController getChiTieuController() {
        return chiTieuController;
    }

    public TongQuanThuChiController getTongQuanController() {
        return tongQuanController;
    }

    public QuanLyThuChiView getMainView() {
        return mainView;
    }
}