// NhanVienRepository.java
package Repository;

import Data.DataConnection;
import Model.NhanVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienRepository {
    
    public List<NhanVien> getAll() throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToNhanVien(rs));
            }
        }
        return list;
    }
    
    public NhanVien getById(int maNhanVien) throws SQLException {
        String sql = "SELECT * FROM NhanVien WHERE MaNhanVien = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhanVien(rs);
                }
            }
        }
        return null;
    }
    
    public NhanVien getBySoDienThoai(String soDienThoai) throws SQLException {
        String sql = "SELECT * FROM NhanVien WHERE SoDienThoai = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, soDienThoai);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhanVien(rs);
                }
            }
        }
        return null;
    }
    
    public List<NhanVien> searchByHoTen(String hoTen) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE HoTen LIKE ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + hoTen + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhanVien(rs));
                }
            }
        }
        return list;
    }
    
    public List<NhanVien> getByChucVu(String chucVu) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE ChucVu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, chucVu);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhanVien(rs));
                }
            }
        }
        return list;
    }
    
    public boolean insert(NhanVien nhanVien) throws SQLException {
        String sql = "INSERT INTO NhanVien (HoTen, NgaySinh, SoDienThoai, DiaChi, ChucVu, NgayVaoLam) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setNhanVienParameters(stmt, nhanVien);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        nhanVien.setMaNhanVien(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(NhanVien nhanVien) throws SQLException {
        String sql = "UPDATE NhanVien SET HoTen=?, NgaySinh=?, SoDienThoai=?, DiaChi=?, ChucVu=?, NgayVaoLam=? " +
                    "WHERE MaNhanVien=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setNhanVienParameters(stmt, nhanVien);
            stmt.setInt(7, nhanVien.getMaNhanVien());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maNhanVien) throws SQLException {
        String sql = "DELETE FROM NhanVien WHERE MaNhanVien = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private void setNhanVienParameters(PreparedStatement stmt, NhanVien nhanVien) throws SQLException {
        stmt.setString(1, nhanVien.getHoTen());
        
        if (nhanVien.getNgaySinh() != null) {
            stmt.setDate(2, Date.valueOf(nhanVien.getNgaySinh()));
        } else {
            stmt.setNull(2, Types.DATE);
        }
        
        stmt.setString(3, nhanVien.getSoDienThoai());
        stmt.setString(4, nhanVien.getDiaChi());
        stmt.setString(5, nhanVien.getChucVu());
        
        if (nhanVien.getNgayVaoLam() != null) {
            stmt.setDate(6, Date.valueOf(nhanVien.getNgayVaoLam()));
        } else {
            stmt.setNull(6, Types.DATE);
        }
    }
    
    private NhanVien mapResultSetToNhanVien(ResultSet rs) throws SQLException {
        return new NhanVien(
            rs.getInt("MaNhanVien"),
            rs.getString("HoTen"),
            rs.getDate("NgaySinh") != null ? rs.getDate("NgaySinh").toLocalDate() : null,
            rs.getString("SoDienThoai"),
            rs.getString("DiaChi"),
            rs.getString("ChucVu"),
            rs.getDate("NgayVaoLam") != null ? rs.getDate("NgayVaoLam").toLocalDate() : null
        );
    }
}