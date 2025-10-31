package Repository;

import Data.DataConnection;
import Model.Giuong;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiuongRepository {

    public List<Giuong> getAll() throws SQLException {
        List<Giuong> list = new ArrayList<>();
        String sql = "SELECT * FROM Giuong ORDER BY SoHieu";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Giuong giuong = mapResultSetToGiuong(rs);
                list.add(giuong);
            }
        }
        return list;
    }

    public Giuong getById(int maGiuong) throws SQLException {
        String sql = "SELECT * FROM Giuong WHERE MaGiuong = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maGiuong);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGiuong(rs);
                }
            }
        }
        return null;
    }

    public boolean insert(Giuong giuong) throws SQLException {
        String sql = "INSERT INTO Giuong (SoHieu, TrangThai, GhiChu) VALUES (?, ?, ?)";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, giuong.getSoHieu());
            stmt.setString(2, giuong.getTrangThai());
            stmt.setString(3, giuong.getGhiChu());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean update(Giuong giuong) throws SQLException {
        String sql = "UPDATE Giuong SET SoHieu = ?, TrangThai = ?, GhiChu = ? WHERE MaGiuong = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, giuong.getSoHieu());
            stmt.setString(2, giuong.getTrangThai());
            stmt.setString(3, giuong.getGhiChu());
            stmt.setInt(4, giuong.getMaGiuong());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int maGiuong) throws SQLException {
        String sql = "DELETE FROM Giuong WHERE MaGiuong = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maGiuong);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateTrangThai(int maGiuong, String trangThai) throws SQLException {
        String sql = "UPDATE Giuong SET TrangThai = ? WHERE MaGiuong = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trangThai);
            stmt.setInt(2, maGiuong);

            return stmt.executeUpdate() > 0;
        }
    }

    // Các phương thức lọc theo trạng thái
    public List<Giuong> getGiuongTrong() throws SQLException {
        return getGiuongByTrangThai("Trống");
    }

    public List<Giuong> getGiuongDaDat() throws SQLException {
        return getGiuongByTrangThai("Đã đặt");
    }

    public List<Giuong> getGiuongDangPhucVu() throws SQLException {
        return getGiuongByTrangThai("Đang phục vụ");
    }

    public List<Giuong> getGiuongDangSuDung() throws SQLException {
        return getGiuongByTrangThai("Đang sử dụng");
    }

    public List<Giuong> getGiuongBaoTri() throws SQLException {
        return getGiuongByTrangThai("Bảo trì");
    }

    private List<Giuong> getGiuongByTrangThai(String trangThai) throws SQLException {
        List<Giuong> list = new ArrayList<>();
        String sql = "SELECT * FROM Giuong WHERE TrangThai = ? ORDER BY SoHieu";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trangThai);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Giuong giuong = mapResultSetToGiuong(rs);
                    list.add(giuong);
                }
            }
        }
        return list;
    }

    private Giuong mapResultSetToGiuong(ResultSet rs) throws SQLException {
        Giuong giuong = new Giuong();
        giuong.setMaGiuong(rs.getInt("MaGiuong"));
        giuong.setSoHieu(rs.getString("SoHieu"));
        giuong.setTrangThai(rs.getString("TrangThai"));
        giuong.setGhiChu(rs.getString("GhiChu"));
        return giuong;
    }
}
