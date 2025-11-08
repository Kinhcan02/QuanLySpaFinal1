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
        String sql = "SELECT dl.* FROM DatLich dl ORDER BY dl.NgayDat DESC, dl.GioDat DESC";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql); 
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DatLich datLich = mapResultSetToDatLich(rs);
                // Tính tổng thời gian riêng
                datLich.setTongThoiGian(tinhTongThoiGian(datLich.getMaLich()));
                // Load chi tiết dịch vụ
                datLich.setDanhSachDichVu(getChiTietByMaLich(datLich.getMaLich()));
                list.add(datLich);
            }
        }
        return list;
    }

    public DatLich getById(int maLich) throws SQLException {
        String sql = "SELECT dl.* FROM DatLich dl WHERE dl.MaLich = ?";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maLich);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DatLich datLich = mapResultSetToDatLich(rs);
                    // Tính tổng thời gian riêng
                    datLich.setTongThoiGian(tinhTongThoiGian(maLich));
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
        String sql = "SELECT dl.* FROM DatLich dl WHERE dl.NgayDat = ? ORDER BY dl.GioDat ASC";

        try (Connection conn = DataConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(ngay));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DatLich datLich = mapResultSetToDatLich(rs);
                    // Tính tổng thời gian riêng
                    datLich.setTongThoiGian(tinhTongThoiGian(datLich.getMaLich()));
                    // Load chi tiết dịch vụ
                    datLich.setDanhSachDichVu(getChiTietByMaLich(datLich.getMaLich()));
                    list.add(datLich);
                }
            }
        }
        return list;
    }

    // Phương thức mới để tính tổng thời gian cho một lịch đặt
    private int tinhTongThoiGian(int maLich) throws SQLException {
        String sql = "SELECT SUM(dv.ThoiGian) as TongThoiGian " +
                    "FROM DatLich_ChiTiet ct " +
                    "JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                    "WHERE ct.MaLich = ?";
        
        try (Connection conn = DataConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maLich);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TongThoiGian");
                }
            }
        }
        return 0;
    }

