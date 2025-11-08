// NguyenLieuRepository.java
package Repository;

import Data.DataConnection;
import Model.NguyenLieu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NguyenLieuRepository {
    
    public List<NguyenLieu> getAll() throws SQLException {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT nl.*, lnl.TenLoaiNL FROM NguyenLieu nl " +
                    "LEFT JOIN LoaiNguyenLieu lnl ON nl.MaLoaiNL = lnl.MaLoaiNL " +
                    "ORDER BY nl.TenNguyenLieu ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToNguyenLieu(rs));
            }
        }
        return list;
    }
    
    public NguyenLieu getById(int maNguyenLieu) throws SQLException {
        String sql = "SELECT nl.*, lnl.TenLoaiNL FROM NguyenLieu nl " +
                    "LEFT JOIN LoaiNguyenLieu lnl ON nl.MaLoaiNL = lnl.MaLoaiNL " +
                    "WHERE nl.MaNguyenLieu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNguyenLieu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNguyenLieu(rs);
                }
            }
        }
        return null;
    }
    
    public NguyenLieu getByTen(String tenNguyenLieu) throws SQLException {
        String sql = "SELECT nl.*, lnl.TenLoaiNL FROM NguyenLieu nl " +
                    "LEFT JOIN LoaiNguyenLieu lnl ON nl.MaLoaiNL = lnl.MaLoaiNL " +
                    "WHERE nl.TenNguyenLieu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tenNguyenLieu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNguyenLieu(rs);
                }
            }
        }
        return null;
    }
    
    public List<NguyenLieu> getByMaLoaiNL(int maLoaiNL) throws SQLException {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT nl.*, lnl.TenLoaiNL FROM NguyenLieu nl " +
                    "LEFT JOIN LoaiNguyenLieu lnl ON nl.MaLoaiNL = lnl.MaLoaiNL " +
                    "WHERE nl.MaLoaiNL = ? " +
                    "ORDER BY nl.TenNguyenLieu ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLoaiNL);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNguyenLieu(rs));
                }
            }
        }
        return list;
    }
    
    public List<NguyenLieu> getBySoLuongTon(int soLuongMin) throws SQLException {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT nl.*, lnl.TenLoaiNL FROM NguyenLieu nl " +
                    "LEFT JOIN LoaiNguyenLieu lnl ON nl.MaLoaiNL = lnl.MaLoaiNL " +
                    "WHERE nl.SoLuongTon <= ? " +
                    "ORDER BY nl.SoLuongTon ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, soLuongMin);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNguyenLieu(rs));
                }
            }
        }
        return list;
    }
    
    public List<NguyenLieu> searchByTen(String tenNguyenLieu) throws SQLException {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT nl.*, lnl.TenLoaiNL FROM NguyenLieu nl " +
                    "LEFT JOIN LoaiNguyenLieu lnl ON nl.MaLoaiNL = lnl.MaLoaiNL " +
                    "WHERE nl.TenNguyenLieu LIKE ? " +
                    "ORDER BY nl.TenNguyenLieu ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + tenNguyenLieu + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNguyenLieu(rs));
                }
            }
        }
        return list;
    }
    
    public List<NguyenLieu> getNguyenLieuSapHet() throws SQLException {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT nl.*, lnl.TenLoaiNL FROM NguyenLieu nl " +
                    "LEFT JOIN LoaiNguyenLieu lnl ON nl.MaLoaiNL = lnl.MaLoaiNL " +
                    "WHERE nl.SoLuongTon <= 10 " + // Ngưỡng cảnh báo
                    "ORDER BY nl.SoLuongTon ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToNguyenLieu(rs));
            }
        }
        return list;
    }
    
    public boolean insert(NguyenLieu nguyenLieu) throws SQLException {
        // Kiểm tra tên nguyên liệu đã tồn tại chưa
        if (isTenNguyenLieuExists(nguyenLieu.getTenNguyenLieu())) {
            throw new SQLException("Tên nguyên liệu đã tồn tại trong hệ thống.");
        }
        
        String sql = "INSERT INTO NguyenLieu (TenNguyenLieu, SoLuongTon, DonViTinh, MaLoaiNL) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setNguyenLieuParameters(stmt, nguyenLieu);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        nguyenLieu.setMaNguyenLieu(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public boolean update(NguyenLieu nguyenLieu) throws SQLException {
        // Kiểm tra tên nguyên liệu đã tồn tại chưa (trừ bản ghi hiện tại)
        if (isTenNguyenLieuExists(nguyenLieu.getTenNguyenLieu(), nguyenLieu.getMaNguyenLieu())) {
            throw new SQLException("Tên nguyên liệu đã tồn tại trong hệ thống.");
        }
        
        String sql = "UPDATE NguyenLieu SET TenNguyenLieu=?, SoLuongTon=?, DonViTinh=?, MaLoaiNL=? WHERE MaNguyenLieu=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setNguyenLieuParameters(stmt, nguyenLieu);
            stmt.setInt(5, nguyenLieu.getMaNguyenLieu());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maNguyenLieu) throws SQLException {
        // Kiểm tra xem nguyên liệu có đang được sử dụng không
        if (isNguyenLieuInUse(maNguyenLieu)) {
            throw new SQLException("Không thể xóa nguyên liệu vì đang được sử dụng trong công thức dịch vụ.");
        }
        
        String sql = "DELETE FROM NguyenLieu WHERE MaNguyenLieu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNguyenLieu);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateSoLuongTon(int maNguyenLieu, int soLuong) throws SQLException {
        String sql = "UPDATE NguyenLieu SET SoLuongTon = ? WHERE MaNguyenLieu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, soLuong);
            stmt.setInt(2, maNguyenLieu);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean tangSoLuongTon(int maNguyenLieu, int soLuongThem) throws SQLException {
        String sql = "UPDATE NguyenLieu SET SoLuongTon = SoLuongTon + ? WHERE MaNguyenLieu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, soLuongThem);
            stmt.setInt(2, maNguyenLieu);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean giamSoLuongTon(int maNguyenLieu, int soLuongGiam) throws SQLException {
        String sql = "UPDATE NguyenLieu SET SoLuongTon = SoLuongTon - ? WHERE MaNguyenLieu = ? AND SoLuongTon >= ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, soLuongGiam);
            stmt.setInt(2, maNguyenLieu);
            stmt.setInt(3, soLuongGiam);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean isTenNguyenLieuExists(String tenNguyenLieu) throws SQLException {
        return isTenNguyenLieuExists(tenNguyenLieu, 0);
    }
    
    public boolean isTenNguyenLieuExists(String tenNguyenLieu, int excludeMaNguyenLieu) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NguyenLieu WHERE TenNguyenLieu = ? AND MaNguyenLieu != ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tenNguyenLieu);
            stmt.setInt(2, excludeMaNguyenLieu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public boolean isNguyenLieuInUse(int maNguyenLieu) throws SQLException {
        String sql = "SELECT COUNT(*) FROM CongThucDichVu WHERE MaNguyenLieu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNguyenLieu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public int getTotalNguyenLieu() throws SQLException {
        String sql = "SELECT COUNT(*) FROM NguyenLieu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public int getTongSoLuongTon() throws SQLException {
        String sql = "SELECT SUM(SoLuongTon) FROM NguyenLieu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public List<NguyenLieu> getTopNguyenLieuItNhat(int soLuong) throws SQLException {
        List<NguyenLieu> list = new ArrayList<>();
        String sql = "SELECT TOP ? nl.*, lnl.TenLoaiNL FROM NguyenLieu nl " +
                    "LEFT JOIN LoaiNguyenLieu lnl ON nl.MaLoaiNL = lnl.MaLoaiNL " +
                    "ORDER BY nl.SoLuongTon ASC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, soLuong);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNguyenLieu(rs));
                }
            }
        }
        return list;
    }
    
    private void setNguyenLieuParameters(PreparedStatement stmt, NguyenLieu nguyenLieu) throws SQLException {
        stmt.setString(1, nguyenLieu.getTenNguyenLieu());
        stmt.setInt(2, nguyenLieu.getSoLuongTon());
        stmt.setString(3, nguyenLieu.getDonViTinh());
        
        if (nguyenLieu.getMaLoaiNL() != null) {
            stmt.setInt(4, nguyenLieu.getMaLoaiNL());
        } else {
            stmt.setNull(4, Types.INTEGER);
        }
    }
    
    private NguyenLieu mapResultSetToNguyenLieu(ResultSet rs) throws SQLException {
        NguyenLieu nguyenLieu = new NguyenLieu(
            rs.getInt("MaNguyenLieu"),
            rs.getString("TenNguyenLieu"),
            rs.getInt("SoLuongTon"),
            rs.getString("DonViTinh"),
            rs.getInt("MaLoaiNL")
        );
        
        // Xử lý các trường có thể null
        if (rs.wasNull()) {
            nguyenLieu.setMaLoaiNL(null);
        }
        
        // Thêm thông tin loại nguyên liệu nếu có
        try {
            String tenLoaiNL = rs.getString("TenLoaiNL");
            if (tenLoaiNL != null) {
                // Có thể set thêm thông tin loại nguyên liệu nếu model hỗ trợ
            }
        } catch (SQLException e) {
            // Bỏ qua nếu không có cột TenLoaiNL
        }
        
        return nguyenLieu;
    }
}