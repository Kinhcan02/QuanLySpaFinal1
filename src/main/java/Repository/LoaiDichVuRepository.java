// LoaiDichVuRepository.java
package Repository;

import Data.DataConnection;
import Model.LoaiDichVu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiDichVuRepository {
    
    public List<LoaiDichVu> getAll() throws SQLException {
        List<LoaiDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM LoaiDichVu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToLoaiDichVu(rs));
            }
        }
        return list;
    }
    
    public LoaiDichVu getById(int maLoaiDV) throws SQLException {
        String sql = "SELECT * FROM LoaiDichVu WHERE MaLoaiDV = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiDV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoaiDichVu(rs);
                }
            }
        }
        return null;
    }
    
    public boolean insert(LoaiDichVu loaiDV) throws SQLException {
        String sql = "INSERT INTO LoaiDichVu (TenLoaiDV, MoTa) VALUES (?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, loaiDV.getTenLoaiDV());
            stmt.setString(2, loaiDV.getMoTa());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        loaiDV.setMaLoaiDV(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(LoaiDichVu loaiDV) throws SQLException {
        String sql = "UPDATE LoaiDichVu SET TenLoaiDV=?, MoTa=? WHERE MaLoaiDV=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, loaiDV.getTenLoaiDV());
            stmt.setString(2, loaiDV.getMoTa());
            stmt.setInt(3, loaiDV.getMaLoaiDV());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maLoaiDV) throws SQLException {
        String sql = "DELETE FROM LoaiDichVu WHERE MaLoaiDV = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiDV);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private LoaiDichVu mapResultSetToLoaiDichVu(ResultSet rs) throws SQLException {
        return new LoaiDichVu(
            rs.getInt("MaLoaiDV"),
            rs.getString("TenLoaiDV"),
            rs.getString("MoTa")
        );
    }
}