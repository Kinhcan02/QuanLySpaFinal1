package Repository;

import Data.DataConnection;
import Model.LuongNhanVien;
import Model.NhanVien;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LuongNhanVienRepository {
    
    public List<LuongNhanVien> getAll() {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen, n.HeSoLuong FROM LuongNhanVien l " +
                    "LEFT JOIN NhanVien n ON l.MaNhanVien = n.MaNhanVien " +
                    "ORDER BY l.Nam DESC, l.Thang DESC, n.HoTen";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LuongNhanVien luong = mapResultSetToLuongNhanVien(rs);
                list.add(luong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<LuongNhanVien> getByThangNam(Integer thang, Integer nam) {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen, n.HeSoLuong FROM LuongNhanVien l " +
                    "LEFT JOIN NhanVien n ON l.MaNhanVien = n.MaNhanVien " +
                    "WHERE l.Thang = ? AND l.Nam = ? " +
                    "ORDER BY n.HoTen";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LuongNhanVien luong = mapResultSetToLuongNhanVien(rs);
                list.add(luong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<LuongNhanVien> getByMaNhanVienThangNam(Integer maNhanVien, Integer thang, Integer nam) {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen, n.HeSoLuong FROM LuongNhanVien l " +
                    "LEFT JOIN NhanVien n ON l.MaNhanVien = n.MaNhanVien " +
                    "WHERE l.MaNhanVien = ? AND l.Thang = ? AND l.Nam = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                LuongNhanVien luong = mapResultSetToLuongNhanVien(rs);
                list.add(luong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean insert(LuongNhanVien luong) {
        String sql = "INSERT INTO LuongNhanVien (MaNhanVien, Thang, Nam, TongLuong, NgayTinhLuong, TrangThai) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, luong.getMaNhanVien());
            stmt.setInt(2, luong.getThang());
            stmt.setInt(3, luong.getNam());
            stmt.setBigDecimal(4, luong.getTongLuong());
            stmt.setTimestamp(5, Timestamp.valueOf(luong.getNgayTinhLuong()));
            stmt.setString(6, luong.getTrangThai());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean update(LuongNhanVien luong) {
        String sql = "UPDATE LuongNhanVien SET TongLuong = ?, NgayTinhLuong = ?, TrangThai = ? " +
                    "WHERE MaLuong = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, luong.getTongLuong());
            stmt.setTimestamp(2, Timestamp.valueOf(luong.getNgayTinhLuong()));
            stmt.setString(3, luong.getTrangThai());
            stmt.setInt(4, luong.getMaLuong());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateTrangThai(Integer maLuong, String trangThai) {
        String sql = "UPDATE LuongNhanVien SET TrangThai = ? WHERE MaLuong = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, trangThai);
            stmt.setInt(2, maLuong);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public BigDecimal tinhTongThanhTienTheoThangNam(Integer maNhanVien, Integer thang, Integer nam) {
        String sql = "SELECT SUM(ThanhTien) as TongThanhTien FROM ChiTietHoaDon cthd " +
                    "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                    "WHERE cthd.MaNhanVien = ? AND MONTH(hd.NgayLap) = ? AND YEAR(hd.NgayLap) = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal("TongThanhTien");
                return result != null ? result : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    public boolean exists(Integer maNhanVien, Integer thang, Integer nam) {
        String sql = "SELECT COUNT(*) FROM LuongNhanVien WHERE MaNhanVien = ? AND Thang = ? AND Nam = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private LuongNhanVien mapResultSetToLuongNhanVien(ResultSet rs) throws SQLException {
        LuongNhanVien luong = new LuongNhanVien();
        luong.setMaLuong(rs.getInt("MaLuong"));
        luong.setMaNhanVien(rs.getInt("MaNhanVien"));
        luong.setThang(rs.getInt("Thang"));
        luong.setNam(rs.getInt("Nam"));
        luong.setTongLuong(rs.getBigDecimal("TongLuong"));
        
        Timestamp ngayTinh = rs.getTimestamp("NgayTinhLuong");
        if (ngayTinh != null) {
            luong.setNgayTinhLuong(ngayTinh.toLocalDateTime());
        }
        
        luong.setTrangThai(rs.getString("TrangThai"));
        
        // Tạo đối tượng nhân viên tham chiếu
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(rs.getInt("MaNhanVien"));
        nv.setHoTen(rs.getString("HoTen"));
        nv.setHeSoLuong(rs.getBigDecimal("HeSoLuong"));
        luong.setNhanVien(nv);
        
        return luong;
    }
    public boolean delete(Integer maLuong) {
    String sql = "DELETE FROM LuongNhanVien WHERE MaLuong = ?";
    
    try (Connection conn = DataConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, maLuong);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
}