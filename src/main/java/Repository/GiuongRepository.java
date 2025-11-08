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

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql); 
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Giuong giuong = mapResultSetToGiuong(rs);
                list.add(giuong);
            }
        }
        return list;
    }

    public Giuong getById(int maGiuong) throws SQLException {
        String sql = "SELECT * FROM Giuong WHERE MaGiuong = ?";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maGiuong);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGiuong(rs);
                }
            }
        }
        return null;
    }

    public Giuong getBySoHieu(String soHieu) throws SQLException {
        String sql = "SELECT * FROM Giuong WHERE SoHieu = ?";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, soHieu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGiuong(rs);
                }
            }
        }
        return null;
    }

    public boolean insert(Giuong giuong) throws SQLException {
        // SỬA: Chỉ insert các cột có trong database (bỏ NgayTao)
        String sql = "INSERT INTO Giuong (SoHieu, TrangThai, GhiChu) VALUES (?, ?, ?)";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, giuong.getSoHieu());
            stmt.setString(2, giuong.getTrangThai());
            
            // Xử lý giá trị null cho GhiChu
            if (giuong.getGhiChu() != null) {
                stmt.setString(3, giuong.getGhiChu());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        giuong.setMaGiuong(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean update(Giuong giuong) throws SQLException {
        // SỬA: Chỉ update các cột có trong database (bỏ NgayCapNhat)
        String sql = "UPDATE Giuong SET SoHieu = ?, TrangThai = ?, GhiChu = ? WHERE MaGiuong = ?";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, giuong.getSoHieu());
            stmt.setString(2, giuong.getTrangThai());
            
            // Xử lý giá trị null cho GhiChu
            if (giuong.getGhiChu() != null) {
                stmt.setString(3, giuong.getGhiChu());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            
            stmt.setInt(4, giuong.getMaGiuong());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int maGiuong) throws SQLException {
        // Kiểm tra xem giường có đang được sử dụng không trước khi xóa
        if (isGiuongInUse(maGiuong)) {
            throw new SQLException("Không thể xóa giường vì đang được sử dụng trong các đơn đặt lịch");
        }
        
        String sql = "DELETE FROM Giuong WHERE MaGiuong = ?";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maGiuong);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean isGiuongInUse(int maGiuong) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DatLich WHERE MaGiuong = ? AND TrangThai IN ('Chờ xác nhận', 'Đã xác nhận', 'Đang thực hiện')";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maGiuong);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean updateTrangThai(int maGiuong, String trangThai) throws SQLException {
        // SỬA: Chỉ update trạng thái, không update NgayCapNhat
        String sql = "UPDATE Giuong SET TrangThai = ? WHERE MaGiuong = ?";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trangThai);
            stmt.setInt(2, maGiuong);

            return stmt.executeUpdate() > 0;
        }
    }

    // Kiểm tra số hiệu giường đã tồn tại chưa (dùng cho thêm mới)
    public boolean isSoHieuExists(String soHieu) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Giuong WHERE SoHieu = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, soHieu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Kiểm tra số hiệu giường đã tồn tại (trừ giường hiện tại - dùng cho cập nhật)
    public boolean isSoHieuExists(String soHieu, int maGiuongHienTai) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Giuong WHERE SoHieu = ? AND MaGiuong <> ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, soHieu);
            stmt.setInt(2, maGiuongHienTai);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
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

    public List<Giuong> getGiuongCoTheDat() throws SQLException {
        // Lấy các giường có thể đặt (Trống hoặc Đã đặt nhưng có thể override)
        List<Giuong> list = new ArrayList<>();
        String sql = "SELECT * FROM Giuong WHERE TrangThai IN ('Trống', 'Đã đặt') ORDER BY SoHieu";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Giuong giuong = mapResultSetToGiuong(rs);
                list.add(giuong);
            }
        }
        return list;
    }

    public List<Giuong> searchGiuong(String keyword) throws SQLException {
        List<Giuong> list = new ArrayList<>();
        String sql = "SELECT * FROM Giuong WHERE SoHieu LIKE ? OR GhiChu LIKE ? ORDER BY SoHieu";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Giuong giuong = mapResultSetToGiuong(rs);
                    list.add(giuong);
                }
            }
        }
        return list;
    }

    public int getTongSoGiuong() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Giuong";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getSoGiuongTheoTrangThai(String trangThai) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Giuong WHERE TrangThai = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, trangThai);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private List<Giuong> getGiuongByTrangThai(String trangThai) throws SQLException {
        List<Giuong> list = new ArrayList<>();
        String sql = "SELECT * FROM Giuong WHERE TrangThai = ? ORDER BY SoHieu";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        
        // Xử lý giá trị null cho GhiChu
        String ghiChu = rs.getString("GhiChu");
        if (!rs.wasNull()) {
            giuong.setGhiChu(ghiChu);
        }
        
        // SỬA: Bỏ hoàn toàn phần xử lý NgayTao và NgayCapNhat vì các cột này không tồn tại
        // Chỉ lấy các cột có trong database: MaGiuong, SoHieu, TrangThai, GhiChu
        
        return giuong;
    }
}