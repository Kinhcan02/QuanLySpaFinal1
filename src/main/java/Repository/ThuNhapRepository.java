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
        String sql = "SELECT * FROM ThuNhap ORDER BY NgayThu DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ThuNhap tn = new ThuNhap(
                    rs.getInt("MaThu"),
                    rs.getDate("NgayThu").toLocalDate(),
                    rs.getBigDecimal("SoTien"),
                    rs.getString("NoiDung")
                );
                list.add(tn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ThuNhap> getThuNhapByDateRange(LocalDate fromDate, LocalDate toDate) {
        List<ThuNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM ThuNhap WHERE NgayThu BETWEEN ? AND ? ORDER BY NgayThu DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(fromDate));
            stmt.setDate(2, java.sql.Date.valueOf(toDate));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ThuNhap tn = new ThuNhap(
                    rs.getInt("MaThu"),
                    rs.getDate("NgayThu").toLocalDate(),
                    rs.getBigDecimal("SoTien"),
                    rs.getString("NoiDung")
                );
                list.add(tn);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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
    public boolean addThuNhap(ThuNhap thuNhap) {
        String sql = "INSERT INTO ThuNhap (NgayThu, SoTien, NoiDung, LoaiThu) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(thuNhap.getNgayThu()));
            stmt.setBigDecimal(2, thuNhap.getSoTien());
            stmt.setString(3, thuNhap.getNoiDung());
            stmt.setString(4, "Dịch vụ");
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateThuNhap(ThuNhap thuNhap) {
        String sql = "UPDATE ThuNhap SET NgayThu=?, SoTien=?, NoiDung=? WHERE MaThu=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(thuNhap.getNgayThu()));
            stmt.setBigDecimal(2, thuNhap.getSoTien());
            stmt.setString(3, thuNhap.getNoiDung());
            stmt.setInt(4, thuNhap.getMaThu());
            
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
    public BigDecimal getTongThuNhap(LocalDate fromDate, LocalDate toDate) {
        String sql = "SELECT SUM(SoTien) as TongThu FROM ThuNhap WHERE NgayThu BETWEEN ? AND ?";
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
        // Sửa lại query cho đúng với database thực tế
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
            // Trả về 0 nếu có lỗi để không làm crash ứng dụng
            return BigDecimal.ZERO;
        }
        return result;
    }
}