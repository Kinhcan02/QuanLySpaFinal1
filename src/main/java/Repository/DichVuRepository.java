package Repository;

import Data.DataConnection;
import Model.DichVu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DichVuRepository {
    
    public List<DichVu> getAll() throws SQLException {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu ORDER BY TenDichVu";
        
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
        String sql = "SELECT * FROM DichVu WHERE MaLoaiDV = ? ORDER BY TenDichVu";
        
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
        // Access sử dụng ALIKE hoặc LIKE với *
        String sql = "SELECT * FROM DichVu WHERE TenDichVu LIKE ? ORDER BY TenDichVu";
        
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
    
    public List<DichVu> getDichVuDangHoatDong() throws SQLException {
        List<DichVu> list = new ArrayList<>();
        // Thêm phương thức để lấy dịch vụ đang hoạt động (nếu có trạng thái)
        String sql = "SELECT * FROM DichVu WHERE TrangThai IS NULL OR TrangThai = 1 ORDER BY TenDichVu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToDichVu(rs));
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
        // Kiểm tra xem dịch vụ có đang được sử dụng không trước khi xóa
        if (isDichVuInUse(maDichVu)) {
            throw new SQLException("Không thể xóa dịch vụ vì đang được sử dụng trong các đơn đặt lịch");
        }
        
        String sql = "DELETE FROM DichVu WHERE MaDichVu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maDichVu);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean isDichVuInUse(int maDichVu) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DatLich_ChiTiet WHERE MaDichVu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maDichVu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public boolean updateGiaDichVu(int maDichVu, java.math.BigDecimal giaMoi) throws SQLException {
        String sql = "UPDATE DichVu SET Gia = ? WHERE MaDichVu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, giaMoi);
            stmt.setInt(2, maDichVu);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    private void setDichVuParameters(PreparedStatement stmt, DichVu dichVu) throws SQLException {
        stmt.setString(1, dichVu.getTenDichVu());
        stmt.setBigDecimal(2, dichVu.getGia());
        
        // Xử lý giá trị null cho ThoiGian
        if (dichVu.getThoiGian() != null) {
            stmt.setInt(3, dichVu.getThoiGian());
        } else {
            stmt.setNull(3, Types.INTEGER);
        }
        
        // Xử lý giá trị null cho MaLoaiDV
        if (dichVu.getMaLoaiDV() != null) {
            stmt.setInt(4, dichVu.getMaLoaiDV());
        } else {
            stmt.setNull(4, Types.INTEGER);
        }
        
        // Xử lý giá trị null cho GhiChu
        if (dichVu.getGhiChu() != null) {
            stmt.setString(5, dichVu.getGhiChu());
        } else {
            stmt.setNull(5, Types.VARCHAR);
        }
    }
    
    private DichVu mapResultSetToDichVu(ResultSet rs) throws SQLException {
        DichVu dichVu = new DichVu();
        dichVu.setMaDichVu(rs.getInt("MaDichVu"));
        dichVu.setTenDichVu(rs.getString("TenDichVu"));
        dichVu.setGia(rs.getBigDecimal("Gia"));
        
        // Xử lý giá trị null cho ThoiGian
        int thoiGian = rs.getInt("ThoiGian");
        if (!rs.wasNull()) {
            dichVu.setThoiGian(thoiGian);
        }
        
        // Xử lý giá trị null cho MaLoaiDV
        int maLoaiDV = rs.getInt("MaLoaiDV");
        if (!rs.wasNull()) {
            dichVu.setMaLoaiDV(maLoaiDV);
        }
        
        // Xử lý giá trị null cho GhiChu
        String ghiChu = rs.getString("GhiChu");
        if (!rs.wasNull()) {
            dichVu.setGhiChu(ghiChu);
        }
        
        return dichVu;
    }
    
    // Phương thức để lấy tổng số dịch vụ
    public int getTongSoDichVu() throws SQLException {
        String sql = "SELECT COUNT(*) FROM DichVu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    // Phương thức để lấy dịch vụ có giá trong khoảng
    public List<DichVu> getDichVuTheoGia(java.math.BigDecimal giaTu, java.math.BigDecimal giaDen) throws SQLException {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu WHERE Gia BETWEEN ? AND ? ORDER BY Gia, TenDichVu";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, giaTu);
            stmt.setBigDecimal(2, giaDen);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDichVu(rs));
                }
            }
        }
        return list;
    }
}