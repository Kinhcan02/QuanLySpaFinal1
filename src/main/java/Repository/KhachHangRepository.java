// KhachHangRepository.java
package Repository;

import Data.DataConnection;
import Model.KhachHang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangRepository {
    
    public List<KhachHang> getAll() throws SQLException {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToKhachHang(rs));
            }
        }
        return list;
    }
    
    public KhachHang getById(int maKhachHang) throws SQLException {
        String sql = "SELECT * FROM KhachHang WHERE MaKhachHang = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maKhachHang);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKhachHang(rs);
                }
            }
        }
        return null;
    }
    
    public KhachHang getBySoDienThoai(String soDienThoai) throws SQLException {
        String sql = "SELECT * FROM KhachHang WHERE SoDienThoai = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, soDienThoai);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKhachHang(rs);
                }
            }
        }
        return null;
    }
    
    public List<KhachHang> searchByHoTen(String hoTen) throws SQLException {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE HoTen LIKE ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + hoTen + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToKhachHang(rs));
                }
            }
        }
        return list;
    }
    
    public List<KhachHang> getByLoaiKhach(String loaiKhach) throws SQLException {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE LoaiKhach = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, loaiKhach);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToKhachHang(rs));
                }
            }
        }
        return list;
    }
    
    public boolean insert(KhachHang khachHang) throws SQLException {
        String sql = "INSERT INTO KhachHang (HoTen, NgaySinh, LoaiKhach, SoDienThoai, GhiChu, NgayTao) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setKhachHangParameters(stmt, khachHang);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        khachHang.setMaKhachHang(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(KhachHang khachHang) throws SQLException {
        String sql = "UPDATE KhachHang SET HoTen=?, NgaySinh=?, LoaiKhach=?, SoDienThoai=?, GhiChu=? " +
                    "WHERE MaKhachHang=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setKhachHangParameters(stmt, khachHang);
            stmt.setInt(6, khachHang.getMaKhachHang());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maKhachHang) throws SQLException {
        String sql = "DELETE FROM KhachHang WHERE MaKhachHang = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maKhachHang);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private void setKhachHangParameters(PreparedStatement stmt, KhachHang khachHang) throws SQLException {
        stmt.setString(1, khachHang.getHoTen());
        
        if (khachHang.getNgaySinh() != null) {
            stmt.setDate(2, Date.valueOf(khachHang.getNgaySinh()));
        } else {
            stmt.setNull(2, Types.DATE);
        }
        
        stmt.setString(3, khachHang.getLoaiKhach());
        stmt.setString(4, khachHang.getSoDienThoai());
        stmt.setString(5, khachHang.getGhiChu());
        stmt.setTimestamp(6, Timestamp.valueOf(khachHang.getNgayTao()));
    }
    
    private KhachHang mapResultSetToKhachHang(ResultSet rs) throws SQLException {
        return new KhachHang(
            rs.getInt("MaKhachHang"),
            rs.getString("HoTen"),
            rs.getDate("NgaySinh") != null ? rs.getDate("NgaySinh").toLocalDate() : null,
            rs.getString("LoaiKhach"),
            rs.getString("SoDienThoai"),
            rs.getString("GhiChu"),
            rs.getTimestamp("NgayTao").toLocalDateTime()
        );
    }
}