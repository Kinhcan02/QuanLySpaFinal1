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
        String sql = "SELECT * FROM SuDungDichVu";
        
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
        String sql = "SELECT * FROM SuDungDichVu WHERE MaSuDung = ?";
        
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
        String sql = "SELECT * FROM SuDungDichVu WHERE MaKhachHang = ?";
        
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
        String sql = "SELECT * FROM SuDungDichVu WHERE MaNhanVien = ?";
        
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
    
    public List<SuDungDichVu> getByDateRange(Date fromDate, Date toDate) throws SQLException {
        List<SuDungDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM SuDungDichVu WHERE CAST(NgaySuDung AS DATE) BETWEEN ? AND ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);
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
        return new SuDungDichVu(
            rs.getInt("MaSuDung"),
            rs.getInt("MaKhachHang"),
            rs.getInt("MaDichVu"),
            rs.getInt("MaNhanVien"),
            rs.getTimestamp("NgaySuDung").toLocalDateTime(),
            rs.getBigDecimal("SoTien"),
            rs.getBigDecimal("TienTip")
        );
    }
}