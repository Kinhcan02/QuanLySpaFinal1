package Repository;

import Data.DataConnection;
import Model.ThongBao;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ThongBaoRepository {
    private Connection connection;

    public ThongBaoRepository() {
        this.connection = DataConnection.getConnection();
    }

    public List<ThongBao> getThongBaoSinhNhat() {
        List<ThongBao> danhSach = new ArrayList<>();
        // Access sử dụng MONTH() và DAY() nhưng không có GETDATE(), sử dụng Date() thay thế
        String sql = "SELECT k.MaKhachHang, k.HoTen, k.NgaySinh, k.SoDienThoai " +
                    "FROM KhachHang k " +
                    "WHERE MONTH(k.NgaySinh) = MONTH(DATE()) " +
                    "AND DAY(k.NgaySinh) = DAY(DATE()) " +
                    "AND k.NgaySinh IS NOT NULL";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ThongBao tb = new ThongBao();
                tb.setLoaiThongBao("SINH_NHAT");
                tb.setTieuDe("Sinh nhật khách hàng");
                tb.setNoiDung("Hôm nay là sinh nhật của " + rs.getString("HoTen") + 
                             " (" + rs.getString("SoDienThoai") + ")");
                tb.setThoiGian(LocalDateTime.now());
                tb.setTrangThai("MOI");
                danhSach.add(tb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public List<ThongBao> getThongBaoDatLichSapToi() {
        List<ThongBao> danhSach = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalTime timeThreshold = now.plusMinutes(10).toLocalTime();
        LocalDate today = now.toLocalDate();

        // Access không hỗ trợ CONVERT(TIME), sử dụng trực tiếp Time values
        String sql = "SELECT dl.MaLich, dl.GioDat, dl.NgayDat, kh.HoTen, kh.SoDienThoai, " +
                    "dl.MaGiuong, dl.TrangThai " +
                    "FROM DatLich dl " +
                    "INNER JOIN KhachHang kh ON dl.MaKhachHang = kh.MaKhachHang " +
                    "WHERE dl.NgayDat = ? " +
                    "AND dl.GioDat BETWEEN ? AND ? " +
                    "AND dl.TrangThai IN ('Chờ xác nhận', 'Đã xác nhận') " +
                    "ORDER BY dl.GioDat";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(today));
            stmt.setTime(2, Time.valueOf(now.toLocalTime()));
            stmt.setTime(3, Time.valueOf(timeThreshold));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ThongBao tb = new ThongBao();
                    tb.setLoaiThongBao("DAT_LICH");
                    tb.setTieuDe("Lịch hẹn sắp tới");
                    
                    LocalTime gioDat = rs.getTime("GioDat").toLocalTime();
                    long phutConLai = java.time.Duration.between(now.toLocalTime(), gioDat).toMinutes();
                    
                    tb.setNoiDung("Lịch hẹn với " + rs.getString("HoTen") + 
                                 " lúc " + gioDat.toString() + 
                                 " (còn " + phutConLai + " phút)");
                    tb.setThoiGian(LocalDateTime.now());
                    tb.setTrangThai("MOI");
                    tb.setMaLich(rs.getInt("MaLich"));
                    danhSach.add(tb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi SQL trong getThongBaoDatLichSapToi: " + e.getMessage());
        }
        return danhSach;
    }

    // Phương thức thay thế nếu vẫn có lỗi
    public List<ThongBao> getThongBaoDatLichSapToiAlternative() {
        List<ThongBao> danhSach = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalTime timeThreshold = now.plusMinutes(10).toLocalTime();
        LocalDate today = now.toLocalDate();

        // Cách tiếp cận khác: Lấy tất cả lịch trong ngày và lọc trong Java
        String sql = "SELECT dl.MaLich, dl.GioDat, dl.NgayDat, kh.HoTen, kh.SoDienThoai, " +
                    "dl.MaGiuong, dl.TrangThai " +
                    "FROM DatLich dl " +
                    "INNER JOIN KhachHang kh ON dl.MaKhachHang = kh.MaKhachHang " +
                    "WHERE dl.NgayDat = ? " +
                    "AND dl.TrangThai IN ('Chờ xác nhận', 'Đã xác nhận') " +
                    "ORDER BY dl.GioDat";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(today));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalTime gioDat = rs.getTime("GioDat").toLocalTime();
                    
                    // Lọc trong Java: chỉ lấy các lịch trong khoảng thời gian hiện tại đến +10 phút
                    if (gioDat.isAfter(now.toLocalTime()) && 
                        gioDat.isBefore(timeThreshold) || 
                        gioDat.equals(now.toLocalTime())) {
                        
                        ThongBao tb = new ThongBao();
                        tb.setLoaiThongBao("DAT_LICH");
                        tb.setTieuDe("Lịch hẹn sắp tới");
                        
                        long phutConLai = java.time.Duration.between(now.toLocalTime(), gioDat).toMinutes();
                        
                        tb.setNoiDung("Lịch hẹn với " + rs.getString("HoTen") + 
                                     " lúc " + gioDat.toString() + 
                                     " (còn " + phutConLai + " phút)");
                        tb.setThoiGian(LocalDateTime.now());
                        tb.setTrangThai("MOI");
                        tb.setMaLich(rs.getInt("MaLich"));
                        danhSach.add(tb);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi SQL trong getThongBaoDatLichSapToiAlternative: " + e.getMessage());
        }
        return danhSach;
    }

    // PHƯƠNG THỨC MỚI: Thông báo nguyên liệu sắp hết
    public List<ThongBao> getThongBaoNguyenLieuSapHet() {
        List<ThongBao> danhSach = new ArrayList<>();
        String sql = "SELECT MaNguyenLieu, TenNguyenLieu, SoLuongTon, DonViTinh " +
                    "FROM NguyenLieu " +
                    "WHERE SoLuongTon <= 10 " + // Ngưỡng cảnh báo
                    "ORDER BY SoLuongTon ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ThongBao tb = new ThongBao();
                tb.setLoaiThongBao("NGUYEN_LIEU");
                tb.setTieuDe("Nguyên liệu sắp hết");
                tb.setNoiDung(rs.getString("TenNguyenLieu") + 
                             " chỉ còn " + rs.getInt("SoLuongTon") + 
                             " " + rs.getString("DonViTinh"));
                tb.setThoiGian(LocalDateTime.now());
                tb.setTrangThai("MOI");
                tb.setMaNguyenLieu(rs.getInt("MaNguyenLieu"));
                danhSach.add(tb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // PHƯƠNG THỨC MỚI: Thông báo doanh thu tháng
    public List<ThongBao> getThongBaoDoanhThuThang() {
        List<ThongBao> danhSach = new ArrayList<>();
        
        // Tính doanh thu tháng hiện tại từ bảng HoaDon
        String sql = "SELECT SUM(TongTien) as DoanhThuThang " +
                    "FROM HoaDon " +
                    "WHERE MONTH(NgayLap) = MONTH(DATE()) AND YEAR(NgayLap) = YEAR(DATE())";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                java.math.BigDecimal doanhThu = rs.getBigDecimal("DoanhThuThang");
                if (doanhThu != null && doanhThu.compareTo(java.math.BigDecimal.ZERO) > 0) {
                    ThongBao tb = new ThongBao();
                    tb.setLoaiThongBao("DOANH_THU");
                    tb.setTieuDe("Doanh thu tháng");
                    tb.setNoiDung("Doanh thu tháng này: " + 
                                 String.format("%,d", doanhThu.intValue()) + " VNĐ");
                    tb.setThoiGian(LocalDateTime.now());
                    tb.setTrangThai("MOI");
                    danhSach.add(tb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // PHƯƠNG THỨC MỚI: Thông báo nhân viên làm việc hôm nay
    public List<ThongBao> getThongBaoNhanVienLamViec() {
        List<ThongBao> danhSach = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        String sql = "SELECT DISTINCT nv.MaNhanVien, nv.HoTen, cl.GioBatDau, cl.GioKetThuc " +
                    "FROM CaLam cl " +
                    "INNER JOIN NhanVien nv ON cl.MaNhanVien = nv.MaNhanVien " +
                    "WHERE cl.NgayLam = ? " +
                    "ORDER BY cl.GioBatDau";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(today));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ThongBao tb = new ThongBao();
                    tb.setLoaiThongBao("NHAN_VIEN");
                    tb.setTieuDe("Nhân viên làm việc hôm nay");
                    tb.setNoiDung(rs.getString("HoTen") + 
                                 " làm từ " + rs.getTime("GioBatDau").toLocalTime() + 
                                 " đến " + rs.getTime("GioKetThuc").toLocalTime());
                    tb.setThoiGian(LocalDateTime.now());
                    tb.setTrangThai("MOI");
                    tb.setMaNhanVien(rs.getInt("MaNhanVien"));
                    danhSach.add(tb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public List<ThongBao> getAllThongBao() {
        List<ThongBao> danhSach = new ArrayList<>();
        
        // Kết hợp tất cả các loại thông báo
        danhSach.addAll(getThongBaoSinhNhat());
        
        // Sử dụng phương thức thay thế nếu phương thức chính bị lỗi
        try {
            danhSach.addAll(getThongBaoDatLichSapToi());
        } catch (Exception e) {
            System.err.println("Sử dụng phương thức thay thế cho thông báo đặt lịch");
            danhSach.addAll(getThongBaoDatLichSapToiAlternative());
        }
        
        danhSach.addAll(getThongBaoNguyenLieuSapHet());
        danhSach.addAll(getThongBaoDoanhThuThang());
        danhSach.addAll(getThongBaoNhanVienLamViec());
        
        return danhSach;
    }

    // PHƯƠNG THỨC MỚI: Lọc thông báo theo loại
    public List<ThongBao> getThongBaoTheoLoai(String loaiThongBao) {
        switch (loaiThongBao.toUpperCase()) {
            case "SINH_NHAT":
                return getThongBaoSinhNhat();
            case "DAT_LICH":
                return getThongBaoDatLichSapToi();
            case "NGUYEN_LIEU":
                return getThongBaoNguyenLieuSapHet();
            case "DOANH_THU":
                return getThongBaoDoanhThuThang();
            case "NHAN_VIEN":
                return getThongBaoNhanVienLamViec();
            default:
                return new ArrayList<>();
        }
    }

    // PHƯƠNG THỨC MỚI: Đánh dấu thông báo đã đọc
    public void danhDauDaDoc(List<ThongBao> thongBaos) {
        // Trong thực tế, bạn có thể lưu trạng thái đã đọc vào database
        // Ở đây chỉ cập nhật trạng thái trong danh sách
        for (ThongBao tb : thongBaos) {
            tb.setTrangThai("DA_DOC");
        }
    }
}