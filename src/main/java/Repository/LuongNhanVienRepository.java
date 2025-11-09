package Repository;

import Data.DataConnection;
import Model.LuongNhanVien;
import Model.NhanVien;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LuongNhanVienRepository {

    // ==================== PHƯƠNG THỨC TÍNH LƯƠNG MỚI - TRÁNH LỖI DATABASE ====================
    public boolean tinhLuongThang(Integer thang, Integer nam) {
        try {
            System.out.println("🔄 BẮT ĐẦU TÍNH LƯƠNG THÁNG " + thang + "/" + nam);

            // BƯỚC 2: TẠO CHI TIẾT TIỀN DỊCH VỤ
            taoChiTietTienDichVuChoThang(thang, nam);

            // BƯỚC 3: TÍNH VÀ CHÈN LƯƠNG MỚI
            return tinhVaChenLuongMoi(thang, nam);

        } catch (Exception e) {
            System.err.println("❌ LỖI TÍNH LƯƠNG: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== XÓA LƯƠNG CŨ TRONG THÁNG ====================
    private void xoaLuongThang(Integer thang, Integer nam) {
        String sql = "DELETE FROM TinhLuongNhanVien WHERE Thang = ? AND Nam = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            int deletedRows = stmt.executeUpdate();
            System.out.println("🗑️ Đã xóa " + deletedRows + " bản ghi lương cũ");

        } catch (SQLException e) {
            System.err.println("⚠️ Cảnh báo khi xóa lương cũ: " + e.getMessage());
            // Tiếp tục xử lý ngay cả khi có lỗi
        }
    }

    // ==================== TẠO CHI TIẾT TIỀN DỊCH VỤ ====================
    private void taoChiTietTienDichVuChoThang(Integer thang, Integer nam) {
        String sql = "INSERT INTO ChiTietTienDichVuCuaNhanVien (MaCTHD, MaDichVu, MaNhanVien, MaPhanTram, DonGiaThucTe, NgayTao) "
                + "SELECT cthd.MaCTHD, cthd.MaDichVu, cthd.MaNhanVien, pt.MaPhanTram, "
                + "       (cthd.SoLuong * cthd.DonGia * pt.TiLePhanTram / 100) as DonGiaThucTe, "
                + "       NOW() as NgayTao "
                + "FROM ChiTietHoaDon cthd "
                + "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon "
                + "INNER JOIN DichVu dv ON cthd.MaDichVu = dv.MaDichVu "
                + "INNER JOIN PhanTramDichVu pt ON dv.MaLoaiDV = pt.MaLoaiDV AND cthd.MaNhanVien = pt.MaNhanVien "
                + "WHERE MONTH(hd.NgayLap) = ? AND YEAR(hd.NgayLap) = ? "
                + "AND cthd.MaNhanVien IS NOT NULL "
                + "AND NOT EXISTS ("
                + "    SELECT 1 FROM ChiTietTienDichVuCuaNhanVien ct "
                + "    WHERE ct.MaCTHD = cthd.MaCTHD AND ct.MaNhanVien = cthd.MaNhanVien"
                + ")";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            int affectedRows = stmt.executeUpdate();
            System.out.println("📦 Đã tạo " + affectedRows + " chi tiết tiền dịch vụ");

        } catch (SQLException e) {
            System.err.println("⚠️ Cảnh báo khi tạo chi tiết tiền dịch vụ: " + e.getMessage());
        }
    }

    // ==================== TÍNH VÀ CHÈN LƯƠNG MỚI ====================
    private boolean tinhVaChenLuongMoi(Integer thang, Integer nam) {
        List<NhanVien> danhSachNhanVien = getAllNhanVien();
        int count = 0;

        for (NhanVien nv : danhSachNhanVien) {
            try {
                System.out.println("\n--- Xử lý nhân viên: " + nv.getHoTen() + " ---");

                // Tính tổng tiền dịch vụ
                BigDecimal tongTienDichVu = getTongTienDichVuByThangNam(nv.getMaNhanVien(), thang, nam);
                BigDecimal luongCanBan = nv.getLuongCanBan() != null ? nv.getLuongCanBan() : BigDecimal.ZERO;
                BigDecimal tongLuong = luongCanBan.add(tongTienDichVu);

                System.out.println("💰 Lương CB: " + luongCanBan + " | Tiền DV: " + tongTienDichVu + " | Tổng: " + tongLuong);

                // Chỉ tạo lương nếu có lương
                if (tongLuong.compareTo(BigDecimal.ZERO) > 0) {
                    if (chenLuongMoi(nv.getMaNhanVien(), thang, nam, luongCanBan, tongTienDichVu, tongLuong)) {
                        count++;
                        System.out.println("✅ ĐÃ TẠO LƯƠNG MỚI");
                    }
                } else {
                    System.out.println("⚠️ Bỏ qua - Không có lương cần tính");
                }

            } catch (Exception e) {
                System.err.println("❌ Lỗi khi xử lý nhân viên " + nv.getHoTen() + ": " + e.getMessage());
            }
        }

        System.out.println("\n✅ HOÀN THÀNH TÍNH LƯƠNG!");
        System.out.println("📊 Đã tạo " + count + " bản ghi lương mới");
        return count > 0;
    }

    // ==================== CHÈN LƯƠNG MỚI ====================
    private boolean chenLuongMoi(Integer maNhanVien, Integer thang, Integer nam,
            BigDecimal luongCanBan, BigDecimal tongTienDichVu, BigDecimal tongLuong) {
        String sql = "INSERT INTO TinhLuongNhanVien (MaNhanVien, Thang, Nam, LuongCanBan, TongTienDichVu, TongLuong, NgayTinhLuong, TrangThai, GhiChu) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);
            stmt.setBigDecimal(4, luongCanBan);
            stmt.setBigDecimal(5, tongTienDichVu);
            stmt.setBigDecimal(6, tongLuong);
            stmt.setTimestamp(7, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setString(8, "Chưa thanh toán");
            stmt.setString(9, "Tính tự động tháng " + thang + "/" + nam);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi chèn lương mới: " + e.getMessage());
            return false;
        }
    }

    // ==================== TÍNH TỔNG TIỀN DỊCH VỤ ====================
    private BigDecimal getTongTienDichVuByThangNam(Integer maNhanVien, Integer thang, Integer nam) {
        String sql = "SELECT SUM(ct.DonGiaThucTe) as TongTienDichVu "
                + "FROM ChiTietTienDichVuCuaNhanVien ct "
                + "INNER JOIN ChiTietHoaDon cthd ON ct.MaCTHD = cthd.MaCTHD "
                + "INNER JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon "
                + "WHERE ct.MaNhanVien = ? AND MONTH(hd.NgayLap) = ? AND YEAR(hd.NgayLap) = ? "
                + "AND ct.DonGiaThucTe > 0";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal("TongTienDichVu");
                return result != null ? result : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tính tổng tiền dịch vụ: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    // ==================== CÁC PHƯƠNG THỨC KHÁC ====================
    public List<LuongNhanVien> getAll() {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen, n.LuongCanBan FROM TinhLuongNhanVien l "
                + "LEFT JOIN NhanVien n ON l.MaNhanVien = n.MaNhanVien "
                + "ORDER BY l.Nam DESC, l.Thang DESC, n.HoTen";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToLuongNhanVien(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách lương: " + e.getMessage());
        }
        return list;
    }

    public List<LuongNhanVien> getByThangNam(Integer thang, Integer nam) {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen, n.LuongCanBan FROM TinhLuongNhanVien l "
                + "LEFT JOIN NhanVien n ON l.MaNhanVien = n.MaNhanVien "
                + "WHERE l.Thang = ? AND l.Nam = ? "
                + "ORDER BY n.HoTen";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToLuongNhanVien(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy lương theo tháng: " + e.getMessage());
        }
        return list;
    }

    // THÊM PHƯƠNG THỨC BỊ THIẾU
    public List<LuongNhanVien> getByNhanVienThangNam(Integer maNhanVien, Integer thang, Integer nam) {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen, n.LuongCanBan FROM TinhLuongNhanVien l "
                + "LEFT JOIN NhanVien n ON l.MaNhanVien = n.MaNhanVien "
                + "WHERE l.MaNhanVien = ? AND l.Thang = ? AND l.Nam = ? "
                + "ORDER BY l.Nam DESC, l.Thang DESC";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maNhanVien);
            stmt.setInt(2, thang);
            stmt.setInt(3, nam);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToLuongNhanVien(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy lương theo nhân viên và tháng: " + e.getMessage());
        }
        return list;
    }

    public boolean capNhatTrangThai(Integer maLuong, String trangThai) {
        String sql = "UPDATE TinhLuongNhanVien SET TrangThai = ? WHERE MaTinhLuong = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trangThai);
            stmt.setInt(2, maLuong);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        return false;
    }

    public boolean xoaLuong(Integer maLuong) {
        String sql = "DELETE FROM TinhLuongNhanVien WHERE MaTinhLuong = ?";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maLuong);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi xóa lương: " + e.getMessage());
        }
        return false;
    }

    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY HoTen";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getInt("MaNhanVien"));
                nv.setHoTen(rs.getString("HoTen"));
                nv.setLuongCanBan(rs.getBigDecimal("LuongCanBan"));
                list.add(nv);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy danh sách nhân viên: " + e.getMessage());
        }
        return list;
    }

    private LuongNhanVien mapResultSetToLuongNhanVien(ResultSet rs) throws SQLException {
        LuongNhanVien luong = new LuongNhanVien();
        luong.setMaLuong(rs.getInt("MaTinhLuong"));
        luong.setMaNhanVien(rs.getInt("MaNhanVien"));
        luong.setThang(rs.getInt("Thang"));
        luong.setNam(rs.getInt("Nam"));
        luong.setLuongCanBan(rs.getBigDecimal("LuongCanBan"));
        luong.setTongTienDichVu(rs.getBigDecimal("TongTienDichVu"));
        luong.setTongLuong(rs.getBigDecimal("TongLuong"));

        Timestamp ngayTinh = rs.getTimestamp("NgayTinhLuong");
        if (ngayTinh != null) {
            luong.setNgayTinhLuong(ngayTinh.toLocalDateTime());
        }

        luong.setTrangThai(rs.getString("TrangThai"));
        luong.setGhiChu(rs.getString("GhiChu"));

        // Tạo đối tượng nhân viên tham chiếu
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(rs.getInt("MaNhanVien"));
        nv.setHoTen(rs.getString("HoTen"));
        nv.setLuongCanBan(rs.getBigDecimal("LuongCanBan"));
        luong.setNhanVien(nv);

        return luong;
    }
    // ==================== LẤY LỊCH SỬ TÍNH LƯƠNG ====================

    public List<LuongNhanVien> getLichSuTinhLuong(Integer thang, Integer nam) {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen, n.LuongCanBan FROM TinhLuongNhanVien l "
                + "LEFT JOIN NhanVien n ON l.MaNhanVien = n.MaNhanVien "
                + "WHERE l.Thang = ? AND l.Nam = ? "
                + "ORDER BY l.NgayTinhLuong DESC, n.HoTen";

        try (Connection conn = DataConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, thang);
            stmt.setInt(2, nam);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToLuongNhanVien(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi lấy lịch sử tính lương: " + e.getMessage());
        }
        return list;
    }
}
