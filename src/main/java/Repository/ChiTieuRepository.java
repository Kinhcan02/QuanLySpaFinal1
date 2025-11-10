package Repository;

import Model.ChiTieu;
import Data.DataConnection;
import java.util.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class ChiTieuRepository implements IChiTieuRepository {
    
    @Override
    public List<ChiTieu> getAllChiTieu() {
        List<ChiTieu> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTieu ORDER BY Nam DESC, Thang DESC, NgayTao DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ChiTieu ct = mapResultSetToChiTieu(rs);
                list.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ChiTieu> getChiTieuByThangNam(int thang, int nam) {
        List<ChiTieu> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTieu WHERE Thang = ? AND Nam = ? ORDER BY NgayTao DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ChiTieu ct = mapResultSetToChiTieu(rs);
                list.add(ct);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ChiTieu> getChiTieuByNam(int nam) {
        List<ChiTieu> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTieu WHERE Nam = ? ORDER BY Thang DESC, NgayTao DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nam);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ChiTieu ct = mapResultSetToChiTieu(rs);
                list.add(ct);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public ChiTieu getChiTieuById(int maChi) {
        String sql = "SELECT * FROM ChiTieu WHERE MaChi = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maChi);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToChiTieu(rs);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean addChiTieu(ChiTieu chiTieu) {
        String sql = "INSERT INTO ChiTieu (NgayChi, MucDich, SoTien, LoaiChi, NgayTao, Thang, Nam) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(chiTieu.getNgayChi()));
            stmt.setString(2, chiTieu.getMucDich());
            stmt.setBigDecimal(3, chiTieu.getSoTien());
            stmt.setString(4, chiTieu.getLoaiChi());
            stmt.setDate(5, java.sql.Date.valueOf(chiTieu.getNgayTao()));
            stmt.setInt(6, chiTieu.getThang());
            stmt.setInt(7, chiTieu.getNam());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        chiTieu.setMaChi(generatedKeys.getInt(1));
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
    public boolean updateChiTieu(ChiTieu chiTieu) {
        String sql = "UPDATE ChiTieu SET NgayChi=?, MucDich=?, SoTien=?, LoaiChi=?, Thang=?, Nam=? WHERE MaChi=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(chiTieu.getNgayChi()));
            stmt.setString(2, chiTieu.getMucDich());
            stmt.setBigDecimal(3, chiTieu.getSoTien());
            stmt.setString(4, chiTieu.getLoaiChi());
            stmt.setInt(5, chiTieu.getThang());
            stmt.setInt(6, chiTieu.getNam());
            stmt.setInt(7, chiTieu.getMaChi());
            
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
    public BigDecimal getTongChiTieuByThangNam(int thang, int nam) {
        String sql = "SELECT SUM(SoTien) as TongChi FROM ChiTieu WHERE Thang = ? AND Nam = ?";
        return executeSumQuery(sql, thang, nam);
    }
    
    @Override
    public BigDecimal getTongChiTieuByLoai(int thang, int nam, String loaiChi) {
        String sql = "SELECT SUM(SoTien) as TongChi FROM ChiTieu WHERE Thang = ? AND Nam = ? AND LoaiChi = ?";
        BigDecimal result = BigDecimal.ZERO;
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            stmt.setString(3, loaiChi);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal(1);
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
    public List<ChiTieu> getAllChiTieuWithNguyenLieu(int thang, int nam) {
        List<ChiTieu> combinedList = new ArrayList<>();
        
        // 1. Lấy tất cả chi tiêu thông thường
        List<ChiTieu> chiTieuList = getChiTieuByThangNam(thang, nam);
        combinedList.addAll(chiTieuList);
        
        // 2. Lấy tổng chi phí nguyên liệu và tạo đối tượng ChiTieu
        BigDecimal tongNguyenLieu = getTongNhapNguyenLieuByThangNam(thang, nam);
        
        if (tongNguyenLieu.compareTo(BigDecimal.ZERO) > 0) {
            // Kiểm tra xem đã có chi phí nguyên liệu trong bảng chưa
            boolean daCoNguyenLieu = chiTieuList.stream()
                    .anyMatch(ct -> "Nguyên liệu".equals(ct.getLoaiChi()));
            
            if (!daCoNguyenLieu) {
                // Thêm chi phí nguyên liệu vào bảng ChiTieu
                ChiTieu nguyenLieuChi = new ChiTieu();
                nguyenLieuChi.setNgayChi(LocalDate.of(nam, thang, 1));
                nguyenLieuChi.setThang(thang);
                nguyenLieuChi.setNam(nam);
                nguyenLieuChi.setMucDich("Nhập nguyên liệu tháng " + thang + "/" + nam);
                nguyenLieuChi.setSoTien(tongNguyenLieu);
                nguyenLieuChi.setLoaiChi("Nguyên liệu");
                nguyenLieuChi.setNgayTao(LocalDate.now());
                
                // Lưu vào database
                if (addChiTieu(nguyenLieuChi)) {
                    combinedList.add(nguyenLieuChi);
                }
            }
        }
        
        // Sắp xếp theo ngày chi
        combinedList.sort((c1, c2) -> c2.getNgayChi().compareTo(c1.getNgayChi()));
        
        return combinedList;
    }
    
    private BigDecimal getTongNhapNguyenLieuByThangNam(int thang, int nam) {
        String sql = "SELECT SUM(SoLuong * DonGia) as TongNhap FROM NhapNguyenLieu WHERE MONTH(NgayNhap) = ? AND YEAR(NgayNhap) = ?";
        BigDecimal result = BigDecimal.ZERO;
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal(1);
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
    
    private BigDecimal executeSumQuery(String sql, int thang, int nam) {
        BigDecimal result = BigDecimal.ZERO;
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getBigDecimal(1);
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
    
    private ChiTieu mapResultSetToChiTieu(ResultSet rs) throws SQLException {
        ChiTieu ct = new ChiTieu();
        ct.setMaChi(rs.getInt("MaChi"));
        
        // Lấy tháng, năm từ các cột riêng biệt
        ct.setThang(rs.getInt("Thang"));
        ct.setNam(rs.getInt("Nam"));
        
        java.sql.Date ngayChi = rs.getDate("NgayChi");
        if (ngayChi != null) {
            ct.setNgayChi(ngayChi.toLocalDate());
        }
        
        ct.setMucDich(rs.getString("MucDich"));
        ct.setSoTien(rs.getBigDecimal("SoTien"));
        ct.setLoaiChi(rs.getString("LoaiChi"));
        
        java.sql.Date ngayTao = rs.getDate("NgayTao");
        if (ngayTao != null) {
            ct.setNgayTao(ngayTao.toLocalDate());
        }
        
        return ct;
    }
}