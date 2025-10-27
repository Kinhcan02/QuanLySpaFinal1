// DatLichRepository.java
package Repository;

import Data.DataConnection;
import Model.DatLich;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatLichRepository {
    
    public List<DatLich> getAll() throws SQLException {
        List<DatLich> list = new ArrayList<>();
        String sql = "SELECT * FROM DatLich";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToDatLich(rs));
            }
        }
        return list;
    }
    
    public DatLich getById(int maLich) throws SQLException {
        String sql = "SELECT * FROM DatLich WHERE MaLich = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLich);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDatLich(rs);
                }
            }
        }
        return null;
    }
    
    public List<DatLich> getByMaKhachHang(int maKhachHang) throws SQLException {
        List<DatLich> list = new ArrayList<>();
        String sql = "SELECT * FROM DatLich WHERE MaKhachHang = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maKhachHang);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDatLich(rs));
                }
            }
        }
        return list;
    }
    
    public List<DatLich> getByTrangThai(String trangThai) throws SQLException {
        List<DatLich> list = new ArrayList<>();
        String sql = "SELECT * FROM DatLich WHERE TrangThai = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, trangThai);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDatLich(rs));
                }
            }
        }
        return list;
    }
    
    public boolean insert(DatLich datLich) throws SQLException {
        String sql = "INSERT INTO DatLich (MaKhachHang, NgayDat, GioDat, MaDichVu, TrangThai, MaGiuong, GhiChu) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setDatLichParameters(stmt, datLich);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        datLich.setMaLich(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(DatLich datLich) throws SQLException {
        String sql = "UPDATE DatLich SET MaKhachHang=?, NgayDat=?, GioDat=?, MaDichVu=?, " +
                    "TrangThai=?, MaGiuong=?, GhiChu=? WHERE MaLich=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setDatLichParameters(stmt, datLich);
            stmt.setInt(8, datLich.getMaLich());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maLich) throws SQLException {
        String sql = "DELETE FROM DatLich WHERE MaLich = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLich);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateTrangThai(int maLich, String trangThai) throws SQLException {
        String sql = "UPDATE DatLich SET TrangThai = ? WHERE MaLich = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, trangThai);
            stmt.setInt(2, maLich);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    private void setDatLichParameters(PreparedStatement stmt, DatLich datLich) throws SQLException {
        stmt.setInt(1, datLich.getMaKhachHang());
        stmt.setDate(2, Date.valueOf(datLich.getNgayDat()));
        stmt.setTime(3, Time.valueOf(datLich.getGioDat()));
        
        if (datLich.getMaDichVu() != null) {
            stmt.setInt(4, datLich.getMaDichVu());
        } else {
            stmt.setNull(4, Types.INTEGER);
        }
        
        stmt.setString(5, datLich.getTrangThai());
        
        if (datLich.getMaGiuong() != null) {
            stmt.setInt(6, datLich.getMaGiuong());
        } else {
            stmt.setNull(6, Types.INTEGER);
        }
        
        stmt.setString(7, datLich.getGhiChu());
    }
    
    private DatLich mapResultSetToDatLich(ResultSet rs) throws SQLException {
        return new DatLich(
            rs.getInt("MaLich"),
            rs.getInt("MaKhachHang"),
            rs.getDate("NgayDat").toLocalDate(),
            rs.getTime("GioDat").toLocalTime(),
            rs.getInt("MaDichVu"),
            rs.getString("TrangThai"),
            rs.getInt("MaGiuong"),
            rs.getString("GhiChu")
        );
    }
}