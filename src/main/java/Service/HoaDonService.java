package Service;

import Model.HoaDon;
import Model.ChiTietHoaDon;
import Model.DichVu;
import Repository.HoaDonRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class HoaDonService {

    private final HoaDonRepository repository;
    private final DichVuService dichVuService;

    public HoaDonService() {
        this.repository = new HoaDonRepository();
        this.dichVuService = new DichVuService();
    }

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

    public HoaDon getHoaDonById(Integer maHoaDon) {
        try {
            return repository.getById(maHoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin hóa đơn: " + e.getMessage(), e);
        }
    }

    public boolean updateHoaDon(HoaDon hoaDon) {
        try {
            return repository.update(hoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật hóa đơn: " + e.getMessage(), e);
        }
    }

    public boolean addHoaDon(HoaDon hoaDon) {
        try {
            return repository.insert(hoaDon);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm hóa đơn: " + e.getMessage(), e);
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

    // Phương thức tạo hóa đơn từ thông tin đặt lịch hoàn thành
    public HoaDon createHoaDonFromDatLich(Map<String, Object> datLichInfo) {
        try {
            HoaDon hoaDon = new HoaDon();

            // Thiết lập thông tin cơ bản
            hoaDon.setMaKhachHang((Integer) datLichInfo.get("maKhachHang"));
            hoaDon.setNgayLap(LocalDateTime.now());
            hoaDon.setGhiChu("Hóa đơn từ lịch hẹn - Giường: " + datLichInfo.get("soHieuGiuong"));

            // TODO: Set mã nhân viên từ session hiện tại
            // hoaDon.setMaNhanVienLap(currentUser.getMaNhanVien());
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
                chiTietGiuong.recalculateThanhTien(); // Tính lại thành tiền
                chiTietGiuong.setDichVu(createDichVuGiuong(phiGiuong));

                chiTietList.add(chiTietGiuong);
                tongTien = tongTien.add(chiTietGiuong.getThanhTien());
            }

            hoaDon.setTongTien(tongTien);
            hoaDon.setChiTietHoaDon(chiTietList);

            return hoaDon;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo hóa đơn từ lịch hẹn: " + e.getMessage(), e);
        }
    }

    // Tính phí giường dựa trên số lượng người
    private BigDecimal calculatePhiGiuong(Integer soLuongNguoi) {
        if (soLuongNguoi == null || soLuongNguoi == 1) {
            return BigDecimal.ZERO; // Miễn phí giường cho 1 người
        }

        // Phí giường: 50,000 VND cho mỗi người thêm
        BigDecimal phiCoBan = new BigDecimal("50000");
        return phiCoBan.multiply(BigDecimal.valueOf(soLuongNguoi - 1));
    }

    // Tạo đối tượng dịch vụ cho phí giường
    private DichVu createDichVuGiuong(BigDecimal phiGiuong) {
        DichVu dichVu = new DichVu();
        dichVu.setMaDichVu(999);
        dichVu.setTenDichVu("Phí giường thêm");
        dichVu.setGia(phiGiuong);
        return dichVu;
    }

    // Phương thức hoàn chỉnh: tạo và lưu hóa đơn từ lịch hẹn
    public boolean taoHoaDonTuDatLich(Map<String, Object> datLichInfo) {
        try {
            HoaDon hoaDon = createHoaDonFromDatLich(datLichInfo);
            boolean success = addHoaDon(hoaDon);

            if (success) {
                System.out.println("Tạo hóa đơn thành công từ lịch hẹn: " + hoaDon.getMaHoaDon());

                // Log thông tin hóa đơn
                logHoaDonInfo(hoaDon, datLichInfo);
            }

            return success;

        } catch (Exception e) {
            System.err.println("Lỗi khi tạo hóa đơn từ lịch hẹn: " + e.getMessage());
            return false;
        }
    }

    private void logHoaDonInfo(HoaDon hoaDon, Map<String, Object> datLichInfo) {
        System.out.println("=== THÔNG TIN HÓA ĐƠN ===");
        System.out.println("Mã hóa đơn: " + hoaDon.getMaHoaDon());
        System.out.println("Mã khách hàng: " + hoaDon.getMaKhachHang());
        System.out.println("Tên khách hàng: " + datLichInfo.get("tenKhachHang"));
        System.out.println("Mã giường: " + datLichInfo.get("maGiuong"));
        System.out.println("Số hiệu giường: " + datLichInfo.get("soHieuGiuong"));
        System.out.println("Số lượng người: " + datLichInfo.get("soLuongNguoi"));
        System.out.println("Tổng tiền: " + hoaDon.getTongTien());
        System.out.println("Ngày lập: " + hoaDon.getNgayLap());

        if (hoaDon.hasChiTiet()) {
            System.out.println("Chi tiết dịch vụ:");
            for (ChiTietHoaDon chiTiet : hoaDon.getChiTietHoaDon()) {
                String tenDichVu = chiTiet.getDichVu() != null ? chiTiet.getDichVu().getTenDichVu() : "Không xác định";
                System.out.println("  - " + tenDichVu
                        + ": " + chiTiet.getDonGia() + " x " + chiTiet.getSoLuong()
                        + " = " + chiTiet.getThanhTien());
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
