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
        String sql = "SELECT * FROM LoaiDichVu ORDER BY TenLoaiDV ASC";
        
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
    
    public LoaiDichVu getByTenLoaiDV(String tenLoaiDV) throws SQLException {
        String sql = "SELECT * FROM LoaiDichVu WHERE TenLoaiDV = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tenLoaiDV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoaiDichVu(rs);
                }
            }
        }
        return null;
    }
    
    public List<LoaiDichVu> searchByTen(String tenLoaiDV) throws SQLException {
        List<LoaiDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM LoaiDichVu WHERE TenLoaiDV LIKE ? ORDER BY TenLoaiDV ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + tenLoaiDV + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLoaiDichVu(rs));
                }
            }
        }
        return list;
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
        // Kiểm tra xem loại dịch vụ có đang được sử dụng không
        if (isDichVuUsingLoai(maLoaiDV)) {
            throw new SQLException("Không thể xóa loại dịch vụ vì có dịch vụ đang sử dụng loại này.");
        }
        
        String sql = "DELETE FROM LoaiDichVu WHERE MaLoaiDV = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiDV);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean isDichVuUsingLoai(int maLoaiDV) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DichVu WHERE MaLoaiDV = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiDV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public int countDichVuByLoai(int maLoaiDV) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DichVu WHERE MaLoaiDV = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiDV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public boolean isTenLoaiDVExists(String tenLoaiDV) throws SQLException {
        return isTenLoaiDVExists(tenLoaiDV, 0);
    }
    
    public boolean isTenLoaiDVExists(String tenLoaiDV, int excludeMaLoaiDV) throws SQLException {
        String sql = "SELECT COUNT(*) FROM LoaiDichVu WHERE TenLoaiDV = ? AND MaLoaiDV != ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tenLoaiDV);
            stmt.setInt(2, excludeMaLoaiDV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public int getTotalLoaiDichVu() throws SQLException {
        String sql = "SELECT COUNT(*) FROM LoaiDichVu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public List<LoaiDichVu> getLoaiDichVuWithDichVuCount() throws SQLException {
        List<LoaiDichVu> list = new ArrayList<>();
        String sql = "SELECT ldv.*, COUNT(dv.MaDichVu) AS SoDichVu " +
                    "FROM LoaiDichVu ldv " +
                    "LEFT JOIN DichVu dv ON ldv.MaLoaiDV = dv.MaLoaiDV " +
                    "GROUP BY ldv.MaLoaiDV, ldv.TenLoaiDV, ldv.MoTa " +
                    "ORDER BY ldv.TenLoaiDV ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LoaiDichVu loaiDV = mapResultSetToLoaiDichVu(rs);
                // Có thể set thêm thông tin số lượng dịch vụ nếu cần
                list.add(loaiDV);
            }
        }
        return list;
    }
    
    private LoaiDichVu mapResultSetToLoaiDichVu(ResultSet rs) throws SQLException {
        LoaiDichVu loaiDV = new LoaiDichVu(
            rs.getInt("MaLoaiDV"),
            rs.getString("TenLoaiDV"),
            rs.getString("MoTa")
        );
        
        // Xử lý các trường có thể null
        String moTa = rs.getString("MoTa");
        if (rs.wasNull()) {
            loaiDV.setMoTa(null);
        }
        
        return loaiDV;
    }
}