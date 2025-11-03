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
        // SỬA QUERY PHÙ HỢP VỚI CẤU TRÚC BẢNG THỰC TẾ
        String sql = "SELECT * FROM ThuNhap ORDER BY NgayTinhThuNhap DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                // SỬA THEO CẤU TRÚC BẢNG THỰC TẾ
                ThuNhap tn = new ThuNhap();
                tn.setMaThu(rs.getInt("MaThu"));
                
                // SỬA: Sử dụng java.sql.Date rõ ràng
                java.sql.Date ngay = rs.getDate("NgayTinhThuNhap");
                if (ngay != null) {
                    tn.setNgayThu(ngay.toLocalDate());
                } else {
                    // Nếu không có cột ngày, sử dụng ngày hiện tại
                    tn.setNgayThu(LocalDate.now());
                }
                
                // Sử dụng cột số tiền thực tế
                if (hasColumn(rs, "TongDoanhThuDichVu")) {
                    tn.setSoTien(rs.getBigDecimal("TongDoanhThuDichVu"));
                } else if (hasColumn(rs, "SoTien")) {
                    tn.setSoTien(rs.getBigDecimal("SoTien"));
                } else {
                    tn.setSoTien(BigDecimal.ZERO);
                }
                
                // Sử dụng cột nội dung thực tế
                if (hasColumn(rs, "GhiChu")) {
                    tn.setNoiDung(rs.getString("GhiChu"));
                } else if (hasColumn(rs, "NoiDung")) {
                    tn.setNoiDung(rs.getString("NoiDung"));
                } else {
                    tn.setNoiDung("Thu nhập dịch vụ");
                }
                
                list.add(tn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Phương thức kiểm tra xem cột có tồn tại trong ResultSet không
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    @Override
    public List<ThuNhap> getThuNhapByDateRange(LocalDate fromDate, LocalDate toDate) {
        List<ThuNhap> list = new ArrayList<>();
        // SỬA QUERY SỬ DỤNG CỘT NGÀY THỰC TẾ
        String sql = "SELECT * FROM ThuNhap WHERE NgayTinhThuNhap BETWEEN ? AND ? ORDER BY NgayTinhThuNhap DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(fromDate));
            stmt.setDate(2, java.sql.Date.valueOf(toDate));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ThuNhap tn = new ThuNhap();
                tn.setMaThu(rs.getInt("MaThu"));
                
                // SỬA: Sử dụng java.sql.Date rõ ràng
                java.sql.Date ngay = rs.getDate("NgayTinhThuNhap");
                if (ngay != null) {
                    tn.setNgayThu(ngay.toLocalDate());
                }
                
                tn.setSoTien(rs.getBigDecimal("TongDoanhThuDichVu"));
                tn.setNoiDung(rs.getString("GhiChu"));
                
                list.add(tn);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public boolean addThuNhap(ThuNhap thuNhap) {
        // SỬA QUERY INSERT PHÙ HỢP VỚI CẤU TRÚC BẢNG
        String sql = "INSERT INTO ThuNhap (NgayTinhThuNhap, TongDoanhThuDichVu, GhiChu, Thang, Nam) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(thuNhap.getNgayThu()));
            stmt.setBigDecimal(2, thuNhap.getSoTien());
            stmt.setString(3, thuNhap.getNoiDung());
            stmt.setInt(4, thuNhap.getNgayThu().getMonthValue());
            stmt.setInt(5, thuNhap.getNgayThu().getYear());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateThuNhap(ThuNhap thuNhap) {
        // SỬA QUERY UPDATE
        String sql = "UPDATE ThuNhap SET NgayTinhThuNhap=?, TongDoanhThuDichVu=?, GhiChu=?, Thang=?, Nam=? WHERE MaThu=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(thuNhap.getNgayThu()));
            stmt.setBigDecimal(2, thuNhap.getSoTien());
            stmt.setString(3, thuNhap.getNoiDung());
            stmt.setInt(4, thuNhap.getNgayThu().getMonthValue());
            stmt.setInt(5, thuNhap.getNgayThu().getYear());
            stmt.setInt(6, thuNhap.getMaThu());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<ThuNhap> getThuNhapByMonth(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return getThuNhapByDateRange(startDate, endDate);
    }
    
    @Override
    public List<ThuNhap> getThuNhapByYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return getThuNhapByDateRange(startDate, endDate);
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
    public BigDecimal getTongThuNhap(LocalDate fromDate, LocalDate toDate) {
        String sql = "SELECT SUM(TongDoanhThuDichVu) as TongThu FROM ThuNhap WHERE NgayTinhThuNhap BETWEEN ? AND ?";
        BigDecimal result = BigDecimal.ZERO;
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(fromDate));
            stmt.setDate(2, java.sql.Date.valueOf(toDate));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal("TongThu");
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
    public BigDecimal getTongThuNhapByHoaDon(LocalDate fromDate, LocalDate toDate) {
        String sql = "SELECT SUM(TongTien) as TongThu FROM HoaDon WHERE NgayLap BETWEEN ? AND ?";
        BigDecimal result = BigDecimal.ZERO;
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(fromDate));
            stmt.setDate(2, java.sql.Date.valueOf(toDate));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal("TongThu");
                if (result == null) {
                    result = BigDecimal.ZERO;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
        return result;
    }
}