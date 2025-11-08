package Repository;

import Data.DataConnection;
import Model.LuongNhanVien;
import Model.NhanVien;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LuongNhanVienRepository {
    
    public List<LuongNhanVien> getAll() {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen, n.LuongCanBan FROM TinhLuongNhanVien l " +
                    "LEFT JOIN NhanVien n ON l.MaNhanVien = n.MaNhanVien " +
                    "ORDER BY l.Nam DESC, l.Thang DESC, n.HoTen";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LuongNhanVien luong = mapResultSetToLuongNhanVien(rs);
                list.add(luong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<LuongNhanVien> getByThangNam(Integer thang, Integer nam) {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen, n.LuongCanBan FROM TinhLuongNhanVien l " +
                    "LEFT JOIN NhanVien n ON l.MaNhanVien = n.MaNhanVien " +
                    "WHERE l.Thang = ? AND l.Nam = ? " +
                    "ORDER BY n.HoTen";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LuongNhanVien luong = mapResultSetToLuongNhanVien(rs);
                list.add(luong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<LuongNhanVien> getByMaNhanVienThangNam(Integer maNhanVien, Integer thang, Integer nam) {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen, n.LuongCanBan FROM TinhLuongNhanVien l " +
                    "LEFT JOIN NhanVien n ON l.MaNhanVien = n.MaNhanVien " +
                    "WHERE l.MaNhanVien = ? AND l.Thang = ? AND l.Nam = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LuongNhanVien luong = mapResultSetToLuongNhanVien(rs);
                list.add(luong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean insert(LuongNhanVien luong) {
        String sql = "INSERT INTO TinhLuongNhanVien (MaNhanVien, Thang, Nam, LuongCanBan, TongTienDichVu, TongLuong, NgayTinhLuong, TrangThai, GhiChu) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, luong.getMaNhanVien());
            stmt.setInt(2, luong.getThang());
            stmt.setInt(3, luong.getNam());
            stmt.setBigDecimal(4, luong.getLuongCanBan());
            stmt.setBigDecimal(5, luong.getTongTienDichVu());
            stmt.setBigDecimal(6, luong.getTongLuong());
            
            if (luong.getNgayTinhLuong() != null) {
                stmt.setTimestamp(7, Timestamp.valueOf(luong.getNgayTinhLuong()));
            } else {
                stmt.setNull(7, Types.TIMESTAMP);
            }
            
            stmt.setString(8, luong.getTrangThai());
            stmt.setString(9, luong.getGhiChu());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean update(LuongNhanVien luong) {
        String sql = "UPDATE TinhLuongNhanVien SET LuongCanBan = ?, TongTienDichVu = ?, TongLuong = ?, " +
                    "NgayTinhLuong = ?, TrangThai = ?, GhiChu = ? " +
                    "WHERE MaTinhLuong = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, luong.getLuongCanBan());
            stmt.setBigDecimal(2, luong.getTongTienDichVu());
            stmt.setBigDecimal(3, luong.getTongLuong());
            
            if (luong.getNgayTinhLuong() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(luong.getNgayTinhLuong()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            
            stmt.setString(5, luong.getTrangThai());
            stmt.setString(6, luong.getGhiChu());
            stmt.setInt(7, luong.getMaLuong());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateTrangThai(Integer maLuong, String trangThai) {
        String sql = "UPDATE TinhLuongNhanVien SET TrangThai = ? WHERE MaTinhLuong = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, trangThai);
            stmt.setInt(2, maLuong);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public BigDecimal tinhTongThanhTienTheoThangNam(Integer maNhanVien, Integer thang, Integer nam) {
        String sql = "SELECT SUM(ctdv.DonGiaThucTe * ctdv.SoLuong) as TongThanhTien " +
                    "FROM ChiTietTienDichVuCuaNhanVien ctdv " +
                    "INNER JOIN ChiTietHoaDon cthd ON ctdv.MaCTHD = cthd.MaCTHD " +
                    "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                    "WHERE ctdv.MaNhanVien = ? AND MONTH(hd.NgayLap) = ? AND YEAR(hd.NgayLap) = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal("TongThanhTien");
                return result != null ? result : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    public boolean exists(Integer maNhanVien, Integer thang, Integer nam) {
        String sql = "SELECT COUNT(*) FROM TinhLuongNhanVien WHERE MaNhanVien = ? AND Thang = ? AND Nam = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean delete(Integer maLuong) {
        String sql = "DELETE FROM TinhLuongNhanVien WHERE MaTinhLuong = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLuong);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private LuongNhanVien mapResultSetToLuongNhanVien(ResultSet rs) throws SQLException {
        LuongNhanVien luong = new LuongNhanVien();
        luong.setMaLuong(rs.getInt("MaTinhLuong"));
        luong.setMaNhanVien(rs.getInt("MaNhanVien"));
        luong.setThang(rs.getInt("Thang"));
        luong.setNam(rs.getInt("Nam"));
        luong.setLuongCanBan(rs.getBigDecimal("LuongCanBan"));
        luong.setTongTienDichVu(rs.getBigDecimal("TongTienDichVu"));
        luong.setTongLuong(rs.getBigDecimal("TongLuong"));
        
        Timestamp ngayTinh = rs.getTimestamp("NgayTinhLuong");
        if (ngayTinh != null) {
            luong.setNgayTinhLuong(ngayTinh.toLocalDateTime());
        }
        
        luong.setTrangThai(rs.getString("TrangThai"));
        luong.setGhiChu(rs.getString("GhiChu"));
        
        // Tạo đối tượng nhân viên tham chiếu
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(rs.getInt("MaNhanVien"));
        nv.setHoTen(rs.getString("HoTen"));
        nv.setLuongCanBan(rs.getBigDecimal("LuongCanBan"));
        luong.setNhanVien(nv);
        
        return luong;
    }
}