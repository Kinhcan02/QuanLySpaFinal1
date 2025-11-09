package Repository;

import Data.DataConnection;
import Model.ChiTietTienDichVuCuaNhanVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietTienDichVuCuaNhanVienRepository {
    
    public List<ChiTietTienDichVuCuaNhanVien> getAll() throws SQLException {
        List<ChiTietTienDichVuCuaNhanVien> list = new ArrayList<>();
        String sql = "SELECT ct.*, dv.TenDichVu, nv.HoTen, pt.TiLePhanTram, ldv.MaLoaiDV " +
                    "FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "LEFT JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                    "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien " +
                    "LEFT JOIN PhanTramDichVu pt ON ct.MaPhanTram = pt.MaPhanTram " +
                    "LEFT JOIN LoaiDichVu ldv ON dv.MaLoaiDV = ldv.MaLoaiDV " +
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
        String sql = "SELECT ct.*, dv.TenDichVu, nv.HoTen, pt.TiLePhanTram, ldv.MaLoaiDV " +
                    "FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "LEFT JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                    "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien " +
                    "LEFT JOIN PhanTramDichVu pt ON ct.MaPhanTram = pt.MaPhanTram " +
                    "LEFT JOIN LoaiDichVu ldv ON dv.MaLoaiDV = ldv.MaLoaiDV " +
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
        String sql = "SELECT ct.*, dv.TenDichVu, nv.HoTen, pt.TiLePhanTram, ldv.MaLoaiDV " +
                    "FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "LEFT JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                    "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien " +
                    "LEFT JOIN PhanTramDichVu pt ON ct.MaPhanTram = pt.MaPhanTram " +
                    "LEFT JOIN LoaiDichVu ldv ON dv.MaLoaiDV = ldv.MaLoaiDV " +
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
        String sql = "SELECT ct.*, dv.TenDichVu, nv.HoTen, pt.TiLePhanTram, ldv.MaLoaiDV " +
                    "FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "LEFT JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                    "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien " +
                    "LEFT JOIN PhanTramDichVu pt ON ct.MaPhanTram = pt.MaPhanTram " +
                    "LEFT JOIN LoaiDichVu ldv ON dv.MaLoaiDV = ldv.MaLoaiDV " +
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
    
    // FIXED: Lấy chi tiết tiền dịch vụ theo tháng/năm - JOIN với HoaDon để lấy đúng tháng/năm
    public List<ChiTietTienDichVuCuaNhanVien> getByThangNam(int maNhanVien, int thang, int nam) throws SQLException {
        List<ChiTietTienDichVuCuaNhanVien> list = new ArrayList<>();
        String sql = "SELECT ct.*, dv.TenDichVu, nv.HoTen, pt.TiLePhanTram, ldv.MaLoaiDV " +
                    "FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "INNER JOIN ChiTietHoaDon cthd ON ct.MaCTHD = cthd.MaCTHD " +
                    "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                    "LEFT JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                    "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien " +
                    "LEFT JOIN PhanTramDichVu pt ON ct.MaPhanTram = pt.MaPhanTram " +
                    "LEFT JOIN LoaiDichVu ldv ON dv.MaLoaiDV = ldv.MaLoaiDV " +
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
    
    // FIXED: Tự động tạo chi tiết tiền dịch vụ dựa trên chi tiết hóa đơn và phân trăm dịch vụ
    public boolean taoChiTietTienDichVuTuDong(int maCTHD) throws SQLException {
        String sql = "INSERT INTO ChiTietTienDichVuCuaNhanVien (MaCTHD, MaDichVu, MaNhanVien, MaPhanTram, DonGiaThucTe, NgayTao) " +
                    "SELECT cthd.MaCTHD, cthd.MaDichVu, cthd.MaNhanVien, pt.MaPhanTram, " +
                    "       (cthd.SoLuong * cthd.DonGia * pt.TiLePhanTram / 100) as DonGiaThucTe, " +
                    "       NOW() as NgayTao " +
                    "FROM ChiTietHoaDon cthd " +
                    "INNER JOIN DichVu dv ON cthd.MaDichVu = dv.MaDichVu " +
                    "INNER JOIN PhanTramDichVu pt ON dv.MaLoaiDV = pt.MaLoaiDV AND cthd.MaNhanVien = pt.MaNhanVien " +
                    "WHERE cthd.MaCTHD = ? AND NOT EXISTS (" +
                    "    SELECT 1 FROM ChiTietTienDichVuCuaNhanVien ct " +
                    "    WHERE ct.MaCTHD = cthd.MaCTHD AND ct.MaNhanVien = cthd.MaNhanVien" +
                    ")";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maCTHD);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean insert(ChiTietTienDichVuCuaNhanVien chiTiet) throws SQLException {
        String sql = "INSERT INTO ChiTietTienDichVuCuaNhanVien (MaCTHD, MaDichVu, MaNhanVien, MaPhanTram, DonGiaThucTe, NgayTao) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, chiTiet.getMaCTHD());
            stmt.setInt(2, chiTiet.getMaDichVu());
            stmt.setInt(3, chiTiet.getMaNhanVien());
            stmt.setInt(4, chiTiet.getMaPhanTram());
            stmt.setBigDecimal(5, chiTiet.getDonGiaThucTe());
            
            if (chiTiet.getNgayTao() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(chiTiet.getNgayTao()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean update(ChiTietTienDichVuCuaNhanVien chiTiet) throws SQLException {
        String sql = "UPDATE ChiTietTienDichVuCuaNhanVien SET MaCTHD=?, MaDichVu=?, MaNhanVien=?, " +
                    "MaPhanTram=?, DonGiaThucTe=?, NgayTao=? WHERE MaCTTienDV=?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, chiTiet.getMaCTHD());
            stmt.setInt(2, chiTiet.getMaDichVu());
            stmt.setInt(3, chiTiet.getMaNhanVien());
            stmt.setInt(4, chiTiet.getMaPhanTram());
            stmt.setBigDecimal(5, chiTiet.getDonGiaThucTe());
            
            if (chiTiet.getNgayTao() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(chiTiet.getNgayTao()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            
            stmt.setInt(7, chiTiet.getMaCTTienDV());
            
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
        chiTiet.setMaPhanTram(rs.getInt("MaPhanTram"));
        chiTiet.setDonGiaThucTe(rs.getBigDecimal("DonGiaThucTe"));
        
        Timestamp ngayTao = rs.getTimestamp("NgayTao");
        if (ngayTao != null) {
            chiTiet.setNgayTao(ngayTao.toLocalDateTime());
        }
        
        return chiTiet;
    }
}