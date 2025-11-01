package Repository;

import Data.DataConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKeRepository {
    
    // Thống kê khách hàng sử dụng nhiều dịch vụ nhất
    public List<Map<String, Object>> getKhachHangNhieuDichVuNhat(java.util.Date fromDate, java.util.Date toDate, int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT TOP (?) " +
                    "kh.MaKhachHang, " +
                    "kh.HoTen, " +
                    "kh.SoDienThoai, " +
                    "kh.LoaiKhach, " +
                    "COUNT(DISTINCT ct.MaDichVu) as SoDichVuDaDung, " +
                    "SUM(ct.ThanhTien) as TongChiTieu " +
                    "FROM KhachHang kh " +
                    "LEFT JOIN HoaDon hd ON kh.MaKhachHang = hd.MaKhachHang " +
                    "LEFT JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon " +
                    "WHERE hd.NgayLap BETWEEN ? AND ? " +
                    "GROUP BY kh.MaKhachHang, kh.HoTen, kh.SoDienThoai, kh.LoaiKhach " +
                    "ORDER BY SoDichVuDaDung DESC, TongChiTieu DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setDate(2, new java.sql.Date(fromDate.getTime()));
            stmt.setDate(3, new java.sql.Date(toDate.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("MaKhachHang", rs.getInt("MaKhachHang"));
                item.put("HoTen", rs.getString("HoTen"));
                item.put("SoDienThoai", rs.getString("SoDienThoai"));
                item.put("LoaiKhach", rs.getString("LoaiKhach"));
                item.put("SoDichVuDaDung", rs.getInt("SoDichVuDaDung"));
                item.put("TongChiTieu", rs.getDouble("TongChiTieu"));
                result.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Thống kê doanh thu theo năm/tháng
    public List<Map<String, Object>> getDoanhThuTheoThang(int year) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT " +
                    "MONTH(hd.NgayLap) as Thang, " +
                    "SUM(hd.TongTien) as DoanhThu, " +
                    "COUNT(hd.MaHoaDon) as SoHoaDon " +
                    "FROM HoaDon hd " +
                    "WHERE YEAR(hd.NgayLap) = ? " +
                    "GROUP BY MONTH(hd.NgayLap) " +
                    "ORDER BY Thang";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("Thang", rs.getInt("Thang"));
                item.put("DoanhThu", rs.getDouble("DoanhThu"));
                item.put("SoHoaDon", rs.getInt("SoHoaDon"));
                result.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Thống kê doanh thu theo ngày
    public List<Map<String, Object>> getDoanhThuTheoNgay(java.util.Date fromDate, java.util.Date toDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT " +
                    "CONVERT(DATE, hd.NgayLap) as Ngay, " +
                    "SUM(hd.TongTien) as DoanhThu, " +
                    "COUNT(hd.MaHoaDon) as SoHoaDon " +
                    "FROM HoaDon hd " +
                    "WHERE hd.NgayLap BETWEEN ? AND ? " +
                    "GROUP BY CONVERT(DATE, hd.NgayLap) " +
                    "ORDER BY Ngay";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(fromDate.getTime()));
            stmt.setDate(2, new java.sql.Date(toDate.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("Ngay", rs.getDate("Ngay"));
                item.put("DoanhThu", rs.getDouble("DoanhThu"));
                item.put("SoHoaDon", rs.getInt("SoHoaDon"));
                result.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Thống kê dịch vụ bán chạy
    public List<Map<String, Object>> getDichVuBanChay(java.util.Date fromDate, java.util.Date toDate, int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT TOP (?) " +
                    "dv.MaDichVu, " +
                    "dv.TenDichVu, " +
                    "ldv.TenLoaiDV, " +
                    "dv.Gia, " +
                    "SUM(ct.SoLuong) as SoLuongBan, " +
                    "SUM(ct.ThanhTien) as DoanhThu " +
                    "FROM DichVu dv " +
                    "LEFT JOIN LoaiDichVu ldv ON dv.MaLoaiDV = ldv.MaLoaiDV " +
                    "LEFT JOIN ChiTietHoaDon ct ON dv.MaDichVu = ct.MaDichVu " +
                    "LEFT JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon " +
                    "WHERE hd.NgayLap BETWEEN ? AND ? " +
                    "GROUP BY dv.MaDichVu, dv.TenDichVu, ldv.TenLoaiDV, dv.Gia " +
                    "ORDER BY SoLuongBan DESC, DoanhThu DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setDate(2, new java.sql.Date(fromDate.getTime()));
            stmt.setDate(3, new java.sql.Date(toDate.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("MaDichVu", rs.getInt("MaDichVu"));
                item.put("TenDichVu", rs.getString("TenDichVu"));
                item.put("TenLoaiDV", rs.getString("TenLoaiDV"));
                item.put("Gia", rs.getDouble("Gia"));
                item.put("SoLuongBan", rs.getInt("SoLuongBan"));
                item.put("DoanhThu", rs.getDouble("DoanhThu"));
                result.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Thống kê tổng quan
    public Map<String, Object> getThongKeTongQuan(java.util.Date fromDate, java.util.Date toDate) {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT " +
                    "COUNT(DISTINCT hd.MaHoaDon) as TongHoaDon, " +
                    "ISNULL(SUM(hd.TongTien), 0) as TongDoanhThu, " +
                    "COUNT(DISTINCT hd.MaKhachHang) as TongKhachHang, " +
                    "CASE WHEN COUNT(DISTINCT hd.MaHoaDon) > 0 THEN ISNULL(SUM(hd.TongTien), 0) / COUNT(DISTINCT hd.MaHoaDon) ELSE 0 END as DonGiaTrungBinh " +
                    "FROM HoaDon hd " +
                    "WHERE hd.NgayLap BETWEEN ? AND ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(fromDate.getTime()));
            stmt.setDate(2, new java.sql.Date(toDate.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result.put("TongHoaDon", rs.getInt("TongHoaDon"));
                result.put("TongDoanhThu", rs.getDouble("TongDoanhThu"));
                result.put("TongKhachHang", rs.getInt("TongKhachHang"));
                result.put("DonGiaTrungBinh", rs.getDouble("DonGiaTrungBinh"));
            } else {
                // Trả về giá trị mặc định nếu không có dữ liệu
                result.put("TongHoaDon", 0);
                result.put("TongDoanhThu", 0.0);
                result.put("TongKhachHang", 0);
                result.put("DonGiaTrungBinh", 0.0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Trả về giá trị mặc định nếu có lỗi
            result.put("TongHoaDon", 0);
            result.put("TongDoanhThu", 0.0);
            result.put("TongKhachHang", 0);
            result.put("DonGiaTrungBinh", 0.0);
        }
        return result;
    }
    
    // Lấy danh sách năm có dữ liệu
    public List<Integer> getDanhSachNam() {
        List<Integer> result = new ArrayList<>();
        String sql = "SELECT DISTINCT YEAR(NgayLap) as Nam " +
                    "FROM HoaDon " +
                    "ORDER BY Nam DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("Nam"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}