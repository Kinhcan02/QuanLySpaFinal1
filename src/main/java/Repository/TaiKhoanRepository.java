package Repository;

import Model.TaiKhoan;
import Data.DataConnection;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TaiKhoanRepository {

    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?";
        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

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
