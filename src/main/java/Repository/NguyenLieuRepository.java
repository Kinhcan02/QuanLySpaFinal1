// NguyenLieuRepository.java
package Repository;

import Data.DataConnection;
import Model.NguyenLieu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NguyenLieuRepository {
    
    public List<NguyenLieu> getAll() throws SQLException {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT * FROM NguyenLieu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToNguyenLieu(rs));
            }
        }
        return list;
    }
    
    public NguyenLieu getById(int maNguyenLieu) throws SQLException {
        String sql = "SELECT * FROM NguyenLieu WHERE MaNguyenLieu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNguyenLieu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNguyenLieu(rs);
                }
            }
        }
        return null;
    }
    
    public List<NguyenLieu> getByMaLoaiNL(int maLoaiNL) throws SQLException {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT * FROM NguyenLieu WHERE MaLoaiNL = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiNL);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNguyenLieu(rs));
                }
            }
        }
        return list;
    }
    
    public List<NguyenLieu> getBySoLuongTon(int soLuongMin) throws SQLException {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT * FROM NguyenLieu WHERE SoLuongTon <= ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, soLuongMin);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNguyenLieu(rs));
                }
            }
        }
        return list;
    }
    
    public boolean insert(NguyenLieu nguyenLieu) throws SQLException {
        String sql = "INSERT INTO NguyenLieu (TenNguyenLieu, SoLuongTon, DonViTinh, MaLoaiNL) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setNguyenLieuParameters(stmt, nguyenLieu);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        nguyenLieu.setMaNguyenLieu(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(NguyenLieu nguyenLieu) throws SQLException {
        String sql = "UPDATE NguyenLieu SET TenNguyenLieu=?, SoLuongTon=?, DonViTinh=?, MaLoaiNL=? WHERE MaNguyenLieu=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setNguyenLieuParameters(stmt, nguyenLieu);
            stmt.setInt(5, nguyenLieu.getMaNguyenLieu());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maNguyenLieu) throws SQLException {
        String sql = "DELETE FROM NguyenLieu WHERE MaNguyenLieu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNguyenLieu);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateSoLuongTon(int maNguyenLieu, int soLuong) throws SQLException {
        String sql = "UPDATE NguyenLieu SET SoLuongTon = ? WHERE MaNguyenLieu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, soLuong);
            stmt.setInt(2, maNguyenLieu);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    private void setNguyenLieuParameters(PreparedStatement stmt, NguyenLieu nguyenLieu) throws SQLException {
        stmt.setString(1, nguyenLieu.getTenNguyenLieu());
        stmt.setInt(2, nguyenLieu.getSoLuongTon());
        stmt.setString(3, nguyenLieu.getDonViTinh());
        
        if (nguyenLieu.getMaLoaiNL() != null) {
            stmt.setInt(4, nguyenLieu.getMaLoaiNL());
        } else {
            stmt.setNull(4, Types.INTEGER);
        }
    }
    
    private NguyenLieu mapResultSetToNguyenLieu(ResultSet rs) throws SQLException {
        return new NguyenLieu(
            rs.getInt("MaNguyenLieu"),
            rs.getString("TenNguyenLieu"),
            rs.getInt("SoLuongTon"),
            rs.getString("DonViTinh"),
            rs.getInt("MaLoaiNL")
        );
    }
    public NguyenLieu getByTen(String tenNguyenLieu) throws SQLException {
    String sql = "SELECT * FROM NguyenLieu WHERE TenNguyenLieu = ?";
    
    try (Connection conn = DataConnection.getConnection(); 
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, tenNguyenLieu);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToNguyenLieu(rs);
            }
        }
    }
    return null;
}
    
}