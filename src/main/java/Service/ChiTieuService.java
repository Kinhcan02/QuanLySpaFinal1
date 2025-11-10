package Service;

import Data.DataConnection;
import Repository.IChiTieuRepository;
import Repository.ChiTieuRepository;
import Model.ChiTieu;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

public class ChiTieuService {

    private IChiTieuRepository chiTieuRepository;

    public ChiTieuService() {
        this.chiTieuRepository = new ChiTieuRepository();
    }

    public ChiTieuService(IChiTieuRepository chiTieuRepository) {
        this.chiTieuRepository = chiTieuRepository;
    }

    public List<ChiTieu> getAllChiTieu() {
        return chiTieuRepository.getAllChiTieu();
    }

    public List<ChiTieu> getChiTieuByThangNam(int thang, int nam) {
        validateThangNam(thang, nam);
        return chiTieuRepository.getChiTieuByThangNam(thang, nam);
    }

    public List<ChiTieu> getChiTieuByNam(int nam) {
        validateNam(nam);
        return chiTieuRepository.getChiTieuByNam(nam);
    }

    public ChiTieu getChiTieuById(int maChi) {
        if (maChi <= 0) {
            throw new IllegalArgumentException("Mã chi tiêu không hợp lệ");
        }
        return chiTieuRepository.getChiTieuById(maChi);
    }

    public boolean themChiTieu(ChiTieu chiTieu) {
        if (chiTieu == null || !chiTieu.isValid()) {
            return false;
        }
        return chiTieuRepository.addChiTieu(chiTieu);
    }

    public boolean suaChiTieu(ChiTieu chiTieu) {
        if (chiTieu == null || !chiTieu.isValid() || chiTieu.getMaChi() == null) {
            return false;
        }
        return chiTieuRepository.updateChiTieu(chiTieu);
    }

    public boolean xoaChiTieu(int maChi) {
        if (maChi <= 0) {
            return false;
        }
        return chiTieuRepository.deleteChiTieu(maChi);
    }

    public BigDecimal tinhTongChiTieuThang(int thang, int nam) {
        validateThangNam(thang, nam);
        return chiTieuRepository.getTongChiTieuByThangNam(thang, nam);
    }

    public BigDecimal tinhTongChiTieuTheoLoai(int thang, int nam, String loaiChi) {
        validateThangNam(thang, nam);
        if (loaiChi == null || loaiChi.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại chi không được để trống");
        }
        return chiTieuRepository.getTongChiTieuByLoai(thang, nam, loaiChi);
    }

    public BigDecimal tinhTongNhapNguyenLieuThang(int thang, int nam) {
        validateThangNam(thang, nam);
        return getTongNhapNguyenLieuByThangNam(thang, nam);
    }

