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
        // Access sử dụng TOP và không có DISTINCT trong COUNT
        String sql = "SELECT TOP ? "
                + "kh.MaKhachHang, "
                + "kh.HoTen, "
                + "kh.SoDienThoai, "
                + "kh.LoaiKhach, "
                + "COUNT(ct.MaDichVu) as SoDichVuDaDung, "
                + // Access không hỗ trợ COUNT(DISTINCT)
                "SUM(ct.ThanhTien) as TongChiTieu "
                + "FROM (KhachHang kh "
                + "LEFT JOIN HoaDon hd ON kh.MaKhachHang = hd.MaKhachHang) "
                + "LEFT JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon "
                + "WHERE hd.NgayLap BETWEEN ? AND ? "
                + "GROUP BY kh.MaKhachHang, kh.HoTen, kh.SoDienThoai, kh.LoaiKhach "
                + "ORDER BY SoDichVuDaDung DESC, TongChiTieu DESC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

    // Thêm phương thức mới trong ThongKeRepository
    public List<Map<String, Object>> getHoaDonTheoThoiGian(java.util.Date fromDate, java.util.Date toDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT "
                + "hd.MaHoaDon, "
                + "hd.NgayLap, "
                + "kh.HoTen as TenKhachHang, "
                + "nv.HoTen as TenNhanVien, "
                + "hd.TongTien, "
                + "COUNT(ct.MaCTHD) as SoDichVu, "
                + "hd.GhiChu "
                + "FROM ((HoaDon hd "
                + "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang) "
                + "LEFT JOIN NhanVien nv ON hd.MaNhanVienLap = nv.MaNhanVien) "
                + "LEFT JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon "
                + "WHERE hd.NgayLap BETWEEN ? AND ? "
                + "GROUP BY hd.MaHoaDon, hd.NgayLap, kh.HoTen, nv.HoTen, hd.TongTien, hd.GhiChu "
                + "ORDER BY hd.NgayLap DESC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, new Timestamp(fromDate.getTime()));
            stmt.setTimestamp(2, new Timestamp(toDate.getTime() + 24 * 60 * 60 * 1000 - 1000));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("MaHoaDon", rs.getInt("MaHoaDon"));
                item.put("NgayLap", rs.getTimestamp("NgayLap"));
                item.put("TenKhachHang", rs.getString("TenKhachHang"));
                item.put("TenNhanVien", rs.getString("TenNhanVien"));
                item.put("TongTien", rs.getDouble("TongTien"));
                item.put("SoDichVu", rs.getInt("SoDichVu"));
                item.put("GhiChu", rs.getString("GhiChu"));
                result.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

// Thêm phương thức thống kê chi tiết hóa đơn
    public List<Map<String, Object>> getChiTietHoaDon(int maHoaDon) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT "
                + "ct.MaCTHD, "
                + "dv.TenDichVu, "
                + "ldv.TenLoaiDV, "
                + "nv.HoTen as TenNhanVienThucHien, "
                + "ct.SoLuong, "
                + "ct.DonGia, "
                + "ct.ThanhTien "
                + "FROM ((ChiTietHoaDon ct "
                + "LEFT JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu) "
                + "LEFT JOIN LoaiDichVu ldv ON dv.MaLoaiDV = ldv.MaLoaiDV) "
                + "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien "
                + "WHERE ct.MaHoaDon = ? "
                + "ORDER BY ct.MaCTHD";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maHoaDon);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("MaCTHD", rs.getInt("MaCTHD"));
                item.put("TenDichVu", rs.getString("TenDichVu"));
                item.put("TenLoaiDV", rs.getString("TenLoaiDV"));
                item.put("TenNhanVienThucHien", rs.getString("TenNhanVienThucHien"));
                item.put("SoLuong", rs.getInt("SoLuong"));
                item.put("DonGia", rs.getDouble("DonGia"));
                item.put("ThanhTien", rs.getDouble("ThanhTien"));
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
        String sql = "SELECT "
                + "MONTH(hd.NgayLap) as Thang, "
                + "SUM(hd.TongTien) as DoanhThu, "
                + "COUNT(hd.MaHoaDon) as SoHoaDon "
                + "FROM HoaDon hd "
                + "WHERE YEAR(hd.NgayLap) = ? "
                + "GROUP BY MONTH(hd.NgayLap) "
                + "ORDER BY Thang";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        // Access không hỗ trợ CONVERT, sử dụng Date field trực tiếp
        String sql = "SELECT "
                + "hd.NgayLap as Ngay, "
                + // Access không có DATE type riêng, dùng DateTime
                "SUM(hd.TongTien) as DoanhThu, "
                + "COUNT(hd.MaHoaDon) as SoHoaDon "
                + "FROM HoaDon hd "
                + "WHERE hd.NgayLap BETWEEN ? AND ? "
                + "GROUP BY hd.NgayLap "
                + // Group theo DateTime
                "ORDER BY Ngay";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, new Timestamp(fromDate.getTime()));
            stmt.setTimestamp(2, new Timestamp(toDate.getTime() + 24 * 60 * 60 * 1000 - 1000)); // Thêm 1 ngày - 1 giây

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("Ngay", rs.getTimestamp("Ngay"));
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
        String sql = "SELECT TOP ? "
                + "dv.MaDichVu, "
                + "dv.TenDichVu, "
                + "ldv.TenLoaiDV, "
                + "dv.Gia, "
                + "SUM(ct.SoLuong) as SoLuongBan, "
                + "SUM(ct.ThanhTien) as DoanhThu "
                + "FROM ((DichVu dv "
                + "LEFT JOIN LoaiDichVu ldv ON dv.MaLoaiDV = ldv.MaLoaiDV) "
                + "LEFT JOIN ChiTietHoaDon ct ON dv.MaDichVu = ct.MaDichVu) "
                + "LEFT JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon "
                + "WHERE hd.NgayLap BETWEEN ? AND ? "
                + "GROUP BY dv.MaDichVu, dv.TenDichVu, ldv.TenLoaiDV, dv.Gia "
                + "ORDER BY SoLuongBan DESC, DoanhThu DESC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        // Access sử dụng IIF thay vì CASE WHEN
        String sql = "SELECT "
                + "COUNT(DISTINCT hd.MaHoaDon) as TongHoaDon, "
                + "IIF(SUM(hd.TongTien) IS NULL, 0, SUM(hd.TongTien)) as TongDoanhThu, "
                + "COUNT(DISTINCT hd.MaKhachHang) as TongKhachHang, "
                + "IIF(COUNT(DISTINCT hd.MaHoaDon) > 0, IIF(SUM(hd.TongTien) IS NULL, 0, SUM(hd.TongTien)) / COUNT(DISTINCT hd.MaHoaDon), 0) as DonGiaTrungBinh "
                + "FROM HoaDon hd "
                + "WHERE hd.NgayLap BETWEEN ? AND ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        String sql = "SELECT DISTINCT YEAR(NgayLap) as Nam "
                + "FROM HoaDon "
                + "ORDER BY Nam DESC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("Nam"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // THÊM PHƯƠNG THỨC MỚI: Thống kê nhân viên có doanh thu cao nhất
    public List<Map<String, Object>> getNhanVienDoanhThuCaoNhat(java.util.Date fromDate, java.util.Date toDate, int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT TOP ? "
                + "nv.MaNhanVien, "
                + "nv.HoTen, "
                + "nv.ChucVu, "
                + "COUNT(DISTINCT hd.MaHoaDon) as SoHoaDon, "
                + "SUM(hd.TongTien) as TongDoanhThu "
                + "FROM NhanVien nv "
                + "LEFT JOIN HoaDon hd ON nv.MaNhanVien = hd.MaNhanVienLap "
                + "WHERE hd.NgayLap BETWEEN ? AND ? "
                + "GROUP BY nv.MaNhanVien, nv.HoTen, nv.ChucVu "
                + "ORDER BY TongDoanhThu DESC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setDate(2, new java.sql.Date(fromDate.getTime()));
            stmt.setDate(3, new java.sql.Date(toDate.getTime()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("MaNhanVien", rs.getInt("MaNhanVien"));
                item.put("HoTen", rs.getString("HoTen"));
                item.put("ChucVu", rs.getString("ChucVu"));
                item.put("SoHoaDon", rs.getInt("SoHoaDon"));
                item.put("TongDoanhThu", rs.getDouble("TongDoanhThu"));
                result.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // THÊM PHƯƠNG THỨC MỚI: Thống kê doanh thu theo loại dịch vụ
    public List<Map<String, Object>> getDoanhThuTheoLoaiDichVu(java.util.Date fromDate, java.util.Date toDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT "
                + "ldv.MaLoaiDV, "
                + "ldv.TenLoaiDV, "
                + "COUNT(DISTINCT dv.MaDichVu) as SoDichVu, "
                + "SUM(ct.ThanhTien) as DoanhThu, "
                + "COUNT(DISTINCT hd.MaHoaDon) as SoHoaDon "
                + "FROM (((LoaiDichVu ldv "
                + "LEFT JOIN DichVu dv ON ldv.MaLoaiDV = dv.MaLoaiDV) "
                + "LEFT JOIN ChiTietHoaDon ct ON dv.MaDichVu = ct.MaDichVu) "
                + "LEFT JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon) "
                + "WHERE hd.NgayLap BETWEEN ? AND ? "
                + "GROUP BY ldv.MaLoaiDV, ldv.TenLoaiDV "
                + "ORDER BY DoanhThu DESC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(fromDate.getTime()));
            stmt.setDate(2, new java.sql.Date(toDate.getTime()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("MaLoaiDV", rs.getInt("MaLoaiDV"));
                item.put("TenLoaiDV", rs.getString("TenLoaiDV"));
                item.put("SoDichVu", rs.getInt("SoDichVu"));
                item.put("DoanhThu", rs.getDouble("DoanhThu"));
                item.put("SoHoaDon", rs.getInt("SoHoaDon"));
                result.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // THÊM PHƯƠNG THỨC MỚI: Thống kê chi tiêu và thu nhập
    public Map<String, Object> getThongKeTaiChinh(int thang, int nam) {
        Map<String, Object> result = new HashMap<>();

        // Doanh thu từ hóa đơn
        String sqlDoanhThu = "SELECT SUM(TongTien) as TongDoanhThu "
                + "FROM HoaDon "
                + "WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?";

        // Chi phí lương nhân viên
        String sqlLuong = "SELECT SUM(TongLuong) as TongLuong "
                + "FROM TinhLuongNhanVien "
                + "WHERE Thang = ? AND Nam = ?";

        // Chi phí nhập nguyên liệu
        String sqlNhapNL = "SELECT SUM(SoLuong * DonGia) as TongNhapNL "
                + "FROM NhapNguyenLieu "
                + "WHERE MONTH(NgayNhap) = ? AND YEAR(NgayNhap) = ?";

        try (Connection conn = DataConnection.getConnection()) {

            // Doanh thu
            try (PreparedStatement stmt = conn.prepareStatement(sqlDoanhThu)) {
                stmt.setInt(1, thang);
                stmt.setInt(2, nam);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    result.put("TongDoanhThu", rs.getDouble("TongDoanhThu"));
                }
            }

            // Lương
            try (PreparedStatement stmt = conn.prepareStatement(sqlLuong)) {
                stmt.setInt(1, thang);
                stmt.setInt(2, nam);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    result.put("TongLuong", rs.getDouble("TongLuong"));
                }
            }

            // Nhập nguyên liệu
            try (PreparedStatement stmt = conn.prepareStatement(sqlNhapNL)) {
                stmt.setInt(1, thang);
                stmt.setInt(2, nam);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    result.put("TongNhapNL", rs.getDouble("TongNhapNL"));
                }
            }

            // Tính thu nhập thực
            double doanhThu = (Double) result.getOrDefault("TongDoanhThu", 0.0);
            double luong = (Double) result.getOrDefault("TongLuong", 0.0);
            double nhapNL = (Double) result.getOrDefault("TongNhapNL", 0.0);
            double thuNhapThuc = doanhThu - luong - nhapNL;

            result.put("ThuNhapThuc", thuNhapThuc);
            result.put("Thang", thang);
            result.put("Nam", nam);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
