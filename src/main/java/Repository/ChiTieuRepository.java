package Repository;

import Model.ChiTieu;
import Data.DataConnection;
import java.util.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.sql.*;

public class ChiTieuRepository implements IChiTieuRepository {
    
    @Override
    public List<ChiTieu> getAllChiTieu() {
        List<ChiTieu> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTieu ORDER BY NgayChi DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ChiTieu ct = new ChiTieu(
                    rs.getInt("MaChi"),
                    rs.getDate("NgayChi").toLocalDate(),
                    rs.getString("MucDich"),
                    rs.getBigDecimal("SoTien")
                );
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ChiTieu> getChiTieuByDateRange(LocalDate fromDate, LocalDate toDate) {
        List<ChiTieu> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTieu WHERE NgayChi BETWEEN ? AND ? ORDER BY NgayChi DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(fromDate));
            stmt.setDate(2, java.sql.Date.valueOf(toDate));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ChiTieu ct = new ChiTieu(
                    rs.getInt("MaChi"),
                    rs.getDate("NgayChi").toLocalDate(),
                    rs.getString("MucDich"),
                    rs.getBigDecimal("SoTien")
                );
                list.add(ct);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ChiTieu> getChiTieuByMonth(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return getChiTieuByDateRange(startDate, endDate);
    }
    
    @Override
    public List<ChiTieu> getChiTieuByYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return getChiTieuByDateRange(startDate, endDate);
    }
    
    @Override
    public boolean addChiTieu(ChiTieu chiTieu) {
        String sql = "INSERT INTO ChiTieu (NgayChi, SoTien, MucDich) VALUES (?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(chiTieu.getNgayChi()));
            stmt.setBigDecimal(2, chiTieu.getSoTien());
            stmt.setString(3, chiTieu.getMucDich());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateChiTieu(ChiTieu chiTieu) {
        String sql = "UPDATE ChiTieu SET NgayChi=?, SoTien=?, MucDich=? WHERE MaChi=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(chiTieu.getNgayChi()));
            stmt.setBigDecimal(2, chiTieu.getSoTien());
            stmt.setString(3, chiTieu.getMucDich());
            stmt.setInt(4, chiTieu.getMaChi());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteChiTieu(int maChi) {
        String sql = "DELETE FROM ChiTieu WHERE MaChi=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maChi);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public BigDecimal getTongChiTieu(LocalDate fromDate, LocalDate toDate) {
        String sql = "SELECT SUM(SoTien) as TongChi FROM ChiTieu WHERE NgayChi BETWEEN ? AND ?";
        BigDecimal result = BigDecimal.ZERO;
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(fromDate));
            stmt.setDate(2, java.sql.Date.valueOf(toDate));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal("TongChi");
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
    public BigDecimal getTongChiTieuByNguyenLieu(LocalDate fromDate, LocalDate toDate) {
        // Sửa lại query cho đúng với bảng NhapNguyenLieu trong database
        String sql = "SELECT SUM(SoLuong * DonGia) as TongChi FROM NhapNguyenLieu WHERE NgayNhap BETWEEN ? AND ?";
        BigDecimal result = BigDecimal.ZERO;
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(fromDate));
            stmt.setDate(2, java.sql.Date.valueOf(toDate));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal("TongChi");
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