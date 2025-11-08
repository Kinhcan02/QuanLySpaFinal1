package Repository;

import Data.DataConnection;
import Model.PhanTramDichVu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhanTramDichVuRepository {
    
    public List<PhanTramDichVu> getAll() throws SQLException {
        List<PhanTramDichVu> list = new ArrayList<>();
        String sql = "SELECT pt.*, ldv.TenLoaiDV, nv.HoTen " +
                    "FROM PhanTramDichVu pt " +
                    "LEFT JOIN LoaiDichVu ldv ON pt.MaLoaiDV = ldv.MaLoaiDV " +
                    "LEFT JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien " +
                    "ORDER BY ldv.TenLoaiDV, nv.HoTen";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToPhanTramDichVu(rs));
            }
        }
        return list;
    }
    
    public PhanTramDichVu getById(int maPhanTram) throws SQLException {
        String sql = "SELECT pt.*, ldv.TenLoaiDV, nv.HoTen " +
                    "FROM PhanTramDichVu pt " +
                    "LEFT JOIN LoaiDichVu ldv ON pt.MaLoaiDV = ldv.MaLoaiDV " +
                    "LEFT JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien " +
                    "WHERE pt.MaPhanTram = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maPhanTram);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhanTramDichVu(rs);
                }
            }
        }
        return null;
    }
    
    public List<PhanTramDichVu> getByNhanVien(int maNhanVien) throws SQLException {
        List<PhanTramDichVu> list = new ArrayList<>();
        String sql = "SELECT pt.*, ldv.TenLoaiDV, nv.HoTen " +
                    "FROM PhanTramDichVu pt " +
                    "LEFT JOIN LoaiDichVu ldv ON pt.MaLoaiDV = ldv.MaLoaiDV " +
                    "LEFT JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien " +
                    "WHERE pt.MaNhanVien = ? " +
                    "ORDER BY ldv.TenLoaiDV";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPhanTramDichVu(rs));
                }
            }
        }
        return list;
    }
    
    public List<PhanTramDichVu> getByLoaiDichVu(int maLoaiDV) throws SQLException {
        List<PhanTramDichVu> list = new ArrayList<>();
        String sql = "SELECT pt.*, ldv.TenLoaiDV, nv.HoTen " +
                    "FROM PhanTramDichVu pt " +
                    "LEFT JOIN LoaiDichVu ldv ON pt.MaLoaiDV = ldv.MaLoaiDV " +
                    "LEFT JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien " +
                    "WHERE pt.MaLoaiDV = ? " +
                    "ORDER BY nv.HoTen";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiDV);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPhanTramDichVu(rs));
                }
            }
        }
        return list;
    }
    
    public PhanTramDichVu getByNhanVienAndLoaiDV(int maNhanVien, int maLoaiDV) throws SQLException {
        String sql = "SELECT pt.*, ldv.TenLoaiDV, nv.HoTen " +
                    "FROM PhanTramDichVu pt " +
                    "LEFT JOIN LoaiDichVu ldv ON pt.MaLoaiDV = ldv.MaLoaiDV " +
                    "LEFT JOIN NhanVien nv ON pt.MaNhanVien = nv.MaNhanVien " +
                    "WHERE pt.MaNhanVien = ? AND pt.MaLoaiDV = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, maLoaiDV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhanTramDichVu(rs);
                }
            }
        }
        return null;
    }
    
    public boolean insert(PhanTramDichVu phanTram) throws SQLException {
        String sql = "INSERT INTO PhanTramDichVu (MaLoaiDV, MaNhanVien, TiLePhanTram) VALUES (?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, phanTram.getMaLoaiDV());
            stmt.setInt(2, phanTram.getMaNhanVien());
            stmt.setDouble(3, phanTram.getTiLePhanTram());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    public boolean update(PhanTramDichVu phanTram) throws SQLException {
        String sql = "UPDATE PhanTramDichVu SET MaLoaiDV=?, MaNhanVien=?, TiLePhanTram=? WHERE MaPhanTram=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, phanTram.getMaLoaiDV());
            stmt.setInt(2, phanTram.getMaNhanVien());
            stmt.setDouble(3, phanTram.getTiLePhanTram());
            stmt.setInt(4, phanTram.getMaPhanTram());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maPhanTram) throws SQLException {
        String sql = "DELETE FROM PhanTramDichVu WHERE MaPhanTram = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maPhanTram);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean exists(int maNhanVien, int maLoaiDV) throws SQLException {
        String sql = "SELECT COUNT(*) FROM PhanTramDichVu WHERE MaNhanVien = ? AND MaLoaiDV = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, maLoaiDV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    // Phương thức để lấy ID vừa insert trong Access
    public int getLastInsertId() throws SQLException {
        String sql = "SELECT @@IDENTITY AS LastID";
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("LastID");
            }
        }
        return 0;
    }
    
    private PhanTramDichVu mapResultSetToPhanTramDichVu(ResultSet rs) throws SQLException {
        PhanTramDichVu phanTram = new PhanTramDichVu();
        phanTram.setMaPhanTram(rs.getInt("MaPhanTram"));
        phanTram.setMaLoaiDV(rs.getInt("MaLoaiDV"));
        phanTram.setMaNhanVien(rs.getInt("MaNhanVien"));
        phanTram.setTiLePhanTram(rs.getDouble("TiLePhanTram"));
        
        return phanTram;
    }
}