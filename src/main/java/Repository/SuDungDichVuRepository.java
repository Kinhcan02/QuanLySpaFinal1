// SuDungDichVuRepository.java
package Repository;

import Data.DataConnection;
import Model.SuDungDichVu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuDungDichVuRepository {
    
    public List<SuDungDichVu> getAll() throws SQLException {
        List<SuDungDichVu> list = new ArrayList<>();
        String sql = "SELECT sddv.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien, dv.TenDichVu " +
                    "FROM ((SuDungDichVu sddv " +
                    "LEFT JOIN KhachHang kh ON sddv.MaKhachHang = kh.MaKhachHang) " +
                    "LEFT JOIN NhanVien nv ON sddv.MaNhanVien = nv.MaNhanVien) " +
                    "LEFT JOIN DichVu dv ON sddv.MaDichVu = dv.MaDichVu " +
                    "ORDER BY sddv.NgaySuDung DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToSuDungDichVu(rs));
            }
        }
        return list;
    }
    
    public SuDungDichVu getById(int maSuDung) throws SQLException {
        String sql = "SELECT sddv.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien, dv.TenDichVu " +
                    "FROM ((SuDungDichVu sddv " +
                    "LEFT JOIN KhachHang kh ON sddv.MaKhachHang = kh.MaKhachHang) " +
                    "LEFT JOIN NhanVien nv ON sddv.MaNhanVien = nv.MaNhanVien) " +
                    "LEFT JOIN DichVu dv ON sddv.MaDichVu = dv.MaDichVu " +
                    "WHERE sddv.MaSuDung = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maSuDung);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSuDungDichVu(rs);
                }
            }
        }
        return null;
    }
    
    public List<SuDungDichVu> getByMaKhachHang(int maKhachHang) throws SQLException {
        List<SuDungDichVu> list = new ArrayList<>();
        String sql = "SELECT sddv.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien, dv.TenDichVu " +
                    "FROM ((SuDungDichVu sddv " +
                    "LEFT JOIN KhachHang kh ON sddv.MaKhachHang = kh.MaKhachHang) " +
                    "LEFT JOIN NhanVien nv ON sddv.MaNhanVien = nv.MaNhanVien) " +
                    "LEFT JOIN DichVu dv ON sddv.MaDichVu = dv.MaDichVu " +
                    "WHERE sddv.MaKhachHang = ? " +
                    "ORDER BY sddv.NgaySuDung DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maKhachHang);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSuDungDichVu(rs));
                }
            }
        }
        return list;
    }
    
    public List<SuDungDichVu> getByMaNhanVien(int maNhanVien) throws SQLException {
        List<SuDungDichVu> list = new ArrayList<>();
        String sql = "SELECT sddv.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien, dv.TenDichVu " +
                    "FROM ((SuDungDichVu sddv " +
                    "LEFT JOIN KhachHang kh ON sddv.MaKhachHang = kh.MaKhachHang) " +
                    "LEFT JOIN NhanVien nv ON sddv.MaNhanVien = nv.MaNhanVien) " +
                    "LEFT JOIN DichVu dv ON sddv.MaDichVu = dv.MaDichVu " +
                    "WHERE sddv.MaNhanVien = ? " +
                    "ORDER BY sddv.NgaySuDung DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSuDungDichVu(rs));
                }
            }
        }
        return list;
    }
    
    public List<SuDungDichVu> getByMaDichVu(int maDichVu) throws SQLException {
        List<SuDungDichVu> list = new ArrayList<>();
        String sql = "SELECT sddv.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien, dv.TenDichVu " +
                    "FROM ((SuDungDichVu sddv " +
                    "LEFT JOIN KhachHang kh ON sddv.MaKhachHang = kh.MaKhachHang) " +
                    "LEFT JOIN NhanVien nv ON sddv.MaNhanVien = nv.MaNhanVien) " +
                    "LEFT JOIN DichVu dv ON sddv.MaDichVu = dv.MaDichVu " +
                    "WHERE sddv.MaDichVu = ? " +
                    "ORDER BY sddv.NgaySuDung DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maDichVu);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSuDungDichVu(rs));
                }
            }
        }
        return list;
    }
    
    public List<SuDungDichVu> getByDateRange(Date fromDate, Date toDate) throws SQLException {
        List<SuDungDichVu> list = new ArrayList<>();
        // Access không hỗ trợ CAST AS DATE, sử dụng trực tiếp Date
        String sql = "SELECT sddv.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien, dv.TenDichVu " +
                    "FROM ((SuDungDichVu sddv " +
                    "LEFT JOIN KhachHang kh ON sddv.MaKhachHang = kh.MaKhachHang) " +
                    "LEFT JOIN NhanVien nv ON sddv.MaNhanVien = nv.MaNhanVien) " +
                    "LEFT JOIN DichVu dv ON sddv.MaDichVu = dv.MaDichVu " +
                    "WHERE sddv.NgaySuDung BETWEEN ? AND ? " +
                    "ORDER BY sddv.NgaySuDung DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new Timestamp(fromDate.getTime()));
            stmt.setTimestamp(2, new Timestamp(toDate.getTime() + 24 * 60 * 60 * 1000 - 1000)); // Thêm 1 ngày - 1 giây
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSuDungDichVu(rs));
                }
            }
        }
        return list;
    }
    
    // PHƯƠNG THỨC MỚI: Tìm theo tháng năm
    public List<SuDungDichVu> getByThangNam(int thang, int nam) throws SQLException {
        List<SuDungDichVu> list = new ArrayList<>();
        // Access sử dụng MONTH() và YEAR() cho DateTime fields
        String sql = "SELECT sddv.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien, dv.TenDichVu " +
                    "FROM ((SuDungDichVu sddv " +
                    "LEFT JOIN KhachHang kh ON sddv.MaKhachHang = kh.MaKhachHang) " +
                    "LEFT JOIN NhanVien nv ON sddv.MaNhanVien = nv.MaNhanVien) " +
                    "LEFT JOIN DichVu dv ON sddv.MaDichVu = dv.MaDichVu " +
                    "WHERE MONTH(sddv.NgaySuDung) = ? AND YEAR(sddv.NgaySuDung) = ? " +
                    "ORDER BY sddv.NgaySuDung DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSuDungDichVu(rs));
                }
            }
        }
        return list;
    }
    
    // PHƯƠNG THỨC MỚI: Tìm theo năm
    public List<SuDungDichVu> getByNam(int nam) throws SQLException {
        List<SuDungDichVu> list = new ArrayList<>();
        String sql = "SELECT sddv.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien, dv.TenDichVu " +
                    "FROM ((SuDungDichVu sddv " +
                    "LEFT JOIN KhachHang kh ON sddv.MaKhachHang = kh.MaKhachHang) " +
                    "LEFT JOIN NhanVien nv ON sddv.MaNhanVien = nv.MaNhanVien) " +
                    "LEFT JOIN DichVu dv ON sddv.MaDichVu = dv.MaDichVu " +
                    "WHERE YEAR(sddv.NgaySuDung) = ? " +
                    "ORDER BY sddv.NgaySuDung DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSuDungDichVu(rs));
                }
            }
        }
        return list;
    }
    
    public boolean insert(SuDungDichVu suDungDV) throws SQLException {
        String sql = "INSERT INTO SuDungDichVu (MaKhachHang, MaDichVu, MaNhanVien, NgaySuDung, SoTien, TienTip) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setSuDungDichVuParameters(stmt, suDungDV);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        suDungDV.setMaSuDung(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(SuDungDichVu suDungDV) throws SQLException {
        String sql = "UPDATE SuDungDichVu SET MaKhachHang=?, MaDichVu=?, MaNhanVien=?, NgaySuDung=?, SoTien=?, TienTip=? " +
                    "WHERE MaSuDung=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setSuDungDichVuParameters(stmt, suDungDV);
            stmt.setInt(7, suDungDV.getMaSuDung());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maSuDung) throws SQLException {
        String sql = "DELETE FROM SuDungDichVu WHERE MaSuDung = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maSuDung);
            return stmt.executeUpdate() > 0;
        }
    }
    
    // PHƯƠNG THỨC MỚI: Tính tổng doanh thu theo tháng năm
    public java.math.BigDecimal getTongDoanhThuTheoThangNam(int thang, int nam) throws SQLException {
        String sql = "SELECT SUM(SoTien) as TongDoanhThu FROM SuDungDichVu " +
                    "WHERE MONTH(NgaySuDung) = ? AND YEAR(NgaySuDung) = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    java.math.BigDecimal tongDoanhThu = rs.getBigDecimal("TongDoanhThu");
                    return tongDoanhThu != null ? tongDoanhThu : java.math.BigDecimal.ZERO;
                }
            }
        }
        return java.math.BigDecimal.ZERO;
    }
    
    // PHƯƠNG THỨC MỚI: Tính tổng tiền tip theo tháng năm
    public java.math.BigDecimal getTongTienTipTheoThangNam(int thang, int nam) throws SQLException {
        String sql = "SELECT SUM(TienTip) as TongTienTip FROM SuDungDichVu " +
                    "WHERE MONTH(NgaySuDung) = ? AND YEAR(NgaySuDung) = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    java.math.BigDecimal tongTienTip = rs.getBigDecimal("TongTienTip");
                    return tongTienTip != null ? tongTienTip : java.math.BigDecimal.ZERO;
                }
            }
        }
        return java.math.BigDecimal.ZERO;
    }
    
    // PHƯƠNG THỨC MỚI: Thống kê dịch vụ được sử dụng nhiều nhất
    public List<Object[]> getTopDichVuSuDungNhieuNhat(int limit) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT TOP ? dv.TenDichVu, COUNT(sddv.MaSuDung) as SoLanSuDung, SUM(sddv.SoTien) as TongTien " +
                    "FROM SuDungDichVu sddv " +
                    "INNER JOIN DichVu dv ON sddv.MaDichVu = dv.MaDichVu " +
                    "GROUP BY dv.MaDichVu, dv.TenDichVu " +
                    "ORDER BY SoLanSuDung DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] data = new Object[3];
                    data[0] = rs.getString("TenDichVu");
                    data[1] = rs.getInt("SoLanSuDung");
                    data[2] = rs.getBigDecimal("TongTien");
                    list.add(data);
                }
            }
        }
        return list;
    }
    
    // PHƯƠNG THỨC MỚI: Thống kê doanh thu theo dịch vụ
    public List<Object[]> getDoanhThuTheoDichVu(Date fromDate, Date toDate) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT dv.TenDichVu, COUNT(sddv.MaSuDung) as SoLanSuDung, SUM(sddv.SoTien) as TongDoanhThu " +
                    "FROM SuDungDichVu sddv " +
                    "INNER JOIN DichVu dv ON sddv.MaDichVu = dv.MaDichVu " +
                    "WHERE sddv.NgaySuDung BETWEEN ? AND ? " +
                    "GROUP BY dv.MaDichVu, dv.TenDichVu " +
                    "ORDER BY TongDoanhThu DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new Timestamp(fromDate.getTime()));
            stmt.setTimestamp(2, new Timestamp(toDate.getTime() + 24 * 60 * 60 * 1000 - 1000));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] data = new Object[3];
                    data[0] = rs.getString("TenDichVu");
                    data[1] = rs.getInt("SoLanSuDung");
                    data[2] = rs.getBigDecimal("TongDoanhThu");
                    list.add(data);
                }
            }
        }
        return list;
    }
    
    private void setSuDungDichVuParameters(PreparedStatement stmt, SuDungDichVu suDungDV) throws SQLException {
        if (suDungDV.getMaKhachHang() != null) {
            stmt.setInt(1, suDungDV.getMaKhachHang());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }
        
        stmt.setInt(2, suDungDV.getMaDichVu());
        
        if (suDungDV.getMaNhanVien() != null) {
            stmt.setInt(3, suDungDV.getMaNhanVien());
        } else {
            stmt.setNull(3, Types.INTEGER);
        }
        
        stmt.setTimestamp(4, Timestamp.valueOf(suDungDV.getNgaySuDung()));
        
        if (suDungDV.getSoTien() != null) {
            stmt.setBigDecimal(5, suDungDV.getSoTien());
        } else {
            stmt.setNull(5, Types.DECIMAL);
        }
        
        if (suDungDV.getTienTip() != null) {
            stmt.setBigDecimal(6, suDungDV.getTienTip());
        } else {
            stmt.setNull(6, Types.DECIMAL);
        }
    }
    
    private SuDungDichVu mapResultSetToSuDungDichVu(ResultSet rs) throws SQLException {
        SuDungDichVu suDungDV = new SuDungDichVu(
            rs.getInt("MaSuDung"),
            rs.getInt("MaKhachHang"),
            rs.getInt("MaDichVu"),
            rs.getInt("MaNhanVien"),
            rs.getTimestamp("NgaySuDung").toLocalDateTime(),
            rs.getBigDecimal("SoTien"),
            rs.getBigDecimal("TienTip")
        );
        
        // Xử lý các giá trị có thể null
        if (rs.getInt("MaKhachHang") == 0 && rs.wasNull()) {
            suDungDV.setMaKhachHang(null);
        }
        
        if (rs.getInt("MaNhanVien") == 0 && rs.wasNull()) {
            suDungDV.setMaNhanVien(null);
        }
        
        return suDungDV;
    }
}