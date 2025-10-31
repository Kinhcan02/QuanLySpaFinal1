package Repository;

import Data.DataConnection;
import Model.KhachHang;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KhachHangRepository {
    private Connection connection;
    private static final Logger logger = Logger.getLogger(KhachHangRepository.class.getName());

    public KhachHangRepository() {
        this.connection = DataConnection.getConnection();
        // Đảm bảo auto-commit là true
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
        }
    }

    public List<KhachHang> getAll() throws SQLException {
        String sql = "SELECT * FROM KhachHang ORDER BY MaKhachHang DESC";
        List<KhachHang> danhSach = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                danhSach.add(mapResultSetToKhachHang(rs));
            }
        }
        return danhSach;
    }

    public KhachHang getById(int maKhachHang) throws SQLException {
        String sql = "SELECT * FROM KhachHang WHERE MaKhachHang = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, maKhachHang);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKhachHang(rs);
                }
            }
        }
        return null;
    }

    public KhachHang getBySoDienThoai(String soDienThoai) throws SQLException {
        String sql = "SELECT * FROM KhachHang WHERE SoDienThoai = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, soDienThoai);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKhachHang(rs);
                }
            }
        }
        return null;
    }

    public List<KhachHang> searchByHoTen(String hoTen) throws SQLException {
        String sql = "SELECT * FROM KhachHang WHERE HoTen LIKE ? ORDER BY MaKhachHang DESC";
        List<KhachHang> danhSach = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + hoTen + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToKhachHang(rs));
                }
            }
        }
        return danhSach;
    }

    public List<KhachHang> getByLoaiKhach(String loaiKhach) throws SQLException {
        String sql = "SELECT * FROM KhachHang WHERE LoaiKhach = ? ORDER BY MaKhachHang DESC";
        List<KhachHang> danhSach = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loaiKhach);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToKhachHang(rs));
                }
            }
        }
        return danhSach;
    }

    public boolean insert(KhachHang khachHang) throws SQLException {
        String sql = "INSERT INTO KhachHang (HoTen, NgaySinh, LoaiKhach, SoDienThoai, GhiChu, DiemTichLuy) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, khachHang.getHoTen());
            
            if (khachHang.getNgaySinh() != null) {
                stmt.setDate(2, Date.valueOf(khachHang.getNgaySinh()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            stmt.setString(3, khachHang.getLoaiKhach());
            stmt.setString(4, khachHang.getSoDienThoai());
            stmt.setString(5, khachHang.getGhiChu());
            stmt.setInt(6, khachHang.getDiemTichLuy());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Lấy mã khách hàng tự sinh
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        khachHang.setMaKhachHang(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean update(KhachHang khachHang) throws SQLException {
        String sql = "UPDATE KhachHang SET HoTen = ?, NgaySinh = ?, LoaiKhach = ?, " +
                    "SoDienThoai = ?, GhiChu = ?, DiemTichLuy = ? " +
                    "WHERE MaKhachHang = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, khachHang.getHoTen());
            
            if (khachHang.getNgaySinh() != null) {
                stmt.setDate(2, Date.valueOf(khachHang.getNgaySinh()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            stmt.setString(3, khachHang.getLoaiKhach());
            stmt.setString(4, khachHang.getSoDienThoai());
            stmt.setString(5, khachHang.getGhiChu());
            stmt.setInt(6, khachHang.getDiemTichLuy());
            stmt.setInt(7, khachHang.getMaKhachHang());
            
            int rowsAffected = stmt.executeUpdate();
            
            // Đảm bảo changes được commit
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
            
            return rowsAffected > 0;
        }
    }

    public boolean delete(int maKhachHang) throws SQLException {
        // Kiểm tra xem khách hàng có tồn tại không
        KhachHang khachHang = getById(maKhachHang);
        if (khachHang == null) {
            return false;
        }
        
        try {
            String sql = "DELETE FROM KhachHang WHERE MaKhachHang = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, maKhachHang);
                
                int rowsAffected = stmt.executeUpdate();
                
                return rowsAffected > 0;
            }
            
        } catch (SQLException e) {
            // Xử lý lỗi ràng buộc khóa ngoại
            if (e.getErrorCode() == 547 || e.getSQLState().equals("23000") || 
                e.getMessage().contains("foreign key constraint")) {
                throw new SQLException("Không thể xóa khách hàng vì có dữ liệu liên quan trong hệ thống. Hãy xóa các dữ liệu liên quan trước.", e);
            } else {
                throw e;
            }
        }
    }

    // Phương thức kiểm tra dữ liệu liên quan
    public Map<String, Integer> kiemTraDuLieuLienQuan(int maKhachHang) throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        
        String[] checkSqls = {
            "HoaDon", "SELECT COUNT(*) FROM HoaDon WHERE MaKhachHang = ?",
            "DatLich", "SELECT COUNT(*) FROM DatLich WHERE MaKhachHang = ?", 
            "SuDungDichVu", "SELECT COUNT(*) FROM SuDungDichVu WHERE MaKhachHang = ?"
        };
        
        for (int i = 0; i < checkSqls.length; i += 2) {
            String tableName = checkSqls[i];
            String sql = checkSqls[i + 1];
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, maKhachHang);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        if (count > 0) {
                            result.put(tableName, count);
                        }
                    }
                }
            }
        }
        
        return result;
    }

    private KhachHang mapResultSetToKhachHang(ResultSet rs) throws SQLException {
        KhachHang khachHang = new KhachHang();
        khachHang.setMaKhachHang(rs.getInt("MaKhachHang"));
        khachHang.setHoTen(rs.getString("HoTen"));
        
        Date ngaySinh = rs.getDate("NgaySinh");
        if (ngaySinh != null) {
            khachHang.setNgaySinh(ngaySinh.toLocalDate());
        }
        
        khachHang.setLoaiKhach(rs.getString("LoaiKhach"));
        khachHang.setSoDienThoai(rs.getString("SoDienThoai"));
        khachHang.setGhiChu(rs.getString("GhiChu"));
        
        Timestamp ngayTao = rs.getTimestamp("NgayTao");
        if (ngayTao != null) {
            khachHang.setNgayTao(ngayTao.toLocalDateTime());
        }
        
        khachHang.setDiemTichLuy(rs.getInt("DiemTichLuy"));
        
        return khachHang;
    }
}