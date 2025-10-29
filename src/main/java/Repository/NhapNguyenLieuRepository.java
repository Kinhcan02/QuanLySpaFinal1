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
        String sql = "SELECT * FROM NhapNguyenLieu";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToNhapNguyenLieu(rs));
            }
        }
        return list;
    }

    public NhapNguyenLieu getById(int maNhap) throws SQLException {
        String sql = "SELECT * FROM NhapNguyenLieu WHERE MaNhap = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        String sql = "SELECT * FROM NhapNguyenLieu WHERE MaNguyenLieu = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        String sql = "SELECT * FROM NhapNguyenLieu WHERE NgayNhap BETWEEN ? AND ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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

    public boolean insert(NhapNguyenLieu nhapNL) throws SQLException {
        String sql = "INSERT INTO NhapNguyenLieu (MaNguyenLieu, NgayNhap, TenNguyenLieu, DonViTinh, SoLuong, DonGia, NguonNhap) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            setNhapNguyenLieuParameters(stmt, nhapNL);
            stmt.setInt(8, nhapNL.getMaNhap());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int maNhap) throws SQLException {
        String sql = "DELETE FROM NhapNguyenLieu WHERE MaNhap = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNhap);
            return stmt.executeUpdate() > 0;
        }
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
        return new NhapNguyenLieu(
                rs.getInt("MaNhap"),
                rs.getInt("MaNguyenLieu"),
                rs.getDate("NgayNhap").toLocalDate(),
                rs.getString("TenNguyenLieu"),
                rs.getString("DonViTinh"),
                rs.getInt("SoLuong"),
                rs.getBigDecimal("DonGia"),
                rs.getString("NguonNhap")
        );
    }
}