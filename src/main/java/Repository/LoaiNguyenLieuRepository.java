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
        String sql = "SELECT * FROM LoaiNguyenLieu ORDER BY TenLoaiNL ASC";
        
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
    
    public LoaiNguyenLieu getByTenLoaiNL(String tenLoaiNL) throws SQLException {
        String sql = "SELECT * FROM LoaiNguyenLieu WHERE TenLoaiNL = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tenLoaiNL);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoaiNguyenLieu(rs);
                }
            }
        }
        return null;
    }
    
    public List<LoaiNguyenLieu> searchByTen(String tenLoaiNL) throws SQLException {
        List<LoaiNguyenLieu> list = new ArrayList<>();
        String sql = "SELECT * FROM LoaiNguyenLieu WHERE TenLoaiNL LIKE ? ORDER BY TenLoaiNL ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + tenLoaiNL + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLoaiNguyenLieu(rs));
                }
            }
        }
        return list;
    }
    
    public boolean insert(LoaiNguyenLieu loaiNL) throws SQLException {
        // Kiểm tra tên loại nguyên liệu đã tồn tại chưa
        if (isTenLoaiNLExists(loaiNL.getTenLoaiNL())) {
            throw new SQLException("Tên loại nguyên liệu đã tồn tại trong hệ thống.");
        }
        
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
        // Kiểm tra tên loại nguyên liệu đã tồn tại chưa (trừ bản ghi hiện tại)
        if (isTenLoaiNLExists(loaiNL.getTenLoaiNL(), loaiNL.getMaLoaiNL())) {
            throw new SQLException("Tên loại nguyên liệu đã tồn tại trong hệ thống.");
        }
        
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
        // Kiểm tra xem loại nguyên liệu có đang được sử dụng không
        if (isNguyenLieuUsingLoai(maLoaiNL)) {
            throw new SQLException("Không thể xóa loại nguyên liệu vì có nguyên liệu đang sử dụng loại này.");
        }
        
        String sql = "DELETE FROM LoaiNguyenLieu WHERE MaLoaiNL = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiNL);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean isNguyenLieuUsingLoai(int maLoaiNL) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NguyenLieu WHERE MaLoaiNL = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiNL);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public boolean isTenLoaiNLExists(String tenLoaiNL) throws SQLException {
        return isTenLoaiNLExists(tenLoaiNL, 0);
    }
    
    public boolean isTenLoaiNLExists(String tenLoaiNL, int excludeMaLoaiNL) throws SQLException {
        String sql = "SELECT COUNT(*) FROM LoaiNguyenLieu WHERE TenLoaiNL = ? AND MaLoaiNL != ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tenLoaiNL);
            stmt.setInt(2, excludeMaLoaiNL);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public int countNguyenLieuByLoai(int maLoaiNL) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NguyenLieu WHERE MaLoaiNL = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiNL);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public int getTotalLoaiNguyenLieu() throws SQLException {
        String sql = "SELECT COUNT(*) FROM LoaiNguyenLieu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public List<LoaiNguyenLieu> getLoaiNguyenLieuWithCount() throws SQLException {
        List<LoaiNguyenLieu> list = new ArrayList<>();
        String sql = "SELECT lnl.*, COUNT(nl.MaNguyenLieu) AS SoNguyenLieu " +
                    "FROM LoaiNguyenLieu lnl " +
                    "LEFT JOIN NguyenLieu nl ON lnl.MaLoaiNL = nl.MaLoaiNL " +
                    "GROUP BY lnl.MaLoaiNL, lnl.TenLoaiNL, lnl.MoTa " +
                    "ORDER BY lnl.TenLoaiNL ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LoaiNguyenLieu loaiNL = mapResultSetToLoaiNguyenLieu(rs);
                // Có thể set thêm thông tin số lượng nguyên liệu nếu cần
                list.add(loaiNL);
            }
        }
        return list;
    }
    
    public boolean hasNguyenLieu(int maLoaiNL) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NguyenLieu WHERE MaLoaiNL = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiNL);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public List<LoaiNguyenLieu> getLoaiNguyenLieuKhongCoNguyenLieu() throws SQLException {
        List<LoaiNguyenLieu> list = new ArrayList<>();
        String sql = "SELECT lnl.* FROM LoaiNguyenLieu lnl " +
                    "LEFT JOIN NguyenLieu nl ON lnl.MaLoaiNL = nl.MaLoaiNL " +
                    "WHERE nl.MaNguyenLieu IS NULL " +
                    "ORDER BY lnl.TenLoaiNL ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToLoaiNguyenLieu(rs));
            }
        }
        return list;
    }
    
    private LoaiNguyenLieu mapResultSetToLoaiNguyenLieu(ResultSet rs) throws SQLException {
        LoaiNguyenLieu loaiNL = new LoaiNguyenLieu(
            rs.getInt("MaLoaiNL"),
            rs.getString("TenLoaiNL"),
            rs.getString("MoTa")
        );
        
        // Xử lý các trường có thể null
        String moTa = rs.getString("MoTa");
        if (rs.wasNull()) {
            loaiNL.setMoTa(null);
        }
        
        return loaiNL;
    }
}