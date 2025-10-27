// LoaiNguyenLieuRepository.java
package Repository;

import Data.DataConnection;
import Model.LoaiNguyenLieu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiNguyenLieuRepository {
    
    public List<LoaiNguyenLieu> getAll() throws SQLException {
        List<LoaiNguyenLieu> list = new ArrayList<>();
        String sql = "SELECT * FROM LoaiNguyenLieu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToLoaiNguyenLieu(rs));
            }
        }
        return list;
    }
    
    public LoaiNguyenLieu getById(int maLoaiNL) throws SQLException {
        String sql = "SELECT * FROM LoaiNguyenLieu WHERE MaLoaiNL = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiNL);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoaiNguyenLieu(rs);
                }
            }
        }
        return null;
    }
    
    public boolean insert(LoaiNguyenLieu loaiNL) throws SQLException {
        String sql = "INSERT INTO LoaiNguyenLieu (TenLoaiNL, MoTa) VALUES (?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, loaiNL.getTenLoaiNL());
            stmt.setString(2, loaiNL.getMoTa());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        loaiNL.setMaLoaiNL(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(LoaiNguyenLieu loaiNL) throws SQLException {
        String sql = "UPDATE LoaiNguyenLieu SET TenLoaiNL=?, MoTa=? WHERE MaLoaiNL=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, loaiNL.getTenLoaiNL());
            stmt.setString(2, loaiNL.getMoTa());
            stmt.setInt(3, loaiNL.getMaLoaiNL());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maLoaiNL) throws SQLException {
        String sql = "DELETE FROM LoaiNguyenLieu WHERE MaLoaiNL = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiNL);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private LoaiNguyenLieu mapResultSetToLoaiNguyenLieu(ResultSet rs) throws SQLException {
        return new LoaiNguyenLieu(
            rs.getInt("MaLoaiNL"),
            rs.getString("TenLoaiNL"),
            rs.getString("MoTa")
        );
    }
}