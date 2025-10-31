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
        String sql = "SELECT k.MaKhachHang, k.HoTen, k.NgaySinh, k.SoDienThoai " +
                    "FROM KhachHang k " +
                    "WHERE MONTH(k.NgaySinh) = MONTH(GETDATE()) " +
                    "AND DAY(k.NgaySinh) = DAY(GETDATE()) " +
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

        // Sửa lỗi: Chuyển đổi thời gian sang string để so sánh đúng kiểu
        String sql = "SELECT dl.MaLich, dl.GioDat, dl.NgayDat, kh.HoTen, kh.SoDienThoai, " +
                    "dl.MaGiuong, dl.TrangThai " +
                    "FROM DatLich dl " +
                    "INNER JOIN KhachHang kh ON dl.MaKhachHang = kh.MaKhachHang " +
                    "WHERE dl.NgayDat = ? " +
                    "AND CONVERT(TIME, dl.GioDat) BETWEEN CONVERT(TIME, ?) AND CONVERT(TIME, ?) " +
                    "AND dl.TrangThai IN (N'Chờ xác nhận', N'Đã xác nhận') " +
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
                    "AND dl.TrangThai IN (N'Chờ xác nhận', N'Đã xác nhận') " +
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

    public List<ThongBao> getAllThongBao() {
        List<ThongBao> danhSach = new ArrayList<>();
        
        // Kết hợp cả 2 loại thông báo
        danhSach.addAll(getThongBaoSinhNhat());
        
        // Sử dụng phương thức thay thế nếu phương thức chính bị lỗi
        try {
            danhSach.addAll(getThongBaoDatLichSapToi());
        } catch (Exception e) {
            System.err.println("Sử dụng phương thức thay thế cho thông báo đặt lịch");
            danhSach.addAll(getThongBaoDatLichSapToiAlternative());
        }
        
        return danhSach;
    }
}