    public BigDecimal tinhTongChiTieuNam(int nam) {
        validateNam(nam);
        List<ChiTieu> chiTieuNam = chiTieuRepository.getChiTieuByNam(nam);
        return chiTieuNam.stream()
                .map(ChiTieu::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Phương thức mới: Lấy tất cả chi phí bao gồm cả nguyên liệu
    public List<ChiTieu> getAllChiTieuWithNguyenLieu(int thang, int nam) {
        validateThangNam(thang, nam);
        return chiTieuRepository.getAllChiTieuWithNguyenLieu(thang, nam);
    }

    // Phương thức mới: Tính tổng tất cả chi phí (bao gồm cả nguyên liệu)
    public BigDecimal tinhTongTatCaChiPhi(int thang, int nam) {
        validateThangNam(thang, nam);
        
        // Tổng chi tiêu thông thường
        BigDecimal tongChiTieu = chiTieuRepository.getTongChiTieuByThangNam(thang, nam);
        
        // Tổng nguyên liệu
        BigDecimal tongNguyenLieu = getTongNhapNguyenLieuByThangNam(thang, nam);
        
        return tongChiTieu.add(tongNguyenLieu);
    }

    // Phương thức để tự động thêm chi phí nguyên liệu vào bảng ChiTieu
    public boolean dongBoChiPhiNguyenLieu(int thang, int nam) {
        try {
            validateThangNam(thang, nam);
            
            // Kiểm tra xem đã có chi phí nguyên liệu cho tháng này chưa
            List<ChiTieu> chiTieuThang = chiTieuRepository.getChiTieuByThangNam(thang, nam);
            boolean daCoNguyenLieu = chiTieuThang.stream()
                    .anyMatch(ct -> "Nguyên liệu".equals(ct.getLoaiChi()));
            
            if (daCoNguyenLieu) {
                return true; // Đã có rồi, không cần thêm
            }
            
            // Lấy tổng chi phí nguyên liệu
            BigDecimal tongNguyenLieu = getTongNhapNguyenLieuByThangNam(thang, nam);
            
            if (tongNguyenLieu.compareTo(BigDecimal.ZERO) > 0) {
                // Tạo chi phí nguyên liệu mới
                ChiTieu nguyenLieuChi = new ChiTieu();
                nguyenLieuChi.setThang(thang);
                nguyenLieuChi.setNam(nam);
                nguyenLieuChi.setNgayChi(LocalDate.of(nam, thang, 1));
                nguyenLieuChi.setMucDich("Nhập nguyên liệu tháng " + thang + "/" + nam);
                nguyenLieuChi.setSoTien(tongNguyenLieu);
                nguyenLieuChi.setLoaiChi("Nguyên liệu");
                nguyenLieuChi.setNgayTao(LocalDate.now());
                
                return chiTieuRepository.addChiTieu(nguyenLieuChi);
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private BigDecimal getTongNhapNguyenLieuByThangNam(int thang, int nam) {
        // Sử dụng SQL query từ repository
        String sql = "SELECT SUM(SoLuong * DonGia) as TongNhap FROM NhapNguyenLieu WHERE MONTH(NgayNhap) = ? AND YEAR(NgayNhap) = ?";
        BigDecimal result = BigDecimal.ZERO;
        
        try (java.sql.Connection conn = DataConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal(1);
                if (result == null) {
                    result = BigDecimal.ZERO;
                }
            }
            rs.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void validateThangNam(int thang, int nam) {
        if (thang < 1 || thang > 12) {
            throw new IllegalArgumentException("Tháng phải từ 1 đến 12");
        }
        validateNam(nam);
    }

    private void validateNam(int nam) {
        int currentYear = LocalDate.now().getYear();
        if (nam < 2020 || nam > currentYear + 5) {
            throw new IllegalArgumentException("Năm phải từ 2020 đến " + (currentYear + 5));
        }
    }

    public String layBaoCaoChiTieu(int thang, int nam) {
        // Đồng bộ chi phí nguyên liệu trước
        dongBoChiPhiNguyenLieu(thang, nam);
        
        BigDecimal chiNguyenLieu = tinhTongChiTieuTheoLoai(thang, nam, "Nguyên liệu");
        BigDecimal chiDien = tinhTongChiTieuTheoLoai(thang, nam, "Điện");
        BigDecimal chiNuoc = tinhTongChiTieuTheoLoai(thang, nam, "Nước");
        BigDecimal chiVeSinh = tinhTongChiTieuTheoLoai(thang, nam, "Vệ sinh");
        BigDecimal chiWifi = tinhTongChiTieuTheoLoai(thang, nam, "Wifi");
        BigDecimal chiKhac = tinhTongChiTieuTheoLoai(thang, nam, "Khác");

        // Tổng chi = tổng tất cả chi phí
        BigDecimal tongChi = chiNguyenLieu
            .add(chiDien)
            .add(chiNuoc)
            .add(chiVeSinh)
            .add(chiWifi)
            .add(chiKhac);

        return String.format(
                "BÁO CÁO CHI TIÊU THÁNG %d/%d\n"
                + "Tổng chi: %,d VND\n"
                + "- Chi nguyên liệu: %,d VND\n"
                + "- Chi điện: %,d VND\n"
                + "- Chi nước: %,d VND\n"
                + "- Chi vệ sinh: %,d VND\n"
                + "- Chi wifi: %,d VND\n"
                + "- Chi khác: %,d VND",
                thang, nam,
                tongChi.intValue(),
                chiNguyenLieu.intValue(),
                chiDien.intValue(),
                chiNuoc.intValue(),
                chiVeSinh.intValue(),
                chiWifi.intValue(),
                chiKhac.intValue()
        );
    }
}