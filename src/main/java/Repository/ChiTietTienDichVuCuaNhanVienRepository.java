package Repository;

import Data.DataConnection;
import Model.ChiTietTienDichVuCuaNhanVien;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietTienDichVuCuaNhanVienRepository {
    
    public List<ChiTietTienDichVuCuaNhanVien> getAll() throws SQLException {
        List<ChiTietTienDichVuCuaNhanVien> list = new ArrayList<>();
        String sql = "SELECT ct.*, dv.TenDichVu, nv.HoTen " +
                    "FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "LEFT JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                    "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien " +
                    "ORDER BY ct.NgayTao DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToChiTietTienDV(rs));
            }
        }
        return list;
    }
    
    public ChiTietTienDichVuCuaNhanVien getById(int maCTTienDV) throws SQLException {
        String sql = "SELECT ct.*, dv.TenDichVu, nv.HoTen " +
                    "FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "LEFT JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                    "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien " +
                    "WHERE ct.MaCTTienDV = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maCTTienDV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToChiTietTienDV(rs);
                }
            }
        }
        return null;
    }
    
    public List<ChiTietTienDichVuCuaNhanVien> getByNhanVien(int maNhanVien) throws SQLException {
        List<ChiTietTienDichVuCuaNhanVien> list = new ArrayList<>();
        String sql = "SELECT ct.*, dv.TenDichVu, nv.HoTen " +
                    "FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "LEFT JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                    "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien " +
                    "WHERE ct.MaNhanVien = ? " +
                    "ORDER BY ct.NgayTao DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToChiTietTienDV(rs));
                }
            }
        }
        return list;
    }
    
    public List<ChiTietTienDichVuCuaNhanVien> getByChiTietHoaDon(int maCTHD) throws SQLException {
        List<ChiTietTienDichVuCuaNhanVien> list = new ArrayList<>();
        String sql = "SELECT ct.*, dv.TenDichVu, nv.HoTen " +
                    "FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "LEFT JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                    "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien " +
                    "WHERE ct.MaCTHD = ? " +
                    "ORDER BY ct.NgayTao DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maCTHD);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToChiTietTienDV(rs));
                }
            }
        }
        return list;
    }
    
    public List<ChiTietTienDichVuCuaNhanVien> getByThangNam(int maNhanVien, int thang, int nam) throws SQLException {
        List<ChiTietTienDichVuCuaNhanVien> list = new ArrayList<>();
        // Access: Dùng MONTH() và YEAR() vẫn hoạt động
        String sql = "SELECT ct.*, dv.TenDichVu, nv.HoTen " +
                    "FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "INNER JOIN ChiTietHoaDon cthd ON ct.MaCTHD = cthd.MaCTHD " +
                    "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                    "LEFT JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                    "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien " +
                    "WHERE ct.MaNhanVien = ? AND MONTH(hd.NgayLap) = ? AND YEAR(hd.NgayLap) = ? " +
                    "ORDER BY ct.NgayTao DESC";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToChiTietTienDV(rs));
                }
            }
        }
        return list;
    }
    
    public BigDecimal getTongTienDichVuByThangNam(int maNhanVien, int thang, int nam) throws SQLException {
        String sql = "SELECT SUM(ct.DonGiaThucTe * ct.SoLuong) as TongTien " +
                    "FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "INNER JOIN ChiTietHoaDon cthd ON ct.MaCTHD = cthd.MaCTHD " +
                    "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                    "WHERE ct.MaNhanVien = ? AND MONTH(hd.NgayLap) = ? AND YEAR(hd.NgayLap) = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal result = rs.getBigDecimal("TongTien");
                    return result != null ? result : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
    
    public boolean insert(ChiTietTienDichVuCuaNhanVien chiTiet) throws SQLException {
        // Access: Không dùng RETURN_GENERATED_KEYS
        String sql = "INSERT INTO ChiTietTienDichVuCuaNhanVien (MaCTHD, MaDichVu, MaNhanVien, SoLuong, " +
                    "DonGiaGoc, TiLePhanTram, DonGiaThucTe, NgayTao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, chiTiet.getMaCTHD());
            stmt.setInt(2, chiTiet.getMaDichVu());
            stmt.setInt(3, chiTiet.getMaNhanVien());
            stmt.setInt(4, chiTiet.getSoLuong());
            stmt.setBigDecimal(5, chiTiet.getDonGiaGoc());
            stmt.setDouble(6, chiTiet.getTiLePhanTram());
            stmt.setBigDecimal(7, chiTiet.getDonGiaThucTe());
            
            // Access: Xử lý TIMESTAMP
            if (chiTiet.getNgayTao() != null) {
                stmt.setTimestamp(8, Timestamp.valueOf(chiTiet.getNgayTao()));
            } else {
                stmt.setNull(8, Types.TIMESTAMP);
            }
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean update(ChiTietTienDichVuCuaNhanVien chiTiet) throws SQLException {
        String sql = "UPDATE ChiTietTienDichVuCuaNhanVien SET MaCTHD=?, MaDichVu=?, MaNhanVien=?, SoLuong=?, " +
                    "DonGiaGoc=?, TiLePhanTram=?, DonGiaThucTe=?, NgayTao=? WHERE MaCTTienDV=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, chiTiet.getMaCTHD());
            stmt.setInt(2, chiTiet.getMaDichVu());
            stmt.setInt(3, chiTiet.getMaNhanVien());
            stmt.setInt(4, chiTiet.getSoLuong());
            stmt.setBigDecimal(5, chiTiet.getDonGiaGoc());
            stmt.setDouble(6, chiTiet.getTiLePhanTram());
            stmt.setBigDecimal(7, chiTiet.getDonGiaThucTe());
            
            if (chiTiet.getNgayTao() != null) {
                stmt.setTimestamp(8, Timestamp.valueOf(chiTiet.getNgayTao()));
            } else {
                stmt.setNull(8, Types.TIMESTAMP);
            }
            
            stmt.setInt(9, chiTiet.getMaCTTienDV());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean delete(int maCTTienDV) throws SQLException {
        String sql = "DELETE FROM ChiTietTienDichVuCuaNhanVien WHERE MaCTTienDV = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maCTTienDV);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean existsByChiTietHoaDonAndNhanVien(int maCTHD, int maNhanVien) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ChiTietTienDichVuCuaNhanVien WHERE MaCTHD = ? AND MaNhanVien = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maCTHD);
            stmt.setInt(2, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    // Phương thức để lấy ID vừa insert trong Access
    public int getLastInsertId() throws SQLException {
        String sql = "SELECT @@IDENTITY AS LastID";
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("LastID");
            }
        }
        return 0;
    }
    
    private ChiTietTienDichVuCuaNhanVien mapResultSetToChiTietTienDV(ResultSet rs) throws SQLException {
        ChiTietTienDichVuCuaNhanVien chiTiet = new ChiTietTienDichVuCuaNhanVien();
        chiTiet.setMaCTTienDV(rs.getInt("MaCTTienDV"));
        chiTiet.setMaCTHD(rs.getInt("MaCTHD"));
        chiTiet.setMaDichVu(rs.getInt("MaDichVu"));
        chiTiet.setMaNhanVien(rs.getInt("MaNhanVien"));
        chiTiet.setSoLuong(rs.getInt("SoLuong"));
        chiTiet.setDonGiaGoc(rs.getBigDecimal("DonGiaGoc"));
        chiTiet.setTiLePhanTram(rs.getDouble("TiLePhanTram"));
        chiTiet.setDonGiaThucTe(rs.getBigDecimal("DonGiaThucTe"));
        
        // Xử lý TIMESTAMP
        Timestamp ngayTao = rs.getTimestamp("NgayTao");
        if (ngayTao != null) {
            chiTiet.setNgayTao(ngayTao.toLocalDateTime());
        }
        
        return chiTiet;
    }
}