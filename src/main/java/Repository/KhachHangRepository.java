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
    private boolean hasNgayCapNhatColumn = false;

    public KhachHangRepository() {
        this.connection = DataConnection.getConnection();
        // Kiểm tra xem cột NgayCapNhat có tồn tại không
        
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi thiết lập auto-commit", e);
        }
    }



    public List<KhachHang> getAll() throws SQLException {
        // Sửa câu SQL để không select NgayCapNhat nếu không tồn tại
        String sql = hasNgayCapNhatColumn 
            ? "SELECT * FROM KhachHang ORDER BY MaKhachHang DESC"
            : "SELECT MaKhachHang, HoTen, NgaySinh, LoaiKhach, SoDienThoai, GhiChu, NgayTao, DiemTichLuy FROM KhachHang ORDER BY MaKhachHang DESC";
        
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
        String sql = hasNgayCapNhatColumn 
            ? "SELECT * FROM KhachHang WHERE MaKhachHang = ?"
            : "SELECT MaKhachHang, HoTen, NgaySinh, LoaiKhach, SoDienThoai, GhiChu, NgayTao, DiemTichLuy FROM KhachHang WHERE MaKhachHang = ?";
        
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
        String sql = hasNgayCapNhatColumn 
            ? "SELECT * FROM KhachHang WHERE SoDienThoai = ?"
            : "SELECT MaKhachHang, HoTen, NgaySinh, LoaiKhach, SoDienThoai, GhiChu, NgayTao, DiemTichLuy FROM KhachHang WHERE SoDienThoai = ?";
        
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
        String sql = hasNgayCapNhatColumn 
            ? "SELECT * FROM KhachHang WHERE HoTen LIKE ? ORDER BY MaKhachHang DESC"
            : "SELECT MaKhachHang, HoTen, NgaySinh, LoaiKhach, SoDienThoai, GhiChu, NgayTao, DiemTichLuy FROM KhachHang WHERE HoTen LIKE ? ORDER BY MaKhachHang DESC";
        
        List<KhachHang> danhSach = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "*" + hoTen + "*");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToKhachHang(rs));
                }
            }
        }
        return danhSach;
    }

    public List<KhachHang> getByLoaiKhach(String loaiKhach) throws SQLException {
        String sql = hasNgayCapNhatColumn 
            ? "SELECT * FROM KhachHang WHERE LoaiKhach = ? ORDER BY MaKhachHang DESC"
            : "SELECT MaKhachHang, HoTen, NgaySinh, LoaiKhach, SoDienThoai, GhiChu, NgayTao, DiemTichLuy FROM KhachHang WHERE LoaiKhach = ? ORDER BY MaKhachHang DESC";
        
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
        // Sửa câu INSERT để không insert NgayCapNhat nếu không tồn tại
        String sql = hasNgayCapNhatColumn 
            ? "INSERT INTO KhachHang (HoTen, NgaySinh, LoaiKhach, SoDienThoai, GhiChu, DiemTichLuy, NgayTao) VALUES (?, ?, ?, ?, ?, ?, ?)"
            : "INSERT INTO KhachHang (HoTen, NgaySinh, LoaiKhach, SoDienThoai, GhiChu, DiemTichLuy, NgayTao) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, khachHang.getHoTen());
            
            if (khachHang.getNgaySinh() != null) {
                stmt.setDate(2, new Date(khachHang.getNgaySinh().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            stmt.setString(3, khachHang.getLoaiKhach());
            stmt.setString(4, khachHang.getSoDienThoai());
            stmt.setString(5, khachHang.getGhiChu());
            stmt.setInt(6, khachHang.getDiemTichLuy());
            
            Timestamp ngayTao = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(7, ngayTao);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                khachHang.setMaKhachHang(getLastInsertId());
                return true;
            }
            return false;
        }
    }

    public boolean update(KhachHang khachHang) throws SQLException {
        // Sửa câu UPDATE để không update NgayCapNhat nếu không tồn tại
        String sql = hasNgayCapNhatColumn 
            ? "UPDATE KhachHang SET HoTen = ?, NgaySinh = ?, LoaiKhach = ?, SoDienThoai = ?, GhiChu = ?, DiemTichLuy = ?, NgayCapNhat = ? WHERE MaKhachHang = ?"
            : "UPDATE KhachHang SET HoTen = ?, NgaySinh = ?, LoaiKhach = ?, SoDienThoai = ?, GhiChu = ?, DiemTichLuy = ? WHERE MaKhachHang = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            stmt.setString(paramIndex++, khachHang.getHoTen());
            
            if (khachHang.getNgaySinh() != null) {
                stmt.setDate(paramIndex++, new Date(khachHang.getNgaySinh().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()));
            } else {
                stmt.setNull(paramIndex++, Types.DATE);
            }
            
            stmt.setString(paramIndex++, khachHang.getLoaiKhach());
            stmt.setString(paramIndex++, khachHang.getSoDienThoai());
            stmt.setString(paramIndex++, khachHang.getGhiChu());
            stmt.setInt(paramIndex++, khachHang.getDiemTichLuy());
            
            if (hasNgayCapNhatColumn) {
                Timestamp ngayCapNhat = new Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(paramIndex++, ngayCapNhat);
            }
            
            stmt.setInt(paramIndex, khachHang.getMaKhachHang());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean delete(int maKhachHang) throws SQLException {
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
            if (e.getMessage().toLowerCase().contains("constraint") || 
                e.getMessage().toLowerCase().contains("foreign") ||
                e.getMessage().toLowerCase().contains("related") ||
                e.getMessage().toLowerCase().contains("violation")) {
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
            "DatLich", "SELECT COUNT(*) FROM DatLich WHERE MaKhachHang = ?"
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

    public boolean updateDiemTichLuy(int maKhachHang, int diemTichLuy) throws SQLException {
        String sql = hasNgayCapNhatColumn 
            ? "UPDATE KhachHang SET DiemTichLuy = ?, NgayCapNhat = ? WHERE MaKhachHang = ?"
            : "UPDATE KhachHang SET DiemTichLuy = ? WHERE MaKhachHang = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            stmt.setInt(paramIndex++, diemTichLuy);
            
            if (hasNgayCapNhatColumn) {
                Timestamp ngayCapNhat = new Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(paramIndex++, ngayCapNhat);
            }
            
            stmt.setInt(paramIndex, maKhachHang);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public int getTongSoKhachHang() throws SQLException {
        String sql = "SELECT COUNT(*) FROM KhachHang";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<KhachHang> getTopKhachHangTheoDiem(int soLuong) throws SQLException {
        String sql = hasNgayCapNhatColumn 
            ? "SELECT TOP " + soLuong + " * FROM KhachHang ORDER BY DiemTichLuy DESC"
            : "SELECT TOP " + soLuong + " MaKhachHang, HoTen, NgaySinh, LoaiKhach, SoDienThoai, GhiChu, NgayTao, DiemTichLuy FROM KhachHang ORDER BY DiemTichLuy DESC";
        
        List<KhachHang> danhSach = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToKhachHang(rs));
                }
            }
        }
        return danhSach;
    }

    // Phương thức để lấy ID vừa insert trong Access
    private int getLastInsertId() throws SQLException {
        String sql = "SELECT @@IDENTITY AS NewID";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("NewID");
            }
        }
        return -1;
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
        
        // Chỉ lấy NgayCapNhat nếu cột tồn tại
        if (hasNgayCapNhatColumn) {
            try {
                Timestamp ngayCapNhat = rs.getTimestamp("NgayCapNhat");
                if (ngayCapNhat != null) {
                    khachHang.setNgayCapNhat(ngayCapNhat.toLocalDateTime());
                }
            } catch (SQLException e) {
                // Bỏ qua lỗi nếu cột không tồn tại
                
            }
        }
        
        khachHang.setDiemTichLuy(rs.getInt("DiemTichLuy"));
        
        return khachHang;
    }
}