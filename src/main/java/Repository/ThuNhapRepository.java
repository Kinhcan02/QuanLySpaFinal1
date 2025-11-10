package Repository;

import Model.ThuNhap;
import Data.DataConnection;
import java.util.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.sql.*;

public class ThuNhapRepository implements IThuNhapRepository {
    
    @Override
    public List<ThuNhap> getAllThuNhap() {
        List<ThuNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuNhap ORDER BY Nam DESC, Thang DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ThuNhap tn = mapResultSetToThuNhap(rs);
                list.add(tn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ThuNhap> getThuNhapByThangNam(int thang, int nam) {
        List<ThuNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuNhap WHERE Thang = ? AND Nam = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ThuNhap tn = mapResultSetToThuNhap(rs);
                list.add(tn);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ThuNhap> getThuNhapByNam(int nam) {
        List<ThuNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuNhap WHERE Nam = ? ORDER BY Thang DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nam);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ThuNhap tn = mapResultSetToThuNhap(rs);
                list.add(tn);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public ThuNhap getThuNhapById(int maThu) {
        String sql = "SELECT * FROM ThuNhap WHERE MaThu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maThu);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToThuNhap(rs);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean addThuNhap(ThuNhap thuNhap) {
        String sql = "INSERT INTO ThuNhap (Thang, Nam, TongDoanhThuDichVu, TongLuongNhanVien, ThuNhapThuc, NgayTinhThuNhap, GhiChu) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, thuNhap.getThang());
            stmt.setInt(2, thuNhap.getNam());
            stmt.setBigDecimal(3, thuNhap.getTongDoanhThuDichVu());
            stmt.setBigDecimal(4, thuNhap.getTongLuongNhanVien());
            stmt.setBigDecimal(5, thuNhap.getThuNhapThuc());
            stmt.setDate(6, java.sql.Date.valueOf(thuNhap.getNgayTinhThuNhap()));
            stmt.setString(7, thuNhap.getGhiChu());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        thuNhap.setMaThu(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean updateThuNhap(ThuNhap thuNhap) {
        String sql = "UPDATE ThuNhap SET Thang=?, Nam=?, TongDoanhThuDichVu=?, TongLuongNhanVien=?, ThuNhapThuc=?, NgayTinhThuNhap=?, GhiChu=? WHERE MaThu=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thuNhap.getThang());
            stmt.setInt(2, thuNhap.getNam());
            stmt.setBigDecimal(3, thuNhap.getTongDoanhThuDichVu());
            stmt.setBigDecimal(4, thuNhap.getTongLuongNhanVien());
            stmt.setBigDecimal(5, thuNhap.getThuNhapThuc());
            stmt.setDate(6, java.sql.Date.valueOf(thuNhap.getNgayTinhThuNhap()));
            stmt.setString(7, thuNhap.getGhiChu());
            stmt.setInt(8, thuNhap.getMaThu());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteThuNhap(int maThu) {
        String sql = "DELETE FROM ThuNhap WHERE MaThu=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maThu);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public BigDecimal getTongDoanhThuThang(int thang, int nam) {
        LocalDate startDate = LocalDate.of(nam, thang, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        String sql = "SELECT SUM(TongTien) as TongDoanhThu FROM HoaDon WHERE NgayLap BETWEEN ? AND ?";
        BigDecimal result = BigDecimal.ZERO;
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal("TongDoanhThu");
                if (result == null) {
                    result = BigDecimal.ZERO;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    @Override
    public BigDecimal getTongLuongThang(int thang, int nam) {
        // Lấy lương mới nhất của mỗi nhân viên trong tháng và tính tổng
        String sql = "SELECT SUM(luong_moi_nhat.TongLuong) as TongLuong " +
                     "FROM ( " +
                     "    SELECT MaNhanVien, MAX(NgayTinhLuong) as NgayMoiNhat " +
                     "    FROM TinhLuongNhanVien " +
                     "    WHERE Thang = ? AND Nam = ? " +
                     "    GROUP BY MaNhanVien " +
                     ") as latest " +
                     "JOIN TinhLuongNhanVien as luong_moi_nhat " +
                     "ON latest.MaNhanVien = luong_moi_nhat.MaNhanVien " +
                     "AND latest.NgayMoiNhat = luong_moi_nhat.NgayTinhLuong";
        
        BigDecimal result = BigDecimal.ZERO;
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal("TongLuong");
                if (result == null) {
                    result = BigDecimal.ZERO;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    @Override
    public BigDecimal getTongThuNhapByNam(int nam) {
        String sql = "SELECT SUM(ThuNhapThuc) as TongThuNhap FROM ThuNhap WHERE Nam = ?";
        BigDecimal result = BigDecimal.ZERO;
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nam);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal("TongThuNhap");
                if (result == null) {
                    result = BigDecimal.ZERO;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private ThuNhap mapResultSetToThuNhap(ResultSet rs) throws SQLException {
        ThuNhap tn = new ThuNhap();
        tn.setMaThu(rs.getInt("MaThu"));
        tn.setThang(rs.getInt("Thang"));
        tn.setNam(rs.getInt("Nam"));
        tn.setTongDoanhThuDichVu(rs.getBigDecimal("TongDoanhThuDichVu"));
        tn.setTongLuongNhanVien(rs.getBigDecimal("TongLuongNhanVien"));
        tn.setThuNhapThuc(rs.getBigDecimal("ThuNhapThuc"));
        
        java.sql.Date ngay = rs.getDate("NgayTinhThuNhap");
        if (ngay != null) {
            tn.setNgayTinhThuNhap(ngay.toLocalDate());
        }
        
        tn.setGhiChu(rs.getString("GhiChu"));
        
        return tn;
    }
}