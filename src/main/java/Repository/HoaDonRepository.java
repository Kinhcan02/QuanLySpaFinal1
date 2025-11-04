package Repository;

import Data.DataConnection;
import Model.HoaDon;
import Model.ChiTietHoaDon;
import Model.DichVu;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonRepository {

    public List<HoaDon> getAll() throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT hd.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien "
                + "FROM HoaDon hd "
                + "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang "
                + "LEFT JOIN NhanVien nv ON hd.MaNhanVienLap = nv.MaNhanVien "
                + "ORDER BY hd.NgayLap DESC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                HoaDon hoaDon = mapResultSetToHoaDon(rs);
                // Load chi tiết hóa đơn
                hoaDon.setChiTietHoaDon(getChiTietByMaHoaDon(hoaDon.getMaHoaDon()));
                list.add(hoaDon);
            }
        }
        return list;
    }

    // REMOVE THIS DUPLICATE METHOD - IT'S CAUSING THE ERROR
    /*
    public HoaDon getById(Integer maHoaDon) throws SQLException {
        String sql = "SELECT * FROM HoaDon WHERE MaHoaDon = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    HoaDon hoaDon = mapResultSetToHoaDon(rs);
                    // Load chi tiết hóa đơn
                    hoaDon.setChiTietHoaDon(getChiTietHoaDon(maHoaDon)); // ERROR: getChiTietHoaDon doesn't exist
                    return hoaDon;
                }
            }
        }
        return null;
    }
    */

    private void deleteChiTietHoaDon(Connection conn, Integer maHoaDon) throws SQLException {
        String sql = "DELETE FROM ChiTietHoaDon WHERE MaHoaDon = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maHoaDon);
            stmt.executeUpdate();
        }
    }

    public HoaDon getById(int maHoaDon) throws SQLException {
        String sql = "SELECT hd.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien "
                + "FROM HoaDon hd "
                + "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang "
                + "LEFT JOIN NhanVien nv ON hd.MaNhanVienLap = nv.MaNhanVien "
                + "WHERE hd.MaHoaDon = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
                + "FROM HoaDon hd "
                + "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang "
                + "LEFT JOIN NhanVien nv ON hd.MaNhanVienLap = nv.MaNhanVien "
                + "WHERE hd.MaKhachHang = ? "
                + "ORDER BY hd.NgayLap DESC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        String sql = "SELECT hdct.*, dv.TenDichVu, dv.Gia "
                + "FROM ChiTietHoaDon hdct "
                + "JOIN DichVu dv ON hdct.MaDichVu = dv.MaDichVu "
                + "WHERE hdct.MaHoaDon = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon chiTiet = new ChiTietHoaDon();
                    chiTiet.setMaCTHD(rs.getInt("MaCTHD"));
                    chiTiet.setMaHoaDon(rs.getInt("MaHoaDon"));
                    chiTiet.setMaDichVu(rs.getInt("MaDichVu"));
                    chiTiet.setSoLuong(rs.getInt("SoLuong"));
                    chiTiet.setDonGia(rs.getBigDecimal("DonGia"));
                    chiTiet.setThanhTien(rs.getBigDecimal("ThanhTien"));

                    // Thông tin dịch vụ
                    DichVu dichVu = new DichVu();
                    dichVu.setMaDichVu(rs.getInt("MaDichVu"));
                    dichVu.setTenDichVu(rs.getString("TenDichVu"));
                    dichVu.setGia(rs.getBigDecimal("Gia"));
                    chiTiet.setDichVu(dichVu);

                    list.add(chiTiet);
                }
            }
        }
        return list;
    }

    public boolean insert(HoaDon hoaDon) throws SQLException {
        String sql = "INSERT INTO HoaDon (MaKhachHang, NgayLap, TongTien, MaNhanVienLap, GhiChu) "
                + "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, hoaDon.getMaKhachHang());
            stmt.setTimestamp(2, Timestamp.valueOf(hoaDon.getNgayLap()));
            stmt.setBigDecimal(3, hoaDon.getTongTien());

            if (hoaDon.getMaNhanVienLap() != null) {
                stmt.setInt(4, hoaDon.getMaNhanVienLap());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setString(5, hoaDon.getGhiChu());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int generatedMaHoaDon = rs.getInt(1);
                    hoaDon.setMaHoaDon(generatedMaHoaDon);

                    // Insert chi tiết hóa đơn
                    if (hoaDon.getChiTietHoaDon() != null && !hoaDon.getChiTietHoaDon().isEmpty()) {
                        for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDon()) {
                            insertChiTiet(conn, generatedMaHoaDon, chiTiet);
                        }
                    }

                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    private boolean insertChiTiet(Connection conn, int maHoaDon, ChiTietHoaDon chiTiet) throws SQLException {
        String sql = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaDichVu, SoLuong, DonGia) "
                + // BỎ CỘT ThanhTien
                "VALUES (?, ?, ?, ?)";

        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maHoaDon);
            stmt.setInt(2, chiTiet.getMaDichVu());
            stmt.setInt(3, chiTiet.getSoLuong());
            stmt.setBigDecimal(4, chiTiet.getDonGia());
            // KHÔNG set ThanhTien vì nó là computed column

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public boolean update(HoaDon hoaDon) throws SQLException {
        String sql = "UPDATE HoaDon SET MaKhachHang=?, NgayLap=?, TongTien=?, MaNhanVienLap=?, GhiChu=? WHERE MaHoaDon=?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, hoaDon.getMaKhachHang());
            stmt.setTimestamp(2, Timestamp.valueOf(hoaDon.getNgayLap()));
            stmt.setBigDecimal(3, hoaDon.getTongTien());

            if (hoaDon.getMaNhanVienLap() != null) {
                stmt.setInt(4, hoaDon.getMaNhanVienLap());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setString(5, hoaDon.getGhiChu());
            stmt.setInt(6, hoaDon.getMaHoaDon());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Xóa chi tiết cũ và thêm mới
                deleteChiTietByMaHoaDon(conn, hoaDon.getMaHoaDon());

                // Insert chi tiết hóa đơn mới
                if (hoaDon.getChiTietHoaDon() != null && !hoaDon.getChiTietHoaDon().isEmpty()) {
                    for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDon()) {
                        insertChiTiet(conn, hoaDon.getMaHoaDon(), chiTiet);
                    }
                }

                conn.commit();
                return true;
            }
            conn.rollback();
            return false;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
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
        PreparedStatement stmt = null;

        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            // Xóa chi tiết trước
            deleteChiTietByMaHoaDon(conn, maHoaDon);

            // Xóa hóa đơn
            String sql = "DELETE FROM HoaDon WHERE MaHoaDon = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, maHoaDon);

            int affectedRows = stmt.executeUpdate();
            conn.commit();

            return affectedRows > 0;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public BigDecimal getTongDoanhThuTheoThang(int thang, int nam) throws SQLException {
        String sql = "SELECT SUM(TongTien) as TongDoanhThu FROM HoaDon "
                + "WHERE MONTH(NgayLap) = ? AND YEAR(NgayLap) = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("TongDoanhThu");
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public List<HoaDon> getHoaDonTheoKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT hd.*, kh.HoTen as TenKhachHang, nv.HoTen as TenNhanVien "
                + "FROM HoaDon hd "
                + "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang "
                + "LEFT JOIN NhanVien nv ON hd.MaNhanVienLap = nv.MaNhanVien "
                + "WHERE hd.NgayLap BETWEEN ? AND ? "
                + "ORDER BY hd.NgayLap DESC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
}