// NhapNguyenLieuRepository.java
package Repository;

import Data.DataConnection;
import Model.NhapNguyenLieu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhapNguyenLieuRepository {

    public List<NhapNguyenLieu> getAll() throws SQLException {
        List<NhapNguyenLieu> list = new ArrayList<>();
        String sql = "SELECT nnl.*, nl.TenNguyenLieu as TenNguyenLieuGoc FROM NhapNguyenLieu nnl " +
                    "LEFT JOIN NguyenLieu nl ON nnl.MaNguyenLieu = nl.MaNguyenLieu " +
                    "ORDER BY nnl.NgayNhap DESC, nnl.MaNhap DESC";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql); 
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToNhapNguyenLieu(rs));
            }
        }
        return list;
    }

    public NhapNguyenLieu getById(int maNhap) throws SQLException {
        String sql = "SELECT nnl.*, nl.TenNguyenLieu as TenNguyenLieuGoc FROM NhapNguyenLieu nnl " +
                    "LEFT JOIN NguyenLieu nl ON nnl.MaNguyenLieu = nl.MaNguyenLieu " +
                    "WHERE nnl.MaNhap = ?";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNhap);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhapNguyenLieu(rs);
                }
            }
        }
        return null;
    }

    public List<NhapNguyenLieu> getByMaNguyenLieu(int maNguyenLieu) throws SQLException {
        List<NhapNguyenLieu> list = new ArrayList<>();
        String sql = "SELECT nnl.*, nl.TenNguyenLieu as TenNguyenLieuGoc FROM NhapNguyenLieu nnl " +
                    "LEFT JOIN NguyenLieu nl ON nnl.MaNguyenLieu = nl.MaNguyenLieu " +
                    "WHERE nnl.MaNguyenLieu = ? " +
                    "ORDER BY nnl.NgayNhap DESC";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNguyenLieu);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhapNguyenLieu(rs));
                }
            }
        }
        return list;
    }

    public List<NhapNguyenLieu> getByDateRange(Date fromDate, Date toDate) throws SQLException {
        List<NhapNguyenLieu> list = new ArrayList<>();
        String sql = "SELECT nnl.*, nl.TenNguyenLieu as TenNguyenLieuGoc FROM NhapNguyenLieu nnl " +
                    "LEFT JOIN NguyenLieu nl ON nnl.MaNguyenLieu = nl.MaNguyenLieu " +
                    "WHERE nnl.NgayNhap BETWEEN ? AND ? " +
                    "ORDER BY nnl.NgayNhap DESC";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhapNguyenLieu(rs));
                }
            }
        }
        return list;
    }

    // PHƯƠNG THỨC MỚI: Tìm theo tháng năm
    public List<NhapNguyenLieu> getByThangNam(int thang, int nam) throws SQLException {
        List<NhapNguyenLieu> list = new ArrayList<>();
        // Access sử dụng MONTH() và YEAR() cho Date fields
        String sql = "SELECT nnl.*, nl.TenNguyenLieu as TenNguyenLieuGoc FROM NhapNguyenLieu nnl " +
                    "LEFT JOIN NguyenLieu nl ON nnl.MaNguyenLieu = nl.MaNguyenLieu " +
                    "WHERE MONTH(nnl.NgayNhap) = ? AND YEAR(nnl.NgayNhap) = ? " +
                    "ORDER BY nnl.NgayNhap DESC";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhapNguyenLieu(rs));
                }
            }
        }
        return list;
    }

    // PHƯƠNG THỨC MỚI: Tìm theo năm
    public List<NhapNguyenLieu> getByNam(int nam) throws SQLException {
        List<NhapNguyenLieu> list = new ArrayList<>();
        String sql = "SELECT nnl.*, nl.TenNguyenLieu as TenNguyenLieuGoc FROM NhapNguyenLieu nnl " +
                    "LEFT JOIN NguyenLieu nl ON nnl.MaNguyenLieu = nl.MaNguyenLieu " +
                    "WHERE YEAR(nnl.NgayNhap) = ? " +
                    "ORDER BY nnl.NgayNhap DESC";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhapNguyenLieu(rs));
                }
            }
        }
        return list;
    }

    public boolean insert(NhapNguyenLieu nhapNL) throws SQLException {
        String sql = "INSERT INTO NhapNguyenLieu (MaNguyenLieu, NgayNhap, TenNguyenLieu, DonViTinh, SoLuong, DonGia, NguonNhap) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setNhapNguyenLieuParameters(stmt, nhapNL);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        nhapNL.setMaNhap(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean update(NhapNguyenLieu nhapNL) throws SQLException {
        String sql = "UPDATE NhapNguyenLieu SET MaNguyenLieu=?, NgayNhap=?, TenNguyenLieu=?, DonViTinh=?, SoLuong=?, DonGia=?, NguonNhap=? "
                + "WHERE MaNhap=?";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setNhapNguyenLieuParameters(stmt, nhapNL);
            stmt.setInt(8, nhapNL.getMaNhap());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int maNhap) throws SQLException {
        String sql = "DELETE FROM NhapNguyenLieu WHERE MaNhap = ?";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNhap);
            return stmt.executeUpdate() > 0;
        }
    }

    // PHƯƠNG THỨC MỚI: Tính tổng tiền nhập theo tháng năm
    public java.math.BigDecimal getTongTienNhapTheoThangNam(int thang, int nam) throws SQLException {
        String sql = "SELECT SUM(SoLuong * DonGia) as TongTien FROM NhapNguyenLieu " +
                    "WHERE MONTH(NgayNhap) = ? AND YEAR(NgayNhap) = ?";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    java.math.BigDecimal tongTien = rs.getBigDecimal("TongTien");
                    return tongTien != null ? tongTien : java.math.BigDecimal.ZERO;
                }
            }
        }
        return java.math.BigDecimal.ZERO;
    }

    // PHƯƠNG THỨC MỚI: Tính tổng số lượng nhập theo nguyên liệu và thời gian
    public int getTongSoLuongNhap(int maNguyenLieu, Date fromDate, Date toDate) throws SQLException {
        String sql = "SELECT SUM(SoLuong) as TongSoLuong FROM NhapNguyenLieu " +
                    "WHERE MaNguyenLieu = ? AND NgayNhap BETWEEN ? AND ?";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNguyenLieu);
            stmt.setDate(2, fromDate);
            stmt.setDate(3, toDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TongSoLuong");
                }
            }
        }
        return 0;
    }

    // PHƯƠNG THỨC MỚI: Lấy các nguồn nhập distinct
    public List<String> getDanhSachNguonNhap() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT NguonNhap FROM NhapNguyenLieu WHERE NguonNhap IS NOT NULL ORDER BY NguonNhap";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("NguonNhap"));
            }
        }
        return list;
    }

    // PHƯƠNG THỨC MỚI: Tìm kiếm theo tên nguyên liệu
    public List<NhapNguyenLieu> searchByTenNguyenLieu(String tenNguyenLieu) throws SQLException {
        List<NhapNguyenLieu> list = new ArrayList<>();
        String sql = "SELECT nnl.*, nl.TenNguyenLieu as TenNguyenLieuGoc FROM NhapNguyenLieu nnl " +
                    "LEFT JOIN NguyenLieu nl ON nnl.MaNguyenLieu = nl.MaNguyenLieu " +
                    "WHERE nnl.TenNguyenLieu LIKE ? OR nl.TenNguyenLieu LIKE ? " +
                    "ORDER BY nnl.NgayNhap DESC";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + tenNguyenLieu + "%");
            stmt.setString(2, "%" + tenNguyenLieu + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhapNguyenLieu(rs));
                }
            }
        }
        return list;
    }

    private void setNhapNguyenLieuParameters(PreparedStatement stmt, NhapNguyenLieu nhapNL) throws SQLException {
        stmt.setInt(1, nhapNL.getMaNguyenLieu());
        stmt.setDate(2, Date.valueOf(nhapNL.getNgayNhap()));
        stmt.setString(3, nhapNL.getTenNguyenLieu());
        stmt.setString(4, nhapNL.getDonViTinh());
        stmt.setInt(5, nhapNL.getSoLuong());
        stmt.setBigDecimal(6, nhapNL.getDonGia());
        stmt.setString(7, nhapNL.getNguonNhap());
    }

    private NhapNguyenLieu mapResultSetToNhapNguyenLieu(ResultSet rs) throws SQLException {
        NhapNguyenLieu nhapNL = new NhapNguyenLieu(
                rs.getInt("MaNhap"),
                rs.getInt("MaNguyenLieu"),
                rs.getDate("NgayNhap").toLocalDate(),
                rs.getString("TenNguyenLieu"),
                rs.getString("DonViTinh"),
                rs.getInt("SoLuong"),
                rs.getBigDecimal("DonGia"),
                rs.getString("NguonNhap")
        );

        // Xử lý các giá trị có thể null
        if (rs.getInt("MaNguyenLieu") == 0 && rs.wasNull()) {
            nhapNL.setMaNguyenLieu(null);
        }

        return nhapNL;
    }
}