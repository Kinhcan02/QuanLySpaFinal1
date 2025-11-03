package Repository;

import Data.DataConnection;
import Model.DatLich;
import Model.DatLichChiTiet;
import Model.DichVu;
import Model.NhanVien;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatLichRepository {

    public List<DatLich> getAll() throws SQLException {
        List<DatLich> list = new ArrayList<>();
        String sql = "SELECT dl.*, "
                + "(SELECT SUM(dv.ThoiGian) FROM DatLich_ChiTiet ct "
                + "JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu WHERE ct.MaLich = dl.MaLich) as TongThoiGian "
                + "FROM DatLich dl ORDER BY dl.NgayDat DESC, dl.GioDat DESC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DatLich datLich = mapResultSetToDatLich(rs);
                // Load chi tiết dịch vụ
                datLich.setDanhSachDichVu(getChiTietByMaLich(datLich.getMaLich()));
                list.add(datLich);
            }
        }
        return list;
    }

    public DatLich getById(int maLich) throws SQLException {
        String sql = "SELECT dl.*, "
                + "(SELECT SUM(dv.ThoiGian) FROM DatLich_ChiTiet ct "
                + "JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu WHERE ct.MaLich = dl.MaLich) as TongThoiGian "
                + "FROM DatLich dl WHERE dl.MaLich = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maLich);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DatLich datLich = mapResultSetToDatLich(rs);
                    // Load chi tiết dịch vụ
                    datLich.setDanhSachDichVu(getChiTietByMaLich(maLich));
                    return datLich;
                }
            }
        }
        return null;
    }

    public List<DatLich> getByNgay(LocalDate ngay) throws SQLException {
        List<DatLich> list = new ArrayList<>();
        String sql = "SELECT dl.*, "
                + "(SELECT SUM(dv.ThoiGian) FROM DatLich_ChiTiet ct "
                + "JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu WHERE ct.MaLich = dl.MaLich) as TongThoiGian "
                + "FROM DatLich dl WHERE dl.NgayDat = ? ORDER BY dl.GioDat ASC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(ngay));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DatLich datLich = mapResultSetToDatLich(rs);
                    // Load chi tiết dịch vụ
                    datLich.setDanhSachDichVu(getChiTietByMaLich(datLich.getMaLich()));
                    list.add(datLich);
                }
            }
        }
        return list;
    }

    public List<DatLichChiTiet> getChiTietByMaLich(int maLich) throws SQLException {
        List<DatLichChiTiet> list = new ArrayList<>();
        String sql = "SELECT ct.*, dv.TenDichVu, dv.ThoiGian, dv.Gia, "
                + "nv.HoTen as TenNhanVien, nv.MaNhanVien "
                + "FROM DatLich_ChiTiet ct "
                + "JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu "
                + "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien "
                + "WHERE ct.MaLich = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maLich);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DatLichChiTiet chiTiet = new DatLichChiTiet();
                    chiTiet.setMaCTDL(rs.getInt("MaCTDL"));
                    chiTiet.setMaLich(rs.getInt("MaLich"));
                    chiTiet.setMaDichVu(rs.getInt("MaDichVu"));
                    chiTiet.setMaNhanVien(rs.getInt("MaNhanVien"));
                    chiTiet.setGhiChu(rs.getString("GhiChu"));
                    chiTiet.setNgayTao(rs.getTimestamp("NgayTao").toLocalDateTime());

                    // Thông tin dịch vụ
                    DichVu dichVu = new DichVu();
                    dichVu.setMaDichVu(rs.getInt("MaDichVu"));
                    dichVu.setTenDichVu(rs.getString("TenDichVu"));
                    dichVu.setThoiGian(rs.getInt("ThoiGian"));
                    dichVu.setGia(rs.getBigDecimal("Gia"));
                    chiTiet.setDichVu(dichVu);

                    // Thông tin nhân viên nếu có
                    if (rs.getInt("MaNhanVien") != 0) {
                        NhanVien nhanVien = new NhanVien();
                        nhanVien.setMaNhanVien(rs.getInt("MaNhanVien"));
                        nhanVien.setHoTen(rs.getString("TenNhanVien"));
                        chiTiet.setNhanVien(nhanVien);
                    }

                    list.add(chiTiet);
                }
            }
        }
        return list;
    }

    public boolean insert(DatLich datLich) throws SQLException {
        // Cập nhật SQL để thêm SoLuongNguoi
        String sql = "INSERT INTO DatLich (MaKhachHang, NgayDat, GioDat, TrangThai, MaGiuong, ThoiGianDuKien, GhiChu, MaNhanVienTao, SoLuongNguoi) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, datLich.getMaKhachHang());
            stmt.setDate(2, Date.valueOf(datLich.getNgayDat()));
            stmt.setTime(3, Time.valueOf(datLich.getGioDat()));
            stmt.setString(4, datLich.getTrangThai());

            if (datLich.getMaGiuong() != null) {
                stmt.setInt(5, datLich.getMaGiuong());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setInt(6, datLich.tinhTongThoiGian());
            stmt.setString(7, datLich.getGhiChu());

            // TODO: Set MaNhanVienTao từ session hiện tại
            stmt.setNull(8, Types.INTEGER);

            // Thêm SoLuongNguoi
            stmt.setInt(9, datLich.getSoLuongNguoi());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int generatedMaLich = rs.getInt(1);
                    datLich.setMaLich(generatedMaLich);

                    // Insert chi tiết dịch vụ
                    if (datLich.hasDichVu()) {
                        for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                            insertChiTiet(conn, generatedMaLich, chiTiet);
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

    private void insertChiTiet(Connection conn, int maLich, DatLichChiTiet chiTiet) throws SQLException {
        String sql = "INSERT INTO DatLich_ChiTiet (MaLich, MaDichVu, MaNhanVien, GhiChu) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLich);
            stmt.setInt(2, chiTiet.getMaDichVu());

            if (chiTiet.getMaNhanVien() != null) {
                stmt.setInt(3, chiTiet.getMaNhanVien());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, chiTiet.getGhiChu());
            stmt.executeUpdate();
        }
    }

    public boolean update(DatLich datLich) throws SQLException {
        // Cập nhật SQL để thêm SoLuongNguoi
        String sql = "UPDATE DatLich SET MaKhachHang=?, NgayDat=?, GioDat=?, TrangThai=?, "
                + "MaGiuong=?, ThoiGianDuKien=?, GhiChu=?, NgayCapNhat=SYSUTCDATETIME(), SoLuongNguoi=? WHERE MaLich=?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, datLich.getMaKhachHang());
            stmt.setDate(2, Date.valueOf(datLich.getNgayDat()));
            stmt.setTime(3, Time.valueOf(datLich.getGioDat()));
            stmt.setString(4, datLich.getTrangThai());

            if (datLich.getMaGiuong() != null) {
                stmt.setInt(5, datLich.getMaGiuong());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setInt(6, datLich.tinhTongThoiGian());
            stmt.setString(7, datLich.getGhiChu());
            // Thêm SoLuongNguoi
            stmt.setInt(8, datLich.getSoLuongNguoi());
            stmt.setInt(9, datLich.getMaLich());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Xóa chi tiết cũ và thêm mới
                deleteChiTietByMaLich(conn, datLich.getMaLich());

                // Insert chi tiết dịch vụ mới
                if (datLich.hasDichVu()) {
                    for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                        insertChiTiet(conn, datLich.getMaLich(), chiTiet);
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

    private void deleteChiTietByMaLich(Connection conn, int maLich) throws SQLException {
        String sql = "DELETE FROM DatLich_ChiTiet WHERE MaLich = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLich);
            stmt.executeUpdate();
        }
    }

    public boolean delete(int maLich) throws SQLException {
        String sql = "DELETE FROM DatLich WHERE MaLich = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maLich);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateTrangThai(int maLich, String trangThai) throws SQLException {
        String sql = "UPDATE DatLich SET TrangThai = ?, NgayCapNhat = SYSUTCDATETIME() WHERE MaLich = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trangThai);
            stmt.setInt(2, maLich);

            return stmt.executeUpdate() > 0;
        }
    }

    private DatLich mapResultSetToDatLich(ResultSet rs) throws SQLException {
        DatLich datLich = new DatLich();
        datLich.setMaLich(rs.getInt("MaLich"));
        datLich.setMaKhachHang(rs.getInt("MaKhachHang"));
        datLich.setNgayDat(rs.getDate("NgayDat").toLocalDate());
        datLich.setGioDat(rs.getTime("GioDat").toLocalTime());
        datLich.setTrangThai(rs.getString("TrangThai"));

        int maGiuong = rs.getInt("MaGiuong");
        if (!rs.wasNull()) {
            datLich.setMaGiuong(maGiuong);
        }

        datLich.setThoiGianDuKien(rs.getInt("ThoiGianDuKien"));
        datLich.setGhiChu(rs.getString("GhiChu"));

        // Thêm SoLuongNguoi
        datLich.setSoLuongNguoi(rs.getInt("SoLuongNguoi"));

        Timestamp ngayTao = rs.getTimestamp("NgayTao");
        if (ngayTao != null) {
            datLich.setNgayTao(ngayTao.toLocalDateTime());
        }

        Timestamp ngayCapNhat = rs.getTimestamp("NgayCapNhat");
        if (ngayCapNhat != null) {
            datLich.setNgayCapNhat(ngayCapNhat.toLocalDateTime());
        }

        int maNhanVienTao = rs.getInt("MaNhanVienTao");
        if (!rs.wasNull()) {
            datLich.setMaNhanVienTao(maNhanVienTao);
        }

        return datLich;
    }
}
