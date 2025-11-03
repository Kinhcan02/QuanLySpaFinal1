package Repository;

import Data.DataConnection;
import Model.CaLam;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaLamRepository {
    
    public List<CaLam> getAll() throws SQLException {
        List<CaLam> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLam ORDER BY NgayLam DESC, GioBatDau ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToCaLam(rs));
            }
        }
        return list;
    }
    
    public CaLam getById(int maCa) throws SQLException {
        String sql = "SELECT * FROM CaLam WHERE MaCa = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maCa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCaLam(rs);
                }
            }
        }
        return null;
    }
    
    public List<CaLam> getByMaNhanVien(int maNhanVien) throws SQLException {
        List<CaLam> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLam WHERE MaNhanVien = ? ORDER BY NgayLam DESC, GioBatDau ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCaLam(rs));
                }
            }
        }
        return list;
    }
    
    public List<CaLam> getByNgay(Date ngayLam) throws SQLException {
        List<CaLam> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLam WHERE NgayLam = ? ORDER BY GioBatDau ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, ngayLam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCaLam(rs));
                }
            }
        }
        return list;
    }
    
    public List<CaLam> getByThangNam(int thang, int nam) throws SQLException {
        List<CaLam> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLam WHERE MONTH(NgayLam) = ? AND YEAR(NgayLam) = ? ORDER BY NgayLam ASC, GioBatDau ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCaLam(rs));
                }
            }
        }
        return list;
    }
    
    public boolean insert(CaLam caLam) throws SQLException {
        String sql = "INSERT INTO CaLam (MaNhanVien, NgayLam, GioBatDau, GioKetThuc, SoGioLam, SoGioTangCa, SoLuongKhachPhucVu, TienTip) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setCaLamParameters(stmt, caLam);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        caLam.setMaCa(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(CaLam caLam) throws SQLException {
        String sql = "UPDATE CaLam SET MaNhanVien=?, NgayLam=?, GioBatDau=?, GioKetThuc=?, SoGioLam=?, " +
                    "SoGioTangCa=?, SoLuongKhachPhucVu=?, TienTip=? WHERE MaCa=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setCaLamParameters(stmt, caLam);
            stmt.setInt(9, caLam.getMaCa());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateTienTip(int maCa, BigDecimal tienTip) throws SQLException {
        String sql = "UPDATE CaLam SET TienTip = ?, NgayCapNhat = GETDATE() WHERE MaCa = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, tienTip);
            stmt.setInt(2, maCa);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maCa) throws SQLException {
        String sql = "DELETE FROM CaLam WHERE MaCa = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maCa);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean exists(int maNhanVien, Date ngayLam, Time gioBatDau, Time gioKetThuc) throws SQLException {
        String sql = "SELECT COUNT(*) FROM CaLam WHERE MaNhanVien = ? AND NgayLam = ? AND " +
                    "((GioBatDau <= ? AND GioKetThuc >= ?) OR (GioBatDau <= ? AND GioKetThuc >= ?))";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setDate(2, ngayLam);
            stmt.setTime(3, gioBatDau);
            stmt.setTime(4, gioBatDau);
            stmt.setTime(5, gioKetThuc);
            stmt.setTime(6, gioKetThuc);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    private void setCaLamParameters(PreparedStatement stmt, CaLam caLam) throws SQLException {
        stmt.setInt(1, caLam.getMaNhanVien());
        stmt.setDate(2, Date.valueOf(caLam.getNgayLam()));
        stmt.setTime(3, Time.valueOf(caLam.getGioBatDau()));
        stmt.setTime(4, Time.valueOf(caLam.getGioKetThuc()));
        stmt.setBigDecimal(5, caLam.getSoGioLam());
        stmt.setBigDecimal(6, caLam.getSoGioTangCa());
        stmt.setInt(7, caLam.getSoLuongKhachPhucVu());
        stmt.setBigDecimal(8, caLam.getTienTip());
    }
    
    private CaLam mapResultSetToCaLam(ResultSet rs) throws SQLException {
        CaLam caLam = new CaLam(
            rs.getInt("MaCa"),
            rs.getInt("MaNhanVien"),
            rs.getDate("NgayLam").toLocalDate(),
            rs.getTime("GioBatDau").toLocalTime(),
            rs.getTime("GioKetThuc").toLocalTime(),
            rs.getBigDecimal("SoGioLam"),
            rs.getBigDecimal("SoGioTangCa"),
            rs.getInt("SoLuongKhachPhucVu"),
            rs.getBigDecimal("TienTip"),
            rs.getTimestamp("NgayTao").toLocalDateTime(),
            rs.getTimestamp("NgayCapNhat") != null ? 
                rs.getTimestamp("NgayCapNhat").toLocalDateTime() : null
        );
        
        return caLam;
    }
}