package Repository;

import Data.DataConnection;
import Model.Giuong;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiuongRepository {
    
    public List<Giuong> getAll() throws SQLException {
        List<Giuong> list = new ArrayList<>();
        String sql = "SELECT * FROM Giuong";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToGiuong(rs));
            }
        }
        return list;
    }
    
    public Giuong getById(int maGiuong) throws SQLException {
        String sql = "SELECT * FROM Giuong WHERE MaGiuong = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maGiuong);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGiuong(rs);
                }
            }
        }
        return null;
    }
    
    public boolean insert(Giuong giuong) throws SQLException {
        String sql = "INSERT INTO Giuong (SoHieu, TrangThai, GhiChu) VALUES (?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, giuong.getSoHieu());
            stmt.setString(2, giuong.getTrangThai());
            stmt.setString(3, giuong.getGhiChu());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        giuong.setMaGiuong(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(Giuong giuong) throws SQLException {
        String sql = "UPDATE Giuong SET SoHieu = ?, TrangThai = ?, GhiChu = ? WHERE MaGiuong = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, giuong.getSoHieu());
            stmt.setString(2, giuong.getTrangThai());
            stmt.setString(3, giuong.getGhiChu());
            stmt.setInt(4, giuong.getMaGiuong());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateTrangThai(int maGiuong, String trangThai) throws SQLException {
        String sql = "UPDATE Giuong SET TrangThai = ? WHERE MaGiuong = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, trangThai);
            stmt.setInt(2, maGiuong);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maGiuong) throws SQLException {
        String sql = "DELETE FROM Giuong WHERE MaGiuong = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maGiuong);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<Giuong> getByTrangThai(String trangThai) throws SQLException {
        List<Giuong> list = new ArrayList<>();
        String sql = "SELECT * FROM Giuong WHERE TrangThai = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, trangThai);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToGiuong(rs));
                }
            }
        }
        return list;
    }
    
    public List<Giuong> getGiuongTrong() throws SQLException {
        return getByTrangThai("Trống");
    }
    
    public List<Giuong> getGiuongDaDat() throws SQLException {
        return getByTrangThai("Đã đặt");
    }
    
    public List<Giuong> getGiuongDangSuDung() throws SQLException {
        return getByTrangThai("Đang sử dụng");
    }
    
    public List<Giuong> getGiuongBaoTri() throws SQLException {
        return getByTrangThai("Bảo trì");
    }
    
    private Giuong mapResultSetToGiuong(ResultSet rs) throws SQLException {
        return new Giuong(
            rs.getInt("MaGiuong"),
            rs.getString("SoHieu"),
            rs.getString("TrangThai"),
            rs.getString("GhiChu")  // Đổi từ MoTa thành GhiChu
        );
    }
}