package Repository;

import Model.TaiKhoan;
import Data.DataConnection;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanRepository {

    // READ - Lấy tài khoản theo tên đăng nhập
    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tenDangNhap);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new TaiKhoan(
                        rs.getInt("MaTaiKhoan"),
                        rs.getString("TenDangNhap"),
                        rs.getString("MatKhauHash"),
                        rs.getString("VaiTro"),
                        rs.getInt("MaNhanVien")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ - Lấy tài khoản theo mã
    public TaiKhoan findById(int maTaiKhoan) {
        String sql = "SELECT * FROM TaiKhoan WHERE MaTaiKhoan = ?";
        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maTaiKhoan);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new TaiKhoan(
                        rs.getInt("MaTaiKhoan"),
                        rs.getString("TenDangNhap"),
                        rs.getString("MatKhauHash"),
                        rs.getString("VaiTro"),
                        rs.getInt("MaNhanVien")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ - Lấy tất cả tài khoản
    public List<TaiKhoan> findAll() {
        List<TaiKhoan> danhSachTaiKhoan = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan ORDER BY MaTaiKhoan";
        
        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TaiKhoan taiKhoan = new TaiKhoan(
                        rs.getInt("MaTaiKhoan"),
                        rs.getString("TenDangNhap"),
                        rs.getString("MatKhauHash"),
                        rs.getString("VaiTro"),
                        rs.getInt("MaNhanVien")
                );
                danhSachTaiKhoan.add(taiKhoan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachTaiKhoan;
    }

    // CREATE - Thêm tài khoản mới
    public boolean insert(TaiKhoan taiKhoan) {
        // Access sử dụng AUTOINCREMENT thay vì IDENTITY
        String sql = "INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, VaiTro, MaNhanVien) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, taiKhoan.getTenDangNhap());
            stmt.setString(2, hashPassword(taiKhoan.getMatKhauHash())); // Hash mật khẩu trước khi lưu
            stmt.setString(3, taiKhoan.getVaiTro());
            
            // Xử lý giá trị NULL cho MaNhanVien
            if (taiKhoan.getMaNhanVien() != 0) {
                stmt.setInt(4, taiKhoan.getMaNhanVien());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE - Cập nhật tài khoản
    public boolean update(TaiKhoan taiKhoan) {
        String sql = "UPDATE TaiKhoan SET TenDangNhap = ?, VaiTro = ?, MaNhanVien = ? WHERE MaTaiKhoan = ?";
        
        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, taiKhoan.getTenDangNhap());
            stmt.setString(2, taiKhoan.getVaiTro());
            
            // Xử lý giá trị NULL cho MaNhanVien
            if (taiKhoan.getMaNhanVien() != 0) {
                stmt.setInt(3, taiKhoan.getMaNhanVien());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setInt(4, taiKhoan.getMaTaiKhoan());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE - Đổi mật khẩu
    public boolean changePassword(int maTaiKhoan, String matKhauMoi) {
        String sql = "UPDATE TaiKhoan SET MatKhauHash = ? WHERE MaTaiKhoan = ?";
        
        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hashPassword(matKhauMoi));
            stmt.setInt(2, maTaiKhoan);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE - Xóa tài khoản
    public boolean delete(int maTaiKhoan) {
        String sql = "DELETE FROM TaiKhoan WHERE MaTaiKhoan = ?";
        
        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maTaiKhoan);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra tên đăng nhập đã tồn tại chưa (dùng cho thêm mới)
    public boolean isTenDangNhapExists(String tenDangNhap) {
        return findByTenDangNhap(tenDangNhap) != null;
    }

    // Kiểm tra tên đăng nhập đã tồn tại (trừ tài khoản hiện tại - dùng cho cập nhật)
    public boolean isTenDangNhapExists(String tenDangNhap, int maTaiKhoanHienTai) {
        String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE TenDangNhap = ? AND MaTaiKhoan <> ?";
        
        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tenDangNhap);
            stmt.setInt(2, maTaiKhoanHienTai);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra đăng nhập
    public boolean kiemTraDangNhap(String tenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = findByTenDangNhap(tenDangNhap);
        if (taiKhoan != null) {
            String storedPassword = taiKhoan.getMatKhauHash();

            // Thử so sánh trực tiếp trước
            if (storedPassword.equals(matKhau)) {
                return true;
            }

            // Nếu không khớp, thử so sánh với hash
            String matKhauHash = hashPassword(matKhau);
            return storedPassword.equals(matKhauHash);
        }
        return false;
    }

    // Hàm băm mật khẩu sử dụng SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}