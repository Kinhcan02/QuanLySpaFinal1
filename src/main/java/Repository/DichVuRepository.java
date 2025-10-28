package Repository;

import Data.DataConnection;
import Model.DichVu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DichVuRepository {
    
    public List<DichVu> getAll() throws SQLException {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToDichVu(rs));
            }
        }
        return list;
    }
    
    public DichVu getById(int maDichVu) throws SQLException {
        String sql = "SELECT * FROM DichVu WHERE MaDichVu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maDichVu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDichVu(rs);
                }
            }
        }
        return null;
    }
    
    public List<DichVu> getByMaLoaiDV(int maLoaiDV) throws SQLException {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu WHERE MaLoaiDV = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiDV);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDichVu(rs));
                }
            }
        }
        return list;
    }
    
    public List<DichVu> searchByTen(String tenDichVu) throws SQLException {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu WHERE TenDichVu LIKE ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + tenDichVu + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDichVu(rs));
                }
            }
        }
        return list;
    }
    
    public boolean insert(DichVu dichVu) throws SQLException {
        String sql = "INSERT INTO DichVu (TenDichVu, Gia, ThoiGian, MaLoaiDV, GhiChu) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setDichVuParameters(stmt, dichVu);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        dichVu.setMaDichVu(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(DichVu dichVu) throws SQLException {
        String sql = "UPDATE DichVu SET TenDichVu=?, Gia=?, ThoiGian=?, MaLoaiDV=?, GhiChu=? WHERE MaDichVu=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setDichVuParameters(stmt, dichVu);
            stmt.setInt(6, dichVu.getMaDichVu());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maDichVu) throws SQLException {
        String sql = "DELETE FROM DichVu WHERE MaDichVu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maDichVu);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private void setDichVuParameters(PreparedStatement stmt, DichVu dichVu) throws SQLException {
        stmt.setString(1, dichVu.getTenDichVu());
        stmt.setBigDecimal(2, dichVu.getGia());
        
        if (dichVu.getThoiGian() != null) {
            stmt.setInt(3, dichVu.getThoiGian());
        } else {
            stmt.setNull(3, Types.INTEGER);
        }
        
        if (dichVu.getMaLoaiDV() != null) {
            stmt.setInt(4, dichVu.getMaLoaiDV());
        } else {
            stmt.setNull(4, Types.INTEGER);
        }
        
        stmt.setString(5, dichVu.getGhiChu());
    }
    
    private DichVu mapResultSetToDichVu(ResultSet rs) throws SQLException {
        return new DichVu(
            rs.getInt("MaDichVu"),
            rs.getString("TenDichVu"),
            rs.getBigDecimal("Gia"),
            rs.getInt("ThoiGian"),
            rs.getInt("MaLoaiDV"),
            rs.getString("GhiChu")
        );
    }
}