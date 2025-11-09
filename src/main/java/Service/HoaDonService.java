package Service;

import Model.HoaDon;
import Model.ChiTietHoaDon;
import Model.DichVu;
import Repository.HoaDonRepository;
import Data.DataConnection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HoaDonService {

    private final HoaDonRepository repository;
    private final DichVuService dichVuService;

    public HoaDonService() {
        this.repository = new HoaDonRepository();
        this.dichVuService = new DichVuService();
    }

    // SỬA LẠI PHƯƠNG THỨC addHoaDon - QUAN TRỌNG
    public boolean addHoaDon(HoaDon hoaDon) {
        Connection conn = null;
        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Lưu hóa đơn chính
            boolean success = repository.insert(hoaDon);
            
            if (success && hoaDon.getMaHoaDon() > 0) {
                // 2. Tạo ChiTietTienDichVuCuaNhanVien tự động NGAY LẬP TỨC
                boolean taoChiTietSuccess = repository.taoChiTietTienDichVuTuDong(hoaDon.getMaHoaDon());
                
                if (taoChiTietSuccess) {
                    conn.commit();
                    System.out.println("✅ Đã tạo ChiTietTienDichVuCuaNhanVien cho hóa đơn: " + hoaDon.getMaHoaDon());
                    
                    // DEBUG: In ra chi tiết đã tạo
                    debugChiTietTienDichVu(hoaDon.getMaHoaDon());
                    return true;
                } else {
                    conn.rollback();
                    System.err.println("❌ Lỗi khi tạo ChiTietTienDichVuCuaNhanVien");
                    return false;
                }
            } else {
                conn.rollback();
                System.err.println("❌ Lỗi khi lưu hóa đơn chính");
                return false;
            }
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("❌ Lỗi khi thêm hóa đơn: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thêm hóa đơn: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // DEBUG: Kiểm tra chi tiết tiền dịch vụ đã tạo
    private void debugChiTietTienDichVu(int maHoaDon) {
        try {
            String sql = "SELECT COUNT(*) as count FROM ChiTietTienDichVuCuaNhanVien ct " +
                        "INNER JOIN ChiTietHoaDon cthd ON ct.MaCTHD = cthd.MaCTHD " +
                        "WHERE cthd.MaHoaDon = ?";
            
            try (Connection conn = DataConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, maHoaDon);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("🔍 Đã tạo " + count + " ChiTietTienDichVuCuaNhanVien cho hóa đơn " + maHoaDon);
                    
                    // Debug chi tiết hơn
                    if (count == 0) {
                        debugKhongTaoDuocChiTiet(maHoaDon);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi debug chi tiết tiền dịch vụ: " + e.getMessage());
        }
    }

    // DEBUG CHI TIẾT: Tại sao không tạo được ChiTietTienDichVuCuaNhanVien
    private void debugKhongTaoDuocChiTiet(int maHoaDon) {
        try {
            System.out.println("🔍 DEBUG chi tiết cho hóa đơn " + maHoaDon + ":");
            
            // 1. Kiểm tra chi tiết hóa đơn
            String sqlChiTiet = "SELECT cthd.*, dv.TenDichVu, nv.HoTen as TenNhanVien, dv.MaLoaiDV " +
                               "FROM ChiTietHoaDon cthd " +
                               "LEFT JOIN DichVu dv ON cthd.MaDichVu = dv.MaDichVu " +
                               "LEFT JOIN NhanVien nv ON cthd.MaNhanVien = nv.MaNhanVien " +
                               "WHERE cthd.MaHoaDon = ?";
            
            try (Connection conn = DataConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlChiTiet)) {
                
                stmt.setInt(1, maHoaDon);
                ResultSet rs = stmt.executeQuery();
                
                boolean hasChiTiet = false;
                while (rs.next()) {
                    hasChiTiet = true;
                    int maDichVu = rs.getInt("MaDichVu");
                    Integer maNhanVien = rs.getInt("MaNhanVien");
                    int maLoaiDV = rs.getInt("MaLoaiDV");
                    
                    System.out.println("  - Dịch vụ: " + rs.getString("TenDichVu") + 
                                     " (Mã DV: " + maDichVu + ", Loại DV: " + maLoaiDV + ")" +
                                     ", NV: " + (maNhanVien > 0 ? maNhanVien : "NULL"));
                    
                    // Kiểm tra PhanTramDichVu
                    if (maNhanVien > 0) {
                        checkPhanTramDichVu(maLoaiDV, maNhanVien);
                    }
                }
                
                if (!hasChiTiet) {
                    System.out.println("  ❌ Không có chi tiết hóa đơn nào!");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi debug chi tiết: " + e.getMessage());
        }
    }

    // KIỂM TRA PHANTRAMDICHVU
    private void checkPhanTramDichVu(int maLoaiDV, int maNhanVien) {
        try {
            String sql = "SELECT * FROM PhanTramDichVu WHERE MaLoaiDV = ? AND MaNhanVien = ?";
            
            try (Connection conn = DataConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, maLoaiDV);
                stmt.setInt(2, maNhanVien);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    System.out.println("    ✅ Có PhanTramDichVu: " + rs.getBigDecimal("TiLePhanTram") + "%");
                } else {
                    System.err.println("    ❌ KHÔNG có PhanTramDichVu cho LoaiDV " + maLoaiDV + " và NV " + maNhanVien);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kiểm tra PhanTramDichVu: " + e.getMessage());
        }
    }

    // PHƯƠNG THỨC TẠO HÓA ĐƠN TỪ ĐẶT LỊCH - ĐẢM BẢO CÓ MA NHÂN VIÊN
    public HoaDon createHoaDonFromDatLich(Map<String, Object> datLichInfo) {
        try {
            HoaDon hoaDon = new HoaDon();

            // Thiết lập thông tin cơ bản
            hoaDon.setMaKhachHang((Integer) datLichInfo.get("maKhachHang"));
            hoaDon.setNgayLap(LocalDateTime.now());
            hoaDon.setGhiChu("Hóa đơn từ lịch hẹn - Giường: " + datLichInfo.get("soHieuGiuong"));

            // QUAN TRỌNG: Set mã nhân viên lập hóa đơn
            if (datLichInfo.get("maNhanVienLap") != null) {
                hoaDon.setMaNhanVienLap((Integer) datLichInfo.get("maNhanVienLap"));
                System.out.println("✅ Đã set mã NV lập hóa đơn: " + hoaDon.getMaNhanVienLap());
            }

            // Tính tổng tiền từ danh sách dịch vụ
            BigDecimal tongTien = BigDecimal.ZERO;
            List<ChiTietHoaDon> chiTietList = new ArrayList<>();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> dichVuList = (List<Map<String, Object>>) datLichInfo.get("dichVu");

            if (dichVuList != null && !dichVuList.isEmpty()) {
                for (Map<String, Object> dichVuInfo : dichVuList) {
                    ChiTietHoaDon chiTiet = new ChiTietHoaDon();
                    chiTiet.setMaDichVu((Integer) dichVuInfo.get("maDichVu"));
                    chiTiet.setSoLuong(1); // Mặc định số lượng 1

                    BigDecimal donGia = (BigDecimal) dichVuInfo.get("gia");
                    chiTiet.setDonGia(donGia);

                    // QUAN TRỌNG: ĐẢM BẢO CÓ MA NHÂN VIÊN THỰC HIỆN DỊCH VỤ
                    if (dichVuInfo.get("maNhanVien") != null) {
                        chiTiet.setMaNhanVien((Integer) dichVuInfo.get("maNhanVien"));
                        System.out.println("✅ Đã gán mã NV " + chiTiet.getMaNhanVien() + " cho dịch vụ " + chiTiet.getMaDichVu());
                    } else {
                        System.err.println("⚠️ Cảnh báo: Dịch vụ " + chiTiet.getMaDichVu() + " không có mã nhân viên!");
                        // Gán mặc định nếu không có
                        if (hoaDon.getMaNhanVienLap() != null) {
                            chiTiet.setMaNhanVien(hoaDon.getMaNhanVienLap());
                            System.out.println("✅ Đã gán mã NV lập hóa đơn làm mặc định: " + chiTiet.getMaNhanVien());
                        }
                    }

                    chiTiet.recalculateThanhTien(); // Tính lại thành tiền

                    tongTien = tongTien.add(chiTiet.getThanhTien());
                    chiTietList.add(chiTiet);
                }
            }

            // Thêm phí giường (nếu có)
            BigDecimal phiGiuong = calculatePhiGiuong((Integer) datLichInfo.get("soLuongNguoi"));
            if (phiGiuong.compareTo(BigDecimal.ZERO) > 0) {
                ChiTietHoaDon chiTietGiuong = new ChiTietHoaDon();
                chiTietGiuong.setMaDichVu(999); // Mã dịch vụ đặc biệt cho phí giường
                chiTietGiuong.setSoLuong(1);
                chiTietGiuong.setDonGia(phiGiuong);
                chiTietGiuong.recalculateThanhTien();
                chiTietGiuong.setDichVu(createDichVuGiuong(phiGiuong));

                chiTietList.add(chiTietGiuong);
                tongTien = tongTien.add(chiTietGiuong.getThanhTien());
            }

            hoaDon.setTongTien(tongTien);
            hoaDon.setChiTietHoaDon(chiTietList);

            System.out.println("✅ Đã tạo hóa đơn với " + chiTietList.size() + " dịch vụ, tổng tiền: " + tongTien);
            return hoaDon;

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo hóa đơn từ lịch hẹn: " + e.getMessage());
            throw new RuntimeException("Lỗi khi tạo hóa đơn từ lịch hẹn: " + e.getMessage(), e);
        }
    }

    // PHƯƠNG THỨC HOÀN CHỈNH: TẠO VÀ LƯU HÓA ĐƠN TỪ LỊCH HẸN
    public boolean taoHoaDonTuDatLich(Map<String, Object> datLichInfo) {
        Connection conn = null;
        try {
            conn = DataConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Tạo hóa đơn từ thông tin đặt lịch
            HoaDon hoaDon = createHoaDonFromDatLich(datLichInfo);
            
            // 2. Lưu hóa đơn
            boolean success = repository.insert(hoaDon);
            
            if (success && hoaDon.getMaHoaDon() > 0) {
                // 3. Tạo ChiTietTienDichVuCuaNhanVien NGAY LẬP TỨC
                boolean taoChiTietSuccess = repository.taoChiTietTienDichVuTuDong(hoaDon.getMaHoaDon());
                
                if (taoChiTietSuccess) {
                    conn.commit();
                    System.out.println("✅ Tạo hóa đơn thành công từ lịch hẹn: " + hoaDon.getMaHoaDon());
                    
                    // Log thông tin
                    logHoaDonInfo(hoaDon, datLichInfo);
                    return true;
                } else {
                    conn.rollback();
                    System.err.println("❌ Lỗi khi tạo chi tiết tiền dịch vụ");
                    return false;
                }
            } else {
                conn.rollback();
                System.err.println("❌ Lỗi khi lưu hóa đơn chính");
                return false;
            }

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("❌ Lỗi khi tạo hóa đơn từ lịch hẹn: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Các phương thức khác giữ nguyên...
    public List<HoaDon> getAllHoaDon() {
        try {
            return repository.getAll();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage(), e);
        }
    }

    public HoaDon getHoaDonById(int maHoaDon) {
        try {
            return repository.getById(maHoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin hóa đơn: " + e.getMessage(), e);
        }
    }

    public List<HoaDon> getHoaDonByMaKhachHang(int maKhachHang) {
        try {
            return repository.getByMaKhachHang(maKhachHang);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy hóa đơn theo khách hàng: " + e.getMessage(), e);
        }
    }

    public List<HoaDon> getHoaDonTheoKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        try {
            return repository.getHoaDonTheoKhoangThoiGian(tuNgay, denNgay);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy hóa đơn theo khoảng thời gian: " + e.getMessage(), e);
        }
    }

    public boolean updateHoaDon(HoaDon hoaDon) {
        try {
            return repository.update(hoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật hóa đơn: " + e.getMessage(), e);
        }
    }

    public boolean deleteHoaDon(int maHoaDon) {
        try {
            return repository.delete(maHoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa hóa đơn: " + e.getMessage(), e);
        }
    }

    public BigDecimal getTongDoanhThuTheoThang(int thang, int nam) {
        try {
            return repository.getTongDoanhThuTheoThang(thang, nam);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy tổng doanh thu: " + e.getMessage(), e);
        }
    }

    private BigDecimal calculatePhiGiuong(Integer soLuongNguoi) {
        if (soLuongNguoi == null || soLuongNguoi == 1) {
            return BigDecimal.ZERO;
        }
        BigDecimal phiCoBan = new BigDecimal("50000");
        return phiCoBan.multiply(BigDecimal.valueOf(soLuongNguoi - 1));
    }

    private DichVu createDichVuGiuong(BigDecimal phiGiuong) {
        DichVu dichVu = new DichVu();
        dichVu.setMaDichVu(999);
        dichVu.setTenDichVu("Phí giường thêm");
        dichVu.setGia(phiGiuong);
        return dichVu;
    }

    private void logHoaDonInfo(HoaDon hoaDon, Map<String, Object> datLichInfo) {
        System.out.println("=== THÔNG TIN HÓA ĐƠN ===");
        System.out.println("Mã hóa đơn: " + hoaDon.getMaHoaDon());
        System.out.println("Mã khách hàng: " + hoaDon.getMaKhachHang());
        System.out.println("Mã NV lập: " + hoaDon.getMaNhanVienLap());
        System.out.println("Tổng tiền: " + hoaDon.getTongTien());
        
        if (hoaDon.hasChiTiet()) {
            System.out.println("Chi tiết dịch vụ:");
            for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDon()) {
                String tenDichVu = chiTiet.getDichVu() != null ? chiTiet.getDichVu().getTenDichVu() : "Không xác định";
                System.out.println("  - " + tenDichVu + " (NV: " + chiTiet.getMaNhanVien() + "): " + 
                    chiTiet.getDonGia() + " x " + chiTiet.getSoLuong() + " = " + chiTiet.getThanhTien());
            }
        }
        System.out.println("========================");
    }

    // Phương thức tính tổng tiền từ chi tiết hóa đơn
    public BigDecimal tinhTongTienTuChiTiet(List<ChiTietHoaDon> chiTietList) {
        if (chiTietList == null || chiTietList.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return chiTietList.stream()
                .map(ChiTietHoaDon::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}