public List<DatLichChiTiet> getChiTietByMaLich(int maLich) throws SQLException {
    List<DatLichChiTiet> list = new ArrayList<>();
    String sql = "SELECT ct.*, dv.TenDichVu, dv.ThoiGian, dv.Gia, " +
                "nv.HoTen as TenNhanVien, nv.MaNhanVien " +
                "FROM (DatLich_ChiTiet ct " +
                "INNER JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu) " +
                "LEFT JOIN NhanVien nv ON ct.MaNhanVien = nv.MaNhanVien " +
                "WHERE ct.MaLich = ?";

    try (Connection conn = DataConnection.getConnection(); 
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, maLich);
        try (ResultSet rs = stmt.executeQuery()) {
            
            // DEBUG: In ra tất cả tên cột để kiểm tra
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.out.println("=== DEBUG: CÁC CỘT TRONG KẾT QUẢ TRUY VẤN DatLich_ChiTiet ===");
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String columnType = metaData.getColumnTypeName(i);
                System.out.println(i + ": " + columnName + " (" + columnType + ")");
            }
            System.out.println("===========================================================");

            while (rs.next()) {
                DatLichChiTiet chiTiet = new DatLichChiTiet();
                
                // Xử lý các cột cơ bản - có try-catch riêng cho từng cột
                try {
                    chiTiet.setMaCTDL(rs.getInt("MaCTDL"));
                } catch (SQLException e) {
                    System.err.println("Lỗi cột MaCTDL: " + e.getMessage());
                }
                
                try {
                    chiTiet.setMaLich(rs.getInt("MaLich"));
                } catch (SQLException e) {
                    System.err.println("Lỗi cột MaLich: " + e.getMessage());
                }
                
                try {
                    chiTiet.setMaDichVu(rs.getInt("MaDichVu"));
                } catch (SQLException e) {
                    System.err.println("Lỗi cột MaDichVu: " + e.getMessage());
                }
                
                try {
                    chiTiet.setMaNhanVien(rs.getInt("MaNhanVien"));
                } catch (SQLException e) {
                    System.err.println("Lỗi cột MaNhanVien: " + e.getMessage());
                    chiTiet.setMaNhanVien(null);
                }
                
                try {
                    chiTiet.setGhiChu(rs.getString("GhiChu"));
                } catch (SQLException e) {
                    System.err.println("Lỗi cột GhiChu: " + e.getMessage());
                    chiTiet.setGhiChu("");
                }

                // Xử lý NgayTao với nhiều tên có thể
                boolean foundNgayTao = false;
                String[] possibleDateColumns = {
                    "NgayTao", "Ngay Tao", "Ngày Tạo", "CreatedDate", 
                    "DateCreated", "NgayTao", "CreateDate"
                };
                
                for (String columnName : possibleDateColumns) {
                    try {
                        Timestamp ngayTao = rs.getTimestamp(columnName);
                        if (ngayTao != null) {
                            chiTiet.setNgayTao(ngayTao.toLocalDateTime());
                            System.out.println("✓ Đã tìm thấy cột ngày tạo: " + columnName);
                            foundNgayTao = true;
                            break;
                        }
                    } catch (SQLException e) {
                        // Tiếp tục thử tên khác
                    }
                }
                
                if (!foundNgayTao) {
                    System.out.println("⚠ Không tìm thấy cột ngày tạo, sử dụng giá trị mặc định");
                    chiTiet.setNgayTao(java.time.LocalDateTime.now());
                }

                // Thông tin dịch vụ
                DichVu dichVu = new DichVu();
                try {
                    dichVu.setMaDichVu(rs.getInt("MaDichVu"));
                    dichVu.setTenDichVu(rs.getString("TenDichVu"));
                    dichVu.setThoiGian(rs.getInt("ThoiGian"));
                    dichVu.setGia(rs.getBigDecimal("Gia"));
                } catch (SQLException e) {
                    System.err.println("Lỗi khi lấy thông tin dịch vụ: " + e.getMessage());
                }
                chiTiet.setDichVu(dichVu);

                // Thông tin nhân viên nếu có
                try {
                    int maNV = rs.getInt("MaNhanVien");
                    if (maNV != 0 && !rs.wasNull()) {
                        NhanVien nhanVien = new NhanVien();
                        nhanVien.setMaNhanVien(maNV);
                        nhanVien.setHoTen(rs.getString("TenNhanVien"));
                        chiTiet.setNhanVien(nhanVien);
                    }
                } catch (SQLException e) {
                    System.err.println("Lỗi khi lấy thông tin nhân viên: " + e.getMessage());
                }

                list.add(chiTiet);
            }
            
            System.out.println("✓ Đã load " + list.size() + " chi tiết dịch vụ cho lịch: " + maLich);
        }
    } catch (SQLException e) {
        System.err.println("❌ Lỗi SQL trong getChiTietByMaLich: " + e.getMessage());
        throw e;
    }
    
    return list;
}

    public boolean insert(DatLich datLich) throws SQLException {
        String sql = "INSERT INTO DatLich (MaKhachHang, NgayDat, GioDat, TrangThai, MaGiuong, ThoiGianDuKien, GhiChu, MaNhanVienTao, SoLuongNguoi, NgayTao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

            // Set MaNhanVienTao
            if (datLich.getMaNhanVienTao() != null) {
                stmt.setInt(8, datLich.getMaNhanVienTao());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            // Thêm SoLuongNguoi
            stmt.setInt(9, datLich.getSoLuongNguoi());
            
            // Thêm NgayTao thủ công cho Access
            stmt.setTimestamp(10, Timestamp.valueOf(java.time.LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int generatedMaLich = rs.getInt(1);
                    datLich.setMaLich(generatedMaLich);

                    // Insert chi tiết dịch vụ
                    if (datLich.getDanhSachDichVu() != null && !datLich.getDanhSachDichVu().isEmpty()) {
                        for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                            insertChiTiet(conn, generatedMaLich, chiTiet);
                        }
                    }

                    // Cập nhật trạng thái giường nếu có (thay thế trigger)
                    if (datLich.getMaGiuong() != null && "Chờ xác nhận".equals(datLich.getTrangThai())) {
                        updateTrangThaiGiuong(conn, datLich.getMaGiuong(), "Đã đặt");
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
        String sql = "INSERT INTO DatLich_ChiTiet (MaLich, MaDichVu, MaNhanVien, GhiChu, NgayTao) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLich);
            stmt.setInt(2, chiTiet.getMaDichVu());

            if (chiTiet.getMaNhanVien() != null) {
                stmt.setInt(3, chiTiet.getMaNhanVien());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, chiTiet.getGhiChu());
            // Thêm NgayTao thủ công cho Access
            stmt.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.executeUpdate();
        }
    }

    public boolean update(DatLich datLich) throws SQLException {
        String sql = "UPDATE DatLich SET MaKhachHang=?, NgayDat=?, GioDat=?, TrangThai=?, " +
                    "MaGiuong=?, ThoiGianDuKien=?, GhiChu=?, NgayCapNhat=?, SoLuongNguoi=? WHERE MaLich=?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            // Lấy thông tin lịch cũ để kiểm tra thay đổi giường và trạng thái
            DatLich datLichCu = getById(datLich.getMaLich());
            Integer maGiuongCu = datLichCu != null ? datLichCu.getMaGiuong() : null;
            String trangThaiCu = datLichCu != null ? datLichCu.getTrangThai() : null;

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
            // Thêm NgayCapNhat thủ công cho Access
            stmt.setTimestamp(8, Timestamp.valueOf(java.time.LocalDateTime.now()));
            // Thêm SoLuongNguoi
            stmt.setInt(9, datLich.getSoLuongNguoi());
            stmt.setInt(10, datLich.getMaLich());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Xóa chi tiết cũ và thêm mới
                deleteChiTietByMaLich(conn, datLich.getMaLich());

                // Insert chi tiết dịch vụ mới
                if (datLich.getDanhSachDichVu() != null && !datLich.getDanhSachDichVu().isEmpty()) {
                    for (DatLichChiTiet chiTiet : datLich.getDanhSachDichVu()) {
                        insertChiTiet(conn, datLich.getMaLich(), chiTiet);
                    }
                }

                // Xử lý cập nhật trạng thái giường (thay thế trigger AFTER UPDATE)
                xuLyCapNhatGiuongSauUpdate(conn, datLich, maGiuongCu, trangThaiCu);

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

    public boolean updateTrangThai(int maLich, String trangThai) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            // Lấy thông tin lịch cũ
            DatLich datLich = getById(maLich);
            if (datLich == null) {
                return false;
            }

            String trangThaiCu = datLich.getTrangThai();
            Integer maGiuong = datLich.getMaGiuong();

            // Cập nhật trạng thái lịch
            String sql = "UPDATE DatLich SET TrangThai = ?, NgayCapNhat = ? WHERE MaLich = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, trangThai);
            stmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(3, maLich);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Xử lý cập nhật trạng thái giường dựa trên thay đổi trạng thái (thay thế trigger)
                xuLyTrangThaiGiuongTheoDatLich(conn, maGiuong, trangThaiCu, trangThai);
                
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

    // Phương thức thay thế trigger AFTER UPDATE
    private void xuLyCapNhatGiuongSauUpdate(Connection conn, DatLich datLichMoi, Integer maGiuongCu, String trangThaiCu) throws SQLException {
        Integer maGiuongMoi = datLichMoi.getMaGiuong();
        String trangThaiMoi = datLichMoi.getTrangThai();

        // TRIGGER LOGIC: Khi xác nhận lịch từ "Chờ xác nhận" sang "Đã xác nhận"
        if (maGiuongMoi != null && "Chờ xác nhận".equals(trangThaiCu) && "Đã xác nhận".equals(trangThaiMoi)) {
            updateTrangThaiGiuong(conn, maGiuongMoi, "Đang sử dụng");
        }
        // TRIGGER LOGIC: Khi hủy lịch hoặc hoàn thành
        else if (maGiuongMoi != null && 
                 trangThaiMoi != null && 
                 ("Đã hủy".equals(trangThaiMoi) || "Hoàn thành".equals(trangThaiMoi)) &&
                 (trangThaiCu != null && ("Chờ xác nhận".equals(trangThaiCu) || "Đã xác nhận".equals(trangThaiCu)))) {
            updateTrangThaiGiuong(conn, maGiuongMoi, "Trống");
        }
        // Xử lý thay đổi giường
        else if (maGiuongCu != null && !maGiuongCu.equals(maGiuongMoi)) {
            // Khôi phục trạng thái giường cũ
            if ("Chờ xác nhận".equals(trangThaiCu) || "Đã xác nhận".equals(trangThaiCu)) {
                updateTrangThaiGiuong(conn, maGiuongCu, "Trống");
            }
            
            // Cập nhật trạng thái giường mới
            if (maGiuongMoi != null) {
                if ("Chờ xác nhận".equals(trangThaiMoi)) {
                    updateTrangThaiGiuong(conn, maGiuongMoi, "Đã đặt");
                } else if ("Đã xác nhận".equals(trangThaiMoi)) {
                    updateTrangThaiGiuong(conn, maGiuongMoi, "Đang sử dụng");
                }
            }
        }
        // TRIGGER LOGIC: Khi chỉ thay đổi trạng thái mà không thay đổi giường
        else if (maGiuongMoi != null && maGiuongMoi.equals(maGiuongCu)) {
            // Từ "Chờ xác nhận" sang "Đã xác nhận"
            if ("Chờ xác nhận".equals(trangThaiCu) && "Đã xác nhận".equals(trangThaiMoi)) {
                updateTrangThaiGiuong(conn, maGiuongMoi, "Đang sử dụng");
            }
            // Từ "Đã xác nhận" sang "Hoàn thành" hoặc "Đã hủy"
            else if ("Đã xác nhận".equals(trangThaiCu) && 
                     ("Hoàn thành".equals(trangThaiMoi) || "Đã hủy".equals(trangThaiMoi))) {
                updateTrangThaiGiuong(conn, maGiuongMoi, "Trống");
            }
        }
    }

    private void xuLyTrangThaiGiuongTheoDatLich(Connection conn, Integer maGiuong, String trangThaiCu, String trangThaiMoi) throws SQLException {
        if (maGiuong == null) {
            return;
        }

        // TRIGGER LOGIC: Khi xác nhận lịch từ "Chờ xác nhận" sang "Đã xác nhận"
        if ("Chờ xác nhận".equals(trangThaiCu) && "Đã xác nhận".equals(trangThaiMoi)) {
            updateTrangThaiGiuong(conn, maGiuong, "Đang sử dụng");
        }
        // TRIGGER LOGIC: Khi hủy lịch
        else if (("Chờ xác nhận".equals(trangThaiCu) || "Đã xác nhận".equals(trangThaiCu)) && 
                 "Đã hủy".equals(trangThaiMoi)) {
            updateTrangThaiGiuong(conn, maGiuong, "Trống");
        }
        // TRIGGER LOGIC: Khi hoàn thành lịch
        else if (("Chờ xác nhận".equals(trangThaiCu) || "Đã xác nhận".equals(trangThaiCu)) && 
                 "Hoàn thành".equals(trangThaiMoi)) {
            updateTrangThaiGiuong(conn, maGiuong, "Trống");
        }
    }

    private void updateTrangThaiGiuong(Connection conn, int maGiuong, String trangThai) throws SQLException {
        String sql = "UPDATE Giuong SET TrangThai = ? WHERE MaGiuong = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThai);
            stmt.setInt(2, maGiuong);
            stmt.executeUpdate();
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
        Connection conn = null;
        PreparedStatement stmtChiTiet = null;
        PreparedStatement stmtDatLich = null;

        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            // Lấy thông tin lịch trước khi xóa để cập nhật giường
            DatLich datLich = getById(maLich);
            Integer maGiuong = datLich != null ? datLich.getMaGiuong() : null;
            String trangThai = datLich != null ? datLich.getTrangThai() : null;

            // Xóa chi tiết trước
            String sqlChiTiet = "DELETE FROM DatLich_ChiTiet WHERE MaLich = ?";
            stmtChiTiet = conn.prepareStatement(sqlChiTiet);
            stmtChiTiet.setInt(1, maLich);
            stmtChiTiet.executeUpdate();

            // Xóa lịch đặt
            String sqlDatLich = "DELETE FROM DatLich WHERE MaLich = ?";
            stmtDatLich = conn.prepareStatement(sqlDatLich);
            stmtDatLich.setInt(1, maLich);
            int affectedRows = stmtDatLich.executeUpdate();

            if (affectedRows > 0) {
                // Khôi phục trạng thái giường nếu có (giống trigger khi hủy)
                if (maGiuong != null && 
                    (trangThai != null && ("Chờ xác nhận".equals(trangThai) || "Đã xác nhận".equals(trangThai)))) {
                    updateTrangThaiGiuong(conn, maGiuong, "Trống");
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
            if (stmtChiTiet != null) stmtChiTiet.close();
            if (stmtDatLich != null) stmtDatLich.close();
            if (conn != null) conn.close();
        }
    }

    private DatLich mapResultSetToDatLich(ResultSet rs) throws SQLException {
        DatLich datLich = new DatLich();
        datLich.setMaLich(rs.getInt("MaLich"));
        datLich.setMaKhachHang(rs.getInt("MaKhachHang"));
        
        // Xử lý ngày có thể null
        Date ngayDat = rs.getDate("NgayDat");
        if (ngayDat != null) {
            datLich.setNgayDat(ngayDat.toLocalDate());
        }
        
        // Xử lý giờ có thể null
        Time gioDat = rs.getTime("GioDat");
        if (gioDat != null) {
            datLich.setGioDat(gioDat.toLocalTime());
        }
        
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