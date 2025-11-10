package Controller;

import View.TongQuanThuChiPanel;
import Service.ThuNhapService;
import Service.ChiTieuService;
import java.math.BigDecimal;
import javax.swing.JOptionPane;

public class TongQuanThuChiController {
    private TongQuanThuChiPanel view;
    private ThuNhapService thuNhapService;
    private ChiTieuService chiTieuService;
    private QuanLyChiTieuController chiTieuController;

    public TongQuanThuChiController(TongQuanThuChiPanel view, QuanLyChiTieuController chiTieuController) {
        this.view = view;
        this.thuNhapService = new ThuNhapService();
        this.chiTieuService = new ChiTieuService();
        this.chiTieuController = chiTieuController;
        initController();
        thongKeTongQuan(); // Tự động thống kê khi khởi động
    }

    private void initController() {
        view.getBtnThongKeTongQuan().addActionListener(e -> thongKeTongQuan());
    }

    private void thongKeTongQuan() {
        try {
            int thang = (int) view.getSpnThangTongQuan().getValue();
            int nam = (int) view.getSpnNamTongQuan().getValue();

            // Lấy dữ liệu thu nhập
            BigDecimal tongThu = thuNhapService.getTongThuNhapByNam(nam);
            
            // Lấy tổng chi từ QuanLyChiTieuController
            BigDecimal tongChi = chiTieuController.getTongChiTheoNam(nam);
            
            BigDecimal loiNhuan = tongThu.subtract(tongChi);

            // Cập nhật giao diện
            view.getLblTongThu().setText(String.format("%,.0f VND", tongThu));
            view.getLblTongChi().setText(String.format("%,.0f VND", tongChi));
            view.getLblLoiNhuan().setText(String.format("%,.0f VND", loiNhuan));

            // Tạo báo cáo chi tiết
            String baoCao = taoBaoCaoTongQuan(thang, nam, tongThu, tongChi, loiNhuan);
            view.getTxtBaoCao().setText(baoCao);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thống kê: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String taoBaoCaoTongQuan(int thang, int nam, BigDecimal tongThu, BigDecimal tongChi, BigDecimal loiNhuan) {
        StringBuilder baoCao = new StringBuilder();
        
        baoCao.append("BÁO CÁO TỔNG QUAN THU CHI NĂM ").append(nam).append("\n\n");
        baoCao.append("=== TỔNG QUAN ===\n");
        baoCao.append(String.format("Tổng thu nhập: %,d VND\n", tongThu.intValue()));
        baoCao.append(String.format("Tổng chi tiêu: %,d VND\n", tongChi.intValue()));
        baoCao.append(String.format("Lợi nhuận: %,d VND\n\n", loiNhuan.intValue()));

        // Tính tỷ lệ
        if (tongThu.compareTo(BigDecimal.ZERO) > 0) {
            double tyLeChi = tongChi.multiply(BigDecimal.valueOf(100))
                    .divide(tongThu, 1, BigDecimal.ROUND_HALF_UP).doubleValue();
            double tyLeLoiNhuan = loiNhuan.multiply(BigDecimal.valueOf(100))
                    .divide(tongThu, 1, BigDecimal.ROUND_HALF_UP).doubleValue();
            
            baoCao.append("=== TỶ LỆ ===\n");
            baoCao.append(String.format("Tỷ lệ chi/doanh thu: %.1f%%\n", tyLeChi));
            baoCao.append(String.format("Tỷ lệ lợi nhuận: %.1f%%\n\n", tyLeLoiNhuan));
        }

        // Phân tích chi tiết theo tháng
        baoCao.append("=== PHÂN TÍCH THEO THÁNG ===\n");
        for (int i = 1; i <= 12; i++) {
            try {
                // Lấy thu nhập và chi tiêu theo tháng
                BigDecimal thuThang = thuNhapService.getTongThuNhapByThang(i, nam);
                BigDecimal chiThang = chiTieuController.getTongChiTheoThangNam(i, nam);
                
                if (thuThang.compareTo(BigDecimal.ZERO) > 0 || chiThang.compareTo(BigDecimal.ZERO) > 0) {
                    baoCao.append(String.format("Tháng %d: Thu %,d VND | Chi %,d VND\n", 
                        i, thuThang.intValue(), chiThang.intValue()));
                }
            } catch (Exception e) {
                // Bỏ qua tháng không có dữ liệu
            }
        }

        return baoCao.toString();
    }
}