package Repository;

import Data.DataConnection;
import Model.HoaDon;
import Model.ChiTietHoaDon;
import Model.DichVu;
import Model.NhanVien;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonRepository {

    public List<HoaDon> getAll() throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT hd.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien "
                + "FROM (HoaDon hd "
                + "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang) "
                + "LEFT JOIN NhanVien nv ON hd.MaNhanVienLap = nv.MaNhanVien "
                + "ORDER BY hd.NgayLap DESC";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                HoaDon hoaDon = mapResultSetToHoaDon(rs);
                // Load chi tiết hóa đơn
                hoaDon.setChiTietHoaDon(getChiTietByMaHoaDon(hoaDon.getMaHoaDon()));
                list.add(hoaDon);
            }
        }
        return list;
    }

    public HoaDon getById(int maHoaDon) throws SQLException {
        String sql = "SELECT hd.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien "
                + "FROM (HoaDon hd "
                + "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang) "
                + "LEFT JOIN NhanVien nv ON hd.MaNhanVienLap = nv.MaNhanVien "
                + "WHERE hd.MaHoaDon = ?";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    HoaDon hoaDon = mapResultSetToHoaDon(rs);
                    // Load chi tiết hóa đơn
                    hoaDon.setChiTietHoaDon(getChiTietByMaHoaDon(maHoaDon));
                    return hoaDon;
                }
            }
        }
        return null;
    }

    public List<HoaDon> getByMaKhachHang(int maKhachHang) throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT hd.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien "
                + "FROM (HoaDon hd "
                + "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang) "
                + "LEFT JOIN NhanVien nv ON hd.MaNhanVienLap = nv.MaNhanVien "
                + "WHERE hd.MaKhachHang = ? "
                + "ORDER BY hd.NgayLap DESC";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maKhachHang);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HoaDon hoaDon = mapResultSetToHoaDon(rs);
                    list.add(hoaDon);
                }
            }
        }
        return list;
    }

    public List<ChiTietHoaDon> getChiTietByMaHoaDon(int maHoaDon) throws SQLException {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT hdct.*, dv.TenDichVu, dv.Gia, nv.HoTen as TenNhanVien "
                + "FROM (ChiTietHoaDon hdct "
                + "INNER JOIN DichVu dv ON hdct.MaDichVu = dv.MaDichVu) "
                + "LEFT JOIN NhanVien nv ON hdct.MaNhanVien = nv.MaNhanVien "
                + "WHERE hdct.MaHoaDon = ?";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon chiTiet = mapResultSetToChiTietHoaDon(rs);
                    list.add(chiTiet);
                }
            }
        }
        return list;
    }

    public boolean insert(HoaDon hoaDon) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtHoaDon = null;
        ResultSet rs = null;
        
        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert hóa đơn chính
            String sqlHoaDon = "INSERT INTO HoaDon (MaKhachHang, NgayLap, TongTien, MaNhanVienLap, GhiChu) "
                    + "VALUES (?, ?, ?, ?, ?)";
            
            stmtHoaDon = conn.prepareStatement(sqlHoaDon, Statement.RETURN_GENERATED_KEYS);
            stmtHoaDon.setInt(1, hoaDon.getMaKhachHang());
            stmtHoaDon.setTimestamp(2, Timestamp.valueOf(hoaDon.getNgayLap()));
            stmtHoaDon.setBigDecimal(3, hoaDon.getTongTien());

            // Kiểm tra MaNhanVienLap hợp lệ
            if (hoaDon.getMaNhanVienLap() != null && hoaDon.getMaNhanVienLap() > 0) {
                stmtHoaDon.setInt(4, hoaDon.getMaNhanVienLap());
            } else {
                stmtHoaDon.setNull(4, Types.INTEGER);
            }

            stmtHoaDon.setString(5, hoaDon.getGhiChu());

            int affectedRows = stmtHoaDon.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            // Lấy mã hóa đơn vừa tạo
            rs = stmtHoaDon.getGeneratedKeys();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            int generatedMaHoaDon = rs.getInt(1);
            hoaDon.setMaHoaDon(generatedMaHoaDon);

            // 2. Insert chi tiết hóa đơn
            if (hoaDon.getChiTietHoaDon() != null && !hoaDon.getChiTietHoaDon().isEmpty()) {
                for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDon()) {
                    if (!insertChiTiet(conn, generatedMaHoaDon, chiTiet)) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw e;
        } finally {
            // Đóng resources theo đúng thứ tự
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmtHoaDon != null) {
                try {
                    stmtHoaDon.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean insertChiTiet(Connection conn, int maHoaDon, ChiTietHoaDon chiTiet) throws SQLException {
        String sql = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaDichVu, MaNhanVien, SoLuong, DonGia, ThanhTien) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maHoaDon);
            stmt.setInt(2, chiTiet.getMaDichVu());

            // FIX: Kiểm tra MaNhanVien hợp lệ - không cho phép 0
            if (chiTiet.getMaNhanVien() != null && chiTiet.getMaNhanVien() > 0) {
                stmt.setInt(3, chiTiet.getMaNhanVien());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setInt(4, chiTiet.getSoLuong());
            stmt.setBigDecimal(5, chiTiet.getDonGia());
            
            // TÍNH TOÁN THÀNH TIỀN: Số lượng * Đơn giá
            BigDecimal thanhTien = chiTiet.getDonGia().multiply(BigDecimal.valueOf(chiTiet.getSoLuong()));
            stmt.setBigDecimal(6, thanhTien);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean update(HoaDon hoaDon) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtHoaDon = null;
        
        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Update hóa đơn chính
            String sqlHoaDon = "UPDATE HoaDon SET MaKhachHang=?, NgayLap=?, TongTien=?, MaNhanVienLap=?, GhiChu=? WHERE MaHoaDon=?";
            stmtHoaDon = conn.prepareStatement(sqlHoaDon);
            stmtHoaDon.setInt(1, hoaDon.getMaKhachHang());
            stmtHoaDon.setTimestamp(2, Timestamp.valueOf(hoaDon.getNgayLap()));
            stmtHoaDon.setBigDecimal(3, hoaDon.getTongTien());

            if (hoaDon.getMaNhanVienLap() != null && hoaDon.getMaNhanVienLap() > 0) {
                stmtHoaDon.setInt(4, hoaDon.getMaNhanVienLap());
            } else {
                stmtHoaDon.setNull(4, Types.INTEGER);
            }

            stmtHoaDon.setString(5, hoaDon.getGhiChu());
            stmtHoaDon.setInt(6, hoaDon.getMaHoaDon());

            int affectedRows = stmtHoaDon.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                return false;
            }

            // 2. Xóa chi tiết cũ và thêm mới
            deleteChiTietByMaHoaDon(conn, hoaDon.getMaHoaDon());

            // 3. Insert chi tiết hóa đơn mới
            if (hoaDon.getChiTietHoaDon() != null && !hoaDon.getChiTietHoaDon().isEmpty()) {
                for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDon()) {
                    if (!insertChiTiet(conn, hoaDon.getMaHoaDon(), chiTiet)) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (stmtHoaDon != null) {
                try {
                    stmtHoaDon.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deleteChiTietByMaHoaDon(Connection conn, int maHoaDon) throws SQLException {
        String sql = "DELETE FROM ChiTietHoaDon WHERE MaHoaDon = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maHoaDon);
            stmt.executeUpdate();
        }
    }

    public boolean delete(int maHoaDon) throws SQLException {
        Connection conn = null;
        
        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Xóa chi tiết trước
            deleteChiTietByMaHoaDon(conn, maHoaDon);

            // 2. Xóa hóa đơn
            String sqlHoaDon = "DELETE FROM HoaDon WHERE MaHoaDon = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlHoaDon)) {
                stmt.setInt(1, maHoaDon);
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public BigDecimal getTongDoanhThuTheoThang(int thang, int nam) throws SQLException {
        String sql = "SELECT SUM(TongTien) as TongDoanhThu FROM HoaDon "
                + "WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal tongDoanhThu = rs.getBigDecimal("TongDoanhThu");
                    return tongDoanhThu != null ? tongDoanhThu : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public List<HoaDon> getHoaDonTheoKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT hd.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien "
                + "FROM (HoaDon hd "
                + "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang) "
                + "LEFT JOIN NhanVien nv ON hd.MaNhanVienLap = nv.MaNhanVien "
                + "WHERE hd.NgayLap BETWEEN ? AND ? "
                + "ORDER BY hd.NgayLap DESC";

        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(tuNgay));
            stmt.setTimestamp(2, Timestamp.valueOf(denNgay));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HoaDon hoaDon = mapResultSetToHoaDon(rs);
                    list.add(hoaDon);
                }
            }
        }
        return list;
    }

    private HoaDon mapResultSetToHoaDon(ResultSet rs) throws SQLException {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(rs.getInt("MaHoaDon"));
        hoaDon.setMaKhachHang(rs.getInt("MaKhachHang"));

        Timestamp ngayLap = rs.getTimestamp("NgayLap");
        if (ngayLap != null) {
            hoaDon.setNgayLap(ngayLap.toLocalDateTime());
        }

        hoaDon.setTongTien(rs.getBigDecimal("TongTien"));

        int maNhanVienLap = rs.getInt("MaNhanVienLap");
        if (!rs.wasNull()) {
            hoaDon.setMaNhanVienLap(maNhanVienLap);
        }

        hoaDon.setGhiChu(rs.getString("GhiChu"));

        return hoaDon;
    }

    private ChiTietHoaDon mapResultSetToChiTietHoaDon(ResultSet rs) throws SQLException {
        ChiTietHoaDon chiTiet = new ChiTietHoaDon();
        chiTiet.setMaCTHD(rs.getInt("MaCTHD"));
        chiTiet.setMaHoaDon(rs.getInt("MaHoaDon"));
        chiTiet.setMaDichVu(rs.getInt("MaDichVu"));
        
        int maNhanVien = rs.getInt("MaNhanVien");
        if (!rs.wasNull()) {
            chiTiet.setMaNhanVien(maNhanVien);
        }
        
        chiTiet.setSoLuong(rs.getInt("SoLuong"));
        chiTiet.setDonGia(rs.getBigDecimal("DonGia"));
        
        // TÍNH TOÁN THÀNH TIỀN: Số lượng * Đơn giá
        BigDecimal thanhTien = chiTiet.getDonGia().multiply(BigDecimal.valueOf(chiTiet.getSoLuong()));
        chiTiet.setThanhTien(thanhTien);

        // Thông tin dịch vụ
        DichVu dichVu = new DichVu();
        dichVu.setMaDichVu(rs.getInt("MaDichVu"));
        dichVu.setTenDichVu(rs.getString("TenDichVu"));
        dichVu.setGia(rs.getBigDecimal("Gia"));
        chiTiet.setDichVu(dichVu);

        // Thông tin nhân viên nếu có
        if (maNhanVien > 0) {
            NhanVien nhanVien = new NhanVien();
            nhanVien.setMaNhanVien(maNhanVien);
            nhanVien.setHoTen(rs.getString("TenNhanVien"));
            chiTiet.setNhanVien(nhanVien);
        }

        return chiTiet;
    }
}